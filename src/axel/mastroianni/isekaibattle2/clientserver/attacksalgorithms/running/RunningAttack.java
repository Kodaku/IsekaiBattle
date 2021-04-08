package axel.mastroianni.isekaibattle2.clientserver.attacksalgorithms.running;

import java.awt.Rectangle;
import java.util.ArrayList;

import axel.mastroianni.isekaibattle2.clientserver.Packet;
import axel.mastroianni.isekaibattle2.clientserver.Player;
import axel.mastroianni.isekaibattle2.clientserver.attacksalgorithms.Attack;
import axel.mastroianni.isekaibattle2.constantsclass.ActionCode;

public class RunningAttack extends Attack {
	
	protected int runX = 0;
	
	protected int runningIndex = 0;
	protected int runningLength = 0;
	protected int runningArrayIndex = 0;
	
	private int runningX;
	private int runningY;
	private int runningWidth;
	private int runningHeight;
	
	protected int vx = 10;
	
	protected boolean hitted = false;
	private boolean run = false;
	
	private Rectangle runningBounds = new Rectangle();

	public RunningAttack(Player player) {
		super(player);
		id = 5;
	}

	@Override
	public void move() {
		imageIndex++;
		if(imageIndex == imagesLength) {
			run = true;
			imageIndex--;
			moveRunning();
		}
	}
	
	public void moveRunning() {
		runX = player.getXRef();
		runningIndex++;
		runningIndex = runningIndex % runningLength;
		if(player.isMirrored()) {
			runX -= vx;
		}
		else {
			runX += vx;
		}
		if(runX >= player.getBgWidth()) {
			player.setIsMirrored(!player.isMirrored());
			runX = player.getBgWidth();
			player.setState(Player.STATE_STAND);
		}
		else if(runX <= 0) {
			runX = 30;
			player.setIsMirrored(!player.isMirrored());
			player.setState(Player.STATE_STAND);
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
		player.requestAttackBounds();
		enemy.requestPlayerBounds();
		Rectangle bounds = getBounds();
		if(bounds != null && bounds.intersects(enemy.getBounds())) {
			changeEnemyState();
		}
	}
	
	protected void changeEnemyState() {
		hitted = true;
		enemy.setState(Player.STATE_HIT);
		enemy.reduceLife();
	}
	
	public void reset() {
		run = false;
		hitted = false;
		player.setState(Player.STATE_STAND);
		if(enemy.getState() == Player.STATE_HIT) {
			enemy.setState(Player.STATE_STAND);
		}
		imageIndex = 0;
		arrayAttackIndex = 0;
		runningIndex = 0;
		runningArrayIndex = 0;
	}
	
	@Override
	public void setBounds(ArrayList<String> parameters) {
		String action = parameters.get(0);
		if(action.equals(ActionCode.ATTACK_BOUNDS)) {
			runningX = Integer.parseInt(parameters.get(1));
			runningY = Integer.parseInt(parameters.get(2));
			runningWidth = Integer.parseInt(parameters.get(3));
			runningHeight = Integer.parseInt(parameters.get(4));
			runningBounds = new Rectangle(runningX, runningY, runningWidth, runningHeight);
		}
		else if(action.equals(ActionCode.EMPTY_ATTACK_BOUNDS)) {
			runningBounds = new Rectangle();
		}
	}

	@Override
	public Rectangle getBounds() {
		return runningBounds;
	}

	@Override
	public void preparePacketForImages(int playerID, int state) {
		packet = new Packet(ActionCode.SET_ATTACK_IMAGES);
		packet.add(playerID);//1
		packet.add(state);//2
		packet.add(arrayAttackIndex);//3
		packet.add(imageIndex);//4
		packet.add(runningArrayIndex);//5
		packet.add(runningIndex);//6
	}

	@Override
	public void preparePacketForRepaint(int playerID, int state) {
		packet = new Packet(ActionCode.DRAW_ATTACK);
		packet.add(playerID);//1
		packet.add(state);//2
		packet.add(imageIndex);//3
		packet.add(run);//4
		packet.add(runningIndex);//5
	}

	@Override
	public void setAttackLength(ArrayList<String> parameters) {
		imagesLength = Integer.parseInt(parameters.get(1));
		runningLength = Integer.parseInt(parameters.get(2));
	}

}
