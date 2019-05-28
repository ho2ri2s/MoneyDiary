package com.kurus.moneydiary;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.OnDayClickListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalendarActivity extends AppCompatActivity implements OnDayClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);

        List<EventDay> events = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        events.add(new EventDay(calendar, R.drawable.ic_arrow_right));

        CalendarView calendarView = findViewById(R.id.calendarView);
        calendarView.setEvents(events);
        calendarView.setOnDayClickListener(this);
        
    }

    @Override
    public void onDayClick(EventDay eventDay) {
        Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show();
    }
}
