import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Checkpoint {
    public static void main(String[] args) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while(true)
            try {
                String input = reader.readLine();
                act(input);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(0);
            }
    }

    static void act(String input) throws Exception{
        String[] msg = input.split(" ");
        String output = "";
        int turn;
        boolean found = false;
        switch(msg[0]){
        case "BEGIN":
            size = Integer.parseInt(msg[3]);
            grid = new int[size][size];
            target = size/2;
            break;
        case "DESTROY":
            turn = Integer.parseInt(msg[1]);
            for(int x=0;x<size;x+=2)
                for(int y=0;y<size&&!found;y++)
                    if(grid[(x+turn*2)%size][(y+target)%size]==INACTIVE){
                        output = "VERTEX " + ((x+turn*2)%size) + "," + ((y+target)%size);
                        found = true;
                    }
            if(output.length() < 1)
                output = "NONE";
            break;
        case "BROKEN":
            for(int i=2;i<msg.length;i++){
                String[] tokens = msg[i].split(",");
                if(tokens.length>1){
                    int x = Integer.parseInt(tokens[0]);
                    int y = Integer.parseInt(tokens[1]);                    
                    if(grid[x][y]==INACTIVE)
                        grid[x][y] = BROKEN;
                }
            }
            break;
        case "ACTIVATE":
            turn = Integer.parseInt(msg[1]);
            for(int x=1;x<size;x+=2)
                for(int y=0;y<size&&!found;y++)
                    if(grid[(x+turn*2)%size][(y+target)%size]==INACTIVE){
                        output = "VERTEX " + ((x+turn*2)%size) + "," + ((y+target)%size);
                        found = true;
                    }
            if(output.length() < 1)
                output = "NONE";
            break;
        case "OWNED":
            for(int i=2;i<msg.length;i++){
                String[] tokens = msg[i].split(",");
                if(tokens.length>1){
                    int x = Integer.parseInt(tokens[0]);
                    int y = Integer.parseInt(tokens[1]);
                    if(i==2){
                        if(grid[x][y]==INACTIVE)
                            grid[x][y] = MINE;
                    }else{
                        if(grid[x][y]==INACTIVE)
                            grid[x][y]=ACTIVE;
                    }
                }
            }
            break;
        case "SCORE":
            System.exit(0);
            break;
        }
        if(output.length()>0)
            System.out.println(output);
    }

    static int size = 2;
    static int target = size/2;
    static int[][] grid = new int[size][size];

    static final int INACTIVE = 0;
    static final int ACTIVE   = 1;
    static final int BROKEN   = 2;
    static final int MINE     = 3;
}