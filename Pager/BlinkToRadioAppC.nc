 #include <Timer.h>
 #include "BlinkToRadio.h"
 #include "Oscilloscope.h"
 
 configuration BlinkToRadioAppC {
 }
 implementation {
   components MainC;
   components LedsC;
   components SounderC,new DemoSensorC() as Sensor;
   components BlinkToRadioC as App;
   components new TimerMilliC() as Timer0;
   components ActiveMessageC;
   components new AMReceiverC(AM_BLINKTORADIO);
   
   App.Receive -> AMReceiverC;
   App.Boot -> MainC;
   App.Leds -> LedsC;
   App.Timer0 -> Timer0;
   App.AMControl -> ActiveMessageC;
   App.Mts300Sounder -> SounderC;

 }
 

