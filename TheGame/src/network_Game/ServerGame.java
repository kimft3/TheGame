package network_Game;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import objects.Bomb;
import objects.Generel;
import objects.Player;
import objects.pair;
import threads.BombThread;
import threads.ServerThread;

public class ServerGame {
	public static List<Player> players = new ArrayList<>();
	public static List<Bomb> bombs = new ArrayList<>();

	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {
		ServerSocket talk = new ServerSocket(12345);
		while (true) {
			Socket connectionSocket = talk.accept();
			ServerThread st = (new ServerThread(connectionSocket));
			st.start();
			BombThread bt = new BombThread();
			bt.start();
		}
	}

	public static void sendGameUpdate(Player me) throws InterruptedException {
		try {
			String playerData = "";
			if (me.getXposOld() == -2) { // new player
				for (Player p : players) {
					playerData = "m" + "#"+p.getName() + "#" + p.getPoint() + "#" + 0 + "#" + 0 + "#" + p.getXpos() + "#"
							+ p.getYpos() + "#" + p.getDirection();
					me.getOutStream().writeBytes(playerData + '\n');
				}
			}
			for (Player p : players) {
				String s = "m" + "#"+me.getName() + "#" + me.getPoint() + "#" + me.getXposOld() + "#" + me.getYposOld() + "#"
						+ me.getXpos() + "#" + me.getYpos() + "#" + me.getDirection() + '\n';
				p.getOutStream().writeBytes(s);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void sendGameUpdate(Bomb bomb, String action) throws IOException {
		if (action.equals("load")) {
			for (Player p : players) {
				String s = "b" + "#" + bomb.getXpos() + "#" + bomb.getYpos() + '\n';
				p.getOutStream().writeBytes(s);
			}
		} else {
			for (Player p : players) {
				String s = "e" + "#" + bomb.getXpos() + "#" + bomb.getYpos() + '\n';
				p.getOutStream().writeBytes(s);
			}
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			for (Player p : players) {
				String s = "w" + "#" + bomb.getXpos() + "#" + bomb.getYpos() + '\n';
				p.getOutStream().writeBytes(s);
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
				try {
					(outToClient).writeBytes("Good to go" + '\n');
				} catch (IOException e) {
					e.printStackTrace();
				}
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
				for (Bomb b : bombs) {
					if (b.getXpos() == x && b.getYpos() == y)
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
		Player p = getPlayerAt(x + delta_x, y + delta_y);
		Bomb b= getBombAt(x + delta_x, y + delta_y);
		if (Generel.board[y + delta_y].charAt(x + delta_x) == 'w') {
			me.addPoints(-1);
		}else if(b!=null) {
			if(b.getBombExplode()) {
			me.addPoints(-50);
			pair pa = getRandomFreePosition();
			me.setXpos(pa.getX());
			me.setYpos(pa.getY());
			
			}
		} 
		
		
		else {
			
			if (p != null) {
				me.addPoints(10);
				p.addPoints(-10);
				pair pa = getRandomFreePosition();
				p.setXpos(pa.getX());
				p.setYpos(pa.getY());
				p.setXposOld(-1);
				p.setYposOld(-1);
				sendGameUpdate(p);
			}
			 else {
				me.addPoints(1);
			}
			me.setXpos(x + delta_x);
			me.setYpos(y + delta_y);
		
		}
		
		
		sendGameUpdate(me);
	}

	private static Bomb getBombAt(int x, int y) {
		for (Bomb b : ServerGame.bombs) {
			if (b.getXpos() == x && b.getYpos() == y) {
				return b;
			}
		}
		return null;
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
