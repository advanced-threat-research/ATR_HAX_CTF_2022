package com.f32.fit32;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.f32.fit32.helper.ActivityHelper;
import com.f32.fit32.helper.DialogHelper;
import com.f32.fit32.helper.LogHelper;
import com.f32.fit32.helper.SharedPreferencesHelper;
import com.f32.fit32.interfaces.F32Activity;

import java.util.Collections;
import java.util.List;

public class HistoryActivity extends AppCompatActivity implements F32Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        final String workoutIndex = getIntent().getStringExtra(SharedPreferencesHelper.WORKOUTS);
        final String workoutName = SharedPreferencesHelper.instance.getPreference(workoutIndex, SharedPreferencesHelper.NAME);

        if(workoutName.trim().length() > 0) {
            setTitle(workoutName + " " + getTitle().toString());
        }

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        init();
    }

    public void init() {
        final Context context = this;
        final AppCompatActivity activity = this;
        final String workoutIndex = getIntent().getStringExtra(SharedPreferencesHelper.WORKOUTS);

        List<String> histories =  SharedPreferencesHelper.instance.getHistory(workoutIndex);

        Collections.reverse(histories);

        for(String history : histories) {
            final String historyIndex = history;

            String notes = SharedPreferencesHelper.instance.getPreference(historyIndex, SharedPreferencesHelper.NOTES).trim();

            if(notes.length() > 0) {
                int notesMaxLength = notes.length() > 20 ? 20 : notes.length();
                notes = " - " + notes.substring(0, notesMaxLength) + (notes.length() > notesMaxLength ? "..." : "");
            }

            final AppCompatButton buttonHistory = ActivityHelper.createButton(context, SharedPreferencesHelper.instance.getPreference(historyIndex, SharedPreferencesHelper.DATE) + notes, true);
            buttonHistory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, HistActivity.class);
                    intent.putExtra(SharedPreferencesHelper.HISTORY, historyIndex);
                    startActivity(intent);
                }
            });
            buttonHistory.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final AlertDialog dialog = DialogHelper.createDialog(context, SharedPreferencesHelper.instance.getPreference(historyIndex, SharedPreferencesHelper.DATE), DialogHelper.HISTORY_MENU);
                    dialog.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String text = ((AppCompatTextView) view).getText().toString();
                            switch (text) {
                                case DialogHelper.MOVE_UP:
                                    //call move down because history is displayed in reverse order
                                    ActivityHelper.moveDown(activity, R.id.content_history, historyIndex);
                                    dialog.dismiss();
                                    break;
                                case DialogHelper.MOVE_DOWN:
                                    //call move up because history is displayed in reverse order
                                    ActivityHelper.moveUp(activity, R.id.content_history, historyIndex);
                                    dialog.dismiss();
                                    break;
                                case DialogHelper.REMOVE:
                                    DialogHelper.createRemoveDialog(activity, R.id.content_history, historyIndex, "", "", SharedPreferencesHelper.DATE);
                                    dialog.dismiss();
                                    break;
                                default:
                                    LogHelper.error("Received unknown menu selection: " + text);
                            }
                        }
                    });
                    return true;
                }
            });

            ((ViewGroup) findViewById(R.id.content_history)).addView(buttonHistory);
        }
    }

    @Override
    public void startActivity(Intent intent) {
        if(intent.getComponent().getClassName().equals(WorkoutsActivity.class.getName())) {
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
