

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Main {

   //-----------------------------------------------------------------
   //  Displays the main frame of the program.
   //-----------------------------------------------------------------
   public static void main (String[] args) {
	  // Intro s=new Intro();

      JFrame frame = new JFrame ("Humanity Crisis");
      frame.setExtendedState(Frame.MAXIMIZED_BOTH);  
      frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
      frame.getContentPane().add(new Game());
      
     
      frame.pack();
      frame.setVisible(true);
   }
}
