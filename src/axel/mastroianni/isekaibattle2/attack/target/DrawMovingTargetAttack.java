package axel.mastroianni.isekaibattle2.attack.target;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import axel.mastroianni.isekaibattle2.attack.DrawAttack;
import axel.mastroianni.isekaibattle2.clientserver.Packet;
import axel.mastroianni.isekaibattle2.constantsclass.ActionCode;
import axel.mastroianni.isekaibattle2.constantsclass.Initialization;
import axel.mastroianni.isekaibattle2.player.DrawPlayerOld;

public class DrawMovingTargetAttack extends DrawAttack {
	
	private BufferedImage attackImage;
	
	private ArrayList<BufferedImage[]> movingSequence;
	private ArrayList<BufferedImage[]> targetSequence;
	
	private BufferedImage[] movingImages;
	private BufferedImage[] targetImages;
	
	private BufferedImage movingImage;
	private BufferedImage targetImage;
	
	private int arrayMovingIndex = 0;
	private int movingIndex = 0;
	private int movingLength = 0;
	
	private int arrayTargetIndex = 0;
	private int targetIndex = 0;
	private int targetLength = 0;
	
	private int targetX;
	private int targetY;
	
	private boolean drawMoving = false;
	private boolean drawTarget = false;
	private boolean hitted = false;

	public DrawMovingTargetAttack(ArrayList<BufferedImage[]> attackSequence,
			ArrayList<BufferedImage[]> movingSequence, ArrayList<BufferedImage[]> targetSequence,
			DrawPlayerOld player) {
		super(attackSequence, player);
		this.movingSequence = movingSequence;
		this.targetSequence = targetSequence;
		id = 7;
	}

	@Override
	public void draw(Graphics g) {
		int x = 0;
		int y = 0;
		attackImage = images[imageIndex];
		x = player.getXRef() - attackImage.getWidth() / 2;
		y = player.getYRef() - attackImage.getHeight();
		g.drawImage(attackImage, x, y, null);
		if(drawMoving && !drawTarget) {
			movingImage = movingImages[movingIndex];
			x = targetX - movingImage.getWidth() / 2 - 30;
			y = targetY - movingImage.getHeight();
			g.drawImage(movingImage, x, y, null);
		}
		else if(drawTarget && !drawMoving){
			targetImage = targetImages[targetIndex];
			x = targetX - targetImage.getWidth() / 2;
			y = targetY - targetImage.getHeight();
			g.drawImage(targetImage, targetX, targetY, null);
		}
	}

	@Override
	public void move() {
		imageIndex++;
		if(imageIndex == imagesLength) {
			imageIndex--;
			drawMoving = true;
			moveMoving();
		}
	}
	
	private void moveMoving() {
		movingImage = movingImages[movingIndex];
		movingIndex++;
		if(movingIndex == movingLength) {
			drawMoving = false;
			drawTarget = true;
			movingIndex--;
			if(!hitted) {
				changeEnemyState();
			}
			changeMoving();
		}
	}
	
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
	
	private void changeEnemyState() {
		hitted = true;
		enemy.setState(Initialization.STATE_HIT);
		enemy.reduceLife();
	}
	
	private void reset() {
		drawMoving = false;
		drawTarget = false;
		hitted = false;
		player.setState(Initialization.STATE_STAND);
		if(enemy.getState() == Initialization.STATE_HIT) {
			enemy.setState(Initialization.STATE_STAND);
		}
		arrayMovingIndex = 0;
		arrayTargetIndex = 0;
		imageIndex = 0;
		movingIndex = 0;
		targetIndex = 0;
		arrayAttackIndex = 0;
	}
	
	@Override
	public void setImages() {
		enemy = player.getEnemy();
		images = attackSequence.get(arrayAttackIndex);
		imagesLength = images.length;
		setMovingImages();
		setTargetImages();
	}
	
	private void setMovingImages() {
		movingImages = movingSequence.get(arrayMovingIndex);
		movingLength = movingImages.length;
	}
	
	private void setTargetImages() {
		targetImages = targetSequence.get(arrayTargetIndex);
		targetLength = targetImages.length;
	}

	@Override
	public void setImages(ArrayList<String> parameters) {
		arrayAttackIndex = Integer.parseInt(parameters.get(3));
		enemy = player.getEnemy();
		images = attackSequence.get(arrayAttackIndex);
		imagesLength = images.length;
		setMovingImages(parameters);
		setTargetImages(parameters);
	}
	
	private void setMovingImages(ArrayList<String> parameters) {
		arrayMovingIndex = Integer.parseInt(parameters.get(5));
		movingIndex = Integer.parseInt(parameters.get(6));
		movingImages = movingSequence.get(arrayMovingIndex);
		movingImage = movingImages[movingIndex];
		movingLength = movingImages.length;
	}
	
	private void setTargetImages(ArrayList<String> parameters) {
		arrayTargetIndex = Integer.parseInt(parameters.get(7));
		targetIndex = Integer.parseInt(parameters.get(8));
		targetImages = targetSequence.get(arrayTargetIndex);
		targetImage = targetImages[targetIndex];
		targetLength = targetImages.length;
	}

	@Override
	public Rectangle getBounds() {
//		packet = new Packet(ActionCode.NO_ATTACK_BOUNDS);
		return null;
	}

	@Override
	public void preparePacketForImageLength() {
		packet = new Packet(ActionCode.ATTACK_IMAGES_LENGTH);
		packet.add(imagesLength);//1
		packet.add(movingLength);//2
		packet.add(targetLength);//3
	}

	@Override
	public void setAttackVariables(ArrayList<String> parameters) {
		imageIndex = Integer.parseInt(parameters.get(3));
		drawMoving = Boolean.parseBoolean(parameters.get(4));
		movingIndex = Integer.parseInt(parameters.get(5));
		drawTarget = Boolean.parseBoolean(parameters.get(6));
		targetIndex = Integer.parseInt(parameters.get(7));
		targetX = Integer.parseInt(parameters.get(8));
		targetY = Integer.parseInt(parameters.get(9));
	}

}
