#include <Timer.h>
#include "BlinkToRadio.h"
#include "Oscilloscope.h"
 
 module BlinkToRadioC {
   uses interface Boot;
   uses interface Leds;
   uses interface Timer<TMilli> as Timer0;
   uses interface Packet;
   uses interface AMPacket;
   uses interface SplitControl as AMControl;
   uses interface Receive;
   uses  interface Mts300Sounder;
 }
 
 
 implementation {
   uint16_t value=0;
   bool busy = FALSE;
   message_t pkt;
 
   event void Boot.booted() {
     call AMControl.start();
   }

   event void AMControl.startDone(error_t err) {
   call Leds.led2Toggle();
  }  



event message_t* Receive.receive(message_t* msg, void* payload, uint8_t len) {
  if (len == sizeof(BlinkToRadioMsg)) {
    BlinkToRadioMsg* btrpkt = (BlinkToRadioMsg*)payload;
    value = btrpkt->counter;
    if (value == 4){
     call Leds.led1On();
      call Leds.led2Off();
      call Timer0.startOneShot(5000);

  }
}
return msg;
}

event void Timer0.fired(){
           call Mts300Sounder.beep(3000);}

  event void AMControl.stopDone(error_t err) {
  }
 }
