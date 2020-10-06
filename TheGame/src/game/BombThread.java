package game;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import network.ServerGame;

public class BombThread extends Thread {
List<Bomb> bombs=new ArrayList<>();
	
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
		Thread.sleep(5000);
		pair p = ServerGame.getRandomFreePosition();
		Bomb b=new Bomb(p.getX(),p.getY(),5000);
	bombs.add(b);
		ServerGame.sendGameUpdate(b);
	
	}
}
