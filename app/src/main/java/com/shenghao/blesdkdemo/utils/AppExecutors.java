package com.shenghao.blesdkdemo.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppExecutors {
    private static AppExecutors mInstance;
    private ExecutorService ioThreadPool;
    private ExecutorService singleThreadPool;

    public static AppExecutors getInstance() {
        if (mInstance == null) {
            mInstance = new AppExecutors();
        }
        return mInstance;
    }

    private AppExecutors() {
        initIoPool();
        initSinglePool();
    }

    private void initIoPool() {
        ioThreadPool = Executors.newCachedThreadPool();
    }

    private void initSinglePool() {
        singleThreadPool = Executors.newSingleThreadExecutor();
    }

    public void executeIoTask(Runnable runnable) {
        if (ioThreadPool == null) {
            initIoPool();
        }
        ioThreadPool.execute(runnable);
    }

    public void executeSingleTask(Runnable runnable) {
        if (singleThreadPool == null) {
            initSinglePool();
        }
        singleThreadPool.execute(runnable);
    }

    private synchronized void destroyIoPool() {
        if (ioThreadPool != null) {
            try {
                ioThreadPool.shutdownNow();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                ioThreadPool = null;
            }
        }
    }

    private synchronized void destroySinglePool() {
        if (singleThreadPool != null) {
            try {
                singleThreadPool.shutdownNow();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                singleThreadPool = null;
            }
        }
    }

    public synchronized void destroy() {
        if (mInstance != null) {
            mInstance.destroyIoPool();
            mInstance.destroySinglePool();
        }
        mInstance = null;
    }
}
