package fr.redflyo.labyrinthe;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;



public class main extends JavaPlugin {

	event event;
	GenerateLabyrinthe maze;
	
	@Override
	public void onEnable() {
		
		this.event = new event(this,"mazeRed");
		
		PluginManager plug = Bukkit.getServer().getPluginManager();
		plug.registerEvents(event,this);
		
		Command com = new Command(this);
		this.getCommand("door").setExecutor(com);
		this.getCommand("doorNextState").setExecutor(com);
		
		maze = new GenerateLabyrinthe("mazeRed");
		maze.loadMaze();
		
	}
	
	@Override	
	public void onDisable() {
		
	}
	
	
}
