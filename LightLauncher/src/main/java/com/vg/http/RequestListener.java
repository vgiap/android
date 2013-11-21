package com.vg.http;

import java.io.IOException;

/**
 * 发起访问接口的请求时所需的回调接口
 * @author luopeng (luopeng@staff.sina.com.cn)
 */
public interface RequestListener {
    /**
     * process service http response
     * @param response
     */
	public void onComplete(String response);
	//public void onIOException(IOException e);
	public void onError(VGException e);
}
