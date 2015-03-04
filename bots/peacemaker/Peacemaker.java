import java.awt.Point;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class Peacemaker {
    private static final int INACTIVE = 0;
    private static final int ACTIVE   = 1;
    private static final int BROKEN   = 2;
    private static final int MINE     = 3;

    private int size = 0;
    private int[][] grid = new int[size][size];
    private int startingPoint = 0;

    public static void main(String[] args) {
        new Peacemaker().start();
    }

    private void start() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while(true) {
            try {
                String input = reader.readLine();
                act(input);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
    }

    private void act(String input) throws Exception {
        String[] msg = input.split(" ");
        String output = "";
        int turn;
        switch(msg[0]){
        case "BEGIN":
            size = Integer.parseInt(msg[3]);
            grid = new int[size][size];
            break;
        case "DESTROY":
            output = "NONE";
            break;
        case "BROKEN":
            update(msg, true);
            break;
        case "ACTIVATE":
            turn = Integer.parseInt(msg[1]);
            output = activate(turn);
            break;
        case "OWNED":
            update(msg, false);
            break;
        case "SCORE":
            System.exit(0);
            break;
        }
        if (output.length() > 0) {
            System.out.println(output);
        }
    }

    private String activate(int turn) {
        Random r = new Random();
        if (turn == 0) {
            startingPoint = r.nextInt(size);
            return "VERTEX " + startingPoint + "," + 0;
        } else {

            Point point = searchConflicts();

            int posX = point.x;
            int posY = point.y;
            int count = 1;
            while (grid[posX][posY] == BROKEN || grid[posX][posY] == MINE) {
                if (count + posX > size || count + posY > size)
                    count = -count;
                if (count == 0)
                    count = 1;
                posX+= r.nextInt(Math.abs(count)) * Math.signum(count);
                posY+= r.nextInt(Math.abs(count)) * Math.signum(count);

                count++;
            }

            return "VERTEX " + posX + "," + posY;
        }
    }

    private Point searchConflicts() {

        int previousCellScore = 0;
        int cellX = 0;
        int cellY = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j ++) {
                if (previousCellScore < adjacentCellsScore(i, j)) {
                    cellX = i; cellY = j;
                    previousCellScore = adjacentCellsScore(i, j);
                }
            }
        }
        return new Point(cellX, cellY);
    }

    /*  Format of adjacent cells :
     * 
     *   0 1 2
     *   3 . 4
     *   5 6 7
     */
    private int adjacentCellsScore(int x, int y) {

        int[] scores = new int[8];

        int previousX = (x - 1 < 0 ? size - 1 : x - 1);
        int nextX = (x + 1 > size - 1 ? 0 : x + 1);
        int previousY = (y - 1 < 0 ? size - 1 : y - 1);
        int nextY = (y + 1 > size - 1 ? 0 : y + 1);

        scores[0] = calcScore(previousX, nextY);
        scores[1] = calcScore(x, nextY);
        scores[2] = calcScore(nextX, nextY);
        scores[3] = calcScore(previousX, y);
        scores[4] = calcScore(nextX, y);
        scores[5] = calcScore(previousX, previousY);
        scores[6] = calcScore(x, previousY);
        scores[7] = calcScore(nextX, previousY);

        return IntStream.of(scores).reduce(0, (a, b) -> a + b);
    }

    private int calcScore(int x, int y) {
        int activeScore = 2;
        int mineScore = 1;
        int inactiveScore = 0;
        int brokenScore = 3;

        if (grid[x][y] == ACTIVE) 
            return activeScore;
        else if (grid[x][y] == MINE)
            return mineScore;
        else if (grid[x][y] == INACTIVE) 
            return inactiveScore;
        else if (grid[x][y] == BROKEN) 
            return brokenScore;
        else
            return 0;
    }


    private void update(String[] args, boolean destroyPhase) {
        for(int i = 2; i < args.length; i++) {
            String[] tokens = args[i].split(",");
            if(tokens.length > 1){
                int x = Integer.parseInt(tokens[0]);
                int y = Integer.parseInt(tokens[1]);
                if (grid[x][y] == INACTIVE) {
                    if (destroyPhase) {
                        grid[x][y] = BROKEN;
                    } else if (i == 2) {
                        grid[x][y] = MINE;
                    } else {
                        grid[x][y] = ACTIVE;
                    }
                }
            }
        }
    }       
}