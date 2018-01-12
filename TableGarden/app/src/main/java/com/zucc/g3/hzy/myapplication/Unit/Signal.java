package com.zucc.g3.hzy.myapplication.Unit;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2018/1/12.
 */

public class Signal {
    public int openLightTime;
    public int lightDelay;
    public float temperature;
    public float humidity;
    public float soilHumidity;
    public int setHumidity;
    public int setTemperature;
    public int setSoilHumidity;


    public void ParseJson(String jsonString) throws JSONException
    {
        JSONObject jObject = new JSONObject(jsonString);
        openLightTime =(int)jObject.get("opLt");
        lightDelay =(int)jObject.get("LtDl");
        temperature =Float.parseFloat(jObject.get("tem").toString());
        humidity =Float.parseFloat(jObject.get("hum").toString());
        soilHumidity =Float.parseFloat(jObject.get("soilHum").toString());
        setHumidity =(int)jObject.get("setHum");
        setTemperature =(int)jObject.get("setTem");
        setSoilHumidity =(int)jObject.get("setsoilHum");

    }
}
