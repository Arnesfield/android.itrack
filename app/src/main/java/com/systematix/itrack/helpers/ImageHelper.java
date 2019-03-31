package com.systematix.itrack.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class ImageHelper {

    private ImageHelper() {}

    public static String stringify(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        final byte[] bytes = out.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    public static File createImageFile(Context context) throws IOException {
        // Create an image file name
        final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        final String imageFileName = "IMG_" + timeStamp + "_";
        // final File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        final File storageDir = context.getFilesDir();
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    public static String getImagePath(File image) {
        // Save a file: path for use with ACTION_VIEW intents
        return "file:" + image.getAbsolutePath();
    }
}
