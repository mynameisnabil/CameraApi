package com.example.cameralibrary;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

public class OpenGalleryforSelectPhoto {

    public static final int REQUEST_SELECT_IMAGE = 2;

    public static void openGallery(Activity activity) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (galleryIntent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivityForResult(galleryIntent, REQUEST_SELECT_IMAGE);
        }
    }

    public static PhotoResult handleActivityResult(int requestCode, int resultCode, Intent data, Activity activity) {
        if (requestCode == REQUEST_SELECT_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri imageUri = data.getData();
                String name = getFileNameFromUri(imageUri, activity);
                String size = getFileSizeFromUri(imageUri, activity);
                return new PhotoResult(imageUri, size, name);
            }
        }
        return null;
    }

    private static String getFileNameFromUri(Uri uri, Activity activity) {
        String[] projection = {MediaStore.Images.Media.DISPLAY_NAME};
        android.database.Cursor cursor = activity.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
            String fileName = cursor.getString(columnIndex);
            cursor.close();
            return fileName;
        }
        return null;
    }

    private static String getFileSizeFromUri(Uri uri, Activity activity) {
        String[] projection = {MediaStore.Images.Media.SIZE};
        android.database.Cursor cursor = activity.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE);
            long size = cursor.getLong(columnIndex);
            cursor.close();
            if (size >= 1024 * 1024) {
                double sizeInMB = (double) size / (1024 * 1024);
                return String.format("%.2f", sizeInMB) + " MB";
            } else {
                double sizeInKB = (double) size / 1024;
                return String.format("%.2f", sizeInKB) + " KB";
            }
        }
        return null;
    }


    public static class PhotoResult {
        private Uri uri;
        private String size;
        private String name;

        public PhotoResult(Uri uri, String size, String name) {
            this.uri = uri;
            this.size = size;
            this.name = name;
        }

        public Uri getUri() {
            return uri;
        }

        public String getSize() {
            return size;
        }

        public String getName() {
            return name;
        }
    }
}
