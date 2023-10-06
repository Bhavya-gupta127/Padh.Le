package com.example.project;

// this class stores the information regarding the Tasks done on the present day (helps on forming the BarChart)
public class BarHelper {
    int easy=0;
    int medium=0;
    int big=0;
    int v_big=0;

    public BarHelper(){}
    public BarHelper(int easy,int medium,int big,int v_big){
        this.easy=easy;
        this.medium=medium;
        this.big=big;
        this.v_big=v_big;
    }

    public int getEasy() {
        return easy;
    }

    public void setEasy(int easy) {
        this.easy = easy;
    }

    public int getMedium() {
        return medium;
    }

    public void setMedium(int medium) {
        this.medium = medium;
    }

    public int getBig() {
        return big;
    }

    public void setBig(int big) {
        this.big = big;
    }

    public int getV_big() {
        return v_big;
    }

    public void setV_big(int v_big) {
        this.v_big = v_big;
    }
}
