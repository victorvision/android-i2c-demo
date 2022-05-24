/*
      RoboCore - Tutorial Comunicacao entre Arduinos: I2C - Parte 1
                                                           (Master)
                                                       (03/05/2015)

  Escrito por Marcelo Farias.

  Exemplo de como comunicar Arduinos utilizando o protocolo I2C.

  Altera o estado do LED conectado a placa Slave quando o botao ligado
  a placa Master for pressionado.

  Na placa Master foi utilizado um botao conectado ao pino 4, com o
  resistor de pullup interno da placa acionado e realizou-se um debounce
  em software. Na placa Slave foi utilizado um LED conectado ao pino 7.

  Referencias:
  Exemplo Debounce - https://www.arduino.cc/en/Tutorial/Debounce
  Tutorial Master Writer - https://www.arduino.cc/en/Tutorial/MasterWriter
*/

#include "Wire.h"

#define buttonPin 4 // numero do pino onde o botao esta conectado
  
  
// endereco do modulo slave que pode ser um valor de 0 a 255
#define slaveAdress 0x08

boolean buttonState;             // estado atual do botao
boolean lastButtonState = LOW;   // valor da ultima leitura do botao
boolean ledState = HIGH;         // estado atual do LED

// as variaveis a seguir sao do tipo long por conta que o tempo, medido 
// em milessegundos alcancara rapidamente um numero grande demais para 
// armazenar em uma variavel do tipo int
unsigned long lastDebounceTime = 0;  // tempo da ultima modificacao do estado do LED

// tempo de debounce; aumentar se o LED oscilar; espera-se que o LED acenda
// apenas se o botao for pressionado por mais de 50ms
unsigned long debounceDelay = 50;    

void setup() {
  Wire.begin(); // ingressa ao barramento I2C
  
  // configura o pino do botao como entrada com resistor de pullup interno
  pinMode(buttonPin, INPUT_PULLUP);
}

void loop() {
  // le o estado do botao e salva em uma variavel local
  int reading = digitalRead(buttonPin);

  // verifica se voce apenas apertou o botao (i.e. se a entrada foi de LOW 
  // to HIGH), e se ja esperou tempo suficiente para ignorar qualquer ruido

  // se a entrada foi alterada devido ao ruido ou botao ter sido pressionado:
  if (reading != lastButtonState) {
    // reseta o tempo do debounce
    lastDebounceTime = millis();
  }
  
  if ((millis() - lastDebounceTime) > debounceDelay) {
    // qualquer que seja a leitura atual, ela se manteve por um tempo maior
    // que o nosso debounce delay, então atualizemos o estado atual:

    // se o estado do botao foi alterado:
    if (reading != buttonState) {
      buttonState = reading;

      // apenas altera o estado do LED se o novo estado do botao e HIGH
      if (buttonState == HIGH) {
        ledState = !ledState;
        // incia a transmissao para o endereco 0x08 (slaveAdress)
        Wire.beginTransmission(slaveAdress);
        Wire.write(ledState); // envia um byte contendo o estado do LED
        Wire.endTransmission(); // encerra a transmissao
      }
    }
  }
  // salva a leitura. No proximo laco este sera o ultimo 
  // estado do botao (lastButtonState)
  lastButtonState = reading;
}
