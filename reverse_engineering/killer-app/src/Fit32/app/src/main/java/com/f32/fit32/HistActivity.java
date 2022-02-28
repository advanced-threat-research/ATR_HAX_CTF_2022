package com.f32.fit32;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.f32.fit32.helper.ActivityHelper;
import com.f32.fit32.helper.SharedPreferencesHelper;
import com.f32.fit32.interfaces.F32Activity;

import java.util.List;

public class HistActivity extends AppCompatActivity implements F32Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hist);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        final String historyIndex = getIntent().getStringExtra(SharedPreferencesHelper.HISTORY);
        final String historyName = SharedPreferencesHelper.instance.getPreference(historyIndex, SharedPreferencesHelper.NAME);
        final String historyDate = SharedPreferencesHelper.instance.getPreference(historyIndex, SharedPreferencesHelper.DATE);

        if(historyName.trim().length() > 0 && historyDate.trim().length() > 0) {
            setTitle(historyName + " " + historyDate);
        }

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        init();
    }

    public void init() {
        final Context context = this;
        final String historyIndex = getIntent().getStringExtra(SharedPreferencesHelper.HISTORY);

        List<String> exercises = SharedPreferencesHelper.instance.getExercises(historyIndex);

        for(String exerciseIndex : exercises) {
            final AppCompatButton buttonExercise = ActivityHelper.createButton(context, SharedPreferencesHelper.instance.getPreference(exerciseIndex, SharedPreferencesHelper.NAME), false);
            buttonExercise.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
            buttonExercise.setTextColor(ContextCompat.getColor(context, R.color.colorWhite));

            ((ViewGroup) findViewById(R.id.content_hist)).addView(buttonExercise);

            List<String> sets = SharedPreferencesHelper.instance.getSets(exerciseIndex);

            for(String setIndex : sets) {
                final AppCompatButton buttonSet = ActivityHelper.createButton(context, SharedPreferencesHelper.instance.getPreference(setIndex, SharedPreferencesHelper.NAME), false);
                final AppCompatButton buttonWeight = ActivityHelper.createButton(context, SharedPreferencesHelper.instance.getPreference(setIndex, SharedPreferencesHelper.WEIGHT), false);
                final AppCompatButton buttonReps = ActivityHelper.createButton(context, SharedPreferencesHelper.instance.getPreference(setIndex, SharedPreferencesHelper.REPS), false);

                final LinearLayoutCompat linearLayout = new LinearLayoutCompat(context);
                linearLayout.setOrientation(LinearLayoutCompat.HORIZONTAL);
                linearLayout.addView(buttonSet);
                linearLayout.addView(buttonWeight);
                linearLayout.addView(buttonReps);

                ((ViewGroup) findViewById(R.id.content_hist)).addView(linearLayout);
            }
        }

        final AppCompatButton buttonNotes = ActivityHelper.createButton(context, SharedPreferencesHelper.NOTES, false);
        buttonNotes.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
        buttonNotes.setTextColor(ContextCompat.getColor(context, R.color.colorWhite));

        ((ViewGroup) findViewById(R.id.content_hist)).addView(buttonNotes);

        final AppCompatButton buttonNotesText = ActivityHelper.createButton(context, SharedPreferencesHelper.instance.getPreference(historyIndex, SharedPreferencesHelper.NOTES), false);

        ((ViewGroup) findViewById(R.id.content_hist)).addView(buttonNotesText);
    }

    @Override
    public void startActivity(Intent intent) {
        if(intent.getComponent().getClassName().equals(HistoryActivity.class.getName())) {
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
