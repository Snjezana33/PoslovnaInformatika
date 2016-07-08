package utils;

import java.awt.Window;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class LookAndFeel {
	
	public static void main(String[] a) {
		list();
	}
	
	public static Map<String, String> list(){
		Map<String, String> lfs = new HashMap<String, String>();
		
		UIManager.LookAndFeelInfo[] looks = UIManager.getInstalledLookAndFeels();
	    for (UIManager.LookAndFeelInfo look : looks) {
	      System.out.println(look.getClassName());
	      System.out.println(look.getName());
	      
	      lfs.put(look.getClassName(), look.getName());
	    }
	    
	    return lfs;
	}
	
	public static void setLookAdnFeel(String classNAme){
		try {
			UIManager.setLookAndFeel(classNAme);
			for(Window window : JFrame.getWindows()) {
		        SwingUtilities.updateComponentTreeUI(window);
		    }
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	}
}
