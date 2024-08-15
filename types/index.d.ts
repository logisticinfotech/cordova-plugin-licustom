// Type definitions for cordova-plugin-licustom
// Project: https://github.com/logisticinfotech/cordova-plugin-licustom
// Definitions by: Microsoft Open Technologies Inc <http://msopentech.com>
//                 Tim Brust <https://github.com/timbru31>
// Definitions: https://github.com/DefinitelyTyped/DefinitelyTyped

/**
 * This plugin defines a global device object, which describes the device's hardware and software.
 * Although the object is in the global scope, it is not available until after the deviceready event.
 */
interface LiCustom {
    echo(): void;
}

declare var licustom: LiCustom;
