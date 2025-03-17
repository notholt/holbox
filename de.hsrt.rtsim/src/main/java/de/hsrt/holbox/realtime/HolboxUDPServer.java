package de.hsrt.holbox.realtime;

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.time.Instant;
import java.util.List;

import de.hsrt.holbox.importExport.ImportWorkshopData;
import de.hsrt.holbox.playground.HybridPowerPlantExample;
import de.hsrt.holbox.util.*;


public class HolboxUDPServer {
	
	private int listenPort = 7121;
	private int sendPort = 	7122;
	
	private DatagramSocket 	listeningSocket;
	private InetAddress 	clientAdr;
	
	private byte[] inBuf = new byte[256];
	private byte[] outBuf = new byte[256];
	
	private DatagramSocket sendingSocket;
	
	private List<Signal> inputs;
	private List<Signal> outputs;
	private long requestedTimeOffsetMs;
	

	

	private void setupServer(List<Signal> inputs) throws SocketException
	{
		this.inputs = inputs;
		this.listeningSocket = new DatagramSocket(listenPort);
		this.listeningSocket.setSoTimeout(10000);
	}
	
	private void setupClient(List<Signal> outputs) throws SocketException
	{
		this.outputs = outputs;
		this.sendingSocket = new DatagramSocket();
	}
	
	private void listen() throws Exception
	{
		int length = 0;
		int signals = this.inputs.size();
		DatagramPacket packet 
        = new DatagramPacket(inBuf, inBuf.length);
		
		try {
			listeningSocket.receive(packet);
			
			clientAdr = packet.getAddress();
			length = packet.getLength();
			
			if(length != (signals+1)*4)
			{
				throw new Exception("Malformed packet, expecting " + (signals+1)*4 + "bytes but found only " + length + "!");
			}
			else
			{
				requestedTimeOffsetMs = getInt32(inBuf, 0);
				for(int j = 0; j<signals; j++)
				{
					inputs.get(j).setValue(getInt32(inBuf, 4 + j*4)/1000.0);
				}
			}
		}catch(Exception e)
		{
			System.out.println(e.getLocalizedMessage());
		}
		
		

	}
	
	int getInt32(byte[] bytes, int idx) {
	     return ((bytes[idx+3] & 0xFF) << 24) | 
	            ((bytes[idx+2] & 0xFF) << 16) | 
	            ((bytes[idx+1] & 0xFF) << 8 ) | 
	            ((bytes[idx+0] & 0xFF) << 0 );
	}
	
	void setInt32InBuf(int val, byte[] bytes, int idx)
	{
		bytes[idx+3] = (byte)(val >> 24);
		bytes[idx+2] = (byte)(val >> 16);
		bytes[idx+1] = (byte)(val >> 8);
		bytes[idx+0] = (byte)val;
	}
	
	private void send() throws Exception
	{
		int i=0;
		for(i=0; i<outputs.size(); i++)
		{
			setInt32InBuf((int)(outputs.get(i).getValue()*1000.0), outBuf, i*4);
		}
		DatagramPacket packet 
        = new DatagramPacket(outBuf, i*4, clientAdr, sendPort);
		this.sendingSocket.send(packet);
	}
	
	public static void main(String[] args) {
		
		HolboxUDPServer srv = new HolboxUDPServer();
		Dataset inputDataset = new Dataset();
		
		ImportWorkshopData.appendHourlyData("C:\\holboxPrj\\workshop\\MessDatenWorkshop.csv", inputDataset);
		HybridPowerPlantExample sys;
		
		
		try
		{
			sys = new HybridPowerPlantExample(inputDataset);
			sys.setup();
			srv.setupServer(sys.getControlSignals());
			srv.setupClient(sys.getStatusSignals());		
		}catch(Exception e)
		{
			System.out.println(e.getMessage());
			return;
		}
		
		while(true)
		{
			try {
				srv.listen();
				long ts = inputDataset.getStartTime().toEpochMilli();
				Instant tsI = Instant.ofEpochMilli(ts/1000 + srv.requestedTimeOffsetMs); // TODO: Why divide by 1000????
				sys.run(tsI);
				srv.send();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				//System.out.println(e.getMessage());
				//break;
			}
		}
		
		
	}
	
	

}
