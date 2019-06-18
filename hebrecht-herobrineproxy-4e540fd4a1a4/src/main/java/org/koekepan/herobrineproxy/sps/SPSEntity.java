package org.koekepan.herobrineproxy.sps;

public class SPSEntity {
	private int x;
	private int y;
	private int z;
	private int entityID;
	
	public SPSEntity() {}
	
	public SPSEntity(int entityID, int x, int y, int z) {
		this.entityID = entityID;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	public void setZ(int z) {
		this.z = z;
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}
	
	public int getZ() {
		return this.z;
	}
}
