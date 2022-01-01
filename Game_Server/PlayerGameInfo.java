package Game_making;

public class PlayerGameInfo {
	private int[][] ttt = new int[3][3];// [x][y], 0 >> 빈 칸, 1 >> 내 칸, 2 >> 상대 칸
	private int[] rcp = new int[2];// [0] >> 내 패, [1] >> 상대 패, 0 >> 가위, 1 >> 바위, 2 >> 보
	
	public void InitializeGame() {
		for (int i = 0; i < 3; i++)
			for (int k = 0; k < 3; k++)
				ttt[i][k] = 0;
		rcp[0] = -1;
		rcp[1] = -1;
	}
	
	// flag: 0 >> 내 턴, flag: 1 >> 상대 턴
	//true 반환: 턴 마침, false 반환: 놓을 수 없는 위치
	public boolean setT3(int flag, int x, int y) {
		if (ttt[x][y] == 0) {
			if (flag == 0) ttt[x][y] = 1;
			else ttt[x][y] = 2;
			return true;
		}
		return false;
	}
	
	//flag: 0 >> 내 턴, flag: 1 >> 상대 턴
	//0 >> 가위, 1 >> 바위, 2 >> 보
	public void setRCP(int flag, int pick) {
		if (flag == 0) rcp[0] = pick;
		else rcp[1] = pick;
	}
	
	//0 반환: 승리, 1 반환: 패배, 2 반환: 무승부, 3 반환: 결과 안나옴
	// 결과 나온 경우, DB에 저장
	public int T3Result(String ID) {
		for (int i = 0; i < 3; i++)
			if (ttt[i][0] == 1 && ttt[i][1] == 1 && ttt[i][2] == 1) {
				User.set_gameResult(ID, 1, 0);
				return 0;
			}
			else if (ttt[i][0] == 2 && ttt[i][1] == 2 && ttt[i][3] == 2) {
				User.set_gameResult(ID, 1, 1);
				return 1;
			}
			else if (ttt[0][i] == 1 && ttt[1][i] == 1 && ttt[2][i] == 1) {
				User.set_gameResult(ID, 1, 0);
				return 0;
			}
			else if (ttt[0][i] == 2 && ttt[1][i] == 2 && ttt[2][i] == 2) {
				User.set_gameResult(ID, 1, 1);
				return 1;
			}
		
		if (ttt[0][0] == 1 && ttt[1][1] == 1 && ttt[2][2] == 1) {
			User.set_gameResult(ID, 1, 0);
			return 0;
		}
		else if (ttt[0][0] == 2 && ttt[1][1] == 2 && ttt[2][2] == 2) {
			User.set_gameResult(ID, 1, 1);
			return 1;
		}
		else if (ttt[2][0] == 1 && ttt[1][1] == 1 && ttt[0][2] == 1) {
			User.set_gameResult(ID, 1, 0);
			return 0;
		}
		else if (ttt[2][0] == 2 && ttt[1][1] == 2 && ttt[0][2] == 2) {
			User.set_gameResult(ID, 1, 1);
			return 1;
		}
		
		for (int i = 0; i < 3; i++)
			for (int k = 0; k < 3; k++)
				if (ttt[i][k] == 0) return 3;
		
		User.set_gameResult(ID, 1, 2);
		return 2;
	}
	
	//0 반환: 승리, 1 반환: 패배, 2 반환: 무승부, 3 반환: 결과 아직임
	// 결과 나온 경우, DB에 저장
	public int RCPResult(String ID) {
		if ((rcp[0] == 0 && rcp[1] == 1) || (rcp[0] == 1 && rcp[1] == 2) || (rcp[0] == 2 && rcp[1] == 0)) {
			User.set_gameResult(ID, 0, 0);
			return 0;
		}
		
		if ((rcp[0] == 1 && rcp[1] == 0) || (rcp[0] == 2 && rcp[1] == 1) || (rcp[0] == 0 && rcp[1] == 2)) {
			User.set_gameResult(ID, 0, 1);
			return 1;
		}
		
		if (rcp[0] == -1 || rcp[1] == -1)
			return 3;
		
		User.set_gameResult(ID, 0, 2);
		return 2;
	}
}