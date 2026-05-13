package com.shenghao.blesdkdemo.ui;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.XXPermissions;
import com.hjq.permissions.permission.PermissionLists;
import com.hjq.permissions.permission.base.IPermission;
import com.shenghao.blesdkdemo.R;
import com.shenghao.blesdkdemo.XXPermissions.PermissionDescription;
import com.shenghao.blesdkdemo.XXPermissions.PermissionInterceptor;
import com.shenghao.blesdkdemo.bean.UserInfo;
import com.shenghao.blesdkdemo.bean.UserInfoResp;
import com.shenghao.blesdkdemo.okhttp.OkHttpBaseResp;
import com.shenghao.blesdkdemo.okhttp.OkHttpResultCallBack;
import com.shenghao.blesdkdemo.present.OkHttpPresent;
import com.shenghao.blesdkdemo.utility.AppSingleton;
import com.shenghao.blesdkdemo.utils.BitmapUtils;
import com.shenghao.blesdkdemo.utils.JsonUtils;
import com.shenghao.blesdkdemo.utils.LogUtils;
import com.shenghao.blesdkdemo.utils.StatusBarUtils;
import com.shenghao.blesdkdemo.utils.StoreUtils;
import com.shenghao.blesdkdemo.utils.ToastUtils;
import com.shenghao.blesdkdemo.widget.IosBottomSheetDialog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;
import okhttp3.Response;

public class PersonalActivity extends BaseActivity {
    public final String TAG = this.getClass().getSimpleName();
    private static final int REQUEST_PICK_IMAGE = 101;
    private static final int REQUEST_IMAGE_CAPTURE = 102;
    private static final int REQUESTCODE_CUT = 103;
    private Uri photoURI;
    private ImageView ivUser,iv;
    private EditText etName;
    private String path;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);
        StatusBarUtils.statusBarLightMode(this);
        StatusBarUtils.setStatusBarColor(this,R.color.white);
        initViews();
    }

    @Override
    protected void initViews() {
        super.initViews();
        ivUser = findViewById(R.id.ivUser);
        Glide.with(PersonalActivity.this)
                .load(AppSingleton.getInstance().getUserInfo().getAvatar())
                .placeholder(R.drawable.ic_user_default_portrait) // 加载中的占位图
                .error(R.drawable.ic_user_default_portrait)
                .into(ivUser);
        etName = findViewById(R.id.etName);
        etName.setText(AppSingleton.getInstance().getUserInfo().getNickName());
        iv = findViewById(R.id.iv);
        ivUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheet();
            }
        });
        findViewById(R.id.btnOk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(path)){
                    startBind(AppSingleton.getInstance().getUserInfo().getAvatar());
                } else {
                    try {
                        // 添加图片处理异常捕获
                        Bitmap bitmap1 = StoreUtils.lowThumbnailImageFromPath(path);
                        if (bitmap1 == null) {
                            ToastUtils.showShort(PersonalActivity.this, "图片处理失败");
                            return;
                        }

                        String path2 = StoreUtils.saveImageToCache(PersonalActivity.this, bitmap1);
                        if (TextUtils.isEmpty(path2)) {
                            ToastUtils.showShort(PersonalActivity.this, "图片保存失败");
                            return;
                        }

                        String base64 = BitmapUtils.imageToBase64(path2);
                        if (TextUtils.isEmpty(base64)) {
                            ToastUtils.showShort(PersonalActivity.this, "图片编码失败");
                            return;
                        }

                        LogUtils.d(TAG, "图片处理成功: " + bitmap1.getWidth() + "x" + bitmap1.getHeight());
                        startBind(base64.replace("\n",""));

                    } catch (Exception e) {
                        LogUtils.e(TAG, "图片处理异常: " + e.getMessage());
                        ToastUtils.showShort(PersonalActivity.this, "图片处理异常，请重试");
                    }
                }

            }
        });
    }

    private void startBind(String base64) {
//        if (TextUtils.isEmpty(terminalNo)) {
//            ToastUtils.showShort(BindTerminalActivity.this, "请输入设备号");
//            return;
//        }
        String name = TextUtils.isEmpty(etName.getText().toString()) ? AppSingleton.getInstance().getUserName() : etName.getText().toString();
        OkHttpPresent.updateUserInfo(name, base64, new OkHttpResultCallBack() {
            @Override
            protected void start() {
                super.start();
                showLoadingDialog();
            }

            @Override
            protected void onResponse(Response response, String body) throws IOException {
                LogUtils.e(TAG, "onResponse: 上传成功 = " + body);
                try {
                    OkHttpBaseResp baseResp = JsonUtils.parseT(body, OkHttpBaseResp.class);
                    if (baseResp != null) { //绑定成功
                        if (baseResp.isSuccess()) {
                            getUserInfo();
                        } else {
                            // 显示服务器返回的具体错误信息
                            String errorMsg = TextUtils.isEmpty(baseResp.getMsg()) ?
                                    getString(R.string.request_retry) : baseResp.getMsg();
                            ToastUtils.showShort(PersonalActivity.this, errorMsg);
                        }
                    } else {
                        ToastUtils.showShort(PersonalActivity.this, getString(R.string.request_retry));
                    }
                } catch (Exception e) {
                    ToastUtils.showShort(PersonalActivity.this, getString(R.string.request_retry));
                }

            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                LogUtils.e(TAG, "onFailed: 上传失败 = " + e);
                ToastUtils.showShort(PersonalActivity.this, getString(R.string.request_retry));
            }

            @Override
            protected void end() {
                super.end();
                hideLoadingDialog();
            }
        });
    }
    private void getUserInfo(){
        OkHttpPresent.getUserInfo(new OkHttpResultCallBack() {
            @Override
            protected void start() {
                super.start();
                showLoadingDialog();
            }

            @Override
            protected void onResponse(Response response, String body) throws IOException {
                LogUtils.e(TAG, "onResponse: 成功 = " + body);
                UserInfoResp userInfoResp = JsonUtils.parseT(body, UserInfoResp.class);
                if (userInfoResp != null && userInfoResp.isSuccess()) {
                    UserInfo userInfo = userInfoResp.getData();
                    AppSingleton.getInstance().setUserInfo(userInfo);
                    finish();
                }else {
                    ToastUtils.showShort(PersonalActivity.this, getString(R.string.request_retry));
                }
            }

            @Override
            protected void onFailed(Request request, Exception e) {
                super.onFailed(request, e);
                LogUtils.e(TAG, "onFailed: 失败 = " + e);
                ToastUtils.showShort(PersonalActivity.this, getString(R.string.request_retry));
            }

            @Override
            protected void end() {
                super.end();
                hideLoadingDialog();
            }
        });
    }


    private void showBottomSheet() {
//        Manifest.permission.READ_EXTERNAL_STORAGE,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE
            List<String> permissions = new ArrayList<>();
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                permissions.add(Manifest.permission.CAMERA);
                permissions.add(Manifest.permission.READ_MEDIA_IMAGES);
            }else {
                permissions.add(Manifest.permission.CAMERA);
                permissions.add(Manifest.permission.READ_MEDIA_IMAGES);
//            permissions.add(Manifest.permission.CAMERA);
//            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
//            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            XXPermissions.with(this)
//                    .permission(permissions)
                    .permission(PermissionLists.getCameraPermission())
                    .permission(PermissionLists.getReadMediaImagesPermission())
                    // 设置权限请求拦截器（局部设置）
//                    .interceptor(new PermissionInterceptor(getString(R.string.permissionCamera)))
                    .interceptor(new PermissionInterceptor())
                    .description(new PermissionDescription())
                    // 设置不触发错误检测机制（局部设置）
                    //.unchecked()
                    .request(new OnPermissionCallback() {

                        @Override
                        public void onResult(@NonNull List<IPermission> grantedList, @NonNull List<IPermission> deniedList) {
                            boolean allGranted = deniedList.isEmpty();
                            if (!allGranted) {
                                // 判断请求失败的权限是否被用户勾选了不再询问的选项
                                boolean doNotAskAgain = XXPermissions.isDoNotAskAgainPermissions(PersonalActivity.this, deniedList);
                                // 在这里处理权限请求失败的逻辑
                                if (doNotAskAgain) {
                                    // 如果是被永久拒绝就跳转到应用权限系统设置页面
                                    XXPermissions.startPermissionActivity(PersonalActivity.this, deniedList);
                                } else {
                                    ToastUtils.showShort(PersonalActivity.this,"获取权限失败");
                                }
                                return;
                            }
                            // 在这里处理权限请求成功的逻辑
                            IosBottomSheetDialog.Builder builder = new IosBottomSheetDialog.Builder(PersonalActivity.this);
                            builder.addItemView("拍照上传", 0, false, new IosBottomSheetDialog.OnItemClickListener() {
                                @Override
                                public void onClick(View view) {
                                    intixiangji();
                                }
                            });
                            builder.addItemView("从手机相册选择", 0, false, new IosBottomSheetDialog.OnItemClickListener() {
                                @Override
                                public void onClick(View view) {
                                    intixiangce();
                                }
                            });
                            builder.build().show();
                        }

//                        @Override
//                        public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
//                            if (!allGranted) {
//                                ToastUtils.showShort(PersonalActivity.this,"获取部分权限成功，但部分权限未正常授予");
//                                return;
//                            }
//                            IosBottomSheetDialog.Builder builder = new IosBottomSheetDialog.Builder(PersonalActivity.this);
//                            builder.addItemView("拍照上传", 0, false, new IosBottomSheetDialog.OnItemClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                    intixiangji();
//                                }
//                            });
//                            builder.addItemView("从手机相册选择", 0, false, new IosBottomSheetDialog.OnItemClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                    intixiangce();
//                                }
//                            });
//                            builder.build().show();
//                        }
//
//                        @Override
//                        public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
//                            if (doNotAskAgain) {
//                                // 如果是被永久拒绝就跳转到应用权限系统设置页面
//                                XXPermissions.startPermissionActivity(PersonalActivity.this, permissions);
//                            } else {
//                                ToastUtils.showShort(PersonalActivity.this,"获取权限失败");
//                            }
//                        }
                    });


    }

    // 打开相册方法
    private void intixiangce() {
        // 创建一个 Intent，用于打开设备的相册，选择图片
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // 启动相册 Activity 并期望返回结果
        startActivityForResult(galleryIntent, REQUEST_PICK_IMAGE);
    }

    // 打开相机方法
    private void intixiangji() {
        // 创建一个 Intent，用于打开设备的相机
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // 确认设备上是否有可以处理相机启动的应用
        // 启动相机 Activity 并期望返回结果
        if (intent.resolveActivity(getPackageManager()) != null)
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }
    private void intixiangji2() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this, "com.wuyang.fileProvider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = "JPEG_" + System.currentTimeMillis() + "_";
        File storageDir = getExternalFilesDir(null);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }


    /**
     * 打开系统图片裁剪功能
     * @param uri
     */
    private void startPhotoZoom(Uri uri,boolean need) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri,"image/*");
        intent.putExtra("crop",true);
        intent.putExtra("aspectX",1);
        intent.putExtra("aspectY",1);
        intent.putExtra("outputX",300);
        intent.putExtra("outputY",300);
        intent.putExtra("scale",true); //黑边
        intent.putExtra("scaleUpIfNeeded",true); //黑边
        intent.putExtra("return-data",true);
        intent.putExtra("noFaceDetection",true);
        if(need)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        startActivityForResult(intent,REQUESTCODE_CUT);
    }

    private void setPicToView(Intent data) {
        Bundle bundle =  data.getExtras();
        if (bundle != null){
            //这里也可以做文件上传
            Bitmap mBitmap = bundle.getParcelable("data");
            ivUser.setImageBitmap(mBitmap);
        }
    }

    private void startUCrop(String file,String output){
//        UCrop.of(Uri.fromFile(new File(file)), Uri.fromFile(new File(output))).withAspectRatio(1,1).start(this,REQUESTCODE_CUT);
    }

    // 处理返回的结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 如果返回的结果是成功的
        if (resultCode == RESULT_OK) {
            // 清除原来的背景
            ivUser.setBackground(null);  // 删除原来的背景
            // 判断请求码，来分别处理相机或相册返回的图片
            try {
                if (requestCode == REQUEST_IMAGE_CAPTURE) {
                    // 如果是相机拍照返回的图片
                    if (data != null && data.getExtras() != null){
                        Bundle extras = data.getExtras();  // 获取返回的额外数据
                        Bitmap imageBitmap = (Bitmap) extras.get("data");  // 获取图片的 Bitmap 数据
                        if (imageBitmap != null) {
                            // 将获取到的图片设置到 ImageView 中
                            ivUser.setImageBitmap(imageBitmap);
//                Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), imageBitmap, null, null));
//                Uri uri = bitmapToUri(this, imageBitmap);
                            path = StoreUtils.saveImageToCache(this,imageBitmap);
                            if (TextUtils.isEmpty(path)) {
                                ToastUtils.showShort(this, "图片保存失败");
                            }
                        }
                    }
//                ToastUtils.showShort(this,path);
//                startUCrop(path,path);
//                Uri uri= FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".fileProvider",new File(path));
//                LogUtils.e("存储路径",path+","+uri+","+photoURI);
//                startPhotoZoom(photoURI,true);
                } else if (requestCode == REQUEST_PICK_IMAGE) {
                    // 如果是从相册选择的图片
                    if (data == null || data.getData() == null){
                        return;
                    }
                    Uri contentURI = data.getData();  // 获取图片的 URI
//                startPhotoZoom(data.getData(),false);
                    // 将获取到的图片 URI 设置到 ImageView 中
                    ivUser.setImageURI(contentURI);
                    path = StoreUtils.saveImageFromUri(contentURI, this);
                    if (TextUtils.isEmpty(path)) {
                        ToastUtils.showShort(this, "图片获取失败");
                    }
//                ToastUtils.showShort(this,path);
//                startUCrop(path,path);
                }
            } catch (Exception e) {
                e.printStackTrace();
                ToastUtils.showShort(this, "图片处理失败");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 清理图片资源
        if (ivUser != null) {
            ivUser.setImageDrawable(null);
        }
    }

    //    https://blog.51cto.com/u_16213564/7469170
    //原文链接：https://blog.csdn.net/2402_85226471/article/details/144315512
}
