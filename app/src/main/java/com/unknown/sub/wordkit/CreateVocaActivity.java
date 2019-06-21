package com.unknown.sub.wordkit;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import com.unknown.sub.wordkit.data.DBHelper;
import com.unknown.sub.wordkit.data.DBInfo.DBEntry;
import java.io.File;
import java.text.SimpleDateFormat;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class CreateVocaActivity extends AppCompatActivity {
    String tableName;
    String filePath;
    DBHelper dbHelper;
    ProgressDialog myDialog;

    protected void onCreate(Bundle savedInstancesState){
        super.onCreate(savedInstancesState);
        setContentView(R.layout.activity_create);
        dbHelper = new DBHelper(this);
        Intent intent = getIntent();
        tableName = intent.getStringExtra("tableName");
        filePath = intent.getStringExtra("filePath");
        create(tableName);
        HtmlAsyncTask htmlAsyncTask = new HtmlAsyncTask();
        htmlAsyncTask.execute();
    }

    private class HtmlAsyncTask extends AsyncTask<String,Void,Word> {
        Word word = new Word();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected Word doInBackground(String... strings) {
            File fpath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File f = new File(fpath,filePath);
            try {
                XSSFWorkbook workbook = new XSSFWorkbook(f);
                XSSFSheet sheet = workbook.getSheetAt(0);
                int rowsCount = sheet.getPhysicalNumberOfRows();
                FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
                for (int r = 0; r<rowsCount; r++) {
                    Row row = sheet.getRow(r);
                    int cellsCount = row.getPhysicalNumberOfCells();
                    for (int c = 0; c<cellsCount; c++) {
                        String[] means = new String[5];
                        String value = getCellAsString(row, c, formulaEvaluator);
                        Document doc = Jsoup.connect("http://dic.daum.net/search.do?q="+value+"&dic=eng&search_first=Y").get();
                        Elements elements = doc.getElementsByClass("list_search");
                        if(elements.size()==0) continue;
                        means[0] = elements.first().select(".txt_search").text();  // 단어 뜻
                        Elements elements2 = doc.getElementsByClass("txt_example");
                        if(elements2.size()!=0) means[1] = elements2.get(0).select(".txt_ex").text();  // 예문
                        Elements elements3 = doc.getElementsByClass("mean_example");
                        if(elements3.size()!=0) means[2] = elements3.get(0).select(".txt_ex").text();  // 예문 뜻
                        Elements elements4 = doc.getElementsByClass("txt_example");
                        if(elements4.size()!=0) means[3] = elements4.get(1).select(".txt_ex").text();  // 예문2
                        Elements elements5 = doc.getElementsByClass("mean_example");
                        if(elements5.size()!=0) means[4] = elements5.get(1).select(".txt_ex").text();  // 예문2 뜻
                        word.setWord(value);
                        word.setWordMean(means[0]);
                        word.setExample1(means[1]);
                        word.setExampleMean1(means[2]);
                        word.setExample2(means[3]);
                        word.setExampleMean2(means[4]);
                        DBEntry.insert(dbHelper, tableName, word);
                    }
                }
            } catch (Exception e) {
            }

            return word;
        }
        protected void onPostExecute(Word word){
            if(word==null)
                return;
            myDialog.dismiss();
            finish();

        }
    }
    private void showProgressDialog() {
        myDialog = new ProgressDialog(this);
        myDialog.setMessage("단어장 생성중...");
        myDialog.setCancelable(false);
        myDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        myDialog.show();
    }

    protected String getCellAsString(Row row, int c, FormulaEvaluator formulaEvaluator) {
        String value = "";
        try {
            Cell cell = row.getCell(c);
            CellValue cellValue = formulaEvaluator.evaluate(cell);
            switch (cellValue.getCellType()) {
                case Cell.CELL_TYPE_BOOLEAN:
                    value = ""+cellValue.getBooleanValue();
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    double numericValue = cellValue.getNumberValue();
                    if(HSSFDateUtil.isCellDateFormatted(cell)) {
                        double date = cellValue.getNumberValue();
                        SimpleDateFormat formatter =
                                new SimpleDateFormat("dd/MM/yy");
                        value = formatter.format(HSSFDateUtil.getJavaDate(date));
                    } else {
                        value = ""+numericValue;
                    }
                    break;
                case Cell.CELL_TYPE_STRING:
                    value = ""+cellValue.getStringValue();
                    break;
                default:
            }
        } catch (NullPointerException e) {
        }
        return value;
    }

    private void create(String tableName){
        DBEntry.TABLE_NAME = tableName;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        dbHelper.create(db);
    }


}
