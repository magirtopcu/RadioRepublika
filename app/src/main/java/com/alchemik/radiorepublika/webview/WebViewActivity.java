package com.alchemik.radiorepublika.webview;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.alchemik.radiorepublika.SingleFragmentActivity;

/**
 * Created by Leszek Jasek on 3/21/2016.
 */
public class WebViewActivity extends SingleFragmentActivity {

    @Override
    public Fragment createFragment() {
        return new WebViewFragment();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setTitle("O aplikacji");

    }
}
