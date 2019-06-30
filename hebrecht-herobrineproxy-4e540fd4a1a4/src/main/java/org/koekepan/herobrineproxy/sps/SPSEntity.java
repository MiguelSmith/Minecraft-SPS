package org.koekepan.herobrineproxy.sps;

import org.koekepan.herobrineproxy.ConsoleIO;

import com.github.steveice10.mc.protocol.data.game.entity.type.MobType;

public class SPSEntity {
	private double x;
	private double y;
	private double z;
	private double prevX;
	private double prevY;
	private double prevZ;
	private int entityID;
	private java.util.UUID UUID;
	private MobType type;
	private float yaw;
	private float pitch;
	private float headYaw;
	private double velX;
	private double velY;
	private double velZ;
	
	public SPSEntity() {}
	
	public SPSEntity(int entityID, double x, double y, double z) {
		this.entityID = entityID;
		this.x = x;
		this.y = y;
		this.z = z;
		//ConsoleIO.println("SPSEntity::constructor -> entity " + entityID + " created at <" + x + "," + z + ">");
	}
	
	public SPSEntity(int entityID, java.util.UUID uuid, MobType mobType, double x, double y, double z, float yaw, float pitch, float headYaw, double velX, double velY, double velZ) {
		this(entityID, x, y, z);
		this.UUID = uuid;
		this.type = mobType;
		this.yaw = yaw;
		this.pitch = pitch;
		this.headYaw = headYaw;
		this.setVelocity(velX, velY, velZ);
	}

	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}
	
	public void setZ(double z) {
		this.z = z;
	}

	public double getX() {
		return this.x;
	}

	public double getY() {
		return this.y;
	}
	
	public double getZ() {
		return this.z;
	}

	public int getEntityID() {
		return entityID;
	}

	public void setEntityID(int entityID) {
		this.entityID = entityID;
	}

	public java.util.UUID getUUID() {
		return UUID;
	}

	public void setUUID(java.util.UUID uuid) {
		this.UUID = uuid;
	}

	public MobType getType() {
		return type;
	}

	public void setType(MobType type) {
		this.type = type;
	}

	public float getYaw() {
		return yaw;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	public float getPitch() {
		return pitch;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public float getHeadYaw() {
		return headYaw;
	}

	public void setHeadYaw(float headYaw) {
		this.headYaw = headYaw;
	}

	public double getVelX() {
		return velX;
	}

	public void setVelocity(double velX, double velY, double velZ) {
		this.velX = velX;
		this.velY = velY;
		this.velZ = velZ;
	}

	public double getVelY() {
		return velY;
	}

	public double getVelZ() {
		return velZ;
	}
	
	public void move(double x, double y, double z) {
		this.setPrevX(this.x);
		this.setPrevY(this.y);
		this.setPrevZ(this.z);
		this.x += x;
		this.y += y;
		this.z += z;
	}

	public double getPrevX() {
		return prevX;
	}

	public void setPrevX(double prevX) {
		this.prevX = prevX;
	}

	public double getPrevY() {
		return prevY;
	}

	public void setPrevY(double prevY) {
		this.prevY = prevY;
	}

	public double getPrevZ() {
		return prevZ;
	}

	public void setPrevZ(double prevZ) {
		this.prevZ = prevZ;
	}
}
