package com.unknown.sub.wordkit;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.unknown.sub.wordkit.data.DBHelper;
import com.unknown.sub.wordkit.data.DBInfo.DBEntry;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class WordListActivity extends AppCompatActivity {
    String tableName;
    ProgressDialog myDialog;
    DBHelper dbHelper;
    ArrayAdapter adapter;
    ArrayList<Word> words;
    ArrayList<String> strWord;
    boolean isSucess = true;
    protected void onCreate(Bundle savedInstancesState) {
        super.onCreate(savedInstancesState);
        setContentView(R.layout.activity_wordlist);
        dbHelper = new DBHelper(this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDialog();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        tableName = getIntent().getStringExtra("tableName");
        strWord = extractWord();
        adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, strWord);
        ListView listView = (ListView)findViewById(R.id.word_list);
        listView.setAdapter(adapter) ;


        //리스트 뷰 이벤트 리스너
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
                Intent intent = new Intent(WordListActivity.this, EditWordActivity.class);
                intent.putExtra("word", words.get(position));
                intent.putExtra("tableName",tableName);
                startActivity(intent);
            }
        });
    }

    private void addDialog(){
        final EditText edittext = new EditText(this);
        edittext.setFilters(new InputFilter[]{filterEng});
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("단어추가");
        builder.setMessage("추가할 단어 및 숙어를 입력하세요\n(한글x)");
        builder.setView(edittext);
        builder.setCancelable(false);
        builder.setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        HtmlAsyncTask htmlAsyncTask = new HtmlAsyncTask();
                        htmlAsyncTask.execute(edittext.getText().toString().trim());
                    }
                });
        builder.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();
    }

    public InputFilter filterEng = new InputFilter() {
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Pattern ps = Pattern.compile("^[-_a-zA-Z0-9 ]+$");
            if (!ps.matcher(source).matches()) {
                return "";
            }
            return null;
        }
    };

    private class HtmlAsyncTask extends AsyncTask<String,Void,Word> {
        Word word = new Word();
        String[] means = new String[5];

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected Word doInBackground(String... strings) {
            try {
                Document doc2 = Jsoup.connect("http://dic.daum.net/search.do?q="+strings[0]+"&dic=eng&search_first=Y").get();
                Elements elements = doc2.getElementsByClass("list_search");
                means[0] = elements.first().select(".txt_search").text();  // 단어 뜻
                Elements elements2 = doc2.getElementsByClass("txt_example");
                means[1] = elements2.get(0).select(".txt_ex").text();  // 예문
                Elements elements3 = doc2.getElementsByClass("mean_example");
                means[2] = elements3.get(0).select(".txt_ex").text();  // 예문 뜻
                Elements elements4 = doc2.getElementsByClass("txt_example");
                means[3] = elements4.get(1).select(".txt_ex").text();  // 예문2
                Elements elements5 = doc2.getElementsByClass("mean_example");
                means[4] = elements5.get(1).select(".txt_ex").text();  // 예문2 뜻
                word.setWord(strings[0]);
                word.setWordMean(means[0]);
                word.setExample1(means[1]);
                word.setExampleMean1(means[2]);
                word.setExample2(means[3]);
                word.setExampleMean2(means[4]);
                DBEntry.insert(dbHelper, tableName, word);
                isSucess = true;
                }
             catch (Exception e) {
                isSucess = false;
            }
            return word;
        }

        protected void onPostExecute(Word word){
            if(word==null)
                return;
            strWord = extractWord();
            adapter.clear();
            adapter.addAll(strWord);
            adapter.notifyDataSetChanged();
            myDialog.dismiss();
            showToast(isSucess);
        }
    }

    private void showToast(boolean b){
        if(b == true) Toast.makeText(getApplicationContext(),"추가완료",Toast.LENGTH_SHORT).show();
        else Toast.makeText(getApplicationContext(),"오류",Toast.LENGTH_SHORT).show();
    }

    private ArrayList<String> extractWord(){
        int i=0;
        DBHelper dbHelper = new DBHelper(this);
        words = DBEntry.databaseInfo(dbHelper,tableName, true, true, -1);
        ArrayList<String> tmp = new ArrayList<>();
        while(i<words.size()){
            tmp.add(words.get(i).getWord());
            i++;
        }
        return tmp;
    }

    private void showProgressDialog() {
        myDialog = new ProgressDialog(this);
        myDialog.setMessage("단어를 추가하는중...");
        myDialog.setCancelable(false);
        myDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        myDialog.show();
    }
}
