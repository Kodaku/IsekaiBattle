package axel.mastroianni.isekaibattle2.attack;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import axel.mastroianni.isekaibattle2.clientserver.Packet;
import axel.mastroianni.isekaibattle2.draw.DrawPlayer;
import axel.mastroianni.isekaibattle2.player.DrawPlayerOld;

public abstract class DrawAttack {
	
	protected int imageIndex = 0;
	protected int imagesLength;
	protected int arrayAttackIndex = 0;
	
	protected int x_ref;
	protected int y_ref;
	
	protected int id;
	
	protected BufferedImage[] images;
	
	protected ArrayList<BufferedImage[]> attackSequence;
	
	protected DrawPlayer playerMulti;
	
	protected DrawPlayerOld player = null;
	protected DrawPlayerOld enemy;
	
	protected Packet packet;
	
	public DrawAttack(ArrayList<BufferedImage[]> attackSequence, DrawPlayerOld player) {
		this.attackSequence = attackSequence;
		this.player = player;
	}
	
	public void setPlayerCoordinates() {
		x_ref = player.getXRef();
		y_ref = player.getYRef();
	}
	
	public void setEnemy(DrawPlayerOld enemy) {
		this.enemy = enemy;
	}
	
	public abstract void draw(Graphics g);
	
	public abstract void move();
	
	public abstract void setImages(ArrayList<String> parameters);
	
	public abstract void setImages();
	
	public abstract void preparePacketForImageLength();
	
	public abstract void setAttackVariables(ArrayList<String> parameters);
	
//	public abstract void getBoundsMulti();
	
	public abstract Rectangle getBounds();
	
	public int getId() {
		return id;
	}
	
	public Packet getPacket() {
		return packet;
	}
	
	public void setPlayer(DrawPlayer playerMulti) {
		this.playerMulti = playerMulti;
		x_ref = playerMulti.getPaintX();
		y_ref = playerMulti.getPaintY();
	}

}
