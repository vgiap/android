package com.vg.delivery;

/**
 * Created by huadong on 11/27/13.
 *
 * process download goods static file
 */
public interface DownloadListener {
    public void onBeginDownload();
    public void onProgress(int progress);
    public void onEndDownload();
}
