package com.example.laba19;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.widget.PopupWindow;

import com.example.laba19.databinding.ActivityMainBinding;
import com.example.laba19.databinding.BasicPopupWindowBinding;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private BasicPopupWindowBinding popupBinding;
    private boolean popupWindowIsActive = false;

    private static final int POPUP_WINDOW_TYPE_WELCOME = 0, POPUP_WINDOW_TYPE_FAREWELL = 1;

    private String year, month, day, hour, minute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Calendar minCalendar = Calendar.getInstance();
        minCalendar.add(Calendar.DATE, -7);
        int minYear = minCalendar.get(Calendar.YEAR), minMonth = minCalendar.get(Calendar.MONTH), minDay = minCalendar.get(Calendar.DAY_OF_MONTH);
        minCalendar.set(minYear, minMonth, minDay);
        binding.datePicker.setMinDate(minCalendar.getTimeInMillis());

        Calendar maxCalendar = Calendar.getInstance();
        maxCalendar.add(Calendar.DATE, 7);
        int maxYear = maxCalendar.get(Calendar.YEAR), maxMonth = maxCalendar.get(Calendar.MONTH), maxDay = maxCalendar.get(Calendar.DAY_OF_MONTH);
        maxCalendar.set(maxYear, maxMonth, maxDay);
        binding.datePicker.setMaxDate(maxCalendar.getTimeInMillis());

        binding.chooseButton.setOnClickListener(view -> {
            String text;

            year = "" + binding.datePicker.getYear();
            if (year.length() < 2) { year = "0" + year; }
            month = "" + (binding.datePicker.getMonth()+1);
            if (month.length() < 2) { month = "0" + month; }
            day = "" + binding.datePicker.getDayOfMonth();
            if (day.length() < 2) { day = "0" + day; }
            text = this.getString(R.string.app_chosen_date_text);
            text = text.replace("-", (day + "." + month + "." + year));
            binding.chosenDateTextView.setText(text);

            hour = "" + binding.timePicker.getHour();
            if (hour.length() < 2) { hour = "0" + hour; }
            minute = "" + binding.timePicker.getMinute();
            if (minute.length() < 2) { minute = "0" + minute; }
            text = this.getString(R.string.app_chosen_time_text);
            text = text.replace("-", hour + ":" + minute);
            binding.chosenTimeTextView.setText(text);
        });

        Timer event_timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> createPopupWindow(POPUP_WINDOW_TYPE_WELCOME));
                //Log.d("APP_DEBUGGER", "Called the create popup window function.");
            }
        };
        event_timer.schedule(timerTask, 1000L);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.leaveButton) {
            createPopupWindow(POPUP_WINDOW_TYPE_FAREWELL);
        }
        return super.onOptionsItemSelected(item);
    }

    private void createPopupWindow(int windowType) {
        if (popupWindowIsActive) {
            return;
        }
        popupWindowIsActive = true;

        if (windowType != POPUP_WINDOW_TYPE_WELCOME && windowType != POPUP_WINDOW_TYPE_FAREWELL) {
            throw (new RuntimeException("Unknown popup window type."));
        }

        popupBinding = BasicPopupWindowBinding.inflate(getLayoutInflater());
        View popupView = popupBinding.getRoot();

        int width = ConstraintLayout.LayoutParams.WRAP_CONTENT;
        int height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
        PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

        String text;
        if (windowType == POPUP_WINDOW_TYPE_WELCOME) {
            text = this.getString(R.string.app_welcome_text);
        } else {
            text = this.getString(R.string.app_farewell_text);
        }
        popupBinding.popupText.setText(text);

        if (windowType == POPUP_WINDOW_TYPE_WELCOME) {
            popupBinding.noButton.setVisibility(View.GONE);
            popupBinding.yesButton.setVisibility(View.GONE);

            popupBinding.middleButton.setOnClickListener(view -> {
                popupWindow.dismiss();
            });
        } else {
            popupBinding.middleButton.setVisibility(View.GONE);

            popupBinding.noButton.setOnClickListener(view -> {
                popupWindow.dismiss();
            });
            popupBinding.yesButton.setOnClickListener(view -> {
                finish();
                System.exit(0);
            });
        }

        popupWindow.setOnDismissListener(() -> {
            popupWindowIsActive = false;
        });

        popupWindow.showAtLocation(binding.getRoot(), Gravity.CENTER, 0, 0);
    }
}