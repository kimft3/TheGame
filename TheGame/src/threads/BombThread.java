package threads;

import java.io.IOException;

import network_Game.ServerGame;
import objects.Bomb;
import objects.pair;

public class BombThread extends Thread {
	Bomb b;

	public BombThread() {
		super();
	}

	@Override
	public void run() {
		try {
			makeBombs();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}

	}

	public void makeBombs() throws IOException, InterruptedException {
		while (true) {
			Thread.sleep(5000);
			pair p = ServerGame.getRandomFreePosition();
			b = new Bomb(p.getX(), p.getY());
			ServerGame.sendGameUpdate(b, "load");
			Thread.sleep(5000);
			ServerGame.sendGameUpdate(b, "expload");
		}
	}
}
