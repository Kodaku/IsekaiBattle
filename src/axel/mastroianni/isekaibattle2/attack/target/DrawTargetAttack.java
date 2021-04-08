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

public class DrawTargetAttack extends DrawAttack {
	/**
	 * 3
	 */
	
	private BufferedImage attackImage;
	
	private ArrayList<BufferedImage[]> targetSequence;
	
	private BufferedImage[] targetImages;
	
	private BufferedImage targetImage;
	
	private int targetX; //this is the enemy x
	private int targetY;
	
	private int targetIndex = 0;
	private int targetArrayIndex = 0;
	private int targetLength;
	
	private boolean moveTarget = false;
	private boolean last = false;
	private boolean hitted = false;

	public DrawTargetAttack(ArrayList<BufferedImage[]> attackSequence,
			ArrayList<BufferedImage[]> targetSequence, DrawPlayerOld player) {
		super(attackSequence, player);
		this.targetSequence = targetSequence;
		id = 3;
	}
	
	public void draw(Graphics g) {
		int x = 0;
		int y = 0;
		attackImage = images[imageIndex];
		x = player.getXRef() - attackImage.getWidth() / 2;
		y = player.getYRef() - attackImage.getHeight();
		g.drawImage(attackImage, x, y, null);
		if(moveTarget) {
			drawTarget(g);
		}
	}
	
	private void drawTarget(Graphics g) {
		targetImage = targetImages[targetIndex];
		int x = targetX;
		int y = targetY - targetImage.getHeight();
		g.drawImage(targetImages[targetIndex], x, y, null);
	}
	
	public void move() {
		imageIndex++;
		if(arrayAttackIndex == attackSequence.size() - 1) {
			last = true;
		}
		if(imageIndex >= imagesLength / 2 && imageIndex != imagesLength && last) {
			moveTarget = true;
			moveTarget();
		}
		if(imageIndex == imagesLength) {
			arrayAttackIndex++;
			if(player.getState() == Initialization.STATE_ATTACK) {
				imageIndex = 0;
				hitted = false;
				player.setState(Initialization.STATE_STAND);
				if(enemy.getState() == Initialization.STATE_HIT) {
					enemy.setState(Initialization.STATE_STAND);
				}
			}
			else {
				if(arrayAttackIndex < attackSequence.size()) {
					imageIndex = 0;
					setImages();
				}
				else {
					imageIndex--;
					moveTarget = true;
					moveTarget();
				}
			}
		}
	}
	
	private void moveTarget() {
		targetImage = targetImages[targetIndex];
		targetIndex++;
		if(getBounds().intersects(enemy.getBounds()) && !hitted) {
			hitted = true;
			enemy.setState(Initialization.STATE_HIT);
			enemy.reduceLife();
		}
		if(targetIndex == targetLength) {
			targetArrayIndex++;
			if(targetArrayIndex < targetSequence.size()) {
				targetIndex = 0;
				setTargetImages();
			}
			else {
				moveTarget = false;
				last = false;
				hitted = false;
				player.setState(Initialization.STATE_STAND);
				if(enemy.getState() == Initialization.STATE_HIT) {
					enemy.setState(Initialization.STATE_STAND);
				}
				targetArrayIndex = 0;
				imageIndex = 0;
				arrayAttackIndex = 0;
				targetIndex = 0;
			}
		}
	}
	
	@Override
	public void setImages() {
		images = attackSequence.get(arrayAttackIndex);
		enemy = player.getEnemy();
		imagesLength = images.length;
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
		arrayAttackIndex = Integer.parseInt(parameters.get(3));
		images = attackSequence.get(arrayAttackIndex);
		imagesLength = images.length;
		setTargetImages(parameters);
	}
	
	private void setTargetImages(ArrayList<String> parameters) {
		targetArrayIndex = Integer.parseInt(parameters.get(5));
		targetIndex = Integer.parseInt(parameters.get(6));
//		enemy = player.getEnemy();
		targetImages = targetSequence.get(targetArrayIndex);
		targetImage = targetImages[targetIndex];
		targetLength = targetImages.length;
//		targetX = enemy.getXRef();
//		targetY = enemy.getYRef();
	}
	
	public Rectangle getBounds() {
		if(targetImage != null) {
			int width = targetImage.getWidth();
			int height = targetImage.getHeight();
			Rectangle bounds = new Rectangle(targetX, targetY,width, height);
			return bounds;
		}
		return new Rectangle();
//		packet = new Packet(ActionCode.NO_ATTACK_BOUNDS);
	}

	@Override
	public void preparePacketForImageLength() {
		packet = new Packet(ActionCode.ATTACK_IMAGES_LENGTH);
		packet.add(imagesLength);//1
		packet.add(targetLength);//2
	}

	@Override
	public void setAttackVariables(ArrayList<String> parameters) {
		imageIndex = Integer.parseInt(parameters.get(3));
		moveTarget = Boolean.parseBoolean(parameters.get(4));
		targetX = Integer.parseInt(parameters.get(5));
		targetY = Integer.parseInt(parameters.get(6));
		targetIndex = Integer.parseInt(parameters.get(7));
	}

}
