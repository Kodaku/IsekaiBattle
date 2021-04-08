package axel.mastroianni.isekaibattle2.attack.physical;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import axel.mastroianni.isekaibattle2.attack.DrawAttack;
import axel.mastroianni.isekaibattle2.clientserver.Packet;
import axel.mastroianni.isekaibattle2.constantsclass.ActionCode;
import axel.mastroianni.isekaibattle2.constantsclass.Initialization;
import axel.mastroianni.isekaibattle2.player.DrawPlayerOld;

public class DrawPhysicalAttack extends DrawAttack{
	/**
	 * 0
	 */
	
	private BufferedImage attackImage;
	
	private boolean hitted = false;

	public DrawPhysicalAttack(ArrayList<BufferedImage[]> attackSequence, DrawPlayerOld player) {
		super(attackSequence, player);
		id = 0;
	}
	
	public void draw(Graphics g) {
		int x = 0;
		int y = 0;
		attackImage = images[imageIndex];
		if(player.isMirrored()) {
			x = player.getXRef() - attackImage.getWidth();
		}
		else {
			x = player.getXRef();
		}
		y = player.getYRef() - attackImage.getHeight();
		g.drawImage(attackImage, x, y, null);
	}
	
	public void move() {
		attackImage = images[imageIndex];
		imageIndex++;
		if(getBounds().intersects(enemy.getBounds()) && !hitted) {
			hitted = true;
			enemy.setState(Initialization.STATE_HIT);
			enemy.reduceLife();
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
					imageIndex = 0;
					hitted = false;
					player.setState(Initialization.STATE_STAND);
					if(enemy.getState() == Initialization.STATE_HIT) {
						enemy.setState(Initialization.STATE_STAND);
					}
				}
			}
			if(arrayAttackIndex >= attackSequence.size()) {
				arrayAttackIndex = 0;
			}
		}
	}
	
	public void setImages(ArrayList<String> parameters) {
		arrayAttackIndex = Integer.parseInt(parameters.get(3));
		imageIndex = Integer.parseInt(parameters.get(4));
		enemy = player.getEnemy();
		images = attackSequence.get(arrayAttackIndex);
		attackImage = images[imageIndex];
		imagesLength = images.length;
	}
	
	public Rectangle getBounds() {
		if(attackImage != null) {
			int width = attackImage.getWidth();
			int height = attackImage.getHeight();
			int x = 0;
			if(player.isMirrored()) {
				x = player.getXRef() - attackImage.getWidth();
			}
			else {
				x = player.getXRef();
			}
//			packet = new Packet(ActionCode.ATTACK_BOUNDS);
//			packet.add(x);//1
//			packet.add(player.getPaintY());//2
//			packet.add(width);//3
//			packet.add(height);//4
			Rectangle bounds = new Rectangle(x, player.getYRef(), width, height);
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
		packet.add(attackSequence.size());//2
	}

	@Override
	public void setAttackVariables(ArrayList<String> parameters) {
		imageIndex = Integer.parseInt(parameters.get(3));
	}

	@Override
	public void setImages() {
		images = attackSequence.get(arrayAttackIndex);
		imagesLength = images.length;
		enemy = player.getEnemy();
	}

}
