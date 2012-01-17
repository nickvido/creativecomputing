package cc.creativecomputing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GraphicsDevice;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;

/**
 * @invisible
 * @author texone
 * @nosuperclasses
 */
public class CCFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3566712236052208336L;
	
	CCApp _myApplication;

	public CCFrame(final CCApp theApplication, final CCApplicationSettings theSettings) {
		super(theSettings.title(),theSettings.displayConfiguration());

		setResizable(theSettings.isResizable());
		setUndecorated(theSettings.undecorated());
		getContentPane().setLayout(new BorderLayout());
		getContentPane().setBackground(new Color(
			theSettings.background().red(),
			theSettings.background().green(),
			theSettings.background().blue())
		);
		setDefaultCloseOperation(theSettings.closeOperation().id());
		pack();
		
		//get insets to adjust frame size
		theSettings.appMode().setupFrame(this);
		
		Rectangle myBounds = theSettings.displayConfiguration().getBounds();
		
		if(theSettings.x() > -1){
			setLocation(myBounds.x+ theSettings.x(),myBounds.y + theSettings.y());
		}else{
			setLocation(
				myBounds.x + (myBounds.width - theSettings.width())/2,
				myBounds.y + (myBounds.height - theSettings.height())/2
			);
		}

		
		_myApplication = theApplication;
		_myApplication._myFrame = this;
		
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				_myApplication.component().setVisible(false);
				_myApplication.dispose();
			}
		});
		
		

		getContentPane().add(_myApplication.component(), BorderLayout.CENTER);
		
		_myApplication.frameSetup();
		

	}

	
	public void setVisible(boolean visible) {
		_myApplication.component().setVisible(visible);
		super.setVisible(visible);

		//attempt to get the focus of the canvas
		if(visible)_myApplication.component().requestFocus();
	}
}