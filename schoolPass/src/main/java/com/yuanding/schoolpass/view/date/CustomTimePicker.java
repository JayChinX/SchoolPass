package com.yuanding.schoolpass.view.date;

import java.lang.reflect.Field;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import com.yuanding.schoolpass.R;

/**
 * A subclass of {@link android.widget.TimePicker} that uses
 * reflection to allow for customization of the default blue
 * dividers.
 *
 * @author jjobes
 *
 */
public class CustomTimePicker extends TimePicker
{
    private static final String TAG = "CustomTimePicker";

    public CustomTimePicker(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        Class<?> idClass = null;
        Class<?> numberPickerClass = null;
        Field selectionDividerField = null;
        Field hourField = null;
        Field minuteField = null;
        Field secondField = null;
        NumberPicker hourNumberPicker = null;
        NumberPicker minuteNumberPicker = null;
        NumberPicker secondNumberPicker = null;

        try
        {
            // Create an instance of the id class
            idClass = Class.forName("com.android.internal.R$id");

            // Get the fields that store the resource IDs for the hour, minute and amPm NumberPickers
            hourField = idClass.getField("hour");
            minuteField = idClass.getField("minute");
           // secondField = idClass.getField("second");

            // Use the resource IDs to get references to the hour, minute and amPm NumberPickers
            hourNumberPicker = (NumberPicker) findViewById(hourField.getInt(null));
            minuteNumberPicker = (NumberPicker) findViewById(minuteField.getInt(null));
            //secondNumberPicker = (NumberPicker) findViewById(secondField.getInt(null));

            numberPickerClass = Class.forName("android.widget.NumberPicker");

            // Set the value of the mSelectionDivider field in the hour, minute and amPm NumberPickers
            // to refer to our custom drawables
            selectionDividerField = numberPickerClass.getDeclaredField("mSelectionDivider");
            selectionDividerField.setAccessible(true);
            selectionDividerField.set(hourNumberPicker, getResources().getDrawable(R.drawable.selection_divider));
            selectionDividerField.set(minuteNumberPicker, getResources().getDrawable(R.drawable.selection_divider));
           // selectionDividerField.set(secondNumberPicker, getResources().getDrawable(R.drawable.selection_divider));
        }
        catch (ClassNotFoundException e)
        {
            Log.e(TAG, "ClassNotFoundException in CustomTimePicker", e);
        }
        catch (NoSuchFieldException e)
        {
            Log.e(TAG, "NoSuchFieldException in CustomTimePicker", e);
        }
        catch (IllegalAccessException e)
        {
            Log.e(TAG, "IllegalAccessException in CustomTimePicker", e);
        }
        catch (IllegalArgumentException e)
        {
            Log.e(TAG, "IllegalArgumentException in CustomTimePicker", e);
        }
    }
    
    
    //android.view.InflateException: Binary XML file line #3: Error inflating class com.cc.CustomTimePickerc
}
