import java.util.*;
import java.io.*;

/*
 * 풀이 시간
 * - 3시간 40분
 * 
 * 틀린 이유
 *  1. 충돌로 튕겨나간 산타 좌표 업데이트 빠트림
 *  2. 조기 종료 후 점수를 출력하지 않음
 * */
public class Main {

    static int N, M, P, C, D;
    static int[][] map;
    static int[] deer;
    static Santa[] santas;
    static boolean canPlay;
    static int[] dx = {-1, 0, 1, 0, 1, 1, -1, -1};
    static int[] dy = {0, 1, 0, -1, 1, -1, 1, -1};

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        P = Integer.parseInt(st.nextToken());
        C = Integer.parseInt(st.nextToken());
        D = Integer.parseInt(st.nextToken());
        map = new int[N + 1][N + 1];
        st = new StringTokenizer(br.readLine());
        deer = new int[2];
        deer[0] = Integer.parseInt(st.nextToken());
        deer[1] = Integer.parseInt(st.nextToken());
        santas = new Santa[P + 1];
        for (int i = 0; i < P; i++) {
            st = new StringTokenizer(br.readLine());
            int no = Integer.parseInt(st.nextToken());
            int x = Integer.parseInt(st.nextToken());
            int y = Integer.parseInt(st.nextToken());
            santas[no] = new Santa(x, y);
            map[x][y] = no;
        }
        canPlay = true;

        solve();
    }

    private static void solve() {
        // M턴 반복
        for (int i = 0; i < M; i++) {
            // 루돌프 로직
            playDeer(i);
            if (!canPlay) {
                break;
            }

            // 산타 로직
            for (int j = 1; j <= P; j++) {
                if (santas[j].inRange && santas[j].canMove <= i) {
                    playSanta(i, j);
                }
                
            }

            // 산타 점수 획득
            for (int j = 1; j <= P; j++) {
                if (santas[j].inRange) {
                    santas[j].score++;
                }
            }
        }
        
        // 정답 출력
        printAnswer();
    }

    private static void playDeer(int turn) {
        Santa target = findTarget();
        if (target == null) {
            canPlay = false;
            return;
        }
        moveDeer(turn, target);
    }

    private static Santa findTarget() {
        int minDist = Integer.MAX_VALUE;
        Santa target = null;

        for (int i = 1; i <= P; i++) {
            Santa santa = santas[i];
            if (!santa.inRange) {
                continue;
            }
            int dist = (int) (Math.pow((deer[0] - santa.x), 2) + Math.pow((deer[1] - santa.y), 2));
            if (dist < minDist) {
                minDist = dist;
                target = santa;
            } else if (dist == minDist) {
                if (santa.x > target.x || (santa.x == target.x && santa.y > target.y)) {
                    minDist = dist;
                    target = santa;
                }
            }
        }
        
        return target;
    }

    private static void moveDeer(int turn, Santa target) {
        // 8 방향 중 가장 가까워지는 곳으로 이동
        int minDist = Integer.MAX_VALUE;
        int nx = 0;
        int ny = 0;
        int dir = 0;

        for (int i = 0; i < 8; i++) {
            int x = deer[0] + dx[i];
            int y = deer[1] + dy[i];
            int dist = (int) (Math.pow((x - target.x), 2) + Math.pow((y - target.y), 2));
            if (dist < minDist) {
                minDist = dist;
                dir = i;
                nx = x;
                ny = y;
            }
        }

        deer[0] = nx;
        deer[1] = ny;

        // 산타가 존재하면 충돌
        if (map[nx][ny] > 0) {
            collide(turn, map[nx][ny], dir, C);
        }
    }

    private static void collide(int turn, int santaIdx, int dir, int power) {
        // 산타 밀려나기
        Santa santa = santas[santaIdx];
        santa.score += power;
        santa.canMove = turn + 2;
        map[santa.x][santa.y] = 0;
        santa.x += (dx[dir] * power);
        santa.y += (dy[dir] * power);

        if (santa.x < 1 || santa.x > N || santa.y < 1 || santa.y > N) {
            santa.inRange = false;
            return;
        }

        if (map[santa.x][santa.y] == 0) {
        	map[santa.x][santa.y] = santaIdx;
            return;
        }

        // 연쇄 상호작용

        while (true) {
            // 산타 한 칸 밀기
            int nextSantaIdx = map[santa.x][santa.y];
            Santa nextSanta = santas[nextSantaIdx];
            nextSanta.x += dx[dir];
            nextSanta.y += dy[dir];
            map[santa.x][santa.y] = santaIdx;

            // 밀려난 산타의 탈락 여부 확인
            if (nextSanta.x < 1 || nextSanta.x > N || nextSanta.y < 1 || nextSanta.y > N) {
                nextSanta.inRange = false;
                break;
            }

            // 상호작용 종료
            if (map[nextSanta.x][nextSanta.y] == 0) {
                map[nextSanta.x][nextSanta.y] = nextSantaIdx;
                break;
            }

            santa = nextSanta;
            santaIdx = nextSantaIdx;
        }
    }

    private static void playSanta(int turn, int santaIdx) {
        // 산타 인덱스는 맵에 표시
        Santa santa = santas[santaIdx];
        int dir = 0;
        int x = santa.x;
        int y = santa.y;
        int dist = (int) (Math.pow((deer[0] - x), 2) + Math.pow((deer[1] - y), 2));
        map[x][y] = 0;
        
        for (int i = 0; i < 4; i++) {
            int nx = santa.x + dx[i];
            int ny = santa.y + dy[i];
            int nDist = (int) (Math.pow((deer[0] - nx), 2) + Math.pow((deer[1] - ny), 2));
            if (nx < 1 || nx > N || ny < 1 || ny > N) {
                continue;
            }

            if (map[nx][ny] != 0) {
                continue;
            }

            if (nDist < dist) {
                dir = i;
                dist = nDist;
                x = nx;
                y = ny;
            }
        }

        map[x][y] = santaIdx;
        santa.x = x;
        santa.y = y;

        // 사슴 충돌여부 확인
        if (deer[0] == x && deer[1] == y) {
            collide(turn, santaIdx, (dir + 2) % 4, D);
        }
    }

    private static void printAnswer() {
        for (int i = 1; i <= P; i++) {
            System.out.printf("%d ", santas[i].score);
        }
    }

    static class Santa {
        int x;
        int y;
        int score;
        int canMove;
        boolean inRange;

        public Santa(int x, int y) {
            this.x = x;
            this.y = y;
            score = 0;
            canMove = 0;
            inRange = true;
        }
    }
}