package com.logistic.cordova.licustom;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.apptivatellc.iSS.R;
import com.dynamsoft.core.basic_structures.CompletionListener;
import com.dynamsoft.core.basic_structures.DSRect;
import com.dynamsoft.core.basic_structures.EnumCapturedResultItemType;
import com.dynamsoft.cvr.CaptureVisionRouter;
import com.dynamsoft.cvr.CaptureVisionRouterException;
import com.dynamsoft.cvr.CapturedResultReceiver;
import com.dynamsoft.cvr.EnumPresetTemplate;
import com.dynamsoft.cvr.SimplifiedCaptureVisionSettings;
import com.dynamsoft.dbr.BarcodeResultItem;
import com.dynamsoft.dbr.DecodedBarcodesResult;
import com.dynamsoft.dce.CameraEnhancer;
import com.dynamsoft.dce.CameraEnhancerException;
import com.dynamsoft.dce.CameraView;
import com.dynamsoft.dce.Feedback;
import com.dynamsoft.dce.utils.PermissionUtil;
import com.dynamsoft.license.LicenseManager;
import com.dynamsoft.license.LicenseVerificationListener;
import com.dynamsoft.utility.MultiFrameResultCrossFilter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class ProgrammaticCameraActivity extends AppCompatActivity {

    private ConstraintLayout rootLayout;
    private CameraView mCameraView;
    private CameraEnhancer mCamera;
    private CaptureVisionRouter mRouter;
    private AlertDialog mAlertDialog;

    private JSONObject settingsJsonObject = null;
    private String license = "DLS2eyJvcmdhbml6YXRpb25JRCI6IjIwMDAwMSJ9";
    private String stopButtonText = "Stop Scanner";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String settingsJsonString = getIntent().getStringExtra("settings");
        try {
          settingsJsonObject = new JSONObject(settingsJsonString);
        } catch (JSONException e) {
          throw new RuntimeException(e);
        }

        // Create the root layout
        rootLayout = new ConstraintLayout(this);
        setContentView(rootLayout);

        // Initialize the license
         initLicence();

        // Request camera permission
        PermissionUtil.requestCameraPermission(this);

        // Create and configure CameraView
        mCameraView = new CameraView(this);
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        mCameraView.setLayoutParams(params);

        // Add CameraView to the root layout
        rootLayout.addView(mCameraView);

        // Initialize camera enhancer
        mCamera = new CameraEnhancer(mCameraView, this);
        mRouter = new CaptureVisionRouter(this);

        // update runtime barcode settings
        updateSettings();

        // If you don't add any styles for the button, the torch will be displayed on the top left corner of the screen.
        mCameraView.setTorchButtonVisible(true);

        // set camera scanning view region
        setRegion();

        // visible region mask and set color of scanner laser and mask
        isScanRegionVisible();
        isScanLaserVisible();
        mCameraView.setScanRegionMaskStyle(R.color.scan_region_stroke, R.color.scan_region_mask, 1f);

        // Configure result filter
        MultiFrameResultCrossFilter filter = new MultiFrameResultCrossFilter();
        filter.enableResultCrossVerification(EnumCapturedResultItemType.CRIT_BARCODE, true);
        mRouter.addResultFilter(filter);

        try {
            // Set the camera enhancer as the input
            mRouter.setInput(mCamera);
        } catch (CaptureVisionRouterException e) {
            throw new RuntimeException(e);
        }

        // add button for stop scanner
        addStopScannerButton();

        // Add result receiver
        mRouter.addResultReceiver(new CapturedResultReceiver() {
            @Override
            public void onDecodedBarcodesReceived(DecodedBarcodesResult result) {
                runOnUiThread(() -> showResult(result));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Start video barcode reading
        try {
            // Open the camera
            mCamera.open();
        } catch (CameraEnhancerException e) {
            e.printStackTrace();
        }
        // Start capturing
        mRouter.startCapturing(EnumPresetTemplate.PT_READ_SINGLE_BARCODE, new CompletionListener() {
            @Override
            public void onSuccess() {
                // Successfully started capturing
            }

            @Override
            public void onFailure(int errorCode, String errorString) {
                runOnUiThread(() -> showDialog("Error",
                    String.format(Locale.getDefault(), "ErrorCode: %d %nErrorMessage: %s",
                    errorCode, errorString)));
            }
        });
    }

    @Override
    protected void onPause() {
        // Stop video barcode reading
        try {
            mCamera.close();
        } catch (CameraEnhancerException e) {
            e.printStackTrace();
        }
        mRouter.stopCapturing();
        super.onPause();
    }

    private void showResult(DecodedBarcodesResult result) {
        StringBuilder strRes = new StringBuilder();

        if (result != null && result.getItems() != null && result.getItems().length > 0) {
            mRouter.stopCapturing();
            mRouter.getInput().clearBuffer();
            // Extract the barcode format and the barcode text from the BarcodeResultItem
            for (int i = 0; i < result.getItems().length; i++) {
                BarcodeResultItem item = result.getItems()[i];
                strRes.append(item.getText()).append("\n\n");
//              strRes.append(item.getFormatString()).append(":").append(item.getText()).append("\n\n");
            }
            if (mAlertDialog != null && mAlertDialog.isShowing()) {
                return;
            }
            Feedback.vibrate(this);

            // Send result back to plugin
            Intent resultIntent = new Intent();
            resultIntent.putExtra("barcode_result", strRes.toString());
            setResult(RESULT_OK, resultIntent);
            finish();
        }
    }

    private void showDialog(String title, String message) {
        if (mAlertDialog == null) {
            // Restart the capture when the dialog is closed
            mAlertDialog = new AlertDialog.Builder(this)
                .setCancelable(true)
                .setPositiveButton("OK", null)
                .setOnDismissListener(dialog ->
                    mRouter.startCapturing(EnumPresetTemplate.PT_READ_SINGLE_BARCODE, null))
                .create();
        }
        mAlertDialog.setTitle(title);
        mAlertDialog.setMessage(message);
        mAlertDialog.show();
    }

    private void initLicence() {
        if (!settingsJsonObject.isNull("license")) {
          try {
            license = settingsJsonObject.getString("license");
          } catch (JSONException e) {
            throw new RuntimeException(e);
          }
        }

        LicenseManager.initLicense(license, this, new LicenseVerificationListener() {
          @Override
          public void onLicenseVerified(boolean isSuccess, Exception error) {
            if(!isSuccess){
              error.printStackTrace();
            } else {
              Log.i("BARCODE", "Init License successful.");
            }
          }
        });
    }

    private void updateSettings() {
        SimplifiedCaptureVisionSettings s = null;
        try {
            s = mRouter.getSimplifiedSettings(EnumPresetTemplate.PT_READ_SINGLE_BARCODE);

            if (!settingsJsonObject.isNull("barcodeFormatIds")) {
              s.barcodeSettings.barcodeFormatIds = settingsJsonObject.getInt("barcodeFormatIds");
            }
            if (!settingsJsonObject.isNull("expectedBarcodesCount")) {
              s.barcodeSettings.expectedBarcodesCount = settingsJsonObject.getInt("expectedBarcodesCount");
            }

            mRouter.updateSettings(EnumPresetTemplate.PT_READ_SINGLE_BARCODE, s);
        } catch (CaptureVisionRouterException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void isScanRegionVisible() {
      if (!settingsJsonObject.isNull("is_scan_region_visible")) {
        try {
          if (settingsJsonObject.getBoolean("is_scan_region_visible")) {
            mCameraView.setScanRegionMaskVisible(true);
          }
        } catch (JSONException e) {
          throw new RuntimeException(e);
        }
      }
    }

    private void isScanLaserVisible() {
      if (!settingsJsonObject.isNull("is_scan_laser_visible")) {
        try {
          if (settingsJsonObject.getBoolean("is_scan_laser_visible")) {
            mCameraView.setScanLaserVisible(true);
          }
        } catch (JSONException e) {
          throw new RuntimeException(e);
        }
      }
    }

    private void setRegion() {
        DSRect region = new DSRect();
        region.left = 0.15F;
        region.right = 0.85F;
        region.top = 0.15F;
        region.bottom = 0.85F;
        region.measuredInPercentage = true;
        try {
          mCamera.setScanRegion(region);
        } catch (CameraEnhancerException e) {
          e.printStackTrace();
        }
    }

    private void addStopScannerButton() {
        // Create RelativeLayout
        RelativeLayout relativeLayout = new RelativeLayout(this);
        relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(
          RelativeLayout.LayoutParams.MATCH_PARENT,
          RelativeLayout.LayoutParams.MATCH_PARENT
        ));

        // Set Button Layout Parameters
        RelativeLayout.LayoutParams relativeLayoutParams = new RelativeLayout.LayoutParams(
          RelativeLayout.LayoutParams.WRAP_CONTENT,
          RelativeLayout.LayoutParams.WRAP_CONTENT
        );

        relativeLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM); // Align button to bottom
        relativeLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);  // Center horizontally
        relativeLayoutParams.setMargins(0, 0, 0, 50); // Optional: Add margin from bottom

        // Define gradient colors
        int[] colors = {0xFFFA2E03, 0xFFFB6C4D}; // #FA2E03 to #FB6C4D

        // Create GradientDrawable
        GradientDrawable gradientDrawable = new GradientDrawable(
          GradientDrawable.Orientation.TOP_BOTTOM, // Gradient direction
          colors
        );
        gradientDrawable.setCornerRadius(0f); // Remove border radius (square edges)

        if (!settingsJsonObject.isNull("stop_scanner_btn_text")) {
          try {
            stopButtonText = settingsJsonObject.getString("stop_scanner_btn_text");
          } catch (JSONException e) {
            throw new RuntimeException(e);
          }
        }

        Button stopScannerButton = new Button(this);
        stopScannerButton.setText(stopButtonText);
        stopScannerButton.setPadding(100,0,100,0);
        stopScannerButton.setBackground(gradientDrawable);
        stopScannerButton.setTextColor(Color.parseColor("#FFFFFF"));

        stopScannerButton.setLayoutParams(relativeLayoutParams);

        stopScannerButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            mRouter.stopCapturing();
            mRouter.getInput().clearBuffer();

            setResult(RESULT_CANCELED);
            finish();
          }
        });

        // Add Button to Layout
        relativeLayout.addView(stopScannerButton);
        rootLayout.addView(relativeLayout);
    }
}
