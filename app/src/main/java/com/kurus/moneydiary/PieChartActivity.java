package com.kurus.moneydiary;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;

public class PieChartActivity extends AppCompatActivity implements View.OnClickListener {


    enum Chart {
        DAILY,
        MONTHLY,
        YEARLY
    }

    private Realm realm;

    private ImageButton btnPreviousDay;
    private ImageButton btnNextDay;
    private TextView txtDate;
    private PieChart pieChart;

    private PreviewAdapter previewAdapter;
    private ListView listView;

    private Calendar specifiedCalendar;
    private Chart chart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pie_chart);
        setTitle("円グラフ");

        realm = Realm.getDefaultInstance();

        btnPreviousDay = findViewById(R.id.btnPreviousDay);
        btnNextDay = findViewById(R.id.btnNextDay);
        txtDate = findViewById(R.id.txtDate);
        pieChart = findViewById(R.id.pieChart);

        txtDate.setOnClickListener(this);
        btnPreviousDay.setOnClickListener(this);
        btnNextDay.setOnClickListener(this);

        //現在日時設定
        chart = Chart.DAILY;
        setCurrentDate();

        //円グラフ表示
        showData();
        setupPieChartView();
    }

    private void setCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.AM_PM, 0);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        specifiedCalendar = calendar;
        txtDate.setText(getFormattedDate(specifiedCalendar.getTime(), chart));
    }

    public void showData() {

        listView = findViewById(R.id.listView);
        previewAdapter = new PreviewAdapter(this, R.layout.preview_list_item, getEventListData());
        listView.setAdapter(previewAdapter);

    }

    private List<RealmEventDay> getEventListData() {
        List<RealmEventDay> realmEventDayList = new ArrayList<>();
        RealmResults<RealmEventDay> realmEventDays;

        if (chart == Chart.DAILY) {
            //1日の支出のデータを取り出す
            realmEventDays = realm.where(RealmEventDay.class).equalTo("date", specifiedCalendar.getTime()).findAll();
            for (RealmEventDay realmEventDay : realmEventDays) {
                realmEventDayList.add(realmEventDay);
            }

        } else if (chart == Chart.MONTHLY) {
            //1ヵ月の支出データを取り出す
            int maxDate = specifiedCalendar.getActualMaximum(Calendar.DATE);

            for (int dayValue = 1; dayValue <= maxDate; dayValue++) {
                specifiedCalendar.set(Calendar.DATE, dayValue);
                realmEventDays = realm.where(RealmEventDay.class).equalTo("date", specifiedCalendar.getTime()).findAll();
                for (RealmEventDay realmEventDay : realmEventDays) {
                    realmEventDayList.add(realmEventDay);
                }
            }

        } else if (chart == Chart.YEARLY) {
            //1年の支出データを取り出す(月は0始まり)
            for (int monthValue = 0; monthValue < 12; monthValue++) {
                specifiedCalendar.set(Calendar.MONTH, monthValue);
                int maxDate = specifiedCalendar.getActualMaximum(Calendar.DATE);
                for (int dayValue = 0; dayValue <= maxDate; dayValue++) {
                    realmEventDays = realm.where(RealmEventDay.class).equalTo("date", specifiedCalendar.getTime()).findAll();
                    for (RealmEventDay realmEventDay : realmEventDays) {
                        realmEventDayList.add(realmEventDay);
                    }
                }
            }
        }

        return realmEventDayList;
    }

    public void setupPieChartView() {

        //同種別の支出をまとめてHashMapで管理
        HashMap<String, Integer> valueMap = new HashMap<>();
        //支出データをRealmから取り出してListに格納
        List<RealmEventDay> realmEventDayList = getEventListData();

        //同種別であれば値を加算
        for (RealmEventDay realmEventDay : realmEventDayList) {
            if (valueMap.containsKey(realmEventDay.getItemType())) {
                valueMap.put(realmEventDay.getItemType(), valueMap.get(realmEventDay.getItemType()) + realmEventDay.getPrice());
            } else {
                valueMap.put(realmEventDay.getItemType(), realmEventDay.getPrice());
            }
        }

        //Pie Chartに値とラベルを格納
        List<PieEntry> pieEntries = new ArrayList<>();
        for (Iterator<Map.Entry<String, Integer>> iterator = valueMap.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<String, Integer> entry = iterator.next();
            pieEntries.add(new PieEntry((float) entry.getValue(), entry.getKey()));
        }

        //データをセット、Pie Chartのスタイル設定
        PieDataSet dataSet = new PieDataSet(pieEntries, "種別");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSet.setDrawValues(true);

        PieData pieData = new PieData(dataSet);
        pieData.setValueTextSize(14f);
        pieData.setValueFormatter(new PercentFormatter());

        //説明文の編集
        Description description = new Description();
        description.setText("支出");
        description.setTextColor(Color.WHITE);
        pieChart.setDescription(description);

        pieChart.setData(pieData);
        pieChart.invalidate();

        pieChart.setNoDataText("支出がありません");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtDate:
                //ダイアログでカレンダーを表示する
                DatePickerBuilder builder = new DatePickerBuilder(this, new OnSelectDateListener() {
                    @Override
                    public void onSelect(List<Calendar> calendar) {
                        specifiedCalendar = calendar.get(0);
                        txtDate.setText(getFormattedDate(specifiedCalendar.getTime(), chart));
                    }
                })
                        .pickerType(CalendarView.ONE_DAY_PICKER)
                        .date(Calendar.getInstance());
                DatePicker datePicker = builder.build();
                datePicker.show();
                break;
            case R.id.btnPreviousDay:
                //日月年に関してそれぞれ1つ前を表示する
                if (chart == Chart.DAILY) {
                    specifiedCalendar.add(Calendar.DAY_OF_MONTH, -1);
                } else if (chart == Chart.MONTHLY) {
                    specifiedCalendar.add(Calendar.MONTH, -1);
                } else if (chart == Chart.YEARLY) {
                    specifiedCalendar.add(Calendar.YEAR, -1);
                }
                txtDate.setText(getFormattedDate(specifiedCalendar.getTime(), chart));
                showData();
                setupPieChartView();
                break;
            case R.id.btnNextDay:
                //日月年に関してそれぞれ1つ後を表示する
                if (chart == Chart.DAILY) {
                    specifiedCalendar.add(Calendar.DAY_OF_MONTH, 1);
                } else if (chart == Chart.MONTHLY) {
                    specifiedCalendar.add(Calendar.MONTH, 1);
                } else if (chart == Chart.YEARLY) {
                    specifiedCalendar.add(Calendar.YEAR, 1);
                }
                txtDate.setText(getFormattedDate(specifiedCalendar.getTime(), chart));
                showData();
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem deleteMenu = menu.findItem(R.id.delete_event);
        deleteMenu.setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.intent_calendar:
                Intent intentCalendar = new Intent(PieChartActivity.this, CalendarActivity.class);
                startActivity(intentCalendar);
                finish();
                break;
            case R.id.intent_pie_chart:
                Intent intentPieChart = new Intent(PieChartActivity.this, PieChartActivity.class);
                startActivity(intentPieChart);
                finish();
                break;
            case R.id.daily_pie_chart:
                //日別集計を表示
                chart = Chart.DAILY;
                showData();
                setupPieChartView();
                setCurrentDate();
                break;
            case R.id.monthly_pie_chart:
                //月別集計を表示
                chart = Chart.MONTHLY;
                showData();
                setupPieChartView();
                setCurrentDate();
                break;
            case R.id.yearly_pie_chart:
                //年別集計を表示
                chart = Chart.YEARLY;
                showData();
                setupPieChartView();
                setCurrentDate();
                break;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    public static String getFormattedDate(Date date, Chart chart) {
        SimpleDateFormat simpleDateFormat;
        if (chart == Chart.DAILY) {
            simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault());
        } else if (chart == Chart.MONTHLY) {
            simpleDateFormat = new SimpleDateFormat("yyyy年MM月", Locale.getDefault());
        } else {
            simpleDateFormat = new SimpleDateFormat("yyyy年", Locale.getDefault());
        }
        return simpleDateFormat.format(date);
    }

}
