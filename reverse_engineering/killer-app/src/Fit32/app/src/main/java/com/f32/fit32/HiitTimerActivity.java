package com.f32.fit32;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.f32.fit32.helper.ActivityHelper;
import com.f32.fit32.helper.SharedPreferencesHelper;
import com.f32.fit32.interfaces.F32Activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class HiitTimerActivity extends AppCompatActivity implements F32Activity {

    private static int seconds = 0;
    private static Timer timer = null;

    private int rounds;
    private AppCompatButton buttonRound;
    private AppCompatButton buttonAction;
    private AppCompatButton buttonTime;
    private final List<Map<String, Integer>> list = new ArrayList<>();
    private final ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, ToneGenerator.MAX_VOLUME);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hiit_timer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        init();
    }

    public void init() {
        final Context context = this;

        int go = getIntent().getIntExtra(SharedPreferencesHelper.HIIT_GO, 0);
        int rest = getIntent().getIntExtra(SharedPreferencesHelper.HIIT_REST, 0);
        rounds = getIntent().getIntExtra(SharedPreferencesHelper.HIIT_ROUNDS, 0);

        for(int i = 0; i < rounds; i++) {
            Map<String, Integer> map = new HashMap<>();
            map.put(SharedPreferencesHelper.HIIT_GO, go);
            map.put(SharedPreferencesHelper.HIIT_REST, rest);

            list.add(map);
        }

        buttonRound = ActivityHelper.createButton(context, "", false);
        buttonRound.setGravity(Gravity.CENTER);
        buttonRound.setTextSize(buttonRound.getTextSize() * 4);
        ((ViewGroup)findViewById(R.id.content_hiit_timer)).addView(buttonRound);

        buttonAction = ActivityHelper.createButton(context, "", false);
        buttonAction.setGravity(Gravity.CENTER);
        buttonAction.setTextSize(buttonAction.getTextSize() * 4);
        ((ViewGroup)findViewById(R.id.content_hiit_timer)).addView(buttonAction);

        buttonTime = ActivityHelper.createButton(context, "", false);
        buttonTime.setGravity(Gravity.CENTER);
        buttonTime.setTextSize(buttonTime.getTextSize() * 4);
        ((ViewGroup)findViewById(R.id.content_hiit_timer)).addView(buttonTime);

        continueTimer();
    }

    private void continueTimer() {
        if(list.size() > 0) {
            buttonRound.setText(getString(R.string.round_rounds, (rounds - list.size() + 1), rounds));
            int time;
            if(list.get(0).get(SharedPreferencesHelper.HIIT_GO) != null) {
                time = list.get(0).get(SharedPreferencesHelper.HIIT_GO);
                list.get(0).remove(SharedPreferencesHelper.HIIT_GO);
                buttonAction.setText(getString(R.string.go));
                buttonAction.setTextColor(ContextCompat.getColor(this, R.color.colorGreen));
                buttonTime.setTextColor(ContextCompat.getColor(this, R.color.colorGreen));
            } else {
                time = list.get(0).get(SharedPreferencesHelper.HIIT_REST);
                list.remove(0);
                buttonAction.setText(getString(R.string.rest));
                buttonAction.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
                buttonTime.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
            }
            if(time > 0) {
                beep(2);
                startTimer(time);
            } else {
                continueTimer();
            }
        } else {
            beep(4);
            finish();
        }
    }

    private void startTimer(final int initSeconds) {
        stopTimer();
        seconds = initSeconds;
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(seconds > 0) {
                            buttonTime.setText(String.valueOf(seconds));
                            seconds--;
                        } else {
                            stopTimer();
                            continueTimer();
                        }
                    }
                });
            }
        }, 0, 1000);
    }

    private void stopTimer() {
        if(timer != null) {
            timer.cancel();
        }
    }

    private void beep(final int times) {
        if(times > 0) {
            toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP2);
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    beep(times - 1);
                }
            }, 1000);
        }
    }

    @Override
    public void startActivity(Intent intent) {
        if(intent.getComponent().getClassName().equals(HiitSettingsActivity.class.getName())) {
            finish();
        } else {
            super.startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    @Override
    public void finish() {
        stopTimer();
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
