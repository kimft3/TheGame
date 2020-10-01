package game;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ClientGame extends Application{

	public static final int size = 30; 
	public static final int scene_height = size * 20 + 50;
	public static final int scene_width = size * 20 + 200;

	public static Image image_floor;
	public static Image image_wall;
	public static Image hero_right,hero_left,hero_up,hero_down;

	public static Player me;
	public static List<Player> players = new ArrayList<Player>();

	private static Label[][] fields;
	private TextArea scoreList;
	static String sendString="";
	static String receiveString="";
	
	static Socket clientSocket;
	static DataOutputStream outToServer;
	
	public static void main(String args[]) throws Exception{
		
		
		
		
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		 clientSocket= new Socket("localhost",12345);//Connections is established, 3 text (send-receive-send)
		 outToServer = new DataOutputStream(clientSocket.getOutputStream());
		 BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			try {
				outToServer.writeBytes("j"+"#"+"Kurt"+"#"+""+"#"+""+"#"+'\n');
			} catch (IOException e) {
				e.printStackTrace();
			} 
			
			launch(args);
		 while(true) {
		receiveString = inFromServer.readLine();
		System.out.println("C Receiving " + receiveString);
		updateGame(receiveString);
		 }
		
		
		
		
		
			
		
//		clientSocket.close();// 2 text, send-receive
	
	}
	
	

	// -------------------------------------------
	// | Maze: (0,0)              | Score: (1,0) |
	// |-----------------------------------------|
	// | boardGrid (0,1)          | scorelist    |
	// |                          | (1,1)        |
	// -------------------------------------------

	@Override
	public void start(Stage primaryStage) {
		try {
			GridPane grid = new GridPane();
			grid.setHgap(10);
			grid.setVgap(10);
			grid.setPadding(new Insets(0, 10, 0, 10));

			Text mazeLabel = new Text("Maze:");
			mazeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
	
			Text scoreLabel = new Text("Score:");
			scoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));

			scoreList = new TextArea();
			
			GridPane boardGrid = new GridPane();

			image_wall  = new Image(getClass().getResourceAsStream("Image/wall4.png"),size,size,false,false);
			image_floor = new Image(getClass().getResourceAsStream("Image/floor1.png"),size,size,false,false);

			hero_right  = new Image(getClass().getResourceAsStream("Image/heroRight.png"),size,size,false,false);
			hero_left   = new Image(getClass().getResourceAsStream("Image/heroLeft.png"),size,size,false,false);
			hero_up     = new Image(getClass().getResourceAsStream("Image/heroUp.png"),size,size,false,false);
			hero_down   = new Image(getClass().getResourceAsStream("Image/heroDown.png"),size,size,false,false);

			fields = new Label[20][20];
			for (int j=0; j<20; j++) {
				for (int i=0; i<20; i++) {
					switch (Generel.board[j].charAt(i)) {
					case 'w':
						fields[i][j] = new Label("", new ImageView(image_wall));
						break;
					case ' ':					
						fields[i][j] = new Label("", new ImageView(image_floor));
						break;
					default: throw new Exception("Illegal field value: "+Generel.board[j].charAt(i) );
					}
					boardGrid.add(fields[i][j], i, j);
				}
			}
			scoreList.setEditable(false);
			
			
			grid.add(mazeLabel,  0, 0); 
			grid.add(scoreLabel, 1, 0); 
			grid.add(boardGrid,  0, 1);
			grid.add(scoreList,  1, 1);
						
			Scene scene = new Scene(grid,scene_width,scene_height);
			primaryStage.setScene(scene);
			primaryStage.show();

			scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
				switch (event.getCode()) {
				case UP:    playerMoved(0,-1,"up");    break;
				case DOWN:  playerMoved(0,+1,"down");  break;
				case LEFT:  playerMoved(-1,0,"left");  break;
				case RIGHT: playerMoved(+1,0,"right"); break;
				default: break;
				}
			});
		
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void updateGame(String playerInfo) {
		
    // Setting up standard players
String[] info=playerInfo.split("#");
me = new Player(info[0],Integer.parseInt(info[1]),Integer.parseInt(info[2]),info[3]);
players.add(me);
fields[me.getXpos()][me.getYpos()].setGraphic(new ImageView(hero_up));

//Player harry = new Player("Harry",p.getX(),p.getY(),"up");
//players.add(harry);
//fields[p.getX()][p.getY()].setGraphic(new ImageView(hero_up));
//
//scoreList.setText(getScoreList());
		
		
		
		
	}
	
	public pair getRandomFreePosition()
	// finds a random new position which is not wall 
	// and not occupied by other players 
	{
		int x = 1;
		int y = 1;
		boolean found = false;
		while  (!found) {
			Random r = new Random();
			x = Math.abs(r.nextInt()%18) +1;
			y = Math.abs(r.nextInt()%18) +1;
			if (Generel.board[y].charAt(x)==' ')
			{
				found = true;
				for (Player p: players) {
					if (p.getXpos()==x && p.getYpos()==y)
						found = false;
				}
				
			}
		}
		pair p = new pair(x,y);
		return p;
	}
	
	public void movePlayerOnScreen(int oldx,int oldy,int newx,int newy,String direction)
	{
		Platform.runLater(() -> {
			fields[oldx][oldy].setGraphic(new ImageView(image_floor));
			});
      		Platform.runLater(() -> {	
			if (direction.equals("right")) {
				fields[newx][newy].setGraphic(new ImageView(hero_right));
			};
			if (direction.equals("left")) {
				fields[newx][newy].setGraphic(new ImageView(hero_left));
			};
			if (direction.equals("up")) {
				fields[newx][newy].setGraphic(new ImageView(hero_up));
			};
			if (direction.equals("down")) {
				fields[newx][newy].setGraphic(new ImageView(hero_down));
			};
			});

		
	}
	
	public void updateScoreTable()
	{
		Platform.runLater(() -> {
			scoreList.setText(getScoreList());
			});
	}
	public void playerMoved(int delta_x, int delta_y, String direction) {
		sendString ="m"+"#"+me.getName()+"#"+delta_x+"#"+delta_y+"#"+direction + '\n';
		try {
			outToServer.writeBytes(sendString);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getScoreList() {
		StringBuffer b = new StringBuffer(100);
		for (Player p : players) {
			b.append(p+"\r\n");
		}
		return b.toString();
	}

	public Player getPlayerAt(int x, int y) {
		for (Player p : players) {
			if (p.getXpos()==x && p.getYpos()==y) {
				return p;
			}
		}
		return null;
	}
		
}

