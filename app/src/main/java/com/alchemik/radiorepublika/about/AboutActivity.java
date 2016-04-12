package com.alchemik.radiorepublika.about;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.IntegerRes;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alchemik.radiorepublika.BuildConfig;
import com.alchemik.radiorepublika.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        ((TextView) findViewById(R.id.about_version)).setText("Wersja: " + BuildConfig.VERSION_NAME);

        TextView exomediaLib = (TextView) findViewById(R.id.about_libs_exomedia);
        TextView jodaTimeLib = (TextView) findViewById(R.id.about_libs_jodatime);
        TextView jsoupLib = (TextView) findViewById(R.id.about_libs_jsoup);
        TextView crashlyticsLib = (TextView) findViewById(R.id.about_libs_crashlytics);
        exomediaLib.setText(Html.fromHtml(getString(R.string.about_exomedia)));
        jodaTimeLib.setText(Html.fromHtml(getString(R.string.about_jodatime)));
        jsoupLib.setText(Html.fromHtml(getString(R.string.about_jsoup)));
        crashlyticsLib.setText(Html.fromHtml(getString(R.string.about_crashlytics)));

        View.OnClickListener onLibClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.about_libs_exomedia:
                        openWebPage(R.string.about_exomedia_url);
                        Toast.makeText(AboutActivity.this, ((TextView)v).getText(), Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.about_libs_jodatime:
                        openWebPage(R.string.about_jodatime_url);
                        Toast.makeText(AboutActivity.this, ((TextView)v).getText(), Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.about_libs_jsoup:
                        openWebPage(R.string.about_jsoup_url);
                        Toast.makeText(AboutActivity.this, ((TextView)v).getText(), Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.about_libs_crashlytics:
                        openWebPage(R.string.about_crashlytics_url);
                        Toast.makeText(AboutActivity.this, ((TextView)v).getText(), Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
        exomediaLib.setOnClickListener(onLibClickListener);
        jodaTimeLib.setOnClickListener(onLibClickListener);
        jsoupLib.setOnClickListener(onLibClickListener);
        crashlyticsLib.setOnClickListener(onLibClickListener);
    }

    public void openWebPage(@StringRes Integer url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(url)));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(AboutActivity.this, "Brak przeglądarki internetowej", Toast.LENGTH_SHORT).show();
        }
    }
}
