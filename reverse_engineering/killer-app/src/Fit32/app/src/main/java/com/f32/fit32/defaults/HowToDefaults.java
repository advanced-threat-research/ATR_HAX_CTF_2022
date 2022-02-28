package com.f32.fit32.defaults;

import com.f32.fit32.constants.Constants;
import com.f32.fit32.helper.LogHelper;
import com.f32.fit32.helper.SharedPreferencesHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class HowToDefaults {

    public static void create() {
        if(!SharedPreferencesHelper.instance.getLocalPreferences().containsKey(SharedPreferencesHelper.HOW_TO_HOME) ||
                !SharedPreferencesHelper.instance.getLocalPreferences().containsKey(SharedPreferencesHelper.HOW_TO_WORKOUTS) ||
                !SharedPreferencesHelper.instance.getLocalPreferences().containsKey(SharedPreferencesHelper.HOW_TO_WORKOUT)) {
            LogHelper.debug("Creating How To defaults.");
            create("", getHowToDefaults());
            SharedPreferencesHelper.instance.commit();
        } else {
            LogHelper.debug("How To already exists.");
        }
    }

    private static void create(String parent, Map<String,Object> map) {
        if(SharedPreferencesHelper.instance.isParentIndexValid(parent)) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (entry.getValue() instanceof String) {
                    String preferenceString = SharedPreferencesHelper.instance.buildPreferenceString(parent, entry.getKey());
                    if(!SharedPreferencesHelper.instance.getLocalPreferences().containsKey(preferenceString)) {
                        SharedPreferencesHelper.instance.setPreference(preferenceString, (String) entry.getValue(), Constants.MIN, Constants.MAX, false);
                    }
                } else {
                    List<Object> list = (List<Object>) entry.getValue();

                    for (int i = 0; i < list.size(); i++) {
                        create(SharedPreferencesHelper.instance.buildPreferenceString(parent, entry.getKey(), String.valueOf(i)), (Map<String, Object>) list.get(i));
                    }
                }
            }
        }
    }

    private static Map<String,Object> getHowToDefaults() {
        return new HashMap<String,Object>() {{
            put(SharedPreferencesHelper.HOW_TO_HOME, "true");
            put(SharedPreferencesHelper.HOW_TO_WORKOUTS, "true");
            put(SharedPreferencesHelper.HOW_TO_WORKOUT, "true");
        }};
    }
}
