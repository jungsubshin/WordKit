package com.unknown.sub.wordkit;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.unknown.sub.wordkit.data.DBHelper;
import com.unknown.sub.wordkit.data.DBInfo.DBEntry;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Toast vocaToast, nullToast;
    private final int REQUEST_PERMISSION_CODE = 2222;
    ArrayList<Table> tables = new ArrayList<>();
    DBHelper dbHelper = new DBHelper(this);
    TableAdapter adapter;
    public static Context mContext;
    final CharSequence[] items = {"목록", "삭제"};
    final CharSequence[] addItems = {"엑셀파일", "빈단어장"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        vocaToast = Toast.makeText(this,"파일 접근을 위한 권한이 필요합니다.", Toast.LENGTH_LONG);
        nullToast = Toast.makeText(this, "공부할 단어가 없습니다.", Toast.LENGTH_LONG);
    }

    public void setFont(TextView tv){
        Typeface face = Typeface.createFromAsset(getAssets(),"fonts/helvetica.ttf");
        tv.setTypeface(face);
    }

    public void setFontBold(TextView tv){
        Typeface face = Typeface.createFromAsset(getAssets(),"fonts/helvetica_bold.ttf");
        tv.setTypeface(face);
    }

    @Override
    protected void onStart() {
        super.onStart();
        tables = DBEntry.allTableInfo(dbHelper);
        adapter = new TableAdapter(this, tables);
        ListView listView = (ListView) findViewById(R.id.table_list);
        listView.setAdapter(adapter);
        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(Integer.parseInt(tables.get(position).getReviewCount())==0 &&  Integer.parseInt(tables.get(position).getStudyCount())==0)
                    nullToast.show();
                else {
                    Intent intent = new Intent(MainActivity.this, VocaInfoActivity.class);
                    intent.putExtra("tableName", tables.get(position).getTable());
                    intent.putExtra("reviewCount", tables.get(position).getReviewCount());
                    intent.putExtra("studyCount", tables.get(position).getStudyCount());
                    startActivity(intent);
                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("옵션선택")        // 제목 설정
                        .setItems(items, new DialogInterface.OnClickListener(){    // 목록 클릭시 설정
                            public void onClick(DialogInterface dialog, int index){
                                switch (index){
                                    case 0:
                                        Intent intent = new Intent(MainActivity.this, WordListActivity.class);
                                        intent.putExtra("tableName", tables.get(position).getTable());
                                        startActivity(intent);
                                        break;
                                    case 1:
                                        deleteAlarm(tables.get(position).getTable());
                                        break;
                                }
                            }
                        });
                AlertDialog dialog = builder.create();    // 알림창 객체 생성
                dialog.show();
                return true;
            }
        });
    }

    private void addAlarm(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("단어장 생성")        // 제목 설정
                .setItems(addItems, new DialogInterface.OnClickListener(){    // 목록 클릭시 설정
                    public void onClick(DialogInterface dialog, int index){
                        switch (index){
                            case 0:
                                checkPermission();
                                break;
                            case 1:
                                addEmptyVoca();
                                break;
                        }
                    }
                });
        AlertDialog dialog = builder.create();    // 알림창 객체 생성
        dialog.show();
    }

    private void addEmptyVoca(){
        final EditText edittext = new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("빈 단어장 생성");
        builder.setMessage("단어장 이름을 입력하세요");
        builder.setView(edittext);
        builder.setCancelable(false);
        builder.setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        create(edittext.getText().toString().trim());
                        tables = DBEntry.allTableInfo(dbHelper);
                        adapter.clear();
                        adapter.addAll(tables);
                        adapter.notifyDataSetChanged();
                    }
                });
        builder.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();
    }

    private void create(String tableName){
        DBEntry.TABLE_NAME = tableName;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        dbHelper.create(db);
    }

    private void deleteAlarm(final String tableName){
        AlertDialog.Builder alert_confirm = new AlertDialog.Builder(MainActivity.this);
        alert_confirm.setMessage("단어장을 삭제 하시겠습니까?").setCancelable(false).setPositiveButton("삭제",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteTable(tableName);
                        tables = DBEntry.allTableInfo(dbHelper);
                        adapter.clear();
                        adapter.addAll(tables);
                        adapter.notifyDataSetChanged();
                    }
                }).setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
        AlertDialog alert = alert_confirm.create();
        alert.show();
    }

    private void deleteTable(String tableName){
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("drop table "+ tableName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_addVoca) {
            addAlarm();
        }
        else if (id == R.id.menu_help) {
            if (item.getTitle().equals("도움말")) {
                Intent intent = new Intent(MainActivity.this, HelpActivity.class);
                startActivity(intent);
            }
        }else if(id == R.id.menu_preference){
            if(item.getTitle().equals("환경설정")){
                Intent intent = new Intent(MainActivity.this, PreferenceActivity.class);
                startActivity(intent);
            }
        }
        return super.onOptionsItemSelected(item);
    }


    void checkPermission(){
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            Intent Intent = new Intent(MainActivity.this,FileListActivity.class);
            startActivity(Intent);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_CODE);
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                vocaToast.show();
            }else{
                vocaToast.show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_PERMISSION_CODE:
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Intent filelistIntent = new Intent(MainActivity.this,FileListActivity.class);
                    startActivity(filelistIntent);
                }else{
                    vocaToast.show();
                }
                break;
        }
    }
}
