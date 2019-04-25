package sec3.chat.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ChatClientExmaple {
	public final static int PORT = 5001;

	String ip;
	String userid;

	Socket server;
	ObjectInputStream ois;
	ObjectOutputStream oos;
	BufferedReader read;
	String sendData;
	String receiveData;


	ChatClientExmaple(String userid, String ip){  // 생성할때 id와 ip값을 받는다.
		this.userid=userid;
		this.ip=ip;

		try {
			System.out.println("****** 클라이언트 ******");
			System.out.println("연결 요청 : "+ip+" : "+PORT);
			server = new Socket(); //소켓생성후					
			server.connect(new InetSocketAddress(ip,PORT));  // 반환타입이 socket인 connect 메서드를이용해 ip값과PORT값으로 server에 연결한다.
			System.out.println("연결 성공");

			read = new BufferedReader(new InputStreamReader(System.in)); // 콘솔로 입력받는다.

			ois = new ObjectInputStream(server.getInputStream());		// 받는 연결 통로 생성
			oos = new ObjectOutputStream(server.getOutputStream());	// 내보내는 연결 통로 생성
			oos.writeObject(userid);			//ChatClientExmaple 생성자를 통해 만들어진 userid를 연결된 서버에 전송한다.
			oos.flush();							// 버퍼를비운다.

			ReceiveDataThread rdt = new ReceiveDataThread(server,ois); //Runnable을 implements하는 ReceiveDataThread객체생성
			Thread t = new Thread(rdt);											//↘매게변수로 socket과 서버와연결된ois를 받는다.
			t.start();

			while(true) {
				sendData = read.readLine();
				oos.writeObject(sendData);
				oos.flush();
				if(sendData.trim().equals("/quit")) {
					t.interrupt();
					break;
				}
			}
		} catch (Exception e) {}
		finally {
			try {
				if(server !=null && !server.isClosed()) {
					ois.close();
					oos.close();
					read.close();
					server.close();
					System.out.println("종료");
					System.exit(0);
				}
			} catch (IOException e) {
			}
		}
	}
	public static void main(String[] args) {
		//	id , 연결 ip
		new ChatClientExmaple("수박","192.168.0.119");
	}
}
