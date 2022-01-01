package Game_making;

import java.io.*;
import java.net.*;
import java.util.*;

public class Main extends Thread {
	private Socket sock;// 현재 조작 소켓
	private static ArrayList<Socket> clients = new ArrayList<Socket>(10);// 소켓 리스트
	
	public Main(Socket sock) {
		this.sock = sock;
	}
	
	public void remove(Socket socket) {
		for (Socket s : Main.clients)
			if (socket == s) {
				Main.clients.remove(socket);
				break;
			}
	}
	
	public void run() {// 채팅 구현
		InputStream fromClient = null;
		OutputStream toClient = null;
		
		try {/////////////////현재 socket이 닫히는 현상을 디버깅하는중... stream을 닫는게 문제인듯하다./////////////
			System.out.println(sock + ": 연결됨");
			
			int count = 0;
			byte[] buf = new byte[1502];
			
			fromClient = sock.getInputStream();
			toClient = sock.getOutputStream();		
			
			while ((count = fromClient.read(buf)) != -1) {
				String temp = new String(buf);
				String temp_new = temp.substring(0, temp.indexOf('\n'));
				String[] find_protocol = temp_new.split(" ", 2);
				int protocol = Integer.parseInt(find_protocol[0]);
				
				System.out.println(temp);
				System.out.println(temp.indexOf('\n'));
				
				if (protocol == 100) {// 완료
					System.out.println("100 from client");
					String[] login_info = find_protocol[1].split(" ", 2);
					Operations.Login(login_info[0], login_info[1].getBytes(), sock, toClient);
				}
				else if (protocol == 105) {// 완료
					System.out.println("105 from client");
					String[] join_info = find_protocol[1].split(" ", 6);
					Operations.Join(join_info[0], join_info[1], join_info[2], join_info[3].getBytes(), join_info[4].getBytes(), join_info[5], sock, toClient);
				}
				else if (protocol == 200) {// 완료
					System.out.println("200 from client");
					String[] chat_info = find_protocol[1].split(" ", 2);
					Operations.Chat(Integer.parseInt(chat_info[0]), chat_info[1].getBytes(), sock);
				}
				else if (protocol == 300) {// 완료
					System.out.println("300 from client");
					String getUserInfo_info = find_protocol[1];
					Operations.getAllInfo(Integer.parseInt(getUserInfo_info), sock);
				}
				else if (protocol == 400) {// 완료
					System.out.println("400 from client");
					String game2main_info = find_protocol[1];
					Operations.gameRoom2mainRoom(Integer.parseInt(game2main_info), sock);
				}
				else if (protocol == 500) {// 완료
					System.out.println("500 from client");
					String[] inviting_info = find_protocol[1].split(" ", 2);
					Operations.giveInviteMessage(inviting_info[0], inviting_info[1]);
				}
				else if (protocol == 505) {// 완료
					System.out.println("505 from client");
					String[] invited_info = find_protocol[1].split(" ", 3);
					if (Operations.getInviteResult(invited_info[1], invited_info[2], Integer.parseInt(invited_info[0]))) {
						Operations.mainRoom2gameRoom(invited_info[1], invited_info[2]);
					}
				}
				else if (protocol == 600) {// 완료
					System.out.println("600 from client");
					String currentRoomName_info = find_protocol[1];
					Operations.currentRoomName(Integer.parseInt(currentRoomName_info), sock);
				}
				else if (protocol == 605) {// 완료
					System.out.println("605 from client");
					String[] renameRoom_info = find_protocol[1].split(" ", 2);
					Operations.changeRoomName(Integer.parseInt(renameRoom_info[0]), renameRoom_info[1], sock);
				}
				else if (protocol == 700) {// 완료
					System.out.println("700 from client");
					String[] startGame_info = find_protocol[1].split(" ", 3);
					Operations.startGame(Integer.parseInt(startGame_info[0]), startGame_info[1], startGame_info[2]);
				}
				else if (protocol == 701) {// 완료
					System.out.println("701 from client");
					String[] doT3_info = find_protocol[1].split(" ", 5);
					Operations.playT3Game(Integer.parseInt(doT3_info[0]), doT3_info[1], doT3_info[2], Integer.parseInt(doT3_info[3]), Integer.parseInt(doT3_info[4]));
				}/******************
				개발 중간에 클라이언트 측의 요청으로 개발 범위를 축소하였습니다.
				그 과정에서 가위바위보에 대한 구현을 중단하였으며, 디버깅을 수행하지 않았습니다.
				따라서, 가위바위보 관련 코드에 오류가 있을 수 있습니다.
				else if (protocol == 702) {
					System.out.println("702 from client");
					String[] doRCP_info = find_protocol[1].split(" ", 4);
					Operations.playRCPGame(Integer.parseInt(doRCP_info[0]), doRCP_info[1], doRCP_info[2], Integer.parseInt(doRCP_info[3]));
				}**************************/
				else if (protocol == 808) {// 완료
					System.out.println("808 from client");
					String exitGame_info = find_protocol[1];
					Operations.exit(Integer.parseInt(exitGame_info), sock);
				}
				else {// 완료
					System.out.println("else from client");
					toClient.write("2000\n".getBytes());
					toClient.flush();
				}
			}
		} catch(Exception ex) {
			System.out.println(sock + ": ERROR(" + ex + ")");
		} finally {
			try {
				if (sock != null) {// 클라이언트가 갑자기 종료한 경우
					sock.close();// 종료한 소켓 닫기
					remove(sock);// ArrayList에서 종료된 소켓 제거
				}
				fromClient.close();
				fromClient = null;
				toClient.close();
				toClient = null;
			} catch(IOException ex) {
				System.out.println(ex);
			}
		}
	}
	
	public static void main(String[] args) throws IOException {
		ServerSocket serverSock = new ServerSocket(9090);
		System.out.println(serverSock + ": 서버소켓생성");
		
		Operations.makeMainRoom();// 초기 메인 룸 생성 및 더미 유저 생성
		
		while (true) {
			Socket client = serverSock.accept();// TCP 연결 생성
			clients.add(client);// 소켓 리스트에 새로운 연결을 추가
			
			Main myServer = new Main(client);// 현재 조작 소켓 지정
			myServer.start();// run() 실행
		}
	}
}