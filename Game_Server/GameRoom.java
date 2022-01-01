package Game_making;

import java.io.*;
import java.util.*;
import java.net.*;

public class GameRoom {
	private int id;
	private List<GameUser> userList;
	private GameUser roomOwner;
	private String roomName;
	// 방 이름은 Room_(roomID) 가 Default이다.
	// 메인 대기방 이름은 Main_Room이 Default이다.
	
	// 아무도 없는 방 생성
	public GameRoom(int roomId) {
		this.id = roomId;
		userList = new ArrayList<GameUser>();
	}
	
	public void enterUser(GameUser user) throws IOException {
		user.enterRoom(this);
		userList.add(user);
		System.out.println("this room id: " + this.id);
	}
	
	// 메인은 더미 유저가 있으므로 없어질 일 없음
	public void exitUser(GameUser user) throws IOException {
		user.exitRoom(this);
		userList.remove(user);
		
		if (userList.size() < 1)
			RoomManager.removeEmptyRoom(this);
	}
	
	// 채팅 (전송 데이터는 프로토콜 참조!)
	public void broadcast(byte[] data, Socket socket) throws IOException {
		byte[] pbuf = "850 ".getBytes();
		byte[] nbuf = null;
		byte[] ebuf = "\n".getBytes();
		
		for (GameUser user : userList) {
			if (user.getSock() != null && user.getSock() == socket) {
				nbuf = (user.getNickname() + " ").getBytes();
				break;
			}
		}
		
		byte[] buf = new byte[pbuf.length + nbuf.length + data.length + ebuf.length];
		System.arraycopy(pbuf, 0, buf, 0, pbuf.length);
		System.arraycopy(nbuf, 0, buf, pbuf.length, nbuf.length);
		System.arraycopy(data, 0, buf, nbuf.length + pbuf.length, data.length);
		System.arraycopy(ebuf, 0, buf, nbuf.length + pbuf.length + data.length, ebuf.length);
		
		for (GameUser user : userList) {
			if (user.getSock() != null && user.getSock() != socket) {
				user.getUserOutputStream().write(buf);
				user.getUserOutputStream().flush();
			}
		}
	}
	
	// 유저 정보 출력 프로그램 (정확히는 유저 리스트를 받고 출력하는 것 뿐이지만...)
	// 본래 브로드캐스트를 하려고 하였기에 이름에 Broadcast가 들어간다. 하지만, 실제로는 전혀 관계가 없다.
	// 유저가 Timeout을 정해놓고 계속 호출해야한다. ex) 10초에 1회씩 시행한다던가...?
	public void userInfoBroadcast(List<GameUser> allUsers, Socket sock) throws IOException {
		for (GameUser U : allUsers) {
			if (U.getSock() == null) continue;
			
			byte[] protocol = "900 ".getBytes();
			byte[] nickName = null;
			byte[] win_rcp = null;
			byte[] lose_rcp = null;
			byte[] total_game_rcp = null;
			byte[] win_t3 = null;
			byte[] lose_t3 = null;
			byte[] total_game_t3 = null;
			
			int[] info = U.getGameInfo();
			
			nickName = (U.getNickname() + " ").getBytes();
			win_rcp = (String.valueOf(info[0]) + " ").getBytes();
			lose_rcp = (String.valueOf(info[1]) + " ").getBytes();
			total_game_rcp = (String.valueOf(info[2]) + " ").getBytes();
			win_t3 = (String.valueOf(info[3]) + " ").getBytes();
			lose_t3 = (String.valueOf(info[4]) + " ").getBytes();
			total_game_t3 = (String.valueOf(info[5]) + "\n").getBytes();
			
			byte[] buf = new byte[protocol.length + nickName.length + win_rcp.length + lose_rcp.length
			                      + total_game_rcp.length + win_t3.length + lose_t3.length + total_game_t3.length];
			
			System.arraycopy(protocol, 0, buf, 0, protocol.length);
			System.arraycopy(nickName, 0, buf, protocol.length, nickName.length);
			System.arraycopy(win_rcp, 0, buf, protocol.length + nickName.length, win_rcp.length);
			System.arraycopy(lose_rcp, 0, buf, protocol.length + nickName.length + win_rcp.length, lose_rcp.length);
			System.arraycopy(total_game_rcp, 0, buf, protocol.length + nickName.length + win_rcp.length + lose_rcp.length, total_game_rcp.length);
			System.arraycopy(win_t3, 0, buf, protocol.length + nickName.length + win_rcp.length + lose_rcp.length + total_game_rcp.length, win_t3.length);
			System.arraycopy(lose_t3, 0, buf, protocol.length + nickName.length + win_rcp.length + lose_rcp.length + total_game_rcp.length + win_t3.length, lose_t3.length);
			System.arraycopy(total_game_t3, 0, buf, protocol.length + nickName.length + win_rcp.length + lose_rcp.length + total_game_rcp.length + win_t3.length + lose_t3.length, total_game_t3.length);
			
			for (GameUser Uu : this.userList)
				if (Uu.getSock() != null && Uu.getSock() == sock) {
					Uu.getUserOutputStream().write(buf);
					Uu.getUserOutputStream().flush();
					break;
				}
		}
	}
	
	public boolean isMain() {
		if (this.id == 1) return true;
		else return false;
	}
	
	public void setOwner(GameUser gameUser) throws IOException {
		this.roomOwner = gameUser;
		if (gameUser.getSock() == null) return;
		gameUser.getUserOutputStream().write("1000\n".getBytes());
		gameUser.getUserOutputStream().flush();
	}
	
	public void setDefaultRoomName() {
		if (this.id == 1) this.roomName = "Main_Room";
		else this.roomName = "Room_" + String.valueOf(this.getRoomID());
	}
	
	public void setRoomName(String name) throws IOException {
		this.roomName = name;
		for (GameUser u : this.userList) {
			if (u.getSock() == null) continue;
			u.getUserOutputStream().write(("1005 " + name + "\n").getBytes());
			u.getUserOutputStream().flush();
		}
	}
	
	public String getRoomName() {
		return this.roomName;
	}
	
	public int getUserNum() {
		return userList.size();
	}
	
	public GameUser getOwner() {
		return this.roomOwner;
	}
	
	public int getRoomID() {
		return this.id;
	}
	
	public List<GameUser> getUserList() {
		return this.userList;
	}
	
	public GameUser getRoomOwner() {
		return this.roomOwner;
	}
	
	public void setRoomOwner(GameUser roomOwner) {
		this.roomOwner = roomOwner;
	}
	
	// 이 방의 모든 유저 정보를 List로 제공한다. (Operations에서 사용된다.)
	public List<GameUser> getAllUser_inRoom() {
		List<GameUser> retVal = new ArrayList<GameUser>();// 원본을 그대로 주지는 않음!
		for (GameUser u : this.userList) {
			retVal.add(u);
		}
		return retVal;
	}
}











