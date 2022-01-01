package Game_making;

import java.sql.*;

public class DB {
	// 정상 작동 확인
	public boolean preCheck(String ID) {
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		int check = 0;
		
		String driver = "com.mysql.cj.jdbc.Driver";
		
		String url = "jdbc:mysql://localhost:3306/game?serverTimezone=UTC";
		String user = "root";
		String pw = "12345";
		
		String c_SQL = "SELECT connected FROM user_info WHERE ID=\'"+ID+ "\'";
		
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, user, pw);
			stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			rs = stmt.executeQuery(c_SQL);
			rs.last();
			int count = rs.getRow();
			rs.close();
			System.out.println("count: " + count);
			if (count == 0) check = 0;
			else {
				rs = stmt.executeQuery(c_SQL);
				rs.next();
				int t = rs.getInt(1);
				if (t == 1) check = 0;
				else check = 1;
			}
		} catch(SQLException e) {
			check = 0;
			System.out.println("[SQL ERROR: " + e.getMessage() + "]");
		} catch(ClassNotFoundException e) {
			check = 0;
			System.out.println("[JDBC Connector Driver ERROR: " + e.getMessage() + "]");
		} finally {
			try {
				if (con != null && !con.isClosed()) {
					con.close();
				}
				
				if (stmt != null && !stmt.isClosed()) {
					stmt.close();
				}
				
				if (rs != null && !rs.isClosed()) {
					rs.close();
				}
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
		
		if (check == 0) return false;
		else return true;
	}
	
	// 정상 작동 확인
	public boolean check(String ID, String Hashing_PW) {
		Connection con = null;
		PreparedStatement pstmt = null;
		Statement stmt = null;
		ResultSet rs = null;
		int check = 0;
		
		String driver = "com.mysql.cj.jdbc.Driver";
		
		String url = "jdbc:mysql://localhost:3306/game?serverTimezone=UTC";
		String user = "root";
		String pw = "12345";
		
		String c_SQL = "SELECT ID FROM user_info WHERE ID=\'"+ID+"\' AND PW=\'"+Hashing_PW+"\'";
		String u_SQL = "UPDATE user_info set connected=1 WHERE ID=? AND PW=?";
		
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, user, pw);
			stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			rs = stmt.executeQuery(c_SQL);
			rs.last();
			int count = rs.getRow();
			
			System.out.println("count: " + count);
			
			if (count >= 1) {
				check = 1;
				pstmt = con.prepareStatement(u_SQL);
				pstmt.setString(1, ID);
				pstmt.setString(2, Hashing_PW);
				int r = pstmt.executeUpdate();

				if (r == 0) check = 0;
				else check = 1;
			}
			else {
				check = 0;
			}
		} catch(SQLException e) {
			check = 0;
			System.out.println("[SQL ERROR: " + e.getMessage() + "]");
		} catch(ClassNotFoundException e) {
			check = 0;
			System.out.println("[JDBC Connector Driver ERROR: " + e.getMessage() + "]");
		} finally {
			try {
				if (con != null && !con.isClosed()) {
					con.close();
				}
				
				if (stmt != null && !stmt.isClosed()) {
					stmt.close();
				}
				
				if (pstmt != null && !pstmt.isClosed()) {
					pstmt.close();
				}
				
				if (rs != null && !rs.isClosed()) {
					rs.close();
				}
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
		
		if (check == 0) return false;
		else return true;
	}
	
	// 정상 동작 확인
	public boolean set_User(String name, String nickname, String ID, String Hashing_PW, String email, String SALT) {
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		int check = 0;
		
		String driver = "com.mysql.cj.jdbc.Driver";
		
		String url = "jdbc:mysql://localhost:3306/game?serverTimezone=UTC";
		String user = "root";
		String pw = "12345";
		
		String c_SQL = "SELECT ID FROM user_info WHERE ID=\'"+ID+"\' OR nickname=\'"+nickname+"\'";
		String i_SQL = "INSERT INTO user_info VALUES(?,?,?,?,?,0,0,0,0,0,0,0,?)";
		
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, user, pw);
			stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			rs = stmt.executeQuery(c_SQL);
			rs.last();
			int count = rs.getRow();
			System.out.println("count: " + count);

			if (count == 0) {
				check = 1;
				pstmt = con.prepareStatement(i_SQL);
				pstmt.setString(1, name);
				pstmt.setString(2, nickname);
				pstmt.setString(3, ID);
				pstmt.setString(4, Hashing_PW);
				pstmt.setString(5, email);
				pstmt.setString(6, SALT);
				int r = pstmt.executeUpdate();
				if (r == 0) check = 0;
				else check = 1;
			}
			else {
				check = 0;
			}
		} catch(SQLException e) {
			check = 0;
			System.out.println("[SQL ERROR: " + e.getMessage() + "]");
		} catch(ClassNotFoundException e) {
			check = 0;
			System.out.println("[JDBC Connector Driver ERROR: " + e.getMessage() + "]");
		} finally {
			try {
				if (con != null && !con.isClosed()) {
					con.close();
				}
				
				if (stmt != null && !stmt.isClosed()) {
					stmt.close();
				}
				
				if (pstmt != null && !pstmt.isClosed()) {
					pstmt.close();
				}
				
				if (rs != null && !rs.isClosed()) {
					rs.close();
				}
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
		
		if (check == 0) return false;
		else return true;
	}
	
	// 정상 동작 확인
	public boolean set_Unconnected(String ID) {
		Connection con = null;
		PreparedStatement pstmt = null;
		int check = 0;
		
		String driver = "com.mysql.cj.jdbc.Driver";
		
		String url = "jdbc:mysql://localhost:3306/game?serverTimezone=UTC";
		String user = "root";
		String pw = "12345";
		
		String SQL = "UPDATE user_info SET connected=0 WHERE ID=?";
		
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, user, pw);
			pstmt = con.prepareStatement(SQL);
			
			pstmt.setString(1, ID);
			
			int count = pstmt.executeUpdate();
			
			if (count == 0) check = 0;
			else check = 1;
		} catch(SQLException e) {
			check = 0;
			System.out.println("[SQL ERROR: " + e.getMessage() + "]");
		} catch(ClassNotFoundException e) {
			check = 0;
			System.out.println("[JDBC Connector Driver ERROR: " + e.getMessage() + "]");
		} finally {
			try {
				if (con != null && !con.isClosed()) {
					con.close();
				}
				if (pstmt != null && !pstmt.isClosed()) {
					pstmt.close();
				}
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
		
		if (check == 0) return false;
		else return true;
	}
	
	// 정상 동작 확인
	public String get_nickname(String ID) {
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		String driver = "com.mysql.cj.jdbc.Driver";
		String url = "jdbc:mysql://localhost:3306/game?serverTimezone=UTC";
		String user = "root";
		String pw = "12345";
		
		String result = null;
		
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, user, pw);
			
			stmt = con.createStatement();
			
			String sql = "SELECT nickname FROM user_info WHERE ID=\""+ID+"\"";
			
			rs = stmt.executeQuery(sql);
			rs.next();
			result = rs.getString(1);
		} catch(SQLException e) {
			result = null;
			System.out.println("[SQL ERROR: " + e.getMessage() + "]");
		} catch(ClassNotFoundException e) {
			result = null;
			System.out.println("[JDBC Connector Driver ERROR: " + e.getMessage() + "]");
		} finally {
			try {
				if (con != null && !con.isClosed()) {
					con.close();
				}
				if (stmt != null && !stmt.isClosed()) {
					stmt.close();
				}
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	// 정상 동작 확인
	public int[] get_gameInfo(String ID) {
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		String driver = "com.mysql.cj.jdbc.Driver";
		String url = "jdbc:mysql://localhost:3306/game?serverTimezone=UTC";
		String user = "root";
		String pw = "12345";
		
		int[] result = new int[6];
		
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, user, pw);
			
			stmt = con.createStatement();
			
			String sql = "SELECT win_rcp, lose_rcp, total_game_rcp, win_ttt, lose_ttt, total_game_ttt FROM user_info WHERE ID=\""+ID+"\"";
			
			rs = stmt.executeQuery(sql);
			rs.next();
			result[0] = rs.getInt(1);
			result[1] = rs.getInt(2);
			result[2] = rs.getInt(3);
			result[3] = rs.getInt(4);
			result[4] = rs.getInt(5);
			result[5] = rs.getInt(6);
		} catch(SQLException e) {
			result = null;
			System.out.println("[SQL ERROR: " + e.getMessage() + "]");
		} catch(ClassNotFoundException e) {
			result = null;
			System.out.println("[JDBC Connector Driver ERROR: " + e.getMessage() + "]");
		} finally {
			try {
				if (con != null && !con.isClosed()) {
					con.close();
				}
				if (stmt != null && !stmt.isClosed()) {
					stmt.close();
				}
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	// 정상 작동 확인
	public String get_SALT(String ID) {
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		String driver = "com.mysql.cj.jdbc.Driver";
		String url = "jdbc:mysql://localhost:3306/game?serverTimezone=UTC";
		String user = "root";
		String pw = "12345";
		
		String result = null;
		
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, user, pw);
			
			stmt = con.createStatement();
			
			String sql = "SELECT SALT FROM user_info WHERE ID=\'"+ID+"\'";
			
			rs = stmt.executeQuery(sql);
			rs.next();
			result = rs.getString(1);
		} catch(SQLException e) {
			result = null;
			System.out.println("[SQL ERROR: " + e.getMessage() + "]");
		} catch(ClassNotFoundException e) {
			result = null;
			System.out.println("[JDBC Connector Driver ERROR: " + e.getMessage() + "]");
		} finally {
			try {
				if (con != null && !con.isClosed()) {
					con.close();
				}
				if (stmt != null && !stmt.isClosed()) {
					stmt.close();
				}
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	// 정상 작동 확인
	// gameFlag: 0 >> rcp, 1 >> ttt
	// resultFlag: 0 >> 승리, 1 >> 패배, 2 >> 무승부
	public boolean set_gameResult(String ID, int gameFlag, int resultFlag) {
		Connection con = null;
		PreparedStatement pstmt = null;
		int[] info = this.get_gameInfo(ID);
		int check = 0;
		
		String driver = "com.mysql.cj.jdbc.Driver";
		
		String url = "jdbc:mysql://localhost:3306/game?serverTimezone=UTC";
		String user = "root";
		String pw = "12345";
		
		String SQL_f = "UPDATE user_info SET ";
		
		String SQL_rcp_win = "win_rcp="+(info[0]+1)+", total_game_rcp="+(info[2]+1);
		String SQL_rcp_lose = "lose_rcp="+(info[1]+1)+", total_game_rcp="+(info[2]+1);
		String SQL_rcp_draw = "total_game_rcp="+(info[2]+1);
		
		String SQL_T3_win = "win_ttt="+(info[3]+1)+", total_game_ttt="+(info[5]+1);
		String SQL_T3_lose = "lose_ttt="+(info[4]+1)+", total_game_ttt="+(info[5]+1);
		String SQL_T3_draw = "total_game_ttt="+(info[5]+1);
		
		String SQL_b = " WHERE ID=?";
		
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, user, pw);
			
			if (gameFlag == 0 && resultFlag == 0) {
				pstmt = con.prepareStatement(SQL_f + SQL_rcp_win + SQL_b);
			}
			else if (gameFlag == 0 && resultFlag == 1) {
				pstmt = con.prepareStatement(SQL_f + SQL_rcp_lose + SQL_b);
			}
			else if (gameFlag == 0 && resultFlag == 2) {
				pstmt = con.prepareStatement(SQL_f + SQL_rcp_draw + SQL_b);
			}
			else if (gameFlag == 1 && resultFlag == 0) {
				pstmt = con.prepareStatement(SQL_f + SQL_T3_win + SQL_b);
			}
			else if (gameFlag == 1 && resultFlag == 1) {
				pstmt = con.prepareStatement(SQL_f + SQL_T3_lose + SQL_b);
			}
			else {
				pstmt = con.prepareStatement(SQL_f + SQL_T3_draw + SQL_b);
			}

			pstmt.setString(1, ID);
			
			int count = pstmt.executeUpdate();
			
			if (count == 0) check = 0;
			else check = 1;
		} catch(SQLException e) {
			check = 0;
			System.out.println("[SQL ERROR: " + e.getMessage() + "]");
		} catch(ClassNotFoundException e) {
			check = 0;
			System.out.println("[JDBC Connector Driver ERROR: " + e.getMessage() + "]");
		} finally {
			try {
				if (con != null && !con.isClosed()) {
					con.close();
				}
				if (pstmt != null && !pstmt.isClosed()) {
					pstmt.close();
				}
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
		
		if (check == 0) return false;
		else return true;
	}
	
	// 정상 작동 확인
	public boolean is_On(String ID) {
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		String driver = "com.mysql.cj.jdbc.Driver";
		String url = "jdbc:mysql://localhost:3306/game?serverTimezone=UTC";
		String user = "root";
		String pw = "12345";
		
		boolean result = false;
		
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, user, pw);
			
			stmt = con.createStatement();
			
			String sql = "SELECT connected FROM user_info WHERE ID=\'"+ID+"\'";
			
			rs = stmt.executeQuery(sql);
			rs.next();
			result = rs.getBoolean(1);
		} catch(SQLException e) {
			result = false;
			System.out.println("[SQL ERROR: " + e.getMessage() + "]");
		} catch(ClassNotFoundException e) {
			result = false;
			System.out.println("[JDBC Connector Driver ERROR: " + e.getMessage() + "]");
		} finally {
			try {
				if (con != null && !con.isClosed()) {
					con.close();
				}
				if (stmt != null && !stmt.isClosed()) {
					stmt.close();
				}
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
}