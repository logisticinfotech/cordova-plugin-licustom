/**
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
*/

package com.logistic.cordova.licustom.helper;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.accessibility.AccessibilityManager;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.logistic.cordova.licustom.LiCustom;

import java.lang.IllegalAccessException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@TargetApi(Build.VERSION_CODES.DONUT)
public class DonutLiCustomHelper extends AbstractLiCustomHelper {

    AccessibilityManager mAccessibilityManager;
    View mView;

    @Override
    public void initialize(LiCustom liCustom) {
      liCustom = liCustom;
        WebView view;
        try {
            view = (WebView) liCustom.webView;
            mView = view;
        } catch(ClassCastException ce) {  // cordova-android 4.0+
            try {
                Method getView = liCustom.webView.getClass().getMethod("getView");
                mView = (View) getView.invoke(liCustom.webView);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        mAccessibilityManager = (AccessibilityManager) liCustom.cordova.getActivity().getSystemService(Context.ACCESSIBILITY_SERVICE);
    }

    @SuppressWarnings("deprecation")
    @Override
    public double getTextZoom() {
        double zoom = 100;
        WebSettings.TextSize wTextSize = WebSettings.TextSize.NORMAL;

        try {
            Method getSettings = mView.getClass().getMethod("getSettings");
            Object wSettings = getSettings.invoke(mView);
            Method getTextSize = wSettings.getClass().getMethod("getTextSize");
            wTextSize = (WebSettings.TextSize) getTextSize.invoke(wSettings);
        } catch(ClassCastException ce) {
            ce.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        switch (wTextSize) {
        case LARGEST:
            zoom = 200;
            break;
        case LARGER:
            zoom = 150;
            break;
        case SMALLER:
            zoom = 75;
            break;
        case SMALLEST:
            zoom = 50;
            break;
        }

        return zoom;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void setTextZoom(double textZoom) {
        WebSettings.TextSize wTextSize = WebSettings.TextSize.SMALLEST;
        if (textZoom > 115) {
            wTextSize = WebSettings.TextSize.LARGEST;
        } else if (textZoom > 100) {
            wTextSize = WebSettings.TextSize.LARGER;
        } else if (textZoom == 100) {
            wTextSize = WebSettings.TextSize.NORMAL;
        } else if (textZoom > 50) {
            wTextSize = WebSettings.TextSize.SMALLER;
        }

        //Log.i("MobileAccessibility", "fontScale = " + zoom + ", WebSettings.TextSize = " + wTextSize.toString());
        try {
            Method getSettings = mView.getClass().getMethod("getSettings");
            Object wSettings = getSettings.invoke(mView);
            Method setTextSize = wSettings.getClass().getMethod("setTextSize", WebSettings.TextSize.class);
            setTextSize.invoke(wSettings, wTextSize);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
