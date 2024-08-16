# Logisic Infotech - LiCustom Plugin
==========================================

This plugin is specially created for custom methods. Currently it exposes information on the status of accessibility features of the mobile preferred scaling for text.

---------------
## Installation

    $ cordova plugin add github:logisticinfotech/cordova-plugin-licustom

----------------------
## Supported Platforms

- Android
- iOS

----------------------
## LiCustom

The `licustom` object, exposed by `window.licustom`, provides methods for determining the status of accessibility features active on the user's device, methods changing the text zoom of the Cordova web view and for using the user's preferred text zoom as set in the operating system settings.

-----------
### Methods

- licustom.getTextZoom
- licustom.setTextZoom

--------------------------------------------------------
#### licustom.getTextZoom(callback)

Makes an asynchronous call to native `licustom` to return the current text zoom percent value for the WebView.

##### Parameters

- __callback__ (Function) A callback method to receive the text zoom percent value asynchronously from the native `licustom` plugin.

##### Usage

```javascript
    function getTextZoomCallback(textZoom) {
        console.log('Current text zoom = ' + textZoom + '%')
    }

    licustom.getTextZoom(getTextZoomCallback);
```
##### Supported Platforms

- Android
- iOS

--------------------------------------------------------
#### MobileAccessibility.setTextZoom(textZoom, callback)

Makes an asynchronous call to native `licustom` to set the current text zoom percent value for the WebView.

##### Parameters

- __textZoom__ (Number) A percentage value by which text in the WebView should be scaled.
- __callback__ (Function) A callback method to receive the new text zoom percent value asynchronously from the native `licustom` plugin.

##### Usage

```javascript
    function setTextZoomCallback(textZoom) {
        console.log('WebView text should be scaled ' + textZoom + '%')
    }

    licustom.setTextZoom(200, setTextZoomCallback);
```

##### Supported Platforms

- Android
- iOS

----------
### Events