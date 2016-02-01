import java.io.*;
import javax.sound.sampled.*;


import java.util.Random;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import javax.swing.*;

public class Game extends JPanel {

   private final int WIDTH = 1674, HEIGHT = 980,METEORS=15,SHOTS=10;
  private final int DELAY = 9, IMAGE_SIZE = 46;
  private Image img;
   private ImageIcon imageship,imagemeteorS,imagemeteorSR,win,lose,explosionS,explosionMS,Intro;
private int[] xm=new int[METEORS];
private int[] ym=new int[METEORS];
private int[] moveXm=new int[METEORS];
private int[] moveYm=new int[METEORS];
private int[] checkmeteorshot=new int[METEORS];
private int[] countanimationmeteor=new int[METEORS];
private ImageIcon[] meteor=new ImageIcon[METEORS];
private ImageIcon[] explosionM=new ImageIcon[METEORS];
private ImageIcon[] shot=new ImageIcon[SHOTS];
private int[] xSs=new int[SHOTS];
private int[] ySs=new int[SHOTS];
private int[] checkshot=new int[SHOTS];
Clip clip,clip2,clip3,clip4,clip5;
   private Timer timer;
private static int xs,ys,moveXs,checkshiphit=0,xmS,ymS,moveXmS,moveYmS,checkmeteorS=0,count=0,checkwin=0,checkMSfall=0
		   ,countanimationship=0,increment=0,checkmeteorshotS=0,countanimationmeteorS=0,intro=0;

   //-----------------------------------------------------------------
   //  Sets up the panel, including the timer for the animation.
   //-----------------------------------------------------------------
   public Game() {
	   try {
	         // Open an audio input stream.
		   File soundFile = new File("music.wav");
		    AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
	         // Get a sound clip resource.
	          clip = AudioSystem.getClip();
	         // Open audio clip and load samples from the audio input stream.
	         clip.open(audioIn);
	         clip.loop(0);  // repeat none (play once), can be used in place of start().
	         clip.loop(5);  // repeat 5 times (play 6 times)
	         clip.loop(Clip.LOOP_CONTINUOUSLY);  // repeat forever
		         
	      } catch (UnsupportedAudioFileException e) {
	         e.printStackTrace();
	      } catch (IOException e) {
	         e.printStackTrace();
	      } catch (LineUnavailableException e) {
	         e.printStackTrace();
	      }

	   
	   
      timer = new Timer(DELAY, new ReboundListener());
System.out.println();
      imageship = new ImageIcon ("ship.png");
      imagemeteorS=new ImageIcon ("meteorS.png");
      imagemeteorSR=new ImageIcon ("meteorRS.png");
      win=new ImageIcon ("win.gif");
      lose=new ImageIcon ("lose.gif");
      Intro=new ImageIcon ("Intro.gif");
      explosionS=new ImageIcon ("explosionS.gif");
      for(int i=0;i<METEORS;i++)
      explosionM[i]=new ImageIcon ("explosionM.gif");
      explosionMS=new ImageIcon ("explosionMS.gif");
      for(int i=0;i<METEORS;i++)
      meteor[i] = new ImageIcon ("meteor.png");
      for(int i=0;i<SHOTS;i++)
      shot[i] = new ImageIcon ("shot.png");
      img = Toolkit.getDefaultToolkit().createImage("behind.jpg");
      Random randomGenerator = new Random();
      for(int i=0;i<METEORS;i++){
    	  if(i==0)
      xm[i]=randomGenerator.nextInt(1550)-200;
    	  else
    		  xm[i]=2000; 
      	ym[i]=-100;
      	
      	moveXm[i]=randomGenerator.nextInt(2)+2;
      	moveYm[i]=randomGenerator.nextInt(7)+1;
      }

       xs = 835;
    	ys = 900;

    	ymS=-100;
    		xmS=randomGenerator.nextInt(1500);
    		moveXmS=randomGenerator.nextInt(2)+2;
    		moveYmS=randomGenerator.nextInt(3)+2;
      moveXs = 0;


      setPreferredSize (new Dimension(WIDTH, HEIGHT));
      setBackground (Color.black);
    
      timer.start();


   }

   //-----------------------------------------------------------------
   //  Draws the image in the current location.
   //-----------------------------------------------------------------
   public void paintComponent (Graphics g)
   {
	   
	   super.paintComponent (g);
      g.drawImage(img, 0, 0, null);
      if(intro<=1000)
      Intro.paintIcon(this, g, (getWidth() / 2 - win.getIconWidth() / 2)-95, getHeight() / 2);
      if(checkshiphit==0)
      imageship.paintIcon (this, g, xs, ys);
      for(int i=0;i<METEORS;i++)
    	  if(checkmeteorshot[i]==0)
      meteor[i].paintIcon (this, g, xm[i], ym[i]);
      for(int i=0;i<SHOTS;i++)
    	  if(checkshot[i]==1)
      shot[i].paintIcon (this, g, xSs[i], ySs[i]);
      if(checkmeteorshotS==0)
      if(checkmeteorS==0)    	 
      imagemeteorS.paintIcon (this, g,xmS, ymS);    	 
      else
      imagemeteorSR.paintIcon (this, g,xmS, ymS);
      if(checkwin==1 && checkshiphit==0)
      win.paintIcon(this, g, getWidth() / 2 - win.getIconWidth() / 2, getHeight() / 2);
      if(checkshiphit==1 && countanimationship>65 || checkMSfall==1)
      lose.paintIcon(this, g, getWidth() / 2 - lose.getIconWidth() / 2, getHeight() / 2);
      if(checkshiphit==1 && countanimationship<65){ 	  
      explosionS.paintIcon (this, g,xs, ys-50);
      }
      for(int i=0;i<METEORS;i++)
      if(checkmeteorshot[i]==1 && countanimationmeteor[i]<65)
      explosionM[i].paintIcon (this, g, xm[i], ym[i]);
      if(checkmeteorshotS==1 && countanimationmeteorS<65)
      explosionMS.paintIcon (this, g,xmS, ymS);
   }

   //*****************************************************************
   //  Represents the action listener for the timer.
   //*****************************************************************
   private class ReboundListener implements ActionListener,KeyListener
   {
	   public ReboundListener()
	   {
		   addKeyListener(this);
	   setFocusable(true);
	   setFocusTraversalKeysEnabled(false);
	   }
	   //--------------------------------------------------------------
      //  Updates the position of the image and possibly the direction
      //  of movement whenever the timer fires an action event.
      //--------------------------------------------------------------
      public void actionPerformed (ActionEvent event)
      {
    	  intro++;
    	  if(intro>1000){
    	  Random randomGenerator = new Random();
    	  count++;
    	  if(checkshiphit==1){
    	  countanimationship++;
    	  
		   try {
		         // Open an audio input stream.
			   File soundFile6 = new File("meteorhit.wav");
			    AudioInputStream audioIn6 = AudioSystem.getAudioInputStream(soundFile6);
		         // Get a sound clip resource.
		          clip5 = AudioSystem.getClip();
		         // Open audio clip and load samples from the audio input stream.
		         clip5.open(audioIn6);
		         clip5.start();
			         
		      } catch (UnsupportedAudioFileException e) {
		         e.printStackTrace();
		      } catch (IOException e) {
		         e.printStackTrace();
		      } catch (LineUnavailableException e) {
		         e.printStackTrace();
		      }
    	  
    	  }
    	  if(checkmeteorshotS==1)
    		  countanimationmeteorS++;    	  
    	  for(int i=0;i<METEORS;i++)
    		  if(checkmeteorshot[i]==1)
    	  countanimationmeteor[i]++;
  
    	  if(count==18000/4){
    		  timer.stop();
    		  checkwin=1;
    		  
    		  if (clip.isRunning()){ 
    			  clip.stop();
    		   try {
    		         // Open an audio input stream.
    			   File soundFile4 = new File("won.wav");
    			    AudioInputStream audioIn4 = AudioSystem.getAudioInputStream(soundFile4);
    		         // Get a sound clip resource.
    		          clip3 = AudioSystem.getClip();
    		         // Open audio clip and load samples from the audio input stream.
    		         clip3.open(audioIn4);
    		         clip3.start();
    			         
    		      } catch (UnsupportedAudioFileException e) {
    		         e.printStackTrace();
    		      } catch (IOException e) {
    		         e.printStackTrace();
    		      } catch (LineUnavailableException e) {
    		         e.printStackTrace();
    		      }
    		  }
    	  }
    	  if(xs<0){
    		  moveXs=0;
    		  xs=0;
    	  }
    	  if(xs>WIDTH-IMAGE_SIZE){
    		  moveXs=0;
    		  xs=WIDTH-IMAGE_SIZE;
    	  }
    	  if(checkshiphit==0)
          xs=xs+moveXs;
          xmS=xmS+moveXmS;
          if (xmS+50 > WIDTH-IMAGE_SIZE){
        	  moveXmS = moveXmS * -1;
        	  checkmeteorS=1;
          }

    	  for(int i=0;i<METEORS;i++){
          if (ym[i]>1200){
         	 ym[i]=-100;
         	checkmeteorshot[i]=0;
         	countanimationmeteor[i]=0;
         	xm[i]=randomGenerator.nextInt(1674)-100;
         	for(int j=0;j<METEORS;j++){
          	moveXm[i]=randomGenerator.nextInt(2)+2;
          	moveYm[i]=randomGenerator.nextInt(7)+1;
         	}
         	}
         
          xm[i] += moveXm[i];
         ym[i] += moveYm[i];
   	  
         if (xm[i]+45<=xs+50 && xm[i]+70>=xs-10 && ym[i]+75>ys && ym[i]<ys+67 && checkmeteorshot[i]==0) 
        	 checkshiphit=1; 

         
       	  if(countanimationship>65){
		  timer.stop();
		  if (clip.isRunning()){ 
			  clip.stop();
		   try {
		         // Open an audio input stream.
			   File soundFile2 = new File("lost.wav");
			    AudioInputStream audioIn2 = AudioSystem.getAudioInputStream(soundFile2);
		         // Get a sound clip resource.
		          clip2 = AudioSystem.getClip();
		         // Open audio clip and load samples from the audio input stream.
		         clip2.open(audioIn2);
		         clip2.start();
			         
		      } catch (UnsupportedAudioFileException e) {
		         e.printStackTrace();
		      } catch (IOException e) {
		         e.printStackTrace();
		      } catch (LineUnavailableException e) {
		         e.printStackTrace();
		      }
		  }
       	  }
    	  }
          for(int i=0;i<SHOTS;i++)
        	  for(int j=0;j<METEORS;j++)
        		  if(checkmeteorshot[j]==0)
              if (xm[j]+45<=xSs[i]+20 && xm[j]+70>=xSs[i]-10 && ym[j]+75>ySs[i] && ym[j]+69<ySs[i]+67 && checkshot[i]==1){ 
            	  checkmeteorshot[j]=1;
            	  checkshot[i]=0;
            	  

        		   try {
        		         // Open an audio input stream.
        			   File soundFile5 = new File("meteorhit.wav");
        			    AudioInputStream audioIn5 = AudioSystem.getAudioInputStream(soundFile5);
        		         // Get a sound clip resource.
        		          clip4 = AudioSystem.getClip();
        		         // Open audio clip and load samples from the audio input stream.
        		         clip4.open(audioIn5);
        		         clip4.start();
        			         
        		      } catch (UnsupportedAudioFileException e) {
        		         e.printStackTrace();
        		      } catch (IOException e) {
        		         e.printStackTrace();
        		      } catch (LineUnavailableException e) {
        		         e.printStackTrace();
        		      }
        		  
            	  
              }
          if(checkmeteorS==0){
          for(int i=0;i<SHOTS;i++)
        	  if(checkmeteorshotS==0)
              if (xmS+45<=xSs[i]+20 && xmS+70>=xSs[i]-10 && ymS+75>ySs[i] && ymS+69<ySs[i]+67 && checkshot[i]==1){ 
            	  checkmeteorshotS=1;
            	  checkshot[i]=0; 
            	  

        		   try {
        		         // Open an audio input stream.
        			   File soundFile5 = new File("meteorhit.wav");
        			    AudioInputStream audioIn5 = AudioSystem.getAudioInputStream(soundFile5);
        		         // Get a sound clip resource.
        		          clip4 = AudioSystem.getClip();
        		         // Open audio clip and load samples from the audio input stream.
        		         clip4.open(audioIn5);
        		         clip4.start();
        			         
        		      } catch (UnsupportedAudioFileException e) {
        		         e.printStackTrace();
        		      } catch (IOException e) {
        		         e.printStackTrace();
        		      } catch (LineUnavailableException e) {
        		         e.printStackTrace();
        		      }
        		  
            	  
              }
          }
          else
              for(int i=0;i<SHOTS;i++)
            	  if(checkmeteorshotS==0)
                  if (xmS<=xSs[i]+5 && xmS+42>=xSs[i] && ymS+75>ySs[i] && ymS+69<ySs[i]+67 && checkshot[i]==1){ 
                	  checkmeteorshotS=1;
                	  checkshot[i]=0;   
                	  
           		   try {
      		         // Open an audio input stream.
      			   File soundFile5 = new File("meteorhit.wav");
      			    AudioInputStream audioIn5 = AudioSystem.getAudioInputStream(soundFile5);
      		         // Get a sound clip resource.
      		          clip4 = AudioSystem.getClip();
      		         // Open audio clip and load samples from the audio input stream.
      		         clip4.open(audioIn5);
      		         clip4.start();
      			         
      		      } catch (UnsupportedAudioFileException e) {
      		         e.printStackTrace();
      		      } catch (IOException e) {
      		         e.printStackTrace();
      		      } catch (LineUnavailableException e) {
      		         e.printStackTrace();
      		      }
                	  
                  }
        	  
    	  if (ymS>1095){
    		  if(checkmeteorshotS==0){ 
    		  checkMSfall=1;    		  
    		  timer.stop();
    		  
    		  if (clip.isRunning()){ 
    			  clip.stop();
    		   try {
    		         // Open an audio input stream.
    			   File soundFile2 = new File("lost.wav");
    			    AudioInputStream audioIn2 = AudioSystem.getAudioInputStream(soundFile2);
    		         // Get a sound clip resource.
    		          clip2 = AudioSystem.getClip();
    		         // Open audio clip and load samples from the audio input stream.
    		         clip2.open(audioIn2);
    		         clip2.start();
    			         
    		      } catch (UnsupportedAudioFileException e) {
    		         e.printStackTrace();
    		      } catch (IOException e) {
    		         e.printStackTrace();
    		      } catch (LineUnavailableException e) {
    		         e.printStackTrace();
    		      }
    		  }

    		  if (clip.isRunning()){ 
    			  clip.stop();
    		   try {
    		         // Open an audio input stream.
    			   File soundFile3 = new File("sound.wav");
    			    AudioInputStream audioIn3 = AudioSystem.getAudioInputStream(soundFile3);
    		         // Get a sound clip resource.
    		          clip2 = AudioSystem.getClip();
    		         // Open audio clip and load samples from the audio input stream.
    		         clip2.open(audioIn3);
    		         clip2.start();
    			         
    		      } catch (UnsupportedAudioFileException e) {
    		         e.printStackTrace();
    		      } catch (IOException e) {
    		         e.printStackTrace();
    		      } catch (LineUnavailableException e) {
    		         e.printStackTrace();
    		      }
    		  }
    		  

    		   
    		  }
    	  }
    	  if (ymS>9000){
    		  countanimationmeteorS=0;
    		  moveXmS = moveXmS * -1;
    		  xmS=randomGenerator.nextInt(1500);
    		  ymS=-100;
    		  checkmeteorS=0;
    		  checkmeteorshotS=0;

    	  }
    	  
    	  for(int i=0;i<SHOTS;i++)
    	  ySs[i]+=-5;
    	  ymS+=moveYmS;
    	  if(checkmeteorS==0){
          if (xmS+45<=xs+50 && xmS+70>=xs-10 && ymS+75>ys && ymS<ys+67 && checkmeteorshotS==0) 
         	 checkshiphit=1; 
    	  }
    	  else if (xmS<=xs+35 && xmS+42>=xs && ymS+75>ys && ymS<ys+67 && checkmeteorshotS==0) 
           	 checkshiphit=1; 

         	   
    	  }
         repaint();

         
      }
      public void keyPressed(KeyEvent e){
    	  int p=e.getKeyCode();

    	  if(p==KeyEvent.VK_LEFT)
    		  moveXs=-5;
    	  if(p==KeyEvent.VK_RIGHT)
    		  moveXs=5;  
    	  
   
    	  
      }
      public void keyTyped(KeyEvent e){
    	  int c=e.getKeyCode();


      }
      public void keyReleased(KeyEvent e){
    	  int r=e.getKeyCode();
    	  if(r!=KeyEvent.VK_SPACE)
    	  moveXs=0;
          if(r==KeyEvent.VK_SPACE){

        	  if(increment<SHOTS){    		 
			  checkshot[increment]=1;
			  xSs[increment]=xs+10;
			  ySs[increment]=ys-55;
    			  increment++;
    			  }
          }
      }
   }
      
   }
   

