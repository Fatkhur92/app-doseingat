package com.satyajitghosh.mediclock.medicine;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.satyajitghosh.mediclock.R;

/**
 * This activity is used for changing the default times
 *
 * @author SATYAJIT GHOSH
 * @since 1.6.0
 */
public class TimeChangeActivity extends AppCompatActivity {
    String time = "0000";
    private TextView morning_edit_time;
    private TextView lunch_edit_time;
    private TextView night_edit_time;

    /**
     * It is used to convert the integer hour and minute information received from the picker to a string to display properly on textview
     *
     * @param hour   it takes the hour information in Integer like 9 or 11
     * @param minute it takes the minute information in Integer like 3 or 29
     * @return it returns a string made like "08:00" or "10:30"
     */
    public static String timeTextView(int hour, int minute) {
        String result = "";
        if (hour < 10) {
            result = "0" + hour;
        } else {
            result = Integer.toString(hour);
        }
        result += ":";
        if (minute < 10) {
            result += "0" + minute;
        } else {
            result += Integer.toString(minute);
        }

        return result;
    }

    /**
     * It is used to convert the integer hour and minute information received from the picker to a string to send on sharedPreferences
     *
     * @param hour   it takes the hour information in Integer like 9 or 11
     * @param minute it takes the minute information in Integer like 3 or 29
     * @return it returns a string made like "0800" or "1030"
     */
    public static String timeToString(int hour, int minute) {
        String result = "";
        if (hour < 10) {
            result = "0" + hour;
        } else {
            result = Integer.toString(hour);
        }
        if (minute < 10) {
            result += "0" + minute;
        } else {
            result += Integer.toString(minute);
        }

        return result;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_change);
        morning_edit_time = findViewById(R.id.morning_edit_time);
        lunch_edit_time = findViewById(R.id.afternoon_edit_time);
        night_edit_time = findViewById(R.id.night_edit_time);

        morning_edit_time.setText(TIMEClassToTextView(TIME.MORNING));
        lunch_edit_time.setText(TIMEClassToTextView(TIME.AFTERNOON));
        night_edit_time.setText(TIMEClassToTextView(TIME.NIGHT));


        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fade);

        morning_edit_time.startAnimation(animation);
        lunch_edit_time.startAnimation(animation);
        night_edit_time.startAnimation(animation);


        morning_edit_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MaterialTimePicker picker =
                        new MaterialTimePicker.Builder()
                                .setTimeFormat(TimeFormat.CLOCK_24H)
                                .setHour(TIMEClassToHour(TIME.MORNING))
                                .setMinute(TIMEClassToMinutes(TIME.MORNING))
                                .build();

                picker.show(getSupportFragmentManager(), "tag");

                picker.addOnPositiveButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        morning_edit_time.setText(timeTextView(picker.getHour(), picker.getMinute()));
                        String time_to_string = timeToString(picker.getHour(), picker.getMinute());
                        setTime(time_to_string, 0);
                    }
                });

            }
        });
        lunch_edit_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MaterialTimePicker picker =
                        new MaterialTimePicker.Builder()
                                .setTimeFormat(TimeFormat.CLOCK_24H)
                                .setHour(TIMEClassToHour(TIME.AFTERNOON))
                                .setMinute(TIMEClassToMinutes(TIME.AFTERNOON))
                                .build();

                picker.show(getSupportFragmentManager(), "tag");

                picker.addOnPositiveButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        lunch_edit_time.setText(timeTextView(picker.getHour(), picker.getMinute()));
                        String time_to_string = timeToString(picker.getHour(), picker.getMinute());
                        setTime(time_to_string, 1);
                    }
                });

            }
        });
        night_edit_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MaterialTimePicker picker =
                        new MaterialTimePicker.Builder()
                                .setTimeFormat(TimeFormat.CLOCK_24H)
                                .setHour(TIMEClassToHour(TIME.NIGHT))
                                .setMinute(TIMEClassToMinutes(TIME.NIGHT))
                                .build();

                picker.show(getSupportFragmentManager(), "tag");

                picker.addOnPositiveButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        night_edit_time.setText(timeTextView(picker.getHour(), picker.getMinute()));
                        String time_to_string = timeToString(picker.getHour(), picker.getMinute());
                        setTime(time_to_string, 2);
                    }
                });

            }
        });
    }

    /**
     * It is used on the fetched time information from the TIMEClass to make that information textview display ready
     *
     * @param time It receives time as a string like "0830"
     * @return It returns like "08:30"
     */
    public String TIMEClassToTextView(String time) {
        return time.substring(0, 2) + ":" + time.substring(2, 4);
    }

    /**
     * It is used to extract the hour from the time information.
     *
     * @param time It receives the time as a string like "0830"
     * @return It returns the hour in Integer like 8
     */
    public int TIMEClassToHour(String time) {
        return Integer.parseInt(time.substring(0, 2));

    }

    /**
     * It is used to extract the minute from the time information
     *
     * @param time It receives the time as string like "0830"
     * @return It returns the hour in Integer like 30
     */
    public int TIMEClassToMinutes(String time) {
        return Integer.parseInt(time.substring(2, 4));
    }

    /**
     * It is used to change the values in the TIME class after fetching the information from the sharedPreferences
     */
    public void getTime() {
        SharedPreferences sharedPref = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        TIME.MORNING = sharedPref.getString("MORNING", "");
        TIME.AFTERNOON = sharedPref.getString("AFTERNOON", "");
        TIME.NIGHT = sharedPref.getString("NIGHT", "");
    }

    /**
     * It is used to update the times in the shared preferences after receiving them from the picker
     *
     * @param time It receives the time as a string (Ex- "0830")
     * @param flag This is used to know which information (MORNING/AFTERNOON/NIGHT) is needed to be updated
     */
    public void setTime(String time, int flag) {
        SharedPreferences sharedPref = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        if (flag == 0) {
            editor.putString("MORNING", time);
        } else if (flag == 1) {
            editor.putString("AFTERNOON", time);
        } else if (flag == 2) {
            editor.putString("NIGHT", time);
        }
        editor.apply();
        getTime(); //After updating the sharedPreferences , this call make the TIME class values also updated.
    }

}
