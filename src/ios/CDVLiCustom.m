/*
 Licensed to the Apache Software Foundation (ASF) under one
 or more contributor license agreements.  See the NOTICE file
 distributed with this work for additional information
 regarding copyright ownership.  The ASF licenses this file
 to you under the Apache License, Version 2.0 (the
 "License"); you may not use this file except in compliance
 with the License.  You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing,
 software distributed under the License is distributed on an
 "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 KIND, either express or implied.  See the License for the
 specific language governing permissions and limitations
 under the License.
 */

#import "CDVLiCustom.h"

#import <Cordova/CDVAvailability.h>

@interface CDVMobileAccessibility ()
    // add any property overrides
    -(double) mGetTextZoom;
    -(void) mSetTextZoom:(double)zoom;
@end

@implementation CDVLiCustom

- (void)pluginInitialize {
}

- (void)echo:(CDVInvokedUrlCommand *)command {
  NSString* phrase = [command.arguments objectAtIndex:0];
  NSLog(@"%@", phrase);
}

-(double) mGetTextZoom
{
    double zoom = round(mFontScale * 100);
    // NSLog(@"mGetTextZoom %f%%", zoom);
    return zoom;
}

- (void) getTextZoom:(CDVInvokedUrlCommand *)command
{
    double zoom = [self mGetTextZoom];
    [self.commandDelegate runInBackground:^{
        CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDouble: zoom];
        [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
    }];
}

-(void) mSetTextZoom:(double)zoom
{
    // NSLog(@"mSetTextZoom %f%%'", zoom);
    mFontScale = zoom/100;
    if (iOS7Delta)  {
        NSString *jsString = [[NSString alloc] initWithFormat:@"document.getElementsByTagName('body')[0].style.webkitTextSizeAdjust= '%f%%'", zoom];
        [self.commandDelegate evalJs:jsString];
    }
}

- (void) setTextZoom:(CDVInvokedUrlCommand *)command
{
    if (command != nil && [command.arguments count] > 0) {
        double zoom = [[command.arguments objectAtIndex:0] doubleValue];
        [self mSetTextZoom:zoom];

        [self.commandDelegate runInBackground:^{
            CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDouble:zoom];
            [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
        }];
    }
}

@end
