package com.f32.fit32.constants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Constants {

    public final static DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd", Locale.US);

    public final static int WEIGHT_INCREMENT_MIN = 0;
    public final static int WEIGHT_INCREMENT_MAX = 100;

    public final static int SETS_WARMUP_MIN = 0;
    public final static int SETS_WARMUP_MAX = 10;

    public final static int NUMBER_OF_REPS_MIN = 1;
    public final static int NUMBER_OF_REPS_MAX = 100;

    public final static int REST_IN_SECONDS_MIN = 1;
    public final static int REST_IN_SECONDS_MAX = 600;

    public final static int MIN_WEIGHT_MIN = 0;
    public final static int MIN_WEIGHT_MAX = 1000;

    public final static int MAX_WEIGHT_MIN = 0;
    public final static int MAX_WEIGHT_MAX = 1000;

    public final static int CURRENT_VOLUME_MIN = 0;
    public final static int CURRENT_VOLUME_MAX = 1000000;

    public final static int SUCCESS_VOLUME_MIN = 0;
    public final static int SUCCESS_VOLUME_MAX = 1000000;

    public final static int MIN = -1000000000;
    public final static int MAX = 1000000000;

    public final static int HIIT_GO_MIN = 0;
    public final static int HIIT_GO_MAX = 600;

    public final static int HIIT_REST_MIN = 0;
    public final static int HIIT_REST_MAX = 600;

    public final static int HIIT_ROUNDS_MIN = 1;
    public final static int HIIT_ROUNDS_MAX = 1000;
}
