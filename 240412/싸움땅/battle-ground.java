import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.StringTokenizer;

public class Main {

	static int N, M, K;
	static List<Integer>[][] map;
	static Player[] players;
	static int[] dx = { -1, 0, 1, 0 };
	static int[] dy = { 0, 1, 0, -1 };

	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		N = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());
		K = Integer.parseInt(st.nextToken());
		map = new List[N + 1][N + 1];
		for (int i = 1; i <= N; i++) {
			st = new StringTokenizer(br.readLine());
			for (int j = 1; j <= N; j++) {
				map[i][j] = new ArrayList<>();
				int gun = Integer.parseInt(st.nextToken());
				if (gun > 0) {
					map[i][j].add(gun);
				}
			}
		}
		players = new Player[M + 1];
		for (int i = 1; i <= M; i++) {
			st = new StringTokenizer(br.readLine());
			int x = Integer.parseInt(st.nextToken());
			int y = Integer.parseInt(st.nextToken());
			int d = Integer.parseInt(st.nextToken());
			int s = Integer.parseInt(st.nextToken());
			players[i] = new Player(x, y, d, s);
		}

		solve();
		for (int i = 1; i <= M; i++) {
			System.out.printf("%d ", players[i].point);
		}
	}

	private static void solve() {
//		printInfo(0);

		for (int k = 1; k <= K; k++) {
			for (int m = 1; m <= M; m++) {
				Player player = players[m];
				// 1. 이동
				move(player);
				// 2. 총 교체 or 전투
				swapOrBattle(player);
			}

//			printInfo(k);
		}
	}

	private static void move(Player player) {
		int nx = player.x + dx[player.d];
		int ny = player.y + dy[player.d];
		if (nx < 1 || nx > N || ny < 1 || ny > N) {
			player.d = (player.d + 2) % 4;
			nx = player.x + dx[player.d];
			ny = player.y + dy[player.d];
		}
		player.x = nx;
		player.y = ny;
	}

	private static void swapOrBattle(Player player) {
		List<Player> battlePlayers = new ArrayList<>();
		for (int i = 1; i <= M; i++) {
			if (players[i].x == player.x && players[i].y == player.y) {
				battlePlayers.add(players[i]);
			}
		}

		if (battlePlayers.size() == 2) {
			// 전투
			battle(battlePlayers);
		} else {
			// 쏀 총 교체
			swapGun(player);
		}
	}

	private static void battle(List<Player> battlePlayers) {
		// 승자 패자 결정
		Player win = battlePlayers.get(0);
		Player lose = battlePlayers.get(1);

		if (lose.s + lose.gun > win.s + win.gun) {
			win = battlePlayers.get(1);
			lose = battlePlayers.get(0);
		} else if (lose.s + lose.gun == win.s + win.gun && lose.s > win.s) {
			win = battlePlayers.get(1);
			lose = battlePlayers.get(0);
		}

		// 승자 포인트 회득
		win.point += ((win.s + win.gun) - (lose.s + lose.gun));

		// 패자 총 버리기
		if (lose.gun > 0) {
			map[lose.x][lose.y].add(lose.gun);
			lose.gun = 0;
		}

		// 플레이어가 없는 곳으로 패자 이동
		for (int i = 0; i < 4; i++) {
			int nx = lose.x + dx[lose.d];
			int ny = lose.y + dy[lose.d];
			if (nx < 1 || nx > N || ny < 1 || ny > N) {
				lose.d = (lose.d + 1) % 4;
				continue;
			}
			boolean isPlayer = false;
			for (int j = 1; j <= M; j++) {
				if (players[j].x == nx && players[j].y == ny) {
					isPlayer = true;
				}
			}
			if (isPlayer) {
				lose.d = (lose.d + 1) % 4;
				continue;
			}
			lose.x = nx;
			lose.y = ny;
			break;
		}

		// 패자 총 교체
		swapGun(lose);

		// 승자 총 교체
		swapGun(win);
	}

	private static void swapGun(Player player) {
		List<Integer> guns = map[player.x][player.y];
		int maxIdx = 0;
		int maxGun = 0;
		for (int i = 0; i < guns.size(); i++) {
			if (guns.get(i) > maxGun) {
				maxGun = guns.get(i);
				maxIdx = i;
			}
		}

		if (maxGun > player.gun) {
			guns.set(maxIdx, player.gun);
			player.gun = maxGun;
		}
	}

	private static void printInfo(int round) {
		System.out.println("round: " + round);
		System.out.println("[map]");
		for (int i = 1; i <= N; i++) {
			for (int j = 1; j <= N; j++) {
				System.out.print(map[i][j] + " ");
			}
			System.out.println();
		}
		System.out.println();

		System.out.println("[players]");
		for (int i = 1; i <= M; i++) {
			System.out.println(i + " : " + players[i]);
		}
		System.out.println("----------------------------------------------");
	}

	static class Player {
		int x, y, d, s, gun, point;

		public Player(int x, int y, int d, int s) {
			this.x = x;
			this.y = y;
			this.d = d;
			this.s = s;
		}

		public String toString() {
			return String.format("(%d,%d) d: %d s: %d gun: %d point: %d", x, y, d, s, gun, point);
		}
	}
}