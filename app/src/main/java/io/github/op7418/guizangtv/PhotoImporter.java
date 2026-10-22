package io.github.op7418.guizangtv;

import android.content.Context;
import android.graphics.*;
import androidx.exifinterface.media.ExifInterface;
import android.net.Uri;
import android.util.AtomicFile;
import org.json.*;
import java.io.*;
import java.util.UUID;

/** Only copies a user-selected URI. No storage permission, server, or original metadata. */
final class PhotoImporter {
    static void add(Context context, Uri uri) throws Exception {
        File root = context.getExternalFilesDir(null);
        if (root == null) throw new IOException("Local storage unavailable");
        File folder = new File(root, "my-wallpapers");
        if (!folder.isDirectory() && !folder.mkdirs()) throw new IOException("Cannot create gallery");
        AtomicFile catalog = new AtomicFile(new File(folder, "catalog.json"));
        JSONArray rows = new JSONArray();
        if (catalog.getBaseFile().exists()) {
            if (catalog.getBaseFile().length() > 65536) throw new IOException("Catalog too large");
            byte[] bytes = catalog.readFully();
            if (bytes.length > 65536) throw new IOException("Catalog too large");
            rows = new JSONArray(new String(bytes, java.nio.charset.StandardCharsets.UTF_8));
        }
        if (rows.length() >= 30) throw new IOException("Gallery is full (30 photos)");
        BitmapFactory.Options bounds = new BitmapFactory.Options(); bounds.inJustDecodeBounds = true;
        try (InputStream in = context.getContentResolver().openInputStream(uri)) {
            BitmapFactory.decodeStream(in, null, bounds);
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = PhotoLayout.sample(bounds.outWidth, bounds.outHeight);
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap source;
        try (InputStream in = context.getContentResolver().openInputStream(uri)) {
            source = BitmapFactory.decodeStream(in, null, options);
        }
        if (source == null) throw new IOException("Image cannot be decoded");
        Bitmap oriented = source, canvas = null, thumb = null;
        String id = "import_" + UUID.randomUUID().toString().replace("-", "");
        File imageFile = WallpaperPrivacy.resolve(folder, id + ".jpg");
        File thumbFile = WallpaperPrivacy.resolve(folder, id + "_thumb.jpg");
        boolean committed = false;
        try {
            int orientation = ExifInterface.ORIENTATION_NORMAL;
            try (InputStream in = context.getContentResolver().openInputStream(uri)) {
                orientation = new ExifInterface(in).getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            } catch (IOException ignored) { /* Formats without EXIF use their decoded orientation. */ }
            Matrix matrix = new Matrix();
            switch (orientation) {
                case 2: matrix.setScale(-1, 1); break;
                case 3: matrix.setRotate(180); break;
                case 4: matrix.setScale(1, -1); break;
                case 5: matrix.setRotate(90); matrix.postScale(-1, 1); break;
                case 6: matrix.setRotate(90); break;
                case 7: matrix.setRotate(270); matrix.postScale(-1, 1); break;
                case 8: matrix.setRotate(270); break;
                default: break;
            }
            if (!matrix.isIdentity()) oriented = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
            canvas = Bitmap.createBitmap(1920, 1080, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(canvas); c.drawColor(0xFF151719);
            int[] fit = PhotoLayout.fit(oriented.getWidth(), oriented.getHeight(), 1920, 1080);
            int left = (1920-fit[0])/2, top = (1080-fit[1])/2;
            c.drawBitmap(oriented, null, new Rect(left, top, left+fit[0], top+fit[1]), new Paint(Paint.FILTER_BITMAP_FLAG));
            try (OutputStream out = new FileOutputStream(imageFile)) {
                if (!canvas.compress(Bitmap.CompressFormat.JPEG, 94, out)) throw new IOException("Save failed");
            }
            thumb = Bitmap.createScaledBitmap(canvas, 320, 180, true);
            try (OutputStream out = new FileOutputStream(thumbFile)) {
                if (!thumb.compress(Bitmap.CompressFormat.JPEG, 85, out)) throw new IOException("Thumbnail failed");
            }
            rows.put(new JSONObject().put("name", "我的照片 " + (rows.length()+1))
                    .put("file", imageFile.getName()).put("thumb", thumbFile.getName()));
            FileOutputStream out = null;
            try {
                out = catalog.startWrite();
                out.write(rows.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8));
                catalog.finishWrite(out); committed = true;
            } catch (Exception error) { if (out != null) catalog.failWrite(out); throw error; }
        } finally {
            if (thumb != null) thumb.recycle();
            if (canvas != null) canvas.recycle();
            if (oriented != source) oriented.recycle();
            source.recycle();
            if (!committed) { imageFile.delete(); thumbFile.delete(); }
        }
    }
}
