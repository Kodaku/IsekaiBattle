package axel.mastroianni.isekaibattle2.attack.unused;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import axel.mastroianni.isekaibattle2.attack.DrawAttack;
import axel.mastroianni.isekaibattle2.player.DrawPlayerOld;

public class EnergeticAttack extends DrawAttack {
	/**
	 * 1
	 */
	
//	private ArrayList<BufferedImage[]> energeticSequence;
	
//	private BufferedImage[] energeticImages;
	
//	private int energeticIndex = 0;
//	private int energeticArrayIndex = 0;
//	private int energeticLength;
	
	private boolean moveEnergetic = false;
//	private boolean last = false;
//	private boolean hitted = false;

	private BufferedImage energeticImage;
	
	public EnergeticAttack(ArrayList<BufferedImage[]> attackSequence,
			ArrayList<BufferedImage[]> energeticSequence, DrawPlayerOld player) {
		super(attackSequence, player);
//		this.energeticSequence = energeticSequence;
		id = 1;
	}
	
	public void draw(Graphics g) {
		int x = 0;
		int y = 0;
//		x = player.getXRef() - images[imageIndex].getWidth() / 2;
//		y = player.getYRef() - images[imageIndex].getHeight();
		g.drawImage(images[imageIndex], x, (int)y, null);
		if(moveEnergetic) {
			drawEnergetic(g);
		}
	}
	
	private void drawEnergetic(Graphics g) {
//		int x = 0;
//		double y = 0;
//		if(player.isMirrored()) {
//			x = player.getXRef() - images[imageIndex].getWidth() / 2 - 
//					energeticImages[energeticIndex].getWidth();
//		}
//		else {
//			x = player.getXRef() + images[imageIndex].getWidth() / 2;
//		}
//		y = player.getYRef() - images[imageIndex].getHeight(); 
//		g.drawImage(energeticImages[energeticIndex], x, (int)y, null);
	}
	
//	public void move() {
//		imageIndex++;
//		if(arrayAttackIndex == attackSequence.size() - 1) {
//			last = true;
//		}
//		if(imageIndex >= imagesLength / 2 && imageIndex != imagesLength && last) {
//			moveEnergetic = true;
//			moveEnergetic();
//		}
//		if(imageIndex == imagesLength) {
//			arrayAttackIndex++;
//			if(player.getState() == DrawPlayerOld.STATE_ATTACK) {
//				imageIndex = 0;
//				hitted = false;
//				player.setState(DrawPlayerOld.STATE_STAND);
//				if(enemy.getState() == DrawPlayerOld.STATE_HIT) {
//					enemy.setState(DrawPlayerOld.STATE_STAND);
//				}
//			}
//			else {
//				if(arrayAttackIndex < attackSequence.size()) {
//					imageIndex = 0;
//					setImages();
//				}
//				else {
//					imageIndex--;
//					moveEnergetic = true;
//					moveEnergetic();
//				}
//			}
//		}
//	}
	
//	private void moveEnergetic() {
//		energeticImage = energeticImages[energeticIndex];
//		energeticIndex++;
//		if(getBounds().intersects(enemy.getBounds()) && !hitted) {
//			hitted = true;
//			enemy.setState(DrawPlayerOld.STATE_HIT);
//			enemy.reduceLife();
//		}
//		if(energeticIndex == energeticLength) {
//			energeticArrayIndex++;
//			if(energeticArrayIndex < energeticSequence.size()) {
//				energeticIndex = 0;
//				setEnergeticImages();
//			}
//			else {
//				moveEnergetic = false;
//				last = false;
//				hitted = false;
//				player.setState(DrawPlayerOld.STATE_STAND);
//				if(enemy.getState() == DrawPlayerOld.STATE_HIT) {
//					enemy.setState(DrawPlayerOld.STATE_STAND);
//				}
//				arrayAttackIndex = 0;
//				energeticArrayIndex = 0;
//				energeticIndex = 0;
//				imageIndex = 0;
//			}
//		}
//	}
	
	public void setImages() {
		images = attackSequence.get(arrayAttackIndex);
		imagesLength = images.length;
		setEnergeticImages();
	}
	
	private void setEnergeticImages() {
//		enemy = player.getEnemy();
//		energeticImages = energeticSequence.get(energeticArrayIndex);
//		energeticLength = energeticImages.length;
	}
	
	public Rectangle getBounds() {
		if(energeticImage != null) {
//			int width = energeticImage.getWidth();
//			int height = energeticImage.getHeight();
//			if(player.isMirrored()) {
//				bounds = new Rectangle(player.getXRef() - width, player.getYRef(),
//						width, height);
//			}
//			else {
//				bounds = new Rectangle(player.getXRef(), player.getYRef(),width, height);
//			}
		}
		return null;
	}

	@Override
	public void setImages(ArrayList<String> parameters) {
		
	}

	@Override
	public void preparePacketForImageLength() {
		
	}

	@Override
	public void setAttackVariables(ArrayList<String> parameters) {
		
	}

	@Override
	public void move() {
		
	}

}
