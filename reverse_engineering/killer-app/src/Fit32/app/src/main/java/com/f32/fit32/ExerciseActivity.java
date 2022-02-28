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
import com.f32.fit32.defaults.SettingsDefaults;
import com.f32.fit32.helper.ActivityHelper;
import com.f32.fit32.helper.DialogHelper;
import com.f32.fit32.helper.LogHelper;
import com.f32.fit32.helper.SharedPreferencesHelper;
import com.f32.fit32.interfaces.F32Activity;

import java.util.List;

public class ExerciseActivity extends AppCompatActivity implements F32Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        final Context context = this;
        final String exerciseIndex = getIntent().getStringExtra(SharedPreferencesHelper.EXERCISES);
        final String exerciseType = SharedPreferencesHelper.instance.getPreference(exerciseIndex, SharedPreferencesHelper.EXERCISE_TYPE);
        final String exerciseName = SharedPreferencesHelper.instance.getPreference(exerciseIndex, SharedPreferencesHelper.NAME);

        if(exerciseName.trim().length() > 0) {
            setTitle(exerciseName);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(exerciseType.equals(DialogHelper.FREE_WEIGHT)) {
                    final AppCompatEditText editText = DialogHelper.createEditText(context, "", DialogHelper.SET_WEIGHT, InputType.TYPE_CLASS_NUMBER);

                    final AlertDialog dialog = DialogHelper.createDialog(context, DialogHelper.CREATE, DialogHelper.CREATE, editText);

                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (SharedPreferencesHelper.instance.addSet(exerciseIndex, editText.getText().toString(), Integer.valueOf(SharedPreferencesHelper.instance.getPreference(exerciseIndex, SharedPreferencesHelper.MIN_WEIGHT)), Integer.valueOf(SharedPreferencesHelper.instance.getPreference(exerciseIndex, SharedPreferencesHelper.MAX_WEIGHT)))) {
                                ((ViewGroup) findViewById(R.id.content_exercise)).removeAllViews();
                                init();
                                dialog.dismiss();
                            } else {
                                LogHelper.error("Failed to add the set");
                            }
                        }
                    });
                } else {
                    final AppCompatEditText editText = DialogHelper.createEditText(context, "", DialogHelper.SET_REPS, InputType.TYPE_CLASS_NUMBER);

                    final AlertDialog dialog = DialogHelper.createDialog(context, DialogHelper.CREATE, DialogHelper.CREATE, editText);

                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (SharedPreferencesHelper.instance.addSet(exerciseIndex, editText.getText().toString(), Constants.NUMBER_OF_REPS_MIN, Constants.NUMBER_OF_REPS_MAX)) {
                                ((ViewGroup) findViewById(R.id.content_exercise)).removeAllViews();
                                init();
                                dialog.dismiss();
                            } else {
                                LogHelper.error("Failed to add the set");
                            }
                        }
                    });
                }
            }
        });
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        init();
    }

    public void init() {
        final String weightUnit = SharedPreferencesHelper.instance.getPreference(SharedPreferencesHelper.instance.buildPreferenceString(SharedPreferencesHelper.SETTINGS, SettingsDefaults.UNIT, SharedPreferencesHelper.SETTING));

        final Context context = this;
        final AppCompatActivity activity = this;
        final String exerciseIndex = getIntent().getStringExtra(SharedPreferencesHelper.EXERCISES);
        final String exerciseType = SharedPreferencesHelper.instance.getPreference(exerciseIndex, SharedPreferencesHelper.EXERCISE_TYPE);

        List<String> sets = SharedPreferencesHelper.instance.getSets(exerciseIndex);

        //ActivityHelper.createEditButton(activity, R.id.content_exercise, exerciseIndex, DialogHelper.CURRENT_VOLUME, weightUnit, SharedPreferencesHelper.CURRENT_VOLUME, InputType.TYPE_CLASS_NUMBER, false);
        //ActivityHelper.createEditButton(activity, R.id.content_exercise, exerciseIndex, DialogHelper.SUCCESS_VOLUME, weightUnit, SharedPreferencesHelper.SUCCESS_VOLUME, InputType.TYPE_CLASS_NUMBER, false);
        if(exerciseType.equals(DialogHelper.FREE_WEIGHT)) {
            ActivityHelper.createExerciseEditButton(activity, exerciseIndex, DialogHelper.WEIGHT_INCREMENT, weightUnit, SharedPreferencesHelper.INCREMENT, Constants.WEIGHT_INCREMENT_MIN, Constants.WEIGHT_INCREMENT_MAX);
            ActivityHelper.createExerciseEditButton(activity, exerciseIndex, DialogHelper.SETS_WARMUP, "sets", SharedPreferencesHelper.WARMUP_SETS, Constants.SETS_WARMUP_MIN, Constants.SETS_WARMUP_MAX);
            ActivityHelper.createExerciseEditButton(activity, exerciseIndex, DialogHelper.NUMBER_OF_REPS, "reps", SharedPreferencesHelper.REPS, Constants.NUMBER_OF_REPS_MIN, Constants.NUMBER_OF_REPS_MAX);
        }
        ActivityHelper.createExerciseEditButton(activity, exerciseIndex, DialogHelper.REST_IN_SECONDS, "sec", SharedPreferencesHelper.REST, Constants.REST_IN_SECONDS_MIN, Constants.REST_IN_SECONDS_MAX);
        if(exerciseType.equals(DialogHelper.FREE_WEIGHT)) {
            ActivityHelper.createExerciseEditButton(activity, exerciseIndex, DialogHelper.MIN_WEIGHT, weightUnit, SharedPreferencesHelper.MIN_WEIGHT, Constants.MIN_WEIGHT_MIN, Constants.MIN_WEIGHT_MAX);
            ActivityHelper.createExerciseEditButton(activity, exerciseIndex, DialogHelper.MAX_WEIGHT, weightUnit, SharedPreferencesHelper.MAX_WEIGHT, Constants.MAX_WEIGHT_MIN, Constants.MAX_WEIGHT_MAX);
        }

        int setNum = 1;
        for (String set : sets) {
            final String setIndex = set;
            final String setNumber = "Set " + String.valueOf(setNum++) + ": ";

            if(exerciseType.equals(DialogHelper.FREE_WEIGHT)) {
                final AppCompatButton buttonSet = ActivityHelper.createButton(context, setNumber + SharedPreferencesHelper.instance.getPreference(setIndex, SharedPreferencesHelper.WEIGHT) + " " + weightUnit, true);
                buttonSet.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogHelper.createEditDialog(context, buttonSet, setIndex, setNumber, DialogHelper.SET_WEIGHT, setNumber, weightUnit, SharedPreferencesHelper.WEIGHT, InputType.TYPE_CLASS_NUMBER, Integer.valueOf(SharedPreferencesHelper.instance.getPreference(exerciseIndex, SharedPreferencesHelper.MIN_WEIGHT)), Integer.valueOf(SharedPreferencesHelper.instance.getPreference(exerciseIndex, SharedPreferencesHelper.MAX_WEIGHT)));
                    }
                });
                buttonSet.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        final AlertDialog dialog = DialogHelper.createDialog(context, setNumber + SharedPreferencesHelper.instance.getPreference(setIndex, SharedPreferencesHelper.WEIGHT) + " " + weightUnit, DialogHelper.MENU);
                        dialog.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                String text = ((AppCompatTextView) view).getText().toString();
                                switch (text) {
                                    case DialogHelper.MOVE_UP:
                                        ActivityHelper.moveUp(activity, R.id.content_exercise, setIndex);
                                        dialog.dismiss();
                                        break;
                                    case DialogHelper.MOVE_DOWN:
                                        ActivityHelper.moveDown(activity, R.id.content_exercise, setIndex);
                                        dialog.dismiss();
                                        break;
                                    case DialogHelper.COPY:
                                        ActivityHelper.copy(activity, R.id.content_exercise, setIndex);
                                        dialog.dismiss();
                                        break;
                                    case DialogHelper.REMOVE:
                                        DialogHelper.createRemoveDialog(activity, R.id.content_exercise, setIndex, setNumber, weightUnit, SharedPreferencesHelper.WEIGHT);
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

                ((ViewGroup) findViewById(R.id.content_exercise)).addView(buttonSet);
            } else {
                final AppCompatButton buttonSet = ActivityHelper.createButton(context, setNumber + SharedPreferencesHelper.instance.getPreference(setIndex, SharedPreferencesHelper.REPS) + " " + SharedPreferencesHelper.REPS, true);
                buttonSet.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogHelper.createEditDialog(context, buttonSet, setIndex, setNumber, DialogHelper.SET_REPS, setNumber, SharedPreferencesHelper.REPS, SharedPreferencesHelper.REPS, InputType.TYPE_CLASS_NUMBER, Constants.NUMBER_OF_REPS_MIN, Constants.NUMBER_OF_REPS_MAX);
                    }
                });
                buttonSet.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        final AlertDialog dialog = DialogHelper.createDialog(context, setNumber + SharedPreferencesHelper.instance.getPreference(setIndex, SharedPreferencesHelper.REPS) + " " + SharedPreferencesHelper.REPS, DialogHelper.MENU);
                        dialog.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                String text = ((AppCompatTextView) view).getText().toString();
                                switch (text) {
                                    case DialogHelper.MOVE_UP:
                                        ActivityHelper.moveUp(activity, R.id.content_exercise, setIndex);
                                        dialog.dismiss();
                                        break;
                                    case DialogHelper.MOVE_DOWN:
                                        ActivityHelper.moveDown(activity, R.id.content_exercise, setIndex);
                                        dialog.dismiss();
                                        break;
                                    case DialogHelper.COPY:
                                        ActivityHelper.copy(activity, R.id.content_exercise, setIndex);
                                        dialog.dismiss();
                                        break;
                                    case DialogHelper.REMOVE:
                                        DialogHelper.createRemoveDialog(activity, R.id.content_exercise, setIndex, setNumber, SharedPreferencesHelper.REPS, SharedPreferencesHelper.REPS);
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

                ((ViewGroup) findViewById(R.id.content_exercise)).addView(buttonSet);
            }
        }
    }

    @Override
    public void startActivity(Intent intent) {
        if(intent.getComponent().getClassName().equals(ExercisesActivity.class.getName())) {
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
