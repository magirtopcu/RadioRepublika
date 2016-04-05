package com.alchemik.radiorepublika.webview;

import android.support.annotation.StringRes;
import android.util.Log;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.alchemik.radiorepublika.R;

/**
 * Created by Leszek Jasek on 3/19/2016.
 */
public class CustomWebViewClient extends WebViewClient {
    private static final String TAG = CustomWebViewClient.class.getSimpleName();

    private static final String REPUBLIKA_URL = "http://telewizjarepublika.pl/";
    private OnUrlOutOfScopeClickedListener mOnUrlOutOfScopeClicked;

    public CustomWebViewClient(OnUrlOutOfScopeClickedListener callback) {
        super();
        mOnUrlOutOfScopeClicked = callback;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Log.d(TAG, "shouldOverrideUrlLoading: url= " + url);

        //I/chromium: [INFO:CONSOLE(12)] "Not allowed to load local resource: file:///android_asset/webkit/android-weberror.png", source: data:text/html,chromewebdata (12)

        if (url.startsWith(REPUBLIKA_URL)) {
            view.loadUrl(url);
        } else {
            mOnUrlOutOfScopeClicked.showInfoMessage(R.string.webview_url_out_of_scope);
        }
        return true;
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
        mOnUrlOutOfScopeClicked.showInfoMessage(R.string.webview_error);
    }

    public interface OnUrlOutOfScopeClickedListener {
        void showInfoMessage(@StringRes int stringRes);
    }

}
