package com.example.cameralibrary;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class OpenCameraforPhoto {

    public static final int REQUEST_IMAGE_CAPTURE = 1;
    private static Uri photoUri;

    @SuppressLint("SimpleDateFormat")
    public static void openCamera(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_IMAGE_CAPTURE);
        } else {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Toast.makeText(activity, "Failed to create image file", Toast.LENGTH_SHORT).show();
                }
                if (photoFile != null) {
                    photoUri = Uri.fromFile(photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private static File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File imageFile = new File(storageDir, imageFileName + ".jpg");
        return imageFile;
    }

    public static void saveImageToGallery(Activity activity) {
        if (photoUri != null) {
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(photoUri);
            activity.sendBroadcast(mediaScanIntent);
        }
    }

    public static PhotoResult handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            if (photoUri != null) {
                File file = new File(photoUri.getPath());
                return new PhotoResult(photoUri, file.length(), file.getName());
            }
        }
        return null;
    }

    public static class PhotoResult {
        private Uri uri;
        private long size;
        private String name;

        public PhotoResult(Uri uri, long size, String name) {
            this.uri = uri;
            this.size = size;
            this.name = name;
        }

        public Uri getUri() {
            return uri;
        }

        public long getSize() {
            return size;
        }

        public String getName() {
            return name;
        }
    }
}