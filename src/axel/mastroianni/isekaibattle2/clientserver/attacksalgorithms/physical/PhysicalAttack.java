package axel.mastroianni.isekaibattle2.clientserver.attacksalgorithms.physical;

import java.awt.Rectangle;
import java.util.ArrayList;

import axel.mastroianni.isekaibattle2.clientserver.Packet;
import axel.mastroianni.isekaibattle2.clientserver.Player;
import axel.mastroianni.isekaibattle2.clientserver.attacksalgorithms.Attack;
import axel.mastroianni.isekaibattle2.constantsclass.ActionCode;

public class PhysicalAttack extends Attack {
	
	private int physicalX;
	private int physicalY;
	private int physicalWidth;
	private int physicalHeight;
	
	private int sequenceSize = 0;
	
	private boolean hitted = false;
	
	private Rectangle physicalBounds = new Rectangle();

	public PhysicalAttack(Player player) {
		super(player);
		id = 0;
	}

	@Override
	public void move() {
//		attackImage = images[imageIndex];
		imageIndex++;
		player.requestAttackBounds();
		enemy.requestPlayerBounds();
		if(getBounds().intersects(enemy.getBounds()) && !hitted) {
			hitted = true;
			enemy.setState(Player.STATE_HIT);
			enemy.reduceLife();
		}
		if(imageIndex == imagesLength) {
			arrayAttackIndex++;
			if(player.getState() == Player.STATE_ATTACK) {
				imageIndex = 0;
				hitted = false;
				player.setState(Player.STATE_STAND);
				if(enemy.getState() == Player.STATE_HIT) {
					enemy.setState(Player.STATE_STAND);
				}
			}
			else {
				if(arrayAttackIndex < sequenceSize) {
					imageIndex = 0;
				}
				else {
					imageIndex = 0;
					hitted = false;
					player.setState(Player.STATE_STAND);
					if(enemy.getState() == Player.STATE_HIT) {
						enemy.setState(Player.STATE_STAND);
					}
				}
			}
			if(arrayAttackIndex >= sequenceSize) {
				arrayAttackIndex = 0;
			}
		}
	}
	
	@Override
	public void setBounds(ArrayList<String> parameters) {
		String action = parameters.get(0);
		if(action.equals(ActionCode.ATTACK_BOUNDS)) {
			physicalX = Integer.parseInt(parameters.get(1));
			physicalY = Integer.parseInt(parameters.get(2));
			physicalWidth = Integer.parseInt(parameters.get(3));
			physicalHeight = Integer.parseInt(parameters.get(4));
			physicalBounds = new Rectangle(physicalX, physicalY, physicalWidth, physicalHeight);
		}
		else if(action.equals(ActionCode.EMPTY_ATTACK_BOUNDS)) {
			physicalBounds = new Rectangle();
		}
	}

	@Override
	public Rectangle getBounds() {
		return physicalBounds;
	}

	@Override
	public void preparePacketForImages(int playerID, int state) {
		packet = new Packet(ActionCode.SET_ATTACK_IMAGES);
		packet.add(playerID);//1
		packet.add(state);//2
		packet.add(arrayAttackIndex);//3
		packet.add(imageIndex);//4
	}

	@Override
	public void preparePacketForRepaint(int playerID, int state) {
		packet = new Packet(ActionCode.DRAW_ATTACK);
		packet.add(playerID);//1
		packet.add(state);//2
		packet.add(imageIndex);//3
	}

	@Override
	public void setAttackLength(ArrayList<String> parameters) {
		imagesLength = Integer.parseInt(parameters.get(1));
		sequenceSize = Integer.parseInt(parameters.get(2));
	}

}
