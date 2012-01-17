package cloud;

// COPYRIGHT 2001 KEN PERLIN <pre>
import java.applet.*;
import java.awt.*;
import java.awt.image.*;

/********

GENERAL PURPOSE PIXEL-BY-PIXEL DRAWING APPLET

********/

public class PixApplet extends Applet implements Runnable {
    public int W = 0, H, pix[];
    public Image im;
    public boolean damage = true;
    private MemoryImageSource mis;
    private Thread t;
    int zoom = 1;

// THIS IS THE METHOD THE USER MUST OVERRIDE TO DO ANYTHING USEFUL

    public void setPix(int frame) {
       if (W != bounds().width || H != bounds().height)
	  init();
    }

    public void setPix(int col, int row, int red, int grn, int blu) {
       if (zoom == 1 || col % zoom != 0 || row % zoom != 0)
          pix[xy2i(col, row)] = pack(red, grn, blu);
       else {
	  int i = xy2i(col, row);
	  int rgb = pack(red, grn, blu);
	  for (int y = col ; y < col + zoom ; y++) {
	     for (int x = col ; x < col + zoom ; x++) {
		if (pix[i] == 0)
                   pix[i] = rgb;
		i++;
             }
	     i += W-zoom;
          }
       }
    }

// WHEN FIRST STARTING UP, ALLOCATE FRAME BUFFER MEMORY

    public void init() {
        W = bounds().width;  // MUST INQUIRE AS TO RESOLUTION OF APPLET
        H = bounds().height;
        pix = new int[W*H];
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
       for (int frame = 1 ; true ; frame++) {
	  for (int i = 0 ; i < W*H ; i++)
	     pix[i] = 0;
          setPix(frame);
          if (damage) {
             mis.newPixels(0, 0, W, H, true);
             repaint();
          }
          try {
             Thread.sleep(10);
          }
          catch(InterruptedException ie) { ; }
       }
    }
    public void update(Graphics g) {
        g.drawImage(im, 0, 0, null);
    }

// PUBLIC METHODS TO HANDLE THE PIXEL BUFFER

    // CONVERT X,Y COORDS INTO INDEX WITHIN IMAGE PIXEL BUFFER ARRAY

    public int xy2i(int x, int y) { return y * W + x; }

    // PACK RGB COMPONENTS INTO A PIXEL VALUE

    public int pack(int rgb[]) { return pack(rgb[0],rgb[1],rgb[2]); }

    public int pack(int r, int g, int b) {
       return ((r&255)<<16) | ((g&255)<< 8) | ((b&255)) | 0xff000000;
    }

    // UNPACK RGB COMPONENTS FROM A PIXEL VALUE

    public void unpack(int rgb[], int packed) {
       rgb[0] = (packed >> 16) & 255;
       rgb[1] = (packed >>  8) & 255;
       rgb[2] = (packed      ) & 255;
    }
    public int unpack(int packed, int i) {
       switch (i) {
       case 0 : return (packed >> 16) & 255;
       case 1 : return (packed >>  8) & 255;
       default: return (packed      ) & 255;
       }
    }
}

