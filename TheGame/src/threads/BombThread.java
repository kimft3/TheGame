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
			Thread.sleep(5000); // Making bombs appear with interval
			pair p = ServerGame.getRandomFreePosition();
			b = new Bomb(p.getX(), p.getY());
			ServerGame.bombs.add(b);
			ServerGame.sendGameUpdate(b, "load");
			Thread.sleep(5000); // allowing bombs to be visible before exploding
			ServerGame.sendGameUpdate(b, "expload");
			b.setBobExplode();
			ServerGame.play(b.toString(), null);
			ServerGame.bombs.remove(b);
		}
	}
}
