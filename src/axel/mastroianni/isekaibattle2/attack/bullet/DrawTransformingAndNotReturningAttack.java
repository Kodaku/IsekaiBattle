package axel.mastroianni.isekaibattle2.attack.bullet;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import axel.mastroianni.isekaibattle2.player.DrawPlayerOld;

//import axel.mastroianni.isekaibattle2.player.DrawPlayerOld;

public class DrawTransformingAndNotReturningAttack extends DrawBulletAttack {
	
	private ArrayList<BufferedImage[]> secondSequence;
	private ArrayList<BufferedImage[]> targetSequence;
	private ArrayList<BufferedImage[]> returningSequence;
	
	private BufferedImage[] secondImages;
	private BufferedImage[] targetImages;
	private BufferedImage[] returningImages;
	
	private BufferedImage secondImage;
	private BufferedImage targetImage;
	private BufferedImage returningImage;
	
	private int secondArrayIndex = 0;
	private int secondIndex = 0;
	private int secondLength = 0;
	
	private int targetArrayIndex = 0;
	private int targetIndex = 0;
	private int targetLength = 0;
	
	private int returningArrayIndex = 0;
	private int returningIndex = 0;
	private int returningLength = 0;
	
	private int targetX = 0;
	private int targetY = 0;
	
	private boolean drawSecond = false;
	private boolean drawTarget = false;
	private boolean drawReturning = false;

	public DrawTransformingAndNotReturningAttack(ArrayList<BufferedImage[]> attackSequence,
			ArrayList<BufferedImage[]> bulletSequence,ArrayList<BufferedImage[]> secondSequence,
			ArrayList<BufferedImage[]> targetSequence,
			ArrayList<BufferedImage[]> returningSequence, DrawPlayerOld player) {
		super(attackSequence, bulletSequence, player);
		
		this.secondSequence = secondSequence;
		this.targetSequence = targetSequence;
		this.returningSequence = returningSequence;
		id = 9;
	}
	
	@Override
	public void setImages() {
		super.setImages();
		setSecondImages();
		setTargetImages();
		setReturningImages();
	}
	
	private void setSecondImages() {
		secondImages = secondSequence.get(secondArrayIndex);
		secondLength = secondImages.length;
	}
	
	private void setTargetImages() {
		targetImages = targetSequence.get(targetArrayIndex);
		targetLength = targetImages.length;
	}
	
	private void setReturningImages() {
		returningImages = returningSequence.get(returningArrayIndex);
		returningLength = returningImages.length;
	}
	
//	@Override
//	public void setImages(ArrayList<String> parameters) {
//		super.setImages(parameters);
//		setSecondImages(parameters);
//		setTargetImages(parameters);
//		setReturningImages(parameters);
//	}
//	
//	private void setSecondImages(ArrayList<String> parameters) {
//		secondArrayIndex = Integer.parseInt(parameters.get(6));
//		secondIndex = Integer.parseInt(parameters.get(7));
//		secondImages = secondSequence.get(secondArrayIndex);
//		secondLength = secondImages.length;
//		secondImage = secondImages[secondIndex];
//	}
//	
//	private void setTargetImages(ArrayList<String> parameters) {
//		targetArrayIndex = Integer.parseInt(parameters.get(8));
//		targetIndex = Integer.parseInt(parameters.get(9));
//		targetImages = targetSequence.get(targetArrayIndex);
//		targetLength = targetImages.length;
//		targetImage = targetImages[targetIndex];
//	}
//	
//	private void setReturningImages(ArrayList<String> parameters) {
//		returningArrayIndex = Integer.parseInt(parameters.get(10));
//		returningIndex = Integer.parseInt(parameters.get(11));
//		returningImages = returningSequence.get(returningArrayIndex);
//		returningLength = returningImages.length;
//		returningImage = returningImages[returningIndex];
//	}
	
	@Override
	public void draw(Graphics g) {
		int x = 0;
		int y = 0;
		if(!drawSecond && !drawReturning) {
			super.draw(g);
		}
		else if(drawSecond) {
			secondImage = secondImages[secondIndex];
			x = player.getXRef() - secondImage.getWidth() / 2;
			y = player.getYRef() - secondImage.getHeight();
			g.drawImage(secondImage, x, y, null);
			if(drawTarget) {
				targetImage = targetImages[targetIndex];
				int enemyX = targetX - targetImage.getWidth() / 2;
				int enemyY = targetY - targetImage.getHeight();
				g.drawImage(targetImage, enemyX, enemyY, null);
			}
		}
		else {
			returningImage = returningImages[returningIndex];
			x = player.getXRef() - returningImage.getWidth() / 2;
			y = player.getYRef() - returningImage.getHeight();
			g.drawImage(returningImage, x, y, null);
		}
	}
	
	@Override
	public void preparePacketForImageLength() {
		super.preparePacketForImageLength();
		packet.add(secondLength);//4
		packet.add(targetLength);//5
		packet.add(returningLength);//6
	}
	
//	@Override
//	public void setAttackVariables(ArrayList<String> parameters) {
//		super.setAttackVariables(parameters);
//		drawSecond = Boolean.parseBoolean(parameters.get(7));
//		drawReturning = Boolean.parseBoolean(parameters.get(8));
//		drawTarget = Boolean.parseBoolean(parameters.get(9));
//		secondIndex = Integer.parseInt(parameters.get(10));
//		targetIndex = Integer.parseInt(parameters.get(11));
//		returningIndex = Integer.parseInt(parameters.get(12));
//		targetX = Integer.parseInt(parameters.get(13));
//		targetY = Integer.parseInt(parameters.get(14));
//	}
//	
	@Override
	public void changeMoving() {
		moveBullet = false;
		drawSecond = true;
		moveSecond();
	}
	
	private void moveSecond() {
		secondImage = secondImages[secondIndex];
		secondIndex++;
		if(secondIndex == secondLength) {
			secondIndex--;
			drawTarget = true;
			moveTarget();
		}
	}
	
	private void moveTarget() {
		targetImage = targetImages[targetIndex];
		targetIndex++;
		if(targetIndex == targetLength) {
			drawSecond = false;
			drawTarget = false;
			drawReturning = true;
			targetIndex--;
			moveReturning();
		}
	}
	
	private void moveReturning() {
		returningImage = returningImages[returningIndex];
		returningIndex++;
		if(returningIndex == returningLength) {
			reset();
		}
	}
	
	@Override
	public void reset() {
		super.reset();
		
		secondArrayIndex = 0;
		secondIndex = 0;
		secondLength = 0;
		targetArrayIndex = 0;
		targetIndex = 0;
		targetLength = 0;
		returningArrayIndex = 0;
		returningIndex = 0;
		returningLength = 0;
		drawSecond = false;
		drawTarget = false;
		drawReturning = false;
	}

}
