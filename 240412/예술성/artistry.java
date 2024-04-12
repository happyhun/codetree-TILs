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

	static int N;
	static int[][] map;
	static int[][] group;
	static int[][] counts;
	static int answer;
	static boolean[][] visited;
	static int[] dx = { -1, 0, 1, 0 };
	static int[] dy = { 0, -1, 0, 1 };
	static List<Group> groups;

	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		N = Integer.parseInt(br.readLine());
		map = new int[N][N];
		for (int i = 0; i < N; i++) {
			StringTokenizer st = new StringTokenizer(br.readLine());
			for (int j = 0; j < N; j++) {
				map[i][j] = Integer.parseInt(st.nextToken());
			}
		}

		solve();
		System.out.println(answer);
	}

	private static void solve() {
		// 그룹 만들기
		group = new int[N][N];
		groups = new ArrayList<>();
		visited = new boolean[N][N];
		int groupIdx = 0;
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				if (visited[i][j]) {
					continue;
				}
				BFS(i, j, groupIdx++);
			}
		}

//		printGroup();

		// 맞닿는 변의 수 업데이트
		counts = new int[groupIdx][groupIdx];
		updateCounts();

//		printCounts();

		// 예술 점수 계산
		int score = 0;
		for (int i = 0; i < groupIdx; i++) {
			Group A = groups.get(i);
			for (int j = i + 1; j < groupIdx; j++) {
				Group B = groups.get(j);
				score += (A.count + B.count) * A.value * B.value * counts[i][j];
			}
		}
		answer += score;

		for (int k = 0; k < 3; k++) {
			// 회전하기
			int[][] temp = new int[N][N];
			int size = N / 2;
			for (int i = 0; i < N; i++) {
				temp[size][i] = map[i][size];
				temp[i][size] = map[size][N - 1 - i];
			}

			for (int i = 0; i < size; i++) {
				for (int j = 0; j < size; j++) {
					temp[j][size - 1 - i] = map[i][j];
				}
			}

			int r = 0;
			int c = size + 1;
			for (int i = 0; i < size; i++) {
				for (int j = 0; j < size; j++) {
					temp[r + j][c + size - 1 - i] = map[r + i][c + j];
				}
			}

			r = size + 1;
			c = 0;
			for (int i = 0; i < size; i++) {
				for (int j = 0; j < size; j++) {
					temp[r + j][c + size - 1 - i] = map[r + i][c + j];
				}
			}

			r = size + 1;
			c = size + 1;
			for (int i = 0; i < size; i++) {
				for (int j = 0; j < size; j++) {
					temp[r + j][c + size - 1 - i] = map[r + i][c + j];
				}
			}

			map = temp;
//			printMap();

			// 그룹 만들기
			group = new int[N][N];
			groups = new ArrayList<>();
			visited = new boolean[N][N];
			groupIdx = 0;
			for (int i = 0; i < N; i++) {
				for (int j = 0; j < N; j++) {
					if (visited[i][j]) {
						continue;
					}
					BFS(i, j, groupIdx++);
				}
			}

//			printGroup();

			// 맞닿는 변의 수 업데이트
			counts = new int[groupIdx][groupIdx];
			updateCounts();

//			printCounts();

			// 예술 점수 계산
			score = 0;
			for (int i = 0; i < groupIdx; i++) {
				Group A = groups.get(i);
				for (int j = i + 1; j < groupIdx; j++) {
					Group B = groups.get(j);
					score += (A.count + B.count) * A.value * B.value * counts[i][j];
				}
			}
			answer += score;
		}
	}

	private static void BFS(int x, int y, int idx) {
		int count = 0;
		int value = map[x][y];
		Queue<int[]> queue = new ArrayDeque<>();
		queue.add(new int[] { x, y });
		visited[x][y] = true;
		group[x][y] = idx;

		while (!queue.isEmpty()) {
			int[] curr = queue.poll();
			count++;

			for (int i = 0; i < 4; i++) {
				int nx = curr[0] + dx[i];
				int ny = curr[1] + dy[i];
				if (nx < 0 || nx >= N || ny < 0 || ny >= N || visited[nx][ny] || map[nx][ny] != value) {
					continue;
				}
				queue.add(new int[] { nx, ny });
				visited[nx][ny] = true;
				group[nx][ny] = idx;
			}
		}

		groups.add(new Group(value, count));
	}

	private static void updateCounts() {
		visited = new boolean[N][N];
		Queue<int[]> queue = new ArrayDeque<>();

		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				if (visited[i][j]) {
					continue;
				}

				queue.add(new int[] { i, j });
				visited[i][j] = true;

				while (!queue.isEmpty()) {
					int[] curr = queue.poll();

					for (int d = 0; d < 4; d++) {
						int nx = curr[0] + dx[d];
						int ny = curr[1] + dy[d];
						if (nx < 0 || nx >= N || ny < 0 || ny >= N || visited[nx][ny]) {
							continue;
						}
						if (group[curr[0]][curr[1]] != group[nx][ny]) {
							counts[group[curr[0]][curr[1]]][group[nx][ny]]++;
							counts[group[nx][ny]][group[curr[0]][curr[1]]]++;
							continue;
						}
						queue.add(new int[] { nx, ny });
						visited[nx][ny] = true;
					}
				}
			}
		}

	}

	private static void printGroup() {
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				System.out.printf("%d ", group[i][j]);
			}
			System.out.println();
		}
		System.out.println();
	}

	private static void printCounts() {
		for (int i = 0; i < counts.length; i++) {
			for (int j = i + 1; j < counts.length; j++) {
				System.out.printf("(%d,%d) -> %d\n", i, j, counts[i][j]);
			}
		}
		System.out.println();
	}

	private static void printMap() {
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				System.out.printf("%d ", map[i][j]);
			}
			System.out.println();
		}
		System.out.println();
	}

	static class Group {
		int value;
		int count;

		public Group(int value, int count) {
			this.value = value;
			this.count = count;
		}
	}
}