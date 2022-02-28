package com.f32.fit32.defaults;

import com.f32.fit32.constants.Constants;
import com.f32.fit32.helper.DialogHelper;
import com.f32.fit32.helper.LogHelper;
import com.f32.fit32.helper.SharedPreferencesHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class ProgramsDefaults {

    public static void create() {
        if(SharedPreferencesHelper.instance.getPrograms().size() == 0) {
            LogHelper.debug("Creating Programs defaults.");
            create("", getProgramsDefaults());
            SharedPreferencesHelper.instance.commit();
        } else {
            LogHelper.debug("Programs already exists.");
        }
    }

    private static void create(String parent, Map<String,Object> map) {
        if(SharedPreferencesHelper.instance.isParentIndexValid(parent)) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (entry.getValue() instanceof String) {
                    SharedPreferencesHelper.instance.setPreference(parent, entry.getKey(), (String) entry.getValue(), Constants.MIN, Constants.MAX, false);
                } else {
                    List<Object> list = (List<Object>) entry.getValue();

                    for (int i = 0; i < list.size(); i++) {
                        create(SharedPreferencesHelper.instance.buildPreferenceString(parent, entry.getKey(), String.valueOf(i)), (Map<String, Object>) list.get(i));
                    }
                }
            }
        }
    }

    private static Map<String,Object> getProgramsDefaults() {
        return new HashMap<String,Object>() {{
            put(SharedPreferencesHelper.PROGRAMS, new ArrayList<Object>() {{
                add(new HashMap<String, Object>() {{
                    put(SharedPreferencesHelper.NAME, "Light Free Weight");
                    put(SharedPreferencesHelper.WORKOUTS, new ArrayList<Object>() {{
                        add(getFreeWeightWorkout("Chest",
                                "Bench Press", "45",
                                "Incline Press", "45",
                                "Bench Fly", "10", "100",
                                "Incline Fly", "10", "100",
                                "Tricep Pushdown", "50", "10", "200"));
                        add(getFreeWeightWorkout("Back",
                                "Deadlift", "45",
                                "Bent Over Row", "45",
                                "Lat Pulldown", "50", "200",
                                "Seated Row", "50", "200",
                                "Barbell Curl", "45", "45", "1000"));
                        add(getFreeWeightWorkout("Shoulders",
                                "Overhead Press", "45",
                                "Upright Row", "45",
                                "Lateral Delt Raise", "10", "100",
                                "Bent Over Delt Raise", "10", "100",
                                "Abs", "0", "0", "0"));
                        add(getFreeWeightWorkout("Legs",
                                "Squat", "45",
                                "Straight Leg Deadlift", "45",
                                "Leg Extension", "50", "200",
                                "Leg Curl", "50", "200",
                                "Calf Raise", "45", "45", "1000"));
                    }});
                }});
                add(new HashMap<String, Object>() {{
                    put(SharedPreferencesHelper.NAME, "Moderate Free Weight");
                    put(SharedPreferencesHelper.WORKOUTS, new ArrayList<Object>() {{
                        add(getFreeWeightWorkout("Chest",
                                "Bench Press", "135",
                                "Incline Press", "95",
                                "Bench Fly", "20", "100",
                                "Incline Fly", "20", "100",
                                "Tricep Pushdown", "100", "10", "200"));
                        add(getFreeWeightWorkout("Back",
                                "Deadlift", "225",
                                "Bent Over Row", "135",
                                "Lat Pulldown", "100", "200",
                                "Seated Row", "100", "200",
                                "Barbell Curl", "65", "45", "1000"));
                        add(getFreeWeightWorkout("Shoulders",
                                "Overhead Press", "95",
                                "Upright Row", "65",
                                "Lateral Delt Raise", "20", "100",
                                "Bent Over Delt Raise", "20", "100",
                                "Abs", "0", "0", "0"));
                        add(getFreeWeightWorkout("Legs",
                                "Squat", "185",
                                "Straight Leg Deadlift", "115",
                                "Leg Extension", "100", "200",
                                "Leg Curl", "100", "200",
                                "Calf Raise", "185", "45", "1000"));
                    }});
                }});
                add(new HashMap<String, Object>() {{
                    put(SharedPreferencesHelper.NAME, "Heavy Free Weight");
                    put(SharedPreferencesHelper.WORKOUTS, new ArrayList<Object>() {{
                        add(getFreeWeightWorkout("Chest",
                                "Bench Press", "225",
                                "Incline Press", "135",
                                "Bench Fly", "30", "100",
                                "Incline Fly", "30", "100",
                                "Tricep Pushdown", "150", "10", "200"));
                        add(getFreeWeightWorkout("Back",
                                "Deadlift", "405",
                                "Bent Over Row", "225",
                                "Lat Pulldown", "150", "200",
                                "Seated Row", "150", "200",
                                "Barbell Curl", "95", "45", "1000"));
                        add(getFreeWeightWorkout("Shoulders",
                                "Overhead Press", "135",
                                "Upright Row", "95",
                                "Lateral Delt Raise", "30", "100",
                                "Bent Over Delt Raise", "30", "100",
                                "Abs", "0", "0", "0"));
                        add(getFreeWeightWorkout("Legs",
                                "Squat", "315",
                                "Straight Leg Deadlift", "185",
                                "Leg Extension", "150", "200",
                                "Leg Curl", "150", "200",
                                "Calf Raise", "315", "45", "1000"));
                    }});
                }});
                add(new HashMap<String, Object>() {{
                    put(SharedPreferencesHelper.NAME, "Body Weight");
                    put(SharedPreferencesHelper.WORKOUTS, new ArrayList<Object>() {{
                        add(getBodyWeightWorkout("Upper Body", "Pushup", "Pullup"));
                        add(getBodyWeightWorkout("Lower Body", "Squat", "Lunge"));
                    }});
                }});
            }});
        }};
    }

    private static Map<String,Object> getFreeWeightWorkout(final String workoutName,
                                                           final String exerciseName1, final String weight1,
                                                           final String exerciseName2, final String weight2,
                                                           final String exerciseName3, final String weight3, final String maxWeight3,
                                                           final String exerciseName4, final String weight4, final String maxWeight4,
                                                           final String exerciseName5, final String weight5, final String minWeight5, final String maxWeight5) {
        return new HashMap<String, Object>() {{
            put(SharedPreferencesHelper.NAME, workoutName);
            put(SharedPreferencesHelper.EXERCISES, new ArrayList<Object>() {{
                add(getFreeWeightExercise(exerciseName1, weight1, "45", "1000"));
                add(getFreeWeightExercise(exerciseName2, weight2, "45", "1000"));
                add(getFreeWeightExercise(exerciseName3, weight3, "10", maxWeight3));
                add(getFreeWeightExercise(exerciseName4, weight4, "10", maxWeight4));
                add(getFreeWeightExercise(exerciseName5, weight5, minWeight5, maxWeight5));
            }});
        }};
    }

    private static Map<String,Object> getFreeWeightExercise(final String exerciseName, final String weight, final String minWeight, final String maxWeight) {
        if(minWeight.equals("0") && maxWeight.equals("0")) {
            Map<String, Object> bodyWeightExercise = getBodyWeightExercise(exerciseName);

            List<Object> sets = (List)bodyWeightExercise.get(SharedPreferencesHelper.SETS);

            while(sets.size() > 6) {
                sets.remove(0);
            }

            return bodyWeightExercise;
        }
        return new HashMap<String, Object>() {{
            put(SharedPreferencesHelper.NAME, exerciseName);
            put(SharedPreferencesHelper.MIN_WEIGHT, minWeight);
            put(SharedPreferencesHelper.MAX_WEIGHT, maxWeight);
            put(SharedPreferencesHelper.CURRENT_VOLUME, "0");
            put(SharedPreferencesHelper.SUCCESS_VOLUME, "0");
            put(SharedPreferencesHelper.INCREMENT, "10");
            put(SharedPreferencesHelper.WARMUP_SETS, "3");
            put(SharedPreferencesHelper.REPS, "10");
            put(SharedPreferencesHelper.REST, "60");
            put(SharedPreferencesHelper.EXERCISE_TYPE, DialogHelper.FREE_WEIGHT);

            put(SharedPreferencesHelper.SETS, new ArrayList<Object>() {{
                add(new HashMap<String, Object>() {{
                    put(SharedPreferencesHelper.WEIGHT, weight);
                }});
                add(new HashMap<String, Object>() {{
                    put(SharedPreferencesHelper.WEIGHT, weight);
                }});
                add(new HashMap<String, Object>() {{
                    put(SharedPreferencesHelper.WEIGHT, weight);
                }});
            }});
        }};
    }

    private static Map<String,Object> getBodyWeightWorkout(final String workoutName,
                                                           final String exerciseName1,
                                                           final String exerciseName2) {
        return new HashMap<String, Object>() {{
            put(SharedPreferencesHelper.NAME, workoutName);
            put(SharedPreferencesHelper.EXERCISES, new ArrayList<Object>() {{
                add(getBodyWeightExercise(exerciseName1));
                add(getBodyWeightExercise(exerciseName2));
            }});
        }};
    }

    private static Map<String,Object> getBodyWeightExercise(final String exerciseName) {
        return new HashMap<String, Object>() {{
            put(SharedPreferencesHelper.NAME, exerciseName);
            put(SharedPreferencesHelper.MIN_WEIGHT, "0");
            put(SharedPreferencesHelper.MAX_WEIGHT, "0");
            put(SharedPreferencesHelper.CURRENT_VOLUME, "0");
            put(SharedPreferencesHelper.SUCCESS_VOLUME, "0");
            put(SharedPreferencesHelper.INCREMENT, "0");
            put(SharedPreferencesHelper.WARMUP_SETS, "0");
            put(SharedPreferencesHelper.REPS, "10");
            put(SharedPreferencesHelper.REST, "60");
            put(SharedPreferencesHelper.EXERCISE_TYPE, DialogHelper.BODY_WEIGHT);

            put(SharedPreferencesHelper.SETS, new ArrayList<Object>() {{
                add(new HashMap<String, Object>() {{
                    put(SharedPreferencesHelper.REPS, "10");
                }});
                add(new HashMap<String, Object>() {{
                    put(SharedPreferencesHelper.REPS, "10");
                }});
                add(new HashMap<String, Object>() {{
                    put(SharedPreferencesHelper.REPS, "10");
                }});
                add(new HashMap<String, Object>() {{
                    put(SharedPreferencesHelper.REPS, "10");
                }});
                add(new HashMap<String, Object>() {{
                    put(SharedPreferencesHelper.REPS, "10");
                }});
                add(new HashMap<String, Object>() {{
                    put(SharedPreferencesHelper.REPS, "10");
                }});
                add(new HashMap<String, Object>() {{
                    put(SharedPreferencesHelper.REPS, "10");
                }});
                add(new HashMap<String, Object>() {{
                    put(SharedPreferencesHelper.REPS, "10");
                }});
                add(new HashMap<String, Object>() {{
                    put(SharedPreferencesHelper.REPS, "10");
                }});
                add(new HashMap<String, Object>() {{
                    put(SharedPreferencesHelper.REPS, "10");
                }});
            }});
        }};
    }
}
