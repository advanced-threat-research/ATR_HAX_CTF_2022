package com.f32.fit32.defaults;

import com.f32.fit32.constants.Constants;
import com.f32.fit32.helper.LogHelper;
import com.f32.fit32.helper.SharedPreferencesHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class MeasurementsDefaults {
    public static void create() {
        if(SharedPreferencesHelper.instance.getMeasurements().size() == 0) {
            LogHelper.debug("Creating Measurements defaults.");
            create("", getMeasurementsDefaults());
            SharedPreferencesHelper.instance.commit();
        } else {
            LogHelper.debug("Measurements already exists.");
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

    private static Map<String,Object> getMeasurementsDefaults() {
        return new HashMap<String,Object>() {{
            put(SharedPreferencesHelper.MEASUREMENTS, new ArrayList<Object>() {{
                add(new HashMap<String, Object>() {{
                    put(SharedPreferencesHelper.NAME, "Weight (Lbs)");
                }});
                add(new HashMap<String, Object>() {{
                    put(SharedPreferencesHelper.NAME, "Body Fat (%)");
                }});
            }});
        }};
    }
}
