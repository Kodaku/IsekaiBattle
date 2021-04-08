package axel.mastroianni.isekaibattle2.draw;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import axel.mastroianni.isekaibattle2.attack.DrawAttack;
import axel.mastroianni.isekaibattle2.clientserver.Packet;
import axel.mastroianni.isekaibattle2.clientserver.Player;
import axel.mastroianni.isekaibattle2.constantsclass.ActionCode;
import axel.mastroianni.isekaibattle2.menu.GamePanel;
import axel.mastroianni.isekaibattle2.util.SuperDatabase;

public class DrawPlayer {
	
	private static final int MAX_LIFE = 200;
	private static final int MAX_MANA = 200;
	
	private ArrayList<BufferedImage[]> actualNImages = new ArrayList<>();
	private ArrayList<BufferedImage[]> actualMImages = new ArrayList<>();
	
	private BufferedImage[] images;
	
	private BufferedImage playerImage;
	private BufferedImage background;
	
	private DrawAttack actualNormalAttackNormal;
	private DrawAttack actualNormalAttackMirrored;
	private DrawAttack actualSpecialAttackNormal;
	private DrawAttack actualSpecialAttackMirrored;
	private DrawAttack actualFinalAttackNormal;
	private DrawAttack actualFinalAttackMirrored;
	
	private int paintX;
	private int paintY;
	private int imageIndex;
	private int state;
	private int index;
	private int imagesLength;
	
	private int life = MAX_LIFE;
	private int mana = MAX_MANA;
	
	private int transformationIndex = 0;
	private int playerID;
	
	private double paintJumpY;
	
	private boolean isMirrored;
	
	private String name;
	
	private GamePanel gamePanel;
	
	private Packet packet;
	
	public DrawPlayer(int playerID, BufferedImage background, GamePanel gamePanel) {
		this.playerID = playerID;
		this.background = background;
		this.gamePanel = gamePanel;
	}
	
	public void setDataStructures() {
		SuperDatabase.setClientDataStructures(transformationIndex, playerID);
		actualNImages = SuperDatabase.getNImages(playerID);
		actualMImages = SuperDatabase.getMImages(playerID);
		actualNormalAttackNormal = SuperDatabase.getNormalAttackNormal(playerID);
		actualNormalAttackMirrored = SuperDatabase.getNormalAttackMirrored(playerID);
		actualSpecialAttackNormal = SuperDatabase.getSpecialAttackNormal(playerID);
		actualSpecialAttackMirrored = SuperDatabase.getSpecialAttackMirrored(playerID);
		actualFinalAttackNormal = SuperDatabase.getFinalAttackNormal(playerID);
		actualFinalAttackMirrored = SuperDatabase.getFinalAttackMirrored(playerID);
		name = SuperDatabase.getName(playerID);
	}
	
	public void setAttacksPlayer() {
		actualNormalAttackNormal.setPlayer(this);
		actualNormalAttackMirrored.setPlayer(this);
		actualSpecialAttackNormal.setPlayer(this);
		actualSpecialAttackMirrored.setPlayer(this);
		actualFinalAttackNormal.setPlayer(this);
		actualFinalAttackMirrored.setPlayer(this);
	}
	
	public void draw(Graphics g) {
		switch(state) {
		case Player.STATE_DEAD:{
			drawGeneral(g);
			break;
		}
		case Player.STATE_FINAL_ATTACK:{
			if(isMirrored) {
				actualFinalAttackMirrored.draw(g);
			}
			else {
				actualFinalAttackNormal.draw(g);
			}
			break;
		}
		case Player.STATE_HIT:{
			drawGeneral(g);
			break;
		}
		case Player.STATE_INTRO:{
			drawIntro(g);
			break;
		}
		case Player.STATE_JUMP:{
			drawJump(g);
			break;
		}
		case Player.STATE_ATTACK:{
			if(isMirrored) {
				actualNormalAttackMirrored.draw(g);
			}
			else {
				actualNormalAttackNormal.draw(g);
			}
			break;
		}
		case Player.STATE_RUN:{
			drawGeneral(g);
			break;
		}
		case Player.STATE_SPECIAL_ATTACK:{
			if(isMirrored) {
				actualSpecialAttackMirrored.draw(g);
			}
			else {
				actualSpecialAttackNormal.draw(g);
			}
			break;
		}
		case Player.STATE_STAND:{
			drawGeneral(g);
			break;
		}
		}
	}
	
	public void drawIntro(Graphics g) {
		
	}
	
	public void drawGeneral(Graphics g) {
		int x = 0;
		int y = 0;
		playerImage = images[imageIndex];
		x = paintX - playerImage.getWidth() / 2;
		y = paintY - playerImage.getHeight();
		g.drawImage(playerImage, x, (int)y, null);
	}
	
	public void drawJump(Graphics g) {
		int x = 0;
		double y = 0;
		playerImage = images[imageIndex];
		x = paintX - playerImage.getWidth() / 2;
		y = paintJumpY - playerImage.getHeight();
		g.drawImage(playerImage, x, (int)y, null);
	}
	
	public void getBounds() {
		if(playerImage != null) {
			int width = playerImage.getWidth();
			int height = playerImage.getHeight();
			packet = new Packet(ActionCode.PLAYER_BOUNDS);
			packet.add(paintX);//1
			packet.add(paintY);//2
			packet.add(width);//3
			packet.add(height);//4
			send(packet);
		}
	}
	
	public void getAttackBounds(int state) {
		if(state == Player.STATE_ATTACK) {
			if(!isMirrored) {
				actualNormalAttackNormal.getBounds();
				packet = actualNormalAttackNormal.getPacket();
			}
			else {
				actualNormalAttackMirrored.getBounds();
				packet = actualNormalAttackMirrored.getPacket();
			}
		}
		else if(state == Player.STATE_SPECIAL_ATTACK) {
			if(!isMirrored) {
				actualSpecialAttackNormal.getBounds();
				packet = actualSpecialAttackNormal.getPacket();
			}
			else {
				actualSpecialAttackMirrored.getBounds();
				packet = actualSpecialAttackMirrored.getPacket();
			}
		}
		else if(state == Player.STATE_FINAL_ATTACK) {
			if(!isMirrored) {
				actualFinalAttackNormal.getBounds();
				packet = actualFinalAttackNormal.getPacket();
			}
			else {
				actualFinalAttackMirrored.getBounds();
				packet = actualFinalAttackMirrored.getPacket();
			}
		}
	}
	
	public void setImages() {
		imageIndex = 0;
		if(index == -1) {
			return;
		}
		if(!isMirrored) {
			images = actualNImages.get(index);
		}
		else {
			images = actualMImages.get(index);
		}
		imagesLength = images.length;
		playerImage = images[0];
		
		getBounds();
		
		Packet packet = new Packet(ActionCode.IMAGES_LENGTH);
		packet.add(imagesLength);//1
		packet.add(background.getWidth());//2
		packet.add(background.getHeight());//3
		send(packet);
	}
	
	public void setAttackImages(ArrayList<String> parameters) {
		if(state == Player.STATE_ATTACK) {
			if(!isMirrored) {
				actualNormalAttackNormal.setImages(parameters);
				packet = actualNormalAttackNormal.getPacket();
			}
			else {
				actualNormalAttackMirrored.setImages(parameters);
				packet = actualNormalAttackMirrored.getPacket();
			}
		}
		else if(state == Player.STATE_SPECIAL_ATTACK) {
			if(!isMirrored) {
				actualSpecialAttackNormal.setImages(parameters);
				packet = actualSpecialAttackNormal.getPacket();
			}
			else {
				actualSpecialAttackMirrored.setImages(parameters);
				packet = actualSpecialAttackMirrored.getPacket();
			}
		}
		else if(state == Player.STATE_FINAL_ATTACK) {
			if(!isMirrored) {
				actualFinalAttackNormal.setImages(parameters);
				packet = actualFinalAttackNormal.getPacket();
			}
			else {
				actualFinalAttackMirrored.setImages(parameters);
				packet = actualFinalAttackMirrored.getPacket();
			}
		}
		
		gamePanel.send(packet);
	}
	
	public void setAttackVariables(ArrayList<String> parameters) {
		if(state == Player.STATE_ATTACK) {
			if(!isMirrored) {
				actualNormalAttackNormal.setAttackVariables(parameters);
			}
			else {
				actualNormalAttackMirrored.setAttackVariables(parameters);
			}
		}
		else if(state == Player.STATE_SPECIAL_ATTACK) {
			if(!isMirrored) {
				actualSpecialAttackNormal.setAttackVariables(parameters);
			}
			else {
				actualSpecialAttackMirrored.setAttackVariables(parameters);
			}
		}
		else if(state == Player.STATE_FINAL_ATTACK) {
			if(!isMirrored) {
				actualFinalAttackNormal.setAttackVariables(parameters);
			}
			else {
				actualFinalAttackMirrored.setAttackVariables(parameters);
			}
		}
	}

	public void setPaintX(int paintX) {
		this.paintX = paintX;
	}
	
	public int getPaintX() {
		return paintX;
	}

	public void setPaintY(int paintY) {
		this.paintY = paintY;
	}
	
	public int getPaintY() {
		return paintY;
	}

	public void setImageIndex(int imageIndex) {
		this.imageIndex = imageIndex;
	}

	public void setState(int state) {
		this.state = state;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public void setTransformationIndex(int transformationIndex) {
		this.transformationIndex = transformationIndex;
	}

	public void setPaintJumpY(double paintJumpY) {
		this.paintJumpY = paintJumpY;
	}

	public void setMirrored(boolean isMirrored) {
		this.isMirrored = isMirrored;
	}
	
	public boolean getIsMirrored() {
		return isMirrored;
	}
	
	public String getName() {
		return name;
	}
	
	public int getLife() {
		return life;
	}

	public void setLife(int life) {
		this.life = life;
	}

	public int getMana() {
		return mana;
	}

	public void setMana(int mana) {
		this.mana = mana;
	}

	private void send(Packet packet) {
		gamePanel.send(packet);
	}

}
