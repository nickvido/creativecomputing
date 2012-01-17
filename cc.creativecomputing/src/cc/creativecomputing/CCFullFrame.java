package cc.creativecomputing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

/**
 * @invisible
 * @author texone
 *
 */
public class CCFullFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3566712236052208336L;
	
	CCApp _myApplication;

	public CCFullFrame(final CCApp theApplication, final CCApplicationSettings theSettings) {
		super(theSettings.title(),theSettings.displayConfiguration());
		
		setLayout(null);
		setDefaultCloseOperation(theSettings.closeOperation().id());
//		pack();
		
		//get insets to adjust frame size
		theSettings.appMode().setupFrame(this);
		
		//
		final Dimension myScreenDimension = Toolkit.getDefaultToolkit().getScreenSize();
		setSize(myScreenDimension);
		setUndecorated(true);
		getContentPane().setBackground(new Color(
			theSettings.background().red(),
			theSettings.background().green(),
			theSettings.background().blue())
		);

		_myApplication = theApplication;
		_myApplication._myFrame = this;

		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				_myApplication.component().setVisible(false);
				_myApplication.dispose();
			}
		});
		
		int myXlocation;
		int myYlocation;
		
		if(theSettings.x() >-1){
			myXlocation = theSettings.x();
			myYlocation = theSettings.y();
		}else{
			myXlocation = (myScreenDimension.width - theSettings.width()) / 2;
			myYlocation = (myScreenDimension.height - theSettings.height()) / 2;
		}
		
		theApplication.component().setBounds(
			myXlocation, 
			myYlocation, 
			theSettings.width(), 
			theSettings.height()
		);

		add(_myApplication.component());
		
		theSettings.display().setFullScreenWindow(this);
	}

	public void setVisible(boolean visible) {
		super.setVisible(visible);

		//attempt to get the focus of the canvas
		_myApplication.component().requestFocus();
	}
}