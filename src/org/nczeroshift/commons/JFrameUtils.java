package org.nczeroshift.commons;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import java.awt.*;
import java.io.File;
import java.io.InputStream;

/**
 * JFrameUtils
 * Created by Luís F. Loureiro, github.com/nczeroshift
 * Under MIT license
 */

public class JFrameUtils {
	
	/**
	 * Create a simple centered window(JFrame).
	 * @param title Window title.
	 * @param size Window size.
	 * @param panel Window contents panel.
	 * @param exitOnClose If true closes the application
	 * @return New JFrame instance.
	 */
	public static JFrame createCenteredWindow(String title, Dimension size, JPanel panel, boolean exitOnClose){
		JFrame frame = new JFrame(title);
		frame.setSize(size);
		frame.setPreferredSize(size);
		
		if(exitOnClose)
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		else
			frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		frame.add(panel);
		frame.pack();
		frame.setLocationRelativeTo(null);
		
		return frame;
	}

	public static void loadLookAndFeel(){
		InputStream fntVera = JFrameUtils.class.getResourceAsStream("/org/nczeroshift/fonts/VeraMono.ttf");

		try {
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, fntVera));
		} catch (Exception e) {
			e.printStackTrace();
			//Handle exception
		}

		for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()){
			if ("com.sun.java.swing.plaf.windows.WindowsLookAndFeel".equals(info.getClassName())){   
				try{
					UIManager.setLookAndFeel(info.getClassName());
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			} 
		}
	}
}
