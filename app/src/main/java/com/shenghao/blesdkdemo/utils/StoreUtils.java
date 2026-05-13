package com.shenghao.blesdkdemo.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.FileUtils;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StoreUtils {
    public static final String TAG = "StoreUtils";
    public static final String cacheImagePath = "cacheImage"+File.separator;
    /**
     * 这里注释掉的真正原因是因为在Android 11上面，getExternalStorageState()不再允许使用
     * @param context 上下文对象
     * @return 返回文件路径
     */
    public static String getBasePath(Context context){
//        return context.getCacheDir().getAbsolutePath();
//        return context.getExternalFilesDir("").getAbsolutePath();
        return context.getFilesDir().getAbsolutePath();
    }

    public static String cacheImageFile(Context context){
        String rootPath = getBasePath(context) + File.separator;
        String imagePath = rootPath + cacheImagePath;
        File baseImageFile = new File(imagePath);
        if (!baseImageFile.exists()){
            boolean mkdirs = baseImageFile.mkdirs();
            LogUtils.i(TAG,"创建baseImageFile:"+baseImageFile+(mkdirs?"成功":"失败"));
        }
        return imagePath;
    }
    public static String saveImageFromUri(Uri uri, Context context){
        // 获取内容解析器
        ContentResolver contentResolver = context.getContentResolver();
        // 通过内容解析器和图片URI打开一个输入流
        InputStream inputStream;
        Bitmap bitmap = null;
        try {
            inputStream = contentResolver.openInputStream(uri);
            // 使用BitmapFactory解码输入流得到Bitmap对象
            bitmap = BitmapFactory.decodeStream(inputStream);
            // 关闭输入流
            if (inputStream != null) {
                inputStream.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return saveImageToCache(context,bitmap);
    }
    /**
     * 保存图片
     * @param context 上下文对象
     * @param bitmap 要保存的图片
     */
    public static String saveImageToCache(Context context,Bitmap bitmap) {
        if (bitmap==null)return "";
        String fileName = getCurrentTimeMillis()+".png";
        String path = cacheImageFile(context);
        File mFile = new File(path, fileName);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(mFile.getAbsolutePath());
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != out) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mFile.getPath();
    }
    public static Bitmap getImageFromPath(String imagePath){

        if (TextUtils.isEmpty(imagePath)) return null;
        File mFile = new File(imagePath);
        if (!mFile.exists()) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;//这个参数设置为true才有效，
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inSampleSize = 1;
        return BitmapFactory.decodeFile(mFile.getAbsolutePath(), options);
    }
//    public static void clearImageToCache(Context context,String cachePath){
//
//        String rootPath = getBasePath(context) + File.separator;
//        String cacheImage = rootPath + basePath + cacheImagePath;
//
//        File cacheImageFile = new File(cachePath == null ?cacheImage:cachePath);
//        if (cacheImageFile.exists() && cacheImageFile.isDirectory()) {
//            File[] files = cacheImageFile.listFiles();
//            for (int i=0;files!=null&&i<files.length;i++) {
//                File f = files[i];
//                if (f.isDirectory()){
//                    clearImageToCache(context,f.getAbsolutePath());
//                }else {
//                    boolean delete = f.delete();
//                }
//            }
//            cacheImageFile.delete();//删除空文件夹
//        } else {
//            RBQLog.i("clearImageToCache文件不存在......");
//        }
//    }
    
//    public static void clearFileToCache(Context context,String cachePath){
//
//        String rootPath = getBasePath(context) + File.separator;
//        String cacheImage = rootPath + basePath + cacheFileFolderPath;
//
//        File cacheImageFile = new File(cachePath == null ?cacheImage:cachePath);
//        RBQLog.i("clearImageToCache:"+cacheImageFile.exists()+","+cacheImageFile.isDirectory());
//        if (cacheImageFile.exists() && cacheImageFile.isDirectory()) {
//            File[] files = cacheImageFile.listFiles();
//            RBQLog.i("clearImageToCache"+files+","+files.length);
//            for (int i=0;files!=null&&i<files.length;i++) {
//                File f = files[i];
//                RBQLog.i("clearImageToCache:"+f.getAbsolutePath());
//                if (f.isDirectory()){
//                    clearImageToCache(context,f.getAbsolutePath());
//                }else {
//                    boolean delete = f.delete();
//                    RBQLog.i("删除缓存的图片:"+f.getAbsolutePath()+(delete?"成功":"失败"));
//                }
//            }
//            cacheImageFile.delete();//删除空文件夹
//        } else {
//            RBQLog.i("clearImageToCache文件不存在......");
//        }
//    }
    
    public static void clearDataToCache(Context context){

        String rootPath = getBasePath(context) + File.separator;
        String cacheData = rootPath + cacheImagePath;

        File cacheDataFile = new File(cacheData);

        if (cacheDataFile.exists() && cacheDataFile.isDirectory()) {
            File[] files = cacheDataFile.listFiles();
            for (int i=0;files!=null&&i<files.length;i++) {
                File f = files[i];
                if (f.isDirectory()){
                    clearDataToCache(context);
                }else {
                    boolean delete = f.delete();
                    LogUtils.i(TAG,"删除缓存的数据:"+f.getAbsolutePath()+(delete?"成功":"失败"));
                }
            }
        } else {
            LogUtils.i(TAG,"文件不存在......");
        }
    }

    public static String getCurrentTimeMillis(){
        Long now = System.currentTimeMillis();
        return now.toString();
    }

    //保存至手机相册
    public static void saveImageToGallery(@NonNull Context context, @NonNull File file) {
        if (!file.exists()) {
            throw new IllegalArgumentException("bmp should not be null");
        }

        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(),
                    file.getName(), null);
        } catch (FileNotFoundException e) {
            throw new IllegalStateException("File couldn't be found");
        }
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
    }
    /**
     * 保存到相册
     *
     * @param src  源图片
     * @param file 要保存到的文件
     */
    public static void savePhotoAlbum(Bitmap src, File file, Context context) {
        if (src == null) {
            return;
        }
        //先保存到文件
        OutputStream outputStream;
        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(file));
            src.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            if (!src.isRecycled()) {
                src.recycle();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //再更新图库
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, file.getName());
            values.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM);
            ContentResolver contentResolver = context.getContentResolver();
            Uri uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,  values);
            if (uri == null) {
                return;
            }
            try {
                outputStream = contentResolver.openOutputStream(uri);
                FileInputStream fileInputStream = new FileInputStream(file);
                FileUtils.copy(fileInputStream, outputStream);
                fileInputStream.close();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            saveImageToGallery(context,file);
        }
    }
//    public static Bitmap lowThumbnailImageFromPath(String imagePath){
//
//        if (TextUtils.isEmpty(imagePath)) return null;
//        File mFile = new File(imagePath);
//        if (!mFile.exists()) {
//            return null;
//        }
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        //这个参数设置为true才有效，获取到图片的尺寸，但是这个时候图片没加载到内存，设置为false的时候，图片会加载到内存
//        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(mFile.getAbsolutePath(), options);
//
//        int width = options.outWidth;
//        int height = options.outHeight;
//        int inSampleSize = 1;
//        int max = Math.max(width,height);
//        if (max>600){
//            inSampleSize = max/600;
//        }
//        options.inJustDecodeBounds = false;
//        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//        options.inSampleSize = inSampleSize;
//        return BitmapFactory.decodeFile(mFile.getAbsolutePath(), options);
//    }

    public static Bitmap lowThumbnailImageFromPath(String imagePath) {
        if (TextUtils.isEmpty(imagePath)) return null;

        File mFile = new File(imagePath);
        if (!mFile.exists()) {
            return null;
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mFile.getAbsolutePath(), options);

        // 使用改进的inSampleSize计算方法
        options.inSampleSize = calculateInSampleSize(options, 200, 200);

        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        try {
            return BitmapFactory.decodeFile(mFile.getAbsolutePath(), options);
        } catch (OutOfMemoryError e) {
            // 内存不足时，尝试更大的缩放比例
            options.inSampleSize *= 2;
            return BitmapFactory.decodeFile(mFile.getAbsolutePath(), options);
        }
    }

    // 标准的inSampleSize计算方法
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
}
