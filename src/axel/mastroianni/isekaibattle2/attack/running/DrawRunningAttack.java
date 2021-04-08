package axel.mastroianni.isekaibattle2.attack.running;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import axel.mastroianni.isekaibattle2.attack.DrawAttack;
import axel.mastroianni.isekaibattle2.clientserver.Packet;
import axel.mastroianni.isekaibattle2.constantsclass.ActionCode;
import axel.mastroianni.isekaibattle2.constantsclass.Initialization;
import axel.mastroianni.isekaibattle2.player.DrawPlayerOld;

public class DrawRunningAttack extends DrawAttack {
	
	private BufferedImage attackImage;
	
	private ArrayList<BufferedImage[]> runningSequence;
	protected BufferedImage[] runningImages;
	protected BufferedImage runningImage;
	
	protected int runningIndex = 0;
	private int runningArrayIndex = 0;
	protected int runningLength = 0;
	protected int runX = 0;
	protected int vx = 10;
	
	protected boolean hitted = false;
	protected boolean run = false;
	
	public DrawRunningAttack(ArrayList<BufferedImage[]> attackSequence, ArrayList<BufferedImage[]>
			runningSequence, DrawPlayerOld player) {
		super(attackSequence, player);
		this.runningSequence = runningSequence;
		id = 5;
	}

	@Override
	public void draw(Graphics g) {
		attackImage = images[imageIndex];
		int x =  player.getXRef() - attackImage.getWidth() / 2;
		int y = player.getYRef() - attackImage.getHeight(); 
		if(!run) {
			g.drawImage(attackImage, x, y, null);
		}
		else {
			runningImage = runningImages[runningIndex];
			x = runX - runningImage.getWidth() / 2;
			y = player.getYRef() - runningImage.getHeight();
			g.drawImage(runningImage, x, y, null);
		}
	}

	@Override
	public void move() {
		attackImage = images[imageIndex];
		imageIndex++;
		if(imageIndex == imagesLength) {
			run = true;
			imageIndex--;
			moveRunning();
		}
	}
	
	public void moveRunning() {
		runX = player.getXRef();
		runningImage = runningImages[runningIndex];
		runningIndex++;
		runningIndex = runningIndex % runningLength;
		if(player.isMirrored()) {
			runX -= vx;
		}
		else {
			runX += vx;
		}
		if(runX >= player.getBackground().getWidth()) {
			player.setIsMirrored(!player.isMirrored());
			runX = player.getBackground().getWidth();
			player.setState(Initialization.STATE_STAND);
		}
		else if(runX <= 0) {
			runX = runningImages[runningIndex].getWidth();
			player.setIsMirrored(!player.isMirrored());
			player.setState(Initialization.STATE_STAND);
		}
		player.setXRef(runX);
		if(!hitted) {
			checkCollision();
		}
		if(hitted) {
			changeMoving();
		}
	}
	
	public void changeMoving() {
		reset();
	}
	
	private void checkCollision() {
		Rectangle bounds = getBounds();
		if(bounds != null && bounds.intersects(enemy.getBounds())) {
			changeEnemyState();
		}
	}
	
	protected void changeEnemyState() {
		hitted = true;
		enemy.setState(Initialization.STATE_HIT);
		enemy.reduceLife();
	}
	
	protected void reset() {
		run = false;
		hitted = false;
		player.setState(Initialization.STATE_STAND);
		if(enemy.getState() == Initialization.STATE_HIT) {
			enemy.setState(Initialization.STATE_STAND);
		}
		runningArrayIndex = 0;
		imageIndex = 0;
		arrayAttackIndex = 0;
		runningIndex = 0;
	}

	@Override
	public void setImages(ArrayList<String> parameters) {
		arrayAttackIndex = Integer.parseInt(parameters.get(3));
//		enemy = player.getEnemy();
		images = attackSequence.get(arrayAttackIndex);
		imagesLength = images.length;
		setRunningImages(parameters);
	}
	
	private void setRunningImages(ArrayList<String> parameters) {
		runningArrayIndex = Integer.parseInt(parameters.get(5));
		runningIndex = Integer.parseInt(parameters.get(6));
		runningImages = runningSequence.get(runningArrayIndex);
		runningLength = runningImages.length;
	}

	@Override
	public Rectangle getBounds() {
		if(runningImage != null) {
			int width = runningImage.getWidth();
			int height = runningImage.getHeight();
//			packet = new Packet(ActionCode.ATTACK_BOUNDS);
//			packet.add(runX);//1
//			packet.add(player.getPaintY());//2
//			packet.add(width);//3
//			packet.add(height);//4
			Rectangle bounds = new Rectangle(runX, y_ref,width, height);
			return bounds;
		}
		else {
//			packet = new Packet(ActionCode.EMPTY_ATTACK_BOUNDS);
			return new Rectangle();
		}
	}

	@Override
	public void preparePacketForImageLength() {
		packet = new Packet(ActionCode.ATTACK_IMAGES_LENGTH);
		packet.add(imagesLength);//1
		packet.add(runningLength);//2
	}

	@Override
	public void setAttackVariables(ArrayList<String> parameters) {
		imageIndex = Integer.parseInt(parameters.get(3));
		run = Boolean.parseBoolean(parameters.get(4));
		runningIndex = Integer.parseInt(parameters.get(5));
	}

	@Override
	public void setImages() {
		images = attackSequence.get(arrayAttackIndex);
		imagesLength = images.length;
		enemy = player.getEnemy();
		setRunningImages();
	}
	
	private void setRunningImages() {
		runningImages = runningSequence.get(runningArrayIndex);
		runningLength = runningImages.length;
	}

}
