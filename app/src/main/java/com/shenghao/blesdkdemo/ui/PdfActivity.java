package com.shenghao.blesdkdemo.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnRenderListener;
import com.shenghao.blesdkdemo.R;
import com.shenghao.blesdkdemo.utility.AppSingleton;
import com.shenghao.blesdkdemo.utils.StatusBarUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PdfActivity extends BaseActivity {
    private ProgressBar progressBar;
    private PDFView pdfView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);
        StatusBarUtils.statusBarDarkMode(this);
        StatusBarUtils.setStatusBarColor(this, R.color.transparent);
        initViews();
        String manual = AppSingleton.getInstance().getCurrentTerminal().getManual();
        if(TextUtils.isEmpty(manual)){
            return;
        }
        try {
            downloadAndShowPdf(manual);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    protected void initViews() {
        super.initViews();
//        TextView titleNameTv = findViewById(R.id.titleNameTv);
        pdfView = findViewById(R.id.pdfView);
        progressBar = findViewById(R.id.progressBar);
        View statusBarView = findViewById(R.id.statusBarView);
        StatusBarUtils.setStatusBarHeight(this, statusBarView);
    }

    private void downloadAndShowPdf(String pdfUrl) {
        progressBar.setVisibility(View.VISIBLE);

        // 使用 OkHttp 下载文件（更高效）
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(pdfUrl).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(PdfActivity.this, "下载失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (InputStream inputStream = response.body().byteStream()) {
                    // 将PDF保存到缓存文件
                    File pdfFile = File.createTempFile("temp_pdf", ".pdf", getCacheDir());
                    try (FileOutputStream outputStream = new FileOutputStream(pdfFile)) {
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                    }
                    // 显示PDF
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        pdfView.fromFile(pdfFile)
                                .defaultPage(0)
                                .enableSwipe(true)
                                .swipeHorizontal(false)
                                .enableDoubletap(true)
                                .onRender(new OnRenderListener() {
                                    @Override
                                    public void onInitiallyRendered(int nbPages) {
//                                        pdfView.fitToWidth(nbPages);
                                    }
                                })
                                .onError(t -> Toast.makeText(PdfActivity.this, "加载失败", Toast.LENGTH_SHORT).show())
                                .load();
                    });
                } catch (Exception e) {
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(PdfActivity.this, "文件错误: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }
}

