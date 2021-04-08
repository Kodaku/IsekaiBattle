package axel.mastroianni.isekaibattle2.clientserver.attacksalgorithms.running;

import java.awt.Rectangle;
import java.util.ArrayList;

import axel.mastroianni.isekaibattle2.clientserver.Player;

public class RunningTeletransportTargetAttack extends RunningAttack {
	
	private int teletransportIndex = 0;
	private int teletransportLength = 0;
	private int teletransportArrayIndex = 0;
	
	private int targetIndex = 0;
	private int targetLength = 0;
	private int targetArrayIndex = 0;
	
	private boolean drawTarget = false;
	private boolean drawTeletransport = false;

	public RunningTeletransportTargetAttack(Player player) {
		super(player);
		id = 6;
	}
	
	@Override
	public void moveRunning() {
		runningIndex++;
		if(!player.isMirrored()) {
			runX += vx;
		}
		else {
			runX -= vx;
		}
		if(runningIndex == runningLength) {
			drawTeletransport = true;
			runningIndex--;
			moveTeletransport();
		}
	}
	
	private void moveTeletransport() {
		teletransportIndex++;
		if(teletransportIndex == teletransportLength) {
			drawTeletransport = false;
			drawTarget = true;
			teletransportIndex--;
			if(!hitted) {
				changeEnemyState();
			}
			changeMoving();
		}
	}
	
	@Override
	public void changeMoving() {
		moveTarget();
	}
	
	private void moveTarget() {
		targetIndex++;
		if(targetIndex == targetLength) {
			reset();
		}
	}
	
	@Override
	public void reset() {
		super.reset();
		teletransportIndex = 0;
		teletransportLength = 0;
		teletransportArrayIndex = 0;
		targetIndex = 0;
		targetLength = 0;
		targetArrayIndex = 0;
		drawTeletransport = false;
		drawTarget = false;
	}
	
	@Override
	public Rectangle getBounds() {
		return null;
	}
	
	@Override
	public void preparePacketForImages(int playerID, int state) {
		super.preparePacketForImages(playerID, state);
		packet.add(teletransportArrayIndex);//7
		packet.add(teletransportIndex);//8
		packet.add(targetArrayIndex);//9
		packet.add(targetIndex);//10
	}
	
	@Override
	public void setAttackLength(ArrayList<String> parameters) {
		super.setAttackLength(parameters);
		teletransportLength = Integer.parseInt(parameters.get(3));
		targetLength = Integer.parseInt(parameters.get(4));
	}
	
	@Override
	public void preparePacketForRepaint(int playerID, int state) {
		super.preparePacketForRepaint(playerID, state);
		packet.add(drawTeletransport);//6
		packet.add(teletransportIndex);//7
		packet.add(enemy.getXRef());//8
		packet.add(enemy.getYRef());//9
		packet.add(drawTarget);//10
		packet.add(targetIndex);//11
	}

}
