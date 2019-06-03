package com.kurus.moneydiary;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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

public class EditActivity extends AppCompatActivity implements View.OnClickListener {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //main画面と同じレイアウトに
        setContentView(R.layout.activity_main);
        setTitle("編集");

        realm = Realm.getDefaultInstance();

        itemType = "";

        for (int i = 0; i < btnItemType.length; i++) {
            btnItemType[i] = findViewById(btnIds[i]);
            btnItemType[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (choseIcon != null) {
                        choseIcon.setBackground(getResources().getDrawable(R.color.colorPrimary));
                    }
                    choseIcon = (ImageButton) view;
                    choseIcon.setBackground(getResources().getDrawable(R.drawable.chose_border));

                    itemType = view.getTag().toString();
                    choseImageResource = getResources().getIdentifier("ic_" + view.getTag(), "drawable", getPackageName());

                }
            });
        }
        btnPreviousDay = findViewById(R.id.btnPreviousDay);
        btnPreviousDay.setVisibility(View.INVISIBLE);

        btnNextDay = findViewById(R.id.btnNextDay);
        btnNextDay.setVisibility(View.INVISIBLE);

        txtDate = findViewById(R.id.txtDate);
        edtItemName = findViewById(R.id.edtItemName);
        edtPrice = findViewById(R.id.edtPrice);

        fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(this);
        fab.setImageResource(R.drawable.ic_check);

        txtDate.setOnClickListener(this);

        showData();

    }

    private void showData() {
        Intent intent = getIntent();
        String updateDate = intent.getStringExtra("updateDate");

        RealmEventDay realmEventDay = realm.where(RealmEventDay.class).equalTo("updateDate", updateDate).findFirst();
        txtDate.setText(getFormattedDate(realmEventDay.getDate()));
        edtItemName.setText(realmEventDay.getItemName());
        edtPrice.setText(String.valueOf(realmEventDay.getPrice()));

        for (int i = 0; i < btnItemType.length; i++) {
            if (btnItemType[i].getTag().toString().equals(realmEventDay.getItemType())) {
                btnItemType[i].setBackground(getResources().getDrawable(R.drawable.chose_border));
                choseIcon = btnItemType[i];
                break;
            }
        }

    }

    private void editEvent() {
        //updateした日時
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd - hh:mm:ss");
        final String updateDate = simpleDateFormat.format(calendar.getTime());

        final Date date = specifiedCalendar.getTime();
        final String itemName = edtItemName.getText().toString();
        final int price = Integer.parseInt(edtPrice.getText().toString());

        if (itemType != null && itemName != null && price != 0) {
            //保存
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmEventDay realmEventDay = realm.where(RealmEventDay.class).equalTo("updateDate", getIntent().getStringExtra("updateDate")).findFirst();

                    realmEventDay.setUpdateDate(updateDate);
                    realmEventDay.setImageResource(choseImageResource);
                    realmEventDay.setItemType(itemType);
                    realmEventDay.setItemName(itemName);
                    realmEventDay.setPrice(price);
                    realmEventDay.setDate(date);


                }
            });

            finish();

            Toast.makeText(this, "Complete Edit!", Toast.LENGTH_SHORT).show();
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
                        txtDate.setText(getFormattedDate(specifiedCalendar.getTime()));
                    }
                })
                        .pickerType(CalendarView.ONE_DAY_PICKER)
                        .date(Calendar.getInstance());

                DatePicker datePicker = builder.build();
                datePicker.show();
                break;
            case R.id.floatingActionButton:
                editEvent();
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
            case R.id.delete_event:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("本当に削除していいですか？")
                        .setPositiveButton("削除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //メモ削除
                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        RealmEventDay realmEventDay = realm.where(RealmEventDay.class).equalTo("updateDate", getIntent().getStringExtra("updateDate")).findFirst();
                                        realmEventDay.deleteFromRealm();
                                    }
                                });
                                Toast.makeText(EditActivity.this, "削除しました", Toast.LENGTH_SHORT).show();
                                //メモを消したらMainActivityへ戻る
                                finish();
                            }
                        })
                        .setNegativeButton("キャンセル", null)
                        .show();
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
