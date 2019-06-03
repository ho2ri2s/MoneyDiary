package com.kurus.moneydiary;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.DatePicker;
import com.applandeo.materialcalendarview.builders.DatePickerBuilder;
import com.applandeo.materialcalendarview.listeners.OnSelectDateListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Realm realm;

    private ImageButton btnItemType[] = new ImageButton[8];
    private int btnIds[] = {
            R.id.btnEat, R.id.btnTransportation, R.id.btnEducation, R.id.btnHobby,
            R.id.btnExpendables, R.id.btnFashion, R.id.btnRent, R.id.btnCommunicationCost
    };
    private ImageButton btnPreviousDay;
    private ImageButton btnNextDay;
    private ImageButton choseIcon;
    private TextView txtDate;
    private EditText edtPrice;
    private EditText edtItemName;
    private FloatingActionButton fab;

    private Calendar specifiedCalendar;
    private String itemType;
    private int choseImageResource;


    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("記入");

        realm = Realm.getDefaultInstance();

        itemType = "";

        for (int i = 0; i < btnItemType.length; i++) {
            btnItemType[i] = findViewById(btnIds[i]);
            btnItemType[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //選択された場合枠線で囲む処理
                    if(choseIcon != null){
                        choseIcon.setBackground(getResources().getDrawable(R.color.colorPrimary));
                    }
                    choseIcon = (ImageButton)view;
                    choseIcon.setBackground(getResources().getDrawable(R.drawable.chose_border));

                    //Realmに格納する値
                    itemType = view.getTag().toString();
                    choseImageResource = getResources().getIdentifier("ic_" + view.getTag(), "drawable", getPackageName());

                }
            });
        }
        btnPreviousDay = findViewById(R.id.btnPreviousDay);
        btnNextDay = findViewById(R.id.btnNextDay);
        txtDate = findViewById(R.id.txtDate);
        edtItemName = findViewById(R.id.edtItemName);
        edtPrice = findViewById(R.id.edtPrice);

        fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(this);
        txtDate.setOnClickListener(this);
        btnPreviousDay.setOnClickListener(this);
        btnNextDay.setOnClickListener(this);

        //現在の日付を設定
        //calendarViewにに合わせるため、以下のように設定
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.AM_PM, 0);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        specifiedCalendar = calendar;
        txtDate.setText(getFormattedDate(specifiedCalendar.getTime()));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    private void addEvent() {
        //updateDateを識別子として各支出を管理
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd - hh:mm:ss");
        final String updateDate = simpleDateFormat.format(calendar.getTime());

        final String itemName = edtItemName.getText().toString();
        final int price = Integer.parseInt(edtPrice.getText().toString());

        //必要項目が全て記入・選択されていればRealmに保存
        if (itemType != null && itemName != null && price != 0 && specifiedCalendar != null) {
            final Date date = specifiedCalendar.getTime();
            //保存
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmEventDay realmEventDay = realm.createObject(RealmEventDay.class);

                    realmEventDay.setUpdateDate(updateDate);
                    realmEventDay.setImageResource(choseImageResource);
                    realmEventDay.setItemType(itemType);
                    realmEventDay.setItemName(itemName);
                    realmEventDay.setPrice(price);
                    realmEventDay.setDate(date);
                }
            });
            //入力リセット
            edtItemName.setText("");
            edtPrice.setText("0");

            Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "全ての項目を入力してください。", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //カレンダーダイアログを表示、日付の取得
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
            case R.id.floatingActionButton:
                addEvent();
                break;
            case R.id.btnPreviousDay:
                specifiedCalendar.add(Calendar.DAY_OF_MONTH, -1);
                txtDate.setText(getFormattedDate(specifiedCalendar.getTime()));
                break;
            case R.id.btnNextDay:
                specifiedCalendar.add(Calendar.DAY_OF_MONTH, 1);
                txtDate.setText(getFormattedDate(specifiedCalendar.getTime()));
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
        menu.setGroupVisible(R.id.pie_chart_group, false);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.intent_calendar:
                Intent intentCalendar = new Intent(MainActivity.this, CalendarActivity.class);
                startActivity(intentCalendar);
                break;
            case R.id.intent_pie_chart:
                Intent intentPieChart = new Intent(MainActivity.this, PieChartActivity.class);
                startActivity(intentPieChart);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public static String getFormattedDate(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault());
        return simpleDateFormat.format(date);
    }
}
