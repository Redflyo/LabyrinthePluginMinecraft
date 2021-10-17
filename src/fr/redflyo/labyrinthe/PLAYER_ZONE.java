package fr.redflyo.labyrinthe;



import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Sapling;

import fr.redflyo.obj.cell;
import fr.redflyo.obj.history;
import fr.redflyo.obj.tools;



public class PLAYER_ZONE {
	
	float coefAmpli = 40;
	int[][] levels;
	int waterLevel = 3;
	int minTreeLevel = waterLevel + 1;
	int maxTreeLevel = waterLevel + 8;
	
	int minGrassLevel = waterLevel;
	int maxGrassLevel = waterLevel + 20;
	
	int minFlowerLevel = waterLevel;
	int maxFlowerLevel = waterLevel + 4;
	
	int numberOfTree = 50;
	
	int numberOfTypeFlower = 3;
	
	
	public PLAYER_ZONE() {
		cells = new ArrayList<cell>();
		
		
	}
	
	
	public void generateToDown(World world,int x,int y) {
		for (int i = BaseY-1; i > 0 ; i--) {
			Location loc1 = new Location(world, x, i, y);						
			Block b1 = world.getBlockAt(loc1);
			b1.setType(GenerateLabyrinthe.getMat());
		}
	}
	
	
	
	private int getHauteur(float data) {
		
		return  (int)(Math.floor((1-data*data)*coefAmpli));
		
	}
	
	
	
	public int BaseY;
	public  ArrayList<cell> cells;
	public history starter;
	
	
	
	
	
	public World generateSpace(World world,int seed) {
		
		
		int side = (int) Math.sqrt((double)cells.size());
		
		ArrayList<Location> levels = new ArrayList<Location>();
		
		
		
		

		history startB = tools.getBlock(starter);
		int Ymin = 1000;
		
		
		
		for (int xc = 0; xc < side; xc++) {
			for (int yc = 0; yc < side; yc++) {
				
				int indexX = xc;
				int indexZ = yc;
				
				history index = new history(indexX, indexZ); // get cellule
				history bloc = tools.getBlock(index); // get starter bloc of this cellule
				
				
				

		
				float[][] map = PerlinNoise.createPerlinNoise(11, 11, seed, 1, 0.25f, 4, 0.5f,xc*11,yc*11);
				float[][] mapbis1 = PerlinNoise.createPerlinNoise(11, 11, seed+10, 1, 0.25f, 4, 0.5f,xc*11,yc*11);
				float[][] mapbis2 = PerlinNoise.createPerlinNoise(11, 11, seed+50, 1, 0.25f, 4, 0.5f,xc*11,yc*11);
				map = PerlinNoise.addPerlinNoise(map, mapbis1,mapbis2);
				
				
				for (int xb = 0; xb < 11; xb++) { // 11 the size of a cell
					
					
					
				
					for (int yb = 0; yb < 11; yb++) {
				
						
						boolean border = false;
						
						int x = bloc.x + xb + startB.x + 3;
						int y = bloc.y + yb + startB.y + 3;
						
						
						if(xc == side-1) {
							if(xb == 10) {
								generateToDown(world, x, y);
								border = true;
								
							}
						}
						if(xc == 0) {
							if(xb == 0) {
								generateToDown(world, x, y);
								border = true;
							}
						}
						if(yc == 0) {
							if(yb == 0) {
								generateToDown(world, x, y);
								border = true;
							}
						}
						if(yc == side-1) {
							if(yb == 10) {
								generateToDown(world, x, y);
								border = true;
							}
						}
						
						
						
						if(!border) {
							
						
						
						float z = map[xb][yb];
						
						int trueY = getHauteur(z);
						
						
						
						
						if(Ymin > BaseY-trueY+1) {
							Ymin = BaseY-trueY+1;
						}
						
						
						
						Location loc1 = new Location(world, x, BaseY-trueY, y);						
						Block b1 = world.getBlockAt(loc1);
						levels.add(loc1);
						b1.setType(Material.GRASS_BLOCK);
						
						b1.setBiome(Biome.DARK_FOREST);
						
						
						Location loc2 = new Location(world, x, BaseY-trueY-1, y);						
						Block b2 = world.getBlockAt(loc2);
						b2.setType(Material.DIRT);
						}
						else {
							if(xb == 10 && yb == 10 && xc == side -1 && yc == side - 1) {
								
	
								
								
								for (int x1 = startB.x; x1 < x; x1++) {
									for (int y1 = startB.y; y1 < y; y1++) {
										
										for (int hauteur = 0; hauteur < 255; hauteur++) {
										
											if(hauteur == 0) {
												Location loc1 = new Location(world, x1, hauteur, y1);						
												Block b1 = world.getBlockAt(loc1);
												b1.setType(Material.BEDROCK);
											}
											else {
												
												
												
												Location loc1 = new Location(world, x1, hauteur, y1);						
												Block b1 = world.getBlockAt(loc1);
												
												if(b1.getType().equals(Material.DIRT)) {
													break;
												}
												else {
													b1.setType(Material.STONE);
												}
												
											}
											
											
										}
										
										
										
										
										
									}
								}
								
								
								
							}
						}
						
						
						
					}
				}
				
				
				
				
				
				
				
			}
		}
		
		
		
		
		
		world = setEnvironnementLevel(world,levels,Ymin,seed);
		
		
		return world;
	}
	
	
	public World setEnvironnementLevel(World world,ArrayList<Location> locsWater,int Ymin,int seed) {
		
		
	
		int minForWater = Ymin + waterLevel;
		
		int realMinTreeLeevel = Ymin + minTreeLevel;
		int realMaxTreeLeevel = Ymin + maxTreeLevel;
		
		int realMinGrassLeevel = Ymin + minGrassLevel;
		int realMaxGrassLeevel = Ymin + maxGrassLevel;
		
		int realMinFlowerLeevel = Ymin + minFlowerLevel;
		int realMaxFlowerLeevel = Ymin + maxFlowerLevel;
		
		ArrayList<Location> locSappling = new ArrayList<Location>();
		ArrayList<Location> locGrass = new ArrayList<Location>();
		ArrayList<Location> locFlower = new ArrayList<Location>();
		
		for (Location loc : locsWater) {
			
			
			int LY = loc.getBlockY();
			
			if(LY <= minForWater) {
				
				
			Location nloc = new Location(world, loc.getBlockX(), LY + 1, loc.getBlockZ());
			
							
				for (int y = nloc.getBlockY() ; y <= minForWater; y++) {
					
					Block b1 = world.getBlockAt(nloc);					
					b1.setType(Material.WATER);
					
				}
				
			}
			
			
			if(realMinTreeLeevel <= LY && LY <= realMaxTreeLeevel ) {
				
				Location nloc = new Location(world, loc.getBlockX(), LY + 1, loc.getBlockZ());
				
				locSappling.add(nloc);				
				
			}
			
			if(realMinGrassLeevel <= LY && LY <= realMaxGrassLeevel ) {
				
				Location nloc = new Location(world, loc.getBlockX(), LY + 1, loc.getBlockZ());
				
				locGrass.add(nloc);				
				
			}
			
			
			if(realMinFlowerLeevel <= LY && LY <= realMaxFlowerLeevel ) {
				
				Location nloc = new Location(world, loc.getBlockX(), LY + 1, loc.getBlockZ());
				
				locFlower.add(nloc);				
				
			}
			
			
			
			
			
			
			
		}
		
		
		int cptTree = 0;
		
		Random rd = new Random(seed);
		
		while (cptTree < numberOfTree && locSappling.size() != 0) { // for tree
			
			int index = rd.nextInt(locSappling.size());
			
			
			Location loc = locSappling.get(index);
			
			plantTree(loc);
			cptTree++;
			
			locSappling.remove(index);
			
			
			
			
		}
		
		for (Location loc : locGrass) { // for grass
			
			
			
			
			Block b = loc.getBlock();
			
			if(b.getType().equals(Material.AIR)) {
				
				int number = rd.nextInt(3);
				
				if(number == 1) {
					b.setType(Material.GRASS);
				}
				
				
			}
			
			
			
		}
		
		for (Location loc : locFlower) { // For flower
			
			
			
			
			Block b = loc.getBlock();
			
			if(b.getType().equals(Material.AIR)) {
				
				int number = rd.nextInt(10);
				
				if(number == 1) {
					
					int typeFlower = rd.nextInt(numberOfTypeFlower);
					b.setType(getFlower(typeFlower));
					
				}
				
				
			}
			
			
			
		}
		
		
				
				
				
		
		
		return world;
	}
	
	
	// number random between [0,7] else return null
	public static Material getFlower(int number) {
		
		if(number == 0 ) {
			return Material.SUNFLOWER;
		}

		if(number == 1 ) {
			return Material.POPPY;
		}


		if(number == 2 ) {
			return Material.DANDELION;
		}


		return null;
		
	}
	
	
	public static void plantTree(Location loc) {
		Block b = loc.getBlock();
		b.setType(Material.OAK_SAPLING);
		Sapling ag = (Sapling) b.getBlockData();
		ag.setStage(ag.getMaximumStage());
		
		b.setBlockData(ag);
		
		
		
		// tree big
	}
	
	
	
}
