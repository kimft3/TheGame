package threads;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
			try {
				makeBombs();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void makeBombs() throws IOException, InterruptedException {
		while(true) {
		Thread.sleep(5000);
		pair p = ServerGame.getRandomFreePosition();
		b=new Bomb(p.getX(),p.getY());
		ServerGame.sendGameUpdate(b,"load");
		Thread.sleep(5000);
		ServerGame.sendGameUpdate(b,"expload");
		}
	}
}
