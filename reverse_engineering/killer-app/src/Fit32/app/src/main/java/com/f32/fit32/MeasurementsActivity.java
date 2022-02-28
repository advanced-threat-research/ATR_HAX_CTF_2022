package com.f32.fit32;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.f32.fit32.constants.Constants;
import com.f32.fit32.helper.ActivityHelper;
import com.f32.fit32.helper.DialogHelper;
import com.f32.fit32.helper.LogHelper;
import com.f32.fit32.helper.SharedPreferencesHelper;
import com.f32.fit32.interfaces.F32Activity;

import java.util.List;

public class MeasurementsActivity extends AppCompatActivity implements F32Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurements);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        final Context context = this;

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AppCompatEditText editText = DialogHelper.createEditText(context, "", DialogHelper.MEASUREMENT_NAME, InputType.TYPE_CLASS_TEXT);

                final AlertDialog dialog = DialogHelper.createDialog(context, DialogHelper.CREATE, DialogHelper.CREATE, editText);

                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (SharedPreferencesHelper.instance.addMeasurement(editText.getText().toString())) {
                            ((ViewGroup) findViewById(R.id.content_measurements)).removeAllViews();
                            init();
                            dialog.dismiss();
                        } else {
                            LogHelper.error("Failed to add the measurement");
                        }
                    }
                });
            }
        });
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        init();
    }

    public void init() {
        final Context context = this;
        final AppCompatActivity activity = this;

        List<String> measurements =  SharedPreferencesHelper.instance.getMeasurements();

        for(String measurement : measurements) {
            final String measurementIndex = measurement;

            final AppCompatButton buttonMeasurement = ActivityHelper.createButton(context, SharedPreferencesHelper.instance.getPreference(measurementIndex, SharedPreferencesHelper.NAME), true);
            buttonMeasurement.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, MeasurementActivity.class);
                    intent.putExtra(SharedPreferencesHelper.MEASUREMENTS, measurementIndex);
                    startActivity(intent);
                }
            });
            buttonMeasurement.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final AlertDialog dialog = DialogHelper.createDialog(context, SharedPreferencesHelper.instance.getPreference(measurementIndex, SharedPreferencesHelper.NAME), DialogHelper.RENAME_MENU);
                    dialog.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String text = ((AppCompatTextView) view).getText().toString();
                            switch (text) {
                                case DialogHelper.MOVE_UP:
                                    ActivityHelper.moveUp(activity, R.id.content_measurements, measurementIndex);
                                    dialog.dismiss();
                                    break;
                                case DialogHelper.MOVE_DOWN:
                                    ActivityHelper.moveDown(activity, R.id.content_measurements, measurementIndex);
                                    dialog.dismiss();
                                    break;
                                case DialogHelper.COPY:
                                    ActivityHelper.copy(activity, R.id.content_measurements, measurementIndex);
                                    dialog.dismiss();
                                    break;
                                case DialogHelper.RENAME:
                                    DialogHelper.createEditDialog(context, buttonMeasurement, measurementIndex, DialogHelper.RENAME, DialogHelper.MEASUREMENT_NAME, "", "", SharedPreferencesHelper.NAME, InputType.TYPE_CLASS_TEXT, Constants.MIN, Constants.MAX);
                                    dialog.dismiss();
                                    break;
                                case DialogHelper.REMOVE:
                                    DialogHelper.createRemoveDialog(activity, R.id.content_measurements, measurementIndex, "", "", SharedPreferencesHelper.NAME);
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

            ((ViewGroup)findViewById(R.id.content_measurements)).addView(buttonMeasurement);
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
