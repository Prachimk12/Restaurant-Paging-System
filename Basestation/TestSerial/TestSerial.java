/*									tab:4
 * Copyright (c) 2005 The Regents of the University  of California.  
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
 * - Neither the name of the copyright holders nor the names of
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
 */

/**
 * Java-side application for testing serial port communication.
 * 
 *
 * @author Phil Levis <pal@cs.berkeley.edu>
 * @date August 12 2005
 */

/*Importing libraries for displaymode linkedlist and scanner */

import java.awt.DisplayMode;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;

import net.tinyos.message.*;
import net.tinyos.packet.*;
import net.tinyos.util.*;

public class TestSerial implements MessageListener {

	private MoteIF moteIF;
	/*Declaring the linkedlist for table 2, 4, 6*/
	static LinkedList<Integer> table_2;
	static LinkedList<Integer> table_4;
	static LinkedList<Integer> table_6;

	public TestSerial(MoteIF moteIF) {
		this.moteIF = moteIF;
		this.moteIF.registerListener(new TestSerialMsg(), this);
	}
	

	/**Sender Function*/
	public void sendPackets(int table_No) {
		TestSerialMsg payload = new TestSerialMsg(); 
		try {
			System.out.println("Message sent to Mote ID " + table_No);
			payload.set_counter(table_No);
			moteIF.send(0, payload);		//The payload variable contains the value to be sent on basestation
			try {Thread.sleep(1000);}
			catch (InterruptedException exception) {}
		}
		catch (IOException exception) {
			System.err.println("Exception thrown when sending packets. Exiting.");
			System.err.println(exception);
		}
	}

	/**Reciever function.*/
	public void messageReceived(int to, Message message) {
		TestSerialMsg msg = (TestSerialMsg)message;
		//System.out.println("Received packet sequence number " + msg.get_counter());
		//System.out.println(msg.toString());
	}

	
	private static void usage() {
		System.err.println("usage: TestSerial [-comm <source>]");
	}
	
	/** This function is used to add entries to the table using Scanner.*/
	public static void table_Entry(int count)
	{
		Scanner sc1 = new Scanner(System.in);

		switch (count) {
		case 2: System.out.println("Enter Mote ID :");
		table_2.add(sc1.nextInt());			//Call for insert in the queue as FIFO
		break;

		case 4: System.out.println("Enter Mote ID :");
		table_4.add(sc1.nextInt());
		break;

		case 6: System.out.println("Enter Mote ID :");
		table_6.add(sc1.nextInt());
		break;

		default: System.out.println("Table not available for"+count);
		break;
		}
		sc1=null;
	}

	/**This function returns id for which the table is allotted.*/
	public static int table_alloted(int table)
	{
		int id = 0;
		Scanner sc3 = new Scanner(System.in);
		System.out.println("Enter password");

		if(sc3.nextLine().equals("admin"))    //Checks for the password
		{

			switch (table) {
			case 2: id=table_2.removeFirst();		//Call to remove or delete from the queue as FIFO
			System.out.println("Table for 2 alloted to Mote ID : " + id);
			break;

			case 4:	id=table_4.removeFirst();
			System.out.println("Table for 4 alloted to Mote ID : " + id);
			break;

			case 6:	id=table_6.removeFirst();
			System.out.println("Table for 6 alloted to Mote ID : " + id);
			break;

			default: System.out.println("No such table alloted");
			break;
			}
		}
		
		else
			System.out.println("Invalid password");
		return id;
	}

	/**This function is used to display the table entries*/
	public static void display()
	{
		System.out.println("Number of customers in queue for a table of 2 are "+table_2.size());  
		System.out.println("Mote ID's of customers wating for table of 2 :"+table_2);

		System.out.println("\nNumber of customers in queue for a table of 4 are "+table_4.size());
		System.out.println("Mote ID's of customers wating for table of 4 :"+table_4);

		System.out.println("\nNumber of customers in queue for a table of 6 are "+table_6.size());
		System.out.println("Mote ID's of customers wating for table of 6 :"+table_6);
	}

	/** Main function which handles all the queries */
	public static void main(String[] args) throws Exception {
		String source = null;
		String pwd = null;
		int val;
		table_2=new LinkedList<>();
		table_4=new LinkedList<>();
		table_6=new LinkedList<>();
		Scanner sc = new Scanner(System.in);

		
		if (args.length == 2) {
			if (!args[0].equals("-comm")) {
				usage();
				System.exit(1);
			}
			source = args[1];
		}
		else if (args.length != 0) {
			usage();
			System.exit(1);
		}

		PhoenixSource phoenix;

		if (source == null) {
			phoenix = BuildSource.makePhoenix(PrintStreamMessenger.err);
		}
		else {
			phoenix = BuildSource.makePhoenix(source, PrintStreamMessenger.err);
		}

		MoteIF mif = new MoteIF(phoenix);
		TestSerial serial = new TestSerial(mif);

		while(true)
		{
			System.out.println("\nPress 1 for Server Menu \nPress 2 for Customer Menu ");
			
			switch (sc.nextInt()) {
			
			/**This is the menu for server.*/
				case 1: System.out.println("Press 1 to add an entry \nPress 2 to allocate table \nPress 3 to display the table entries \nPress 4 to exit");
				switch (sc.nextInt()) {
					
					/**This switch is for the menu options of server.*/
					
					// case 1 - To add an entry.
					case 1:	System.out.println("Enter the number of customers (2/4/6)");
					table_Entry(sc.nextInt());
					break;

					// case 2 - To allocate table.
					case 2:	System.out.println("Enter the number of customers that can be accommodated");
					val = sc.nextInt();
					val = table_alloted(val);
					serial.sendPackets(val);
					break;

					// case 1 - To display all the entries.
					case 3:	display();
					break;

					// case 4 - exit.
					case 4:
					break;
				}
				break;

				case 2: System.out.println("Press 1 to add an entry \nPress 2 to display the table entries \nPress 3 to exit");
				switch (sc.nextInt())
				{
					/**This switch is for the menu options of customer.*/
					
					// case 1 - To add an entry. 
					case 1:	System.out.println("Enter the number of customers (2/4/6)");
					table_Entry(sc.nextInt());
					break;

					// case 2 - To display all entries.
					case 2:	display();
					break;

					// case 3 - exit.
					case 3:
					break;
				}
				break;

				// Case default - If input given other than 1 or 2, then it asks to re-enter. 
				default: System.out.println("Please re-enter");
				break;
			}
		}		
	}
}
