package cc.creativecomputing.newui;

import java.awt.Toolkit;
import java.awt.datatransfer.*;
import java.io.IOException;

class PrintClipboard {

	public static void main(String args[]) {
		Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
		System.out.println("Object Name: " + clip.getName());
		Transferable contents = clip.getContents(new PrintClipboard().getClass());
		if (contents == null)
			System.out.println("\n\nThe clipboard is empty.");
		else {
			DataFlavor flavors[] = contents.getTransferDataFlavors();
			for (int i = 0; i < flavors.length; ++i) {
				System.out.println("i:" + i);
				System.out.println("Name: " + flavors[i].getHumanPresentableName());
				System.out.println("MIME Type: " + flavors[i].getMimeType());
				Class cl = flavors[i].getRepresentationClass();
				if (cl == null)
					System.out.println("null");
				else
					System.out.println(cl.getName());
				try {
					System.out.println("DATA:" + clip.getData(flavors[i]).getClass().getName());
					System.out.println("DATA:" + clip.getData(flavors[i]));
				} catch (UnsupportedFlavorException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}