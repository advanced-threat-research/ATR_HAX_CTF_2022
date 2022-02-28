package com.f32.fit32;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;

import com.f32.fit32.constants.Constants;
import com.f32.fit32.helper.ActivityHelper;
import com.f32.fit32.helper.DialogHelper;
import com.f32.fit32.helper.LogHelper;
import com.f32.fit32.helper.SharedPreferencesHelper;

public class HomeActivity extends AppCompatActivity {

    private void initApp(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String title = getString(R.string.title_activity_home);
        builder.setTitle(title);
        String button_text = getString(R.string.label_text_button);
        builder.setNegativeButton(button_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        builder.setCancelable(false);
        builder.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        final Context context = this;

        init();
        initApp();
    }

    private void init() {
        final Context context = this;

        SharedPreferencesHelper.instance.init(context);

        AppCompatButton buttonPrograms = ActivityHelper.createButton(context, SharedPreferencesHelper.PROGRAMS, true);
        buttonPrograms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProgramsActivity.class);
                startActivity(intent);
            }
        });

        ((ViewGroup)findViewById(R.id.content_home)).addView(buttonPrograms);

        AppCompatButton buttonMeasurements = ActivityHelper.createButton(context, SharedPreferencesHelper.MEASUREMENTS, true);
        buttonMeasurements.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MeasurementsActivity.class);
                startActivity(intent);
            }
        });

        ((ViewGroup)findViewById(R.id.content_home)).addView(buttonMeasurements);

        AppCompatButton buttonHiit = ActivityHelper.createButton(context, SharedPreferencesHelper.HIIT_TIMER, true);
        buttonHiit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, HiitSettingsActivity.class);
                startActivity(intent);
            }
        });

        ((ViewGroup)findViewById(R.id.content_home)).addView(buttonHiit);

        ((ViewGroup)findViewById(R.id.content_home)).addView(ActivityHelper.getSeparatorView(context));

        ((ViewGroup)findViewById(R.id.content_home)).addView(ActivityHelper.getSeparatorView(context));

        AppCompatButton buttonBackup = ActivityHelper.createButton(context, SharedPreferencesHelper.BACKUP, true);
        buttonBackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog dialog = DialogHelper.createDialog(context, SharedPreferencesHelper.BACKUP, DialogHelper.YES, DialogHelper.NO, "Are you sure you want to backup all of your data to " + SharedPreferencesHelper.BACKUP_FILE + "? This will overwrite the current backup if it exists. Continue?");
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (SharedPreferencesHelper.instance.backup()) {
                            LogHelper.toast("Data backup complete!");
                            dialog.dismiss();
                        } else {
                            LogHelper.error("Data backup failed.");
                        }
                    }
                });
            }
        });

        ((ViewGroup)findViewById(R.id.content_home)).addView(buttonBackup);

        AppCompatButton buttonRestore = ActivityHelper.createButton(context, SharedPreferencesHelper.RESTORE, true);
        buttonRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog dialog = DialogHelper.createDialog(context, SharedPreferencesHelper.RESTORE, DialogHelper.YES, DialogHelper.NO, "Are you sure you want to restore all of your data from " + SharedPreferencesHelper.BACKUP_FILE + "? This will overwrite all current app data. Continue?");
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(SharedPreferencesHelper.instance.restore()) {
                            LogHelper.toast("Data restore complete!");
                            dialog.dismiss();
                        } else {
                            LogHelper.error("Data restore failed.");
                        }
                    }
                });
            }
        });

        ((ViewGroup)findViewById(R.id.content_home)).addView(buttonRestore);

        ((ViewGroup)findViewById(R.id.content_home)).addView(ActivityHelper.getSeparatorView(context));

        AppCompatButton buttonSettings = ActivityHelper.createButton(context, SharedPreferencesHelper.SETTINGS, true);
        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SettingsActivity.class);
                startActivity(intent);
            }
        });

        ((ViewGroup)findViewById(R.id.content_home)).addView(buttonSettings);

        AppCompatButton buttonHelp = ActivityHelper.createButton(context, SharedPreferencesHelper.HELP, true);
        buttonHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, HelpActivity.class);
                startActivity(intent);
            }
        });

        ((ViewGroup)findViewById(R.id.content_home)).addView(buttonHelp);

        ((ViewGroup)findViewById(R.id.content_home)).addView(ActivityHelper.getSeparatorView(context));

        String verse = "\"Father, please forgive for these gains I'm about to receive.\" - Dom Mazzetti.";

        AppCompatButton buttonVerse = ActivityHelper.createButton(context, verse, false);
        buttonVerse.setTextColor(ContextCompat.getColor(context, R.color.colorGray));

        ((ViewGroup)findViewById(R.id.content_home)).addView(buttonVerse);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
