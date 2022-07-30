
#include <RF24.h>
#include <RF24Network.h>
#include <SPI.h>
#define button 6
#define PIN_LED 7

int blue_box_state; // 0 = niks, 1 = wrong, 2 = victory, 3 = reset

//---music_declaratie----------------------------------------------------------------------------------------------------------------
// bron: https://www.arduino.cc/en/Tutorial/toneMelody
#include "pitches.h"

// notes in the melody - victory:
int melody1[] = {
  NOTE_C4, NOTE_G3, NOTE_G3, NOTE_A3, NOTE_G3, 0, NOTE_B3, NOTE_C4
};

// note durations: 4 = quarter note, 8 = eighth note, etc.:
int noteDurations1[] = {
  4, 8, 8, 4, 4, 4, 4, 4
};

// aangepast van bron: https://www.arduino.cc/en/Tutorial/toneMelody
// notes in the melody - wrong:
int melody2[] = {
  NOTE_C4, NOTE_C3
};

// note durations: 4 = quarter note, 8 = eighth note, etc.:
int noteDurations2[] = {
  4, 2
};

//---Prototype----------------------------------------------------------------------------------------------------------------
void victory(int noteDurations1[], int melody1[]);
void wrong(int noteDurations2[], int melody2[]);
void(*resetFunc)(void) = 0; //declare reset function at address 0


RF24 radio(9, 10);               // nRF24L01 (CE,CSN)
RF24Network network(radio);      // Include the radio in the network
const uint16_t this_node = 01;   // Address of this node in Octal format ( 04,031, etc)
const uint16_t node00 = 00;

void setup() {
  SPI.begin();
  radio.begin();
  network.begin(90, this_node);  //(channel, node address)
  radio.setDataRate(RF24_2MBPS);
  pinMode(button, INPUT/*_PULLUP*/);
  pinMode(PIN_LED, OUTPUT);
  digitalWrite(PIN_LED, HIGH);
  Serial.begin(9600);
  Serial.println("Start");
}

void loop() {
  network.update();
  bool buttonState = digitalRead(button);

   while ( network.available() ) {     // Is there any incoming data?
    RF24NetworkHeader header2;
    network.read(header2, &blue_box_state, sizeof(blue_box_state)); // Read the incoming data
    Serial.println(blue_box_state); // Read the incoming data)
     digitalWrite(PIN_LED, HIGH);  //cal reset
   }

  if (buttonState == 1 && blue_box_state != 10) {
    digitalWrite(PIN_LED, LOW);
    wrong(noteDurations2, melody2); // speel fout geluid
  }

   if (buttonState == 1 && blue_box_state == 10) {
    Serial.println("Send");
    RF24NetworkHeader header(node00);     // (Address where the data is going)
    bool ok = network.write(header, &buttonState, sizeof(buttonState)); // Send the data
    Serial.print(ok);
    delay(100);
    digitalWrite(PIN_LED, LOW);
    victory(noteDurations1, melody1); // speel goed geluid
    reset();
}


    
}
//---Music_functie:_victory----------------------------------------------------------------------------------------------------------------
//bron: https://www.arduino.cc/en/Tutorial/toneMelody
void victory(int noteDurations[], int melody[]) {
  // iterate over the notes of the melody:
  for (int thisNote1 = 0; thisNote1 < 8; thisNote1++) {
    int noteDuration1 = 1000 / noteDurations1[thisNote1];  // bepaalt note lengte
    tone(8, melody1[thisNote1], noteDuration1);

    int pauseBetweenNotes1 = noteDuration1 * 1.30;   // zorgt voor een pauze tussen de notes
    delay(pauseBetweenNotes1);
    noTone(8);  // stop het spelen van notes
  }
}

//---Music_functie:_wrong----------------------------------------------------------------------------------------------------------------
//aangepast van bron: https://www.arduino.cc/en/Tutorial/toneMelody
void wrong(int noteDurations2[], int melody2[]) {
  for (int thisNote2 = 0; thisNote2 < 2; thisNote2++) {
    int noteDuration2 = 1000 / noteDurations2[thisNote2]; // bepaalt note lengte
    tone(8, melody2[thisNote2], noteDuration2);

    int pauseBetweenNotes2 = noteDuration2 * 1.30;  // zorgt voor een pauze tussen de notes
    delay(pauseBetweenNotes2);
    noTone(8);  // stop het spelen van notes
  }
}

void reset(){
    digitalWrite(PIN_LED, HIGH);;  //cal reset
    blue_box_state = 5;
  }
