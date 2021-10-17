package fr.redflyo.obj;

import java.util.ArrayList;

public class line {

	public ArrayList<cell> data = new ArrayList<cell>();
	
	public cell open(cell cell) {
		
		cell.isOpen = true;
		data = new ArrayList<cell>();
		return cell;
		
	}
	
}
