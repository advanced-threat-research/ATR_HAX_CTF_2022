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
import android.view.View;
import android.view.ViewGroup;

import com.f32.fit32.helper.ActivityHelper;
import com.f32.fit32.helper.DialogHelper;
import com.f32.fit32.helper.SharedPreferencesHelper;
import com.f32.fit32.interfaces.F32Activity;

import java.util.List;

public class SettingsActivity extends AppCompatActivity implements F32Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        init();
    }

    public void init() {
        final Context context = this;

        List<String> settings =  SharedPreferencesHelper.instance.getSettings();

        for(String setting : settings) {
            final String settingIndex = setting;

            final AppCompatButton buttonHeading = ActivityHelper.createButton(context, SharedPreferencesHelper.instance.getPreference(settingIndex, SharedPreferencesHelper.NAME), Typeface.DEFAULT_BOLD, false);

            ((ViewGroup)findViewById(R.id.content_settings)).addView(buttonHeading);

            ((ViewGroup)findViewById(R.id.content_settings)).addView(ActivityHelper.getSeparatorView(context));

            final AppCompatButton buttonSetting = ActivityHelper.createButton(context, SharedPreferencesHelper.instance.getPreference(settingIndex, SharedPreferencesHelper.SETTING), true);
            final String settingOptions = SharedPreferencesHelper.instance.getPreference(settingIndex, SharedPreferencesHelper.SETTING_OPTIONS);
            if(settingOptions != null && settingOptions.length() > 0) {
                buttonSetting.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogHelper.createDialog(context, buttonSetting, settingIndex, SharedPreferencesHelper.instance.getPreference(settingIndex, SharedPreferencesHelper.NAME), settingOptions.split(","));
                    }
                });
            } else {
                buttonSetting.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogHelper.createEditDialog(context, buttonSetting, settingIndex, SharedPreferencesHelper.instance.getPreference(settingIndex, SharedPreferencesHelper.NAME), SharedPreferencesHelper.instance.getPreference(settingIndex, SharedPreferencesHelper.SETTING_HINT), "", "", SharedPreferencesHelper.SETTING, Integer.valueOf(SharedPreferencesHelper.instance.getPreference(settingIndex, SharedPreferencesHelper.SETTING_TYPE)), Integer.valueOf(SharedPreferencesHelper.instance.getPreference(settingIndex, SharedPreferencesHelper.SETTING_MIN)), Integer.valueOf(SharedPreferencesHelper.instance.getPreference(settingIndex, SharedPreferencesHelper.SETTING_MAX)));
                    }
                });
            }

            ((ViewGroup)findViewById(R.id.content_settings)).addView(buttonSetting);
        }
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
