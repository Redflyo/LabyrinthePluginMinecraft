package fr.redflyo.labyrinthe;

import java.util.Random;

import fr.redflyo.obj.Vector2;

public class PerlinNoise
{
    
    private static float Interpolate(float x0, float x1, float alpha)
    {
        float c = (float) ((1 - Math.cos(alpha * Math.PI)) * 0.5);
        return x0 * (1 - c) + c * x1;
    }


    float[][][] GetEmptyArray(int sx, int sy)
    {
        float[][][] array = new float[sx][][];
        for (int i = 0; i < array.length; i++)
        {
            array[i] = new float[sy][];
            for (int j = 0; j < array[i].length; j++)
            {
                array[i][j] = new float[2];
            }
        }
        return array;
    }





    public static float[][] createPerlinNoise(int width,int height,int seed,float scale,float lacunarity,int octaves,float persistence,int offSetX,int offSetY)
    {

        float[][] noise = new float[width][];

        for (int i = 0; i < width; i++)
        {
            noise[i] = new float[height];
            for (int j = 0; j < height; j++)
            {


                float amplitude = 1 ;
                float frequence = 1 ;
                float value = 0f;

                for (int k = 0; k < octaves; k++)
                {


                    float sx = (i + offSetX) / scale * frequence;
                    float sy = (j + offSetY) / scale * frequence;


                    
                    value = getNoise(sx, sy, seed);

                    value += value * amplitude;





                    amplitude = amplitude * persistence;
                    frequence = frequence * lacunarity;

                    

                }
                noise[i][j] = value;
            }
        }



        return noise;

    }
    
    
    public static float[][] addPerlinNoise(float[][] p1,float[][] p2,float[][] p3){
    	
    	float[][] np = new float[p1.length][];
    	
    	
    	for (int i = 0; i < p1.length; i++) {
    		
    		np[i] = new float[p1[1].length];
    		
    		for (int j = 0; j < p1[i].length; j++) {
    			
    			np[i][j] = ((p1[i][j] * 1 + p2[i][j] * 0.5f + p3[i][j] * 0.25f ) / 1.75f);
    			
    		}
		}
    	
    	return np;
    }



    public static float getNoise(float x, float z, int seed)
    {
        int xmin = (int)x; //  / octave;
        int xmax = (int)xmin + 1;
        int zmin = (int)z; //  / octave;
        int zmax = (int)zmin + 1;

         Vector2 a = new Vector2(xmin, zmin);
        Vector2 b = new Vector2(xmax, zmin);
        Vector2 c = new Vector2(xmax, zmax);
        Vector2 d = new Vector2(xmin, zmax);

        float ra = (float)noise(a, seed);
        float rb = (float)noise(b, seed);
        float rc = (float)noise(c, seed);
        float rd = (float)noise(d, seed);

        float t = x - (int)x;// x / octave - (int)x / octave;
        float t2 = z - (int)z;//z / octave - (int)z / octave;

        float i1 = Interpolate(ra, rb, t);
        float i2 = Interpolate(rd, rc, t);


        float h = Interpolate(i1, i2, t2);

        return h;
    }

    private static double noise(Vector2 coord, int seed)
    {
        double var = 10000.0 * (Math.sin(coord.x + Math.cos(coord.y)) + Math.tan(seed));
        Random random = new Random((int)Math.floor(var));
        double ret = random.nextDouble();

        return ret;
    }

    Random rd;
    public PerlinNoise()
    {
        rd = new Random();
    }







    public float[][][] GenerateWhiteNoise(int width, int height, int seed)
    {
        Random random = new Random(seed); //Seed to 0 for testing
        float[][][] noise = GetEmptyArray(width, height);

        for (int i = 0; i < width; i++)
        {

            for (int j = 0; j < height; j++)
            {

                noise[i][j][0] = (((float)random.nextDouble() % 1) * 2) - 1;
                noise[i][j][1] = (((float)random.nextDouble() % 1) * 2) - 1;

            }
        }

        return noise;
    }
    float dotGridGradient(int ix, int iy, float x, float y,float[][][] datas)
    {

// Precomputed (or otherwise) gradient vectors at each grid node
    

// Compute the distance vector
    float dx = x - (float)ix;
    float dy = y - (float)iy;

// Compute the dot-product
    return (dx* datas[ix][iy][0] + dy* datas[ix][iy] [1]);
    }




    public float[][] GenerateSmoothNoise(int width, int height, int seed)
    {
        float[][][] datas = GenerateWhiteNoise(width, height, seed);

        float[][] values = new float[width][];


        for (int i = 0; i < width; i++)
        {
            values[i] = new float[height];
            for (int j = 0; j < height; j++)
            {


                

                int x0 = (int)i;
                int x1 = x0 + 1;
                int y0 = (int)i;
                int y1 = y0 + 1;

                if(x0 == width - 1)
                {
                    x1 = x0 - 1;
                }
                if(y0 == height - 1)
                {
                    y1 = y0 - 1;
                }


                float sx = i - (float)x0;
                float sy = j - (float)y0;

                float n0, n1, ix0, ix1, value;
                n0 = dotGridGradient(x0, y0, i, j,datas);
                n1 = dotGridGradient(x1, y0, i, j,datas);
                ix0 = Interpolate(n0, n1, sx);

                n0 = dotGridGradient(x0, y1, i, j, datas);
                n1 = dotGridGradient(x1, y1, i, j, datas);
                ix1 = Interpolate(n0, n1, sx);

                value = Interpolate(ix0, ix1, sy);
                values[i][j] = value;

            }
        }

        

        return values;
    }



     
    }
