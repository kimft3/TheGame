package network;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import game.Generel;
import game.Player;
import game.ServerThread;
import game.pair;

public class ServerGame {
	public static List<Player> players = new ArrayList<Player>();

	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {
		ServerSocket talk = new ServerSocket(12345);
		while (true) {
			Socket connectionSocket = talk.accept();
			ServerThread st = (new ServerThread(connectionSocket));
			st.start();
		}
	}

//	TODO Bomber
//	TODO spiller synlig hos alle clienter efter at blive trådt på og ved start

	public static void sendGameUpdate(Player me) throws InterruptedException {
		TimeUnit.MILLISECONDS.sleep(100);
		try {
			String playerData = "";
			if (me.getXposOld() < 1) {
				for (Player p : players) {
					playerData = p.getName() + "#" + p.getPoint() + "#" + 0 + "#" + 0 + "#" + p.getXpos() + "#"
							+ p.getYpos() + "#" + p.getDirection();
					me.getOutStream().writeBytes(playerData + '\n');
					TimeUnit.MILLISECONDS.sleep(100);
				}
			} else {
				for (Player p : players) {
					p.getOutStream().writeBytes(
							me.getName() + "#" + me.getPoint() + "#" + me.getXposOld() + "#" + me.getYposOld() + "#"
									+ me.getXpos() + "#" + me.getYpos() + "#" + me.getDirection() + '\n');
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static boolean isNameUnique(String name) {
		for (Player p : players) {
			if (p.getName().equals(name))
				return false;
		}
		return true;
	}

	public static synchronized void play(String receiveString, DataOutputStream outToClient)
			throws InterruptedException {
		String[] playerMessage = receiveString.split("#");
		String playerName;
		playerName = playerMessage[1];
		char id = playerMessage[0].charAt(0);
		switch (id) {
		case 'j':
			if (!ServerGame.isNameUnique(playerName)) {
				try {
					(outToClient).writeBytes("Name is taken" + '\n');
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				pair p = getRandomFreePosition();
				Player newPlayer = new Player(playerName, p.getX(), p.getY(), "up", outToClient);
				players.add(newPlayer);
				sendGameUpdate(newPlayer);
			}
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

	public static pair getRandomFreePosition() {
		int x = 1;
		int y = 1;
		boolean found = false;
		while (!found) {
			Random r = new Random();
			x = Math.abs(r.nextInt() % 18) + 1;
			y = Math.abs(r.nextInt() % 18) + 1;
			if (Generel.board[y].charAt(x) == ' ') {
				found = true;
				for (Player p : players) {
					if (p.getXpos() == x && p.getYpos() == y)
						found = false;
				}
			}
		}
		pair p = new pair(x, y);
		return p;
	}

	public synchronized static void updatePlayer(Player me, int delta_x, int delta_y, String direction)
			throws InterruptedException {
		me.setDirection(direction);
		int x = me.getXpos(), y = me.getYpos();
		if (Generel.board[y + delta_y].charAt(x + delta_x) == 'w') {
			me.addPoints(-1);
		} else {
			Player p = getPlayerAt(x + delta_x, y + delta_y);
			if (p != null) {
				me.addPoints(10);
				p.addPoints(-10);
				pair pa = getRandomFreePosition();
				p.setXpos(pa.getX());
				p.setYpos(pa.getY());
				p.setXposOld(-1);
				p.setYposOld(-1);
				sendGameUpdate(p);
			} else {
				me.addPoints(1);
			}
			me.setXpos(x + delta_x);
			me.setYpos(y + delta_y);

		}
		sendGameUpdate(me);
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
