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

	static int N, M, H, K;
	static int[][] map;
	static int[][] players; // 0번은 술래
	static int[] dx = { -1, 0, 1, 0 };
	static int[] dy = { 0, 1, 0, -1 };
	static int[][] dirMap;
	static int[][] dirReverseMap;
	static boolean reverse;

	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		N = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());
		H = Integer.parseInt(st.nextToken());
		K = Integer.parseInt(st.nextToken());
		map = new int[N + 1][N + 1];
		players = new int[M + 1][3];
		dirMap = new int[N + 1][N + 1];
		dirReverseMap = new int[N + 1][N + 1];
		reverse = false;
		for (int i = 1; i <= M; i++) {
			st = new StringTokenizer(br.readLine());
			int x = Integer.parseInt(st.nextToken());
			int y = Integer.parseInt(st.nextToken());
			int d = Integer.parseInt(st.nextToken());
			players[i] = new int[] { x, y, d };
		}
		players[0] = new int[] { (N + 1) / 2, (N + 1) / 2, 0 };
		for (int i = 0; i < H; i++) {
			st = new StringTokenizer(br.readLine());
			int x = Integer.parseInt(st.nextToken());
			int y = Integer.parseInt(st.nextToken());
			map[x][y] = 1;
		}

		int answer = solve();
		System.out.println(answer);
	}

	private static int solve() {
		// 0. 술래 이동 방향을 미리 표시
		initDirMap();
//		printDirMap();

		int score = 0;
		for (int turn = 1; turn <= K; turn++) {
			// 1. 술래와의 거리가 3이하인 도망자 동시 이동
			for (int i = 1; i <= M; i++) {
				if (Math.abs(players[0][0] - players[i][0]) + Math.abs(players[0][1] - players[i][1]) <= 3) {
					// 이동
					movePlayer(players[i]);
				}
			}

			// 2. 술래 이동
			moveSeeker(players[0]);
//			printSeekerPos(players[0]);

			// 3. 도망자 잡기
			score += catchPlayer(players[0], turn);

//			printInfo();
		}

		return score;
	}

	private static void initDirMap() {
		int x = players[0][0];
		int y = players[0][1];
		int d = players[0][2];
		for (int i = 1; i <= N - 1; i++) {
			for (int j = 0; j < i; j++) {
				dirMap[x][y] = d;
				x += dx[d];
				y += dy[d];
			}
			d = (d + 1) % 4;
			for (int j = 0; j < i; j++) {
				dirMap[x][y] = d;
				x += dx[d];
				y += dy[d];
			}
			d = (d + 1) % 4;
		}

		x = 1;
		y = 1;
		d = 2;
		for (int i = 1; i <= N - 1; i++) {
			dirReverseMap[i][1] = d;
			x += dx[d];
			y += dy[d];
		}
		d--;
		if (d < 0) {
			d = 3;
		}
		for (int i = N - 1; i >= 1; i--) {
			for (int j = 0; j < i; j++) {
				dirReverseMap[x][y] = d;
				x += dx[d];
				y += dy[d];
			}
			d--;
			if (d < 0) {
				d = 3;
			}
			for (int j = 0; j < i; j++) {
				dirReverseMap[x][y] = d;
				x += dx[d];
				y += dy[d];
			}
			d--;
			if (d < 0) {
				d = 3;
			}
		}
	}

	private static void movePlayer(int[] player) {
		int nx = player[0] + dx[player[2]];
		int ny = player[1] + dy[player[2]];
		if (nx < 1 || nx > N || ny < 1 || ny > N) {
			player[2] = (player[2] + 2) % 4;
			nx = player[0] + dx[player[2]];
			ny = player[1] + dy[player[2]];
		}
		if (players[0][0] == nx && players[0][1] == ny) {
			return;
		}
		player[0] = nx;
		player[1] = ny;
	}

	private static void moveSeeker(int[] seeker) {
		if (reverse) {
			seeker[0] += dx[seeker[2]];
			seeker[1] += dy[seeker[2]];
			seeker[2] = dirReverseMap[seeker[0]][seeker[1]];
		} else {
			seeker[0] += dx[seeker[2]];
			seeker[1] += dy[seeker[2]];
			seeker[2] = dirMap[seeker[0]][seeker[1]];
		}
		if (seeker[0] == 1 && seeker[1] == 1) {
			reverse = true;
			seeker[2] = dirReverseMap[1][1];
		}
		if (seeker[0] == (N + 1) / 2 && seeker[1] == (N + 1) / 2) {
			reverse = false;
			seeker[2] = dirMap[(N + 1) / 2][(N + 1) / 2];
		}
	}

	private static int catchPlayer(int[] seeker, int turn) {
		int score = 0;
		int x = seeker[0];
		int y = seeker[1];
		int d = seeker[2];
		for (int i = 0; i < 3; i++) {
			if (x < 1 || x > N || y < 1 || y > N) {
				break;
			}
			if (map[x][y] == 1) {
				x += dx[d];
				y += dy[d];
				continue;
			}
			for (int j = 1; j <= M; j++) {
				if (players[j][0] == x && players[j][1] == y) {
					players[j][0] = 0;
					players[j][1] = 0;
					score += turn;
				}
			}
			x += dx[d];
			y += dy[d];
		}

		return score;
	}

	private static void printSeekerPos(int[] seeker) {
		System.out.println("seeker pos");
		int[][] temp = new int[N + 1][N + 1];
		temp[seeker[0]][seeker[1]] = 1;
		for (int i = 1; i <= N; i++) {
			for (int j = 1; j <= N; j++) {
				System.out.printf("%d ", temp[i][j]);
			}
			System.out.println();
		}
		System.out.println();
	}

	private static void printDirMap() {
		System.out.println("dirMap");
		for (int i = 1; i <= N; i++) {
			for (int j = 1; j <= N; j++) {
				System.out.printf("%d ", dirMap[i][j]);
			}
			System.out.println();
		}
		System.out.println("dirReverseMap");
		for (int i = 1; i <= N; i++) {
			for (int j = 1; j <= N; j++) {
				System.out.printf("%d ", dirReverseMap[i][j]);
			}
			System.out.println();
		}
		System.out.println();
	}

	private static void printInfo() {
		System.out.println("술래");
		System.out.println(Arrays.toString(players[0]));

		System.out.println("도망자");
		for (int i = 1; i <= M; i++) {
			System.out.println(Arrays.toString(players[i]));
		}
	}
}