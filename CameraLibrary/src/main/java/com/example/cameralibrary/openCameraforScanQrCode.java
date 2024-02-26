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

public class openCameraforScanQrCode implements ZXingScannerView.ResultHandler {

    private AppCompatActivity mActivity;
    private ZXingScannerView mScannerView;
    private FrameLayout mFrameLayoutCam;
    private TextView mTextViewQRCodeValue;

    public
    openCameraforScanQrCode(AppCompatActivity activity, FrameLayout frameLayoutCam, TextView textViewQRCodeValue) {
        mActivity = activity;
        mFrameLayoutCam = frameLayoutCam;
        mTextViewQRCodeValue = textViewQRCodeValue;
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
        switch (requestCode) {
            case 100:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mScannerView.startCamera();
                }
                break;
            default:
        }
    }

    public void onPause() {
        mScannerView.stopCamera();
    }

    public void initDefaultView() {
        mTextViewQRCodeValue.setText("QR Code Value");
    }

    @Override
    public void handleResult(Result rawResult) {
        mTextViewQRCodeValue.setText(rawResult != null ? rawResult.getText() : null);
    }

    private class CustomViewFinderView extends me.dm7.barcodescanner.core.ViewFinderView {

        public CustomViewFinderView(Context context) {
            super(context);
        }
    }
}
