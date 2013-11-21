package com.vg.http;


import com.vg.api.VGClient;

/**
 * 
 * @author luopeng (luopeng@staff.sina.com.cn)
 */
public class AsyncVGRunner {
    /**
     * data request
     * @param url
     * @param params
     * @param httpMethod "GET"or “POST”
     * @param listener callback
     */
	public static void request(final String url, final VGParameters params,
			final String httpMethod, final RequestListener listener) {
		new Thread() {
			@Override
			public void run() {
				try {
					String resp = HttpManager.openUrl(url, httpMethod, params);
					listener.onComplete(resp);
				} catch (VGException e) {
					listener.onError(e);
				}
			}
		}.start();

	}

}
