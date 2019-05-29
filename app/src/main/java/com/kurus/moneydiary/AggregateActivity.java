package com.kurus.moneydiary;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class AggregateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aggregate);
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
                Intent intentCalendar = new Intent(AggregateActivity.this, CalendarActivity.class);
                startActivity(intentCalendar);
                finish();
                break;
            case R.id.intentAggregate:
                Intent intentAggregate = new Intent(AggregateActivity.this, AggregateActivity.class);
                startActivity(intentAggregate);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);

    }
}
