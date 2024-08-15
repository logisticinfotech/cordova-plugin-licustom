/**
 * This represents the mobile device, and provides properties for inspecting the model, version, UUID of the
 * phone, etc.
 * @constructor
 */

var exec = require('cordova/exec');

var PLUGIN_NAME = 'LiCustom';

var LiCustom = {
  echo: function(phrase, cb) {
    exec(cb, null, PLUGIN_NAME, 'echo', [phrase]);
  }
};

module.exports = LiCustom;
