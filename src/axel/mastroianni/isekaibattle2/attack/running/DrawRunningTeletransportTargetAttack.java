package axel.mastroianni.isekaibattle2.attack.running;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

//import axel.mastroianni.isekaibattle2.clientserver.Packet;
//import axel.mastroianni.isekaibattle2.constantsclass.ActionCode;

import axel.mastroianni.isekaibattle2.player.DrawPlayerOld;

public class DrawRunningTeletransportTargetAttack extends DrawRunningAttack {
	
	private ArrayList<BufferedImage[]> teletransportSequence;
	private ArrayList<BufferedImage[]> targetSequence;
	
	private BufferedImage[] teletransportImages;
	private BufferedImage[] targetImages;
	
	private BufferedImage teletransportImage;
	private BufferedImage targetImage;
	
	private int teletransportIndex = 0;
	private int teletransportArrayIndex = 0;
	private int teletransportLength = 0;
	
	private int targetIndex = 0;
	private int targetArrayIndex = 0;
	private int targetLength = 0;
	
	private int targetX;
	private int targetY;
	
	private boolean drawTeletransport = false;
	private boolean drawTarget = false;

	public DrawRunningTeletransportTargetAttack(ArrayList<BufferedImage[]> attackSequence,
			ArrayList<BufferedImage[]> runningSequence, ArrayList<BufferedImage[]> 
			teletransportSequence, ArrayList<BufferedImage[]> targetSequence, 
			DrawPlayerOld player) {
		super(attackSequence, runningSequence, player);
		this.teletransportSequence = teletransportSequence;
		this.targetSequence = targetSequence;
		id = 6;
	}
	
	@Override
	public void setImages() {
		super.setImages();
		setTeletransportImages();
		setTargetImages();
	}
	
	private void setTeletransportImages() {
		teletransportImages = teletransportSequence.get(teletransportArrayIndex);
		teletransportLength = teletransportImages.length;
	}
	
	private void setTargetImages() {
		targetImages = targetSequence.get(targetArrayIndex);
		targetLength = targetImages.length;
	}
	
	@Override
	public void setImages(ArrayList<String> parameters) {
		super.setImages(parameters);
		chargeTeletransportImages(parameters);
		chargeTargetImages(parameters);
	}
	
	private void chargeTeletransportImages(ArrayList<String> parameters) {
		teletransportArrayIndex = Integer.parseInt(parameters.get(7));
		teletransportIndex = Integer.parseInt(parameters.get(8));
		teletransportImages = teletransportSequence.get(teletransportArrayIndex);
		teletransportImage = teletransportImages[teletransportIndex];
		teletransportLength = teletransportImages.length;
	}
	
	private void chargeTargetImages(ArrayList<String> parameters) {
		targetArrayIndex = Integer.parseInt(parameters.get(9));
		targetIndex = Integer.parseInt(parameters.get(10));
		targetImages = targetSequence.get(targetArrayIndex);
		targetImage = targetImages[targetIndex];
		targetLength = targetImages.length;
	}
	
	public void preparePacketForImageLength() {
		super.preparePacketForImageLength();
		packet.add(teletransportLength);//3
		packet.add(targetLength);//4
	}
	
	@Override
	public void moveRunning() {
		runningImage = runningImages[runningIndex];
		runningIndex++;
		if(!player.isMirrored()) {
			runX += vx;
		}
		else {
			runX -= vx;
		}
		if(runningIndex == runningLength) {
			drawTeletransport = true;
			runningIndex--;
			moveTeletransport();
		}
	}
	
	private void moveTeletransport() {
		teletransportImage = teletransportImages[teletransportIndex];
		teletransportIndex++;
		if(teletransportIndex == teletransportLength) {
			drawTeletransport = false;
			drawTarget = true;
			teletransportIndex--;
			if(!hitted) {
				changeEnemyState();
			}
			changeMoving();
		}
	}
	
	@Override
	public void changeMoving() {
		moveTarget();
	}
	
	private void moveTarget() {
		targetImage = targetImages[targetIndex];
		targetIndex++;
		if(targetIndex == targetLength) {
			reset();
		}
	}
	
	@Override
	public void draw(Graphics g) {
		int x = 0;
		int y = 0; 
		if(drawTeletransport) {
			teletransportImage = teletransportImages[teletransportIndex];
			x = player.getXRef() - teletransportImage.getWidth()/2;
			y = player.getYRef() - teletransportImage.getHeight();
			g.drawImage(teletransportImage, x, y, null);
		}
		else if(drawTarget) {
			targetImage = targetImages[targetIndex];
			x = targetX - targetImage.getWidth()/2;
			y = targetY - targetImage.getHeight();
			g.drawImage(targetImage, x, y, null);
		}
		else {
			super.draw(g);
		}
	}
	
	@Override
	public void setAttackVariables(ArrayList<String> parameters) {
		super.setAttackVariables(parameters);
		drawTeletransport = Boolean.parseBoolean(parameters.get(6));
		teletransportIndex = Integer.parseInt(parameters.get(7));
		targetX = Integer.parseInt(parameters.get(8));
		targetY = Integer.parseInt(parameters.get(9));
		drawTarget = Boolean.parseBoolean(parameters.get(10));
		targetIndex = Integer.parseInt(parameters.get(11));
	}
	
	@Override
	public void reset() {
		super.reset();
		teletransportArrayIndex = 0;
		teletransportIndex = 0;
		teletransportLength = 0;
		targetArrayIndex = 0;
		targetIndex = 0;
		targetLength = 0;
		drawTeletransport = false;
		drawTarget = false;
	}
	
	@Override
	public Rectangle getBounds() {
//		packet = new Packet(ActionCode.NO_ATTACK_BOUNDS);
		return null;
	}

}
