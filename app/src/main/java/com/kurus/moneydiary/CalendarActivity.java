package com.kurus.moneydiary;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;

public class CalendarActivity extends AppCompatActivity implements OnDayClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);

        CalendarView calendarView = findViewById(R.id.calendarView);

        calendarView.setOnDayClickListener(this);

    }

    @Override
    public void onDayClick(EventDay eventDay) {
        Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.intentCalendar:
                Intent intentCalendar = new Intent(CalendarActivity.this, CalendarActivity.class);
                startActivity(intentCalendar);
                finish();
                break;
            case R.id.intentAggregate:
                Intent intentAggregate = new Intent(CalendarActivity.this, AggregateActivity.class);
                startActivity(intentAggregate);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);

    }
}
