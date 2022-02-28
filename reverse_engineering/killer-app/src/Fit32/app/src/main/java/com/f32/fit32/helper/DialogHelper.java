package com.f32.fit32.helper;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ScrollView;

import com.f32.fit32.R;
import com.f32.fit32.constants.Constants;
import com.f32.fit32.interfaces.F32Activity;

public class DialogHelper {

    public final static String HISTORY = "History";
    public final static String MOVE_UP = "Move Up";
    public final static String MOVE_DOWN = "Move Down";
    public final static String COPY = "Copy";
    public final static String RENAME = "Rename";
    public final static String REMOVE = "Remove";
    public final static String EXERCISE_TYPE = "Exercise Type";
    public final static String FREE_WEIGHT = "Free Weight";
    public final static String BODY_WEIGHT = "Body Weight";
    public final static String PROGRAM_NAME = "Program Name";
    public final static String WORKOUT_NAME = "Workout Name";
    public final static String EXERCISE_NAME = "Exercise Name";
    //public final static String CURRENT_VOLUME = "Current Volume";
    //public final static String SUCCESS_VOLUME = "Success Volume";
    public final static String WEIGHT_INCREMENT = "Weight Increment";
    public final static String SETS_WARMUP = "Number of Warmup Sets";
    public final static String NUMBER_OF_REPS = "Number of Reps";
    public final static String REST_IN_SECONDS = "Rest in Seconds";
    public final static String MIN_WEIGHT = "Minimum Weight";
    public final static String MAX_WEIGHT = "Maximum Weight";
    public final static String SET_WEIGHT = "New Set Weight";
    public final static String SET_REPS = "New Set Reps";
    public final static String MEASUREMENT_NAME = "Measurement Name";
    public final static String CURRENT = "Current";
    public final static String AVERAGE = "7 Day Average";
    public final static String HIGH = "7 Day High";
    public final static String LOW = "7 Day Low";
    public final static String CREATE = "Create";
    public final static String SAVE = "Save";
    private final static String CANCEL = "Cancel";
    public final static String YES = "Yes";
    public final static String NO = "No";
    public final static String CLOSE = "Close";
    public final static String HOW_TO = "How To";

    public final static String[] MENU = {MOVE_UP, MOVE_DOWN, COPY, REMOVE};
    public final static String[] RENAME_MENU = {MOVE_UP, MOVE_DOWN, COPY, RENAME, REMOVE};
    public final static String[] WORKOUT_MENU = {HISTORY, MOVE_UP, MOVE_DOWN, COPY, RENAME, REMOVE};
    public final static String[] HISTORY_MENU = {MOVE_UP, MOVE_DOWN, REMOVE};
    public final static String[] MEASUREMENT_MENU = {MOVE_UP, MOVE_DOWN, REMOVE};
    public final static String[] EXERCISE_TYPE_MENU = {FREE_WEIGHT, BODY_WEIGHT};

    public static AppCompatEditText createEditText(Context context, String text, String hint, int inputType) {
        AppCompatEditText editText = new AppCompatEditText(context);
        editText.setText(text);
        editText.setHint(hint);
        editText.setInputType(inputType);

        return editText;
    }

    public static AlertDialog createDialog(Context context, String title, String posBtnText, View ... views) {
        LinearLayoutCompat linearLayout = new LinearLayoutCompat(context);
        linearLayout.setOrientation(LinearLayoutCompat.VERTICAL);
        for(View view : views) {
            linearLayout.addView(view);
        }
        ScrollView scrollView = new ScrollView(context);
        scrollView.addView(linearLayout);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setView(scrollView, getPadding(context).getPaddingLeft(), getPadding(context).getPaddingTop(), getPadding(context).getPaddingRight(), getPadding(context).getPaddingBottom())
                .setPositiveButton(posBtnText, null)
                .setNegativeButton(CANCEL, null)
                .create();

        dialog.show();
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));

        return dialog;
    }

    public static AlertDialog createDialog(final Context context, String title, String posBtnText, String negBtnText, String message) {
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(Html.fromHtml(
                        message,
                        new Html.ImageGetter() {
                            @Override
                            public Drawable getDrawable(String source) {
                                Drawable drawable = ContextCompat.getDrawable(context, Integer.valueOf(source));
                                drawable.setBounds(0, 0, drawable.getIntrinsicWidth() / 2, drawable.getIntrinsicHeight() / 2);
                                return drawable;
                            }
                        },
                        null))
                .setPositiveButton(posBtnText, null)
                .setNegativeButton(negBtnText, null)
                .create();

        dialog.show();
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));

        return dialog;
    }

    public static AlertDialog createDialog(Context context, String title, String[] items) {
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setItems(items, null)
                .setNegativeButton(CLOSE, null)
                .create();

        dialog.show();
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));

        return dialog;
    }

    public static void createDialog(final Context context, final AppCompatButton button, final String index, final String title, final String[] items) {
        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setItems(items, null)
                .setNegativeButton(CLOSE, null)
                .create();

        dialog.show();
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
        dialog.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String text = ((AppCompatTextView) view).getText().toString();
                if (SharedPreferencesHelper.instance.setPreference(index, SharedPreferencesHelper.SETTING, text, Constants.MIN, Constants.MAX)) {
                    button.setText(text);
                    dialog.dismiss();
                } else {
                    LogHelper.error("Failed to save " + title);
                }
            }
        });
    }

    public static void createEditDialog(final Context context, final AppCompatButton button, final String index, final String title, final String hint, final String label, final String unit, final String preference, final int inputType, final int min, final int max) {
        final AppCompatEditText editText = createEditText(context, SharedPreferencesHelper.instance.getPreference(index, preference), hint, inputType);

        final AlertDialog dialogEdit = createDialog(context, title, SAVE, editText);

        dialogEdit.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SharedPreferencesHelper.instance.setPreference(index, preference, editText.getText().toString(), min, max)) {
                    button.setText(context.getString(R.string.label_text_label, label, editText.getText().toString(), unit));
                    dialogEdit.dismiss();
                } else {
                    LogHelper.error("Failed to save " + label);
                }
            }
        });
    }

    public static void createMeasurementEditDialog(final Context context, final String index, final String hint, final String label, final int inputType) {
        final AppCompatEditText editText = createEditText(context, SharedPreferencesHelper.instance.getPreference(index, SharedPreferencesHelper.ENTRY), hint, inputType);

        final AlertDialog dialogEdit = createDialog(context, label, SAVE, editText);

        dialogEdit.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SharedPreferencesHelper.instance.setPreference(index, SharedPreferencesHelper.ENTRY, editText.getText().toString(), Constants.MIN, Constants.MAX)) {
                    ((ViewGroup)((AppCompatActivity)context).findViewById(R.id.content_measurement)).removeAllViews();
                    ((F32Activity)context).init();
                    dialogEdit.dismiss();
                } else {
                    LogHelper.error("Failed to save " + hint);
                }
            }
        });
    }

    public static void createRemoveDialog(final AppCompatActivity activity, final int resId, final String index, final String label, final String unit, final String preference) {
        String message = "Are you sure you want to permanently remove " + label + SharedPreferencesHelper.instance.getPreference(index, preference) + " " + unit + "?";

        final AlertDialog dialogRemove = createDialog(activity, REMOVE, YES, NO, message);

        dialogRemove.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SharedPreferencesHelper.instance.removePreferenceTree(index)) {
                    ((ViewGroup)activity.findViewById(resId)).removeAllViews();
                    ((F32Activity)activity).init();
                    dialogRemove.dismiss();
                } else {
                    LogHelper.error("Failed to remove " + SharedPreferencesHelper.instance. buildPreferenceString(index, preference));
                }
            }
        });
    }

    private static View getPadding(Context context) {
        AlertDialog dialog = new AlertDialog.Builder(context)
                .create();

        dialog.show();
        dialog.dismiss();

        return dialog.findViewById(android.R.id.message);
    }
}
