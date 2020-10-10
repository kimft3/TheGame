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
		}
	}

	public static void sendGameUpdate(Player me) throws InterruptedException {
		try {
			String playerData = "";
			if (me.getDirection() == null) { // player quitting
				players.remove(players.indexOf(me));
				if(players.size()>0) {
				for (Player p : players) {
					playerData = "q" + "#" + me.getName() + "#" + me.getXpos() + "#" + me.getYpos();
					p.getOutStream().writeBytes(playerData + '\n');
				}}
			}
			else{
			if (me.getXposOld() == -2) { // new player, all players current status is send
				for (Player p : players) {
					playerData = "m" + "#" + p.getName() + "#" + p.getPoint() + "#" + 0 + "#" + 0 + "#" + p.getXpos()
							+ "#" + p.getYpos() + "#" + p.getDirection();
					me.getOutStream().writeBytes(playerData + '\n');
				}
			}
			for (Player p : players) {
				playerData = "m" + "#" + me.getName() + "#" + me.getPoint() + "#" + me.getXposOld() + "#"
						+ me.getYposOld() + "#" + me.getXpos() + "#" + me.getYpos() + "#" + me.getDirection() + '\n';
				p.getOutStream().writeBytes(playerData);
			}
			}
		} catch (IOException e) {
			System.out.println("l-50 servergame");
//			e.printStackTrace();
		}
	}

	public static synchronized void sendGameUpdate(Bomb bomb, String action) throws IOException {
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
				Thread.sleep(150); // Making the explosion visible, and a small lag--------------
			} catch (InterruptedException e) {
				System.out.println("l 69 servergame");
//				e.printStackTrace();
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
		if(receiveString == null) {
//			System.out.println("tadahh");
		}else {
		String[] playerMessage = receiveString.split("#");
		String playerName;
		playerName = playerMessage[1];
		char id = playerMessage[0].charAt(0);
		switch (id) {
		case 'j': //new player j(oining)
			if (!isNameUnique(playerName)) {
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
				BombThread bt = new BombThread();
				bt.start();
				players.add(newPlayer);
				
				sendGameUpdate(newPlayer);
			}
			break;
		case 'm': //player m(oving)
			for (Player pl : players) {
				if (pl.getName().equals(playerMessage[1])) {
					updatePlayer(pl, Integer.parseInt(playerMessage[2]), Integer.parseInt(playerMessage[3]),
							playerMessage[4]);
				}
			}
			break;
		case 'b': // bomb exploding
			updatePlayer(null, Integer.parseInt(playerMessage[1]), Integer.parseInt(playerMessage[2]), "here");
			break;
		
		case 'q': //player leaving
			System.out.println("I'm leaving");
			if(players.size()>0) {
				System.out.println(players.toString());
			for (Player pl : players) {
				if (pl.getName().equals(playerMessage[1])) {
					updatePlayer(pl, pl.getXpos(), pl.getYpos(),
							null);
					break;
				}
			}
			}
			break;
	}}
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
	if(direction==null) {
		System.out.println("me too");
		me.setDirection(direction);

//		players.remove(players.indexOf(me));
		sendGameUpdate(me);
	}
		
		
		else if (me == null) { //call from bomb thread, using same method because of synchronizing
			updatePlayerExploded(delta_x, delta_y);
		} else {
			me.setDirection(direction);
			int x = me.getXpos(), y = me.getYpos();
			Bomb b = getBombAt(x + delta_x, y + delta_y);
			if (Generel.board[y + delta_y].charAt(x + delta_x) == 'w') {
				me.addPoints(-1);
			} else if (b != null) {
				// do nothing-------------------------------
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
	}

	private static void updatePlayerExploded(int x, int y) throws InterruptedException {
		List<Player> playersToExplode = new ArrayList<>();
		for (int delta_x = -1; delta_x < 2; delta_x++) { // finding all players around the bomb
			for (int delta_y = -1; delta_y < 2; delta_y++) {
				playersToExplode.add(getPlayerAt(x + delta_x, y + delta_y));
			}
			for (Player p : playersToExplode) {
				if (p != null) {
					p.addPoints(-50);
					pair pa = getRandomFreePosition();
					p.setXpos(pa.getX());
					p.setYpos(pa.getY());
					sendGameUpdate(p);
				}
			}
		}
	}

	private static Bomb getBombAt(int x, int y) {
		for (Bomb b : bombs) {
			if (b.getXpos() == x && b.getYpos() == y) {
				return b;
			}
		}
		return null;
	}

	public static Player getPlayerAt(int x, int y) {
		for (Player p : players) {
			if (p.getXpos() == x && p.getYpos() == y) {
				return p;
			}
		}
		return null;
	}

}
