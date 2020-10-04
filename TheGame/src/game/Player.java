package game;

import java.io.DataOutputStream;

public class Player {
	String name;
	int xpos;
	int ypos;
	int point;
	String direction;

	int xposOld = -1;
	int yposOld = -1;

	DataOutputStream outStream;

	public Player(String name, int xpos, int ypos, String direction, DataOutputStream outStream) {
		this.name = name;
		this.xpos = xpos;
		this.ypos = ypos;
		this.direction = direction;
		this.point = 0;
		this.outStream = outStream;
	}

	public DataOutputStream getOutStream() {
		return outStream;
	}

	public void setOutStream(DataOutputStream outStream) {
		this.outStream = outStream;
	}

	public int getXposOld() {
		return xposOld;
	}

	public int getYposOld() {
		return yposOld;
	}

	public void setXposOld(int xposOld) {
		this.xposOld = xposOld;
	}

	public void setYposOld(int yposOld) {
		this.yposOld = yposOld;
	}

	public int getPoint() {
		return point;
	}

	public int getXpos() {
		return xpos;
	}

	public void setXpos(int xpos) {
		this.xposOld = this.xpos;
		this.xpos = xpos;
	}

	public int getYpos() {
		return ypos;
	}

	public void setYpos(int ypos) {
		this.yposOld = this.ypos;
		this.ypos = ypos;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public void addPoints(int p) {
		point += p;
	}

	@Override
	public String toString() {
		return name + ":   " + point;
	}

	public String getName() {
		return name;
	}
}
