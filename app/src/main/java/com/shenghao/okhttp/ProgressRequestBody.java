package com.shenghao.okhttp;

import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

public class ProgressRequestBody extends RequestBody {

    private final RequestBody requestBody;
    private final OkHttpResultCallBack okHttpResultCallBack;
    private BufferedSink bufferedSink;
    private Handler mHandler;

    public ProgressRequestBody(Handler handler, RequestBody requestBody,
            OkHttpResultCallBack okHttpResultCallBack) {
        this.mHandler = handler;
        this.requestBody = requestBody;
        this.okHttpResultCallBack = okHttpResultCallBack;
    }

    @Nullable
    @Override
    public MediaType contentType() {
        return requestBody.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return requestBody.contentLength();
    }

    @Override
    public void writeTo(@NonNull BufferedSink sink) throws IOException {
        if (bufferedSink == null) {
            Sink sk = sink(sink);
            bufferedSink = Okio.buffer(sk);
        }
        requestBody.writeTo(bufferedSink);
        bufferedSink.flush();
    }

    @Override
    public boolean isOneShot() {
        return true;
    }

    private Sink sink(Sink sink) {
        return new ForwardingSink(sink) {
            long byteWritten = 0L;
            long contentLength = 0L;

            @Override
            public void write(@NonNull Buffer source, long byteCount) throws IOException {
                super.write(source, byteCount);
                if (contentLength == 0) {
                    contentLength = contentLength();
                }
                byteWritten += byteCount;
                if (okHttpResultCallBack != null) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            okHttpResultCallBack.onReqProgress(byteWritten, contentLength,
                                    byteWritten == contentLength);
                            if (contentLength != 0) {
                                int progress = (int) (byteWritten * 100 / contentLength);
                                okHttpResultCallBack.onRespProgress(progress);
                            }
                        }
                    });
                }
            }
        };
    }
}
