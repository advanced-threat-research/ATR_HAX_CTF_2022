package com.f32.fit32;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.f32.fit32.constants.Constants;
import com.f32.fit32.defaults.SettingsDefaults;
import com.f32.fit32.helper.ActivityHelper;
import com.f32.fit32.helper.DialogHelper;
import com.f32.fit32.helper.LogHelper;
import com.f32.fit32.helper.SharedPreferencesHelper;
import com.f32.fit32.interfaces.F32Activity;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class WorkoutActivity extends AppCompatActivity implements F32Activity {

    private static int snackbarSeconds = 0;
    private static Timer snackbarTimer = null;
    private static Timer tempoTimer = null;

    private final static ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, ToneGenerator.MAX_VOLUME);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        final Context context = this;

        final String workoutIndex = getIntent().getStringExtra(SharedPreferencesHelper.WORKOUTS);
        final String workoutName = SharedPreferencesHelper.instance.getPreference(workoutIndex, SharedPreferencesHelper.NAME);

        if(workoutName.trim().length() > 0) {
            setTitle(workoutName);
        }

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        startTempoTimer();

        String keepScreenOn = SharedPreferencesHelper.instance.getPreference(SharedPreferencesHelper.instance.buildPreferenceString(SharedPreferencesHelper.SETTINGS, SettingsDefaults.KEEP_SCREEN_ON, SharedPreferencesHelper.SETTING));
        if(keepScreenOn.equals("Enabled")) {
            addFlagKeepScreenOn();
        }

        init();

        String howToWorkout = SharedPreferencesHelper.instance.getPreference(SharedPreferencesHelper.HOW_TO_WORKOUT);
        if(howToWorkout.equals("true")) {
            SharedPreferencesHelper.instance.setPreference(SharedPreferencesHelper.HOW_TO_WORKOUT, "false", Constants.MIN, Constants.MAX, true);

            DialogHelper.createDialog(
                    context,
                    DialogHelper.HOW_TO,
                    DialogHelper.CLOSE,
                    "",
                    "After completing a set touch the Reps button to enter the number of reps you completed. A rest timer will be started automatically.<br><br>" +
                            "Once you have completed the workout touch the Finish Workout button to save it to the History."
            );
        }
    }

    public void init() {
        final String weightUnit = SharedPreferencesHelper.instance.getPreference(SharedPreferencesHelper.instance.buildPreferenceString(SharedPreferencesHelper.SETTINGS, SettingsDefaults.UNIT, SharedPreferencesHelper.SETTING));

        final Context context = this;
        final String workoutIndex = getIntent().getStringExtra(SharedPreferencesHelper.WORKOUTS);

        final Map<String, Map<String, Boolean>> exerciseMap = new HashMap<>();
        final Map<String, String> historyMap = new HashMap<>();

        historyMap.put(SharedPreferencesHelper.DATE, Constants.DATE_FORMAT.format(new Date()));
        historyMap.put(SharedPreferencesHelper.NAME, getTitle().toString());
        historyMap.put(SharedPreferencesHelper.NOTES, "");

        List<String> exercises = SharedPreferencesHelper.instance.getExercises(workoutIndex);

        for(String exerciseIndex : exercises) {
            final String exerciseType = SharedPreferencesHelper.instance.getPreference(exerciseIndex, SharedPreferencesHelper.EXERCISE_TYPE);
            final Map<String, Boolean> setMap = new HashMap<>();
            exerciseMap.put(exerciseIndex, setMap);

            final String historyExerciseIndex = exerciseIndex.substring(exerciseIndex.lastIndexOf(SharedPreferencesHelper.EXERCISES));

            historyMap.put(SharedPreferencesHelper.instance.buildPreferenceString(historyExerciseIndex, SharedPreferencesHelper.NAME), SharedPreferencesHelper.instance.getPreference(exerciseIndex, SharedPreferencesHelper.NAME));
            historyMap.put(SharedPreferencesHelper.instance.buildPreferenceString(historyExerciseIndex, SharedPreferencesHelper.EXERCISE_TYPE), SharedPreferencesHelper.instance.getPreference(exerciseIndex, SharedPreferencesHelper.EXERCISE_TYPE));

            /* add exercise title */
            String PR = "";
            if(SharedPreferencesHelper.instance.getVolume(exerciseIndex) > Integer.valueOf(SharedPreferencesHelper.instance.getPreference(exerciseIndex, SharedPreferencesHelper.SUCCESS_VOLUME))) {
                PR = " PR";
            }

            final AppCompatButton buttonExercise = ActivityHelper.createButton(context, SharedPreferencesHelper.instance.getPreference(exerciseIndex, SharedPreferencesHelper.NAME) + PR, false);
            buttonExercise.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
            buttonExercise.setTextColor(ContextCompat.getColor(context, R.color.colorWhite));

            ((ViewGroup) findViewById(R.id.content_workout)).addView(buttonExercise);
            /**/

            int historySetCount = 0;

            //get all sets
            List<String> sets = SharedPreferencesHelper.instance.getSets(exerciseIndex);

            /* add warm-up sets */
            if(exerciseType.equals(DialogHelper.FREE_WEIGHT) && sets.size() > 0) {
                final int numberOfWarmupSets = Integer.valueOf(SharedPreferencesHelper.instance.getPreference(exerciseIndex, SharedPreferencesHelper.WARMUP_SETS));
                final int firstSetWeight = Integer.valueOf(SharedPreferencesHelper.instance.getPreference(sets.get(0), SharedPreferencesHelper.WEIGHT));
                final int increment = Integer.valueOf(SharedPreferencesHelper.instance.getPreference(exerciseIndex, SharedPreferencesHelper.INCREMENT));
                final int minWeight = Integer.valueOf(SharedPreferencesHelper.instance.getPreference(exerciseIndex, SharedPreferencesHelper.MIN_WEIGHT));

                final double warmupPercent = Double.valueOf(SharedPreferencesHelper.instance.getPreference(SharedPreferencesHelper.instance.buildPreferenceString(SharedPreferencesHelper.SETTINGS, SettingsDefaults.WARMUP, SharedPreferencesHelper.SETTING)));

                for (int i = 0; i < numberOfWarmupSets; i++) {
                    double halfWeight = firstSetWeight / (100.0 / warmupPercent);
                    double warmupIncrement = (firstSetWeight - halfWeight) / numberOfWarmupSets;
                    int targetWarmupWeight = (int) ((warmupIncrement * i) + halfWeight);
                    int warmupWeight = minWeight;
                    while(warmupWeight <= (targetWarmupWeight - increment)) {
                        warmupWeight += increment;
                    }

                    final AppCompatButton buttonWarmupSet = ActivityHelper.createButton(context, "WU " + (i + 1), false);
                    final AppCompatButton buttonWarmupWeight = ActivityHelper.createButton(context, warmupWeight + " " + weightUnit, false);

                    final String historySetIndex = SharedPreferencesHelper.instance.buildPreferenceString(historyExerciseIndex, SharedPreferencesHelper.SETS, String.valueOf(historySetCount++));
                    historyMap.put(SharedPreferencesHelper.instance.buildPreferenceString(historySetIndex, SharedPreferencesHelper.NAME), buttonWarmupSet.getText().toString());
                    historyMap.put(SharedPreferencesHelper.instance.buildPreferenceString(historySetIndex, SharedPreferencesHelper.WEIGHT), buttonWarmupWeight.getText().toString());

                    final AppCompatButton buttonWarmupReps = createRepsButton(exerciseIndex, null, null, historyMap, historySetIndex);

                    final LinearLayoutCompat linearLayout = new LinearLayoutCompat(context);
                    linearLayout.setOrientation(LinearLayoutCompat.HORIZONTAL);
                    linearLayout.addView(buttonWarmupSet);
                    linearLayout.addView(buttonWarmupWeight);
                    linearLayout.addView(buttonWarmupReps);

                    ((ViewGroup) findViewById(R.id.content_workout)).addView(linearLayout);
                }
            }
            /**/

            /* add working sets */
            int setNum = 1;
            for (String setIndex : sets) {
                setMap.put(setIndex, false);

                final AppCompatButton buttonSet = ActivityHelper.createButton(context, "Set " + String.valueOf(setNum++), false);

                AppCompatButton buttonWeight;
                if(exerciseType.equals(DialogHelper.FREE_WEIGHT)) {
                    buttonWeight = ActivityHelper.createButton(context, SharedPreferencesHelper.instance.getPreference(setIndex, SharedPreferencesHelper.WEIGHT) + " " + weightUnit, false);
                } else {
                    buttonWeight = ActivityHelper.createButton(context, "- " + weightUnit, false);
                }

                final String historySetIndex = SharedPreferencesHelper.instance.buildPreferenceString(historyExerciseIndex, SharedPreferencesHelper.SETS, String.valueOf(historySetCount++));
                historyMap.put(SharedPreferencesHelper.instance.buildPreferenceString(historySetIndex, SharedPreferencesHelper.NAME), buttonSet.getText().toString());
                historyMap.put(SharedPreferencesHelper.instance.buildPreferenceString(historySetIndex, SharedPreferencesHelper.WEIGHT), buttonWeight.getText().toString());

                final AppCompatButton buttonReps = createRepsButton(exerciseIndex, setMap, setIndex, historyMap, historySetIndex);

                final LinearLayoutCompat linearLayout = new LinearLayoutCompat(context);
                linearLayout.setOrientation(LinearLayoutCompat.HORIZONTAL);
                linearLayout.addView(buttonSet);
                linearLayout.addView(buttonWeight);
                linearLayout.addView(buttonReps);

                ((ViewGroup) findViewById(R.id.content_workout)).addView(linearLayout);
            }
            /**/
        }

        final AppCompatButton buttonNotes = ActivityHelper.createButton(context, SharedPreferencesHelper.NOTES, false);
        buttonNotes.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
        buttonNotes.setTextColor(ContextCompat.getColor(context, R.color.colorWhite));

        ((ViewGroup) findViewById(R.id.content_workout)).addView(buttonNotes);

        final AppCompatEditText editTextNotes = ActivityHelper.createEditText(context, "", "Add notes", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        editTextNotes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //do nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                historyMap.put(SharedPreferencesHelper.NOTES, s.toString());
            }
        });

        ((ViewGroup) findViewById(R.id.content_workout)).addView(editTextNotes);

        final AppCompatButton buttonFinish = ActivityHelper.createButton(context, "Finish Workout", true);
        buttonFinish.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
        buttonFinish.setTextColor(ContextCompat.getColor(context, R.color.colorWhite));
        buttonFinish.setSupportAllCaps(true);
        buttonFinish.setGravity(Gravity.CENTER);
        buttonFinish.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    v.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
                }
                return false;
            }
        });
        buttonFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTempoTimer();
                final AlertDialog dialog = DialogHelper.createDialog(context, "Finish Workout", DialogHelper.YES, DialogHelper.NO, "Finish workout and auto-update weights/reps?");
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean finishWorkout = true;
                        for (Map.Entry<String, Map<String, Boolean>> exerciseEntry : exerciseMap.entrySet()) {
                            boolean success = true;
                            String exerciseIndex = exerciseEntry.getKey();
                            for (Map.Entry<String, Boolean> setEntry : exerciseEntry.getValue().entrySet()) {
                                success = (success && setEntry.getValue());
                            }
                            finishWorkout = (finishWorkout && SharedPreferencesHelper.instance.finishWorkout(exerciseIndex, success));
                        }
                        finishWorkout = (finishWorkout && SharedPreferencesHelper.instance.addHistory(workoutIndex, historyMap));
                        dialog.dismiss();
                        if (finishWorkout) {
                            finish();
                        } else {
                            LogHelper.error("Failed to save and update the workout");
                        }
                    }
                });
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startTempoTimer();
                        dialog.dismiss();
                    }
                });
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        startTempoTimer();
                    }
                });
            }
        });

        ((ViewGroup) findViewById(R.id.content_workout)).addView(buttonFinish);
    }

    private AppCompatButton createRepsButton(final String exerciseIndex, final Map<String, Boolean> setMap, final String setIndex, final Map<String, String> historyMap, final String historySetIndex) {
        final Context context = this;
        final String exerciseType = SharedPreferencesHelper.instance.getPreference(exerciseIndex, SharedPreferencesHelper.EXERCISE_TYPE);
        int reps;
        if(exerciseType.equals(DialogHelper.FREE_WEIGHT)) {
            reps = Integer.valueOf(SharedPreferencesHelper.instance.getPreference(exerciseIndex, SharedPreferencesHelper.REPS));
        } else {
            reps = Integer.valueOf(SharedPreferencesHelper.instance.getPreference(setIndex, SharedPreferencesHelper.REPS));
        }
        AppCompatButton buttonReps = ActivityHelper.createButton(context, getResources().getQuantityString(R.plurals.reps, reps, reps), true);
        buttonReps.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, 1));
        buttonReps.setBackgroundColor(ContextCompat.getColor(context, R.color.colorLightGray));

        historyMap.put(SharedPreferencesHelper.instance.buildPreferenceString(historySetIndex, SharedPreferencesHelper.REPS), getResources().getQuantityString(R.plurals.reps, 0, 0));

        buttonReps.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (((AppCompatButton) v).getCurrentTextColor() == ContextCompat.getColor(context, R.color.colorAccentText)) {
                        v.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccentDark));
                    } else if (((AppCompatButton) v).getCurrentTextColor() == ContextCompat.getColor(context, R.color.colorGreenText)) {
                        v.setBackgroundColor(ContextCompat.getColor(context, R.color.colorGreenDark));
                    } else {
                        v.setBackgroundColor(ContextCompat.getColor(context, R.color.colorGray));
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    if (((AppCompatButton) v).getCurrentTextColor() == ContextCompat.getColor(context, R.color.colorAccentText)) {
                        v.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
                    } else if (((AppCompatButton) v).getCurrentTextColor() == ContextCompat.getColor(context, R.color.colorGreenText)) {
                        v.setBackgroundColor(ContextCompat.getColor(context, R.color.colorGreen));
                    }else {
                        v.setBackgroundColor(ContextCompat.getColor(context, R.color.colorLightGray));
                    }
                }
                return false;
            }
        });
        buttonReps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int textColor = ((AppCompatButton) v).getCurrentTextColor();
                String text = ((AppCompatButton) v).getText().toString();
                int reps = Integer.valueOf(text.substring(0, text.indexOf(" ")));

                if (textColor == ContextCompat.getColor(context, R.color.colorAccentText) || textColor == ContextCompat.getColor(context, R.color.colorGreenText)) {
                    if (reps == 0) {
                        if(exerciseType.equals(DialogHelper.FREE_WEIGHT)) {
                            reps = Integer.valueOf(SharedPreferencesHelper.instance.getPreference(exerciseIndex, SharedPreferencesHelper.REPS));
                        } else {
                            reps = Integer.valueOf(SharedPreferencesHelper.instance.getPreference(setIndex, SharedPreferencesHelper.REPS));
                        }

                        v.setBackgroundColor(ContextCompat.getColor(context, R.color.colorLightGray));
                        ((AppCompatButton) v).setTextColor(ActivityHelper.getButton(context).getCurrentTextColor());
                        ((AppCompatButton) v).setText(getResources().getQuantityString(R.plurals.reps, reps, reps));

                        historyMap.put(SharedPreferencesHelper.instance.buildPreferenceString(historySetIndex, SharedPreferencesHelper.REPS), getResources().getQuantityString(R.plurals.reps, 0, 0));
                    } else {
                        reps = reps - 1;

                        v.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
                        ((AppCompatButton) v).setTextColor(ContextCompat.getColor(context, R.color.colorAccentText));
                        ((AppCompatButton) v).setText(getResources().getQuantityString(R.plurals.reps, reps, reps));

                        historyMap.put(SharedPreferencesHelper.instance.buildPreferenceString(historySetIndex, SharedPreferencesHelper.REPS), ((AppCompatButton) v).getText().toString());
                    }
                    if(setMap != null && setIndex != null) {
                        setMap.put(setIndex, false);
                    }
                } else {
                    v.setBackgroundColor(ContextCompat.getColor(context, R.color.colorGreen));
                    ((AppCompatButton) v).setTextColor(ContextCompat.getColor(context, R.color.colorGreenText));

                    historyMap.put(SharedPreferencesHelper.instance.buildPreferenceString(historySetIndex, SharedPreferencesHelper.REPS), ((AppCompatButton) v).getText().toString());

                    if(setMap != null && setIndex != null) {
                        setMap.put(setIndex, true);
                    }
                }

                final Snackbar snackbar = Snackbar.make(v, "Rest Time: " + SharedPreferencesHelper.instance.getPreference(exerciseIndex, SharedPreferencesHelper.REST), Snackbar.LENGTH_INDEFINITE);

                snackbar.setAction("Dismiss", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        snackbar.dismiss();
                        clearFlagKeepScreenOn();
                        startTempoTimer();
                    }
                });

                snackbar.show();
                startSnackbarTimer(snackbar, Integer.valueOf(SharedPreferencesHelper.instance.getPreference(exerciseIndex, SharedPreferencesHelper.REST)));
                addFlagKeepScreenOn();
                stopTempoTimer();
            }
        });

        return buttonReps;
    }

    private void startSnackbarTimer(final Snackbar snackbar, final int seconds) {
        stopSnackbarTimer();
        snackbarSeconds = seconds;
        snackbarTimer = new Timer();
        snackbarTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(snackbar.isShownOrQueued()) {
                            if(snackbarSeconds > 0) {
                                snackbar.setText("Rest Time: " + String.valueOf(snackbarSeconds));
                                snackbarSeconds--;
                            } else {
                                snackbar.dismiss();
                                beep(2);
                                clearFlagKeepScreenOn();
                                startTempoTimer();
                            }
                        } else {
                            stopSnackbarTimer();
                        }
                    }
                });
            }
        }, 0, 1000);
    }

    private void stopSnackbarTimer() {
        if(snackbarTimer != null) {
            snackbarTimer.cancel();
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

    private void startTempoTimer() {
        stopTempoTimer();
        final int interval = Integer.valueOf(SharedPreferencesHelper.instance.getPreference(SharedPreferencesHelper.instance.buildPreferenceString(SharedPreferencesHelper.SETTINGS, SettingsDefaults.TEMPO_INTERVAL, SharedPreferencesHelper.SETTING)));
        if(interval > 0) {
            tempoTimer = new Timer();
            tempoTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP);
                }
            }, 2000, (interval * 1000));
        }
    }

    private void stopTempoTimer() {
        if(tempoTimer != null) {
            tempoTimer.cancel();
        }
    }

    private void addFlagKeepScreenOn() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void clearFlagKeepScreenOn() {
        String keepScreenOn = SharedPreferencesHelper.instance.getPreference(SharedPreferencesHelper.instance.buildPreferenceString(SharedPreferencesHelper.SETTINGS, SettingsDefaults.KEEP_SCREEN_ON, SharedPreferencesHelper.SETTING));
        if(!keepScreenOn.equals("Enabled")) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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
        stopTempoTimer();
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
