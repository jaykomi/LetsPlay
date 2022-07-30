/*
  Arduino Wireless Network - Multiple NRF24L01 Tutorial
  == Example 01 - Servo Control / Node 01 - Servo motor ==
*/
#include <RF24.h>
#include <RF24Network.h>
#include <SPI.h>

RF24 radio(9, 10);               // nRF24L01 (CE,CSN)
RF24Network network(radio);      // Include the radio in the network
const uint16_t this_node = 00;   // Address of our node in Octal format ( 04,031, etc)
const uint16_t other_node = 01;

char incomingByte[6];
bool goed = true;
bool buttonState = false;
int blue_box_state = 10;
int red_box_state = 20;
int green_box_state = 30;
int yellow_box_state = 40;

//---Prototype----------------------------------------------------------------------------------------------------------------
//void Send_States(int blue_box_state, int red_box_state, int green_box_state, int yellow_box_state);

void setup() {
  //  pinMode(led, OUTPUT);
  SPI.begin();
  radio.begin();
  network.begin(90, this_node); //(channel, node address)
  radio.setDataRate(RF24_2MBPS);
  Serial.begin(9600);
  //Serial.println("Start");

}

void loop() {

  
    // read_app_data
    if (Serial.available() > 0){
      Serial.readBytes(incomingByte, 5); // read the incoming byte:
    bool goed = true;
    


    while (goed) {
      // knop check
      if (incomingByte[0] == 'b' && incomingByte[1] == 'l' )
        Send_States(blue_box_state);
      goed = false;

      if (incomingByte[0] == 'r' && incomingByte[1] == 'o' )
        Send_States(red_box_state);
      goed = false;

      if (incomingByte[0] == 'g' && incomingByte[1] == 'r' )
        Send_States(green_box_state);
      goed = false;

      if (incomingByte[0] == 'g' && incomingByte[1] == 'e' )
        Send_States(yellow_box_state);
      goed = false;
    }
  }


    network.update();
    // ontvang knop data


    while ( network.available() ) {     // Is there any incoming data?
      RF24NetworkHeader header;
      bool buttonState;
      network.read(header, &buttonState, sizeof(buttonState)); // Read the incoming data
      //Serial.println(buttonState);  //zend data naar applicatie

      if (buttonState) {
        Serial.println(buttonState);  //zend data naar applicatie

      }


    }

    //unsigned long sendByte = digitalRead(incomingByte);
    //Serial.println("loop end");
    //delay(2000);
  }

//---functie:_Send_States----------------------------------------------------------------------------------------------------------------
void Send_States(int right_box) {
  RF24NetworkHeader header1(other_node);     // (Address where the data is going)
  bool ok4 = network.multicast(header1, &right_box, sizeof(right_box), 1); // Send the data

}
