package axel.mastroianni.isekaibattle2.clientserver;

public class UserPlayer extends Player {
	
	public UserPlayer(int playerID, Connection connection) {
		super(playerID, connection);
	}

	@Override
	public void move() {
		switch(state) {
		case STATE_DEAD:{
			dead();
			break;
		}
		case STATE_FINAL_ATTACK:{
			finalAttack();
			break;
		}
		case STATE_HIT:{
			hit();
			break;
		}
		case STATE_INTRO:{
			intro();
			break;
		}
		case STATE_JUMP:{
			jump();
			break;
		}
		case STATE_ATTACK:{
			normalAttack();
			break;
		}
		case STATE_RUN:{
			run();
			break;
		}
		case STATE_SPECIAL_ATTACK:{
			specialAttack();
			break;
		}
		case STATE_STAND:{
			stand();
			break;
		}
		case STATE_ESCAPE:{
			escape();
			break;
		}
		}
	}

}
