## 2.0.0

**NO BREAKING API CHANGES from v1**

- Bump minimum cordova-android version to 12 (require compile SDK of 33+)
- Bump minimum capacitor version to 5
- Support Android v33 Bluetooth callbacks (#985)

## 1.7.9

- iOS: Convert peripherals list to dict on iOS to avoid ghost peripherals (#1039, #1035, #1026)
- Restore test app functionality and slightly bulk out tests (#962)
- Update build badges

## 1.7.8

- iOS: Properly convert peripheral UUID for isConnected (#1033)

## 1.7.7

- iOS: Fix support for 16 & 32-bit UUIDs on iOS (#1031, #1032)

## 1.7.6

- Update ble.js to use require format to improve loading reliability (#1030, #1029) thanks @sithwarrior

## 1.7.5

- iOS: Improve input validation of UUIDs on iOS to avoid crashes (#1014, #905)
- iOS: Address iOS deprecation warnings & general warnings (#919)
- iOS: Harden up iOS service discovery (#741)
- Android: Tidy up some warnings
- Android: Mark broadcast receivers as exported for Android v34 (#1020)

## 1.7.4

- More cleanly support isConnected usage with a bool return value #1018 with review from @MaximBelov

## 1.7.3

- Android: Expose Promise version of `requestMtu` for async/await
- Types: Added returned mtu to `requestMtu` success callback thanks #1012 @ksievers-irco

## 1.7.2

- Android: add isConnectable Property for API26+ (O) #823 (#993) thanks @Gargamil

## 1.7.1

- Android: Add forceScanFilter option for Android (#989, #987) thanks @younesspotmaster

## 1.7.0

- iOS: Prevent API warnings on iOS if Bluetooth is disabled during timed scan
- iOS: Ensure scan timeout is cleared and reset when new scans are started
- Android: Ensure scan timeout is cleared and reset when new scans are started
- Standardise all scans to go via scanWithOptions
- Add duration flag (in seconds) to scan with options
- CI: Create GitHub Cordova & Capacitor Actions (#969)
- Documentation: update readme with info for advertisement parsing module
- Documentation: Detail capacitor installation instructions

## 1.6.3

- Android: Implement manual bond control on #605 #843

## 1.6.2

- Android: Don't leak refreshDeviceCache callback
- Android: More thoroughly clean up callbacks on device disconnect #954
- Android: Don't disconnect if refreshDeviceCache fails

## 1.6.1

No changes - republish only

## 1.6.0

- Android: Fix enable bluetooth permissions on Android 12+ #940 #941 Thanks @samvimes01
- Android: Clear up time-based stopScan when new scan is started #902
- Android: Report if Bluetooth is disabled when scanning/connecting #826
- Android: Restore refreshDeviceCache to 1.3.X functionality (related to #936)
- Android: Prevent various null ref exceptions (#936, #901 #871, #773, #698)
- Android: Document advanced android scan options
- Android: Improve Android 11 background permissions request ordering
- iOS: Make connect failure errors on iOS more concise #933 Thanks @doug-a-brunner

## 1.5.2

- Android: Exclude ACCESS_BACKGROUND_LOCATION from plugin.xml to avoid complications with Capacitor #928

## 1.5.1

- Android: Request BLUETOOTH_CONNECT on Android 12+ when device name is requested #924
- Android: Avoid Null reference exceptions in service discovery callbacks
- Documentation & type fixes

## 1.5.0

- Android: Add support for target SDK version 31 (Android 12) #923 #879 #875 Thanks @LDLCLid3
- Add L2CAP support (minimum iOS 11 & Android 10 required) for connection-oriented Bluetooth streams #812
- Add ble.withPromises.startNotification variant to indicate when notifications are successfully subscribed #903 #95

## 1.4.4

- Browser: Add support via navigator.Bluetooth #907 #231 Thanks @joeferner
- iOS: More explicitly handle edge cases when starting/stopping notifications on iOS #577 #893
- iOS: Improve ble.write handling when given non-ArrayBuffer types #897 #900
- Android: Report peripherals as disconnected when BLE is turned off #894 #896
- Android: Properly dispose of connected gatts on plugin reset #825 #845

## 1.4.3

- iOS: Make BLUETOOTH_RESTORE_STATE variable optional
- Android Synchronise access to the gatt during cleanup to avoid null ref errors #799
- Android: Ensure scan settings are correctly applied when permissions are requested
- Android: Correctly set match mode rather than callback type for scanWithOptions
- Android: Align "Not connected" message with iOS #784

## 1.4.2

- Add typescript definitions for all methods
- Add location state notifications on Android (similar to Bluetooth state notifications)
- Add support for iOS state restoration. This is opt-in, see README for usage details #717
- Turn API misuse warnings on iOS into reported errors to prevent dead scans #828

## 1.4.1

- Add additional options to startScanWithOptions() on Android #835 Thanks @helllamer
- Fix some instances where the Android command loop locks up #847 #830 Thanks @doug-a-brunner
- Improve documentation around requestConnectionPriority usage #877

## 1.4.0

- Android has new plugin variable ACCESS_BACKGROUND_LOCATION enable or disable background scanning permissions. Defaults to false. #844 #821 #870 Thanks @marioshtika

## 1.3.1

- Android updated to BluetoothLeScanner removing deprecated LeScanCallback #796 Thanks @pentabarf
- Android updated to work with android-cordova@8 and android-cordova@9 #819
- iOS has new plugin variable IOS_INIT_ON_LOAD to delay plugin initialization. Defaults to false. #739 #769 Thanks @jospete

## 1.3.0

- Add new location permssions Android 10 (API29) #771 Thanks @tiagoblackcode, @subratpalhar92 & @QuentinFarizon

## 1.2.5

- Add setPin method for Android #718 Thanks @untilbit
- Give the user feedback after an MTU size request #715 Thanks @agren
- Add requestConnectionPriority for Android #714 #713 Thanks @agren @vamshik113

## 1.2.4

- Add sequence numbers to notifications on Android to handle out of order notifications. Android #625 #656 Thanks @timburke
- Location services is now a warning on Android. Add isLocationEnabled() #607 #633 Thanks @doug-a-brunner
- Use printf of cordova for LOG, reuse pattern, improve loop #647 Thanks @ChristianLutz
- Add queueCleanup method to API #695 Thanks @untilbit
- Fix null pointer in onLeScan #500 Thanks @fjms
- Remove cordova-plugin-compat #705 #617

## 1.2.3

- Fix iOS crash when advertising data contains kCBAdvDataLeBluetoothDeviceAddress #685 #697 Thanks @jospete
- Fix problem with multiple devices and notifications when one device disconnected #674 Thanks @mandrade-digmap
- Add NSBluetoothAlwaysUsageDescription for iOS13 #704 #700 Thanks @favnec5

## 1.2.2

- Remove lambda from Peripheral.java to maintain 1.6 source compatibility #602
- Remove showBluetoothSettings for iOS #603

## 1.2.1

- Fix EXC_BAD_ACCESS on iOS #389 Thanks @claudiovolpato
- Return error if bad device id is passed to disconnect #410
- Better error message when location permission is denied Android #218
- Scan returns an error if location services are disabled Android #527
- Improve autoconnect for iOS #599
- Add ble.refreshDeviceCache (Android) #587 Thanks @Domvel
- Add ble.bondedDevices (Android)
- Add ble.connectedPeripheralsWithServices and ble.peripheralsWithIdentifiers (iOS)

## 1.2.0

- Added un-scanned Peripheral concept on Android #560 Thanks @doug-a-brunner
- Fixed failure to fire callbacks on Android when read or write in flight #561 Thanks @doug-a-brunner
- Fixed dangling promises when reconnecting Android #562 Thanks @doug-a-brunner
- Added error when starting a scan while another is running Android #565 Thanks @doug-a-brunner
- Request MTU Size on Android #568 Thanks @Domvel and @Algoritma
- Don't prompt user to enable Bluetooth on iOS CBCentralManagerOptionShowPowerAlertKey #580 #174 Thanks @H0rst and @cairin @michie
- Implement showBluetoothSettings on iOS #591 Thanks @cairinmichie
- Improve disconnect logic on Android #582

## 1.1.9

- iOS error #558

## 1.1.8

- Fix merge conflicts

## 1.1.7

- Use same characteristic uuid with different service in iOS #349 Thanks @riccardodegan-geekcups
- ble.read() example #346 Thanks @ktemby
- Remove pending stopNotificationCallback for iOS #355 Thanks @legege
- Add missing `resolve` and `reject` callbacks to Promise wrapper #360 Thanks @aj-dev
- Fix documentation typo #371 Thanks @maxchu2021
- Fix documentation typo #424 Thanks @ChanHyuk-Im
- Fix duplicate symbol when using with with cordova-plugin-ble-peripheral #373 Thanks @lucatorella
- Add admonition about using with beacons #413 Hugh Barnes (hughbris)
- Handle errors in didUpdateValueForCharacteristic #385 Thanks @soyelporras
- Fix spelling error in Android code # Thanks @doug-a-brunner
- cordova-plugin-compat deprecated #466 #483 thanks @ddugue
- Fix NullPointer exception on scan #504
- Fixed deprecated iOS CBPeripheral RSSI calls and build warning #446 Thanks @doug-a-brunner
- Trapped commands that caused iOS API misuse warnings #450 Thanks @doug-a-brunner
- Fire callbacks on iOS when device is disconnecting #451 Thanks @doug-a-brunner
- Fixed NSInvalidArgumentException when 'undefined' passed to plugin cmds #452 Thanks @doug-a-brunner
- Better errors on Android, when trying to read or write to a non-existing service #486 Thanks @ddugue
- Add autoConnect support #499 Thanks @hypersolution1

## 1.1.4

- Prevents scan from removing connecting Peripherals on Android #315 & #341 Thanks @mebrunet
- Documentation fixes #330 Thanks @motla
- Documenation clarification about Location Services #318 Thanks @petrometro
- Ensure peripheral is connected for startNotification and stopNotification on Android #343
- Error message for Android 4.3 devices that don't support BLE #263 Thanks @PeacePan
- Must call scan before connect. Update documentation #340

## 1.1.3

- NSBluetoothPeripheralUsageDescription #324 Thanks @timkim

## 1.1.2

- Call connect failure callback for peripheral if user disables Bluetooth #264
- Fix iOS problem with multiple keys in service data #288 Thanks @senator
- Add errorMessage to JSON object that is returned (to connect failure callback) when a peripheral disconnects
- Call gatt.disconnect() before gatt.close() to get problematic devices to disconnect #221, #254, #214
- Include version of JavaScript API with promises #247 Thanks @kellycampbell
- stopNotification on Android writes DISABLE_NOTIFICATION_VALUE to the descriptor #225 Thanks @zjw1918

## 1.1.1

- Update advertising data in peripheral while scanning (Android) #253

## 1.1.0

- Add documentation about receiving notifications in the background on iOS #169
- Add option to report duplicates during scan #172 Thanks @chris-armstrong
- Read RSSI value from an active BLE connection #171 Thanks @chris-armstrong
- Register for callbacks on Bluetooth state change #136 Thanks @rrharvey
- Fix example for write characteristic #195 Thanks @Wynout
- Fix documentation for write & writeWithoutResponse #193 Thanks @blakeparkinson
- Update CC2650 example #200 Thanks @jplourenco
- Connect peripheral with missing ble-flag (Android SDK 23) #226 Thanks @PeacePan

## 1.0.6

- Fix compile error with Cordova 5.x #219

## 1.0.5

- Request Permissions for Android 6.0 (API level 23) #182
- Update documentation for isEnabled #170

## 1.0.4

- Fix compile error with ios@4.0.1 #161

## 1.0.3

- Don't block UI thread on Android when starting scan #121 Thanks @kellycampbell
- Return characteristic even if properties don't match #132 #113 Thanks @kanayo
- StopNotification for Android fixes #51
- Fix conflicts with the BluetoothSerial plugin #114

## 1.0.2

- Update plugin id for examples
- Fix npm keywords

## 1.0.1

- Handle services that reuse UUIDs across characteristics #82 #94 Thanks @ggordan
- Disconnect cancels pending connections on iOS #93 Thanks @rrharvey
- Add dummy browser platform implementation for better PhoneGap developer app support #87 #90 Thanks @surajpindoria
- Replace notify in examples with startNotification #63
- Stop notification from stacking on Android #71 Thanks @charlesalton
- Connect failure callback returns the peripheral #16
- Better log message for unsupported iOS hardware #60
- Update bluefruitle example to work with new versions of the hardware

## 1.0.0

- Change plugin id cordova-plugin-ble-central
- Move to NPM #86
- iOS 9 #62 Thanks Khashayar Pourdeilami

## 0.1.9

- Add start of WP8 for PGDA

## 0.1.8

- Remove SDK version from config.xml (user is responsible for adding)
- Add tests for plugin
- Fix BluetoothLE example for Adafruit nRF8001 #57

## 0.1.7

- Add showBluetoothSettings and enable for Android #43
- Update documentation about UUIDs #38

## 0.1.6

- startNotification handles both notifications and indications

## 0.1.5

- Add startScan and stopScan #40
- Update to RFduino example

## 0.1.4

- Change Android behavior for leScan without service list

## 0.1.3

- Remove NO_RESULT on iOS fixes #32

## 0.1.2

- Ensure connect success callback is only called 1x on iOS #30
- Rename notify to startNotification
- Add stopNotification (iOS only)

## 0.1.1

- Return reason code when write fails on iOS #29

## 0.1.0

- Return advertising data in scan results #6, #7, #18
- Connect success returns service, characteristic and descriptor info #6
- iOS connectCallbackId is stored in Peripheral #17
- Move plugin directory to top level for phonegap build compatibility #20
- Rename writeCommand to writeWithoutResponse #21
- Services callback latch is per peripheral #19
- Connect callback is per peripheral #17
- Fix iOS crash when scanning 2x #5
- Add ble.isEnabled method #11
- Add RedBearLab example
- Add BatteryService example

## 0.0.2

- iOS - fix bug read callback was being called 2x
- iOS - fix bug write callback wasn't being called

## 0.0.1

initial release
