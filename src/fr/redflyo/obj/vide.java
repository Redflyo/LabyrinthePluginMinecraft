package fr.redflyo.obj;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.ChunkGenerator;

public class vide extends ChunkGenerator {
	
	@Override
	 public ChunkData generateChunkData(World world, Random random, int x, int z, BiomeGrid biome) {

	        ChunkData chunkData = createChunkData(world);
	       
	        chunkData.setRegion(0, 1, 0, 15, 1, 15, Material.AIR);
	        for(int x1 = 0; x1 < 16; x1++) {
	            for(int z1 = 0; z1 < 16; z1++) {
	            	for (int y = 0; y < 255; y++) {
	            		 biome.setBiome(x1, y ,z1, Biome.DARK_FOREST);
					}
	              
	            }
	        }
	       
	        
	        return chunkData;
	    }
}
