package game;

public class Bomb {
	int xpos;
	int ypos;
	long timer;
	
	public Bomb(int xpos,	int ypos,	long timer) {
		this.xpos=xpos;
		this.ypos=ypos;
		this.timer=timer;
	}
	
	public void setXpos(int xpos) {
		this.xpos = xpos;
	}public int getXpos() {
		return xpos;
	}public void setYpos(int ypos) {
		this.ypos = ypos;
	}public int getYpos() {
		return ypos;
	}public void setTimer(long timer) {
		this.timer = timer;
	}public long getTimer() {
		return timer;
	}
	
}
