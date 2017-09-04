package com.co.wno.etalk.client.serial;

//import com.pi4j.io.gpio.exception.UnsupportedBoardType;

import com.pi4j.io.serial.*;
import com.pi4j.util.Console;

import java.io.IOException;

//import java.io.BufferedReader;
//import java.io.InputStreamReader;

/**
 * This example program supports the following optional command arguments/options:
 *   "--device (device-path)"                   [DEFAULT: /dev/ttyAMA0]
 *   "--baud (baud-rate)"                       [DEFAULT: 38400]
 *   "--data-bits (5|6|7|8)"                    [DEFAULT: 8]
 *   "--parity (none|odd|even)"                 [DEFAULT: none]
 *   "--stop-bits (1|2)"                        [DEFAULT: 1]
 *   "--flow-control (none|hardware|software)"  [DEFAULT: none]
 */
public class SerialComm {
	
	public final Serial serial = SerialFactory.createInstance();
	public final Console console = new Console();
//	private static boolean verbose = true;
//	private static boolean getVerbose() { return verbose; }

	public SerialComm() { try {
		initComp();
	} catch (InterruptedException e) {
		e.printStackTrace();
	} }

	private void initComp() throws InterruptedException {
		
		console.title("<-- The Pi4J Project -->", "Serial Communication Example");
		console.promptForExit();

	    try {
	        // create serial config object
	        SerialConfig config = new SerialConfig();
	
	        // set default serial settings (device, baud rate, flow control, etc)
	        //
	        // by default, use the DEFAULT com port on the Raspberry Pi (exposed on GPIO header)
	        // NOTE: this utility method will determine the default serial port for the
	        //       detected platform and board/model.  For all Raspberry Pi models
	        //       except the 3B, it will return "/dev/ttyAMA0".  For Raspberry Pi
	        //       model 3B may return "/dev/ttyS0" or "/dev/ttyAMA0" depending on
	        //       environment configuration.
	        config.device("/dev/ttyACM0")
	              .baud(Baud._115200)
	              .dataBits(DataBits._8)
	              .parity(Parity.NONE)
	              .stopBits(StopBits._1)
	              .flowControl(FlowControl.NONE);
	
	        // parse optional command argument options to override the default serial settings.
	        //if(args.length > 0){
	        //    config = CommandArgumentParser.getSerialConfig(config, args);
	        //}
	
	        // display connection details
	        console.box(" Connecting to: " + config.toString(),
	                " We are sending ASCII data on the serial port every 1 second.",
	                " Data received on serial port will be displayed below.");
	
	
	        // open the default serial device/port with the configuration settings
	        serial.open(config);

	        if(serial.isOpen()) console.println("Serial Open");
//
//	        // continuous loop to keep the program running until the user terminates the program
//	        while(console.isRunning()) {
//	            try {
//	                // write a formatted string to the serial transmit buffer
//	                serial.write("CURRENT TIME: " + new Date().toString());
//
//	                // write a individual bytes to the serial transmit buffer
//	                serial.write((byte) 13);
//	                serial.write((byte) 10);
//
//	                // write a simple string to the serial transmit buffer
//	                serial.write("Second Line");
//
//	                // write a individual characters to the serial transmit buffer
//	                serial.write('\r');
//	                serial.write('\n');
//
//	                // write a string terminating with CR+LF to the serial transmit buffer
//	                serial.writeln("Third Line");
//	            }
//	            catch(IllegalStateException ex){
//	                ex.printStackTrace();
//	            }
//
//	            // wait 1 second before continuing
//	            Thread.sleep(1000);
//	        }
	    }
	    catch(IOException ex) {
	        console.println(" ==>> SERIAL SETUP FAILED : " + ex.getMessage());
		}

	// END SNIPPET: serial-snippet
	    		 	
	}
	



	}
