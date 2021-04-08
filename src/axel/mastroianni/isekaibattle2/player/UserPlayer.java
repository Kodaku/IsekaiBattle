package axel.mastroianni.isekaibattle2.player;

import axel.mastroianni.isekaibattle2.constantsclass.Initialization;
import axel.mastroianni.isekaibattle2.menu.GamePanel;

public class UserPlayer extends DrawPlayerOld {

	public UserPlayer(String name, int id, GamePanel gamePanel) {
		super(name, id, gamePanel);
	}
	
	public void move() {
		switch(state) {
		case Initialization.STATE_DEAD:{
			dead();
			break;
		}
		case Initialization.STATE_FINAL_ATTACK:{
			finalAttack();
			break;
		}
		case Initialization.STATE_HIT:{
			hit();
			break;
		}
		case Initialization.STATE_INTRO:{
			intro();
			break;
		}
		case Initialization.STATE_JUMP:{
			jump();
			break;
		}
		case Initialization.STATE_ATTACK:{
			normalAttack();
			break;
		}
		case Initialization.STATE_RUN:{
			run();
			break;
		}
		case Initialization.STATE_SPECIAL_ATTACK:{
			specialAttack();
			break;
		}
		case Initialization.STATE_STAND:{
			stand();
			break;
		}
		case Initialization.STATE_ESCAPE:{
			escape();
			break;
		}
		}
	}

}
