package Game_making;

import java.io.*;
import java.net.*;
import java.util.*;

public class Operations {
	// 서버가 처음 켜질때 실행되는 초기화 동작. 메인 대기방 생성 및 유지를 담당한다.
	public static void makeMainRoom() throws IOException {
		GameUser dummyUser = new GameUser();
		RoomManager.createRoom(dummyUser);
	}
	
	// 로그인하고, 성공하면 Main room에 진입한다.
	// 관련 프로토콜은 'Protocol.txt'를 참조하자!
	public static void Login(String ID, byte[] PW, Socket socket, OutputStream oStream) throws Exception {
		if (User.get_User(ID, PW)) {
			oStream.write("100\n".getBytes());
			oStream.flush();
			GameUser newUser = new GameUser(ID, PW, socket, oStream);
			RoomManager.enterMain(newUser);
			return;
		}
		
		oStream.write("105\n".getBytes());
		oStream.flush();
	}
	
	// 회원가입을 한다.
	public static void Join(String name, String nickname, String ID, byte[] PW, byte[] PW_re, String email, Socket socket, OutputStream oStream) throws Exception {
		if (!java.util.Arrays.equals(PW, PW_re)) oStream.write("210\n".getBytes());
		else if (User.set_User(name, nickname, ID, PW, email)) oStream.write("200\n".getBytes());
		else oStream.write("205\n".getBytes());
	}
	
	// 채팅을 지원한다.
	public static void Chat(int roomID, byte[] data, Socket socket) throws IOException {
		GameRoom chatPlace = RoomManager.getRoomByID(roomID);
		chatPlace.broadcast(data, socket);
	}
	
	// 모든 유저 정보를 제공받는다. (2인 대기방의 경우, 2명의 정보만을 사용하면 된다.)
	// 유저가 Timeout을 정해놓고 계속 호출해야한다. ex) 10초에 1회씩 시행한다던가...?
	// 그 이외에도 필요하다고 생각되는 경우에는 클라이언트가 호출해야한다.
	public static void getAllInfo(int roomID, Socket socket) throws IOException {
		GameRoom currentRoom = null;
		List<GameUser> Info = new ArrayList<GameUser>();
		
		if (roomID == 1) {
			Info.addAll(RoomManager.getAllUser_RM());
			currentRoom = RoomManager.getRoomByID(roomID);
			currentRoom.userInfoBroadcast(Info, socket);
			return;
		}
		
		currentRoom = RoomManager.getRoomByID(roomID);
		Info.addAll(currentRoom.getAllUser_inRoom());
		currentRoom.userInfoBroadcast(Info, socket);
	}
	
	// 2인 대기방에서 메인 대기방으로 이동할 때 사용한다.
	// 1명이라도 나가면 방이 폭파
	public static void gameRoom2mainRoom(int roomID, Socket socket) throws IOException {
		if (roomID == 1) return;

		GameUser user = null;
		for (GameUser U : RoomManager.getRoomByID(roomID).getAllUser_inRoom())
			if (U.getSock() != null && U.getSock() == socket) {
				user = U;
				break;
			}

		RoomManager.gameRoom2mainRoom(user);
	}
	
	
	
	// invited_nickname의 닉네임을 가진 유저에게 초대 메시지를 전송
	public static void giveInviteMessage(String invite_nickname, String invited_nickname) throws IOException {
		List<GameUser> userInMain = RoomManager.getRoomByID(1).getAllUser_inRoom();
		GameUser invitedUser = null;
		
		for (GameUser u : userInMain)
			if (u.getSock() != null && u.getNickname().equals(invited_nickname))
				invitedUser = u;
		
		invitedUser.getUserOutputStream().write(("400 " + invite_nickname + "\n").getBytes());
		invitedUser.getUserOutputStream().flush();
	}
	
	// 위의 giveInviteMessage에서의 결과를 보여준다.
	// true면 수락, false면 거절한 것이다.
	// flag: 0 >> 수락, flag: 1 >> 거절
	public static boolean getInviteResult(String invite_nickname, String invited_nickname, int flag) throws IOException {
		List<GameUser> userInMain = RoomManager.getRoomByID(1).getAllUser_inRoom();// 현재 메인 룸에 있는 유저들
		GameUser inviteUser = null;

		for (GameUser u : userInMain)
			if (u.getSock() != null && u.getNickname().equals(invite_nickname)) {
				inviteUser = u;
				break;
			}

		if (flag == 0) {
			inviteUser.getUserOutputStream().write("300\n".getBytes());
			inviteUser.getUserOutputStream().flush();
			return true;
		}

		inviteUser.getUserOutputStream().write("305\n".getBytes());
		inviteUser.getUserOutputStream().flush();
		return false;
	}
	
	// getInviteResult가 true를 반환할 때에만 사용할 수 있다.(따로 안전장치 없으므로 주의)
	public static void mainRoom2gameRoom(String invite_nickname, String invited_nickname) throws IOException {
		List<GameUser> userInMain = RoomManager.getRoomByID(1).getAllUser_inRoom();
		GameUser inviteUser = null;
		GameUser invitedUser = null;
		
		for (GameUser u : userInMain)
			if (u.getSock() != null && u.getNickname().equals(invite_nickname))
				inviteUser = u;
			else if (u.getSock() != null && u.getNickname().equals(invited_nickname))
				invitedUser = u;
		
		RoomManager.getRoomByID(1).exitUser(invitedUser);
		RoomManager.getRoomByID(1).exitUser(inviteUser);
		
		GameRoom newRoom = RoomManager.createRoom(inviteUser);
		newRoom.enterUser(invitedUser);
	}
	
	// 현재 방의 이름을 반환한다.
	public static void currentRoomName(int roomID, Socket socket) throws IOException {
		String roomName = RoomManager.getRoomByID(roomID).getRoomName();
		GameUser currentUser = null;
		for (GameUser u : RoomManager.getRoomByID(roomID).getAllUser_inRoom())
			if (u.getSock() != null && u.getSock() == socket) {
				currentUser = u;
				break;
			}
		currentUser.getUserOutputStream().write(("1010 " + roomName + "\n").getBytes());
		currentUser.getUserOutputStream().flush();
	}
	
	// 방의 이름을 변경한다.
	public static void changeRoomName(int roomID, String newRoomName, Socket sock) throws IOException {
		GameUser ownerUser = null;
		GameRoom room = RoomManager.getRoomByID(roomID);
		for (GameUser u : room.getAllUser_inRoom())
			if (u.getSock() != null && u == room.getOwner()) {
				ownerUser = u;
				break;
			}
		
		if (ownerUser.getSock() != sock) return;
		
		room.setRoomName(newRoomName);
	}
	
	// 게임을 시작할 때 호출하는 초기화 함수
	public static void startGame(int roomID, String myNickname, String yourNickname) throws IOException {
		List<GameUser> allUsers = RoomManager.getRoomByID(roomID).getAllUser_inRoom();
		GameUser me = null;
		GameUser you = null;

		for (GameUser u : allUsers)
			if (u.getSock() != null && u.getNickname().equals(myNickname)) me = u;
			else if (u.getSock() != null && u.getNickname().equals(yourNickname)) you = u;

		me.InitializeGame();
		you.InitializeGame();

		me.getUserOutputStream().write("650\n".getBytes());
		me.getUserOutputStream().flush();
		
		you.getUserOutputStream().write("650\n".getBytes());
		you.getUserOutputStream().flush();
	}
	
	// ttt게임을 한다.
	// 기본적으로 owner가 우선권을 가진다. (클라이언트에서 처리해줘야 한다.)
	public static void playT3Game(int roomID, String myNickname, String yourNickname, int x, int y) throws IOException {
		List<GameUser> allUsers = RoomManager.getRoomByID(roomID).getAllUser_inRoom();
		GameUser me = null;
		GameUser you = null;
		
		for (GameUser u : allUsers)
			if (u.getSock() != null && u.getNickname().equals(myNickname)) me = u;
			else if (u.getSock() != null && u.getNickname().equals(yourNickname)) you = u;
		
		boolean check = me.setT3(0, x, y);
		you.setT3(1, x, y);
		if (check) {
			you.getUserOutputStream().write("610\n".getBytes());
			you.getUserOutputStream().flush();
		}
		
		if (me.getT3Result()) you.getT3Result();
	}
	
	// RCP게임을 한다.
	public static void playRCPGame(int roomID, String myNickname, String yourNickname, int pick) throws IOException {
		List<GameUser> allUsers = RoomManager.getRoomByID(roomID).getAllUser_inRoom();
		GameUser me = null;
		GameUser you = null;
		
		for (GameUser u : allUsers)
			if (u.getNickname().equals(myNickname)) me = u;
			else if (u.getNickname().equals(yourNickname)) you = u;
		
		me.setRCP(0, pick);
		you.setRCP(1, pick);
		
		if (me.getRCPResult()) you.getRCPResult();
	}
	
	// 게임을 종료한다. (메인 대기방에서만 가능하다)
	public static void exit(int roomID, Socket socket) throws IOException {
		if (roomID != 1) return;
		
		List<GameUser> mainUser = RoomManager.getRoomByID(roomID).getAllUser_inRoom();
		GameUser exitUser = null;
		
		for (GameUser u : mainUser)
			if (u.getSock() == socket) {
				exitUser = u;
				break;
			}

		User.set_Unconnected(exitUser.getID());
		RoomManager.getRoomByID(roomID).exitUser(exitUser);
		socket.close();
		exitUser = null;
	}
}








