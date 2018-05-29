import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.text.DecimalFormat;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayer;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.plaf.LayerUI;
 
 
public class GUI {
    private JFrame mainFrame;
    private JPanel mainPanel;
    private JPanel fractalPanel;
    
    private Mandelbrot mFractal;
    private JPanel mandelbrotPane;
    private JPanel mandelbrotControlPanel;
    private JButton resetButton;
    private JPanel stylePanel;
    private JPanel visualPanel;
    private JComboBox<String> visualComboBox;
    private String[] visuals = {"Normal","Line Orbit Trap","Circular Orbit Trap","Line Region Splits","Circular Region Splits"};
    private JComboBox<String> styleComboBox;
    private String[] styles = {"Mandelbrot","Burning Ship", "Newton"};
    private JPanel displayPosition;
    private JLabel userSelectedPoint;
    
    private Julia jFractal;
    private JPanel saveJuliaPanel;
    private JLabel saveJulia;
    private JTextField juliaName;
    private JButton setJuliaName;
     
    private JPanel loadJuliaSetPanel;
    private HashMap<String,Complex> favourites;
    private String[] arrFavourites;
    private JComboBox<String> favouritesComboBox;
     
    
    private JPanel juliaPane;
    private JPanel juliaControlPanel;
    
    private int startX, startY, endX, endY;
    private boolean dragging = false;
     
    private Complex location;
    private int currentX, currentY;
    private String positionText;
     
    /**
     * Build Mandelbrot and Julia GUIs
     */
    public GUI(){
        mainFrame = new JFrame("Mandelbrot & Julia");
        mainFrame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE);
        mainFrame.setBounds(0,30,1200,740);
         
        favourites = new HashMap<String,Complex>();
    }
     
    /**
     * Setup all GUI elements. 
     */
    public void initGUI(){
        mainPanel = new JPanel();
        mainFrame.setContentPane(mainPanel);
        mainPanel.setLayout(new BorderLayout());
         
        fractalPanel = new JPanel();
        fractalPanel.setLayout(new GridLayout(1,2));
        mainPanel.add(fractalPanel,BorderLayout.CENTER);
         
        mandelbrotPane = new JPanel();
        mandelbrotPane.setLayout(new BorderLayout());
        fractalPanel.add(mandelbrotPane,BorderLayout.CENTER);
         
        //TODO Tidy this up
        //Mandelbrot Pane and ControlPanel
        mFractal = new Mandelbrot();
        LayerUI<JComponent> layerUI = new ZoomUI();
        JLayer<JComponent> jlayer = new JLayer<JComponent>(mFractal, layerUI);
         
        mandelbrotPane.add(jlayer,BorderLayout.CENTER);
        mFractal.addMouseListener(new MandelbrotMouseListener());
        mFractal.addMouseMotionListener(new MandelbrotMouseListener());
         
        mandelbrotControlPanel = new JPanel();
        mandelbrotControlPanel.setLayout(new GridLayout(4,1));
        mandelbrotPane.add(mandelbrotControlPanel,BorderLayout.SOUTH);
        
        resetButton = new JButton("Reset Size");
        mandelbrotControlPanel.add(resetButton);
        resetButton.addActionListener(new ResetListener());
        
        
      //Visual Style Control
	    	visualPanel = new JPanel();
	    	visualPanel.setLayout(new GridLayout(1,2));
	    	mandelbrotControlPanel.add(visualPanel);
	    	
	    	visualPanel.add(new JLabel("Visualization style: "));
	        visualComboBox = new JComboBox<String>(visuals);
	        visualPanel.add(visualComboBox);
	        addVisualComboBoxActionListener();
        
        //Fractal Style Control
        	stylePanel = new JPanel();
        	stylePanel.setLayout(new GridLayout(1,2));
        	mandelbrotControlPanel.add(stylePanel);
        	
        	stylePanel.add(new JLabel("Select fractal style: "));
	        styleComboBox = new JComboBox<String>(styles);
	        stylePanel.add(styleComboBox);
	        addStyleComboBoxActionListener();
         
        //Position Display
         
            displayPosition = new JPanel();
            displayPosition.setLayout(new GridLayout(1,2));
            mandelbrotControlPanel.add(displayPosition);
             
            displayPosition.add(new JLabel("User selected Point: "));
             
            userSelectedPoint = new JLabel("0.00 + 0.00i");
            displayPosition.add(userSelectedPoint);
             
         
        //Julia Pane and ControlPanel
        juliaPane = new JPanel();
        juliaPane.setLayout(new BorderLayout());
        fractalPanel.add(juliaPane,BorderLayout.CENTER);
         
        jFractal = new Julia(4,3.2);
        juliaPane.add(jFractal,BorderLayout.CENTER);
         
         
        juliaControlPanel = new JPanel();
        juliaControlPanel.setLayout(new GridLayout(2,1));
        juliaPane.add(juliaControlPanel,BorderLayout.SOUTH);
         
        //Add to Bookmark UI
        	saveJuliaPanel = new JPanel();
        	saveJuliaPanel.setLayout(new BorderLayout());
        	juliaControlPanel.add(saveJuliaPanel);
        	
            saveJulia = new JLabel("Bookmark: ");
            saveJuliaPanel.add(saveJulia,BorderLayout.WEST);
             
            juliaName = new JTextField(20);
            saveJuliaPanel.add(juliaName,BorderLayout.CENTER);
             
            setJuliaName = new JButton("Add to favourite");
            saveJuliaPanel.add(setJuliaName,BorderLayout.EAST);
            setJuliaName.addActionListener(new addToFavourite());
             
        //Load bookmarked Julia sets UI
            loadJuliaSetPanel = new JPanel();
            loadJuliaSetPanel.setLayout(new GridLayout(1,2));
            juliaControlPanel.add(loadJuliaSetPanel);
             
            loadJuliaSetPanel.add(new JLabel("Load saved Julia Set: "));
             
            favouritesComboBox = new JComboBox<String>();
            loadJuliaSetPanel.add(favouritesComboBox);
         
        //mainFrame.pack();
        mainFrame.setVisible(true);
    }
    
    /**
     * Add Mandelbrot Style ComboBox action listener to styleComboBox
     */
    public void addStyleComboBoxActionListener(){
    	styleComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                //Get new style's name
                String selection = (String) styleComboBox.getSelectedItem();
                 
                //Build a fractal using the new style
                mFractal.changeMode(selection);
                mFractal.reset();
                mFractal.repaint();
                
                //Change julia set to the selected
                jFractal.changeMode(selection);
                jFractal.repaint();
            }
        });
    }
    
    /**
     * Add Mandelbrot Visualization ComboBox action listener to visualComboBox
     */
    public void addVisualComboBoxActionListener(){
    	visualComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                //Get new style's name
                String selection = (String) visualComboBox.getSelectedItem();
                 
                //Build a fractal using the new style
                mFractal.triggerVisual(selection);
            }
        });
    }
    

     
    /**
     * Build the GUI
     * @param args	No Argument.
     */
    public static void main(String[] args){
        GUI gui = new GUI();
        gui.initGUI();
    }
     
    class ResetListener implements ActionListener {
 
        public void actionPerformed(ActionEvent e){
            mFractal.reset();
 
        }
    }
     
    class ZoomUI extends LayerUI<JComponent> {
         
        boolean mouseInside;
        
        /**
         * Install the LayerUI onto the Mandelbrot Panel.
         * 
         * This includes the function for drag-zoom box, mouse position live-update box.
         */
        @Override
          public void installUI(JComponent c) {
            super.installUI(c);
            JLayer jlayer = (JLayer)c;
            jlayer.setLayerEventMask(
              AWTEvent.MOUSE_EVENT_MASK |
              AWTEvent.MOUSE_MOTION_EVENT_MASK
            );
          }
           
        /**
         * Remove the LayerUI from the Mandelbrot Panel.
         */
          //@Override
          public void uninstallUI(JComponent c) {
            JLayer jlayer = (JLayer)c;
            jlayer.setLayerEventMask(0);
            super.uninstallUI(c);
          }
          
          /**
           * Paint the Mouse Live-update box next to the mouse, or paint the drag-zoom box on the Mandelbrot panel.
           */
          @Override
          public void paint (Graphics g, JComponent c) {
            Graphics2D g2 = (Graphics2D)g.create();
 
            // Paint the view.
            super.paint (g2, c);
             
            //Change cursor style and draw cursor position panel
            if(mouseInside){
                mFractal.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
                 
                Color myColour = new Color(0, 0,0, 200 );
                g2.setColor(myColour);
                g2.fillRect(currentX,currentY, 85,15 );
                 
                g.setColor(Color.red);
                g.drawString(positionText, currentX, currentY+10);
            }
             
            //Draw drag rectangle
            if(dragging){
                Color myColour = new Color(0, 0,0, 128 );
                g2.setColor(myColour);
                g2.fillRect(startX, startY, endX - startX, endY - startY);
                myColour = new Color(155, 0,0,255);
                g2.setColor(myColour);
                g2.drawRect (startX, startY, endX - startX, endY - startY); 
            }
          
            g2.dispose();
          }
          
          /**
           * Trigger the mouse location string to appear next to the mouse.
           */
          protected void processMouseEvent(MouseEvent e, JLayer l) {
                if (e.getID() == MouseEvent.MOUSE_ENTERED) mouseInside = true;
                if (e.getID() == MouseEvent.MOUSE_EXITED) mouseInside = false;
          }
           
          /**
           * Repaint the zooming selection box when the user is dragging. 
           */
          @Override
          protected void processMouseMotionEvent(MouseEvent e, JLayer l) {
            l.repaint();
          }
    }
     
    class addToFavourite implements ActionListener {
         
        /**
         * Update ComboBox as the new Julia set point has been added.
         */
        public void actionPerformed(ActionEvent e){
            favourites.put(juliaName.getText(), jFractal.getComplex());
             
            //Remove the current ComboBox
            loadJuliaSetPanel.remove(favouritesComboBox);
            //Store all keys from 'favourites' to an array
            arrFavourites = favourites.keySet().toArray(new String[favourites.size()]);
            //Create a new ComboBox with the new set of keys
            favouritesComboBox = new JComboBox<String>(arrFavourites);
             
            favouritesComboBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    //Get the selected favourite
                    String selection = (String) favouritesComboBox.getSelectedItem();
                     
                    //Build a new Julia set by looking up the complex number in hashmap 'favourites'
                    jFractal.setC(favourites.get(selection));
                    jFractal.repaint();
                }
            });
             
            //Add the new ComboBox back to panel
            loadJuliaSetPanel.add(favouritesComboBox,BorderLayout.SOUTH);
             
            loadJuliaSetPanel.updateUI();
        }
    }
     
    class MandelbrotMouseListener implements MouseListener, MouseMotionListener{
         
        /**
         * Compile a new Julia set from user selected complex number.
         * Update the user selection point in the GUI.
         */
        @Override
        public void mouseClicked(MouseEvent e) {
             
            location = mFractal.getComplexPosition(new Complex(e.getX(),e.getY()));
             
            DecimalFormat df = new DecimalFormat("#.#####");
            userSelectedPoint.setText(df.format(location.getReal())+" + "+df.format(location.getImaginary())+"i");
             
        }
         
        /**
         * Set the starting position for dragging mode. 
         */
        @Override
        public void mousePressed(MouseEvent e) {
            startX = e.getX();
            startY = e.getY();
        }
 
        /**
         * Trigger the end of dragging mode. This set the Mandelbrot into a new size and trigger repaint.
         */
        @Override
        public void mouseReleased(MouseEvent e) {
            if(dragging == true){
                dragging = false;
                
                //mFractal.zoom(new Complex(startX,startY), new Complex(endX, endY));
                mFractal.animateZoom(new Complex(startX,startY), new Complex(endX, endY));
                //mFractal.repaint();
            }
        }
 
        @Override
        public void mouseEntered(MouseEvent e) {}
 
        @Override
        public void mouseExited(MouseEvent e) {}
         
        /**
         * Return current mouse position while dragging.
         */
        @Override
        public void mouseDragged(MouseEvent e) {
            dragging = true;
            endX = e.getX();
            endY = e.getY();
        }
        
        /**
         * Update the current location of the mouse.
         * Update the mouse location String.
         * Set live update for the Julia set.
         */
        @Override
        public void mouseMoved(MouseEvent e) {
            currentX = e.getX();
            currentY = e.getY();
             
            location = mFractal.getComplexPosition(new Complex(e.getX(),e.getY()));
             
            DecimalFormat df = new DecimalFormat("#.##");
            positionText = df.format(location.getReal())+"+"+df.format(location.getImaginary())+"i";
            
            //Live Update for Julia Set
            jFractal.setC(mFractal.getComplexPosition(new Complex(e.getX(),e.getY())));
            jFractal.repaint();
        }
    }
}