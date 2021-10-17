package fr.redflyo.obj;

public class cell {
	// false = fermer
	public boolean right = false; // fermer
	public boolean down = false; // fermer
	public boolean downright = false;
	public boolean isOpen = false;
	public int doorState = 0; // Porte fermer
	public int doorOrientation = 0 ;// 0 == not a door ; right ==  1 ; down == 2; 3 is impossible
	public ZONE_TYPE type = ZONE_TYPE.TRADITIONAL;
	public history location;
	public int distance = 0;
	
	
	// levier activable
	
	
	
}
