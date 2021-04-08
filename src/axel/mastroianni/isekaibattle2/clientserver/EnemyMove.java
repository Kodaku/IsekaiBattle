package axel.mastroianni.isekaibattle2.clientserver;

public class EnemyMove extends Player {
	
	private Player player;

	public EnemyMove(int playerID, Connection connection, Player player) {
		super(playerID, connection);
		this.player = player;
		this.player.setXRef(400);
		this.player.setIsMirrored(true);
	}
	
	@Override
	public void move() {
		player.move();
	}
	
	@Override
	public void convertInState(int keyCode) {
		player.convertInState(keyCode);
	}
	
	@Override
	public void setAttacks() {
		player.setAttacks();
	}
	
	@Override
	public void setTransformation() {
		player.setTransformation();
	}
}
