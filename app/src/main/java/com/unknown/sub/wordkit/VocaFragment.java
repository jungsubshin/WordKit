package com.unknown.sub.wordkit;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;

import static android.speech.tts.TextToSpeech.ERROR;

public class VocaFragment extends Fragment implements View.OnClickListener{
    View view;
    Bundle bundle;
    Word word = new Word();
    TextView textExample1, textExample2, textExampleMean1, textExampleMean2, textWord, textWordMean, textMeans;
    TextToSpeech tts;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        bundle = new Bundle();
        word = (Word)getArguments().get("word");
        view = inflater.inflate(R.layout.voca_list,container,false);
        textExample1 = (TextView) view.findViewById(R.id.voca_example1);
        textExample1.setText(word.getExample1());
        textExample2 = (TextView) view.findViewById(R.id.voca_example2);
        textExample2.setText(word.getExample2());
        textExampleMean1 = (TextView) view.findViewById(R.id.voca_examplemean1);
        textExampleMean1.setText(word.getExampleMean1());
        textExampleMean1.setVisibility(View.INVISIBLE);
        textExampleMean2 = (TextView) view.findViewById(R.id.voca_examplemean2);
        textExampleMean2.setText(word.getExampleMean2());
        textExampleMean2.setVisibility(View.INVISIBLE);
        textWord = (TextView) view.findViewById(R.id.voca_word);
        textWord.setText(word.getWord());
        textWordMean = (TextView) view.findViewById(R.id.voca_wordMean);
        textWordMean.setText(word.getWordMean());
        textWordMean.setVisibility(View.INVISIBLE);
        textMeans = (TextView) view.findViewById(R.id.voca_means);

        textExample1.setOnClickListener(this);
        textExample2.setOnClickListener(this);
        textWord.setOnClickListener(this);
        textMeans.setOnClickListener(this);

        ((MainActivity)MainActivity.mContext).setFontBold(textWord);
        ((MainActivity)MainActivity.mContext).setFont(textExample1);
        ((MainActivity)MainActivity.mContext).setFont(textExample2);
        ((MainActivity)MainActivity.mContext).setFontBold(textWordMean);
        ((MainActivity)MainActivity.mContext).setFont(textExampleMean1);
        ((MainActivity)MainActivity.mContext).setFont(textExampleMean2);

        tts = new TextToSpeech(view.getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != ERROR) {
                    tts.setLanguage(Locale.ENGLISH);
                }
            }
        });
        tts.setSpeechRate(0.8f);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        tts.shutdown();
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.voca_example1:
                tts.speak(textExample1.getText().toString(),TextToSpeech.QUEUE_FLUSH, null);
                break;
            case R.id.voca_example2:
                tts.speak(textExample2.getText().toString(),TextToSpeech.QUEUE_FLUSH, null);
                break;
            case R.id.voca_word:
                tts.speak(textWord.getText().toString(),TextToSpeech.QUEUE_FLUSH, null);
                break;
            case R.id.voca_means:
                textMeans.setVisibility(View.INVISIBLE);
                textExampleMean1.setVisibility(View.VISIBLE);
                textExampleMean2.setVisibility(View.VISIBLE);
                textWordMean.setVisibility(View.VISIBLE);
                break;
        }
    }


}
