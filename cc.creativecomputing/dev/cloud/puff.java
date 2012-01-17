package cloud;

//<pre>
import java.util.*;

public class puff extends PixApplet {
	Random R = new Random(0);
	int blur, mode = 1;
	double P[][];
	double freq = 1. / 20;

	// THINGS TO INITIALIZE WHEN STARTING THE APPLET

	public void init() {
		super.init();
		blur = W / 12;

		// CREATE CLOUD OF PARTICLES

		P = new double[700][3];
		for (int i = 0; i < P.length; i++) {
			double rr = 2;
			do {
				for (int j = 0; j < 3; j++)
					P[i][j] = 2 * (R.nextDouble() % 1) - 1;
				rr = dot(P[i], P[i]);
			} while (rr > 1);
		}
	}

	// DRAW THE PICTURE

	public void setPix(int frame) {
		super.setPix(frame);

		// FIRST SET ALL PIXELS TO BLACK

		for (int x = 0; x < W; x++)
			for (int y = 0; y < H; y++)
				pix[xy2i(x, y)] = 0;

		// THEN DRAW EACH PARTICLE

		for (int i = 0; i < P.length; i++) {
			int x = (int) (W / 2 + P[i][0] * (W / 2 - blur));
			int y = (int) (H / 2 + P[i][1] * (W / 2 - blur));

			// MODE == 0: DISPLAY PARTICLE AS A POINT

			if (mode == 0)
				pix[xy2i(x, y)] = 255;

			// OTHERWISE, DISPLAY PARTICLE AS A BLURRY SPOT

			else
				for (int u = -blur; u < blur; u++)
					for (int v = -blur; v < blur; v++) {
						float un = u / (float)blur;
						float vn = v / (float)blur;
						pix[xy2i(x + u, y + v)] += Math.max(0, blur * blur - u
								* u - v * v);
					}
		}

		// FINALLY, CONVERT CLOUD DENSITY TO PACKED R,G,B VALUES

		for (int x = 0; x < W; x++)
			for (int y = 0; y < H; y++) {
				int c = pix[xy2i(x, y)];
				if (mode == 0)
					setPix(x, y, c, c, c);

				// MODE > 0: DO VARIOUS IMAGE PROCESSING

				else {

					// NORMALIZE THE BLURRY SPOT VALUES

					c = c * 256 / W * 256 / W / 55;
					setPix(x, y, c, c, c);

					if (mode >= 1) {
						if (c == 0)
							setPix(x, y, 0, 0, 0);
						else {
							double t = c / 255.;

							// MODE == 3 OR 4: ADD VARIOUS FREQUENCIES OF NOISE

							if (mode >= 4)
								t += .07 * Math.abs(ImprovedNoise.noise(2 * freq * x, 2 * freq * y, .5));
							if (mode >= 3) {
								t += .14 * Math.abs(ImprovedNoise.noise(freq * x, freq * y, .5));
								t = Math.max(0, Math.min(1, t));
							}

							// MODE > 1: APPLY HIGH CONTRAST FILTER TO CLOUD
							// EDGES

							if (mode >= 2)
								c = (int) (255 * filter(t));

							// BLEND OVER SKY COLOR

							setPix(x, y, c, c, c);
						}
					}
				}
			}
	}

	// HIGH CONTRAST FILTER TO BRING OUT THE CLOUD EDGES

	double filter(double t) {
		t = bias(t, .67);
		if (t < .5)
			t = gain(t, .86);
		t = bias(t, .35);
		return t;
	}

	// ALLOW EXTERNAL APPLICATION TO SET THE MODE

	public void setMode(int mode) {
		this.mode = mode;
		damage = true;
	}

	// DOT PRODUCT

	static double dot(double a[], double b[]) {
		double d = 0;
		for (int i = 0; i < a.length; i++)
			d += a[i] * b[i];
		return d;
	}

	// LINEAR INTERPOLATION

	static double lerp(double t, double a, double b) {
		return a + t * (b - a);
	}

	// BIAS AND GAIN FILTERS (FROM PERLIN '85)

	static final double LOG_HALF = Math.log(0.5);

	static double gain(double a, double b) {
		double p;

		if (a < .001)
			return 0.;
		else if (a > .999)
			return 1;

		b = (b < .001) ? .0001 : (b > .999) ? .999 : b;
		p = Math.log(1. - b) / LOG_HALF;
		if (a < 0.5)
			return Math.pow(2 * a, p) / 2;
		else
			return 1. - Math.pow(2 * (1. - a), p) / 2;
	}

	static double bias(double a, double b) {
		if (a < .001)
			return 0.;
		else if (a > .999)
			return 1.;
		else if (b < .001)
			return 0.;
		else if (b > .999)
			return 1.;
		else
			return Math.pow(a, Math.log(b) / LOG_HALF);
	}
}
