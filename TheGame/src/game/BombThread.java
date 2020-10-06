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
		
		
	}
	
	public void makeBombs() throws IOException {
		pair p = ServerGame.getRandomFreePosition();
		Bomb b=new Bomb(p.getX(),p.getY(),5000);
	bombs.add(b);
//		ServerGame.sendGameUpdate(b);
	
	}
}
