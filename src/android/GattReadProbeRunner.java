// (c) 2026 EdgeTech
// Licensed under the Apache License, Version 2.0.

package com.megster.cordova.ble.central;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;

import androidx.annotation.NonNull;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.LOG;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

/**
 * Android-only, diagnostic GATT read runner.
 *
 * This deliberately does not use {@link Peripheral}: it owns one fresh GATT,
 * callback and operation. It exists to compare Android's direct GATT behavior
 * with the normal Cordova Peripheral queue during support investigations.
 */
final class GattReadProbeRunner {
    static final long CONNECT_TIMEOUT_MS = 10_000L;
    static final long DISCOVERY_TIMEOUT_MS = 10_000L;
    static final long SETTLE_DELAY_MS = 500L;
    static final long READ_MARKER_MS = 8_000L;
    static final long READ_OBSERVATION_MS = 35_000L;
    private static final String TAG = "GattReadProbe";

    private final Context context;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private volatile Run activeRun;
    private volatile JSONObject lastResult;

    GattReadProbeRunner(Context context) {
        this.context = context.getApplicationContext();
    }

    boolean isActive() {
        return activeRun != null;
    }

    void start(CallbackContext callback, BluetoothDevice device, UUID serviceUuid, UUID characteristicUuid) {
        handler.post(() -> {
            if (activeRun != null) {
                callback.error("A diagnostic GATT probe is already active");
                return;
            }
            activeRun = new Run(callback, device, serviceUuid, characteristicUuid);
            activeRun.start();
        });
    }

    void cancel(String requestedRunId, CallbackContext callback) {
        handler.post(() -> {
            Run run = activeRun;
            if (run == null) {
                callback.success(resultForNoActiveProbe(requestedRunId));
                return;
            }
            if (requestedRunId != null && !requestedRunId.isEmpty() && !requestedRunId.equals(run.runId)) {
                callback.error("Diagnostic GATT probe run ID does not match the active run");
                return;
            }
            run.finish("cancelled_by_user", null);
            callback.success(resultForNoActiveProbe(run.runId));
        });
    }

    void getLastResult(CallbackContext callback) {
        handler.post(() -> {
            if (lastResult == null) {
                callback.success(new JSONObject());
                return;
            }
            try {
                callback.success(new JSONObject(lastResult.toString()));
            } catch (JSONException error) {
                callback.error("Could not read the retained diagnostic GATT probe result");
            }
        });
    }

    void shutdown(String reason) {
        handler.post(() -> {
            if (activeRun != null) {
                activeRun.finish(reason, null);
            }
        });
    }

    private JSONObject resultForNoActiveProbe(String requestedRunId) {
        JSONObject result = new JSONObject();
        try {
            result.put("active", false);
            if (requestedRunId != null) result.put("runId", requestedRunId);
        } catch (JSONException ignored) {
            // JSONObject only receives simple values above.
        }
        return result;
    }

    private final class Run extends BluetoothGattCallback {
        private final CallbackContext callback;
        private final BluetoothDevice device;
        private final UUID serviceUuid;
        private final UUID characteristicUuid;
        private final String runId = UUID.randomUUID().toString();
        private final long startedAtElapsedMs = SystemClock.elapsedRealtime();
        private final JSONArray events = new JSONArray();

        private BluetoothGatt gatt;
        private boolean finished;
        private boolean readSubmitted;
        private String state = "preparing";

        private final Runnable connectExpired = () -> finish("connect_timeout", null);
        private final Runnable discoveryExpired = () -> finish("discovery_timeout", null);
        private final Runnable submitRead = this::submitRead;
        private final Runnable readMarker = () -> record("read_observation_marker", "INFO", null, null, null, "eight_seconds_elapsed");
        private final Runnable readExpired = () -> finish("read_observation_expired", null);

        Run(CallbackContext callback, BluetoothDevice device, UUID serviceUuid, UUID characteristicUuid) {
            this.callback = callback;
            this.device = device;
            this.serviceUuid = serviceUuid;
            this.characteristicUuid = characteristicUuid;
        }

        @SuppressLint("MissingPermission")
        void start() {
            state = "connecting";
            record("probe_started", "INFO", null, null, null, null);
            handler.postDelayed(connectExpired, CONNECT_TIMEOUT_MS);
            try {
                gatt = device.connectGatt(context, false, this, BluetoothDevice.TRANSPORT_LE);
                record("connect_gatt_returned", gatt == null ? "ERROR" : "DEBUG", null, null, gatt != null, null);
                if (gatt == null) finish("connect_gatt_returned_null", null);
            } catch (SecurityException error) {
                finish("connect_permission_denied", error);
            } catch (RuntimeException error) {
                finish("connect_exception", error);
            }
        }

        @SuppressLint("MissingPermission")
        @Override
        public void onConnectionStateChange(BluetoothGatt callbackGatt, int status, int newState) {
            if (callbackGatt != gatt || finished) {
                record("connection_callback_ignored", "DEBUG", status, null, null, "obsolete_or_finished");
                return;
            }
            record("connection_state_change", status == BluetoothGatt.GATT_SUCCESS ? "DEBUG" : "ERROR", status, null, null,
                    newState == BluetoothGatt.STATE_CONNECTED ? "connected" : "disconnected");
            if (status != BluetoothGatt.GATT_SUCCESS || newState != BluetoothGatt.STATE_CONNECTED) {
                finish("connection_failed", null);
                return;
            }
            handler.removeCallbacks(connectExpired);
            state = "discovering";
            handler.postDelayed(discoveryExpired, DISCOVERY_TIMEOUT_MS);
            try {
                boolean accepted = callbackGatt.discoverServices();
                record("service_discovery_requested", accepted ? "DEBUG" : "ERROR", null, null, accepted, null);
                if (!accepted) finish("service_discovery_rejected", null);
            } catch (SecurityException error) {
                finish("service_discovery_permission_denied", error);
            } catch (RuntimeException error) {
                finish("service_discovery_exception", error);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt callbackGatt, int status) {
            if (callbackGatt != gatt || finished) {
                record("services_callback_ignored", "DEBUG", status, null, null, "obsolete_or_finished");
                return;
            }
            handler.removeCallbacks(discoveryExpired);
            record("services_discovered", status == BluetoothGatt.GATT_SUCCESS ? "DEBUG" : "ERROR", status, null, null, null);
            if (status != BluetoothGatt.GATT_SUCCESS) {
                finish("service_discovery_failed", null);
                return;
            }
            BluetoothGattService service = callbackGatt.getService(serviceUuid);
            if (service == null) {
                finish("service_not_found", null);
                return;
            }
            BluetoothGattCharacteristic characteristic = service.getCharacteristic(characteristicUuid);
            if (characteristic == null) {
                finish("characteristic_not_found", null);
                return;
            }
            int properties = characteristic.getProperties();
            record("characteristic_selected", (properties & BluetoothGattCharacteristic.PROPERTY_READ) != 0 ? "DEBUG" : "ERROR",
                    null, properties, null, null);
            if ((properties & BluetoothGattCharacteristic.PROPERTY_READ) == 0) {
                finish("characteristic_not_readable", null);
                return;
            }
            state = "settling";
            handler.postDelayed(submitRead, SETTLE_DELAY_MS);
        }

        @SuppressLint("MissingPermission")
        private void submitRead() {
            if (finished || gatt == null) return;
            BluetoothGattService service = gatt.getService(serviceUuid);
            BluetoothGattCharacteristic characteristic = service == null ? null : service.getCharacteristic(characteristicUuid);
            if (characteristic == null) {
                finish("characteristic_lost_before_read", null);
                return;
            }
            state = "reading";
            readSubmitted = true;
            record("read_start", "DEBUG", null, null, null, null);
            handler.postDelayed(readMarker, READ_MARKER_MS);
            handler.postDelayed(readExpired, READ_OBSERVATION_MS);
            try {
                boolean accepted = gatt.readCharacteristic(characteristic);
                record("read_accepted", accepted ? "DEBUG" : "ERROR", null, null, accepted, null);
                if (!accepted) finish("read_rejected", null);
            } catch (SecurityException error) {
                finish("read_permission_denied", error);
            } catch (RuntimeException error) {
                finish("read_exception", error);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt callbackGatt, BluetoothGattCharacteristic characteristic, int status) {
            completeRead(callbackGatt, characteristic, characteristic.getValue(), status, "legacy");
        }

        @Override
        public void onCharacteristicRead(@NonNull BluetoothGatt callbackGatt, @NonNull BluetoothGattCharacteristic characteristic,
                                         @NonNull byte[] value, int status) {
            completeRead(callbackGatt, characteristic, value, status, "api33");
        }

        private void completeRead(BluetoothGatt callbackGatt, BluetoothGattCharacteristic characteristic, byte[] value, int status, String source) {
            if (callbackGatt != gatt || finished) {
                record("read_callback_ignored", "DEBUG", status, value == null ? -1 : value.length, null, source + "_obsolete_or_finished");
                return;
            }
            if (!readSubmitted || !characteristicUuid.equals(characteristic.getUuid())) {
                record("read_callback_ignored", "DEBUG", status, value == null ? -1 : value.length, null, source + "_unexpected_characteristic");
                return;
            }
            record("read_callback", status == BluetoothGatt.GATT_SUCCESS ? "INFO" : "ERROR", status,
                    value == null ? -1 : value.length, null, source);
            finish(status == BluetoothGatt.GATT_SUCCESS ? "read_success" : "read_callback_error", null);
        }

        @SuppressLint("MissingPermission")
        private void finish(String reason, Throwable error) {
            if (finished) return;
            finished = true;
            handler.removeCallbacks(connectExpired);
            handler.removeCallbacks(discoveryExpired);
            handler.removeCallbacks(submitRead);
            handler.removeCallbacks(readMarker);
            handler.removeCallbacks(readExpired);
            state = "finishing";
            record("probe_finished", reason.equals("read_success") ? "INFO" : "ERROR", null, null, null, reason);

            JSONObject result = new JSONObject();
            try {
                result.put("runId", runId);
                result.put("transport", "direct_native_gatt");
                result.put("serviceUuid", serviceUuid.toString());
                result.put("characteristicUuid", characteristicUuid.toString());
                result.put("sdkInt", Build.VERSION.SDK_INT);
                result.put("readSubmitted", readSubmitted);
                result.put("terminalReason", reason);
                result.put("elapsedMs", SystemClock.elapsedRealtime() - startedAtElapsedMs);
                result.put("events", events);
                if (error != null) result.put("error", error.getClass().getSimpleName() + ": " + error.getMessage());
            } catch (JSONException ignored) {
                // The retained result still contains the events collected so far.
            }
            lastResult = result;
            closeGatt();
            activeRun = null;
            state = "closed";
            callback.success(result);
        }

        @SuppressLint("MissingPermission")
        private void closeGatt() {
            BluetoothGatt localGatt = gatt;
            gatt = null;
            if (localGatt == null) return;
            try {
                localGatt.disconnect();
            } catch (RuntimeException error) {
                LOG.w(TAG, "Probe disconnect failed: " + error.getMessage());
            }
            try {
                localGatt.close();
            } catch (RuntimeException error) {
                LOG.w(TAG, "Probe close failed: " + error.getMessage());
            }
        }

        private void record(String event, String level, Integer status, Integer value, Boolean accepted, String detail) {
            JSONObject item = new JSONObject();
            try {
                item.put("event", event);
                item.put("level", level);
                item.put("state", state);
                item.put("elapsedMs", SystemClock.elapsedRealtime() - startedAtElapsedMs);
                if (status != null) item.put("status", status);
                if (value != null) item.put("value", value);
                if (accepted != null) item.put("accepted", accepted);
                if (detail != null) item.put("detail", detail);
                events.put(item);
            } catch (JSONException ignored) {
                LOG.w(TAG, "Could not record GATT probe event");
            }
        }
    }
}
