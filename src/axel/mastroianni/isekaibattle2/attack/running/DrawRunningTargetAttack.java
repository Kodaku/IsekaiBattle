package axel.mastroianni.isekaibattle2.attack.running;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import axel.mastroianni.isekaibattle2.player.DrawPlayerOld;

public class DrawRunningTargetAttack extends DrawRunningAttack {
	
	private ArrayList<BufferedImage[]> targetSequence;
	
	private BufferedImage[] targetImages;
	private BufferedImage targetImage;
	
	private int targetIndex = 0;
	private int targetLength = 0;
	private int targetArrayIndex = 0;
	
	private int targetX;
	private int targetY;
	
	private boolean drawTarget = false;

	public DrawRunningTargetAttack(ArrayList<BufferedImage[]> attackSequence, ArrayList<BufferedImage[]> runningSequence,
			ArrayList<BufferedImage[]> targetSequence, DrawPlayerOld player) {
		super(attackSequence, runningSequence, player);
		this.targetSequence = targetSequence;
		id = 5;
	}
	
	@Override
	public void draw(Graphics g) {
		int x = 0;
		int y = 0;
		if(drawTarget) {
			targetImage = targetImages[targetIndex];
			x = targetX - targetImage.getWidth() / 2;
			y = targetY;// - targetImage.getHeight() / 2;
			g.drawImage(targetImage, x, y, null);
		}
		else {
			super.draw(g);
		}
	}
	
	@Override
	public void setImages() {
		super.setImages();
		setTargetImages();
	}
	
	private void setTargetImages() {
		targetImages = targetSequence.get(targetArrayIndex);
		targetLength = targetImages.length;
		targetX = enemy.getXRef();
		targetY = enemy.getYRef();
	}
	
	@Override
	public void setImages(ArrayList<String> parameters) {
		super.setImages(parameters);
		setTargetImages(parameters);
	}
	
	private void setTargetImages(ArrayList<String> parameters) {
		targetArrayIndex = Integer.parseInt(parameters.get(7));
		targetIndex = Integer.parseInt(parameters.get(8));
		targetImages = targetSequence.get(targetArrayIndex);
		targetImage = targetImages[targetIndex];
		targetLength = targetImages.length;
	}
	
	public void preparePacketForImageLength() {
		super.preparePacketForImageLength();
		packet.add(targetLength);//3
	}
	
	@Override
	public void setAttackVariables(ArrayList<String> parameters) {
		super.setAttackVariables(parameters);
		targetX = Integer.parseInt(parameters.get(6));
		targetY = Integer.parseInt(parameters.get(7));
		drawTarget = Boolean.parseBoolean(parameters.get(8));
		targetIndex = Integer.parseInt(parameters.get(9));
	}
	
	@Override
	public void changeMoving() {
		drawTarget = true;
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
	public void reset() {
		super.reset();
		targetIndex = 0;
		targetArrayIndex = 0;
		drawTarget = false;
		targetLength = 0;
		player.setXRef(runX);
	}

}
