package com.zucc.g3.hzy.myapplication;

/**
 * Created by Administrator on 2018/1/1.
 */
import org.litepal.crud.DataSupport;

public class QRcode extends DataSupport{

    private String IP;
    private String pubtopic;
    private String subtopic;


    public void setIP(String IP) {
        this.IP = IP;
    }
    public void setPubtopic(String pubtopic) {
        this.pubtopic = pubtopic;
    }

    public void setSubtopic(String subtopic) {
        this.subtopic = subtopic;
    }

    public String getSubtopic(){
        return subtopic;
    }
    public String getIP() {
        return IP;
    }

    public String getPubtopic(){
        return pubtopic;
    }



}