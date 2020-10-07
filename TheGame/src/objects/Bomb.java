package objects;

public class Bomb {
	int xpos;
	int ypos;
	boolean bombExplode=false;

	public Bomb(int xpos, int ypos) {
		this.xpos = xpos;
		this.ypos = ypos;
	}

	public void setXpos(int xpos) {
		this.xpos = xpos;
	}

	public int getXpos() {
		return xpos;
	}

	public void setYpos(int ypos) {
		this.ypos = ypos;
	}

	public int getYpos() {
		return ypos;
	}

	public boolean getBombExplode() {
		return this.bombExplode;
	}
	public void setBobExplode() {
		this.bombExplode=true;
	}
	
	@Override
	public String toString() {
		return "b"+"#"+xpos+"#"+ypos;
	}
}
