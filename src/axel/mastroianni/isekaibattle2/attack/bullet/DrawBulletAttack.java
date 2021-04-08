package axel.mastroianni.isekaibattle2.attack.bullet;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import axel.mastroianni.isekaibattle2.attack.DrawAttack;
import axel.mastroianni.isekaibattle2.clientserver.Packet;
//import axel.mastroianni.isekaibattle2.clientserver.Player;
import axel.mastroianni.isekaibattle2.constantsclass.ActionCode;
import axel.mastroianni.isekaibattle2.constantsclass.Initialization;
import axel.mastroianni.isekaibattle2.player.DrawPlayerOld;

public class DrawBulletAttack extends DrawAttack {
	/**
	 * 2
	 */
	
	private ArrayList<BufferedImage[]> bulletSequence;
	
	private BufferedImage[] bulletImages;
	
	private BufferedImage bulletImage;
	
	private int bulletIndex = 0;
	private int bulletArrayIndex = 0;
	private int bulletLength;
	
	protected boolean moveBullet = false;
	
	private int vx = 10;
	
	private int bulletX;
	private int bulletY;
	
	private boolean hitted = false;
	
	public DrawBulletAttack(ArrayList<BufferedImage[]> attackSequence,
			ArrayList<BufferedImage[]> bulletSequence, DrawPlayerOld player) {
		super(attackSequence, player);
		this.bulletSequence = bulletSequence;
		id = 2;
	}
	
	public void draw(Graphics g) {
		int x = 0;
		int y = 0;
		x = player.getXRef() - images[imageIndex].getWidth() / 2;
		y = player.getYRef() - images[imageIndex].getHeight();
		g.drawImage(images[imageIndex], x, (int)y, null);
		if(moveBullet) {
			drawBullet(g);
		}
	}
	
	private void drawBullet(Graphics g) {
		bulletY = player.getYRef() - bulletImage.getHeight();
		g.drawImage(bulletImage, bulletX, bulletY, null);
	}
	
	public void move() {
		imageIndex++;
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
				//processing the various movements
				if(arrayAttackIndex < attackSequence.size()) {
					imageIndex = 0;
					setImages();
				}
				else {
					imageIndex--;
					moveBullet = true;
					moveBullet();
				}
			}
		}
	}
	
	private void moveBullet() {
		bulletImage = bulletImages[bulletIndex];
		bulletIndex++;
		bulletIndex = bulletIndex % bulletLength;
		if(player.isMirrored()) {
			bulletX -= vx;
		}
		else {
			bulletX += vx;
		}
		checkCollision();
		if(hitted) {
			changeMoving();
		}
		else if(bulletX >= x_ref + 300 || bulletX <= x_ref - 300) {
			reset();
		}
	}
	
	private void checkCollision() {
		if(getBounds().intersects(enemy.getBounds()) && !hitted) {
			changeEnemyState();
		}
	}
	
	protected void changeEnemyState() {
		enemy.setState(Initialization.STATE_HIT);
		if(!hitted) {
			enemy.reduceLife();
		}
		hitted = true;
	}
	
	protected void changeMoving() {
		reset();
	}
	
	protected void reset() {
		moveBullet = false;
		hitted = false;
		player.setState(Initialization.STATE_STAND);
		if(enemy.getState() == Initialization.STATE_HIT) {
			enemy.setState(Initialization.STATE_STAND);
		}
		bulletArrayIndex = 0;
		imageIndex = 0;
		arrayAttackIndex = 0;
		bulletIndex = 0;
	}
//	
	public void setImages(ArrayList<String> parameters) {
		arrayAttackIndex = Integer.parseInt(parameters.get(3));
		images = attackSequence.get(arrayAttackIndex);
		imagesLength = images.length;
		setBulletImages(parameters);
	}
	
	private void setBulletImages(ArrayList<String> parameters) {
		bulletArrayIndex = Integer.parseInt(parameters.get(4));
		bulletIndex = Integer.parseInt(parameters.get(5));
//		enemy = player.getEnemy();//used in the collision
		bulletImages = bulletSequence.get(bulletArrayIndex);
		bulletImage = bulletImages[bulletIndex];
		bulletLength = bulletImages.length;
		if(player.isMirrored()) {
			bulletX = x_ref - images[imageIndex].getWidth() / 2 - 
					bulletImages[bulletIndex].getWidth();
		}
		else {
			bulletX = x_ref + images[imageIndex].getWidth() / 2;
		}
	}
	
	public Rectangle getBounds() {
//		if(bulletImage != null) {
//			int width = bulletImage.getWidth();
//			int height = bulletImage.getHeight();
//			packet = new Packet(ActionCode.ATTACK_BOUNDS);
//			packet.add(bulletX);//1
//			packet.add(player.getPaintY());//2
//			packet.add(width);//3
//			packet.add(height);//4
//		}
//		else {
//			packet = new Packet(ActionCode.EMPTY_ATTACK_BOUNDS);
//		}
		if(bulletImage != null) {
			int width = bulletImage.getWidth();
			int height = bulletImage.getHeight();
			Rectangle bounds = new Rectangle(bulletX, player.getYRef(), width, height);
			return bounds;
		}
		return new Rectangle();
	}

	@Override
	public void preparePacketForImageLength() {
		packet = new Packet(ActionCode.ATTACK_IMAGES_LENGTH);
		packet.add(imagesLength);//1
		packet.add(bulletLength);//2
		packet.add(bulletX);//3
	}

	@Override
	public void setAttackVariables(ArrayList<String> parameters) {
		imageIndex = Integer.parseInt(parameters.get(3));
		bulletIndex = Integer.parseInt(parameters.get(4));
		bulletX = Integer.parseInt(parameters.get(5));
		moveBullet = Boolean.parseBoolean(parameters.get(6));
	}

	@Override
	public void setImages() {
		x_ref = player.getXRef();
		enemy = player.getEnemy();
		images = attackSequence.get(arrayAttackIndex);
		imagesLength = images.length;
		setBulletImages();
	}
	
	private void setBulletImages() {
		bulletImages = bulletSequence.get(bulletArrayIndex);
		bulletLength = bulletImages.length;
		if(player.isMirrored()) {
			bulletX = x_ref - images[imageIndex].getWidth() / 2 - 
					bulletImages[bulletIndex].getWidth();
		}
		else {
			bulletX = x_ref + images[imageIndex].getWidth() / 2;
		}
	}

}
