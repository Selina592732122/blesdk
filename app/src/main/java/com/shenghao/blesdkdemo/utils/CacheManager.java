package com.shenghao.blesdkdemo.utils;

import android.content.Context;
import android.os.Environment;

import com.bumptech.glide.Glide;

import java.io.File;
import java.text.DecimalFormat;

public class CacheManager {
    
    /**
     * 获取缓存大小
     */
    public static String getTotalCacheSize(Context context) throws Exception {
        long cacheSize = getFolderSize(context.getCacheDir());
        if (context.getExternalCacheDir() != null) {
            cacheSize += getFolderSize(context.getExternalCacheDir());
        }
        if (context.getFilesDir() != null) {//StoreUtils
            cacheSize += getFolderSize(context.getFilesDir());
        }
        if (context.getCodeCacheDir() != null) {//StoreUtils
            cacheSize += getFolderSize(context.getCodeCacheDir());
        }
        return formatSize(cacheSize);
    }
    
    /**
     * 清除所有缓存
     */
    public static void clearAllCache(Context context) {
        clearInternalCache(context);
        clearExternalCache(context);
        clearFileCache(context);//StoreUtils
        clearCodeCache(context);
        
        // 清除WebView缓存（如果有）
        clearWebViewCache(context);

        // 在子线程中
        new Thread(new Runnable() {
            @Override
            public void run() {
                Glide.get(context).clearDiskCache(); // 清除磁盘缓存（必须在子线程调用）
            }
        }).start();
        
        // 清除SharedPreferences缓存（可选）
        // clearSharedPreferences(context);
    }
    /**
     * 清除所有缓存和数据
     */
    public static void clearAllData(Context context) {
        clearAllCache(context);

        // 清除SharedPreferences缓存（可选）
         clearSharedPreferences(context);
    }

    /**
     * 计算文件夹大小
     */
    public static long getFolderSize(File file) throws Exception {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            if (fileList != null) {
                for (File value : fileList) {
                    if (value.isDirectory()) {
                        size += getFolderSize(value);
                    } else {
                        size += value.length();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }
    
    /**
     * 格式化文件大小
     */
    public static String formatSize(long size) {
        if (size <= 0) return "0B";
        
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        
        return new DecimalFormat("#,##0.#")
                .format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    /**
     * 清除WebView缓存
     */
    public static void clearWebViewCache(Context context) {
        try {
            // 删除WebView缓存目录
            File webViewCacheDir = new File(context.getCacheDir(), "webview");
            if (webViewCacheDir.exists()) {
                deleteDir(webViewCacheDir);
            }
            
            // 清除WebView数据库
            context.deleteDatabase("webview.db");
            context.deleteDatabase("webviewCache.db");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 清除应用内部缓存
     */
    public static void clearInternalCache(Context context) {
        try {
            // 方法1：使用Context的cacheDir
            File cacheDir = context.getCacheDir();
            deleteDir(cacheDir);

            // 方法2：使用系统API
            context.getCacheDir().delete();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 清除应用内部缓存
     */
    public static void clearFileCache(Context context) {
        try {
            // 方法1：使用Context的cacheDir
            File cacheDir = context.getFilesDir();
            deleteDir(cacheDir);

            // 方法2：使用系统API
            context.getFilesDir().delete();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 清除应用内部缓存
     */
    public static void clearCodeCache(Context context) {
        try {
            // 方法1：使用Context的cacheDir
            File cacheDir = context.getCodeCacheDir();
            deleteDir(cacheDir);

            // 方法2：使用系统API
            context.getCodeCacheDir().delete();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 清除应用外部缓存
     */
    public static void clearExternalCache(Context context) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File externalCacheDir = context.getExternalCacheDir();
            if (externalCacheDir != null) {
                deleteDir(externalCacheDir);
            }
        }
    }

    /**
     * 递归删除目录
     */
    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            if (children != null) {
                for (String child : children) {
                    boolean success = deleteDir(new File(dir, child));
                    if (!success) {
                        return false;
                    }
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        }
        return false;
    }
    
    /**
     * 清除SharedPreferences（可选）
     */
    public static void clearSharedPreferences(Context context) {
        // 谨慎使用，这会清除所有SharedPreferences数据
         context.getSharedPreferences(SPUtils.FILE_SP_NAME, Context.MODE_PRIVATE).edit().clear().apply();
    }
}