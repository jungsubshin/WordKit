package com.unknown.sub.wordkit;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;

public class FileListActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString());
        File[] list = folder.listFiles();
        final ArrayList<String> file_str = new ArrayList<String>();
        if (list == null)
            return;
        for(int i =0;i<list.length;i++){
            if(list[i].getName().endsWith(".xlsx")){
                file_str.add(list[i].getName());
            }
        }
        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,file_str);
        ListView listView = (ListView)findViewById(R.id.file_list);
        listView.setAdapter(adapter) ;

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String filePath = (String) parent.getItemAtPosition(position);
                String tableName = filePath.substring(0,filePath.length()-5);
                Intent openExcelintent = new Intent(FileListActivity.this,CreateVocaActivity.class);

                openExcelintent.putExtra("tableName",tableName);
                openExcelintent.putExtra("filePath",filePath);
                startActivityForResult(openExcelintent,1111);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1111:
                finish();
                break;
        }
    }
}

