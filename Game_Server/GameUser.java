package Game_making;

import java.net.*;
import java.io.*;

public class GameUser {
	private String id = null;
	private GameRoom room = null;
	private Socket sock = null;
	private String nickName = null;
	private int[] gameInfo = new int[6];
	private boolean inMainRoom = true;
	private OutputStream oStream = null;
	
	private PlayerGameInfo playGameInfo = new PlayerGameInfo();
	
	public GameUser() {// 더미 유저 생성 (메인 대기방 유지용)
	}
	
	public GameUser(String ID, byte[] PW, Socket socket, OutputStream os) throws Exception {
		this.id = ID;
		this.nickName = User.get_nickname(ID);
		this.gameInfo = User.get_gameInfo(ID);
		this.sock = socket;
		this.oStream = os;
	}
	
	/////////// 회원가입/로그인은 상위에서 구현하는걸로~
	
	// 방에 입장 (얘가 단독으로 실행되는 경우는 없다.) >> GameRoom에서 활용되는 함수
	public void enterRoom(GameRoom room) throws IOException {
		this.room = room;
		
		if (this.sock == null) return;
		if (room.getRoomID() == 1) {
			this.inMainRoom = true;
			if (this.sock != null) {
				oStream.write("700 1\n".getBytes());
				oStream.flush();
			}
		}
		else {
			this.inMainRoom = false;
			if (this.sock != null) {
				oStream.write(("705 " + String.valueOf(room.getRoomID() + "\n")).getBytes());
				oStream.flush();
			}
		}
	}
	
	// 현재 유저의 OutputStream을 반환한다.
	public OutputStream getUserOutputStream() {
		return this.oStream;
	}
	
	// 얘도 단독으로 실행되는 경우는 없다. >> GameRoom에서 활용되는 함수
	public void exitRoom(GameRoom room) throws IOException {
		this.room = null;

		if (this.sock != null) {
			if (this.inMainRoom) {
				oStream.write("710\n".getBytes());
				oStream.flush();
			}
			else {
				oStream.write("715\n".getBytes());
				oStream.flush();
			}
		}
	}
	
	// 현재 메인 대기방에 있다면: true 반환, 아니라면: false 반환
	public boolean whichRoom() {
		return this.inMainRoom;
	}
	
	public void InitializeGame() {
		this.playGameInfo.InitializeGame();
	}
	
	// flag: 0 >> 내 턴, flag: 1 >> 상대 턴
	// 선택이 반영되었으면 true, 아니면 false를 반환한다.
	public boolean setT3(int flag, int x, int y) throws IOException {
		boolean check = this.playGameInfo.setT3(flag, x, y);
		
		if (check && flag == 0) {
			oStream.write("600\n".getBytes());
			oStream.flush();
			return true;
		}
		else if (!check && flag == 0) {
			oStream.write("605\n".getBytes());
			oStream.flush();
		}
		
		return false;
	}
	
	//flag: 0 >> 내 턴, flag: 1 >> 상대 턴
	//0 >> 가위, 1 >> 바위, 2 >> 보
	public void setRCP(int flag, int pick) {
		this.playGameInfo.setRCP(flag, pick);
	}
	
	// true 반환: 결과가 나왔으니 게임 종료, false 반환: 결과나 아직 안나왔으니 게임 유지
	public boolean getT3Result() throws IOException {
		int result = this.playGameInfo.T3Result(this.getID());
		
		if (result == 3) return false;
		else if (result == 0) oStream.write("800\n".getBytes());
		else if (result == 1) oStream.write("801\n".getBytes());
		else oStream.write("802\n".getBytes());
		
		oStream.write("810\n".getBytes());
		oStream.flush();
		return true;
	}
	
	// true 반환: 결과가 나옴, false 반환: 결과 아직임
	public boolean getRCPResult() throws IOException {
		int result = this.playGameInfo.RCPResult(this.getID());
		
		if (result == 0) return false;
		else if (result == 0) oStream.write("800\n".getBytes());
		else if (result == 1) oStream.write("801\n".getBytes());
		else if (result == 2) oStream.write("802\n".getBytes());
		
		oStream.write("810\n".getBytes());
		oStream.flush();
		return true;
	}
	
	public GameUser getUserbyNickname(String nickname) {
		if (this.nickName.equals(nickname)) return this;
		else return null;
	}
	
	public String getID() {
		return this.id;
	}
	
	public GameRoom getRoom() {
		return this.room;
	}
	
	public Socket getSock() {
		return this.sock;
	}
	
	public String getNickname() {
		return this.nickName;
	}
	
	public int[] getGameInfo() {
		return this.gameInfo;
	}
}


















