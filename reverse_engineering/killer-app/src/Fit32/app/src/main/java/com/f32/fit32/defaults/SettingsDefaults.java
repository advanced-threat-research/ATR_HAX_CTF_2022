package com.f32.fit32.defaults;

import android.text.InputType;

import com.f32.fit32.constants.Constants;
import com.f32.fit32.helper.LogHelper;
import com.f32.fit32.helper.SharedPreferencesHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class SettingsDefaults {

    public static final String UNIT = "0";
    public static final String DECREMENT = "1";
    public static final String INCREMENT = "2";
    public static final String WARMUP = "3";
    public static final String TEMPO_INTERVAL = "4";
    public static final String KEEP_SCREEN_ON = "5";

    public static void create() {
        if(SharedPreferencesHelper.instance.getSettings().size() == 0) {
            LogHelper.debug("Creating Settings defaults.");
            create("", getSettingsDefaults());
            SharedPreferencesHelper.instance.commit();
        } else {
            LogHelper.debug("Settings already exists.");
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

    public static Map<String,Object> getSettingsDefaults() {
        return new HashMap<String,Object>() {{
            put(SharedPreferencesHelper.SETTINGS, new ArrayList<Object>() {{
                add(new HashMap<String, Object>() {{
                    put(SharedPreferencesHelper.NAME, "Weight Unit");
                    put(SharedPreferencesHelper.SETTING, "Lbs");
                    put(SharedPreferencesHelper.SETTING_HINT, "Weight Unit (Lbs, Kgs)");
                    put(SharedPreferencesHelper.SETTING_TYPE, String.valueOf(InputType.TYPE_CLASS_TEXT));
                    put(SharedPreferencesHelper.SETTING_MIN, "0");
                    put(SharedPreferencesHelper.SETTING_MAX, "0");
                    put(SharedPreferencesHelper.SETTING_OPTIONS, "");
                }});
                add(new HashMap<String, Object>() {{
                    put(SharedPreferencesHelper.NAME, "Weight Decrement %");
                    put(SharedPreferencesHelper.SETTING, "20");
                    put(SharedPreferencesHelper.SETTING_HINT, "Weight Decrement % (Recommended: 10)");
                    put(SharedPreferencesHelper.SETTING_TYPE, String.valueOf(InputType.TYPE_CLASS_NUMBER));
                    put(SharedPreferencesHelper.SETTING_MIN, "1");
                    put(SharedPreferencesHelper.SETTING_MAX, "100");
                    put(SharedPreferencesHelper.SETTING_OPTIONS, "");
                }});
                add(new HashMap<String, Object>() {{
                    put(SharedPreferencesHelper.NAME, "Weight Increment %");
                    put(SharedPreferencesHelper.SETTING, "1");
                    put(SharedPreferencesHelper.SETTING_HINT, "Weight Increment % (Recommended: 1)");
                    put(SharedPreferencesHelper.SETTING_TYPE, String.valueOf(InputType.TYPE_CLASS_NUMBER));
                    put(SharedPreferencesHelper.SETTING_MIN, "1");
                    put(SharedPreferencesHelper.SETTING_MAX, "100");
                    put(SharedPreferencesHelper.SETTING_OPTIONS, "");
                }});
                add(new HashMap<String, Object>() {{
                    put(SharedPreferencesHelper.NAME, "Starting Warmup %");
                    put(SharedPreferencesHelper.SETTING, "25");
                    put(SharedPreferencesHelper.SETTING_HINT, "Starting Warmup % (Recommended: 50)");
                    put(SharedPreferencesHelper.SETTING_TYPE, String.valueOf(InputType.TYPE_CLASS_NUMBER));
                    put(SharedPreferencesHelper.SETTING_MIN, "1");
                    put(SharedPreferencesHelper.SETTING_MAX, "100");
                    put(SharedPreferencesHelper.SETTING_OPTIONS, "");
                }});
                add(new HashMap<String, Object>() {{
                    put(SharedPreferencesHelper.NAME, "Tempo Interval Seconds");
                    put(SharedPreferencesHelper.SETTING, "0");
                    put(SharedPreferencesHelper.SETTING_HINT, "Tempo Interval Seconds (Recommended: 3)");
                    put(SharedPreferencesHelper.SETTING_TYPE, String.valueOf(InputType.TYPE_CLASS_NUMBER));
                    put(SharedPreferencesHelper.SETTING_MIN, "0");
                    put(SharedPreferencesHelper.SETTING_MAX, "10");
                    put(SharedPreferencesHelper.SETTING_OPTIONS, "");
                }});
                add(new HashMap<String, Object>() {{
                    put(SharedPreferencesHelper.NAME, "Keep Screen On");
                    put(SharedPreferencesHelper.SETTING, "Disabled");
                    put(SharedPreferencesHelper.SETTING_HINT, "Keep Screen On (Recommended: Enabled)");
                    put(SharedPreferencesHelper.SETTING_TYPE, String.valueOf(InputType.TYPE_CLASS_TEXT));
                    put(SharedPreferencesHelper.SETTING_MIN, "0");
                    put(SharedPreferencesHelper.SETTING_MAX, "0");
                    put(SharedPreferencesHelper.SETTING_OPTIONS, "Enabled,Disabled");
                }});
            }});
        }};
    }
}
