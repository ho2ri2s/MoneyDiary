package com.kurus.moneydiary;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
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

    // TODO: 2019/05/29 updateDate(Calendar),EventDayを保持するObjectを作る

    Realm realm;

    ImageButton btnItemType[] = new ImageButton[8];
    int btnIds[] = {
            R.id.btnEat, R.id.btnTransportation, R.id.btnEducation, R.id.btnHobby,
            R.id.btnExpendables, R.id.btnFashion, R.id.btnRent, R.id.btnCommunicationCost
    };
    ImageButton btnPreviousDay;
    ImageButton btnNextDay;
    ImageButton choseIcon;
    TextView txtDate;
    EditText edtPrice;
    EditText edtItemName;
    FloatingActionButton fab;

    Calendar specifiedCalendar;
    String itemType;
    int choseImageResource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        realm = Realm.getDefaultInstance();

        itemType = "";

        for (int i = 0; i < btnItemType.length; i++) {
            btnItemType[i] = findViewById(btnIds[i]);
            btnItemType[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(choseIcon != null){
                        choseIcon.setBackground(getResources().getDrawable(R.color.colorPrimary));
                    }
                    choseIcon = (ImageButton)view;
                    choseIcon.setBackground(getResources().getDrawable(R.drawable.chose_border));

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

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    private void addEvent() {
        //updateした日時
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd - hh:mm:ss");
        final String updateDate = simpleDateFormat.format(calendar.getTime());

        final String itemName = edtItemName.getText().toString();
        final int price = Integer.parseInt(edtPrice.getText().toString());

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
            case R.id.txtDate:
                DatePickerBuilder builder = new DatePickerBuilder(this, new OnSelectDateListener() {
                    @Override
                    public void onSelect(List<Calendar> calendar) {
                        specifiedCalendar = calendar.get(0);
                        Log.d("MYTAG", specifiedCalendar + "  spe");
                        Log.d("MYTAG", Calendar.getInstance() + "  now");

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

                break;
            case R.id.btnNextDay:
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
                Intent intentCalendar = new Intent(MainActivity.this, CalendarActivity.class);
                startActivity(intentCalendar);
                break;
            case R.id.intentAggregate:
                Intent intentAggregate = new Intent(MainActivity.this, AggregateActivity.class);
                startActivity(intentAggregate);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public static String getFormattedDate(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault());
        return simpleDateFormat.format(date);
    }
}
