package com.f32.fit32;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.f32.fit32.helper.ActivityHelper;
import com.f32.fit32.helper.SharedPreferencesHelper;
import com.f32.fit32.interfaces.F32Activity;

public class HelpActivity extends AppCompatActivity implements F32Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        init();
    }

    public void init() {
        Context context = this;

        AppCompatButton buttonGeneralTitle = ActivityHelper.createButton(
                context,
                "Hint",
                Typeface.DEFAULT_BOLD,
                false);

        ((ViewGroup)findViewById(R.id.content_help)).addView(buttonGeneralTitle);

        ((ViewGroup)findViewById(R.id.content_help)).addView(ActivityHelper.getSeparatorView(context));

        AppCompatButton buttonGeneralContent = ActivityHelper.createButton(
                context,
                "My favorite rapper is Smali Biggs",
                false);

        ((ViewGroup)findViewById(R.id.content_help)).addView(buttonGeneralContent);
    }

    @Override
    public void startActivity(Intent intent) {
        if(intent.getComponent().getClassName().equals(HomeActivity.class.getName())) {
            finish();
        } else {
            super.startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

}
