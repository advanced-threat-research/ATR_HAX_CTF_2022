package com.f32.fit32.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import com.f32.fit32.constants.Constants;
import com.f32.fit32.defaults.HowToDefaults;
import com.f32.fit32.defaults.HiitDefaults;
import com.f32.fit32.defaults.MeasurementsDefaults;
import com.f32.fit32.defaults.ProgramsDefaults;
import com.f32.fit32.defaults.SettingsDefaults;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class SharedPreferencesHelper {
    private final static String PACKAGE = SharedPreferencesHelper.class.getName();

    public final static String PROGRAMS = "Programs";
    public final static String WORKOUTS = "Workouts";
    public final static String EXERCISES = "Exercises";
    public final static String SETS = "Sets";
    public final static String NAME = "Name";
    public final static String REPS = "Reps";
    public final static String REST = "Rest";
    public final static String MIN_WEIGHT = "Min Weight";
    public final static String MAX_WEIGHT = "Max Weight";
    public final static String CURRENT_VOLUME = "Current Volume";
    public final static String SUCCESS_VOLUME = "Success Volume";
    public final static String INCREMENT = "Increment";
    public final static String WARMUP_SETS = "Warmup Sets";
    public final static String EXERCISE_TYPE = "Exercise Type";
    public final static String WEIGHT = "Weight";
    public final static String NOTES = "Notes";
    private final static String SEPARATOR = ".";
    private final static String SWAP = "Swap";

    public final static String HISTORY = "History";

    public final static String MEASUREMENTS = "Measurements";
    private final static String ENTRIES = "Entries";
    public final static String DATE = "Date";
    public final static String ENTRY = "Entry";

    public final static String HIIT_TIMER = "HIIT Timer";
    public final static String HIIT_GO = "HIIT Go";
    public final static String HIIT_REST = "HIIT Rest";
    public final static String HIIT_ROUNDS = "HIIT Rounds";

    public final static String HOW_TO_HOME = "How To Home";
    public final static String HOW_TO_WORKOUTS = "How To Workouts";
    public final static String HOW_TO_WORKOUT = "How To Workout";

    //public final static String INSTAGRAM = "Instagram";
    //public final static String MY_FITNESS_PAL = "MyFitnessPal";

    private final static String BACKUP_FOLDER = "AllFormFitness";
    public final static String BACKUP_FILE = "AllFormFitness.bak";
    public final static String BACKUP = "Data Backup";
    public final static String RESTORE = "Data Restore";

    public final static String SETTINGS = "Settings";
    public final static String SETTING = "Setting";
    public final static String SETTING_HINT = "Setting Hint";
    public final static String SETTING_TYPE = "Setting Type";
    public final static String SETTING_MIN = "Setting Min";
    public final static String SETTING_MAX = "Setting Max";
    public final static String SETTING_OPTIONS = "Setting Options";

    public final static String HELP = "Help";

    public final static SharedPreferencesHelper instance = new SharedPreferencesHelper();

    private Context context;
    private SharedPreferences.Editor editor;
    private Map<String, String> localPreferences;


    public void init(Context c) {
        context = c;
        initLocalPreferences();

        ProgramsDefaults.create();
        MeasurementsDefaults.create();
        HiitDefaults.create();
        HowToDefaults.create();
        SettingsDefaults.create();

        /* Temporary code for adding exercise type on upgrade. Can be removed later. */
        boolean needCommit = false;
        String exercisesSearchString = SEPARATOR + EXERCISES + SEPARATOR;
        String historySearchString = SEPARATOR + HISTORY + SEPARATOR;
        Map<String, String> tempPreferences = new HashMap<>(localPreferences);
        for(Map.Entry<String, String> entry : tempPreferences.entrySet()) {
            if(entry.getKey().contains(exercisesSearchString)) {
                String exercisesIndex = entry.getKey().substring(0, entry.getKey().lastIndexOf(exercisesSearchString) + exercisesSearchString.length() + 1);
                if(!localPreferences.containsKey(buildPreferenceString(exercisesIndex, EXERCISE_TYPE))) {
                    LogHelper.debug("********* Creating exercise type for " + exercisesIndex);
                    localPreferences.put(buildPreferenceString(exercisesIndex, EXERCISE_TYPE), DialogHelper.FREE_WEIGHT);
                    needCommit = true;
                }
                if(!localPreferences.containsKey(buildPreferenceString(exercisesIndex, SUCCESS_VOLUME))) {
                    LogHelper.debug("********* Creating success volume for " + exercisesIndex);
                    String failedVolume = localPreferences.get(buildPreferenceString(exercisesIndex, "Failed Volume"));
                    failedVolume = failedVolume == null ? "0" : failedVolume;
                    localPreferences.put(buildPreferenceString(exercisesIndex, SUCCESS_VOLUME), failedVolume);
                    needCommit = true;
                }
            } else if(entry.getKey().contains(historySearchString)) {
                String historyIndex = entry.getKey().substring(0, entry.getKey().lastIndexOf(historySearchString) + historySearchString.length() + 1);
                if(!localPreferences.containsKey(buildPreferenceString(historyIndex, NOTES))) {
                    LogHelper.debug("********** Creating notes for " + historyIndex);
                    localPreferences.put(buildPreferenceString(historyIndex, NOTES), "");
                    needCommit = true;
                }
            }
        }
        Map<String,Object> settingsMap = SettingsDefaults.getSettingsDefaults();
        List settingsList = (List)settingsMap.get(SETTINGS);
        if(!localPreferences.containsKey(buildPreferenceString(SETTINGS, SettingsDefaults.TEMPO_INTERVAL, NAME))) {
            LogHelper.debug("********** Creating tempo interval setting");
            Map<String, Object> tempoIntervalMap = (Map)settingsList.get(Integer.valueOf(SettingsDefaults.TEMPO_INTERVAL));
            localPreferences.put(buildPreferenceString(SETTINGS, SettingsDefaults.TEMPO_INTERVAL, NAME), (String)tempoIntervalMap.get(NAME));
            localPreferences.put(buildPreferenceString(SETTINGS, SettingsDefaults.TEMPO_INTERVAL, SETTING), (String)tempoIntervalMap.get(SETTING));
            localPreferences.put(buildPreferenceString(SETTINGS, SettingsDefaults.TEMPO_INTERVAL, SETTING_HINT), (String)tempoIntervalMap.get(SETTING_HINT));
            localPreferences.put(buildPreferenceString(SETTINGS, SettingsDefaults.TEMPO_INTERVAL, SETTING_TYPE), (String)tempoIntervalMap.get(SETTING_TYPE));
            localPreferences.put(buildPreferenceString(SETTINGS, SettingsDefaults.TEMPO_INTERVAL, SETTING_MIN), (String)tempoIntervalMap.get(SETTING_MIN));
            localPreferences.put(buildPreferenceString(SETTINGS, SettingsDefaults.TEMPO_INTERVAL, SETTING_MAX), (String)tempoIntervalMap.get(SETTING_MAX));
            localPreferences.put(buildPreferenceString(SETTINGS, SettingsDefaults.TEMPO_INTERVAL, SETTING_OPTIONS), (String)tempoIntervalMap.get(SETTING_OPTIONS));
            needCommit = true;
        }
        if(!localPreferences.containsKey(buildPreferenceString(SETTINGS, SettingsDefaults.KEEP_SCREEN_ON, NAME))) {
            LogHelper.debug("********** Creating keep screen on setting");
            Map<String, Object> keepScreenOnMap = (Map)settingsList.get(Integer.valueOf(SettingsDefaults.KEEP_SCREEN_ON));
            localPreferences.put(buildPreferenceString(SETTINGS, SettingsDefaults.KEEP_SCREEN_ON, NAME), (String)keepScreenOnMap.get(NAME));
            localPreferences.put(buildPreferenceString(SETTINGS, SettingsDefaults.KEEP_SCREEN_ON, SETTING), (String)keepScreenOnMap.get(SETTING));
            localPreferences.put(buildPreferenceString(SETTINGS, SettingsDefaults.KEEP_SCREEN_ON, SETTING_HINT), (String)keepScreenOnMap.get(SETTING_HINT));
            localPreferences.put(buildPreferenceString(SETTINGS, SettingsDefaults.KEEP_SCREEN_ON, SETTING_TYPE), (String)keepScreenOnMap.get(SETTING_TYPE));
            localPreferences.put(buildPreferenceString(SETTINGS, SettingsDefaults.KEEP_SCREEN_ON, SETTING_MIN), (String)keepScreenOnMap.get(SETTING_MIN));
            localPreferences.put(buildPreferenceString(SETTINGS, SettingsDefaults.KEEP_SCREEN_ON, SETTING_MAX), (String)keepScreenOnMap.get(SETTING_MAX));
            localPreferences.put(buildPreferenceString(SETTINGS, SettingsDefaults.KEEP_SCREEN_ON, SETTING_OPTIONS), (String)keepScreenOnMap.get(SETTING_OPTIONS));
            needCommit = true;
        }
        if(!localPreferences.containsKey(buildPreferenceString(SETTINGS, SettingsDefaults.UNIT, SETTING_OPTIONS))) {
            Map<String, Object> unitMap = (Map)settingsList.get(Integer.valueOf(SettingsDefaults.UNIT));
            localPreferences.put(buildPreferenceString(SETTINGS, SettingsDefaults.UNIT, SETTING_OPTIONS), (String)unitMap.get(SETTING_OPTIONS));
        }
        if(!localPreferences.containsKey(buildPreferenceString(SETTINGS, SettingsDefaults.DECREMENT, SETTING_OPTIONS))) {
            Map<String, Object> decrementMap = (Map)settingsList.get(Integer.valueOf(SettingsDefaults.DECREMENT));
            localPreferences.put(buildPreferenceString(SETTINGS, SettingsDefaults.DECREMENT, SETTING_OPTIONS), (String)decrementMap.get(SETTING_OPTIONS));
        }
        if(!localPreferences.containsKey(buildPreferenceString(SETTINGS, SettingsDefaults.INCREMENT, SETTING_OPTIONS))) {
            Map<String, Object> incrementMap = (Map)settingsList.get(Integer.valueOf(SettingsDefaults.INCREMENT));
            localPreferences.put(buildPreferenceString(SETTINGS, SettingsDefaults.INCREMENT, SETTING_OPTIONS), (String)incrementMap.get(SETTING_OPTIONS));
        }
        if(!localPreferences.containsKey(buildPreferenceString(SETTINGS, SettingsDefaults.WARMUP, SETTING_OPTIONS))) {
            Map<String, Object> warmupMap = (Map)settingsList.get(Integer.valueOf(SettingsDefaults.WARMUP));
            localPreferences.put(buildPreferenceString(SETTINGS, SettingsDefaults.WARMUP, SETTING_OPTIONS), (String)warmupMap.get(SETTING_OPTIONS));
        }
        if(!localPreferences.containsKey(buildPreferenceString(SETTINGS, SettingsDefaults.TEMPO_INTERVAL, SETTING_OPTIONS))) {
            Map<String, Object> tempoIntervalMap = (Map)settingsList.get(Integer.valueOf(SettingsDefaults.TEMPO_INTERVAL));
            localPreferences.put(buildPreferenceString(SETTINGS, SettingsDefaults.TEMPO_INTERVAL, SETTING_OPTIONS), (String)tempoIntervalMap.get(SETTING_OPTIONS));
        }
        if(!localPreferences.containsKey(buildPreferenceString(SETTINGS, SettingsDefaults.KEEP_SCREEN_ON, SETTING_OPTIONS))) {
            Map<String, Object> keepScreenOnMap = (Map)settingsList.get(Integer.valueOf(SettingsDefaults.KEEP_SCREEN_ON));
            localPreferences.put(buildPreferenceString(SETTINGS, SettingsDefaults.KEEP_SCREEN_ON, SETTING_OPTIONS), (String)keepScreenOnMap.get(SETTING_OPTIONS));
        }
        if(needCommit) {
            commit();
        }
        /**/
    }

    private void initLocalPreferences() {
        if(localPreferences == null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(PACKAGE, Context.MODE_PRIVATE);
            editor = sharedPreferences.edit();
            localPreferences = (Map<String, String>) sharedPreferences.getAll();

            if(!editor.commit()) {
                LogHelper.error("Failed to commit shared preferences.");
            }

            if(localPreferences == null) {
                LogHelper.error("Unable to load preferences. Please restart the app.");
            }
        }
    }

    public Context getContext() {
        return context;
    }

    public Map<String,String> getLocalPreferences() {
        return localPreferences;
    }

    public boolean commit() {
        LogHelper.debug("Committing localPreferences changes to sharedPreferences");

        editor.clear();
        boolean success = editor.commit();

        for(Map.Entry<String, String> entry : localPreferences.entrySet()) {
            editor.putString(entry.getKey(), entry.getValue());
        }
        success = (success && editor.commit());

        return success;
    }

    public List<String> getPrograms() {
        return getList("", PROGRAMS, NAME);
    }

    public boolean addProgram(String name) {
        List<String> programs = getPrograms();

        return setPreference(buildPreferenceString(PROGRAMS, String.valueOf(programs.size())), NAME, name, Constants.MIN, Constants.MAX);
    }

    public List<String> getWorkouts(String program) {
        return getList(program, WORKOUTS, NAME);
    }

    public boolean addWorkout(String program, String name) {
        List<String> workouts = getWorkouts(program);

        return setPreference(buildPreferenceString(program, WORKOUTS, String.valueOf(workouts.size())), NAME, name, Constants.MIN, Constants.MAX);
    }

    public boolean finishWorkout(String exercise, boolean success) {
        boolean retVal = true;

        if(success) {
            int volume = getVolume(exercise);
            int successVolume = Integer.valueOf(getPreference(exercise, SUCCESS_VOLUME));

            if(volume > successVolume) {
                retVal = setPreference(exercise, SUCCESS_VOLUME, String.valueOf(volume), Constants.SUCCESS_VOLUME_MIN, Constants.SUCCESS_VOLUME_MAX, false);
            }
            retVal = (retVal
                    && increaseVolumeOnePercent(exercise));
        } else {
            retVal = decreaseVolumeTenPercent(exercise);
        }

        retVal = (retVal
                && setPreference(exercise, CURRENT_VOLUME, String.valueOf(getVolume(exercise)), Constants.CURRENT_VOLUME_MIN, Constants.CURRENT_VOLUME_MAX, false));

        return retVal;
    }

    private boolean increaseVolumeOnePercent(String exercise) {
        boolean success = true;

        double incrementPercent = Double.valueOf(getPreference(buildPreferenceString(SETTINGS, SettingsDefaults.INCREMENT, SETTING)));
        incrementPercent = 1 + (incrementPercent / 100);

        double targetVolume = getVolume(exercise) * incrementPercent;

        while(getVolume(exercise) < targetVolume) {
            success = (success && increaseVolume(exercise));
        }

        return success;
    }

    private boolean decreaseVolumeTenPercent(String exercise) {
        boolean success = true;

        double decrementPercent = Double.valueOf(getPreference(buildPreferenceString(SETTINGS, SettingsDefaults.DECREMENT, SETTING)));
        decrementPercent = 1 - (decrementPercent / 100);

        double targetVolume = getVolume(exercise) * decrementPercent;

        while(getVolume(exercise) > targetVolume && getVolume(exercise) > getVolumeMin(exercise)) {
            success = (success && decreaseVolume(exercise));
        }

        return success;
    }

    private boolean increaseVolume(String exercise) {
        boolean success = true;

        String exerciseType = getPreference(exercise, EXERCISE_TYPE);

        if(exerciseType.equals(DialogHelper.FREE_WEIGHT)) {
            int minWeight = Integer.valueOf(getPreference(exercise, MIN_WEIGHT));
            int maxWeight = Integer.valueOf(getPreference(exercise, MAX_WEIGHT));
            int increment = Integer.valueOf(getPreference(exercise, INCREMENT));
            int reps = Integer.valueOf(getPreference(exercise, REPS));

            List<String> sets = getSets(exercise);

            int lastSetWeight = Integer.valueOf(getPreference(sets.get(sets.size() - 1), WEIGHT));
            if (lastSetWeight < maxWeight && increment > 0) {
                for (int i = sets.size() - 1; i >= 0; i--) {
                    int setWeight = Integer.valueOf(getPreference(sets.get(i), WEIGHT));
                    if (i == 0 || setWeight < Integer.valueOf(getPreference(sets.get(i - 1), WEIGHT))) {
                        setWeight = setWeight + increment;
                        if (setWeight > maxWeight) {
                            setWeight = maxWeight;
                        }
                        success = setPreference(sets.get(i), WEIGHT, String.valueOf(setWeight), minWeight, maxWeight, false);
                        break;
                    }
                }
            } else {
                success = setPreference(exercise, REPS, String.valueOf(reps + 1), Constants.NUMBER_OF_REPS_MIN, Constants.NUMBER_OF_REPS_MAX, false);

                if (minWeight < maxWeight) {
                    decreaseVolumeTenPercent(exercise);
                }
            }
        } else {
            int minReps = Constants.NUMBER_OF_REPS_MIN;
            int maxReps = Constants.NUMBER_OF_REPS_MAX;
            int increment = 1;

            List<String> sets = getSets(exercise);

            int lastSetReps = Integer.valueOf(getPreference(sets.get(sets.size() - 1), REPS));
            if (lastSetReps < maxReps) {
                for (int i = sets.size() - 1; i >= 0; i--) {
                    int setReps = Integer.valueOf(getPreference(sets.get(i), REPS));
                    if (i == 0 || setReps < Integer.valueOf(getPreference(sets.get(i - 1), REPS))) {
                        setReps = setReps + increment;
                        if (setReps > maxReps) {
                            setReps = maxReps;
                        }
                        success = setPreference(sets.get(i), REPS, String.valueOf(setReps), minReps, maxReps, false);
                        break;
                    }
                }
            }
        }

        return success;
    }

    private boolean decreaseVolume(String exercise) {
        boolean success = true;

        String exerciseType = getPreference(exercise, EXERCISE_TYPE);

        if(exerciseType.equals(DialogHelper.FREE_WEIGHT)) {
            int minWeight = Integer.valueOf(getPreference(exercise, MIN_WEIGHT));
            int maxWeight = Integer.valueOf(getPreference(exercise, MAX_WEIGHT));
            int increment = Integer.valueOf(getPreference(exercise, INCREMENT));
            int reps = Integer.valueOf(getPreference(exercise, REPS));

            List<String> sets = getSets(exercise);

            int firstSetWeight = Integer.valueOf(getPreference(sets.get(0), WEIGHT));
            if (firstSetWeight > minWeight && increment > 0) {
                for (int i = 0; i < sets.size(); i++) {
                    int setWeight = Integer.valueOf(getPreference(sets.get(i), WEIGHT));
                    if (i == (sets.size() - 1) || setWeight > Integer.valueOf(getPreference(sets.get(i + 1), WEIGHT))) {
                        setWeight = setWeight - increment;
                        if (setWeight < minWeight) {
                            setWeight = minWeight;
                        }
                        success = setPreference(sets.get(i), WEIGHT, String.valueOf(setWeight), minWeight, maxWeight, false);
                        break;
                    }
                }
            } else {
                if (reps > 1) {
                    success = setPreference(exercise, REPS, String.valueOf(reps - 1), Constants.NUMBER_OF_REPS_MIN, Constants.NUMBER_OF_REPS_MAX, false);
                }
            }
        } else {
            int minReps = Constants.NUMBER_OF_REPS_MIN;
            int maxReps = Constants.NUMBER_OF_REPS_MAX;
            int increment = 1;

            List<String> sets = getSets(exercise);

            int firstSetReps = Integer.valueOf(getPreference(sets.get(0), REPS));
            if (firstSetReps > minReps) {
                for (int i = 0; i < sets.size(); i++) {
                    int setReps = Integer.valueOf(getPreference(sets.get(i), REPS));
                    if (i == (sets.size() - 1) || setReps > Integer.valueOf(getPreference(sets.get(i + 1), REPS))) {
                        setReps = setReps - increment;
                        if (setReps < minReps) {
                            setReps = minReps;
                        }
                        success = setPreference(sets.get(i), REPS, String.valueOf(setReps), minReps, maxReps, false);
                        break;
                    }
                }
            }
        }

        return success;
    }

    public int getVolume(String exercise) {
        String exerciseType = getPreference(exercise, EXERCISE_TYPE);

        int volume = 0;

        List<String> sets = getSets(exercise);

        if(exerciseType.equals(DialogHelper.FREE_WEIGHT)) {
            int reps = Integer.valueOf(getPreference(exercise, REPS));

            for (int i = 0; i < sets.size(); i++) {
                int setWeight = Integer.valueOf(getPreference(sets.get(i), WEIGHT));

                if (setWeight <= 0) {
                    setWeight = 1;
                }

                volume += setWeight * reps;
            }
        } else {
            for (int i = 0; i < sets.size(); i++) {
                volume += Integer.valueOf(getPreference(sets.get(i), REPS));
            }
        }

        return volume;
    }

    private int getVolumeMin(String exercise) {
        String exerciseType = getPreference(exercise, EXERCISE_TYPE);

        int minWeight = 1;

        if(exerciseType.equals(DialogHelper.FREE_WEIGHT)) {
            minWeight = Integer.valueOf(getPreference(exercise, MIN_WEIGHT));

            if (minWeight <= 0) {
                minWeight = 1;
            }
        }

        List<String> sets = getSets(exercise);

        int volumeMin = minWeight * sets.size();

        if (volumeMin <= 0) {
            volumeMin = 1;
        }

        return volumeMin;
    }

    public List<String> getExercises(String workout) {
        return getList(workout, EXERCISES, NAME);
    }

    public boolean addExercise(String workout, String name, String increment, String warmupSets, String reps, String rest, String minWeight, String maxWeight, String exerciseType) {
        List<String> exercises = getExercises(workout);

        if(name.trim().length() == 0
                || increment.trim().length() == 0
                || warmupSets.trim().length() == 0
                || reps.trim().length() == 0
                || rest.trim().length() == 0
                || minWeight.trim().length() == 0
                || maxWeight.trim().length() == 0
                || exerciseType.trim().length() == 0) {
            LogHelper.error("All preference must contain a value");
            return false;
        }

        String outOfRangeField = "";
        int outOfRangeMin = 0;
        int outOfRangeMax = 0;

        try {
            int incrementValue = Integer.valueOf(increment);
            int warmupSetsValue = Integer.valueOf(warmupSets);
            int repsValue = Integer.valueOf(reps);
            int restValue = Integer.valueOf(rest);
            int minWeightValue = Integer.valueOf(minWeight);
            int maxWeightValue = Integer.valueOf(maxWeight);

            if(incrementValue < Constants.WEIGHT_INCREMENT_MIN || incrementValue > Constants.WEIGHT_INCREMENT_MAX) {
                outOfRangeField = DialogHelper.WEIGHT_INCREMENT;
                outOfRangeMin = Constants.WEIGHT_INCREMENT_MIN;
                outOfRangeMax = Constants.WEIGHT_INCREMENT_MAX;
            } else if(warmupSetsValue < Constants.SETS_WARMUP_MIN || warmupSetsValue > Constants.SETS_WARMUP_MAX) {
                outOfRangeField = DialogHelper.SETS_WARMUP;
                outOfRangeMin = Constants.SETS_WARMUP_MIN;
                outOfRangeMax = Constants.SETS_WARMUP_MAX;
            } else if(repsValue < Constants.NUMBER_OF_REPS_MIN || repsValue > Constants.NUMBER_OF_REPS_MAX) {
                outOfRangeField = DialogHelper.NUMBER_OF_REPS;
                outOfRangeMin = Constants.NUMBER_OF_REPS_MIN;
                outOfRangeMax = Constants.NUMBER_OF_REPS_MAX;
            } else if(restValue < Constants.REST_IN_SECONDS_MIN || restValue > Constants.REST_IN_SECONDS_MAX) {
                outOfRangeField = DialogHelper.REST_IN_SECONDS;
                outOfRangeMin = Constants.REST_IN_SECONDS_MIN;
                outOfRangeMax = Constants.REST_IN_SECONDS_MAX;
            } else if(minWeightValue < Constants.MIN_WEIGHT_MIN || minWeightValue > Constants.MIN_WEIGHT_MAX) {
                outOfRangeField = DialogHelper.MIN_WEIGHT;
                outOfRangeMin = Constants.MIN_WEIGHT_MIN;
                outOfRangeMax = Constants.MIN_WEIGHT_MAX;
            } else if(maxWeightValue < Constants.MAX_WEIGHT_MIN || maxWeightValue > Constants.MAX_WEIGHT_MAX) {
                outOfRangeField = DialogHelper.MAX_WEIGHT;
                outOfRangeMin = Constants.MAX_WEIGHT_MIN;
                outOfRangeMax = Constants.MAX_WEIGHT_MAX;
            }

            if(outOfRangeField.length() > 0) {
                LogHelper.error("Value is out of range for " + outOfRangeField + ". Min = " + outOfRangeMin + ", Max = " + outOfRangeMax);
                return false;
            }
        } catch(NumberFormatException ignored) { }

        return (setPreference(buildPreferenceString(workout, EXERCISES, String.valueOf(exercises.size())), NAME, name, Constants.MIN, Constants.MAX, false)
                && setPreference(buildPreferenceString(workout, EXERCISES, String.valueOf(exercises.size())), INCREMENT, increment, Constants.WEIGHT_INCREMENT_MIN, Constants.WEIGHT_INCREMENT_MAX, false)
                && setPreference(buildPreferenceString(workout, EXERCISES, String.valueOf(exercises.size())), WARMUP_SETS, warmupSets, Constants.SETS_WARMUP_MIN, Constants.SETS_WARMUP_MAX, false)
                && setPreference(buildPreferenceString(workout, EXERCISES, String.valueOf(exercises.size())), REPS, reps, Constants.NUMBER_OF_REPS_MIN, Constants.NUMBER_OF_REPS_MAX, false)
                && setPreference(buildPreferenceString(workout, EXERCISES, String.valueOf(exercises.size())), REST, rest, Constants.REST_IN_SECONDS_MIN, Constants.REST_IN_SECONDS_MAX, false)
                && setPreference(buildPreferenceString(workout, EXERCISES, String.valueOf(exercises.size())), MIN_WEIGHT, minWeight, Constants.MIN_WEIGHT_MIN, Constants.MIN_WEIGHT_MAX, false)
                && setPreference(buildPreferenceString(workout, EXERCISES, String.valueOf(exercises.size())), MAX_WEIGHT, maxWeight, Constants.MAX_WEIGHT_MIN, Constants.MAX_WEIGHT_MAX, false)
                && setPreference(buildPreferenceString(workout, EXERCISES, String.valueOf(exercises.size())), CURRENT_VOLUME, "0", Constants.CURRENT_VOLUME_MIN, Constants.CURRENT_VOLUME_MAX, false)
                && setPreference(buildPreferenceString(workout, EXERCISES, String.valueOf(exercises.size())), SUCCESS_VOLUME, "0", Constants.SUCCESS_VOLUME_MIN, Constants.SUCCESS_VOLUME_MAX, false)
                && setPreference(buildPreferenceString(workout, EXERCISES, String.valueOf(exercises.size())), EXERCISE_TYPE, exerciseType, Constants.MIN, Constants.MAX, false)
                && commit());
    }

    public List<String> getSets(String exercise) {
        String exerciseType = getPreference(exercise, EXERCISE_TYPE);

        if(exerciseType.equals(DialogHelper.FREE_WEIGHT)) {
            return getList(exercise, SETS, WEIGHT);
        } else {
            return getList(exercise, SETS, REPS);
        }
    }

    public boolean addSet(String exercise, String value, int min, int max) {
        List<String> sets = getSets(exercise);
        String exerciseType = getPreference(exercise, EXERCISE_TYPE);

        if(exerciseType.equals(DialogHelper.FREE_WEIGHT)) {
            return setPreference(buildPreferenceString(exercise, SETS, String.valueOf(sets.size())), WEIGHT, value, min, max);
        } else {
            return setPreference(buildPreferenceString(exercise, SETS, String.valueOf(sets.size())), REPS, value, min, max);
        }
    }

    public List<String> getHistory(String workout) {
        return getList(workout, HISTORY, DATE);
    }

    public boolean addHistory(String workout, Map<String, String> historyMap) {
        List<String> history = getHistory(workout);

        boolean success = true;

        for(Map.Entry<String, String> entry : historyMap.entrySet()) {
            success = (success &&
                       setPreference(buildPreferenceString(workout, HISTORY, String.valueOf(history.size()), entry.getKey()), entry.getValue(), Constants.MIN, Constants.MAX, false));
        }

        success = (success && commit());

        return success;
    }

    public List<String> getMeasurements() {
        return getList("", MEASUREMENTS, NAME);
    }

    public boolean addMeasurement(String name) {
        List<String> measurements = getMeasurements();

        return setPreference(buildPreferenceString(MEASUREMENTS, String.valueOf(measurements.size())), NAME, name, Constants.MIN, Constants.MAX);
    }

    public List<String> getEntries(String measurement) {
        return getList(measurement, ENTRIES, DATE);
    }

    public boolean addEntry(String measurement, String date, String entry) {
        List<String> entries = getEntries(measurement);

        try {
            Date dateObj = Constants.DATE_FORMAT.parse(date);
            if(!date.equals(Constants.DATE_FORMAT.format(dateObj))) {
                LogHelper.error("Invalid date format: " + date);
                return false;
            }
        } catch(ParseException e) {
            LogHelper.error("Invalid date format: " + date + ". " + e.toString());
            return false;
        }

        if(date.trim().length() == 0 || entry.trim().length() == 0) {
            LogHelper.error("All preference must contain a value");
            return false;
        }

        return (setPreference(buildPreferenceString(measurement, ENTRIES, String.valueOf(entries.size())), DATE, date, Constants.MIN, Constants.MAX)
                && setPreference(buildPreferenceString(measurement, ENTRIES, String.valueOf(entries.size())), ENTRY, entry, Constants.MIN, Constants.MAX));
    }

    public List<String> getSettings() {
        return getList("", SETTINGS, NAME);
    }

    private List<String> getList(String parent, String child, String preference) {
        List<String> list = new ArrayList<>();

        initLocalPreferences();

        if(isParentIndexValid(parent)) {
            for (int i = 0; ; i++) {
                if (localPreferences.containsKey(buildPreferenceString(parent, child, String.valueOf(i), preference))) {
                    list.add(buildPreferenceString(parent, child, String.valueOf(i)));
                } else {
                    break;
                }
            }
        }

        return list;
    }

    public String getPreference(String parent, String child) {
        String preference = "";

        if(isParentIndexValid(parent)) {
            String preferenceString = buildPreferenceString(parent, child);
            preference = getPreference(preferenceString);
        }

        return preference;
    }

    public String getPreference(String key) {
        String preference = "";

        initLocalPreferences();

        if (localPreferences.containsKey(key)) {
            preference = localPreferences.get(key);
        } else {
            LogHelper.error("Invalid preference " + key);
        }

        return preference;
    }

    public boolean setPreference(String parent, String child, String value, int min, int max) {
        return setPreference(parent, child, value, min, max, true);
    }

    public boolean setPreference(String parent, String child, String value, int min, int max, boolean commit) {
        LogHelper.debug("Setting preference " + parent + ", " + child + ", " + value);

        boolean success = false;

        if (isParentIndexValid(parent)) {
            String preferenceString = buildPreferenceString(parent, child);
            success = setPreference(preferenceString, value, min, max, commit);
        }

        return success;
    }

    public boolean setPreference(String key, String value, int min, int max, boolean commit) {
        boolean success = false;

        if(value.trim().length() > 0 || key.contains(NOTES)) {
            boolean outOfRange = false;

            try {
                int intValue = Integer.valueOf(value);

                if(intValue < min || intValue > max) {
                    outOfRange = true;
                }
            } catch(NumberFormatException ignored) { }

            if(!outOfRange) {
                localPreferences.put(key, value);

                if (commit) {
                    success = commit();

                    if (!success) {
                        LogHelper.error("Commit failed for setting preference " + key);
                    }
                } else {
                    success = true;
                }
            } else {
                LogHelper.error("Value is out of range. Min = " + min + ", Max = " + max);
            }
        }

        return success;
    }

    public boolean removePreferenceTree(String parent) {
        LogHelper.debug("Removing preference tree for " + parent);

        boolean success = true;

        if(parent.length() > 0 && isParentIndexValid(parent)) {
            String parentRoot = parent.substring(0, parent.lastIndexOf(SEPARATOR));
            int parentIndex = Integer.valueOf(parent.substring(parent.lastIndexOf(SEPARATOR) + 1));

            int i;
            for(i = parentIndex; parentExists(buildPreferenceString(parentRoot, String.valueOf(i+1))); i++) {
                success = (success
                        && swap(buildPreferenceString(parentRoot, String.valueOf(i)), buildPreferenceString(parentRoot, String.valueOf(i + 1))));
            }

            for(Map.Entry<String,String> entry : new HashMap<>(localPreferences).entrySet()) {
                if(entry.getKey().contains(buildPreferenceString(parentRoot, String.valueOf(i)))) {
                    localPreferences.remove(entry.getKey());
                }
            }
            success = (success
                    && commit());
        } else {
            LogHelper.error("Failed to remove preference tree for " + parent + " because it is not an index");
            success = false;
        }

        return success;
    }

    public boolean movePreferenceTreeUp(String parent) {
        return movePreferenceTree(parent, -1);
    }

    public boolean movePreferenceTreeDown(String parent) {
        return movePreferenceTree(parent, 1);
    }

    private boolean movePreferenceTree(String parent, int direction) {
        LogHelper.debug("Moving preference tree for " + parent);

        boolean success = true;

        if(parent.length() > 0 && isParentIndexValid(parent)) {
            String parentRoot = parent.substring(0, parent.lastIndexOf(SEPARATOR));
            int parentIndex = Integer.valueOf(parent.substring(parent.lastIndexOf(SEPARATOR) + 1));

            if(parentExists(buildPreferenceString(parentRoot, String.valueOf(parentIndex + direction)))) {
                success = (swap(buildPreferenceString(parentRoot, String.valueOf(parentIndex)), buildPreferenceString(parentRoot, String.valueOf(parentIndex + direction)))
                        && commit());
            }
        } else {
            LogHelper.error("Failed to move preference tree for " + parent + " because it is not an index");
            success = false;
        }

        return success;
    }

    private boolean parentExists(String parent) {
        for(Map.Entry<String,String> entry : localPreferences.entrySet()) {
            if(entry.getKey().contains(parent)) {
                return true;
            }
        }

        return false;
    }

    private boolean swap(String parent1, String parent2) {
        LogHelper.debug("Swapping " + parent1 + " and " + parent2);

        return (move(parent1, buildPreferenceString(parent1, SWAP, "0"))
                && move(parent2, parent1)
                && move(buildPreferenceString(parent1, SWAP, "0"), parent2));
    }

    private boolean move(String fromParent, String toParent) {
        LogHelper.debug("Moving " + fromParent + " to " + toParent);

        boolean success = true;

        if(fromParent.length() > 0 && isParentIndexValid(fromParent) && toParent.length() > 0 && isParentIndexValid(toParent)) {
            for (Map.Entry<String, String> entry : new HashMap<>(localPreferences).entrySet()) {
                if (entry.getKey().contains(fromParent)) {
                    localPreferences.put(entry.getKey().replace(fromParent, toParent), entry.getValue());
                    localPreferences.remove(entry.getKey());
                }
            }
        } else {
            LogHelper.error("Move aborted. Invalid parents " + fromParent + " " + toParent);
            success = false;
        }

        return success;
    }

    public boolean copyPreferenceTree(String parent) {
        LogHelper.debug("Copying preference tree for " + parent);

        boolean success;

        if(parent.length() > 0 && isParentIndexValid(parent)) {
            String parentRoot = parent.substring(0, parent.lastIndexOf(SEPARATOR));
            int parentIndex = Integer.valueOf(parent.substring(parent.lastIndexOf(SEPARATOR) + 1));

            for(int i = parentIndex + 1; ; i++) {
                if (!parentExists(buildPreferenceString(parentRoot, String.valueOf(i)))) {
                    success = (copy(buildPreferenceString(parentRoot, String.valueOf(parentIndex)), buildPreferenceString(parentRoot, String.valueOf(i)))
                            && commit());
                    break;
                }
            }
        } else {
            LogHelper.error("Failed to copy preference tree for " + parent + " because it is not an index");
            success = false;
        }

        return success;
    }

    private boolean copy(String fromParent, String toParent) {
        LogHelper.debug("Copying " + fromParent + " to " + toParent);

        boolean success = true;

        if(fromParent.length() > 0 && isParentIndexValid(fromParent) && toParent.length() > 0 && isParentIndexValid(toParent)) {
            for (Map.Entry<String, String> entry : new HashMap<>(localPreferences).entrySet()) {
                if (entry.getKey().contains(fromParent)) {
                    String tempKey = entry.getKey().replace(fromParent, toParent);
                    success = (success
                            && setPreference(tempKey, entry.getValue(), Constants.MIN, Constants.MAX, false));
                }
            }
        } else {
            LogHelper.error("Copy aborted. Invalid parents " + fromParent + " " + toParent);
            success = false;
        }

        return success;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public boolean isParentIndexValid(String parent) {
        boolean valid = true;

        try {
            if(parent.length() > 0) {
                Integer.valueOf(parent.substring(parent.lastIndexOf(SEPARATOR) + 1));
            }
        } catch(NumberFormatException e) {
            LogHelper.error("Invalid parent index " + parent + ". " + e.toString());
            valid = false;
        }

        return valid;
    }

    public String buildPreferenceString(String ... strings) {
        StringBuilder preferenceString = new StringBuilder("");
        for(String string : strings) {
            if(preferenceString.length() > 0) {
                preferenceString.append(SEPARATOR);
            }
            preferenceString.append(string);
        }
        return preferenceString.toString();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public boolean backup() {
        boolean success = false;

        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File externalStorage = Environment.getExternalStorageDirectory();
            File backupFolder = new File(externalStorage, BACKUP_FOLDER);
            File backupFile = new File(backupFolder, BACKUP_FILE);

            try {
                backupFolder.mkdirs();
                backupFile.createNewFile();

                FileOutputStream oStream = new FileOutputStream(backupFile);

                for (Map.Entry<String, String> entry : localPreferences.entrySet()) {
                    if(entry.getValue() == null) {
                        oStream.write((entry.getKey() + "\n" + "\n").getBytes());
                    } else {
                        oStream.write((entry.getKey() + "\n" + entry.getValue() + "\n").getBytes());
                    }
                }

                oStream.close();

                success = true;
            } catch (IOException e) {
                LogHelper.error(e.toString());
            }
        } else {
            LogHelper.error("SD Card is not mounted.");
        }

        return success;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public boolean restore() {
        boolean success = false;

        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File externalStorage = Environment.getExternalStorageDirectory();
            File backupFolder = new File(externalStorage, BACKUP_FOLDER);
            File backupFile = new File(backupFolder, BACKUP_FILE);

            try {
                FileInputStream iStream = new FileInputStream(backupFile);

                byte[] bytes = new byte[(int) backupFile.length()];
                iStream.read(bytes);
                iStream.close();

                localPreferences.clear();

                boolean savingKey = true;
                StringBuilder key = new StringBuilder("");
                StringBuilder value = new StringBuilder("");
                for (byte b : bytes) {
                    if (b == '\n') {
                        if (savingKey) {
                            savingKey = false;
                        } else {
                            localPreferences.put(key.toString(), value.toString());
                            savingKey = true;
                            key = new StringBuilder("");
                            value = new StringBuilder("");
                        }
                    } else {
                        if (savingKey) {
                            key.append((char)b);
                        } else {
                            value.append((char)b);
                        }
                    }
                }

                success = commit();
            } catch (FileNotFoundException e) {
                LogHelper.toast("The data backup " + BACKUP_FILE + " does not exist. First use " + BACKUP + ". " + e.toString());
            } catch (IOException e) {
                LogHelper.error(e.toString());
            }
        } else {
            LogHelper.error("SD Card is not mounted.");
        }

        return success;
    }

    /*public void printSharedPreferences() {
        LogHelper.debug("********** Printing Shared Preferences **********");
        List<String> sharedPreferencesList = new ArrayList<String>();
        for(Map.Entry<String,String> entry : localPreferences.entrySet()) {
            sharedPreferencesList.add(entry.getKey() + " = " + entry.getValue());
        }
        Collections.sort(sharedPreferencesList);
        for(String sharedPreference : sharedPreferencesList) {
            LogHelper.debug(sharedPreference);
        }
        LogHelper.debug("********** Finished Shared Preferences **********");
    }*/
}
