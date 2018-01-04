#include <ESP8266WiFi.h>
#include <PubSubClient.h>
#include <ArduinoJson.h>
#include <stdlib.h>
#include <string.h>

const char* ssid = "12";//连接的路由器的名字
const char* password = "12345678";//连接的路由器的密码
const char* mqtt_server = "123.206.127.199";//服务器的地址

WiFiClient espClient;
PubSubClient client(espClient);


long lastMsg = 0;//存放时间的变量
long lastMsg2 = 0;//存放时间的变量
char msg[20];//存放要发的数据
char msg1[20];//存放要发的数据
char msg2[20];//存放要发的数据
char msg3[20];//存放要发的数据
char msg4[20];//存放要发的数据
char msg5[20];//存放要发的数据
char msg6[127];//存放要发的数据
int settem,sethum,settu;//控制变量
float t,h,tu;
int openlight=8;//开灯时间
int lightdelay=12;//光照时长

void setup_wifi() {//自动接入网络
  delay(10);
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
  }
}

void transmit(){

        Serial.print('a');
        Serial.print(settem);
        Serial.print('b');
        Serial.print(sethum);
        Serial.print('c');
        Serial.print(settu);
         Serial.print('x');
        Serial.print(openlight);
         Serial.print('z');
        Serial.print(lightdelay);
        Serial.println('#');
        delay(100);
  }
void callback(char* topic, byte* payload, unsigned int length) {//用于接收服务器接收的数据

    
  if ((char)payload[0] == '{') {
       char data[length];
      for(int i=0;i<length;i++){
           data[i]=(char)payload[i];
      }
      decodeJson(data,length);
     transmit();
  }
  if ((char)payload[0] == 'a') {
     if(settem>=40||settem<1){settem=1;}else{settem++;}  transmit();
  }
   if ((char)payload[0] == 'b') {
    if(settem>30||settem<1){settem=1;}else{settem=settem+10;}  transmit();
  }
   if ((char)payload[0] == 'c') {
     if(settem>40||settem<2){settem=40;}else{settem=settem-1;}  transmit();
  }
   if ((char)payload[0] == 'd') {
    if(settem>40||settem<11){settem=40;}else{settem=settem-10;}  transmit();
  }
   if ((char)payload[0] == 'e') {
       if(sethum>=100||sethum<0){sethum=0;}else{sethum++;}  transmit();
  }
   if ((char)payload[0] == 'f') {
      if(sethum>90||sethum<0){sethum=0;}else{sethum=sethum+10;}  transmit();
  }
   if ((char)payload[0] == 'g') {
       if(sethum>100||sethum<1){sethum=100;}else{sethum=sethum-1;}  transmit();
  }
   if ((char)payload[0] == 'h') {
      if(sethum>100||sethum<11){sethum=100;}else{sethum=sethum-10;}  transmit();
  }
   if ((char)payload[0] == 'i') {
      if(settu>=100||settu<0){settu=0;}else{settu++;}  transmit();
  }
   if ((char)payload[0] == 'j') {
         if(settu>90||settu<0){settu=0;}else{settu=settu+10;}  transmit();
  }
   if ((char)payload[0] == 'k') {
         if(settu>100||settu<1){settu=100;}else{settu=settu-1;}  transmit();
  }
   if ((char)payload[0] == 'l') {
        if(settu>100||settu<11){settu=100;}else{settu=settu-10;}  transmit();
  }

}

void  decodeJson(char* payload, unsigned int length) {
 
   DynamicJsonBuffer jsonBuffer;
  JsonObject& root = jsonBuffer.parseObject(payload);
 settu= root["SSH"];
 settem= root["ST"];
 sethum= root["SH"];
 openlight= root["opLt"] ;
 lightdelay=root["LtDl"];
   //  Serial.println(payload);
}





void reconnect() {//等待，直到连接上服务器
  while (!client.connected()) {
    if (client.connect("HZy14d2a85v7")) {//接入时的用户名，尽量取一个很不常用的用户名
      client.subscribe("hgzxyl1428577");//接收外来的数据时的intopic
    } else {
      Serial.print("failed, rc=");
      Serial.print(client.state());//重新连接
      Serial.println(" try again in 5 seconds");
      delay(5000);
    }
  }
}



void receive2(){

  String comdata ="";
  String buf1="";
  String buf2="";
  String buf3="";
  
   while (Serial.available() > 0)  
    {
        comdata += char(Serial.read());
        delay(2);
    }
    if (comdata.length() > 0)
    {
      int ja,jb,jc,jd;
       ja=-1;jb=-1;jc=-1;jd=-1;
for(int i = 0; i < comdata.length(); i++){
 
  if(comdata[i]=='d'){ja=i;}
  if(comdata[i]=='e'){jb=i;}
  if(comdata[i]=='f'){jc=i;}
  if(comdata[i]=='$'){jd=i;}
  
  }
    if(ja!=-1&&jb!=-1&&jc!=-1&&jd!=-1){         
 for(int k=ja+1;k<jb;k++){
  buf1+=char(comdata[k]);
  }
  for(int k2=jb+1;k2<jc;k2++){
  buf2+=char(comdata[k2]);
  }
  for(int k3=jc+1;k3<jd;k3++){
  buf3+=char(comdata[k3]);
  }
         settem=buf1.toInt();
         sethum=buf2.toInt();
         settu=buf3.toInt();
    }
    }
  
  }


void receive1(){

  String comdata = "";
  String buf1="";
  String buf2="";
  String buf3="";
  
    while (Serial.available() > 0)  
    {
        comdata += char(Serial.read());
        delay(2);
    }
    if (comdata.length() > 0)
    {
      int ja,jb,jc,jd;
      ja=-1;jb=-1;jc=-1;jd=-1;
for(int i = 0; i < comdata.length(); i++){
 
  if(comdata[i]=='a'){ja=i;}
  if(comdata[i]=='b'){jb=i;}
  if(comdata[i]=='c'){jc=i;}
  if(comdata[i]=='#'){jd=i;}
  
  }
        if(ja!=-1&&jb!=-1&&jc!=-1&&jd!=-1){       
 for(int k=ja+1;k<jb;k++){
  buf1+=char(comdata[k]);
  }
  for(int k2=jb+1;k2<jc;k2++){
  buf2+=char(comdata[k2]);
  }
  for(int k3=jc+1;k3<jd;k3++){
  buf3+=char(comdata[k3]);
  }
         t=buf1.toFloat();
         h=buf2.toFloat();
        tu=buf3.toFloat();
     
  
    }
    }
  }

  void sending(){
 
     dtostrf(t,1,2,msg);
    client.publish("hgzxyl1428571", msg);
    dtostrf(h,1,2,msg1);
    client.publish("hgzxyl1428572", msg1);
     dtostrf(tu,1,2,msg2);
    client.publish("hgzxyl1428573", msg2);
    snprintf (msg3, 75, "%d", settem);
    client.publish("hgzxyl1428574", msg3);
     snprintf (msg4, 75, "%d", sethum);
    client.publish("hgzxyl1428575", msg4);
     snprintf (msg5, 75, "%d", settu);
    client.publish("hgzxyl1428576", msg5);
    }


String encodeJson(){
  DynamicJsonBuffer jsonBuffer;
  JsonObject& root1 = jsonBuffer.createObject();
  root1["tem"] =double_with_n_digits(t,2);
  root1["hum"] = double_with_n_digits(h,2);
  root1["soilHum"] = double_with_n_digits(tu,2);
  root1["setsoilHum"] = settu;
  root1["setTem"] = settem;
  root1["setHum"] = sethum;
  root1["opLt"] = openlight;
  root1["LtDl"] = lightdelay;
  String json;
  root1.printTo(msg6);
 // loadMsg(json);
  return json;
  }

void loadMsg(String msgCopy){
   int len=msgCopy.length();
   for(int i=0;i<len;i++){
    msg6[i]=msgCopy[i];
   }
}

void setup() {//初始化程序，只运行一遍
  
  Serial.begin(9600);
  Serial1.begin(115200);

  setup_wifi();
  client.setServer(mqtt_server, 1883);
  client.setCallback(callback);
  t=0;tu=0;h=0; 
  long last = millis();
  long now = millis();//记录当前时间
  while (last-now< 6000) {//连续发送6秒钟
  last = millis();
   receive2();
 
  }
}



void loop() {//主循环
  receive1();
  reconnect();//确保连上服务器，否则一直等待。
  client.loop();//MUC接收数据的主循环函数。

  long now = millis();//记录当前时间
    long now2 = millis();//记录当前时间
  if (now - lastMsg > 1000) {//每隔1秒发一次信号
    lastMsg = now;//刷新上一次发送数据的时间
  sending();
  }
  if (now2 - lastMsg2 > 1500) {//每隔1.5秒发一次信号
   lastMsg2 = now2;//刷新上一次发送数据的时间
  encodeJson();
   client.publish("farmbox", msg6);
  }
  transmit();
}


