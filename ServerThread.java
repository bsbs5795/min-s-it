package sec3.chat.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;

public class ServerThread implements Runnable{

	Socket socket;
	HashMap<String,ObjectOutputStream>hm;
	ObjectInputStream ois;
	ObjectOutputStream oos;
	String userid;

	ServerThread(Socket socket,HashMap<String,ObjectOutputStream>hm ){
		this.socket=socket;
		this.hm=hm;

		try {
			System.out.println(socket.getInetAddress()+" 로 부터 연결 요청 받음"); 	 //Client Socket ip주소를 출력
			oos = new ObjectOutputStream(this.socket.getOutputStream());		// 연결길만들기
			ois = new ObjectInputStream(this.socket.getInputStream());			//		``

			userid = (String)ois.readObject();				//아이디 받아와서 userid 에 대입

			broadcast(userid+"님이 입장 하셨습니다.");
			// broadcast 가 HashMap의 value값으로 전달해주기때문에 
			//아직 hm이 생성되기전인 방금연결된 Client에게는 전송되지않는다.
			synchronized(hm) {
				hm.put(userid, oos);		//HashMap에 userid와 socket과 연결된 oos를 추가?
			}									// Thread가 실행중일때 다른 Thread에서 접근하지 못하도록 동기화.
			System.out.println("접속한 클라이언트의 아이디는 : "+userid+"입니다. 방인원 : "+hm.size());
			//내용출력
		} catch (Exception e) {}
	}

	@Override
	public void run() {
		String receiveData;
		//  	/quit 		=>		연결 종료
		//		/to	 id message => 귓속말
		// 		나머지는 전체말
		while(true) {

			try {
				receiveData = (String)ois.readObject(); // Client에서 String type의 Data를 받아와서 receiveDate에 대입.
				if(receiveData.trim().equals("/quit")) {		//receiveData가 /quit일경우 종료.
					break;
				}else if(receiveData.indexOf("/to")> -1) {	// /to 일경우 귓속말을 해주는 sendMsg 실행.
					// 귓속말
					sendMsg(receiveData);					
				}else {											// 둘다아닐경우 전체말을 해주는 broadcast method 실행
					broadcast(userid+" : "+receiveData);
				}
			} catch (Exception e) {}
		}
		synchronized (hm) {
			hm.remove(userid);  //		key값으로 HashMap안의 데이터 하나삭제
		}
		System.out.println(userid+"님이 나가셨습니다.");

	}
	// 메시지 전달
	public void broadcast(String message) {

		try {
			for(ObjectOutputStream oos : hm.values()) { // ObjectOutputStream형태의 hm의 모든vlaue를 호출한다. 
				oos.writeObject(message);				   //  매게변수로받은 message를 모두에게 전송해준다.
				oos.flush();									   // 버퍼를 비워준다.
			}
			System.out.println(message);
		} catch (IOException e) {}
	}

	// 메시지(귓속말)
	//	/to	 id message => 귓속말
	public void sendMsg(String message) {
		int begin = message.indexOf(" ")+1; 	 // id의 첫 시작을 공백+1 [아이디의 시작 index]
		int end = message.indexOf(" ",begin);	 // 공백을 찾는다 begin부터 [아이디의 끝 index]

		if(end != -1) {
			String id =message.substring(begin,end);  //받은 message에서 id를 짜른다 begin 부터 end까지.
			String msg =message.substring(end+1);	 // end두의 문자열은 message에해당 msg로저장
			ObjectOutputStream oos = hm.get(id);	//   그socket의 HashMap key값으로 해당socket의 oos를 불러와 대입.

			try {
				if(oos !=null) {
					oos.writeObject(userid+"님이 귓속말을 보냈어요 : "+msg);
					oos.flush();
				}
			} catch (IOException e) {}
		}
	}
}
