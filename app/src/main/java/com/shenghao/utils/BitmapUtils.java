package com.shenghao.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class BitmapUtils {

    // 将 Bitmap 转换为 Base64 字符串
    public static String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream); // 压缩格式可以是 PNG 或 JPEG
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT); // 编码为 Base64 字符串
    }

    // 从 Base64 字符串恢复 Bitmap
    public static Bitmap base64ToBitmap(String base64String) {
        byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
    public static String imageToBase64(String path){
            //decode to bitmap
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            //convert to byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] bytes = baos.toByteArray();

            //base64 encode
            byte[] encode = Base64.encode(bytes,Base64.DEFAULT);
        return new String(encode);
    }
}