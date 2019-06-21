package com.unknown.sub.wordkit;

import java.io.Serializable;
@SuppressWarnings("serial")
public class Word implements Serializable {


    private int ID;
    private String word;
    private String wordMean;
    private String example1;
    private String exampleMean1;
    private String example2;
    private String exampleMean2;
    private int isReview;
    private String popupDate;
    private int delayTime;
    private int correct;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getWordMean() {
        return wordMean;
    }

    public void setWordMean(String wordMean) {
        this.wordMean = wordMean;
    }

    public String getExample1() {
        return example1;
    }

    public void setExample1(String example1) {
        this.example1 = example1;
    }

    public String getExampleMean1() {
        return exampleMean1;
    }

    public void setExampleMean1(String exampleMean1) {
        this.exampleMean1 = exampleMean1;
    }

    public String getExample2() {
        return example2;
    }

    public void setExample2(String example2) {
        this.example2 = example2;
    }

    public String getExampleMean2() {
        return exampleMean2;
    }

    public void setExampleMean2(String exampleMean2) {
        this.exampleMean2 = exampleMean2;
    }

    public int getIsReview() {
        return isReview;
    }

    public void setIsReview(int review) {
        isReview = review;
    }

    public String getPopupDate() {
        return popupDate;
    }

    public void setPopupDate(String popupDate) {
        this.popupDate = popupDate;
    }

    public int getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(int delayTime) {
        this.delayTime = delayTime;
    }

    public int getCorrect() {
        return correct;
    }

    public void setCorrect(int correct) {
        this.correct = correct;
    }
}
