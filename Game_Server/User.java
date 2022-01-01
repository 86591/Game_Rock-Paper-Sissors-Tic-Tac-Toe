package Game_making;

import java.security.*;

public class User {
	private static final int SALT_SIZE = 16;
	private static DB db = new DB();
	
	// 정상 동작 확인
	public static boolean set_User(String name, String nickname, String ID, byte[] PW, String email) throws Exception {
		String SALT = getSALT();
		if (db.set_User(name, nickname, ID, Hashing(PW, SALT), email, SALT)) return true;
		else return false;
	}
	
	// 정상 동작 확인
	public static boolean get_User(String ID, byte[] PW) throws Exception {
		if (db.preCheck(ID) == false) return false;
		
		String temp_salt = db.get_SALT(ID);
		String temp_pass = Hashing(PW, temp_salt);
		
		if (db.check(ID, temp_pass)) return true;
		else return false;
	}
	
	public static String get_nickname(String ID) {
		return db.get_nickname(ID);
	}
	
	public static int[] get_gameInfo(String ID) {
		return db.get_gameInfo(ID);
	}
	
	public static boolean set_Unconnected(String ID) {
		if (db.set_Unconnected(ID)) return true;
		else return false;
	}
	
	// gameFlag: 0 >> rcp, 1 >> ttt
	// resultFlag: 0 >> 승리, 1 >> 패비, 2 >> 무승부
	public static boolean set_gameResult(String ID, int gameFlag, int resultFlag) {
		if (db.set_gameResult(ID, gameFlag, resultFlag)) return true;
		else return false;
	}
	
	// 로그인 되어있는지 확인한다.
	public static boolean is_On(String ID) {
		return db.is_On(ID);
	}
	
	private static String Hashing(byte[] PW, String SALT) throws Exception{
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		
		for (int i = 0; i < 10000; i++) {
			String temp = Byte_to_String(PW) + SALT;
			md.update(temp.getBytes());
			PW = md.digest();
		}
		
		return Byte_to_String(PW);
	}
	
	private static String getSALT() throws Exception {
		SecureRandom rnd = new SecureRandom();
		byte[] temp = new byte[SALT_SIZE];
		rnd.nextBytes(temp);
		
		return Byte_to_String(temp);
	}
	
	private static String Byte_to_String(byte[] temp) {
		StringBuilder sb = new StringBuilder();
		for (byte a : temp) {
			sb.append(String.format("%02x", a));
		}
		return sb.toString();
	}
}