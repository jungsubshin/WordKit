package com.unknown.sub.wordkit;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import com.unknown.sub.wordkit.data.DBHelper;
import com.unknown.sub.wordkit.data.DBInfo.DBEntry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import static com.unknown.sub.wordkit.PreferenceActivity.*;

public class VocaActivity extends AppCompatActivity implements OnGestureListener,OnTouchListener{
    public GestureDetector gestureScanner;
    private ArrayList<Word> words;
    private Bundle bundle;
    private DBHelper dbHelper = new DBHelper(this);
    private final static int CORRECT=1, ALLCORRECT=2, WRONG=3;
    String tableName,reviewCount,studyCount;
    ImageView iCheck,iDoubleCheck,iPass,iAllPass,iUnpass;
    int selectWrongTime, selectStudyTime, pageCount, pos=0;
    float oneCheckTime, allCheckTime;
    int[] corrects = new int[20];
    int[] delayTime = new int[20];
    String[] correctWord = new String[20];
    FragmentManager fm;
    FragmentTransaction tran;
    VocaFragment vF;
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voca);
        init();
    }

    @Override
    protected void onPause() {
        super.onPause();
        update();
    }

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY){
        vF = new VocaFragment();
        fm = getSupportFragmentManager();
        tran = fm.beginTransaction();
        bundle = new Bundle();
        boolean result = false;
        try {
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                updateVoca(CORRECT);
            }
            // left to right swipe
            else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                if(pos!=0){
                bundle.putSerializable("word",words.get(--pos));
                vF.setArguments(bundle);
                tran.setCustomAnimations(R.anim.enter_from_left,R.anim.exit_to_right);
                tran.replace(R.id.voca_frame,vF);
                tran.commit();
                if(words.get(pos).getIsReview()==0)
                    showCountImage(words.get(pos).getCorrect());
                }
            }
            // down to up swipe
            else if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                updateVoca(ALLCORRECT);
            }
            // up to down swipe
            else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                updateVoca(WRONG);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return result;
    }

    public void updateVoca(int flag){
        if(flag==1){
            corrects[pos]=words.get(pos).getCorrect()+1;
            delayTime[pos]= (int)(words.get(pos).getDelayTime()*oneCheckTime);
        }
        else if(flag==2) {
            corrects[pos] = 3;
            delayTime[pos]= (int)(words.get(pos).getDelayTime()*allCheckTime);
        }
        else if(flag==3) {
            corrects[pos] = 0;
            delayTime[pos]= selectWrongTime;
        }
        correctWord[pos]= words.get(pos).getWord();
        if(pos==words.size()-1){
            update();
            Table table = DBEntry.tableInfo(dbHelper,tableName);
            Intent intent = new Intent(VocaActivity.this, VocaInfoActivity.class);
            intent.putExtra("tableName",tableName);
            intent.putExtra("reviewCount",table.getReviewCount());
            intent.putExtra("studyCount",table.getStudyCount());
            startActivity(intent);
            finish();
        }
        bundle.putSerializable("word",words.get(++pos));
        vF.setArguments(bundle);
        tran.setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_left);
        tran.replace(R.id.voca_frame,vF);
        tran.commit();
        if(words.get(pos).getIsReview()==0)
            showCountImage(words.get(pos).getCorrect());
        blickingCheckBox(flag);
    }

    private void update(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values;
        int i=0;
        while(i < words.size()) {
            values = new ContentValues();
            if(words.get(i).getIsReview()==1) {
                values.put(DBEntry.COLUMN_WORD_POPUPDATE, plusTime(delayTime[i]));
                values.put(DBEntry.COLUMN_WORD_DELAYTIME, delayTime[i]);
            }
            else if (corrects[i] == 3) {
                values.put(DBEntry.COLUMN_WORD_POPUPDATE, plusTime(selectStudyTime));
                values.put(DBEntry.COLUMN_WORD_DELAYTIME, selectStudyTime);
                values.put(DBEntry.COLUMN_WORD_ISREVIEW, 1);
            }
            else
                values.put(DBEntry.COLUMN_WORD_CORRECT, corrects[i]);
            db.update(tableName, values,DBEntry.COLUMN_WORD_WORD + "=?",new String[]{correctWord[i]});
            i++;
        }
    }

    private void init(){
        SharedPreferences prefs = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        selectStudyTime = prefs.getInt(STUDYTIME,20);
        selectWrongTime = prefs.getInt(WRONGTIME,8);
        pageCount = prefs.getInt(PAGECOUNT,5);
        oneCheckTime = prefs.getFloat(ONECHECK, (float) 1.5);
        allCheckTime = prefs.getFloat(ALLCHECK, (float) 3.0);

        iCheck = (ImageView)findViewById(R.id.single_check);
        iDoubleCheck =(ImageView)findViewById(R.id.double_check);
        iPass = (ImageView)findViewById(R.id.pass);
        iAllPass = (ImageView)findViewById(R.id.all_pass);
        iUnpass = (ImageView)findViewById(R.id.unpass);

        gestureScanner = new GestureDetector(this);
        Intent intent = getIntent();
        tableName = intent.getStringExtra("tableName");
        reviewCount = intent.getStringExtra("reviewCount");
        studyCount = intent.getStringExtra("studyCount");
        words = (ArrayList<Word>) intent.getSerializableExtra("words");
        long seed = System.nanoTime();
        Collections.shuffle(words,new Random(seed));
        if(words.get(pos).getIsReview()==0)
            showCountImage(words.get(pos).getCorrect());
        bundle = new Bundle();
        bundle.putSerializable("word", words.get(pos));
        vF = new VocaFragment();
        vF.setArguments(bundle);
        fm = getSupportFragmentManager();
        tran = fm.beginTransaction();
        tran.replace(R.id.voca_frame,vF);
        tran.commit();

        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(50);
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
    }

    private String plusTime(int time) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm",Locale.KOREA);
        cal.setTime(new Date());
        cal.add(Calendar.HOUR, time);
        String popupDate = format.format(cal.getTime());
        return popupDate;
    }

    private void showCountImage(int i){
        switch (i){
            case 0:
                iCheck.setVisibility(View.INVISIBLE);
                iDoubleCheck.setVisibility(View.INVISIBLE);
                break;
            case 1:
                iCheck.setVisibility(View.VISIBLE);
                iDoubleCheck.setVisibility(View.INVISIBLE);
                break;
            case 2:
                iCheck.setVisibility(View.INVISIBLE);
                iDoubleCheck.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void blickingCheckBox(int i){
        AlphaAnimation animation = new AlphaAnimation(1, 0);
        animation.setDuration(500);
        animation.setFillAfter(true);
        switch (i){
            case 0:
                break;
            case 1:
                iPass.startAnimation(animation);
                iPass.setVisibility(View.INVISIBLE);
                break;
            case 2:
                iAllPass.startAnimation(animation);
                iPass.setVisibility(View.INVISIBLE);
                break;
            case 3:
                iUnpass.startAnimation(animation);
                iPass.setVisibility(View.INVISIBLE);
                break;
            default:
                break;
        }
    }





    @Override
    public boolean onTouchEvent(MotionEvent me){
        return gestureScanner.onTouchEvent(me);
    }
    @Override
    public boolean onDown(MotionEvent e){
        return true;
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gestureScanner.onTouchEvent(event);
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        super.dispatchTouchEvent(ev);
        return gestureScanner.onTouchEvent(ev);
    }
    @Override
    public void onLongPress(MotionEvent e){ }
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY){ return true; }
    @Override
    public void onShowPress(MotionEvent e){ }
    @Override
    public boolean onSingleTapUp(MotionEvent e){
        return true;
    }

}
