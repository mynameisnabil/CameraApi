package com.example.cameralibrary;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.Result;

import me.dm7.barcodescanner.core.IViewFinder;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class OpenCameraforScanBarcode implements ZXingScannerView.ResultHandler {
    private AppCompatActivity mActivity;
    private ZXingScannerView mScannerView;
    private FrameLayout mFrameLayoutCam;
    private TextView mTextViewBarcodeValue;

    public OpenCameraforScanBarcode(AppCompatActivity activity, FrameLayout frameLayoutCam, TextView textViewBarcodeValue) {
        mActivity = activity;
        mFrameLayoutCam = frameLayoutCam;
        mTextViewBarcodeValue = textViewBarcodeValue;
        initScannerView();
        initDefaultView();
    }

    private void initScannerView() {
        mScannerView = new ZXingScannerView(mActivity) {
            @Override
            protected IViewFinder createViewFinderView(Context context) {
                return new CustomViewFinderView(context);
            }
        };
        mScannerView.setAutoFocus(true);
        mScannerView.setAspectTolerance(0.5f);
        mScannerView.setResultHandler(this);
        mFrameLayoutCam.addView(mScannerView);
    }

    public void onStart() {
        mScannerView.startCamera();
        doRequestPermission();
    }

    private void doRequestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mActivity.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                mActivity.requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mScannerView.startCamera();
            }
        }
    }

    public void onPause() {
        mScannerView.stopCamera();
    }

    public void initDefaultView() {
        mTextViewBarcodeValue.setText("Barcode Value");
    }

    @Override
    public void handleResult(Result result) {
        mTextViewBarcodeValue.setText(result != null ? result.getText() : null);
    }

    private static class CustomViewFinderView extends me.dm7.barcodescanner.core.ViewFinderView {
        public CustomViewFinderView(Context context) {
            super(context);
        }
    }
}
