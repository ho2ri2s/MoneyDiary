package com.kurus.moneydiary;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.applandeo.materialcalendarview.DatePicker;
import com.applandeo.materialcalendarview.builders.DatePickerBuilder;
import com.applandeo.materialcalendarview.listeners.OnSelectDateListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // TODO: 2019/05/29 updateDate(Calendar),EventDayを保持するObjectを作る

    Realm realm;

    ImageButton btnItemType[] = new ImageButton[8];
    int btnIds[] = {
            R.id.btnEat, R.id.btnTransportation, R.id.btnEducation, R.id.btnHobby,
            R.id.btnExpendables, R.id.btnFashion, R.id.btnRent, R.id.btnCommunicationCost
    };
    Button btnPreviousDay;
    Button btnNextDay;
    TextView txtDate;
    EditText edtPrice;
    EditText edtItemName;
    FloatingActionButton fab;

    Calendar specifiedCalendar;
    String itemType;

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
                    LinearLayout linearLayout = (LinearLayout) view.getParent();
                    TextView txtItemType = (TextView) linearLayout.getChildAt(0);
                    itemType = txtItemType.getText().toString();
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

    private void addSpending() {
        //updateした日時
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd - hh:mm:ss");
        final String updateDate = simpleDateFormat.format(calendar.getTime());
        final Date date = calendar.getTime();

        final String itemName = edtItemName.getText().toString();
        final int price = Integer.parseInt(edtPrice.getText().toString());

        if (itemType != null && itemName != null && price != 0) {
            //保存
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmEventDay realmEventDay = realm.createObject(RealmEventDay.class);
                    // TODO: 2019/05/29 txtCalendar押すと日付が取得できるようにする

                    realmEventDay.setUpdateDate(updateDate);
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
        switch (view.getId()){
            case R.id.floatingActionButton:
                addSpending();
                break;
            case R.id.txtDate:
                DatePickerBuilder builder = new DatePickerBuilder(this, new OnSelectDateListener() {
                    @Override
                    public void onSelect(List<Calendar> calendar) {
                        specifiedCalendar = calendar.get(0);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
                        txtDate.setText(simpleDateFormat.format(specifiedCalendar.getTime()));
                    }
                });
                DatePicker datePicker = builder.build();
                datePicker.show();
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
}
