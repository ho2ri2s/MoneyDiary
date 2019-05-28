package com.kurus.moneydiary;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Realm realm;

    Button btnItemType[] = new Button[8];
    int btnIds[] = {
            R.id.btnEat, R.id.btnTransportation, R.id.btnEducation, R.id.btnHobby,
            R.id.btnExpendables, R.id.btnPlay, R.id.btnRent, R.id.btnCommunicationCost
    };
    Button btnPreviousDay;
    Button btnNextDay;
    TextView txtDate;
    EditText edtPrice;
    EditText edtItemName;
    FloatingActionButton fab;

    String itemType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        realm = Realm.getDefaultInstance();

        itemType = "";

        for(int i = 0; i < btnItemType.length; i++){
         btnItemType[i] = findViewById(btnIds[i]);
         btnItemType[i].setOnClickListener(this);
        }
        btnPreviousDay = findViewById(R.id.btnPreviousDay);
        btnNextDay = findViewById(R.id.btnNextDay);
        txtDate = findViewById(R.id.txtDate);
        edtItemName = findViewById(R.id.edtItemName);
        edtPrice = findViewById(R.id.edtPrice);

        fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSpending();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    private void addSpending() {
        final String date = txtDate.getText().toString();
        final String itemName = edtItemName.getText().toString();
        final int price = Integer.parseInt(edtPrice.getText().toString());

        if(date != null && itemName != null && price != 0){
            //保存
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Spending spending = realm.createObject(Spending.class);
                    spending.setDate(date);
                    spending.setItemName(itemName);
                    spending.setPrice(price);
                    spending.setItemType(itemType);
                }
            });
            //入力リセット
            edtItemName.setText("");
            edtPrice.setText("0");


            Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "全ての項目を入力してください。", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View view) {
        LinearLayout linearLayout = (LinearLayout) view.getParent();
        TextView txtItemType = (TextView) linearLayout.getChildAt(0);
        itemType = txtItemType.getText().toString();
    }
}
