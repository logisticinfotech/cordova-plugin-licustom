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
package com.logistic.cordova.licustom;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaInterface;
import org.json.JSONArray;
import org.json.JSONException;
import android.util.Log;

public class LiCustom extends CordovaPlugin {

    private static final String TAG = "LiCustomPlugin";

    private AbstractLiCustomHelper mLiCustomHelper;

    /**
     * Constructor.
     */
    public LiCustom() {
    }

    /**
     * Sets the context of the Command. This can then be used to do things like
     * get file paths associated with the Activity.
     *
     * @param cordova The context of the main Activity.
     * @param webView The CordovaWebView Cordova is running in.
     */
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);

        Log.d(TAG, "Initializing LiCustomPlugin");

        mLiCustomHelper = new DonutLiCustomHelper();
        mLiCustomHelper.initialize(this);
    }

    /**
     * Executes the request and returns PluginResult.
     *
     * @param action            The action to execute.
     * @param args              JSONArray of arguments for the plugin.
     * @param callbackContext   The callback id used when calling back into JavaScript.
     * @return                  True if the action was valid, false if not.
     */
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if ("echo".equals(action)) {
            String phrase = args.getString(0);

            // Echo back the first argument
            Log.d(TAG, phrase);
        } else if(action.equals("getTextZoom")) {
            getTextZoom(callbackContext);
        } else if(action.equals("setTextZoom")) {
            if (args.length() > 0) {
                double textZoom = args.getDouble(0);
                if (textZoom > 0) {
                    setTextZoom(textZoom, callbackContext);
                }
            }
        } else {
            return false;
        }
        return true;
    }

    //--------------------------------------------------------------------------
    // LOCAL METHODS
    //--------------------------------------------------------------------------
    private void getTextZoom(final CallbackContext callbackContext) {
        cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                final double textZoom = mLiCustomHelper.getTextZoom();
                if (callbackContext != null) {
                    callbackContext.success((int) textZoom);
                }
            }
        });
    }

    private void setTextZoom(final double textZoom, final CallbackContext callbackContext) {
        cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                mLiCustomHelper.setTextZoom(textZoom);
                if (callbackContext != null) {
                    callbackContext.success((int) mLiCustomHelper.getTextZoom());
                }
            }
        });
    }

    public void setTextZoom(final double textZoom) {
        cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                mLiCustomHelper.setTextZoom(textZoom);
            }
        });
    }
}
