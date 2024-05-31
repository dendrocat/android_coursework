package com.example.app.Utils;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import androidx.core.content.FileProvider;

import com.example.app.Databases.Database;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageFileAdapter {

    private static final Database db = Database.getInstance();

    private static Uri curUri = null;

    public static void clearCurrUri() {
        db.clearImage(curUri);
        curUri = null;
    }

    public static Uri getUri(Context ctx) {
        if (curUri != null) {
            Uri temp = curUri;
            curUri = null;
            return temp;
        }

        File directory = new File(Environment.getExternalStorageDirectory(),
                Environment.DIRECTORY_PICTURES);

        String IMAGE_FILENAME = "IMG_" + new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date()) + ".jpg";
        File file = new File(directory.getPath() + File.separator + IMAGE_FILENAME);

        curUri = FileProvider.getUriForFile(ctx,
                "com.example.app.provider", file);
        return curUri;
    }

}