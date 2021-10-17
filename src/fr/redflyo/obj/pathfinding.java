package fr.redflyo.obj;

import java.util.ArrayList;

public class pathfinding {
	
	
	ArrayList<line> Lcells = new ArrayList<line>();
	
	
	public pathfinding() 		
	{
		Lcells = new ArrayList<line>();
	}
	
	
	public int getDistanceWithCell(history loc) {
		
		System.out.println("loc : "+ loc.x + "/"+loc.y);
		return tools.getCell(Lcells, loc).distance;
		
		
		
	}
	

	
	
	public ArrayList<line> createDistanceCenter(history currentCell, ArrayList<line> colums){
		
		Lcells = colums;
		
		System.out.println("Distance creation...");

		createDistanceInMaze(currentCell, Lcells);		// get a List with all the distance, il est trié
	
		System.out.println("Distance created");
		
		
		return Lcells;
		
	
		
	}
	
	
	
	
	private void createDistanceInMaze(history currentCell,ArrayList<line> colums) {
		
		
		

		
		
		
		ArrayList<cell> temp = new ArrayList<cell>();
		temp.add(tools.getCell(Lcells, currentCell));
		int cpt = 0;
		while (temp.size() > cpt) {
			
			cell c = temp.get(cpt);
			cell up = tools.getCellUp(Lcells, c.location);
			cell left = tools.getCellLeft(Lcells, c.location);
			cell right = tools.getCellRight(Lcells, c.location);
			cell down = tools.getCellDown(Lcells, c.location);
			
			cell selfCell = tools.getCell(Lcells, c.location);
			
			System.out.println(cpt);
			

			if ( up != null && up.down && MazeLimitZone(up)) {			
				
				temp = addCellinTemp(up,temp,c.distance + 1) ;
			}
			if ( left != null && left.right && MazeLimitZone(left)) {			
				temp = addCellinTemp(left,temp,c.distance + 1) ;
			}
			if( down != null && selfCell.down && MazeLimitZone(down)) {
				temp = addCellinTemp(down,temp,c.distance + 1) ;
			}
			if( right != null && selfCell.right && MazeLimitZone(right)) {
				temp = addCellinTemp(right,temp,c.distance + 1) ;
			}
			
			
			addCellinLcells(c);
			
			cpt++;
		}
		
		
		System.out.println("add in list");
		
	
		
		
		
	}
	
	
	private ArrayList<cell> addCellinTemp(cell currentCell,ArrayList<cell> temp,int distance) {

		boolean contain = false;
		
		for (cell c : temp) {
			
			if(c.location.x == currentCell.location.x && c.location.y == currentCell.location.y ) {
				
				contain = true;
				
			}
			
		}
		
		if(!contain) {
			currentCell.distance = distance;
			temp.add(currentCell);
			System.out.println(distance);
		}
		
		
		
		return temp;
		
	}
	
	private void addCellinLcells(cell c) {

		
		line l = Lcells.get(c.location.x);
		cell d = l.data.get(c.location.y);
		
		
		
		if(d.distance == 0) {
			
			l.data.set(c.location.y, c);
			Lcells.set(c.location.x, l);
			
		}
		
		
	}
	
	 
	// Verify if zone are 
	
	public static Boolean MazeLimitZone(cell c) {
		
	
		if(c.type == ZONE_TYPE.TRADITIONAL) {
			return true;
		}
		
		return false;
		
	}
	
	
	
	
	
	
}
