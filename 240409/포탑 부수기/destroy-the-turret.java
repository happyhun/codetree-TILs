import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.StringTokenizer;

public class Main {

	static int N, M, K;
	static Tower[][] map;
	static List<Tower> candidates;
	static int[] dx = { 0, 1, 0, -1, -1, 1, 1, -1 };
	static int[] dy = { 1, 0, -1, 0, 1, 1, -1, -1 };

	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		N = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());
		K = Integer.parseInt(st.nextToken());
		map = new Tower[N][M];
		candidates = new ArrayList<>();
		for (int i = 0; i < N; i++) {
			st = new StringTokenizer(br.readLine());
			for (int j = 0; j < M; j++) {
				int damage = Integer.parseInt(st.nextToken());
				map[i][j] = new Tower(i, j, damage);
				if (damage > 0) {
					candidates.add(map[i][j]);
				}
			}
		}

		int answer = solve();
		System.out.println(answer);
	}

	private static int solve() {
		for (int t = 1; t <= K; t++) {
			// 공격자 방어자 선정
			Collections.sort(candidates, (o1, o2) -> {
				if (o1.damage == o2.damage) {
					if (o1.attackTime == o2.attackTime) {
						if ((o1.x + o1.y) == (o2.x + o2.y)) {
							return o2.y - o1.y;
						}
						return (o2.x + o2.y) - (o1.x + o1.y);
					}
					return o2.attackTime - o1.attackTime;
				}
				return o1.damage - o2.damage;
			});

			Tower attacker = candidates.get(0);
			Tower defender = candidates.get(candidates.size() - 1);

			// 공격자 공격력 상승 및 공격시간 업데이트
			attacker.damage += (N + M);
			attacker.attackTime = t;

			// 공격 후 포탑 부수기
			attackTower(attacker, defender);
			if (candidates.size() == 1) {
				return candidates.get(0).damage;
			}

			// 포탑 정비
			repairTower();
		}

		Collections.sort(candidates, (o1, o2) -> {
			if (o1.damage == o2.damage) {
				if (o1.attackTime == o2.attackTime) {
					if ((o1.x + o1.y) == (o2.x + o2.y)) {
						return o2.y - o1.y;
					}
					return (o2.x + o2.y) - (o1.x + o1.y);
				}
				return o2.attackTime - o1.attackTime;
			}
			return o1.damage - o2.damage;
		});

		return candidates.get(candidates.size() - 1).damage;
	}

	private static void attackTower(Tower attacker, Tower defender) {
		attacker.repair = false;
		defender.repair = false;

		// 레이저 공격 시도
		int[][] prev = new int[N][M];
		boolean[][] visited = new boolean[N][M];
		Queue<Tower> queue = new ArrayDeque<>();

		queue.add(attacker);
		visited[attacker.x][attacker.y] = true;

		while (!queue.isEmpty()) {
			Tower curr = queue.poll();
			for (int i = 0; i < 4; i++) {
				int nx = curr.x + dx[i];
				int ny = curr.y + dy[i];
				if (nx < 0) {
					nx = N + nx;
				}
				if (nx >= N) {
					nx %= N;
				}
				if (ny < 0) {
					ny = M + ny;
				}
				if (ny >= M) {
					ny %= M;
				}
				if (map[nx][ny].damage <= 0 || visited[nx][ny]) {
					continue;
				}
				Tower next = map[nx][ny];
				if (next == defender) {
					// 공격 및 경로 역추적
					defender.damage -= attacker.damage;
					if (defender.damage <= 0) {
						candidates.remove(candidates.size() - 1);
					}
					int px = curr.x;
					int py = curr.y;
					while (!(px == attacker.x && py == attacker.y)) {
						Tower prevTower = map[px][py];
						prevTower.damage -= (attacker.damage / 2);
						prevTower.repair = false;
						if (prevTower.damage <= 0) {
							candidates.remove(prevTower);
						}
						int dir = prev[px][py];
						px = prevTower.x + dx[dir];
						py = prevTower.y + dy[dir];
						if (px < 0) {
							px = N + px;
						}
						if (px >= N) {
							px %= N;
						}
						if (py < 0) {
							py = M + py;
						}
						if (py >= M) {
							py %= M;
						}
					}
					return;
				}
				queue.add(next);
				visited[nx][ny] = true;
				prev[nx][ny] = getOppositeDir(i);
			}
		}

		// 포탑 공격
		defender.damage -= attacker.damage;
		if (defender.damage <= 0) {
			candidates.remove(defender);
		}
		for (int i = 0; i < 8; i++) {
			int nx = defender.x + dx[i];
			int ny = defender.y + dy[i];
			if (nx < 0) {
				nx = N + nx;
			}
			if (nx >= N) {
				nx %= N;
			}
			if (ny < 0) {
				ny = M + ny;
			}
			if (ny >= M) {
				ny %= M;
			}
			if (map[nx][ny] == attacker) {
				continue;
			}
			map[nx][ny].damage -= (attacker.damage / 2);
			map[nx][ny].repair = false;
			if (map[nx][ny].damage <= 0) {
				candidates.remove(map[nx][ny]);
			}
		}
	}

	private static int getOppositeDir(int i) {
		switch (i) {
		case 0:
			return 2;
		case 1:
			return 3;
		case 2:
			return 0;
		default:
			return 1;
		}
	}

	private static void repairTower() {
		for (Tower tower : candidates) {
			if (tower.repair) {
				tower.damage++;
			}
			tower.repair = true;
		}
	}

	private static void printMap() {
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < M; j++) {
				System.out.printf("%d ", map[i][j].damage);
			}
			System.out.println();
		}
	}

	static class Tower {
		int x;
		int y;
		int damage;
		int attackTime;
		boolean repair;

		public Tower(int x, int y, int damage) {
			this.x = x;
			this.y = y;
			this.damage = damage;
			repair = true;
		}

		public String toString() {
			return String.format("(%d,%d) damage: %d attackTime: %d repair: %b", x, y, damage, attackTime, repair);
		}
	}
}