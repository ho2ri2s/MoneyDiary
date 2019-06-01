package com.kurus.moneydiary;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

public class AggregateActivity extends AppCompatActivity {

    ImageButton btnPreviousDay;
    ImageButton btnNextDay;
    TextView txtDate;
    PieChart pieChart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aggregate);

        btnPreviousDay = findViewById(R.id.btnPreviousDay);
        btnNextDay = findViewById(R.id.btnNextDay);
        txtDate = findViewById(R.id.txtDate);
        pieChart = findViewById(R.id.pieChart);

        setupPieChartView();

    }

    private void setupPieChartView() {
        List<PieEntry> entries = new ArrayList<>();


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
