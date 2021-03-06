package generation;

import main.Land;

public class WorldGenerator {

	//get in world and change the height
	int xFreq = 5;
	int zFreq = 8;
	float[][] functionResults;
	int prime1 = 15731;
	int prime2 = 789221;
	int prime3 = 1376312589;
	Land[][] world;
	float seed;

	/**
	 * Generates a new height map for the land using a Perlin Noise Algorithm
	 * @param world
	 * @param heightScale
	 */
	public void generate(Land[][] world, int heightScale) {
		// seed for the world
		seed = (float) (System.nanoTime()/10000000);
		this.world = world;
		
		// grid of results for the noise algorithm
		functionResults = new float[(int) (world.length/xFreq)+4][(int) (world[0].length/zFreq)+4]; // adding 4 to create buffer on edges
		int x = 0;
		int z = 0;
		
		// populate the results array
		for(int i = 0; i<functionResults.length; i++){
			for(int j = 0; j<functionResults[0].length; j++){
				functionResults[i][j] = function(x-2*xFreq,z-2*zFreq); // making use of buffer
				z+=zFreq;
			}
			x+=xFreq;
		}
		// interpolate the algorithm results and populate the world
		for(int i = 0; i < world.length; i++){
			for(int j = 0; j<world[0].length; j++){
				world[i][j] = new Land(heightScale*interpolate(i,j),i,j);
			}
		}
	}

	/**
	 * Uses the Perlin Noise Function to calculate the height value
	 * @param x
	 * @param z
	 * @return
	 */
	public float function(int x, int z){
		x = (z^x) << (x)^z;
		x+=seed; // add seed to x for randomization (slight)
		return Math.abs((1.0f - ((x * (x * x * prime1 + prime2) + prime3) & 0x7fffffff) * 0.000000000931322574615478515625f));

	}
/**
 * interpolates data and returns height for given position
 * @param x
 * @param z
 * @return
 */
	public float interpolate(int x, int z){
		int left= (int) (x/xFreq)+1;
		int back = (z/zFreq);
		float[] interps = new float[4];
		for(int i = 0; i < 4; i++){
			interps[i] =  cubicInterpolate(functionResults[left-1][back], functionResults[left][back], functionResults[left+1][back], functionResults[left+2][back], ((float)(x%xFreq))/xFreq);
			back++;
		}
		return cubicInterpolate(interps[0], interps[1], interps[2], interps[3], ((float)(z%zFreq))/zFreq);
	}

	private float cubicInterpolate(float v0, float v1, float v2, float v3, float x) {
		float P = (v3 - v2) - (v0 - v1);
		float Q = (v0 - v1) - P;
		float R = v2 - v0;
		float S = v1;

		return P*x*x*x + Q*x*x + R*x + S;
	}
}
