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
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
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

public class WorkoutsActivity extends AppCompatActivity implements F32Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workouts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        final Context context = this;
        final String programIndex = getIntent().getStringExtra(SharedPreferencesHelper.PROGRAMS);
        final String programName = SharedPreferencesHelper.instance.getPreference(programIndex, SharedPreferencesHelper.NAME);

        if(programName.trim().length() > 0) {
            setTitle(programName + " " + getTitle());
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AppCompatEditText editText = DialogHelper.createEditText(context, "", DialogHelper.WORKOUT_NAME, InputType.TYPE_CLASS_TEXT);

                final AlertDialog dialog = DialogHelper.createDialog(context, DialogHelper.CREATE, DialogHelper.CREATE, editText);

                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (SharedPreferencesHelper.instance.addWorkout(programIndex, editText.getText().toString())) {
                            ((ViewGroup) findViewById(R.id.content_workouts)).removeAllViews();
                            init();
                            dialog.dismiss();

                            List<String> workouts =  SharedPreferencesHelper.instance.getWorkouts(programIndex);
                            Intent intent = new Intent(context, ExercisesActivity.class);
                            intent.putExtra(SharedPreferencesHelper.WORKOUTS, workouts.get(workouts.size()-1));
                            startActivity(intent);
                        } else {
                            LogHelper.error("Failed to add the workout");
                        }
                    }
                });
            }
        });
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        init();

        String howToWorkouts = SharedPreferencesHelper.instance.getPreference(SharedPreferencesHelper.HOW_TO_WORKOUTS);
        if(howToWorkouts.equals("true")) {
            SharedPreferencesHelper.instance.setPreference(SharedPreferencesHelper.HOW_TO_WORKOUTS, "false", Constants.MIN, Constants.MAX, true);

            DialogHelper.createDialog(
                    context,
                    DialogHelper.HOW_TO,
                    DialogHelper.CLOSE,
                    "",
                    "Touch <img src='" + R.drawable.ic_message_play + "'> to start a workout.<br><br>" +
                    "Touch a workout to make changes."
            );
        }
    }

    public void init() {
        final Context context = this;
        final AppCompatActivity activity = this;
        final String programIndex = getIntent().getStringExtra(SharedPreferencesHelper.PROGRAMS);

        List<String> workouts =  SharedPreferencesHelper.instance.getWorkouts(programIndex);

        for(String workout : workouts) {
            final String workoutIndex = workout;

            final AppCompatImageButton buttonStart = ActivityHelper.createImageButton(context);
            buttonStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intentWorkout = new Intent(context, WorkoutActivity.class);
                    intentWorkout.putExtra(SharedPreferencesHelper.WORKOUTS, workoutIndex);
                    startActivity(intentWorkout);
                }
            });

            final AppCompatButton buttonWorkout = ActivityHelper.createButton(context, SharedPreferencesHelper.instance.getPreference(workoutIndex, SharedPreferencesHelper.NAME), true);
            buttonWorkout.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, 1));
            buttonWorkout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ExercisesActivity.class);
                    intent.putExtra(SharedPreferencesHelper.WORKOUTS, workoutIndex);
                    startActivity(intent);
                }
            });
            buttonWorkout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final AlertDialog dialog = DialogHelper.createDialog(context, SharedPreferencesHelper.instance.getPreference(workoutIndex, SharedPreferencesHelper.NAME), DialogHelper.WORKOUT_MENU);
                    dialog.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String text = ((AppCompatTextView) view).getText().toString();
                            switch (text) {
                                case DialogHelper.HISTORY:
                                    Intent intentHistory = new Intent(context, HistoryActivity.class);
                                    intentHistory.putExtra(SharedPreferencesHelper.WORKOUTS, workoutIndex);
                                    startActivity(intentHistory);
                                    dialog.dismiss();
                                    break;
                                case DialogHelper.MOVE_UP:
                                    ActivityHelper.moveUp(activity, R.id.content_workouts, workoutIndex);
                                    dialog.dismiss();
                                    break;
                                case DialogHelper.MOVE_DOWN:
                                    ActivityHelper.moveDown(activity, R.id.content_workouts, workoutIndex);
                                    dialog.dismiss();
                                    break;
                                case DialogHelper.COPY:
                                    ActivityHelper.copy(activity, R.id.content_workouts, workoutIndex);
                                    dialog.dismiss();
                                    break;
                                case DialogHelper.RENAME:
                                    DialogHelper.createEditDialog(context, buttonWorkout, workoutIndex, DialogHelper.RENAME, DialogHelper.WORKOUT_NAME, "", "", SharedPreferencesHelper.NAME, InputType.TYPE_CLASS_TEXT, Constants.MIN, Constants.MAX);
                                    dialog.dismiss();
                                    break;
                                case DialogHelper.REMOVE:
                                    DialogHelper.createRemoveDialog(activity, R.id.content_workouts, workoutIndex, "", "", SharedPreferencesHelper.NAME);
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

            final LinearLayoutCompat linearLayout = new LinearLayoutCompat(context);
            linearLayout.setOrientation(LinearLayoutCompat.HORIZONTAL);
            linearLayout.addView(buttonStart);
            linearLayout.addView(buttonWorkout);

            ((ViewGroup) findViewById(R.id.content_workouts)).addView(linearLayout);
        }
    }

    @Override
    public void startActivity(Intent intent) {
        if(intent.getComponent().getClassName().equals(ProgramsActivity.class.getName())) {
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
