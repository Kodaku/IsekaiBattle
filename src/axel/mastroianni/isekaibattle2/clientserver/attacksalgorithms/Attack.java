package axel.mastroianni.isekaibattle2.clientserver.attacksalgorithms;

import java.awt.Rectangle;
import java.util.ArrayList;

import axel.mastroianni.isekaibattle2.clientserver.Connection;
import axel.mastroianni.isekaibattle2.clientserver.EnemyMove;
import axel.mastroianni.isekaibattle2.clientserver.Packet;
import axel.mastroianni.isekaibattle2.clientserver.Player;

public abstract class Attack {
	
	protected int imageIndex = 0;
	protected int imagesLength;
	protected int arrayAttackIndex = 0;
	
	protected int x_ref;
	protected int y_ref;
	
	protected int id;
	
	protected Player player;
	protected EnemyMove enemy;
	
	protected Packet packet;
	
	public Attack(Player player) {
		this.player = player;
		
		x_ref = player.getXRef();
		y_ref = player.getYRef();
	}
	
	public void setEnemy(EnemyMove enemy) {
		this.enemy = enemy;
	}
	
	public abstract void move();
	
	public abstract void setBounds(ArrayList<String> parameters);
	
	public abstract Rectangle getBounds();
	
	public abstract void preparePacketForImages(int playerID, int state);
	
	public abstract void preparePacketForRepaint(int playerID, int state);
	
	public abstract void setAttackLength(ArrayList<String> parameters);
	
	public int getId() {
		return id;
	}
	
	public void sendPacket() {
		Connection connection = player.getConnection();
		connection.sendToClient(packet);
		connection.broadcast(player.getName(), packet);
	}


}
