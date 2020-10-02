package network;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import game.Generel;
import game.Player;
import game.ServerThread;
import game.pair;

public class ServerGame {

	public static List<Player> players = new ArrayList<Player>();
	public static ArrayList<Socket> playerSockets = new ArrayList<>(); // evt �ndre til
																		// DataOutputStream-------------------------

	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {
		String receiveString = "", sendString = "", playerName = "";
		ServerSocket talk = new ServerSocket(12345);
		while (true) {
			Socket connectionSocket = talk.accept();
			(new ServerThread(connectionSocket)).start();

			DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());

		}
	}

	public static void sendGameUpdate() {
		for (Socket s : playerSockets) {
			try {
				DataOutputStream outToClient = new DataOutputStream(s.getOutputStream());
				String playerData = "";
				for (Player p : players) {
					playerData = p.getName() + "#" + p.getPoint() + "#" + p.getXposOld() + "#" + p.getYposOld() + "#"
							+ p.getXpos() + "#" + p.getYpos() + "#" + p.getDirection();
					System.out.println("S " + playerData + '\n');
					outToClient.writeBytes(playerData + '\n');
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public static boolean isNameUnique(String name) {
		for (Player p : players) {
			if (p.getName().equals(name))
				return false;
		}
		return true;
	}

	public static void play(String receiveString, Socket serverReceiverSocket) {
		System.out.println("ServerThread " + receiveString);
		String[] playerMessage = receiveString.split("#");
		String playerName = playerName = playerMessage[1];

		char id = playerMessage[0].charAt(0);
		switch (id) {
		case 'j':

			while (!ServerGame.isNameUnique(playerName)) {
				try {
					DataOutputStream outToClient = new DataOutputStream(serverReceiverSocket.getOutputStream());
					outToClient.writeBytes("Name is taken" + '\n');
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			// Setting up standard players
			ServerGame.playerSockets.add(serverReceiverSocket);
			pair p = getRandomFreePosition();
			ServerGame.players.add(new Player(playerName, p.getX(), p.getY(), "up"));
			System.out.println("opretter ");
			ServerGame.sendGameUpdate();
			break;
		case 'm':
			for (Player pl : ServerGame.players) {
				if (pl.getName().equals(playerMessage[1])) {
					updatePlayer(pl, Integer.parseInt(playerMessage[2]), Integer.parseInt(playerMessage[3]),
							playerMessage[4]);
				}
			}

			break;
		}

	}

	public static pair getRandomFreePosition()
	// finds a random new position which is not wall
	// and not occupied by other players
	{
		int x = 1;
		int y = 1;
		boolean found = false;
		while (!found) {
			Random r = new Random();
			x = Math.abs(r.nextInt() % 18) + 1;
			y = Math.abs(r.nextInt() % 18) + 1;
			if (Generel.board[y].charAt(x) == ' ') {
				found = true;
				for (Player p : ServerGame.players) {
					if (p.getXpos() == x && p.getYpos() == y)
						found = false;
				}

			}
		}
		pair p = new pair(x, y);
		return p;
	}

	public static void updatePlayer(Player me, int delta_x, int delta_y, String direction) {

		me.setDirection(direction);
		int x = me.getXpos(), y = me.getYpos();

		if (Generel.board[y + delta_y].charAt(x + delta_x) == 'w') {
			me.addPoints(-1);
		} else {
			// prepared for collision detection
			// not quite relevant in single plaver version
			Player p = getPlayerAt(x + delta_x, y + delta_y);
			if (p != null) {
				me.addPoints(10);
				// update the other player
				p.addPoints(-10);
				pair pa = getRandomFreePosition();
				p.setXpos(pa.getX());
				p.setYpos(pa.getY());
				p.setXposOld(-1);
				p.setYposOld(-1);

				// movePlayerOnScreen(x+delta_x,y+delta_y,pa.getX(),pa.getY(),p.direction);
			} else
				me.addPoints(1);
//			movePlayerOnScreen(x,y,x+delta_x,y+delta_y,direction);
			me.setXpos(x + delta_x);
			me.setYpos(y + delta_y);

		}
		ServerGame.sendGameUpdate();

	}

	public static Player getPlayerAt(int x, int y) {
		for (Player p : ServerGame.players) {
			if (p.getXpos() == x && p.getYpos() == y) {
				return p;
			}
		}
		return null;
	}

}
