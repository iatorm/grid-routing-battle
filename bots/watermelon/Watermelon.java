import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Watermelon {

    private static int numberOfBots;
    private static int numberOfTurns;
    private static int sideLength;

    private static int turn = 0;

    private static int[][] theGrid;

    private static final int INACTIVE = -2;
    private static final int BROKEN   = -1;
    private static final int MINE     =  0;
    private static final int ACTIVE   =  1;

    private static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    private static PrintStream out = System.out;

    public static void main(String[] args) throws IOException {
        while (true){
            String[] input = in.readLine().trim().split(" ");
            String instruction = input[0];
            switch (instruction){
                case "BEGIN":
                    begin(input);
                    break;
                case "DESTROY":
                    destroy(input);
                    break;
                case "BROKEN":
                    broken(input);
                    break;
                case "ACTIVATE":
                    activate(input);
                    break;
                case "OWNED":
                    owned(input);
                    break;
                default:
                    return;
            }
            out.flush();
        }
    }

    private static void begin(String[] input) {
        numberOfBots = Integer.parseInt(input[1]);
        numberOfTurns = Integer.parseInt(input[2]);
        sideLength = Integer.parseInt(input[3]);
        theGrid = new int[sideLength][sideLength];
        for (int x = 0; x < sideLength; x++){
            for (int y = 0; y < sideLength; y++){
                theGrid[x][y] = INACTIVE;
            }
        }
    }

    private static void owned(String[] input) {
        turn = Integer.parseInt(input[1]);
        for (int i = input.length - 1; i >= 2; i--){
            if (input[i].equals("N")){
                continue;
            }
            String[] coordinates = input[i].split(",");
            int x = Integer.parseInt(coordinates[0]);
            int y = Integer.parseInt(coordinates[1]);
            int player = i - 2;
            if (player == 0){
                theGrid[x][y] = MINE;
            } else {
                theGrid[x][y] = ACTIVE;
            }
        }
    }

    private static void activate(String[] input) {
        turn = Integer.parseInt(input[1]);
        double[][] values = new double[sideLength][sideLength];
        List<Point> pointList = new ArrayList<>();
        for (int x = 0; x < sideLength; x++){
            for (int y = 0; y < sideLength; y++){
                if (theGrid[x][y] == MINE || theGrid[x][y] == ACTIVE){
                    for (int x1 = 0; x1 < sideLength; x1++){
                        for (int y1 = 0; y1 < sideLength; y1++){
                            double distance = Math.pow(x - x1, 2) + Math.pow(y - y1, 2);
                            values[x1][y1] += 1 / (distance + 1);
                        }
                    }
                }
                pointList.add(new Point(x, y));
            }
        }
        pointList.sort(Comparator.comparingDouble((Point a) -> values[a.x][a.y]).reversed());
        for (Point point : pointList){
            if (theGrid[point.x][point.y] == INACTIVE){
                out.println("VERTEX " + point.x + "," + point.y);
                return;
            }
        }
        out.println("NONE");
    }

    private static void broken(String[] input) {
        turn = Integer.parseInt(input[1]);
        for (int i = 2; i < input.length; i++){
            if (input[i].equals("N")){
                continue;
            }
            String[] coordinates = input[i].split(",");
            int x = Integer.parseInt(coordinates[0]);
            int y = Integer.parseInt(coordinates[1]);
            theGrid[x][y] = BROKEN;
        }
    }

    private static void destroy(String[] input) {
        turn = Integer.parseInt(input[1]);
        double[][] values = new double[sideLength][sideLength];
        List<Point> pointList = new ArrayList<>();
        for (int x = 0; x < sideLength; x++){
            for (int y = 0; y < sideLength; y++){
                if (theGrid[x][y] == MINE){
                    for (int x1 = 0; x1 < sideLength; x1++){
                        for (int y1 = 0; y1 < sideLength; y1++){
                            double distance = Math.pow(x - x1, 2) + Math.pow(y - y1, 2);
                            values[x1][y1] -= 1 / (distance + 1);
                        }
                    }
                }
                if (theGrid[x][y] == ACTIVE){
                    for (int x1 = 0; x1 < sideLength; x1++){
                        for (int y1 = 0; y1 < sideLength; y1++){
                            double distance = Math.pow(x - x1, 2) + Math.pow(y - y1, 2);
                            values[x1][y1] += 1 / (distance + 1) / (numberOfBots - 1);
                        }
                    }
                }
                pointList.add(new Point(x, y));
            }
        }
        pointList.sort(Comparator.comparingDouble((Point a) -> values[a.x][a.y]).reversed());
        for (Point point : pointList){
            if (theGrid[point.x][point.y] == INACTIVE){
                out.println("VERTEX " + point.x + "," + point.y);
                return;
            }
        }
        out.println("NONE");
    }
}