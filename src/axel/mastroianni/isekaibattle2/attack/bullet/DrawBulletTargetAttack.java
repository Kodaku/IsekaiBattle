package axel.mastroianni.isekaibattle2.attack.bullet;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import axel.mastroianni.isekaibattle2.player.DrawPlayerOld;

public class DrawBulletTargetAttack extends DrawBulletAttack {
	
	private ArrayList<BufferedImage[]> targetSequence;
	
	private BufferedImage[] targetImages;
	private BufferedImage targetImage;
	
	private int targetArrayIndex = 0;
	protected int targetIndex = 0;
	protected int targetLength = 0;
	
	private int targetX = 0;
	private int targetY = 0;
	
	protected boolean drawTarget = false;

	public DrawBulletTargetAttack(ArrayList<BufferedImage[]> attackSequence, ArrayList<BufferedImage[]> bulletSequence,
			ArrayList<BufferedImage[]> targetSequence, DrawPlayerOld player) {
		super(attackSequence, bulletSequence, player);
		this.targetSequence = targetSequence;
		id = 4;
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
		targetArrayIndex = Integer.parseInt(parameters.get(6));
		targetIndex = Integer.parseInt(parameters.get(7));
		targetImages = targetSequence.get(targetArrayIndex);
		targetImage = targetImages[targetIndex];
		targetLength = targetImages.length;
		targetX = enemy.getXRef();
		targetY = enemy.getYRef();
	}
	
	@Override
	public void draw(Graphics g) {
		super.draw(g);
		if(drawTarget) {
			drawTarget(g);
		}
	}
	
	@Override
	public void changeMoving() {
		drawTarget = true;
		moveBullet = false;
		moveTarget();
	}
	
	private void drawTarget(Graphics g) {
		targetImage = targetImages[targetIndex];
		int x = targetX - targetImage.getWidth() / 2;
		int y = targetY - targetImage.getHeight();
		g.drawImage(targetImage, x, y, null);
	}
	
	@Override
	public void preparePacketForImageLength() {
		super.preparePacketForImageLength();
		packet.add(targetLength);//4
	}
	
	@Override
	public void setAttackVariables(ArrayList<String> parameters) {
		super.setAttackVariables(parameters);
		drawTarget = Boolean.parseBoolean(parameters.get(7));
		targetIndex = Integer.parseInt(parameters.get(8));
		targetX = Integer.parseInt(parameters.get(9));
		targetY = Integer.parseInt(parameters.get(10));
	}
	
	public void moveTarget() {
		basicMove();
		if(targetIndex == targetLength) {
			reset();
		}
	}
	
	public void basicMove() {
		targetImage = targetImages[targetIndex];
		targetIndex++;
	}
	
	@Override
	public void reset() {
		super.reset();
		drawTarget = false;
		targetIndex = 0;
		targetLength = 0;
		targetArrayIndex = 0;
		
	}

}
