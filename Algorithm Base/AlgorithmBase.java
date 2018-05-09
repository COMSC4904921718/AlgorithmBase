import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.Timer;
import javax.imageio.ImageIO;
import javax.swing.*;

	
public class AlgorithmBase extends JFrame implements ActionListener {
	
	private static final long serialVersionUID = 1L;
	public JFrame frame;
	public JPanel mainPane, leftPane, rightPane, bottomPane;
	public GridBagConstraints c;
	
	//Form the name of and path to this file
	public String Name = ("AlgorithmBase").replace('_', ' ');
	public String Path = System.getProperty("user.dir");
	//Max indicates max number of slides, cSlide is the current slide and size is the maximum size of user's computer screen
	public int max = 0, cSlide = 0, size = (int)((Toolkit.getDefaultToolkit().getScreenSize()).getWidth()*0.9);
	//kiserian indicates whether the program is in autoplay mode or not
	public boolean kiserian = false;
	
	public static void main(String[] args)    {	    new AlgorithmBase();    }
	
	public AlgorithmBase() //Creation of frame and its subsequent panels, and the menu bar
	{		
		//Parse the other files in the module and subtract the 3 non slide files and divide by 2 because two images are shown at once
		File dir = new File(Path + "//Algorithm Folder//" + Name);
		File[] dirList = dir.listFiles();
		if (dirList != null){
			for(File file : dirList){
				max++;
			}
			max = (max - 3)/2;
		}
		
		c = new GridBagConstraints();
		frame = new JFrame(Name + " Visualization");
		frame.setLayout(new GridLayout());
		//Ensures full closure of program on close
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		mainPane = new JPanel(new GridBagLayout());
		leftPane = new JPanel(new GridBagLayout());
		rightPane = new JPanel(new GridBagLayout());
		bottomPane = new JPanel();
		mainPane.add(leftPane,c);
		mainPane.add(rightPane,c);
		mainPane.add(bottomPane,c);
	
		frame.add(mainPane,c);
		frame.setSize(new Dimension(size+30, size/20*12));

		
		changeSlide(0);
		bottomButtons();

		frame.setVisible(true);	
		frame.revalidate();
	}
	public void bottomButtons(){
		try {
			//Delete the bottom Panel and reconstruct to change the autoplay icon to and from pause
			mainPane.remove(bottomPane);
			bottomPane = new JPanel();
			bottomPane.setPreferredSize(new Dimension(size/2,size/20));	
			c.gridy = 2;
			c.gridwidth = 2;
			mainPane.add(bottomPane,c);
			
			JButton button = new JButton("Help");
			button.setPreferredSize(new Dimension(size/20, size/25));
		    button.setName("help");
		    button.addActionListener(this);
		    bottomPane.add(button);
		    
			button = new JButton();
		    Image img = ImageIO.read(new File(Path + "\\resources\\backward.png"));
		    button.setIcon(new ImageIcon(img.getScaledInstance(size/30,size/30, Image.SCALE_SMOOTH)));
		    button.setName("backward");
		    button.addActionListener(this);
		    bottomPane.add(button);
		    
		    button = new JButton();
		    //Alternate pause and play icon based on whether autoplay is running
		    if(kiserian)
		    	img = ImageIO.read(new File(Path + "\\resources\\pause.png"));
		    else
		    	img = ImageIO.read(new File(Path + "\\resources\\play.png"));
		    button.setName("control");
		    button.setIcon(new ImageIcon(img.getScaledInstance(size/30,size/30, Image.SCALE_SMOOTH)));
		    button.addActionListener(this);
		    bottomPane.add(button);
		    
		    button = new JButton();
			img = ImageIO.read(new File(Path + "\\resources\\forward.png"));
			button.setName("forward");
			button.setIcon(new ImageIcon(img.getScaledInstance(size/30,size/30, Image.SCALE_SMOOTH)));
		    button.addActionListener(this);
		    bottomPane.add(button);
		    
			button = new JButton("Glossary");
			button.setPreferredSize(new Dimension(size/20, size/25));
		    button.setName("glossary");
		    button.addActionListener(this);
		    bottomPane.add(button);
		    
			frame.revalidate();
		} catch (Exception ex) {System.out.println(ex);}
	}
	public void changeSlide(int next){//Iterate the slides forward or backward depending on the input next
		if (cSlide + next >= max)
			cSlide = 0;
		else if (cSlide + next < 0)
			cSlide = max;
		else
			cSlide+= next;
		load(cSlide);
	}
	public void load(int slide){ //Recreate the left and right panels and upload the new images
		mainPane.remove(leftPane);
		mainPane.remove(rightPane);
		leftPane = new JPanel(new GridBagLayout());
		leftPane.setPreferredSize(new Dimension(size/2,size/2));
		rightPane = new JPanel(new GridBagLayout());
		rightPane.setPreferredSize(new Dimension(size/2,size/2));
		c.gridy = 1;
		c.gridwidth = 1;
		mainPane.add(leftPane);
		mainPane.add(rightPane);
		//Use the inputed number to determine the correct file to upload (File names are "[x]_Static" and "[x]_Code"
		JLabel pic = new JLabel(scaleImage(Path + "//Algorithm Folder//" + Name + "//" + (slide+1) + "_Static.png"));
		leftPane.add(pic);
		pic = new JLabel(scaleImage(Path + "//Algorithm Folder//" + Name + "//" + (slide+1) + "_Code.png"));
		pic.setVerticalAlignment(JLabel.CENTER);
		pic.setHorizontalAlignment(JLabel.CENTER);
		rightPane.add(pic);
		frame.revalidate();
	}
	public ImageIcon scaleImage(String imageToScale){//Determine the largest dimension of the image and scale to the size of the panel
		try{										 //While maintaining the images dimension ratio
			BufferedImage thisImage = ImageIO.read(new File(imageToScale));
			int y = thisImage.getHeight(); int x = thisImage.getWidth();
			double scale;
			if(x > y)
				scale = size/(2.0*x);
			else
				scale = size/(2.0*y);
			return new ImageIcon((thisImage).getScaledInstance(((int)(x*scale)),((int)(y*scale)), Image.SCALE_SMOOTH));
		}catch(IOException E){return new ImageIcon("");}
	}
	public void open(String fileName){
		try{//Open a the file based on its extension, acts as if the user manually opened the file from the desktop
			Process p = Runtime
		               .getRuntime()
		               .exec("rundll32 url.dll,FileProtocolHandler " + Path + "//Algorithm Folder//" + Name + "//" + fileName + ".txt");
		            p.waitFor();
		}catch(Exception e){System.out.println("File Opening Failure");}
	}
	@Override
	public void actionPerformed(ActionEvent event) {
		if(event.getSource().getClass() == JButton.class)	{
			String command = ((JButton) event.getSource()).getName();
			if(command.equals("backward"))
				changeSlide(-1);
			else if(command.equals("forward"))
				changeSlide(1);
			else if(command.equals("control")){
				//If autoplay is on, turn it off, if its off, turn it on
				kiserian = !kiserian;
				if(kiserian == true){ //If autoplay is on, schedule a slide change every 2 seconds
					Timer t = new Timer();
					t.schedule(new TimerTask() {
						@Override
						public void run(){
							if(kiserian == true)
								changeSlide(1);
						}
					}, 2000, 2000);	
				}
				bottomButtons();
			}
			else if(command.equals("help")){open("Module Help");}
			else if(command.equals("glossary")){open(Name + " Glossary");}
		}	
	}
}