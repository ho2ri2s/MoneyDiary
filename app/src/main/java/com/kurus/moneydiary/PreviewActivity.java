package com.kurus.moneydiary;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;

public class PreviewActivity extends AppCompatActivity implements View.OnClickListener {

    private List<RealmEventDay> realmEventDayList;
    private PreviewAdapter previewAdapter;
    private ListView listView;
    private FloatingActionButton fab;

    private Realm realm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        showData();
    }

    private void showData() {
        listView = findViewById(R.id.listView);
        realmEventDayList = new ArrayList<>();

        Intent intent = getIntent();
        Date eventDate = (Date) intent.getExtras().get("date");

        realm = Realm.getDefaultInstance();
        RealmResults<RealmEventDay> realmEventDays = realm.where(RealmEventDay.class)
                .equalTo("date", eventDate).findAll();

        for(RealmEventDay realmEventDay : realmEventDays){
            realmEventDayList.add(realmEventDay);
        }

        previewAdapter = new PreviewAdapter(this, R.layout.preview_list_item, realmEventDayList);
        listView.setAdapter(previewAdapter);

        setTitle(getFormattedDate(eventDate) + "の支出");
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(PreviewActivity.this, MainActivity.class);
        startActivity(intent);
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
