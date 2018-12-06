/*****************************************************

Pendente:
	Gravar os Arquivos Log
	Rotina de Loop
	Interface de Status do LED

******************************************************/


#include <Wire.h>
#include "RTClib.h"
#include <SPI.h>
#include <SD.h>
#include <Time.h>

//Mapeamento de Portas

#define LED_GREEN 4
#define LED_RED 5

#define BOMBA1 6 
#define BOMBA2 7
#define BOMBA3 8
#define BOMBA4 9

#define PIN_CS_SD 10


#define FILE_CONF_PROG "/Config/prog.cfg"
#define FILE_CONF_ACAO "/Config/acao.cfg"
#define FILE_CONF_DATE "/Config/date.cfg"
#define FILE_CONF "/Config/config.cfg"
#define DIR_LOG "/Log"


#define GREEN 1
#define RED 2
#define YELLOW 3



typedef struct {
  byte codProg = 0;
  byte Logica = 0;
  byte tipoDado = 0;
  String valor1 = "";
  byte condicao = 0;
  String valor2 = "";
  bool condOld = false;
}Prog;

typedef struct {
  byte codProg = 0;
  bool condOld = false;
}OldProg;


RTC_DS1307 rtc;

byte acaoExec = 0;
byte OldAcaoExec = 0;

unsigned long IntervaloRep = 5000; //padrão 5 segundos
unsigned long IntervaloLog = 5000; //padrão 30 minutos

unsigned long millisProg = 0;  // Variável de controle do prog
unsigned long millisLog = 0;  // Variável de controle do Log

void setup() {
  setCorLed(YELLOW);

 
  
  Serial.begin(9600);
  while (!Serial) {
    
    ; // espera que a porta serial se conecte. Necessário apenas para porta USB nativa
  }
  
  pinMode(BOMBA1,OUTPUT);
  pinMode(BOMBA2,OUTPUT); 
  pinMode(BOMBA3,OUTPUT); 
  pinMode(BOMBA4,OUTPUT);
  
  digitalWrite(BOMBA1, HIGH);
  digitalWrite(BOMBA2, HIGH);
  digitalWrite(BOMBA3, HIGH);
  digitalWrite(BOMBA4, HIGH);
  
  iniciaSD(PIN_CS_SD);
  iniciaRTC();
  loadConfig();
  
  Serial.println("Data do Sistema:" + current_date());
  Serial.println("Hora do Sistema:" + current_time());
  
}

void loop() {

  unsigned long currentMillis = millis();    //Tempo atual em ms
  
  //Lógica de verificação do tempo
  if (currentMillis - millisProg > (IntervaloRep*1000)) { 
    setCorLed(YELLOW);    
    executarProg();
    executarAcao(acaoExec);
    delay(100);
    millisProg = millis();    // Salva o tempo atual
  } 

  if (currentMillis - millisLog > (IntervaloLog*1000)) { 
    setCorLed(YELLOW);    
    gravaLog(1);
    delay(100);
    millisLog = millis();    // Salva o tempo atual
  } 
   
  setCorLed(GREEN);
}




void executarAcao(byte acao){
  if (acao!=OldAcaoExec){
    OldAcaoExec = acao;
 // iniciaSD(PIN_CS_SD);
  File file;
  

  file = SD.open(FILE_CONF_ACAO);


  if (file) {
    
    file.seek(0);
    
    bool eof=false; 

    do{
      //Reservando os 10bytes.
      String cond= "          ";
      //index para contrar a string
      int idxCond=0;
      //Fica no loop enquanto não chega no final da linha ou do arquivo
      while(file.peek()!='\n' && file.available()){
        
        //recebe o byte do SD
        char retSD = file.read();
       // Serial.println("Começou a Ler o Arquivo de Ação");
        //coloca na posição na string o byte atual
        cond[idxCond] = retSD;

        //incremenda o Index
        idxCond++;
      }
      file.read();
      if (!file.available()){
        eof=true;    
      }

      //Limpa espaço sobrando
      cond.trim();



      //remove o primeiro pipe
      cond.remove(0,1);
      if(cond.substring(0, cond.indexOf("|")).toInt()==acao){
       
        cond.remove(0, cond.indexOf("|")+1);  
        
        byte porta = cond.substring(0, cond.indexOf("|")).toInt();
        cond.remove(0, cond.indexOf("|")+1);

        byte acao = cond.substring(0, cond.indexOf("|")).toInt();
        digitalWrite(returnPin(porta),!acao);   
      }
        
    }while(!eof);
    
  } else {
    Serial.println("Erro ao abrir arquivo: " FILE_CONF_PROG);
  }
  file.close();}
}



void executarProg(){
  //iniciaSD(PIN_CS_SD);
  File file;
  OldProg oldProg;
  oldProg.condOld = true; 
  
  Serial.println("Executando Programação");
  
  //abre o arquivo de programação
  file = SD.open(FILE_CONF_PROG);

  //Se o arquivo for aberto irá executar a rotina
  if (file) {
    //Set a posição 0 do arquivo
    file.seek(0);

    //controle do final do arquivo
    bool eof=false; 

    do{  
      //Reservando os 34bytes.
      String cond= "                                  ";
      
      //index para contrar a string
      int idxCond=0;

      //Fica no loop enquanto não chega no final da linha ou do arquivo
      while(file.peek()!='\n' && file.available()){
        
        //recebe o byte do SD
        char retSD = file.read();

        //coloca na posição na string o byte atual
        cond[idxCond] = retSD;

        //incremenda o Index
        idxCond++;
      }
      
      file.read();
      
      if (!file.available()){
        eof=true;    
      }
     
      //Limpa espaço sobrando
      cond.trim();

      //Verifica se utiliza sensor, se sim irá subistuir pelo valor atual
      if (cond.indexOf("$A") != -1){
        cond.replace("$A0", String(sensorRead(0))); 
        cond.replace("$A1", String(sensorRead(1))); 
        cond.replace("$A2", String(sensorRead(2))); 
        cond.replace("$A3", String(sensorRead(3))); 
      }

     
      //Pega a hora do RTC
      if( cond.indexOf("$time") != -1){ 
        cond.replace("$time",current_time());
      }

      //Pega a data atual do RTC
      if (cond.indexOf("$date") != -1){
        cond.replace("$date",current_date()); 
      }
        

      /**********************
      Irá montar a estrutura de dados.        
      **********/
      //estancia a estrutura de dados
      Prog prog;
      
      
      
      //remove o primeiro pipe
      cond.remove(0,1);
	  
	  prog.Logica = cond.substring(0, cond.indexOf("|")).toInt();
      cond.remove(0, cond.indexOf("|")+1);

      prog.codProg = cond.substring(0, cond.indexOf("|")).toInt();
      cond.remove(0, cond.indexOf("|")+1);

      prog.tipoDado = cond.substring(0, cond.indexOf("|")).toInt();
      cond.remove(0, cond.indexOf("|")+1);

      prog.valor1 = cond.substring(0, cond.indexOf("|"));
      cond.remove(0, cond.indexOf("|")+1);

      prog.condicao = cond.substring(0, cond.indexOf("|")).toInt();
      cond.remove(0, cond.indexOf("|")+1);

      prog.valor2 = cond.substring(0, cond.indexOf("|"));


      /********************************/
      bool retComp = false;
      
      retComp = compVar(prog.tipoDado, prog.valor1, prog.condicao, prog.valor2);
      
      
      if (oldProg.codProg != prog.codProg){
        prog.condOld = true;  
      }

      prog.condOld = execLogic(prog.condOld, prog.Logica, retComp);


      if (oldProg.codProg != prog.codProg && oldProg.condOld && OldAcaoExec != oldProg.codProg){
        Serial.println("Executa Ação: " + oldProg.codProg);   
        //executarAcao(oldProg.codProg);
        acaoExec = oldProg.codProg; 
        eof = true; 
      }
  

  
      if (eof && prog.condOld && OldAcaoExec != prog.codProg){
        Serial.println("Executa Ação: " + prog.codProg);  
        //executarAcao(prog.codProg);
        acaoExec = prog.codProg; 
        eof = true;
      }

       
      //Se não chegou no final do arquivo irá para o proximo byte
      oldProg.codProg = prog.codProg;
      oldProg.condOld = prog.condOld;
    }while(!eof);
    
  } else {
    Serial.println("Erro ao abrir arquivo: " FILE_CONF_PROG);
  }

  file.close();
}


byte returnPin(byte porta){
  switch(porta){
    case 1:
      return BOMBA1;      
    break;

    case 2:
      return BOMBA2;      
    break;

    case 3:
      return BOMBA3;      
    break;

    case 4:
      return BOMBA4;      
    break;     
  }
}


void setCorLed(byte cor){
  pinMode(LED_GREEN,OUTPUT); 
  pinMode(LED_RED,OUTPUT);
   
  switch(cor){
    case LOW:
      digitalWrite(LED_GREEN, HIGH); 
      digitalWrite(LED_RED, HIGH); 
    break;

    case RED:
      digitalWrite(LED_GREEN, HIGH); 
      digitalWrite(LED_RED, LOW); 
    break;

    case GREEN:
      digitalWrite(LED_GREEN, LOW); 
      digitalWrite(LED_RED, HIGH); 
    break;

    case YELLOW:
      digitalWrite(LED_GREEN, LOW); 
      digitalWrite(LED_RED, LOW); 
    break;

    default:
      digitalWrite(LED_GREEN, LOW); 
      digitalWrite(LED_RED, HIGH); 
  }
}




void loadConfig(){
  if (SD.exists(FILE_CONF)){
    File file;
    
    file = SD.open(FILE_CONF);
    if (file) {
      
      file.seek(0);
      
      bool eof=false; 
  
      do{
        //Reservando os 32bytes.
        String cond= "                                ";
        //index para contrar a string
        int idxCond=0;
        //Fica no loop enquanto não chega no final da linha ou do arquivo
        while(file.peek()!='\n' && file.available()){
          
          //recebe o byte do SD
          char retSD = file.read();
         // Serial.println("Começou a Ler o Arquivo de Ação");
          //coloca na posição na string o byte atual
          cond[idxCond] = retSD;
  
          //incremenda o Index
          idxCond++;
        }
        file.read();
        if (!file.available()){
          eof=true;    
        }
  
        //Limpa espaço sobrando
        cond.trim();
  
  
  
        //remove o primeiro pipe
        cond.remove(0,1);
        
        if(cond.substring(0, cond.indexOf("|"))=="IntervaloRep"){  
          cond.remove(0, cond.indexOf("|")+1);        
          IntervaloRep = cond.substring(0, cond.indexOf("|")).toInt();
        }
        
        if(cond.substring(0, cond.indexOf("|"))=="IntervaloLog"){ 
          cond.remove(0, cond.indexOf("|")+1);        
          IntervaloLog = cond.substring(0, cond.indexOf("|")).toInt();
        }
          
      }while(!eof);
      
    } else {
      Serial.println("Erro ao abrir arquivo: " FILE_CONF);
    }
    file.close();
  }
}


int sensorRead(byte sensor){
  return map(analogRead(sensor), 0, 1024, 100, 0);   
}

//1= todos os sensores e por enquanto é só isso mesmo
void gravaLog(byte tipo){
  DateTime tstamp = rtc.now();
  switch(tipo){
    case 1:
      String dirFile = String("/Log/") + String(tstamp.year()) + printnn(tstamp.month())+ printnn(tstamp.day()) +String(".csv");
      
      boolean arquivoExiste = SD.exists(dirFile);

      File file = SD.open(dirFile, FILE_WRITE);
      if (!arquivoExiste){
        Serial.println("Hora;Sensor 1;Sensor 2;Sensor 3;Sensor 4;");
        file.println("Hora;Sensor 1;Sensor 2;Sensor 3;Sensor 4;");
      }
      
      String dataString = "";
      for (int analogPin = 0; analogPin < 4; analogPin++) {
        dataString += String(sensorRead(analogPin))+ ";";
      }
   
      dataString = printnn(tstamp.hour()) + ":" + printnn(tstamp.minute()) + ":" + printnn(tstamp.second())+ ";"+dataString;
      file.println(dataString);
      file.close();
      Serial.println(dataString);

    break;  
  }
}



bool compVar(byte tipoDado, String valor1, byte operador, String valor2){
  bool retorno = false; 

  switch(operador){
    case 1:
      switch(tipoDado){
        case 1:
          retorno = (valor1.toInt() == valor2.toInt())? true : false;
        break;

        case 2:
          retorno = (dateToDay(valor1) == dateToDay(valor2))? true : false;
        break;

        case 3:
          retorno = (timeToMinute(valor1) == timeToMinute(valor2))? true : false;
        break;
      } 
    break;
     
    case 2:
      switch(tipoDado){
        case 1:
          retorno = (valor1.toInt() > valor2.toInt())? true : false;
        break;

        case 2:
          retorno = (dateToDay(valor1) > dateToDay(valor2))? true : false;
        break;

        case 3:
          retorno = (timeToMinute(valor1) > timeToMinute(valor2))? true : false;
        break;
      } 
    break; 
      
    case 3:
      switch(tipoDado){
        case 1:
          retorno = (valor1.toInt() < valor2.toInt())? true : false;   
        break;

        case 2:
          retorno = (dateToDay(valor1) < dateToDay(valor2))? true : false;   
        break;

        case 3:
          retorno = (timeToMinute(valor1) < timeToMinute(valor2))? true : false;   
        break;
      }         
    break; 
       
    case 4:
      switch(tipoDado){
        case 1:
          retorno = (valor1.toInt() >= valor2.toInt())? true : false;   
        break;

        case 2:
          retorno = (dateToDay(valor1) >= dateToDay(valor2))? true : false;   
        break;

        case 3:
          retorno = (timeToMinute(valor1) >= timeToMinute(valor2))? true : false;   
        break;
      }     
    break; 
      
    case 5:
      switch(tipoDado){
        case 1:
          retorno = (valor1.toInt() <= valor2.toInt())? true : false; 
        break;

        case 2:
          retorno = (dateToDay(valor1) <= dateToDay(valor2))? true : false; 
        break;

        case 3:
          retorno = (timeToMinute(valor1) <= timeToMinute(valor2))? true : false; 
        break;
      }  
    break; 

    case 6:
      switch(tipoDado){
        case 1:
          retorno = (valor1.toInt() != valor2.toInt())? true : false; 
        break;

        case 2:
          retorno = (dateToDay(valor1) != dateToDay(valor2))? true : false; 
        break;

        case 3:
          retorno = (timeToMinute(valor1) != timeToMinute(valor2))? true : false; 
        break;
      } 
  
    break; 

    default:
      retorno = false;
  }
 
  return retorno;
}


bool execLogic(bool valor1, byte operador, bool valor2){
  bool retorno = false; 
  
  if (operador == 1 && valor1 && valor2){
    retorno=true;
  }
  
  if ((operador == 2) && (valor1 || valor2)){
    retorno=true;
  }
  
  return retorno;
}


void iniciaSD(byte pin){
   while(!SD.begin(pin)){
        setCorLed(RED);
        Serial.println("Falha ao iniciar o SD!");  
      delay(1000);
  }
  setCorLed(YELLOW);
  Serial.println("Cartão SD iniciado");   
}

void iniciaRTC(){
  rtc.begin();

  if (SD.exists(FILE_CONF_DATE)){
    int idxCond=0;
    String cond= "                      ";
    File file;
    file = SD.open(FILE_CONF_DATE);
    while(file.peek()!='\n' && file.available()){
      char retSD = file.read();
      cond[idxCond] = retSD;
      idxCond++;
    }
    
    file.close();
    
    cond.remove(0,1);
    int ano = cond.substring(0, cond.indexOf("|")).toInt();
    cond.remove(0, cond.indexOf("|")+1);

    byte mes = cond.substring(0, cond.indexOf("|")).toInt();
    cond.remove(0, cond.indexOf("|")+1);

    byte dia = cond.substring(0, cond.indexOf("|")).toInt();
    cond.remove(0, cond.indexOf("|")+1);

    byte hora = cond.substring(0, cond.indexOf("|")).toInt();
    cond.remove(0, cond.indexOf("|")+1);

    byte minuto = cond.substring(0, cond.indexOf("|")).toInt();
    cond.remove(0, cond.indexOf("|")+1);

    byte segundo = cond.substring(0, cond.indexOf("|")).toInt();
     
    rtc.adjust(DateTime(ano, mes, dia, hora, minuto, segundo));
    Serial.println("Hora do RTC atualizada!");
    SD.remove(FILE_CONF_DATE);
  }
}

String current_date(){
  DateTime tstamp = rtc.now();
  return  printnn(tstamp.day()) + "/" + printnn(tstamp.month()) + "/" + tstamp.year();
}

String current_time(){
  DateTime tstamp = rtc.now();
  return  printnn(tstamp.hour()) + ":" + printnn(tstamp.minute());
}


// imprime um numero com 2 digitos
// acrescenta zero `a esquerda se necessario
String printnn(int n) {
  String digitos= String(n);
  if (digitos.length()==1) {
    digitos="0" + digitos;
  }  
  return digitos; 
}  





String mesExtenso(byte mes){
  switch (mes){
    case 1:
      return "Janeiro";
    break;

    case 2:
      return "Fevereiro";
    break;

    case 3:
      return "Março";
    break;

    case 4:
      return "Abril";
    break;

    case 5:
      return "Maio";
    break;

    case 6:
      return "Junho";
    break;

    case 7:
      return "Julho";
    break;

    case 8:
      return "Agosto";
    break;

    case 9:
      return "Setembro";
    break;

    case 10:
      return "Outubro";
    break;

    case 11:
      return "Novembro";
    break;

    case 12:
      return "Dezembro";
    break;

    default:
      return "Erro";
  }
}


int dateToDay(String sDate){
  //Está função vou ter que arrumar depois, pois não retorna corretamente
  //o dia que deveria retornar
  byte dia;
  byte mes;
  byte ano;
  
  dia = (sDate.substring(0, sDate.indexOf("/")).toInt());
  sDate.remove(0, sDate.indexOf("/")+1); 
  
  mes = (sDate.substring(0, sDate.indexOf("/")).toInt());
  sDate.remove(0, sDate.indexOf("/")+1); 

  ano = sDate.toInt()-2000;
  //não é um retorno certo, mas para a comparação irá funcionar
  return ((dia)+(mes*30)+(ano*365));
}

int timeToMinute(String sTime){
  int minutos=0;
  
  minutos += (sTime.substring(0, sTime.indexOf(":")).toInt()*60);
  sTime.remove(0, sTime.indexOf(":")+1); 

  minutos += sTime.toInt();
  return minutos; 
}












