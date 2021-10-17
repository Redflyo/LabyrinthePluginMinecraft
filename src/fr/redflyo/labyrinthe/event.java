package fr.redflyo.labyrinthe;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import fr.redflyo.obj.cell;
import fr.redflyo.obj.history;
import fr.redflyo.obj.tools;

public class event implements Listener{
	
	main main;
	String wName;
	public event(main main,String wName) {
		this.main = main;
		this.wName = wName;
	}
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		
		System.out.println(wName);
		Location loc = new Location(Bukkit.getWorld(wName), 0, 150, 0);
		p.teleport(loc);
		p.setFlying(true);
	}
	@EventHandler
	public void invOpen(InventoryOpenEvent ev) {
		
		System.out.println("InvOpen");
				
	}
	
	@EventHandler
	public void chat(AsyncPlayerChatEvent event) {
		Player p =	event.getPlayer();
		Location loc = p.getLocation();
		int blocx = loc.getBlockX();
		int blocy = loc.getBlockZ();
		history h = new history(blocx, blocy);
		h = tools.getCell(h);
		cell c = tools.getCell(main.maze.lines, h);
		
		tools.SendCellInfoToPlayer(p, c);
		
		
		
		
	}
	
	
	
	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		Location loc = p.getLocation();
		int blocx = loc.getBlockX();
		int blocy = loc.getBlockZ();
		history h = new history(blocx, blocy);
		h = tools.getCell(h);
		cell c = new cell();
		c.isOpen = true;
		c.down = false;
		c.downright = false;
		c.right = false;
		
		
		p.sendMessage(String.valueOf(main.maze.pathFinding.getDistanceWithCell(h)));
		 
		//System.out.println("reponse: " +h.x+"/"+h.y);
		
	}
	//System.out.println("reponse: " +h2.x+"/"+h2.y);
	
}
