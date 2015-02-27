import java.awt.Point;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Connector {
    private static final int INACTIVE = 0;
    private static final int ACTIVE   = 1;
    private static final int BROKEN   = 2;
    private static final int MINE     = 3;

    private int size = 0;
    private int[][] grid = new int[size][size];
    private int startingPoint = 0;
    private final List<Point> path = new ArrayList<>();

    public static void main(String[] args) {
        new Connector().start();
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
        if (turn == 0) {
            Random r = new Random();
            startingPoint = r.nextInt(size);
            return "VERTEX " + startingPoint + "," + 0;
        }
        path.clear();
        Point lastCell = findLastPathCell(startingPoint, 0);
        if (lastCell.y == size-1) {
            //path is done
            Point extendingPathPoint = findExtendingPathPoint();
            if (extendingPathPoint == null) {
                return "NONE";
            }
            return "VERTEX " + extendingPathPoint.x + "," + extendingPathPoint.y;
        } else {
            int x = findBestX(lastCell.x, lastCell.y);
            return "VERTEX " + x + "," + (lastCell.y + 1);
        }
    }

    private int findBestX(int x, int y) {
        int bestScore = Integer.MIN_VALUE;
        int bestX = 0;
        for (int i = -1; i <= 1; i++) {
            int newY = y + 1;
            int newX = (x + i + size) % size;
            int score = calcCellScore(newX, newY, 10);
            if (score > bestScore) {
                bestScore = score;
                bestX = newX;
            } else if (score == bestScore && Math.random() < 0.3) {
                bestX = newX;
            }
        }
        return bestX;
    }

    private int calcCellScore(int x, int y, int depth) {
        int newY = y + 1;
        if (depth < 0) {
            return 1;
        }
        if (newY >= size)
            return 100;
        int cellScore = 0;
        for (int i = -1; i <= 1; i++) {
            int newX = (x + i + size) % size;
            if (grid[newX][newY] == ACTIVE || grid[newX][newY] == MINE) {
                cellScore += 5;
            } else if (grid[newX][newY] == INACTIVE) {
                cellScore += 1;             
            } else {
                cellScore -= 2;
            }
            cellScore += calcCellScore(newX, newY, depth -1);
        }
        return cellScore;
    }

    private Point findLastPathCell(int x, int y) {
        Point thisCell = new Point(x,y);
        int newY = y + 1;
        if (newY >= size) {
            return thisCell;
        }
        List<Point> endCells = new ArrayList<>();
        endCells.add(thisCell);
        path.add(thisCell);
        for (int i = -1; i <= 1; i++) {
            int newX = (x + i + size) % size;
            if (grid[newX][newY] == ACTIVE || grid[newX][newY] == MINE) {
                endCells.add(findLastPathCell(newX, newY));
            }
        }
        int bestY = -1;
        Point bestPoint = null;
        for (Point p : endCells) {
            if (p.y > bestY) {
                bestY = p.y;
                bestPoint = p;
            }
        }
        return bestPoint;
    }

    private Point findExtendingPathPoint() {
        Random rand = new Random();
        for (int i = 0; i < size; i++) {
            Point cell = path.get(rand.nextInt(path.size()));
            for (int j = -1; j <= 1; j += 2) {
                Point newCellX = new Point((cell.x + j + size) % size, cell.y);
                if (grid[newCellX.x][newCellX.y] == INACTIVE)
                    return newCellX;

                Point newCellY = new Point(cell.x, cell.y + j);
                if (cell.y < 0 || cell.y >= size)
                    continue;
                if (grid[newCellY.x][newCellY.y] == INACTIVE)
                    return newCellY;
            }
        }
        return null;
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