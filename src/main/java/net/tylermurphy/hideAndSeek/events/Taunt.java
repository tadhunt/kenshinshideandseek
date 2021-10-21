package net.tylermurphy.hideAndSeek.events;

import static net.tylermurphy.hideAndSeek.Config.*;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import net.tylermurphy.hideAndSeek.Main;
import net.tylermurphy.hideAndSeek.util.Util;

public class Taunt {

	private final int temp;
	private String tauntPlayer;
	
	public Taunt(int temp) {
		this.temp = temp;
	}
	
	public void schedule() {
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
			public void run() {
				tryTaunt();
			}
		},20*60*5);
	}
	
	private void waitTaunt() {
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
			public void run() {
				tryTaunt();
			}
		},20*60);
	}
	
	private void tryTaunt() {
		if(temp != Main.plugin.gameId) return;
		if(Math.random() > .8) {
			executeTaunt();
		} else {
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
				public void run() {
					tryTaunt();
				}
			},20*60);
		}
	}
	
	private void executeTaunt() {
		Player taunted = null;
		int rand = (int) (Math.random()*Main.plugin.board.sizeHider());
		for(Player player : Main.plugin.board.getPlayers()) {
			if(Main.plugin.board.isHider(player)) {
				rand--;
				if(rand==0) {
					taunted = player;
					break;
				}
			}
		}
		if(taunted != null) {
			taunted.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "Oh no! You have been chosed to be taunted.");
			Util.broadcastMessage(tauntPrefix + " A random hider will be taunted in the next 30s");
			tauntPlayer = taunted.getName();
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
				public void run() {
					if(temp != Main.plugin.gameId) return;
					Player taunted = Main.plugin.board.getPlayer(tauntPlayer);
					if(taunted != null) {
						Firework fw = (Firework) taunted.getLocation().getWorld().spawnEntity(taunted.getLocation(), EntityType.FIREWORK);
						FireworkMeta fwm = fw.getFireworkMeta();
						fwm.setPower(4);
				        fwm.addEffect(FireworkEffect.builder()
				        		.withColor(Color.BLUE)
				        		.withColor(Color.RED)
				        		.withColor(Color.YELLOW)
				        		.with(FireworkEffect.Type.STAR)
				        		.with(FireworkEffect.Type.BALL)
				        		.with(FireworkEffect.Type.BALL_LARGE)
				        		.flicker(true)
				        		.withTrail()
				        		.build());
				        fw.setFireworkMeta(fwm);
				        Util.broadcastMessage(tauntPrefix + " Taunt has been activated");
					}
					tauntPlayer = "";
					waitTaunt();
				}
			},20*30);
		} else {
			waitTaunt();
		}
	}
	
}