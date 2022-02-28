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

import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MeasurementActivity extends AppCompatActivity implements F32Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurement);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        final Context context = this;
        final String measurementIndex = getIntent().getStringExtra(SharedPreferencesHelper.MEASUREMENTS);
        final String measurementName = SharedPreferencesHelper.instance.getPreference(measurementIndex, SharedPreferencesHelper.NAME);

        if(measurementName.trim().length() > 0) {
            setTitle(measurementName);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AppCompatEditText editTextDate = DialogHelper.createEditText(context, Constants.DATE_FORMAT.format(new Date()), SharedPreferencesHelper.DATE, InputType.TYPE_CLASS_DATETIME|InputType.TYPE_DATETIME_VARIATION_DATE);
                final AppCompatEditText editTextEntry = DialogHelper.createEditText(context, "", getTitle().toString(), InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);

                final AlertDialog dialog = DialogHelper.createDialog(context, DialogHelper.CREATE, DialogHelper.CREATE, editTextDate, editTextEntry);

                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (SharedPreferencesHelper.instance.addEntry(measurementIndex, editTextDate.getText().toString(), editTextEntry.getText().toString())) {
                            ((ViewGroup) findViewById(R.id.content_measurement)).removeAllViews();
                            init();
                            dialog.dismiss();
                        } else {
                            LogHelper.error("Failed to add the entry");
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
        final String measurementIndex = getIntent().getStringExtra(SharedPreferencesHelper.MEASUREMENTS);

        List<String> entries = SharedPreferencesHelper.instance.getEntries(measurementIndex);

        Collections.reverse(entries);

        float entryCount = 0;
        float entryTotal = 0;
        float entryCurrent = 0;
        Date entryCurrentDate = null;
        float entryAverage = 0;
        float entryHigh = 0;
        float entryLow = 0;
        for(String entryIndex : entries) {
            String entryDate = SharedPreferencesHelper.instance.getPreference(entryIndex, SharedPreferencesHelper.DATE);
            String entryValue = SharedPreferencesHelper.instance.getPreference(entryIndex, SharedPreferencesHelper.ENTRY);

            try {
                int oneWeek = (7 * 24 * 60 * 60 * 1000);

                Date dateEntry = Constants.DATE_FORMAT.parse(entryDate);
                Date dateWeek = new Date(System.currentTimeMillis() - oneWeek);

                if(dateEntry.after(dateWeek)) {
                    entryCount++;
                    entryTotal += Float.valueOf(entryValue);
                    entryHigh = entryHigh == 0 || Float.valueOf(entryValue) > entryHigh ? Float.valueOf(entryValue) : entryHigh;
                    entryLow = entryLow == 0 || Float.valueOf(entryValue) < entryLow ? Float.valueOf(entryValue) : entryLow;
                }

                if(entryCurrentDate == null || dateEntry.after(entryCurrentDate) || dateEntry.equals(entryCurrentDate)) {
                    entryCurrent = Float.valueOf(entryValue);
                    entryCurrentDate = dateEntry;
                }

            } catch(ParseException e) {
                LogHelper.error(e.toString());
            }
        }
        entryAverage = entryCount > 0 ? entryTotal / entryCount : entryAverage;

        AppCompatButton buttonCurrent = ActivityHelper.createButton(context, DialogHelper.CURRENT + ": " + String.format(Locale.getDefault(), "%.1f", entryCurrent), false);
        AppCompatButton buttonAverage = ActivityHelper.createButton(context, DialogHelper.AVERAGE + ": " + String.format(Locale.getDefault(), "%.1f", entryAverage), false);
        AppCompatButton buttonHigh = ActivityHelper.createButton(context, DialogHelper.HIGH + ": " + String.format(Locale.getDefault(), "%.1f", entryHigh), false);
        AppCompatButton buttonLow = ActivityHelper.createButton(context, DialogHelper.LOW + ": " + String.format(Locale.getDefault(), "%.1f", entryLow), false);

        ((ViewGroup)findViewById(R.id.content_measurement)).addView(buttonCurrent);
        ((ViewGroup)findViewById(R.id.content_measurement)).addView(buttonAverage);
        ((ViewGroup)findViewById(R.id.content_measurement)).addView(buttonHigh);
        ((ViewGroup)findViewById(R.id.content_measurement)).addView(buttonLow);

        for(String entry : entries) {
            final String entryIndex = entry;
            final String date = SharedPreferencesHelper.instance.getPreference(entryIndex, SharedPreferencesHelper.DATE) + ": ";

            final AppCompatButton buttonEntry = ActivityHelper.createButton(context, date + SharedPreferencesHelper.instance.getPreference(entryIndex, SharedPreferencesHelper.ENTRY), true);
            buttonEntry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogHelper.createMeasurementEditDialog(context, entryIndex, getTitle().toString(), date, InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
                }
            });
            buttonEntry.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final AlertDialog dialog = DialogHelper.createDialog(context, date + SharedPreferencesHelper.instance.getPreference(entryIndex, SharedPreferencesHelper.ENTRY), DialogHelper.MEASUREMENT_MENU);
                    dialog.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String text = ((AppCompatTextView) view).getText().toString();
                            switch (text) {
                                case DialogHelper.MOVE_UP:
                                    //call move down because history is displayed in reverse order
                                    ActivityHelper.moveDown(activity, R.id.content_measurement, entryIndex);
                                    dialog.dismiss();
                                    break;
                                case DialogHelper.MOVE_DOWN:
                                    //call move up because history is displayed in reverse order
                                    ActivityHelper.moveUp(activity, R.id.content_measurement, entryIndex);
                                    dialog.dismiss();
                                    break;
                                case DialogHelper.REMOVE:
                                    DialogHelper.createRemoveDialog(activity, R.id.content_measurement, entryIndex, date, "", SharedPreferencesHelper.ENTRY);
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

            ((ViewGroup)findViewById(R.id.content_measurement)).addView(buttonEntry);
        }
    }

    @Override
    public void startActivity(Intent intent) {
        if(intent.getComponent().getClassName().equals(MeasurementsActivity.class.getName())) {
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
