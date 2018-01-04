#include <EEPROM.h>
#include <LiquidCrystal.h>//arduino自带的库
#include <Wire.h>
#include <RTClib.h>
#include <DHT22.h>
#define DHT22_PIN 7//7号脚不接任何电阻
RTC_DS1307 RTC;
LiquidCrystal lcd(12, 11, 5, 4, 3, 2);//定义一些引脚接法
DHT22 myDHT22(DHT22_PIN);

int address1 = 5;
int address2 = 4;
int address3 = 3;

int settem,sethum;//温控湿控变量
int settu;//土控变量
int ye,mo,da,ho,mi,se;//时间变量
float h,t;//温度与湿度
float tu;//土壤湿度
float jumphum;//湿控区间
float jumptem;//温控区间
float jumptu;
bool lh,lt,lu;//辅助变量
int time1=0;//计算LED相关变量
int lcdop=0;
float de;//防止过湿
int openlight;//开灯时间
int lightdelay;//光照时长

void setup() {
  Serial.begin(9600);
  Wire.begin();
  RTC.begin();   
  lcd.begin(16, 2);//初始化lcd1602屏幕
   lcd.setCursor(0, 0);//对lcd1602第1行进行编辑
   lcd.print("Initializing............");lcd.print(' ');lcd.print(' ');
   lcd.setCursor(0, 1);//对lcd1602第1行进行编辑
   lcd.print("Initializing............");lcd.print(' ');lcd.print(' ');
  settem= EEPROM.read(address1);
  sethum= EEPROM.read(address2);
  settu= EEPROM.read(address3);
  pinMode(8,OUTPUT); pinMode(9,OUTPUT); pinMode(10,OUTPUT); pinMode(13,OUTPUT);
  transmit2();
  de=0;h=0,t=0;tu=0;lh=true;lt=true;lu=true;jumphum=1;jumptem=0.5;jumptu=2;//变量初始化
}

void loop() {

if (( (Serial.read()) == ( 'A' ) )) {settime(); }//当串口读入A时修改时间

else
{    gettimeanddht();
  
     if(h>0){//主程序开始
  long last2 = millis();
  long now2 = millis();//记录当前时间
   while (last2-now2< 5000) {//运行3秒
       last2 = millis();
        gettimeanddht();
             lcd1602();
             control();
             transmit();
             receive();
   }
     }
     else{
   lcd.setCursor(0, 0);//对lcd1602第1行进行编辑
   lcd.print("DHT22error............");lcd.print(' ');lcd.print(' ');
   lcd.setCursor(0, 1);//对lcd1602第1行进行编辑
   lcd.print("Waitting............");lcd.print(' ');lcd.print(' ');
      }

    if(lcdop>=1||lcdop<0){lcdop=0;}else{lcdop++;}
      
   }
  
}


void settime(){//修改DS1307的时间
 RTC.set(RTC_YEAR, 2016-48);
 RTC.set(RTC_MONTH, 3);
 RTC.set(RTC_DAY, 12);
 RTC.set(RTC_HOUR, 14);
 RTC.set(RTC_MINUTE, 42);
 RTC.set(RTC_SECOND, 10);
  }

void gettimeanddht(){//从ds1307获取时间数据,从dht22获取温度，湿度数据
  DateTime now = RTC.now();  ye=(now.year());  mo=(now.month());  da=(now.day());  ho=(now.hour());  mi=(now.minute());  se=(now.second());
  DHT22_ERROR_t errorCode;if( myDHT22.readData()== DHT_ERROR_NONE){t=myDHT22.getTemperatureC(); h=myDHT22.getHumidity();}  //读取DHT22的数据判断并赋予h,t.
  tu =100.0*( 1023- analogRead(A2)) /1023.0;//获取土壤湿度值
  de =100.0*( 1023- analogRead(A3)) /1023.0;//获取是否溢出
  time1=ho*60+mi;
  }

void lcd1602(){//lcd显示信息
  if(lcdop==0){
   lcd.setCursor(0, 0);//对lcd1602第1行进行编辑
   lcd.print('T');lcd.print('e');lcd.print('m');lcd.print(':');lcd.print(t);lcd.print(' ');lcd.print('S');lcd.print('e');lcd.print('t');lcd.print(settem);lcd.print(' ');lcd.print(' ');lcd.print(' ');lcd.print(' ');
   lcd.setCursor(0, 1);//对lcd1602第2行进行编辑
   lcd.print('H');lcd.print('u');lcd.print('m');lcd.print(':');lcd.print(h);lcd.print(' ');lcd.print('S');lcd.print('e');lcd.print('t');lcd.print(sethum);lcd.print(' ');lcd.print(' ');lcd.print(' ');lcd.print(' ');
   }
   if(lcdop==1){
     lcd.setCursor(0, 0);//对lcd1602第1行进行编辑
     lcd.print('S');lcd.print('O');lcd.print('I');lcd.print('L');lcd.print(tu);lcd.print(' ');lcd.print('S');lcd.print('e');lcd.print('t');lcd.print(settu);lcd.print(' ');lcd.print(' ');lcd.print(' ');lcd.print(' ');
     lcd.setCursor(0, 1);//对lcd1602第2行进行编辑
     lcd.print(mo); lcd.print('/'); lcd.print(da);lcd.print('/');lcd.print(ho);lcd.print(':');lcd.print(mi);lcd.print(':');lcd.print(se);lcd.print(' ');lcd.print(' ');lcd.print(' ');lcd.print(' '); lcd.print(' ');lcd.print(' ');lcd.print(' ');
  }
}

void control(){//温控湿控土壤湿控光控
              if(h>sethum&&h<=100&&lh){lh=false; }
              if(h<=(sethum-jumphum)){lh=true;}
              if(lh){  digitalWrite(10,LOW);}
              else{  digitalWrite(10,HIGH);}

              if(t<settem&&lt){lt=false; }
              if(t>=(settem+jumptem)){lt=true;}
              if(lt){  digitalWrite(13,LOW);}
              else{  digitalWrite(13,HIGH);}

              if(tu<settu&&lu){lu=false; }
              if(tu>=(settu+jumptu)){lu=true;}
              if(lu||de>5||tu<0.2||settu==0){  digitalWrite(8,LOW);}
              else {  digitalWrite(8,HIGH);}
              
//              if(time1>1200||time1<360){digitalWrite(9,LOW);}
//              if(time1>=360&&time1<=1200){digitalWrite(9,HIGH);}
          lightcontrol();
  }

void lightcontrol(){//led控制
  int CloseTime;
  int OpenTime;
  OpenTime=openlight*60;
  CloseTime=openlight*60+lightdelay*60;
  if(CloseTime>=1440){//跨越24点的情况
    CloseTime=CloseTime-1440;
      if(time1>=CloseTime&&time1<=OpenTime){digitalWrite(9,LOW);}
      else digitalWrite(9,HIGH);
    }
  else{
        if(time1>=OpenTime&&time1<=CloseTime){digitalWrite(9,HIGH);}
       else digitalWrite(9,LOW);
      }
  }

void transmit(){

        Serial.print('a');
        Serial.print(t);
        Serial.print('b');
        Serial.print(h);
        Serial.print('c');
        Serial.print(tu);
        Serial.println('#');
        delay(100);
  }


void transmit2(){
  long last = millis();
  long now = millis();//记录当前时间
   while (last-now< 5000) {//连续发送5秒钟
        last = millis();
        Serial.print('d');
        Serial.print(settem);
        Serial.print('e');
        Serial.print(sethum);
        Serial.print('f');
        Serial.print(settu);
        Serial.println('$');
        delay(100);
   }
  }

void receive(){

  String comdata = "";
  String buf1="";
  String buf2="";
  String buf3="";
  String buf4="";
  String buf5="";
  
    while (Serial.available() > 0)  
    {
        comdata += char(Serial.read());
        delay(2);
    }
    if (comdata.length() > 0)
    {
      int ja,jb,jc,jd,je,jf;
      ja=-1;jb=-1;jc=-1;jd=-1;je=-1;jf=-1;
      
for(int i = 0; i < comdata.length(); i++){
 
  if(comdata[i]=='a'){ja=i;}
  if(comdata[i]=='b'){jb=i;}
  if(comdata[i]=='c'){jc=i;}
  if(comdata[i]=='x'){jd=i;}
  if(comdata[i]=='z'){je=i;}
  if(comdata[i]=='#'){jf=i;}
  
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
    for(int k4=jd+1;k4<je;k4++){
  buf4+=char(comdata[k4]);
  }
    for(int k5=je+1;k5<jf;k5++){
  buf5+=char(comdata[k5]);
  }
         settem=buf1.toInt();
         sethum=buf2.toInt();
         settu=buf3.toInt();
         openlight=buf4.toInt();
         lightdelay=buf5.toInt();
  EEPROM.write(address1, settem);delay(10);
  EEPROM.write(address2, sethum);delay(10);
  EEPROM.write(address3, settu);delay(10);
    }
  
    }
  
  }
