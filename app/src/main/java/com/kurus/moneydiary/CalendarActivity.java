package com.kurus.moneydiary;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class CalendarActivity extends AppCompatActivity implements OnDayClickListener {

    private CalendarView calendarView;

    Realm realm;
    private List<EventDay> eventDays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_calender);

        eventDays = new ArrayList<>();

        calendarView = findViewById(R.id.calendarView);

        try {
            calendarView.setDate(Calendar.getInstance().getTime());
        } catch (OutOfDateRangeException e) {
            e.printStackTrace();
        }
        calendarView.setOnDayClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        realm = Realm.getDefaultInstance();
        showData();
    }



    private void showData() {

        RealmResults<RealmEventDay> eventResult = realm.where(RealmEventDay.class).findAll();
        for (RealmEventDay realmEventDay : eventResult) {
            Log.d("MYTAG", realmEventDay + "");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(realmEventDay.getDate());
            MyEventDay myEventDay = new MyEventDay(calendar, realmEventDay.getUpdateDate(), realmEventDay.getImageResource()
            , realmEventDay.getItemType(), realmEventDay.getItemName(), realmEventDay.getPrice());

            eventDays.add(myEventDay);

        }
        calendarView.setEvents(eventDays);
    }



    @Override
    public void onDayClick(EventDay eventDay) {
        if(eventDay instanceof MyEventDay){
            //イベントがある場合はプレビュー
            previewNote(eventDay);
        }else{
            //イベントがない場合は新規作成
            //(イベントがあってもなくてもeventDay != null)
            addEvent();
        }
    }

    private void previewNote(EventDay eventDay) {
        Intent intent = new Intent(CalendarActivity.this, PreviewActivity.class);
        if(eventDay instanceof  MyEventDay){
            MyEventDay myEventDay = (MyEventDay)eventDay;
            intent.putExtra("date", myEventDay.getCalendar().getTime());
        }
        startActivity(intent);
    }
    private void addEvent() {
        Intent intent = new Intent(CalendarActivity.this, MainActivity.class);
        startActivity(intent);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
