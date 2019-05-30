package com.kurus.moneydiary;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class PreviewAdapter extends ArrayAdapter<RealmEventDay> {

    private int resource;
    private PreviewActivity previewActivity;
    private List<RealmEventDay> realmEventDays;

    public PreviewAdapter(Context context, int resource, List<RealmEventDay> objects) {
        super(context, resource, objects);

        previewActivity = (PreviewActivity) context;
        this.resource = resource;
        realmEventDays = objects;
    }

    public static class ViewHolder{
        private LinearLayout linearLayout;
        private ImageView imgIcon;
        private TextView txtItemType;
        private TextView txtItemName;
        private TextView txtPrice;

        public ViewHolder(View view){
            linearLayout = view.findViewById(R.id.linearLayout);
            imgIcon = view.findViewById(R.id.imgIcon);
            txtItemType = view.findViewById(R.id.txtItemType);
            txtItemName = view.findViewById(R.id.txtItemName);
            txtPrice = view.findViewById(R.id.txtPrice);
        }
    }


    @Override
    public View getView(int position,View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(resource, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        final RealmEventDay item = getItem(position);

        viewHolder.imgIcon.setImageResource(item.getImageResource());
        viewHolder.txtItemType.setText(item.getItemType());
        viewHolder.txtItemName.setText(item.getItemName());
        viewHolder.txtPrice.setText(String.valueOf(item.getPrice()));

        viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(previewActivity, EditActivity.class);
                intent.putExtra("updateDate", item.getUpdateDate());
                previewActivity.startActivity(intent);
            }
        });

        return convertView;
    }
}
