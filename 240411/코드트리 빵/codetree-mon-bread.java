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

	static int N, M;
	static int[][] map; // 지나갈 수 있는지 여부
	static int[][] stores;
	static int[][] persons;
	static int[] dx = { -1, 0, 0, 1 };
	static int[] dy = { 0, -1, 1, 0 };

	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		N = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());
		map = new int[N + 1][N + 1];
		stores = new int[M + 1][2];
		persons = new int[M + 1][2];

		for (int i = 1; i <= N; i++) {
			st = new StringTokenizer(br.readLine());
			for (int j = 1; j <= N; j++) {
				map[i][j] = Integer.parseInt(st.nextToken());
			}
		}

		for (int i = 1; i <= M; i++) {
			st = new StringTokenizer(br.readLine());
			int x = Integer.parseInt(st.nextToken());
			int y = Integer.parseInt(st.nextToken());
			stores[i][0] = x;
			stores[i][1] = y;
		}

		int answer = solve();
		System.out.println(answer);
	}

	private static int solve() {
		int t = 0;
//		printInfo(t);

		while (true) {
			t++;
			// 1. 격자에 있는 사람들 이동
			for (int i = 1; i < Math.min(t, M + 1); i++) {
				// 이미 도착한 사람은 넘기기
				if (persons[i][0] == stores[i][0] && persons[i][1] == stores[i][1]) {
					continue;
				}
				// 최단경로로 이동
				movePerson(persons[i], stores[i]);
			}

			// 2. 편의점 도착 여부 확인
			int count = 0;
			for (int i = 1; i < Math.min(t, M + 1); i++) {
				if (persons[i][0] == stores[i][0] && persons[i][1] == stores[i][1]) {
					map[persons[i][0]][persons[i][1]] = -1;
					count++;
				}
			}
			if (count == M) {
				return t;
			}

			// 3. t에 해당하는 사람 캠프 진입
			if (t <= M) {
				// 편의점과 가장 가까운 캠프 찾기
				persons[t] = findCamp(stores[t]);
			}

//			printInfo(t);
		}
	}

	private static void movePerson(int[] person, int[] store) {
		// 최단경로 찾기
		int[][][] prev = new int[N + 1][N + 1][2];
		boolean[][] visited = new boolean[N + 1][N + 1];
		Queue<int[]> queue = new ArrayDeque<>();

		queue.add(person);
		visited[person[0]][person[1]] = true;

		while (!queue.isEmpty()) {
			int[] curr = queue.poll();

			for (int i = 0; i < 4; i++) {
				int nx = curr[0] + dx[i];
				int ny = curr[1] + dy[i];
				if (nx < 1 || nx > N || ny < 1 || ny > N || visited[nx][ny]) {
					continue;
				}
				if (nx == store[0] && ny == store[1]) {
					// 최단경로 역추적
					int x = nx;
					int y = ny;
					int px = curr[0];
					int py = curr[1];
					while (!(px == person[0] && py == person[1])) {
						x = px;
						y = py;
						px = prev[x][y][0];
						py = prev[x][y][1];
					}
					person[0] = x;
					person[1] = y;
					return;
				}
				if (map[nx][ny] == -1) {
					continue;
				}
				queue.add(new int[] { nx, ny });
				visited[nx][ny] = true;
				prev[nx][ny] = curr;
			}
		}
	}

	private static int[] findCamp(int[] store) {
		List<int[]> candidates = new ArrayList<>();
		boolean[][] visited = new boolean[N + 1][N + 1];
		Queue<int[]> queue = new ArrayDeque<>();

		queue.add(store);
		visited[store[0]][store[1]] = true;

		while (!queue.isEmpty()) {
			if (!candidates.isEmpty()) {
				Collections.sort(candidates, (o1, o2) -> {
					if (o1[0] == o2[0]) {
						return o1[1] - o2[1];
					}
					return o1[0] - o2[0];
				});

				int[] camp = candidates.get(0);
				map[camp[0]][camp[1]] = -1;
				return camp;
			}

			int size = queue.size();
			for (int k = 0; k < size; k++) {
				int[] curr = queue.poll();

				for (int i = 0; i < 4; i++) {
					int nx = curr[0] + dx[i];
					int ny = curr[1] + dy[i];
					if (nx < 1 || nx > N || ny < 1 || ny > N || visited[nx][ny] || map[nx][ny] == -1) {
						continue;
					}
					if (map[nx][ny] == 1) {
						candidates.add(new int[] { nx, ny });
					}
					queue.add(new int[] { nx, ny });
					visited[nx][ny] = true;
				}
			}
		}

		return null;
	}

	private static void printInfo(int time) {
		System.out.println("time:" + time);
		System.out.println("[map]");
		for (int i = 1; i <= N; i++) {
			for (int j = 1; j <= N; j++) {
				System.out.printf("%d ", map[i][j]);
			}
			System.out.println();
		}
		System.out.println();

		System.out.println("[person & store]");
		for (int i = 1; i <= M; i++) {
			System.out.println("person: " + Arrays.toString(persons[i]) + " store: " + Arrays.toString(stores[i]));
		}
		System.out.println();
		System.out.println("------------------------------");
	}
}