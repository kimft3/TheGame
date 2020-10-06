package game;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.sql.Time;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;

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

public class ClientGame extends Application {

	public static final int size = 30;
	public static final int scene_height = size * 20 + 50;
	public static final int scene_width = size * 20 + 200;

	public static Image image_floor;
	public static Image image_wall;
	public static Image hero_right, hero_left, hero_up, hero_down,bomb,explode;

	static String name;

	private static Label[][] fields;
	private static TextArea scoreList;
	static String sendString = "";

	static Socket clientSocket;
	static DataOutputStream outToServer;
	static ClientThread ct;
	static HashMap<String, String> playerScore = new HashMap<>();

	static int counter = 0;

	public static void main(String args[]) throws Exception {
		boolean nameNotValid = true;
		String message = "Enter player name";
		String reply = "";
		while (nameNotValid) {
			name = JOptionPane.showInputDialog(message);
			clientSocket = new Socket("localhost", 12345);// Connections is established, 3 text (send-receive-send)
			outToServer = new DataOutputStream(clientSocket.getOutputStream());
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			try {
				outToServer.writeBytes("j" + "#" + name + "#" + "" + "#" + "" + "#" + '\n');
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("kurt");
			}

			reply = inFromServer.readLine();
			nameNotValid = reply.contains("Name is taken");
			if (nameNotValid) {
				message = "enter a different name";
			}
		}

		ct = new ClientThread(clientSocket);
		ct.start();
		launch(args);
	}

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

			image_wall = new Image(getClass().getResourceAsStream("Image/wall4.png"), size, size, false, false);
			image_floor = new Image(getClass().getResourceAsStream("Image/floor1.png"), size, size, false, false);

			hero_right = new Image(getClass().getResourceAsStream("Image/heroRight.png"), size, size, false, false);
			hero_left = new Image(getClass().getResourceAsStream("Image/heroLeft.png"), size, size, false, false);
			hero_up = new Image(getClass().getResourceAsStream("Image/heroUp.png"), size, size, false, false);
			hero_down = new Image(getClass().getResourceAsStream("Image/heroDown.png"), size, size, false, false);
			bomb = new Image(getClass().getResourceAsStream("Image/bomb.png"), size, size, false, false);
			explode = new Image(getClass().getResourceAsStream("Image/explode.png"), size, size, false, false);


			fields = new Label[20][20];
			for (int j = 0; j < 20; j++) {
				for (int i = 0; i < 20; i++) {
					switch (Generel.board[j].charAt(i)) {
					case 'w':
						fields[i][j] = new Label("", new ImageView(image_wall));
						break;
					case ' ':
						fields[i][j] = new Label("", new ImageView(image_floor));
						break;
					default:
						throw new Exception("Illegal field value: " + Generel.board[j].charAt(i));
					}
					boardGrid.add(fields[i][j], i, j);
				}
			}
			scoreList.setEditable(false);

			grid.add(mazeLabel, 0, 0);
			grid.add(scoreLabel, 1, 0);
			grid.add(boardGrid, 0, 1);
			grid.add(scoreList, 1, 1);

			Scene scene = new Scene(grid, scene_width, scene_height);
			primaryStage.setScene(scene);
			primaryStage.show();

			scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
				switch (event.getCode()) {
				case UP:
					try {
						playerMoved(0, -1, "up");
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					break;
				case DOWN:
					try {
						playerMoved(0, +1, "down");
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					break;
				case LEFT:
					try {
						playerMoved(-1, 0, "left");
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					break;
				case RIGHT:
					try {
						playerMoved(+1, 0, "right");
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					break;
				default:
					break;
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void updateBoard(int oldx, int oldy, int newx, int newy, String direction) {
		System.out.println(oldx+" "+oldy+" "+newx+" "+newy +" "+direction );
		if (oldx > 0 && oldy > 0) {
			Platform.runLater(() -> {
				fields[oldx][oldy].setGraphic(new ImageView(image_floor));
			});
		}
		
		
		Platform.runLater(() -> {
			if (direction.equals("right")) {
				fields[newx][newy].setGraphic(new ImageView(hero_right));
			}
			;
			if (direction.equals("left")) {
				fields[newx][newy].setGraphic(new ImageView(hero_left));
			}
			;
			if (direction.equals("up")) {
				fields[newx][newy].setGraphic(new ImageView(hero_up));
			}
			;
			if (direction.equals("down")) {
				fields[newx][newy].setGraphic(new ImageView(hero_down));
			}
			;
			if (direction.equals("bomb")) {
				fields[newx][newy].setGraphic(new ImageView(bomb));
			}
			;
			if (direction.equals("explode")) {
				fields[newx][newy].setGraphic(new ImageView(explode));
			}
			;
			if (direction.equals("floor")) {
				fields[newx][newy].setGraphic(new ImageView(image_floor));
			}
			;
			
			
		});

	}

	public static void updateScoreTable() {
		Platform.runLater(() -> {
			scoreList.setText(getScoreList());
		});
	}


	public void playerMoved(int delta_x, int delta_y, String direction) throws InterruptedException {
//		TimeUnit.MILLISECONDS.sleep(100);
		counter++;
		sendString = "m" + "#" + name + "#" + delta_x + "#" + delta_y + "#" + direction + "#" + counter + '\n';
		try {
			outToServer.writeBytes(sendString);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getScoreList() {
		StringBuffer b = new StringBuffer(100);
		for (Entry<String, String> entry : playerScore.entrySet()) {
			b.append(entry + "\r\n");
		}
		return b.toString();
	}

	public static void updateScore(String name, String score) {
		if (!playerScore.containsKey(name)) {
			playerScore.put(name, score);
		} else {
			playerScore.replace(name, score);
		}
		updateScoreTable();
	}

}
