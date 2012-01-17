package cloud;

// COPYRIGHT 2001 KEN PERLIN <pre>
import java.applet.*;
import java.awt.*;
import java.awt.image.*;

/********
 * GENERAL PURPOSE PIXEL-BY-PIXEL DRAWING APPLET
 ********/

public class VPixApplet extends Applet implements Runnable {
	public int W, H, pix[];
	public Image im;
	public boolean damage = true;
	private MemoryImageSource mis;
	private Thread t;

	// THIS IS THE METHOD THE USER MUST OVERRIDE TO DO ANYTHING USEFUL

	public void setPix(int frame) {
	}

	// WHEN FIRST STARTING UP, ALLOCATE FRAME BUFFER MEMORY

	public void init() {
		W = bounds().width; // MUST INQUIRE AS TO RESOLUTION OF APPLET
		H = bounds().height;
		pix = new int[W * H];
		mis = new MemoryImageSource(W, H, pix, 0, W);
		mis.setAnimated(true);
		im = createImage(mis);
	}

	// START/STOP/RUN/UPDATE METHODS FOR THE RENDERING THREAD

	public void start() {
		if (t == null) {
			t = new Thread(this);
			t.start();
		}
	}

	public void stop() {
		if (t != null) {
			t.stop();
			t = null;
		}
	}

	public void run() {
		for (int frame = 1; true; frame++) {
			setPix(frame);
			if (damage) {
				mis.newPixels(0, 0, W, H, true);
				repaint();
				damage = false;
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException ie) {
				;
			}
		}
	}

	public void update(Graphics g) {
		g.drawImage(im, 0, 0, null);
	}

	// METHODS TO HANDLE THE PIXEL BUFFER

	// CONVERT XY COORDS TO INDEX INTO THE IMAGE PIXEL BUFFER ARRAY

	public int xy2i(int x, int y) {
		return y * W + x;
	}

	// PACK RGB VALUES INTO AN IMAGE PIXEL

	public int pack(int rgb[]) {
		return pack(rgb[0], rgb[1], rgb[2]);
	}

	public int pack(int r, int g, int b) {
		return ((r & 255) << 16) | ((g & 255) << 8) | ((b & 255)) | 0xff000000;
	}

	// UNPACK RGB VALUES FROM AN IMAGE PIXEL

	public int unpack(int packed, int i) {
		return packed >> 16 - 8 * i & 255;
	}

	public void unpack(int rgb[], int packed) {
		rgb[0] = (packed >> 16) & 255;
		rgb[1] = (packed >> 8) & 255;
		rgb[2] = (packed) & 255;
	}
}
