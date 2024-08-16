/**
 * This represents the mobile device, and provides properties for inspecting the model, version, UUID of the
 * phone, etc.
 * @constructor
 */

var exec = require('cordova/exec');

var PLUGIN_NAME = 'LiCustom';

var LiCustom = {
  /**
   * Asynchronous call to native MobileAccessibility to set the log for the WebView.
   * @param {string} phrase text in the WebView should be printed as log.
   * @param {function} callback A callback method to receive the asynchronous result from the native MobileAccessibility.
   */
  echo: function(phrase, callback) {
    exec(callback, null, PLUGIN_NAME, 'echo', [phrase]);
  },

  /**
   * Asynchronous call to native MobileAccessibility to return the current text zoom percent value for the WebView.
   * @param {function} callback A callback method to receive the asynchronous result from the native MobileAccessibility.
   */
  getTextZoom: function(callback) {
    exec(callback, null, "MobileAccessibility", "getTextZoom", []);
  },


  /**
   * Asynchronous call to native MobileAccessibility to set the current text zoom percent value for the WebView.
   * @param {Number} textZoom A percentage value by which text in the WebView should be scaled.
   * @param {function} callback A callback method to receive the asynchronous result from the native MobileAccessibility.
   */
  setTextZoom: function(textZoom, callback) {
    exec(callback, null, PLUGIN_NAME, "setTextZoom", [textZoom]);
  }
};

module.exports = LiCustom;
