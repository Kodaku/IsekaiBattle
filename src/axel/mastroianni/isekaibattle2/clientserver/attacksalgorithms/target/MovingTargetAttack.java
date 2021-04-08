package axel.mastroianni.isekaibattle2.clientserver.attacksalgorithms.target;

import java.awt.Rectangle;
import java.util.ArrayList;

import axel.mastroianni.isekaibattle2.clientserver.Packet;
import axel.mastroianni.isekaibattle2.clientserver.Player;
import axel.mastroianni.isekaibattle2.clientserver.attacksalgorithms.Attack;
import axel.mastroianni.isekaibattle2.constantsclass.ActionCode;
import axel.mastroianni.isekaibattle2.constantsclass.Initialization;

public class MovingTargetAttack extends Attack {
	
	private int movingIndex = 0;
	private int movingLength = 0;
	private int movingArrayIndex = 0;
	
	private int targetIndex = 0;
	private int targetLength = 0;
	private int targetArrayIndex = 0;
	
	private boolean hitted = false;
	private boolean drawTarget = false;
	private boolean drawMoving = false;

	public MovingTargetAttack(Player player) {
		super(player);
		id = 7;
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
		imageIndex = 0;
		movingIndex = 0;
		movingArrayIndex = 0;
		targetIndex = 0;
		targetArrayIndex = 0;
		arrayAttackIndex = 0;
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
		packet.add(movingArrayIndex);//5
		packet.add(movingIndex);//6
		packet.add(targetArrayIndex);//7
		packet.add(targetIndex);//8
	}

	@Override
	public void preparePacketForRepaint(int playerID, int state) {
		packet = new Packet(ActionCode.DRAW_ATTACK);
		packet.add(playerID);//1
		packet.add(state);//2
		packet.add(imageIndex);//3
		packet.add(drawMoving);//4
		packet.add(movingIndex);//5
		packet.add(drawTarget);//6
		packet.add(targetIndex);//7
		packet.add(enemy.getXRef());//8
		packet.add(enemy.getYRef());//9
	}

	@Override
	public void setAttackLength(ArrayList<String> parameters) {
		imagesLength = Integer.parseInt(parameters.get(1));
		movingLength = Integer.parseInt(parameters.get(2));
		targetLength = Integer.parseInt(parameters.get(3));
	}

	@Override
	public void setBounds(ArrayList<String> parameters) {
		
	}
	
}
