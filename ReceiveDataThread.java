package sec3.chat.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class ReceiveDataThread implements Runnable{
	
	Socket server;
	ObjectInputStream ois;
	String receiveData;
	
	public ReceiveDataThread(Socket s , ObjectInputStream ois) {
		this.server=s;
		this.ois =ois;	
	}
	
	
	@Override
	public void run() {
		try {
			while((receiveData = (String)ois.readObject())!=null) {
				System.out.println(receiveData);
			}
		} catch (Exception e) {
			try {
				if(server != null && !server.isClosed()) {
					server.close();
					ois.close();
				}
			} catch (IOException e1) {}
		}
		
	}
		

	

	

}
