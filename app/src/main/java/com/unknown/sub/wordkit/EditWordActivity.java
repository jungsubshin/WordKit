package com.unknown.sub.wordkit;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.support.v7.widget.Toolbar;

import com.unknown.sub.wordkit.data.DBHelper;
import com.unknown.sub.wordkit.data.DBInfo.DBEntry;

public class EditWordActivity extends AppCompatActivity {
    private EditText eWord, eWordMean, eExample1, eExample2, eExampleMean1, eExampleMean2;
    Word selectWord;
    Toolbar toolbar;
    String tableName;
    int selectid;
    protected void onCreate(Bundle savedInstancesState) {
        super.onCreate(savedInstancesState);
        setContentView(R.layout.activity_edit);
        selectWord = (Word) getIntent().getSerializableExtra("word");
        tableName = getIntent().getStringExtra("tableName");

        eWord = (EditText) findViewById(R.id.edit_word);
        eWordMean = (EditText) findViewById(R.id.edit_wordMean);
        eExample1 = (EditText) findViewById(R.id.edit_example1);
        eExample2 = (EditText) findViewById(R.id.edit_example2);
        eExampleMean1 = (EditText) findViewById(R.id.edit_exampleMean1);
        eExampleMean2 = (EditText) findViewById(R.id.edit_exampleMean2);

        selectid = selectWord.getID();
        eWord.setText(selectWord.getWord());
        eWordMean.setText(selectWord.getWordMean());
        eExample1.setText(selectWord.getExample1());
        eExample2.setText(selectWord.getExample2());
        eExampleMean1.setText(selectWord.getExampleMean1());
        eExampleMean2.setText(selectWord.getExampleMean2());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_save) {
            String word = eWord.getText().toString().trim();
            if(word.isEmpty())
                editAlarm();
            else {
                update();
                finish();
            }
        }else if(id == R.id.menu_delete){
            deleteAlarm();
        }

        return super.onOptionsItemSelected(item);
    }

    private void editAlarm(){
        AlertDialog.Builder alert_confirm = new AlertDialog.Builder(EditWordActivity.this);
        alert_confirm.setMessage("단어를 입력해 주세요").setCancelable(true).setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        AlertDialog alert = alert_confirm.create();
        alert.show();
    }

    private void deleteAlarm(){
        AlertDialog.Builder alert_confirm = new AlertDialog.Builder(EditWordActivity.this);
        alert_confirm.setMessage("단어를 삭제하시겠습니까?").setCancelable(false).setPositiveButton("삭제",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        delete();
                        finish();
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


    private void update(){
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String word = eWord.getText().toString().trim();
        String wordMean = eWordMean.getText().toString().trim();
        String example1 = eExample1.getText().toString().trim();
        String example2 = eExample2.getText().toString().trim();
        String exampleMean1 = eExampleMean1.getText().toString().trim();
        String exampleMean2 = eExampleMean2.getText().toString().trim();

        ContentValues values = new ContentValues();
        values.put(DBEntry.COLUMN_WORD_WORD,word);
        values.put(DBEntry.COLUMN_WORD_WORDMEAN,wordMean);
        values.put(DBEntry.COLUMN_WORD_EXAMPLE1,example1);
        values.put(DBEntry.COLUMN_WORD_EXAMPLEMEAN1,exampleMean1);
        values.put(DBEntry.COLUMN_WORD_EXAMPLE2,example2);
        values.put(DBEntry.COLUMN_WORD_EXAMPLEMEAN2,exampleMean2);
        db.update(tableName,values,DBEntry._ID+ " = ?",new String[]{selectid+""});

    }

    private void delete(){
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(tableName,DBEntry._ID+ " = ?",new String[]{selectid+""});
    }


}