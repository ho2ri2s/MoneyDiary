package com.kurus.moneydiary;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;

public class PreviewActivity extends AppCompatActivity {

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


        showData();

    }

    private void showData() {
        listView = findViewById(R.id.listView);
        realmEventDayList = new ArrayList<>();

        realm = Realm.getDefaultInstance();
        RealmResults<RealmEventDay> realmEventDays = realm.where(RealmEventDay.class)
                .equalTo("date", (Date) getIntent().getExtras().get("date")).findAll();

        Log.d("MYTAG", "(Date) getIntent().getParcelableExtra(\"date\")" + (Date) getIntent().getExtras().get("date"));
        //test
        RealmResults<RealmEventDay> a =  realm.where(RealmEventDay.class).equalTo("itemType", "食費").findAll();
        for (RealmEventDay b : a){
            Log.d("MYTAG", "realm date   " +  b.getDate());
        }


        for(RealmEventDay realmEventDay : realmEventDays){
            realmEventDayList.add(realmEventDay);
        }
        Log.d("MYTAG", "realmEventList" + realmEventDayList);

        previewAdapter = new PreviewAdapter(this, R.layout.preview_list_item, realmEventDayList);

        listView.setAdapter(previewAdapter);

    }


    public static String getFormattedDate(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault());
        return simpleDateFormat.format(date);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
