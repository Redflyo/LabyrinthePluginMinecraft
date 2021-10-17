package fr.redflyo.labyrinthe;


import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.block.Block;


import fr.redflyo.obj.ZONE_TYPE;
import fr.redflyo.obj.cell;
import fr.redflyo.obj.history;
import fr.redflyo.obj.line;
import fr.redflyo.obj.pathfinding;
import fr.redflyo.obj.tools;
import fr.redflyo.obj.vide;





	
public class GenerateLabyrinthe {

	public ArrayList<line> lines ;
	
	ArrayList<cell> doorsZones;
	
	public ArrayList<cell> doorsGlobal;
	
	public pathfinding pathFinding;
	
	ArrayList<PLAYER_ZONE> zonePlayerCoo;
	private String mazeName = "";
	public int BaseY = 60;
	
	public int sizex = 75;
	public int sizez = 75;
	public int sizey = 30;
	
	
	public GenerateLabyrinthe(String mapName) {
		mazeName = mapName;
		zonePlayerCoo = new ArrayList<PLAYER_ZONE>();
		doorsZones = new ArrayList<cell>();
	};
	
	public void loadMaze() {
		if(mazeExist()) {
			// maze already exist only load it
			System.out.println("MapFind");
			WorldCreator creator = new WorldCreator(mazeName);
			creator.type(WorldType.CUSTOMIZED);
			creator = creator.generator(new vide());
			World w = Bukkit.createWorld(creator);
			Bukkit.getWorlds().add(w);
			
		}
		else {
			// maze don't exist create it
			System.out.println("MapCreate");
			generateWorld(sizex, sizez, sizey,2,20);
		}
	}
	
	
	
	
	private ArrayList<line> generateGrille(int sizex,int sizey) {
		
		
		ArrayList<line> colum = new ArrayList<line>();
		
		for (int i = 0; i < sizex; i++) {
			
			line l = new line();
			
			
			for (int j = 0; j < sizey; j++) {
				
				cell c = new cell();
				
				c.location = new history(i, j);
				
				l.data.add(c);
				
				
			}
			
			colum.add(l);
			
		}
		
		return colum;
	}
	
	
	
	
	private World generateWorld(int sizex,int sizez,int sizey,int playerZone,int numSideZone) {
		
		
		lines = generateGrille(sizex, sizez);
		
		
		
		WorldCreator creator = new WorldCreator(mazeName);
		creator.type(WorldType.CUSTOMIZED);
		creator = creator.generator(new vide());
		
		World W = Bukkit.createWorld(creator);
		Bukkit.getWorlds().add(W);
		
		history spawn = createAllZones(lines, sizex, sizey, numSideZone,playerZone); 
		
		
		
		lines = DepthFirst(lines,sizex,sizez,spawn); // dig the maze
		
		// add natural zone for living inside
		
		lines = AddSpecialCell(lines,sizex,sizey);// add special zone in maze
		
	
		
		
		createDoorsintheMaze(spawn, lines, 3);
		history b = tools.getBlock(spawn);
		b.y += 2;
		b.x += 2;
		W.getBlockAt(new Location(W, b.x,BaseY + sizey + 10, b.y)).setType(Material.GLOWSTONE);;
		
		
		for (PLAYER_ZONE zone : zonePlayerCoo) {
			zone.BaseY = BaseY;
			Random rd = new Random(System.nanoTime());
			int seed = rd.nextInt();
			W = zone.generateSpace(W, seed );
		}
		
		
		
		
		
		W = drawMaze(W, lines,sizey);
		
		
		
		
		
		
		
		Bukkit.getWorlds().add(W);
		
		W.save();
		
		
		
		
		
		
		
		return W;
		
		
		
	}
	
	public void createDoorsintheMaze(history center,ArrayList<line> colums,int numOfDistanceWithDoor) {
		
		pathFinding = new pathfinding();
		
		colums = pathFinding.createDistanceCenter(center, colums);
		
		
		System.out.println("Creating Door");
		
		doorsGlobal = new ArrayList<cell>();
		
		int maxDistance = getMaxDistance(colums);
		
		int distanceBetweenDoors = (int) Math.floor(maxDistance / numOfDistanceWithDoor);
		
		
		for (int i = 1; i <= numOfDistanceWithDoor; i++) {
			
			int dist = distanceBetweenDoors * i;
			
			ArrayList<cell> door =  createDoorCellDistance(dist,colums);
			
			Stream<cell> st =  door.stream();
			st.forEach(c -> doorsGlobal.add(c));
			
			
		}
		
		
		
		System.out.println("Door are Created");
		
	}
	
	
	private int getMaxDistance(ArrayList<line> colums) {
	
		int max = 0;
		
		for (line line : colums) {
			for (cell c : line.data) {
				if(c.distance >  max) {
					max = c.distance;
				}
			}
		}
		return max;
	}

	private ArrayList<cell> createDoorCellDistance(int distance,ArrayList<line> maze) {
		
		ArrayList<cell> beReturn = new ArrayList<cell>();
		
		for (line line : maze) {
			for (cell c : line.data) {
				if(c.distance == distance) {
					
					cell left = tools.getCellLeft(maze, c.location);
					cell up = tools.getCellUp(maze, c.location);
					cell right =  tools.getCellRight(maze, c.location);
					cell down =  tools.getCellDown(maze, c.location);
					
					
					
					if(left !=null) {
						if(reduceDistance(c.distance, left.distance) && left.right) {
							beReturn.add(createDoor(left, 1));
						}
					}
					
					if(up !=null) {
						if(reduceDistance(c.distance, up.distance) && up.down) {
							beReturn.add(createDoor(up, 2));
						}
					}
					
					if(right !=null) {
						if(reduceDistance(c.distance, right.distance) && c.right) {
							beReturn.add(createDoor(c, 1));						
						}
					}
					
					if(down != null ) {
						if(reduceDistance(c.distance, down.distance) && c.down) {
							beReturn.add(createDoor(c, 2));
						}	
					}
					
					
				}
			}
		}
		
		return beReturn;
		}
	
	private cell createDoor(cell c0,int direction){
		
		c0.doorOrientation += direction;
		return c0;
		
	}
	
	
	private Boolean reduceDistance(int c0,int c1) {
		if(c1 < c0) {
			return true;
		}
		else {
			return false;
		}
	}
	private Boolean passWall(cell c0,int direction,ArrayList<line> colums) {
		if(direction == 1) {
			return c0.right;
		}
		if(direction == 2) {
			return c0.down;
		}
		if(direction == 3) {
			cell left = tools.getCellLeft(colums, c0.location);
			return left.right;
		}
		if(direction == 4) {
			cell up = tools.getCellUp(colums, c0.location);
			return up.down;
		}
		return true;
	}
	
	
	
	private history createAllZones(ArrayList<line> lines, int sizex, int sizey,int sideSizeZone,int numZone) {
		
		history spawn = null;
		
		ArrayList<line> lns = lines;
		
		Random rd = new Random();		
		
		int ZoneCreated = 0;
		
		Boolean spawnCreated = false;
		
		
		while(ZoneCreated < numZone) {
			
			int x = rd.nextInt(sizex);
			int y = rd.nextInt(sizey);
			
		
			
			if(zoneCanbeCreate(lns,x,y,sideSizeZone)) {
				if(spawnCreated) {
					zoneCreate(lns,x,y,sideSizeZone,ZONE_TYPE.LIVING);		
					
				}
				else {
					spawnCreated = true;
					spawn = zoneCreate(lns,x,y,sideSizeZone,ZONE_TYPE.SPAWN);
				}
				
				ZoneCreated++;
			}
			else {
				Bukkit.getLogger().info("§cCan't create with :"+String.valueOf(x)+"/"+String.valueOf(y));
			}
		
		
		}
		
		
		
		return spawn ;
		
		
	}
	
	
	private history zoneCreate(ArrayList<line> lines, int zx, int zy, int sideSizeZone,ZONE_TYPE type) {
		
		PLAYER_ZONE zone = new PLAYER_ZONE();
		history spawn = null;
		
		for (int y = 0; y < sideSizeZone; y++) {
			int iy = y + zy;
			line l = lines.get(iy);
			
			for (int x = 0; x < sideSizeZone; x++) {
				
				
				
					
					
				int ix = x + zx;
				
				cell c = l.data.get(ix);
				
				
				c.isOpen = true;
				c.type = type;
				zone.cells.add(c);
				
				
				
				if(x == sideSizeZone - 1) {
					
					c.right = true;
					c.downright = false;

					
					
					
				}
				else {
					
					if(y == sideSizeZone - 1) {
						
						c.down = true;
						c.downright = false;
						
					}
					
					else {
						
						c.right = true;
						c.downright = true;
					
						
						c.down = true;
						c.downright = true;
					}
				}
				
			}
			
		}
		
		line l = lines.get(zy + sideSizeZone - 1);
		cell cLastOne =l.data.get(zx + sideSizeZone - 1);
		
		cLastOne.down = false;
		cLastOne.right = false;
		
		
		// form a square
		
		
		
		Random rd  = new Random();
		int num = rd.nextInt(sideSizeZone-2) + 1 ;
		
		line ligne = lines.get(zy+(sideSizeZone-1));
		cell door = ligne.data.get(zx + num);
		
		
		
		
		System.out.println("ligne:"+String.valueOf(zy+num));
		System.out.println("case:"+String.valueOf(zx + (sideSizeZone-1)));
		System.out.println("num:"+String.valueOf(num));
		
		
		door.right = true;
		door.downright = false;
		addDoor(door, zx + num, zy + sideSizeZone-1,1); // 0 because on the right
		if(type == ZONE_TYPE.SPAWN) {
			
			
			spawn = new history(door.location.y+1, door.location.x); // need to inverse y and x
		}
		
		
		
		
		
		System.out.println(zy+"/"+zx);
		history h = new history(zy, zx);
		
		
		
		zone.starter = h;
		
		
		zonePlayerCoo.add(zone);
		
		
		return spawn;
		
	}
	
	
	
	public void addDoor(cell door,int x , int y,int orientation) {
		
		door.doorState = 0 ; // door is close
		door.doorOrientation = orientation ;
		doorsZones.add(door) ;
		
		
	}
	

	private boolean cellIsNormal(cell c) {
		return c.type == ZONE_TYPE.TRADITIONAL ;
	}
	
	private boolean zoneCanbeCreate(ArrayList<line> lines, int zx, int zy, int sideSizeZone) {
		
		if(zy+ sideSizeZone + 1 > lines.size() ||  zy < 2 ) {
			return false;
		}
		if(zx+sideSizeZone + 1 > lines.get(zx).data.size() ||  zx < 2 ) {
			return false;
		}
		
		
		for (int x = 0; x < sideSizeZone; x++) {
			for (int y = 0; y < sideSizeZone; y++) {
				
				int ix = x + zx;
				int iy = y + zy;
				
				if(ix >= lines.size() || iy < 0) {
					return false;
				}
				
				line l = lines.get(iy);
				
				if(l == null) {
					return false;
				}
				if(l.data == null) {
					return false;
				}
				if(ix >= l.data.size() || ix < 0) {
					return false;
				}
				
				cell c =l.data.get(ix);
				
				if(c == null) {
					return false;
				}
				
				if(!cellIsNormal(c)) {
					return false;
				}
				
				
			}
		}
		return true;
		
	}

	private ArrayList<line> AddSpecialCell(ArrayList<line> lines, int sizex, int sizey) {
		
		
		ArrayList<line> lns = lines;
		
			
		
		
		
		
		
		
		return lns;
	}

	
	
	public int getDirection() {
		int dir;
		Random rd =new Random();
		dir = rd.nextInt(4) + 1;
		return dir;
		
	}
	
	
	
	public ArrayList<line> DepthFirst(ArrayList<line> lines,int sizex,int sizey,history head) {
		
		// lines is colum
		ArrayList<line> ln = lines;
		
		
		int headX=	head.x;
		int headY=  head.y;
		
		ArrayList<history> historique = new ArrayList<history>();
		
		
		
		historique.add(head);
		
		
		
		while (historique.size() != 0) {
			
			
			history lastTime = historique.get(historique.size()-1);
			headX = lastTime.x;
			headY = lastTime.y;
			
			
			history up = new history(headX, headY - 1);
			history down = new history(headX, headY + 1);
			history left = new history(headX - 1, headY );
			history right = new history(headX + 1, headY );
			
			cell c = tools.getCell(ln, headX, headY);
			
			int direction = getDirection();
			boolean find = false;
			
			
			
			for (int i = 0; i < 4; i++) {
				
				
				
				
				if(direction == 1) {
					cell haut = tools.getCell(ln, up);
					if(haut != null) {
						
					
						if(!haut.isOpen) {
							haut.down = true;
							haut.isOpen = true;
							find = true;
						}
					}
					
				}
				if(direction == 2) {
					cell bas = tools.getCell(ln, down);
					if(bas != null) {
						
					
						if(!bas.isOpen) {
							c.down = true;
							bas.isOpen = true;
							find = true;
						}
					}
				}
				if(direction == 3) {
					cell droite = tools.getCell(ln, right);
					if(droite != null) {
						
					
						if(!droite.isOpen) {
							c.right = true;
							droite.isOpen = true;
							find = true;
						}
					}
				}
				if(direction == 4) {
					cell gauche = tools.getCell(ln, left);
					if(gauche != null) {
						
					
						if(!gauche.isOpen) {
							gauche.right = true;
							gauche.isOpen = true;
							find = true;
						}
					}
				}
				
				
				
				if(find) {
					break;
				}
				
				
				
				direction++;
				
				if(direction == 5) {
					direction = 1;	
				}
				
			}
			
			if(find) {			
			//System.out.println("trouvé");
				if(direction == 1) {
					historique.add(up);
				}
				if(direction == 2) {
					historique.add(down);
				}
				if(direction == 3) {
					historique.add(right);
				}
				if(direction == 4) {
					historique.add(left);
				}
			
			
			}else {
				//System.out.println("raté: " + headX + "/"+headY);
				historique.remove(historique.size()-1);
			}
			


			
			
			
			
			
			
		}
		
		
		
		
	
		
		
		return ln;
		
		
	}
	
	
	private World addBlock(World world,int x,int y,int z,Material mat) {
		
		World W = world;
		
		Block b = W.getBlockAt(x, y, z);
		b.setType(mat);
		
		
		
		return W;
	}
	
	public static Material getMat() {
		
		Random rd = new Random();
		
		int num = rd.nextInt(7);
		
		if(num == 0) {
			return Material.MOSSY_STONE_BRICKS;
		}
		if(num == 1) {
			return Material.MOSSY_COBBLESTONE;
		}
		if(num == 2 || num == 4) {
			return Material.CRACKED_STONE_BRICKS;
		}
		if(num == 3) {
			return Material.CHISELED_STONE_BRICKS;
		}
		
		
		
		return Material.STONE_BRICKS;
	}
	
	
	
	// create the labyrinthe in blocks
	
	private World drawMaze(World world,ArrayList<line> map,int sizey) {
		
		Logger log = Bukkit.getLogger();
		log.info("Draw Maze");
		World w = world;
		
		for (int i = 0; i < map.size(); i++) {
			log.info(String.valueOf(i));
			
			line lc = map.get(i);
			for (int j = 0; j < lc.data.size(); j++) {	
				
				
				cell c = lc.data.get(j);
				
				
				// 2d
				
				drawCell(c,i,j,sizey,w);
				
				
				
			}
			
		}
			line nl = map.get(1);
			
			for (int x = -5; x < nl.data.size() * 10 - 5; x++) { // +2 car on entoure le labyrinthe
				for (int y = 0; y < sizey; y++) {
					w = addBlock(w, x, y+BaseY, -5 , getMat());
					w = addBlock(w, x, y+BaseY,  map.size()*10, getMat());
				}
			}
			for (int z = -5; z <  map.size() *10 - 5; z++) { // +2 car on entoure le labyrinthe
				for (int y = 0; y < sizey ; y++) {
					w = addBlock(w, -5, y+BaseY, z , getMat());
					w = addBlock(w, nl.data.size() *10, y+BaseY, z, getMat());
				}
			}
			log.info("create floor");
			
			
			
			/// Modification 22:16 28/01/2020
			
			for (int x = -5; x < nl.data.size() * 10 - 5; x++) {
				for (int z = -5; z <  map.size() *10 - 5; z++) {
					
					history currentCel = tools.getCell(new history(x, z));
					
					
					boolean playerZone = false;
					
					for (PLAYER_ZONE zone : zonePlayerCoo) {
						
						int size = (int) Math.sqrt(zone.cells.size());
					
						int maxX = zone.starter.x + size - 1;
						int maxY = zone.starter.y + size - 1;
			/*			System.out.println(String.valueOf("--------------zone--------------"));					
						System.out.println(String.valueOf(maxX)+"/"+String.valueOf(maxY));
						System.out.println(String.valueOf("--------------cell--------------"));					
						System.out.println(String.valueOf(currentCel.x)+"/"+String.valueOf(currentCel.y)); */
						
						if( maxX >= currentCel.x && currentCel.x >= zone.starter.x && maxY >= currentCel.y && currentCel.y >= zone.starter.y ) {
							
							// même cellule
							playerZone = true;
							
						}
						
					}
					
					if(!playerZone) {
						w = addBlock(w, x, BaseY-1, z , getMat());
					}
					
					
					
					
				}
			}
			
			
			
			
			log.info("create the ceiling");
			
			
			for (int x = -5; x < nl.data.size() * 10 - 5;x++) {
				for (int z = -5; z <  map.size() *10 - 5; z++) {
					Location loc = new Location(w, x+1, BaseY + sizey , z+1);
					Block b = w.getBlockAt(loc);
					b.setType(Material.BARRIER);
				}
			}
			
			
			
			
			 
		
		
		
		
		
		return w;
	}
	
	
	
	
	public World drawCell(cell c,int cx,int cy,int sizey,World w) {
		
		int indexX = cx*10;
		int indexZ = cy*10;
		
		for (int k = 0; k < sizey; k++) {
			
			//3d car remonte le long de y == k
			
			
			
			
			

			if(!c.down) {
				
				w = addBlock(w, indexX, k + BaseY, indexZ+5, getMat());
				w = addBlock(w, indexX+1, k + BaseY, indexZ+5, getMat());
				w = addBlock(w, indexX+2, k + BaseY, indexZ+5, getMat());
				w = addBlock(w, indexX+3, k + BaseY, indexZ+5, getMat());
				w = addBlock(w, indexX+4, k + BaseY, indexZ+5, getMat());
				
				w = addBlock(w, indexX-1, k + BaseY, indexZ+5, getMat());
				w = addBlock(w, indexX-2, k + BaseY, indexZ+5, getMat());
				w = addBlock(w, indexX-3, k + BaseY, indexZ+5, getMat());
				w = addBlock(w, indexX-4, k + BaseY, indexZ+5, getMat());
			}
			if(!c.right) {
				w = addBlock(w, indexX+5, k + BaseY, indexZ, getMat());
				w = addBlock(w, indexX+5, k + BaseY, indexZ+1, getMat());
				w = addBlock(w, indexX+5, k + BaseY, indexZ+2, getMat());
				w = addBlock(w, indexX+5, k + BaseY, indexZ+3, getMat());
				w = addBlock(w, indexX+5, k + BaseY, indexZ+4, getMat());
				w = addBlock(w, indexX+5, k + BaseY, indexZ-1, getMat());
				w = addBlock(w, indexX+5, k + BaseY, indexZ-2, getMat());
				w = addBlock(w, indexX+5, k + BaseY, indexZ-3, getMat());
				w = addBlock(w, indexX+5, k + BaseY, indexZ-4, getMat());
			}
			if(!c.downright) {
				w = addBlock(w, indexX+5, k + BaseY, indexZ+5, getMat());
			}
			DrawDoor(c,sizey,w);
			
			
		}
		return w;
		
	}
	
	
	private  void DrawDoor(cell c, int sizey,World w) {
		
		if(c.doorOrientation == 0) {return;} // Cell without door
		
		int down = 0;
		int right = 0;
		history loc = tools.getBlock(c.location);
		
		
		if(c.doorOrientation == 1 ) {
			right = 1;
		}
		else if(c.doorOrientation == 2) {
			down = 1;
		}
		
		
		
		for (int i = 0; i < sizey; i++) {
			int y = i + BaseY;
			
			if(c.doorState == 0) {
				addBlock(w, loc.x+(9*right), y, loc.y + (9*down), Material.STONE_BRICKS);
				addBlock(w, loc.x+(9*right)+(0*down), y, loc.y+(0*right) + (9*down),Material.SPRUCE_WOOD);
				
				addBlock(w, loc.x+(9*right)+(1*down), y, loc.y+(1*right) + (9*down), Material.DARK_OAK_PLANKS);
				addBlock(w, loc.x+(9*right)+(2*down), y, loc.y+(2*right) + (9*down), Material.DARK_OAK_PLANKS);
				addBlock(w, loc.x+(9*right)+(3*down), y, loc.y+(3*right) + (9*down), Material.DARK_OAK_PLANKS);
				
				addBlock(w, loc.x+(9*right)+(4*down), y, loc.y+(4*right) + (9*down), Material.SPRUCE_WOOD);
				
				addBlock(w, loc.x+(9*right)+(5*down), y, loc.y+(5*right) + (9*down), Material.DARK_OAK_PLANKS);
				addBlock(w, loc.x+(9*right)+(6*down), y, loc.y+(6*right) + (9*down), Material.DARK_OAK_PLANKS);
				addBlock(w, loc.x+(9*right)+(7*down), y, loc.y+(7*right) + (9*down), Material.DARK_OAK_PLANKS);
				
				addBlock(w, loc.x+(9*right)+(8*down), y, loc.y+(8*right) + (9*down),Material.SPRUCE_WOOD);
				addBlock(w, loc.x+(9*right)+(9*down), y, loc.y+(9*right) + (9*down), Material.STONE_BRICKS);
				
			}
			
			if(c.doorState == 1) {
				addBlock(w, loc.x+(9*right), y, loc.y + (9*down), Material.STONE_BRICKS);
				addBlock(w, loc.x+(9*right)+(0*down), y, loc.y+(0*right) + (9*down),Material.DARK_OAK_PLANKS);
				addBlock(w, loc.x+(9*right)+(1*down), y, loc.y+(1*right) + (9*down), Material.DARK_OAK_PLANKS);
				addBlock(w, loc.x+(9*right)+(2*down), y, loc.y+(2*right) + (9*down), Material.DARK_OAK_PLANKS);
				addBlock(w, loc.x+(9*right)+(3*down), y, loc.y+(3*right) + (9*down), Material.SPRUCE_WOOD);
				
				addBlock(w, loc.x+(9*right)+(4*down), y, loc.y+(4*right) + (9*down), Material.AIR);
				
				addBlock(w, loc.x+(9*right)+(5*down), y, loc.y+(5*right) + (9*down), Material.SPRUCE_WOOD);
				addBlock(w, loc.x+(9*right)+(6*down), y, loc.y+(6*right) + (9*down), Material.DARK_OAK_PLANKS);
				addBlock(w, loc.x+(9*right)+(7*down), y, loc.y+(7*right) + (9*down), Material.DARK_OAK_PLANKS);
				
				addBlock(w, loc.x+(9*right)+(8*down), y, loc.y+(8*right) + (9*down),Material.DARK_OAK_PLANKS);
				addBlock(w, loc.x+(9*right)+(9*down), y, loc.y+(9*right) + (9*down), Material.STONE_BRICKS);
				
			}
			
			if(c.doorState == 2) {
				addBlock(w, loc.x+(9*right), y, loc.y + (9*down), Material.STONE_BRICKS);
				addBlock(w, loc.x+(9*right)+(0*down), y, loc.y+(0*right) + (9*down),Material.DARK_OAK_PLANKS);
				addBlock(w, loc.x+(9*right)+(1*down), y, loc.y+(1*right) + (9*down), Material.DARK_OAK_PLANKS);
				addBlock(w, loc.x+(9*right)+(2*down), y, loc.y+(2*right) + (9*down), Material.SPRUCE_WOOD);
				addBlock(w, loc.x+(9*right)+(3*down), y, loc.y+(3*right) + (9*down), Material.AIR);
				
				addBlock(w, loc.x+(9*right)+(4*down), y, loc.y+(4*right) + (9*down), Material.AIR);
				
				addBlock(w, loc.x+(9*right)+(5*down), y, loc.y+(5*right) + (9*down), Material.AIR);
				addBlock(w, loc.x+(9*right)+(6*down), y, loc.y+(6*right) + (9*down), Material.SPRUCE_WOOD);
				addBlock(w, loc.x+(9*right)+(7*down), y, loc.y+(7*right) + (9*down), Material.DARK_OAK_PLANKS);
				
				addBlock(w, loc.x+(9*right)+(8*down), y, loc.y+(8*right) + (9*down),Material.DARK_OAK_PLANKS);
				addBlock(w, loc.x+(9*right)+(9*down), y, loc.y+(9*right) + (9*down), Material.STONE_BRICKS);
				
			}
			if(c.doorState == 3) {
				addBlock(w, loc.x+(9*right), y, loc.y + (9*down), Material.STONE_BRICKS);
				addBlock(w, loc.x+(9*right)+(0*down), y, loc.y+(0*right) + (9*down),Material.SPRUCE_WOOD);
				addBlock(w, loc.x+(9*right)+(1*down), y, loc.y+(1*right) + (9*down), Material.AIR);
				addBlock(w, loc.x+(9*right)+(2*down), y, loc.y+(2*right) + (9*down), Material.AIR);
				addBlock(w, loc.x+(9*right)+(3*down), y, loc.y+(3*right) + (9*down), Material.AIR);
				
				addBlock(w, loc.x+(9*right)+(4*down), y, loc.y+(4*right) + (9*down), Material.AIR);
				
				addBlock(w, loc.x+(9*right)+(5*down), y, loc.y+(5*right) + (9*down), Material.AIR);
				addBlock(w, loc.x+(9*right)+(6*down), y, loc.y+(6*right) + (9*down), Material.AIR);
				addBlock(w, loc.x+(9*right)+(7*down), y, loc.y+(7*right) + (9*down), Material.AIR);
				
				addBlock(w, loc.x+(9*right)+(8*down), y, loc.y+(8*right) + (9*down),Material.SPRUCE_WOOD);
				addBlock(w, loc.x+(9*right)+(9*down), y, loc.y+(9*right) + (9*down), Material.STONE_BRICKS);
				
			}
			
			
			
			
			
			
			
			
			
		}
		
		
		
	}
	

	public World getMaze() {
		
		
		return Bukkit.getServer().getWorld(mazeName);
		
	}
	
	public boolean mazeExist() {
		
		File f = new File(mazeName);
		return f.exists();
		
		
	}

}

