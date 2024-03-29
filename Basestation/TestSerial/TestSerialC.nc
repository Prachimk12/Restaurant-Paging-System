// $Id: TestSerialC.nc,v 1.7 2010-06-29 22:07:25 scipio Exp $

/*									tab:4
 * Copyright (c) 2000-2005 The Regents of the University  of California.  
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * - Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the
 *   distribution.
 * - Neither the name of the University of California nor the names of
 *   its contributors may be used to endorse or promote products derived
 *   from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Copyright (c) 2002-2003 Intel Corporation
 * All rights reserved.
 *
 * This file is distributed under the terms in the attached INTEL-LICENSE     
 * file. If you do not find these files, copies can be found by writing to
 * Intel Research Berkeley, 2150 Shattuck Avenue, Suite 1300, Berkeley, CA, 
 * 94704.  Attention:  Intel License Inquiry.
 */

/**
 * Application to test that the TinyOS java toolchain can communicate
 * with motes over the serial port. 
 *
 *  @author Gilman Tolle
 *  @author Philip Levis
 *  
 *  @date   Aug 12 2005
 *
 **/

#include "Timer.h"
#include "TestSerial.h"
#include "BlinkToRadio.h"

module TestSerialC {
  uses {
    interface SplitControl as Control;
    interface Leds;
    interface Boot;
    interface Timer<TMilli> as Timer0;
    interface Packet;
    interface AMPacket;
    interface AMSend;
    interface SplitControl as AMControl;
   interface Receive;
  }

implementation {

  message_t packet;
  bool locked = FALSE;
  uint16_t counter = 0, value;
  bool busy = FALSE;
  message_t pkt;
  
//Call the start of the Radio and the UART upon boot up
  event void Boot.booted() {
    call AMControl.start();
    call Control.start();
  }
  
//Start the Radio
  event void AMControl.startDone(error_t err) {
  }  

//Start the UART
  event void Control.startDone(error_t err) {
  }

//The receive event that will get triggered whne its receives a message from the Base Station and turn on the LEDs that 
//correspond to the last three bits of the Node ID received from Base Station. 

  event message_t* Receive.receive(message_t* bufPtr, 
				   void* payload, uint8_t len) {

      test_serial_msg_t* rcm = (test_serial_msg_t*)payload;
      if (rcm->counter & 0x1) {
	call Leds.led0On();
      }
      else {
	call Leds.led0Off();
      }
      if (rcm->counter & 0x2) {
	call Leds.led1On();
      }
      else {
	call Leds.led1Off();
      }
      if (rcm->counter & 0x4) {
	call Leds.led2On();
      }
      else {
	call Leds.led2Off();
      }
      value = rcm->counter;
      call Timer0.startOneShot(1000);  //call the timer 0 to start the sending process

      return bufPtr;
  }

//Event that gets fired after 1 second and starts the sending process
event void Timer0.fired() { 
    BlinkToRadioMsg* btrpkt = (BlinkToRadioMsg*)(call Packet.getPayload(&pkt, sizeof (BlinkToRadioMsg)));
    btrpkt->nodeid = TOS_NODE_ID;
    btrpkt->counter1 = value;
    call AMSend.send(AM_BROADCAST_ADDR, &pkt, sizeof(BlinkToRadioMsg));
    } 


//After the data has been sent, all the LED's should turn OFF. 
  event void AMSend.sendDone(message_t* msg, error_t error) {
     
     call Leds.led0Off();
     call Leds.led1Off();
     call Leds.led2Off(); 

  }

//Stop the Radio
  event void AMControl.stopDone(error_t err) {
  }


//Stop the UART
  event void Control.stopDone(error_t err) {
  }


}




