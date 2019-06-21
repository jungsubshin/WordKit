package com.unknown.sub.wordkit;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import com.unknown.sub.wordkit.data.DBHelper;
import com.unknown.sub.wordkit.data.DBInfo.DBEntry;
import java.util.ArrayList;

import static com.unknown.sub.wordkit.PreferenceActivity.*;

public class VocaInfoActivity extends AppCompatActivity {
    private String tableName, reviewCount, studyCount;
    int viewCount;
    DBHelper dbHelper = new DBHelper(this);
    ArrayList<Word> words = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vocainfo);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        SharedPreferences prefs = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        tableName = intent.getStringExtra("tableName");
        reviewCount = intent.getStringExtra("reviewCount");
        studyCount = intent.getStringExtra("studyCount");
        viewCount = prefs.getInt(PAGECOUNT,5);
        TextView info = (TextView) findViewById(R.id.vocainfo_showcount);
        TextView desText = (TextView) findViewById(R.id.vocainfo_description);
        if(Integer.parseInt(reviewCount)==0 && Integer.parseInt(studyCount)!=0) {
            words = DBEntry.databaseInfo(dbHelper, tableName, false, false, viewCount);
            desText.setText("암기모드 입니다");
            info.setText("남은 단어 : " + studyCount);
        }
        else if(Integer.parseInt(reviewCount)==0 && Integer.parseInt(studyCount)==0) {

            new AlertDialog.Builder(this)
            .setTitle("WordKit")
            .setMessage("학습완료")
            .setPositiveButton("확인",new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dlg, int sumthin) {
                    finish();
                }
             }).show();
        }
        else {
            words = DBEntry.databaseInfo(dbHelper, tableName, true, false, viewCount);
            desText.setText("복습모드 입니다");
            info.setText("남은 단어 : " + reviewCount);
        }

        TextView textView = (TextView) findViewById(R.id.vocainfo);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VocaInfoActivity.this, VocaActivity.class);
                intent.putExtra("words",words);
                intent.putExtra("tableName",tableName);
                intent.putExtra("reviewCount",reviewCount);
                intent.putExtra("studyCount",studyCount);
                startActivity(intent);
                finish();
            }
        });
    }


}
