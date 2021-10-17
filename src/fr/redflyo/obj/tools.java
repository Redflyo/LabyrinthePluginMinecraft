package fr.redflyo.obj;

import java.util.ArrayList;

import org.bukkit.entity.Player;


public class tools {

	
	public static history getCell(history block) {
				
		history cellule = new history( (int) Math.floor( (block.x+4) / 10) , (int) Math.floor( (block.y+4) / 10));
		return cellule;
		
		
	}
	
	
	public static history getBlock(history cellule) {
		
		history block = new history( cellule.x * 10 - 4 , cellule.y * 10 - 4);
		
		return block;
		
		
	}
	
	public static cell getCellUp(ArrayList<line> maze,history currentCell) {
		
		return getCell(maze, new history(currentCell.x, currentCell.y - 1))	;
		
	}
	public static cell getCellDown(ArrayList<line> maze,history currentCell) {
		
		return getCell(maze, new history(currentCell.x, currentCell.y + 1))	;
		
	}
	public static cell getCellLeft(ArrayList<line> maze,history currentCell) {
		
		return getCell(maze, new history(currentCell.x - 1, currentCell.y))	;
		
	}
	public static cell getCellRight(ArrayList<line> maze,history currentCell) {
		
		return getCell(maze, new history(currentCell.x + 1, currentCell.y))	;
		
	}
	
	
	
	public static void SendCellInfoToPlayer(Player p,cell c) {
		
		p.sendMessage("Location : " + c.location.x + "/" + c.location.y);
		p.sendMessage("Type : " + c.type.toString());
		p.sendMessage("Distance : " + c.distance);
		p.sendMessage("DoorOrientation : " + c.doorOrientation);
		p.sendMessage("DoorState : " + c.doorState);
		p.sendMessage("Wall Down : " + c.down);
		p.sendMessage("Wall Right : " + c.right);
		p.sendMessage("Is Open : " + c.isOpen);
		
	}
	
	public static cell getCell(ArrayList<line> lines,int x ,int y) {
		
		if(lines == null) {
			return null;
		}
		
		if(lines.size() <= x || x < 0) {
			return null;
			
		}
		line l = lines.get(x);
		
		if(l.data.size() <= y || y < 0) {
			return null;
			
		}
		
		
		
			return l.data.get(y);

		
		
		
		
	}
	public static cell getCell(ArrayList<line> lines,history h) {
		
		return getCell(lines, h.x,h.y);
		
	}
	
	public static ArrayList<line> setCell(ArrayList<line> lines,cell c) {
		
		history loc = c.location;
		
		if(lines == null) {
			return null;
		}
		
		if(lines.size() <= loc.x || loc.x < 0) {
			return null;
			
		}
		line l = lines.get(loc.x);
		
		if(l.data.size() <= loc.y || loc.y < 0) {
			l.data.set(loc.y, c);
		}
		
		lines.set(loc.x, l);
		
		return lines;
		
		
		
		
		
	}
	
	

	
	
	
}
