import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Main {

    static int N, M, K;
    static int[][] map;
    static Player[] players;
    static int[] exit;
    static int[] dx = {-1, 1, 0, 0};
    static int[] dy = {0, 0, -1, 1};
    static int playerCount;

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());
        playerCount = M;
        map = new int[N + 1][N + 1];
        for (int i = 1; i <= N; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 1; j <= N; j++) {
                map[i][j] = Integer.parseInt(st.nextToken());
            }
        }
        players = new Player[M + 1];
        for (int i = 1; i <= M; i++) {
            st = new StringTokenizer(br.readLine());
            int x = Integer.parseInt(st.nextToken());
            int y = Integer.parseInt(st.nextToken());
            players[i] = new Player(x, y);
        }
        st = new StringTokenizer(br.readLine());
        int x = Integer.parseInt(st.nextToken());
        int y = Integer.parseInt(st.nextToken());
        exit = new int[]{x, y};

        int answer = solve();
        System.out.println(answer);
        System.out.println(exit[0] + " " + exit[1]);
    }

    private static int solve() {
        for (int t = 1; t <= K; t++) {
            // 플레이어 이동 및 미로 탈출
            for (int i = 1; i <= M; i++) {
                if (players[i].escaped) {
                    continue;
                }
                players[i].move();
            }

            if (playerCount == 0) {
                return calculateDistSum();
            }

            // 가장 작은 정사각형 찾기
            Square square = findSquare();

            // 정사각형 회전시키기
            square.rotate();
        }

        return calculateDistSum();
    }

    private static Square findSquare() {
        for (int size = 2; size < N; size++) {
            for (int i = 1; i <= N + 1 - size; i++) {
                for (int j = 1; j <= N + 1 - size; j++) {
                    if (isMinSquare(i, j, size)) {
                        return makeSquare(i, j, size);
                    }
                }
            }
        }

        return makeSquare(1, 1, N);
    }

    private static boolean isMinSquare(int x, int y, int size) {
        // 출구를 포함하지 않으면 X
        if (x > exit[0] || x + size - 1 < exit[0] || y > exit[1] || y + size - 1 < exit[1]) {
            return false;
        }

        // 플레이어를 포함하지 않으면 X
        for (int i = 1; i <= M; i++) {
            Player player = players[i];
            if (player.escaped || x > player.x || x + size - 1 < player.x || y > player.y || y + size - 1 < player.y) {
                continue;
            }
            return true;
        }
        return false;
    }

    private static Square makeSquare(int x, int y, int size) {
        List<Player> inPlayers = new ArrayList<>();
        for (int i = 1; i <= M; i++) {
            Player player = players[i];
            if (player.escaped || x > player.x || x + size - 1 < player.x || y > player.y || y + size - 1 < player.y) {
                continue;
            }
            inPlayers.add(player);
        }

        return new Square(x, y, size, inPlayers);
    }

    private static int calculateDistSum() {
        int sum = 0;
        for (int i = 1; i <= M; i++) {
            sum += players[i].dist;
        }
        return sum;
    }

    private static void printMap() {
        for (int i = 1; i <= N; i++) {
            for (int j = 1; j <= N; j++) {
                System.out.printf("%d ", map[i][j]);
            }
            System.out.println();
        }
        System.out.println();
    }

    private static void printPlayers() {
        for (int i = 1; i <= M; i++) {
            if (players[i].escaped) {
                continue;
            }
            System.out.println(i + ": " + players[i]);
        }
        System.out.println();
    }

    static class Player {
        int x;
        int y;
        int dist;
        boolean escaped;

        public Player(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public void move() {
            // 4방향 중 출구와 가장 가까운 방향 선택
            int minDist = Math.abs(x - exit[0]) + Math.abs(y - exit[1]);
            for (int i = 0; i < 4; i++) {
                int nx = x + dx[i];
                int ny = y + dy[i];
                if (nx < 1 || nx > N || ny < 1 || ny > N) {
                    continue;
                }
                int dist = Math.abs(nx - exit[0]) + Math.abs(ny - exit[1]);
                if (dist < minDist) {
                    minDist = dist;
                }
            }

            // 벽이 없으면 이동
            for (int i = 0; i < 4; i++) {
                int nx = x + dx[i];
                int ny = y + dy[i];
                if (nx < 1 || nx > N || ny < 1 || ny > N) {
                    continue;
                }
                int dist = Math.abs(nx - exit[0]) + Math.abs(ny - exit[1]);
                if (dist == minDist && map[nx][ny] == 0) {
                    x = nx;
                    y = ny;
                    this.dist++;
                    break;
                }
            }

            // 이동한 곳이 출구면 탈출
            if (x == exit[0] && y == exit[1]) {
                escaped = true;
                playerCount--;
            }
        }

        public String toString() {
            return String.format("(%d,%d) dist: %d escaped: %b", x, y, dist, escaped);
        }
    }

    static class Square {
        int x;
        int y;
        int size;
        List<Player> inPlayers;

        public Square(int x, int y, int size, List<Player> inPlayers) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.inPlayers = inPlayers;
        }

        public void rotate() {
            int[][] tempMap = new int[N + 1][N + 1];
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    tempMap[x + j][y + (size - 1 - i)] = map[x + i][y + j];
                }
            }
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    map[x + i][y + j] = tempMap[x + i][y + j];
                    if (map[x + i][y + j] > 0) {
                        map[x + i][y + j]--;
                    }
                }
            }

            for (Player player : inPlayers) {
                int i = player.x - x;
                int j = player.y - y;
                player.x = x + j;
                player.y = y + (size - 1 - i);
            }

            int i = exit[0] - x;
            int j = exit[1] - y;
            exit[0] = x + j;
            exit[1] = y + (size - 1 - i);
        }

        public String toString() {
            return String.format("(%d,%d) -> (%d,%d) \n", x, y, x + size - 1, y + size - 1) + inPlayers;
        }
    }
}