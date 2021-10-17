package fr.redflyo.labyrinthe;

import java.util.Random;

import org.bukkit.Location;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


import fr.redflyo.obj.cell;
import fr.redflyo.obj.history;
import fr.redflyo.obj.tools;

public class Command implements CommandExecutor {

	main main;
	
	public Command(main main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		if (sender.isOp()  && sender instanceof Player) {
			
			
			
			if(arg2.equalsIgnoreCase("door")) {
				
				Player p = (Player) sender;
				
				int max = main.maze.doorsGlobal.size();
				
				Random rd = new Random();
				
				cell cellDoor = main.maze.doorsGlobal.get(rd.nextInt(max));
				
				history b = tools.getBlock(cellDoor.location);
				p.teleport(new Location(p.getWorld(),b.x, 64 , b.y));
				
				
			}
			
			if(arg2.equalsIgnoreCase("doorNextState")) {
				
				Player p = (Player) sender;
				
				
				Location loc = p.getLocation();
				int blocx = loc.getBlockX();
				int blocy = loc.getBlockZ();
				history h = new history(blocx, blocy);
				h = tools.getCell(h);
				cell c = tools.getCell(main.maze.lines, h);
				
				if(c.doorOrientation == 0) { p.sendMessage("There is no door here."); return false; }
				
				
				
				c.doorState++ ;
				
				if(c.doorState > 3) {
					c.doorState = 0;
				}
				
				
				tools.setCell(main.maze.lines, c);
				

				
				
				main.maze.drawCell(c, c.location.x, c.location.y, main.maze.sizey, p.getWorld());
				
				
				
				
			}
			
			
			
		}
		
		
		
		return false;
	}

}
