package sec3.chat.server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ChatServerExample {
	public static final String IP = "192.168.0.179";
	public static final int PORT=5001;
	
	ServerSocket socket;
	Socket client;
	HashMap<String,ObjectOutputStream> hm;
	ExecutorService serverPool;
	
	public ChatServerExample() {
		
		try {
			serverPool = Executors.newFixedThreadPool(25);
			socket = new ServerSocket();
			socket.bind(new InetSocketAddress(IP,PORT));
			
		} catch (IOException e) {}
		
		System.out.println("******************** 채팅 서버 오픈 ********************");
		hm = new HashMap<>();  	// HashMap 생성
		
		while(true) {
			try {
				System.out.println("클라이언트 연결을 기다리는중....");
				client = socket.accept();  												//	socket을 받아와서 client에 대입.
				ServerThread chatThread = new ServerThread(client,hm); 	// ServerThread 생성자 실행
				serverPool.submit(chatThread);								//Thread Pool에 Runnable 객체인 chatThread을 넣어서 실행
				
			} catch (IOException e) {
				try {
					serverPool.shutdown();
					if(socket !=null && !socket.isClosed()) {
						socket.close();
					}
				} catch (IOException e1) {}
			break;
			}
		}
		System.out.println("서버 종료");
		
	}

	public static void main(String[] args) {
		new ChatServerExample();
		
	}

}
