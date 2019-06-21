package com.unknown.sub.wordkit;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class PreferenceActivity extends AppCompatActivity {
    final static String PREFERENCES = "preferences";
    final static String WRONGTIME = "wrongtime";
    final static String STUDYTIME = "studytime";
    final static String WPOS = "wpos";
    final static String SPOS = "spos";
    final static String ONECHECK = "onecheck";
    final static String ALLCHECK = "allcheck";
    final static String PAGECOUNT = "pagecount";
    Spinner wrongSpinner,studySpinner;
    EditText editOneCheck, editAllCheck, editPageCount;
    String[] wrongArray, studyArray;
    int selectWrongTime, selectStudyTime, wrongS, studyS, selWPos, selSPos, pageCount;
    float oneCheckTime, allCheckTime;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);
        SharedPreferences prefs = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        wrongArray = getResources().getStringArray(R.array.array_wrong_delay_time);
        studyArray = getResources().getStringArray(R.array.array_study_delay_time);

        pageCount = prefs.getInt(PAGECOUNT,5);
        selectWrongTime = prefs.getInt(WRONGTIME,8);
        selectStudyTime = prefs.getInt(STUDYTIME,20);
        wrongS = prefs.getInt(WPOS,0);
        studyS = prefs.getInt(SPOS,1);
        oneCheckTime = prefs.getFloat(ONECHECK, (float) 1.5);
        allCheckTime = prefs.getFloat(ALLCHECK, (float) 3.0);

        wrongSpinner = (Spinner) findViewById(R.id.spinner_wrongdelay);
        studySpinner = (Spinner) findViewById(R.id.spinner_studydelay);
        editOneCheck = (EditText) findViewById(R.id.edit_correcttime);
        editAllCheck = (EditText) findViewById(R.id.edit_allcorrecttime);
        editPageCount = (EditText) findViewById(R.id.edit_page_count);
        setupSpinner();
        editOneCheck.setText(oneCheckTime +"");
        editAllCheck.setText(allCheckTime +"");
        editPageCount.setText(pageCount+"");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_preference, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_save_preference) {
            savePreference();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void savePreference() {
        String correct = editOneCheck.getText().toString().trim();
        String allCorrect = editAllCheck.getText().toString().trim();
        String count = editPageCount.getText().toString().trim();
        if(Integer.valueOf(count)>20) count = 20 + "";
        SharedPreferences prefs = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(PAGECOUNT,Integer.valueOf(count));
        editor.putInt(WRONGTIME,Integer.valueOf(wrongArray[selWPos]));
        editor.putInt(WPOS,selWPos);
        editor.putInt(STUDYTIME,Integer.valueOf(studyArray[selSPos]));
        editor.putInt(SPOS,selSPos);
        editor.putFloat(ONECHECK,Float.valueOf(correct));
        editor.putFloat(ALLCHECK,Float.valueOf(allCorrect));
        editor.commit();
    }

    private void setupSpinner() {
        ArrayAdapter wrongSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_wrong_delay_time, android.R.layout.simple_spinner_item);
        wrongSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        wrongSpinner.setAdapter(wrongSpinnerAdapter);

        ArrayAdapter studySpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_study_delay_time, android.R.layout.simple_spinner_item);
        studySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        studySpinner.setAdapter(studySpinnerAdapter);

        wrongSpinner.setSelection(wrongS);
        studySpinner.setSelection(studyS);

        wrongSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String select = (String)parent.getItemAtPosition(position);
                    if (select.equals(wrongArray[position])) {
                        selWPos = position;
                    }
                }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectWrongTime = 8;
            }
        });

        studySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String select = (String)parent.getItemAtPosition(position);
                if (select.equals(studyArray[position])) {
                    selSPos = position;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectWrongTime = 20;
            }
        });
    }
}