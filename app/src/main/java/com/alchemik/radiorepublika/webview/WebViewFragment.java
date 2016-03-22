/*
package com.alchemik.radiorepublika.webview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.telephony.PhoneStateListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;

import com.alchemik.radiorepublika.CustomWebViewClient;
import com.alchemik.radiorepublika.R;
import com.alchemik.radiorepublika.util.ConnectionUtil;

import static android.telephony.TelephonyManager.CALL_STATE_IDLE;
import static android.telephony.TelephonyManager.CALL_STATE_RINGING;

*/
/**
 * Created by Leszek Jasek on 3/21/2016.
 *//*

public class WebViewFragment extends Fragment {
    private static final String REPUBLIKA_URL = "http://telewizjarepublika.pl/";

    private WebView mWebView;
    private ImageButton webViewSyncBtn;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.webview_fragment);
        mWebView = (WebView) view.findViewById(R.id.news_webview);

        if (ConnectionUtil.isConnectedToNetwork(getActivity())) {
            loadWebView();
        } else {
            mWebView.setBackgroundColor(0x00000000); //Color to transparent
            webViewSyncBtn.setVisibility(View.VISIBLE);
        }

        webViewSyncBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectionUtil.isConnectedToNetwork(getActivity())) {
                    webViewSyncBtn.setVisibility(View.GONE);
                    loadWebView();
                }
            }
        });

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void loadWebView() {

        //TODO: this fragment/activiity must implement callback
        mWebView.setWebViewClient(new CustomWebViewClient(this));
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mWebView.loadUrl(REPUBLIKA_URL);
    }

    // Fixme: method of activity?
    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
            return;
        } else {
            finish();
        }

        super.onBackPressed();
    }
}
*/
