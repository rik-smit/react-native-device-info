/**
 * @providesModule react-native-device-info
 */

var { RNDeviceInfo } = require('react-native').NativeModules;

module.exports = {
  getUniqueID: function () {
    return RNDeviceInfo.deviceId;
  },
  getManufacturer: function () {
    return RNDeviceInfo.systemManufacturer;
  },
  getModel: function () {
    return RNDeviceInfo.model;
  },
  getSystemName: function () {
    return RNDeviceInfo.systemName;
  },
  getSystemVersion: function () {
    return RNDeviceInfo.systemVersion;
  },
  getBundleId: function() {
    return RNDeviceInfo.bundleId;
  },
  getBuildNumber: function() {
    return RNDeviceInfo.buildNumber;
  },
  getVersion: function() {
    return RNDeviceInfo.appVersion;
  },
  getReadableVersion: function() {
    return RNDeviceInfo.appVersion + "." + RNDeviceInfo.buildNumber;
  },
  getUtcOffset: function() {
    return RNDeviceInfo.utcOffset;
  },
  getDstOffset: function() {
    return RNDeviceInfo.dstOffset;
  },
  getLocale: function() {
    return RNDeviceInfo.locale;
  }
};
