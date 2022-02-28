package com.f32.fit32.helper;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.Html;
import android.text.InputType;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.f32.fit32.R;
import com.f32.fit32.interfaces.F32Activity;

public class ActivityHelper {

    public static AppCompatImageButton createImageButton(final Context context) {
        AppCompatImageButton imageButton = new AppCompatImageButton(context);
        imageButton.setImageResource(R.drawable.ic_input_play);
        imageButton.setBackgroundColor(ContextCompat.getColor(context, R.color.colorTransparent));
        imageButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.setBackgroundColor(ContextCompat.getColor(context, R.color.colorLightGray));
                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    v.setBackgroundColor(ContextCompat.getColor(context, R.color.colorTransparent));
                }
                return false;
            }
        });
        imageButton.setPadding(0, 0, 0, 0);

        return imageButton;
    }

    public static AppCompatButton createButton(final Context context, String text, boolean clickable) {
        return createButton(context, text, Typeface.DEFAULT, clickable);
    }

    public static AppCompatButton createButton(final Context context, String text, Typeface typeface, boolean clickable) {
        AppCompatButton button = new AppCompatButton(context);
        button.setText(Html.fromHtml(
                text,
                new Html.ImageGetter() {
                    @Override
                    public Drawable getDrawable(String source) {
                        Drawable drawable = ContextCompat.getDrawable(context, Integer.valueOf(source));
                        drawable.setBounds(0, 0, drawable.getIntrinsicWidth() / 3, drawable.getIntrinsicHeight() / 3);
                        return drawable;
                    }
                },
                null));
        button.setSupportAllCaps(false);
        button.setTypeface(typeface);
        button.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        button.setBackgroundColor(ContextCompat.getColor(context, R.color.colorTransparent));
        if(clickable) {
            button.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        v.setBackgroundColor(ContextCompat.getColor(context, R.color.colorLightGray));
                    } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                        v.setBackgroundColor(ContextCompat.getColor(context, R.color.colorTransparent));
                    }
                    return false;
                }
            });
        }
        button.setPadding(getButton(context).getPaddingLeft(), getButton(context).getPaddingTop(), getButton(context).getPaddingRight(), getButton(context).getPaddingBottom());

        return button;
    }

    public static AppCompatButton getButton(Context context) {
        return new AppCompatButton(context);
    }

    public static AppCompatEditText createEditText(Context context, String text, String hint, int inputType) {
        AppCompatEditText editText = new AppCompatEditText(context);
        editText.setText(text);
        editText.setHint(hint);
        editText.setInputType(inputType);

        return editText;
    }

    public static void createExerciseEditButton(final AppCompatActivity activity, final String index, final String label, final String unit, final String preference, final int min, final int max) {
        final AppCompatButton button = createButton(activity, label + ": " + SharedPreferencesHelper.instance.getPreference(index, preference) + " " + unit, true);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AppCompatEditText editText = DialogHelper.createEditText(activity, SharedPreferencesHelper.instance.getPreference(index, preference), label, InputType.TYPE_CLASS_NUMBER);

                final AlertDialog dialogEdit = DialogHelper.createDialog(activity, label, DialogHelper.SAVE, editText);

                dialogEdit.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (SharedPreferencesHelper.instance.setPreference(index, preference, editText.getText().toString(), min, max)) {
                            button.setText(activity.getString(R.string.label_text_unit, label, editText.getText().toString(), unit));
                            dialogEdit.dismiss();
                        } else {
                            LogHelper.error("Failed to save " + label);
                        }
                    }
                });
            }
        });

        ((ViewGroup)activity.findViewById(R.id.content_exercise)).addView(button);
    }

    public static void moveUp(final AppCompatActivity activity, final int resId, final String index) {
        if (SharedPreferencesHelper.instance.movePreferenceTreeUp(index)) {
            ((ViewGroup)activity.findViewById(resId)).removeAllViews();
            ((F32Activity)activity).init();
        } else {
            LogHelper.error("Failed to move up " + index);
        }
    }

    public static void moveDown(final AppCompatActivity activity, final int resId, final String index) {
        if (SharedPreferencesHelper.instance.movePreferenceTreeDown(index)) {
            ((ViewGroup)activity.findViewById(resId)).removeAllViews();
            ((F32Activity)activity).init();
        } else {
            LogHelper.error("Failed to move down " + index);
        }
    }

    public static void copy(final AppCompatActivity activity, final int resId, final String index) {
        if (SharedPreferencesHelper.instance.copyPreferenceTree(index)) {
            ((ViewGroup)activity.findViewById(resId)).removeAllViews();
            ((F32Activity)activity).init();
        } else {
            LogHelper.error("Failed to copy " + index);
        }
    }

    public static View getSeparatorView(Context context) {
        View separatorView = new View(context);
        separatorView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorLightGray));
        separatorView.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, 1));

        return separatorView;
    }
}
