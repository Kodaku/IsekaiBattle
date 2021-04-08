package axel.mastroianni.isekaibattle2.player;

import java.awt.event.KeyEvent;
import java.util.Random;

import axel.mastroianni.isekaibattle2.constantsclass.Initialization;
import axel.mastroianni.isekaibattle2.menu.GamePanel;

public class EnemyPlayer extends DrawPlayerOld {
	
	private static final int TRANSFORMATION_CONST = 100;
	
	private int specialCount = 0;
	private int jumpProbability = 40;
	private int standProbability = 60;
	
	private int transformationNumber = 5;
	
	private Random rand = new Random();

	public EnemyPlayer(String name, int id, GamePanel gamePanel) {
		super(name, id, gamePanel);
		x_ref = 400;
		isMirrored = true;
	}
	
	public void move() {
		switch(state) {
		case Initialization.STATE_INTRO:{
			intro();
			break;
		}
		case Initialization.STATE_RUN:{
			run();
			break;
		}
		case Initialization.STATE_ATTACK:{
			normalAttack();
			break;
		}
		case Initialization.STATE_SPECIAL_ATTACK:{
			specialAttack();
			break;
		}
		case Initialization.STATE_FINAL_ATTACK:{
			finalAttack();
			break;
		}
		case Initialization.STATE_STAND:{
			stand();
			break;
		}
		case Initialization.STATE_JUMP:{
			jump();
			break;
		}
		case Initialization.STATE_DEAD:{
			dead();
			break;
		}
		}
	}
	
	@Override
	public void intro() {
		imageIndex++;
		if(imageIndex == imagesLength) {
			setState(Initialization.STATE_STAND);//changing the value of some booleans
		}
	}
	
	@Override
	public void run() {
		int transformationExtraction = rand.nextInt() % TRANSFORMATION_CONST;
		if(transformationExtraction == transformationNumber) {
			convertInState(KeyEvent.VK_T);
		}
		if(enemy.getState() == Initialization.STATE_INTRO) {
			setState(Initialization.STATE_STAND);
		}
		else {
			super.run();
		}
		if(!enemy.isAttacking()) {
			if(getBounds().intersects(enemy.getBounds()) || distanceAttack()) {
				if(enemy.isJumping()) {
					setState(Initialization.STATE_STAND);
				}
				else {
					chooseAttack();
				}
			}
		}
		else if(enemy.isAttacking()) {
			chooseMove();
		}
	}
	
	private void chooseAttack() {
		if(mana == Initialization.MAX_MANA && specialCount >= 1) {
			specialCount = 0;
			convertInState(KeyEvent.VK_F);
		}
		else if(mana >= Initialization.MAX_MANA / 2 && specialCount < 1) {
			specialCount++;
			convertInState(KeyEvent.VK_S);
		}
		else {
			convertInState(KeyEvent.VK_N);
		}
	}
	
	@Override
	public void stand() {
		if(!enemy.isAttacking() && enemy.getState() != Initialization.STATE_INTRO) {
			if(x_ref < enemy.getXRef()) {
				convertInState(KeyEvent.VK_RIGHT);
			}
			else {
				convertInState(KeyEvent.VK_LEFT);
			}
		}
		else {
			super.stand();
		}
	}
	
	private void chooseMove() {
		if(enemy.getState() == Initialization.STATE_FINAL_ATTACK) {
			setState(Initialization.STATE_STAND);
		}
		else {
			if(jumpProbability >= standProbability) {
				jumpProbability -= 20;
				standProbability += 20;
				convertInState(KeyEvent.VK_UP);
			}
			else {
				jumpProbability += 20;
				standProbability -= 20;
				setState(Initialization.STATE_STAND);
			}
		}
	}
	
	private boolean distanceAttack() {
		if(mana == Initialization.MAX_MANA && specialCount >= 1) {
			switch(actualFinalAttackNormal.getId()) {
			case 1: case 2: case 3: case 4: case 5: case 6: case 7: case 8: case 9:{
				return true;
			}
			}
		}
		else if(mana >= Initialization.MAX_MANA / 2 && specialCount < 1) {
			switch(actualSpecialAttackNormal.getId()) {
			case 1: case 2: case 3: case 4: case 5: case 6: case 7: case 8: case 9:{
				return true;
			}
			}
		}
		return false;
	}

}
