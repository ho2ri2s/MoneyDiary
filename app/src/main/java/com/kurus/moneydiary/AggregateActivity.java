package com.kurus.moneydiary;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.DatePicker;
import com.applandeo.materialcalendarview.builders.DatePickerBuilder;
import com.applandeo.materialcalendarview.listeners.OnSelectDateListener;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;

public class AggregateActivity extends AppCompatActivity implements View.OnClickListener {

    // TODO: 2019/06/01 アイコンのサイズと色の変更 
    // TODO: 2019/06/01 next,previousボタンの判定 
    // TODO: 2019/06/01 pieChart詳細設定 
    // TODO: 2019/06/01 月、年別チャート
    
    private Realm realm;

    private ImageButton btnPreviousDay;
    private ImageButton btnNextDay;
    private TextView txtDate;
    private PieChart pieChart;

    private List<RealmEventDay> realmEventDayList;
    private PreviewAdapter previewAdapter;
    private ListView listView;

    private Calendar specifiedCalendar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aggregate);

        realm = Realm.getDefaultInstance();

        btnPreviousDay = findViewById(R.id.btnPreviousDay);
        btnNextDay = findViewById(R.id.btnNextDay);
        txtDate = findViewById(R.id.txtDate);
        pieChart = findViewById(R.id.pieChart);

        txtDate.setOnClickListener(this);
        btnPreviousDay.setOnClickListener(this);
        btnNextDay.setOnClickListener(this);

        //現在日時設定
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.AM_PM, 0);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        specifiedCalendar = calendar;
        txtDate.setText(getFormattedDate(specifiedCalendar.getTime()));

        showEventData();
        setupPieChartView();
    }

    public void showEventData() {
        listView = findViewById(R.id.listView);
        realmEventDayList = new ArrayList<>();

        RealmResults<RealmEventDay> realmEventDays = realm.where(RealmEventDay.class).equalTo("date", specifiedCalendar.getTime()).findAll();
        for(RealmEventDay realmEventDay : realmEventDays){
            realmEventDayList.add(realmEventDay);
        }

        previewAdapter = new PreviewAdapter(this, R.layout.preview_list_item, realmEventDayList);
        listView.setAdapter(previewAdapter);
    }

    public void setupPieChartView() {
        List<PieEntry> entries = new ArrayList<>();

        Log.d("MYTAG", "date  " + specifiedCalendar.getTime());
        RealmResults<RealmEventDay> realmEventDays = realm.where(RealmEventDay.class).equalTo("date", specifiedCalendar.getTime()).findAll();
        Log.d("MYTAG", realmEventDays + "");


        for (RealmEventDay realmEventDay : realmEventDays){
            entries.add(new PieEntry((float)realmEventDay.getPrice(), realmEventDay.getItemType()));
            Log.d("MYTAG", realmEventDay.getItemName());
        }

        PieDataSet dataSet = new PieDataSet(entries, "種別");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSet.setDrawValues(true);

        PieData pieData = new PieData(dataSet);
        pieData.setValueTextSize(14f);
        pieData.setValueFormatter(new PercentFormatter());

        pieChart.setData(pieData);
        pieChart.invalidate();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.txtDate:
                DatePickerBuilder builder = new DatePickerBuilder(this, new OnSelectDateListener() {
                    @Override
                    public void onSelect(List<Calendar> calendar) {
                        specifiedCalendar = calendar.get(0);
                        txtDate.setText(getFormattedDate(specifiedCalendar.getTime()));
                    }
                })
                        .pickerType(CalendarView.ONE_DAY_PICKER)
                        .date(Calendar.getInstance());
                DatePicker datePicker = builder.build();
                datePicker.show();
                break;
            case R.id.btnPreviousDay:
                specifiedCalendar.add(Calendar.DAY_OF_MONTH, -1);
                txtDate.setText(getFormattedDate(specifiedCalendar.getTime()));
                showEventData();
                setupPieChartView();
                break;
            case R.id.btnNextDay:
                specifiedCalendar.add(Calendar.DAY_OF_MONTH, 1);
                txtDate.setText(getFormattedDate(specifiedCalendar.getTime()));
                showEventData();
                setupPieChartView();
                break;
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    public static String getFormattedDate(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault());
        return simpleDateFormat.format(date);
    }

}
