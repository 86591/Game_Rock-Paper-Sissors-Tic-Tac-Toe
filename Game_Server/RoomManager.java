package Game_making;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class RoomManager {
	private static List<GameRoom> roomList;
	private static AtomicInteger atomicInteger;
	
	static {
		roomList = new ArrayList<GameRoom>();
		atomicInteger = new AtomicInteger();
	}
	
	public RoomManager() {
		
	}
	
	// 방장이 방을 생성한다.
	public static GameRoom createRoom(GameUser owner) throws IOException {
		int roomId = atomicInteger.incrementAndGet();
		
		GameRoom room = new GameRoom(roomId);
		room.enterUser(owner);
		room.setOwner(owner);
		room.setDefaultRoomName();
		roomList.add(room);
		return room;
	}
	
	// 빈 방을 제거한다. (GameRoom의 exitUser에서 사용된다.)
	public static void removeEmptyRoom(GameRoom room) {
		roomList.remove(room);
		room = null;
	}
	
	// 유저가 존재하는 방을 제거한다. (2인 대기실에만 적용된다.) + 최종적으로 removeEmptyRoom으로 연계된다.
	public static void removeRoom(GameRoom room) throws IOException {
		if (room.getRoomID() == 1) return;
		for (GameUser U : room.getUserList())
			room.exitUser(U);
	}
	
	public static void enterMain(GameUser user) throws IOException {
		roomList.get(0).enterUser(user);
	}
	
	// 2인 대기방 전용
	// 2인 대기방에서 메인 대기방으로 이동한다.
	// 한 명이 나가면 나머지 하나도 나가진다.
	public static void gameRoom2mainRoom(GameUser user) throws IOException {
		GameRoom currentRoom = user.getRoom();
		List<GameUser> currentMember = null;
		currentMember = currentRoom.getAllUser_inRoom();// 복사본
		
		for (GameUser u : currentMember) {
			if (u == null) continue;
			u.getRoom().exitUser(u);// 현재 2인 룸의 유저를 제거
			roomList.get(0).enterUser(u);// 메인 룸에 해당 유저를 추가
		}
	}
	
	// roomID를 받고 해당하는 GameRoom을 반환한다.
	public static GameRoom getRoomByID(int roomID) {
		for (GameRoom retValue : roomList)
			if (retValue.getRoomID() == roomID) return retValue;
		return null;
	}
	
	// 모든 유저 정보를 반환한다.
	public static List<GameUser> getAllUser_RM(){
		List<GameUser> retVal = new ArrayList<GameUser>();
		
		for (GameRoom r : roomList)
			retVal.addAll(r.getAllUser_inRoom());
		
		return retVal;
	}
}