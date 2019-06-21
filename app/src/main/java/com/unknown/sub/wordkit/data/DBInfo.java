package com.unknown.sub.wordkit.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.unknown.sub.wordkit.Table;
import com.unknown.sub.wordkit.Word;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public final class DBInfo {

    private DBInfo() {}

    public static final class DBEntry implements BaseColumns {

        public static String TABLE_NAME = "sample";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_WORD_WORD ="word";
        public final static String COLUMN_WORD_WORDMEAN = "wordMean";
        public final static String COLUMN_WORD_EXAMPLE1 = "example1";
        public final static String COLUMN_WORD_EXAMPLEMEAN1 = "exampleMean1";
        public final static String COLUMN_WORD_EXAMPLE2 = "example2";
        public final static String COLUMN_WORD_EXAMPLEMEAN2 = "exampleMean2";
        public final static String COLUMN_WORD_POPUPDATE = "popupDate";
        public final static String COLUMN_WORD_DELAYTIME = "delayTime";
        public final static String COLUMN_WORD_ISREVIEW = "isReview";
        public final static String COLUMN_WORD_CORRECT = "correct";

        public final static String[] selectAll = {
                DBEntry._ID,
                DBEntry.COLUMN_WORD_WORD,
                DBEntry.COLUMN_WORD_WORDMEAN,
                DBEntry.COLUMN_WORD_EXAMPLE1,
                DBEntry.COLUMN_WORD_EXAMPLEMEAN1,
                DBEntry.COLUMN_WORD_EXAMPLE2,
                DBEntry.COLUMN_WORD_EXAMPLEMEAN2,
                DBEntry.COLUMN_WORD_ISREVIEW,
                DBEntry.COLUMN_WORD_POPUPDATE,
                DBEntry.COLUMN_WORD_DELAYTIME,
                DBEntry.COLUMN_WORD_CORRECT};

        public static ArrayList<Word> databaseInfo(DBHelper dbHelper, String tableName, boolean isReview,boolean order, int count) {
            ArrayList<Word> words = new ArrayList<>();
            DBEntry.TABLE_NAME = tableName;
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            Cursor cursor;
            int select = 1;
            boolean all = false;
            if(count== -1) all = true;
            if(all && order) cursor = db.query(tableName, DBEntry.selectAll, null, null, null,null,DBEntry.COLUMN_WORD_WORD +" ASC");
            else if(all) cursor = db.query(tableName, DBEntry.selectAll, null, null, null, null, null);
            else if(isReview==true) cursor = db.query(tableName, DBEntry.selectAll, DBEntry.COLUMN_WORD_ISREVIEW + "=1", null,null,null,"Random()");
            else cursor = db.query(tableName, DBEntry.selectAll, DBEntry.COLUMN_WORD_ISREVIEW + "=0", null,null,null,null);
            while (cursor.moveToNext()) {
                if(!all && select==count+1) break;
                String popupDate = cursor.getString(cursor.getColumnIndex(DBEntry.COLUMN_WORD_POPUPDATE));
                if(!all && isReview && !compareTime(popupDate)) continue;
                Word word = new Word();
                int idIndex = cursor.getColumnIndex(DBEntry._ID);
                int wordIndex = cursor.getColumnIndex(DBEntry.COLUMN_WORD_WORD);
                int wordMeanIndex = cursor.getColumnIndex(DBEntry.COLUMN_WORD_WORDMEAN);
                int exampleIndex1 = cursor.getColumnIndex(DBEntry.COLUMN_WORD_EXAMPLE1);
                int exampleMeanIndex1 = cursor.getColumnIndex(DBEntry.COLUMN_WORD_EXAMPLEMEAN1);
                int exampleIndex2 = cursor.getColumnIndex(DBEntry.COLUMN_WORD_EXAMPLE2);
                int exampleMeanIndex2 = cursor.getColumnIndex(DBEntry.COLUMN_WORD_EXAMPLEMEAN2);
                int isReviewIndex = cursor.getColumnIndex(DBEntry.COLUMN_WORD_ISREVIEW);
                int delayTimeIndex = cursor.getColumnIndex(DBEntry.COLUMN_WORD_DELAYTIME);
                int popupDateIndex = cursor.getColumnIndex(DBEntry.COLUMN_WORD_POPUPDATE);
                int correctIndex = cursor.getColumnIndex(DBEntry.COLUMN_WORD_CORRECT);
                word.setID(cursor.getInt(idIndex));
                word.setWord(cursor.getString(wordIndex));
                word.setWordMean(cursor.getString(wordMeanIndex));
                word.setExample1(cursor.getString(exampleIndex1));
                word.setExampleMean1(cursor.getString(exampleMeanIndex1));
                word.setExample2(cursor.getString(exampleIndex2));
                word.setExampleMean2(cursor.getString(exampleMeanIndex2));
                word.setDelayTime(cursor.getInt(delayTimeIndex));
                word.setIsReview(cursor.getInt(isReviewIndex));
                word.setPopupDate(cursor.getString(popupDateIndex));
                word.setCorrect(cursor.getInt(correctIndex));
                words.add(word);
                select++;
            }

            return words;
        }

        public static ArrayList<Table> allTableInfo(DBHelper dbHelper){
            ArrayList<Table> arrayList = new ArrayList<>();
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            int i = 1;
            Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type IN ('table', 'view') AND name NOT LIKE 'sqlite_%'",null);
            while(c.moveToPosition(i++)){
                Table table;
                String tableName = c.getString(0);
                table = tableInfo(dbHelper, tableName);
                arrayList.add(table);
            }
            return arrayList;
        }
        public static Table tableInfo(DBHelper dbHelper, String tableName){
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Table table = new Table();
            table.setTable(tableName);
            int count = 0;
            Cursor cReview = db.rawQuery("SELECT * FROM " + tableName + " WHERE " + DBEntry.COLUMN_WORD_ISREVIEW + " = 1 ",null);
            Cursor cStudy = db.rawQuery("SELECT * FROM " + tableName + " WHERE " + DBEntry.COLUMN_WORD_ISREVIEW + " = 0 ",null);
            while (cReview.moveToNext()) {
                int index = cReview.getColumnIndex(DBEntry.COLUMN_WORD_POPUPDATE);
                String popupDate = cReview.getString(index);
                if(compareTime(popupDate)==true) ++count;
            }
            table.setReviewCount(count+"");
            table.setStudyCount(cStudy.getCount()+"");
            return table;
        }

        static boolean compareTime(String popupDate) {
            Date curDate = new Date(System.currentTimeMillis());
            boolean review = false;
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm",Locale.KOREA);
            format.setLenient(false);
            try {
                Date reqDate = format.parse(popupDate);
                review = curDate.after(reqDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return review;
        }

        public static void insert(DBHelper dbHelper, String tableName, Word word){
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            String sql = "INSERT OR IGNORE INTO " + tableName + " ("
                    + DBEntry.COLUMN_WORD_WORD + ","
                    + DBEntry.COLUMN_WORD_WORDMEAN + ","
                    + DBEntry.COLUMN_WORD_EXAMPLE1 + ","
                    + DBEntry.COLUMN_WORD_EXAMPLEMEAN1 + ","
                    + DBEntry.COLUMN_WORD_EXAMPLE2 + ","
                    + DBEntry.COLUMN_WORD_EXAMPLEMEAN2 + ")"
                    + " VALUES(?,?,?,?,?,?)";
            db.execSQL(sql,new Object[]{word.getWord(),word.getWordMean(),word.getExample1(),word.getExampleMean1(),word.getExample2(),word.getExampleMean2()});
        }




    }

}