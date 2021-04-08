package axel.mastroianni.isekaibattle2.clientserver.attacksalgorithms.target;

import java.awt.Rectangle;
import java.util.ArrayList;

import axel.mastroianni.isekaibattle2.clientserver.Packet;
import axel.mastroianni.isekaibattle2.clientserver.Player;
import axel.mastroianni.isekaibattle2.clientserver.attacksalgorithms.Attack;
import axel.mastroianni.isekaibattle2.constantsclass.ActionCode;
import axel.mastroianni.isekaibattle2.constantsclass.Initialization;

public class TargetAttack extends Attack {
	
	private int targetIndex = 0;
	private int targetLength = 0;
	private int targetArrayIndex = 0;
	
	private boolean hitted = false;
	private boolean moveTarget = false;

	public TargetAttack(Player player) {
		super(player);
		id = 3;
	}

	@Override
	public void move() {
		imageIndex++;
		if(imageIndex >= imagesLength / 2 && imageIndex != imagesLength) {
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
				imageIndex--;
				moveTarget = true;
				moveTarget();
			}
		}
	}
	
	private void moveTarget() {
		targetIndex++;
		if(getBounds().intersects(enemy.getBounds()) && !hitted) {
			hitted = true;
			enemy.setState(Player.STATE_HIT);
			enemy.reduceLife();
		}
		if(targetIndex == targetLength) {
			moveTarget = false;
			hitted = false;
			player.setState(Player.STATE_STAND);
			if(enemy.getState() == Player.STATE_HIT) {
				enemy.setState(Player.STATE_STAND);
			}
			imageIndex = 0;
			arrayAttackIndex = 0;
			targetIndex = 0;
		}
	}

	@Override
	public Rectangle getBounds() {
		return null;
	}

	@Override
	public void preparePacketForImages(int playerID, int state) {
		packet = new Packet(ActionCode.SET_ATTACK_IMAGES);
		packet.add(playerID);//1
		packet.add(state);//2
		packet.add(arrayAttackIndex);//3
		packet.add(imageIndex);//4
		packet.add(targetArrayIndex);//5
		packet.add(targetIndex);//6
	}

	@Override
	public void preparePacketForRepaint(int playerID, int state) {
		packet = new Packet(ActionCode.DRAW_ATTACK);
		packet.add(playerID);//1
		packet.add(state);//2
		packet.add(imageIndex);//3
		packet.add(moveTarget);//4
		packet.add(enemy.getXRef());//5
		packet.add(enemy.getYRef());//6
		packet.add(targetIndex);//7
	}

	@Override
	public void setAttackLength(ArrayList<String> parameters) {
		imagesLength = Integer.parseInt(parameters.get(1));
		targetLength = Integer.parseInt(parameters.get(2));
	}

	@Override
	public void setBounds(ArrayList<String> parameters) {
		
	}

}
