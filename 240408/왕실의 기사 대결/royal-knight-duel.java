import java.io.*;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.StringTokenizer;

public class Main {

	static int L, N, Q;
	static int[][] trapMap;
	static int[][] knightMap;
	static Knight[] knights;
	static int[][] orders;
	static int[] dx = { -1, 0, 1, 0 };
	static int[] dy = { 0, 1, 0, -1 };

	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		L = Integer.parseInt(st.nextToken());
		N = Integer.parseInt(st.nextToken());
		Q = Integer.parseInt(st.nextToken());
		trapMap = new int[L + 1][L + 1];
		knightMap = new int[L + 1][L + 1];
		knights = new Knight[N + 1];
		orders = new int[Q][2];
		for (int i = 1; i <= L; i++) {
			st = new StringTokenizer(br.readLine());
			for (int j = 1; j <= L; j++) {
				trapMap[i][j] = Integer.parseInt(st.nextToken());
			}
		}
		for (int i = 1; i <= N; i++) {
			st = new StringTokenizer(br.readLine());
			int r = Integer.parseInt(st.nextToken());
			int c = Integer.parseInt(st.nextToken());
			int h = Integer.parseInt(st.nextToken());
			int w = Integer.parseInt(st.nextToken());
			int k = Integer.parseInt(st.nextToken());
			knights[i] = new Knight(i, r, c, h, w, k);
			knights[i].updateMap(i);
		}
		for (int i = 0; i < Q; i++) {
			st = new StringTokenizer(br.readLine());
			int n = Integer.parseInt(st.nextToken());
			int d = Integer.parseInt(st.nextToken());
			orders[i] = new int[] { n, d };
		}

//		printMap(trapMap);
//		printMap(knightMap);

		int answer = solve();
		System.out.println(answer);
	}

	private static int solve() {
		for (int[] order : orders) {
			if (!knights[order[0]].enabled) {
				continue;
			}

			move(order);
//			System.out.println(Arrays.toString(knights));
		}

		int sum = 0;
		for (int i = 1; i <= N; i++) {
			if (!knights[i].enabled) {
				continue;
			}
			sum += knights[i].damage;
		}

		return sum;
	}

	private static void move(int[] order) {
		int idx = order[0];
		int dir = order[1];

		Knight curr = knights[idx];
		Queue<Knight> queue = curr.getNextKnights(dir);

		if (queue == null) {
			return;
		}

		if (queue.isEmpty()) {
			curr.move(dir);
			return;
		}

		// 연쇄 밀림
		List<Knight> movedKnights = new ArrayList<>();
		movedKnights.add(curr);
		while (!queue.isEmpty()) {
			Knight next = queue.poll();
			movedKnights.add(next);
			Queue<Knight> nQueue = next.getNextKnights(dir);
			if (nQueue == null) {
				return;
			}
			queue.addAll(nQueue);
		}

		// 이동 후 데미지 적용
		for (int i = movedKnights.size() - 1; i >= 0; i--) {
			Knight knight = movedKnights.get(i);
			knight.move(dir);
			if (i > 0) { // 명령받지 않은 기사만 데미지 적용
				knight.updateDamage();
			}
		}
	}

	private static void printMap(int[][] map) {
		for (int i = 1; i <= L; i++) {
			for (int j = 1; j <= L; j++) {
				System.out.printf("%d ", map[i][j]);
			}
			System.out.println();
		}
		System.out.println();
	}

	private static boolean isOutOfRange(int x, int y) {
		return x < 1 || x > L || y < 1 || y > L || trapMap[x][y] == 2;
	}

	static class Knight {
		int idx;
		int r;
		int c;
		int h;
		int w;
		int k;
		int damage;
		boolean enabled;

		public Knight(int idx, int r, int c, int h, int w, int k) {
			this.idx = idx;
			this.r = r;
			this.c = c;
			this.h = h;
			this.w = w;
			this.k = k;
			enabled = true;
		}

		public void updateMap(int value) {
			for (int i = 0; i < h; i++) {
				for (int j = 0; j < w; j++) {
					knightMap[r + i][c + j] = value;
				}
			}
		}

		public String toString() {
			return String.format("r:%d c:%d h:%d w:%d k:%d damage:%d enabled:%b", r, c, h, w, k, damage, enabled);
		}

		public Queue<Knight> getNextKnights(int dir) {
			boolean[] visited = new boolean[N + 1];
			visited[idx] = true;
			Queue<Knight> result = new ArrayDeque<>();

			for (int i = 0; i < h; i++) {
				for (int j = 0; j < w; j++) {
					int nx = r + i + dx[dir];
					int ny = c + j + dy[dir];
					int idx = knightMap[nx][ny];
					if (isOutOfRange(nx, ny)) {
						return null;
					}
					if (idx == 0 || visited[idx]) {
						continue;
					}
					result.add(knights[idx]);
					visited[idx] = true;
				}
			}

			return result;
		}

		public void move(int dir) {
			updateMap(0);
			r += dx[dir];
			c += dy[dir];
			updateMap(idx);
		}

		public void updateDamage() {
			for (int i = 0; i < h; i++) {
				for (int j = 0; j < w; j++) {
					if (trapMap[r + i][c + j] == 1) {
						damage++;
					}
				}
			}

			if (damage >= k) {
				enabled = false;
			}
		}
	}
}