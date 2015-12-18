package com.so;

import javax.swing.JPanel;
import javax.swing.JProgressBar;
/*author
 * Shrisowdhaman.S 
 * 25th Oct 2015
 */
public class ProgressBarExample extends JPanel {
	
	JProgressBar jp = null;
	
	public ProgressBarExample(int min, int max){
		
		jp = new JProgressBar();
		jp.setMinimum(min);
		jp.setMaximum(max);
		jp.setStringPainted(true);
        jp.setVisible(true);
		
		add(jp);
	}
	
	public void updateBar(int perc){
		jp.setValue(perc);		
	}
	
	public void exit(){
		System.exit(0);		
	}
			

}
