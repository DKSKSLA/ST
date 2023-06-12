package com.example.myapplication;

public class DataModel {//할 일 어댑터에 들어갈 데이터 모델 형태
    String id;
    String title;
    String time;
    String space;
    String memo;
    int repeat;

    public String getId() {
        return id;
    }
    public void setText1(String text1) {
        this.id = text1;
    }

    public String getTitle() {
        return title;
    }
    public void setText2(String text2) {
        this.title = text2;
    }

    public String getTime() {return time;}
    public void setText3(String text3) {this.time = text3;}

    public String getSpace() {
        return space;
    }
    public void setText4(String text4) {
        this.space = text4;
    }

    public String getMemo() {
        return memo;
    }
    public void setText5(String text4) {
        this.memo = text4;
    }
    public void setMemo(String text4) {this.memo = text4;}

    public int getRepeat() {
        return repeat;
    }
    public void setRepeat(int text5) {
        this.repeat = text5;
    }

    public DataModel(String id,String title,String time,String space,String memo) {
        this.id = id;
        this.title =title;
        this.time = time;
        this.space =space;
        this.memo=memo;

    }
}
