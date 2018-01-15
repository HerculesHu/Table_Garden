package com.zucc.g3.hzy.myapplication.Unit;

/**
 * Created by Administrator on 2018/1/15.
 */
public class SignalBack{
    private static SignalBack instance = new SignalBack();
       private int SHH;
       private int ST;
       private int SH;
       private int opLT;
       private int LtDl;
    public void setSHH( int SHH){
        this.SHH=SHH;
    }
    public void setSH( int SH){
        this.SH=SH;
    }
    public void setOPLT( int opLT){
        this.opLT=opLT;
    }
    public void setST( int ST){
        this.ST=ST;
    }
    public void setLtDl( int LtDl){
        this.LtDl=LtDl;
    }

    public int getLtDl() {
        return LtDl;
    }

    public int getOpLT() {
        return opLT;
    }

    public int getSH() {
        return SH;
    }

    public int getSHH() {
        return SHH;
    }
    public int getST(){
        return ST;
    }

    private SignalBack(){}
    public static SignalBack newInstance(){
        return instance;
    }
}

