package axel.mastroianni.isekaibattle2.clientserver;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

public class Game {
	
	private UserPlayer player;
	
	private EnemyMove enemy;
	
	private Timer timer;
	
	public Game(UserPlayer player, EnemyMove enemy, Connection connection) {
		this.player = player;
		this.enemy = enemy;
		
		timer = new Timer(100,new ActionListener() {
			//send a packet with the useful information every tick
			@Override
			public void actionPerformed(ActionEvent e) {
				Game.this.player.move();
				Game.this.enemy.move();
			}
		});
	}
	
	public void start() {
		timer.start();
	}
	
	public void stop() {
		timer.stop();
	}
	
}
