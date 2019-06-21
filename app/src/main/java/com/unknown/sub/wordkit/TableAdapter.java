package com.unknown.sub.wordkit;
import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class TableAdapter extends ArrayAdapter<Table> {

    public TableAdapter(Activity context, ArrayList<Table> tables){
        super(context,0,tables);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.table_list_item, parent, false);
        }
        String table = getItem(position).getTable();
        String reviewCount = getItem(position).getReviewCount();
        String studyCount = getItem(position).getStudyCount();


        TextView tableTextView = (TextView) listItemView.findViewById(R.id.table_list_item);
        tableTextView.setText(table);
        if(reviewCount.equals("0")) {
            tableTextView.setBackgroundColor(Color.WHITE);
        }
        TextView reviewTextView = (TextView) listItemView.findViewById(R.id.review_count);
        reviewTextView.setText("복습필요 : " + reviewCount);

        TextView studyTextView = (TextView) listItemView.findViewById(R.id.study_count);
        studyTextView.setText("암기필요 : " + studyCount);

        return listItemView;
    }
}
