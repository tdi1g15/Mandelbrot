import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.Timer;

import javax.swing.JPanel;
  
public class Mandelbrot extends JPanel {
     
    private BufferedImage image;
    private final int MAX_ITERATION = 100;
    private final int IMAGE_WIDTH = 600;
    private final int IMAGE_HEIGHT = 600;
    private Complex c;
    private Complex pixel;
     
    private double xAxisLength;
    private double yAxisLength;
    private Complex complexStartPosition;
    private Complex oldComplexStartPosition;
    private Complex complexEndPosition;
    
    private Timer timer;
    private int iterationCounter;
     
    private Color color;
    private int rgb;
     
     
    private String mode;
     
    private String visual;
    
    
 
    /**
     * Initialize the Mandelbrot Set.
     */
    public Mandelbrot(){
    	
    	setPreferredSize(new Dimension(IMAGE_WIDTH, IMAGE_HEIGHT));
    	
        xAxisLength = 4;
        yAxisLength = 3.2;
         
        image = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
         
        pixel = new Complex(0,0);
        c = new Complex(0,0);
         
        complexStartPosition = new Complex(-2,-1.6);
        oldComplexStartPosition = new Complex(-2,-1.6);
        complexEndPosition = new Complex(2,1.6);
        
        mode = "Mandelbrot";
        visual = "Normal";
    }
     
    /**
     * Change calculation mode.
     * @param mode	The name of the calculation mode.
     */
    public void changeMode(String mode){
        this.mode = mode;
    }
    
    /**
     * Trigger the panel to change visualization.
     * @param visual The name of the visualization.
     */
    public void triggerVisual(String visual){
    	this.visual = visual;
    	repaint();
    }
     
    /**
     * Reset to original size. Real axis from -2 to 2, imaginary axis from -1.6 to 1.6
     */
    public void reset(){
        xAxisLength = 4;
        yAxisLength = 3.2;
        complexStartPosition = new Complex(-2,-1.6);
        oldComplexStartPosition = new Complex(-2,-1.6);
        complexEndPosition = new Complex(2,1.6);
        
        repaint();
    }
     
    /**
     * Unused method.
     * 
     * Static zooming. This zooms into the set directly without animation.
     * @param startPosition	The Complex Number of the top left corner once it has zoomed in.
     * @param endPosition	The Complex Number of the bottom right corner once it has zoomed in.
     */
    public void zoom (Complex startPosition, Complex endPosition){

        //Calculate the new starting and ending position from pixel number to complex number
        complexStartPosition = getComplexPosition(startPosition);
        complexEndPosition = getComplexPosition(endPosition);
        oldComplexStartPosition = complexStartPosition;
         
        //Calculate the length of both axis
        this.xAxisLength = complexEndPosition.getReal() - complexStartPosition.getReal();
        this.yAxisLength = complexEndPosition.getImaginary() - complexStartPosition.getImaginary();
        
        repaint();
    }
    
    /**
     * Animated zooming. 
     * 
     * This zooms into the set with animation.
     * 
     * The animation is split into 15 frames, 30 milliseconds apart. So the estimated time for the zooming is 0.45 seconds.
     * @param startPosition	The Complex Number of the top left corner once it has zoomed in.
     * @param endPosition	The Complex Number of the bottom right corner once it has zoomed in.
     */
    public void animateZoom (Complex startPosition, Complex endPosition){
    	
    	timer = new Timer(30, new ZoomTimer(startPosition,endPosition, 15));
    	timer.start();
    	
    }
    
    class ZoomTimer implements ActionListener{
    	
    	double zoomInFrames;
    	int counter;
    	Complex startPosition;
    	Complex endPosition;
    	
    	double startFrameZoomDistanceX;
    	double startFrameZoomDistanceY;
    	double endFrameZoomDistanceX;
    	double endFrameZoomDistanceY;
    	
    	/**
    	 * Initialize all variables.
    	 * @param startPosition		The Complex Number of the top left corner once it has zoomed in.
    	 * @param endPosition		The Complex Number of the bottom right corner once it has zoomed in.
    	 * @param zoomInFrames		The number of frames when zooming.
    	 */
    	public ZoomTimer(Complex startPosition, Complex endPosition, double zoomInFrames){
    		this.startPosition = startPosition;
    		this.endPosition = endPosition;
    		this.zoomInFrames = zoomInFrames;
    		counter = (int) this.zoomInFrames;
    		
    		//Convert the destination pixel location into complex number location.
    		startPosition = getComplexPosition(startPosition);
	    	endPosition = getComplexPosition(endPosition);
    		
	    	//Measure the length to minimize by for every frame.
    		startFrameZoomDistanceX = (startPosition.getReal() - complexStartPosition.getReal())/zoomInFrames;
	    	startFrameZoomDistanceY = (startPosition.getImaginary() - complexStartPosition.getImaginary())/zoomInFrames;
	    	endFrameZoomDistanceX = (endPosition.getReal() - complexEndPosition.getReal())/zoomInFrames;
	    	endFrameZoomDistanceY = (endPosition.getImaginary() - complexEndPosition.getImaginary())/zoomInFrames;
	    	
    	}
    	
    	/**
    	 * Trigger zoom event. This sets the new zooming frame and display it for 30 milliseconds.
    	 */
		@Override
		public void actionPerformed(ActionEvent e) {
			
			//count number of frames processed. 
	    	if(counter > 0){
		    	
		    	//Calculate the new starting and ending position by minimizing the size from the last frame
	        	complexStartPosition.setReal(complexStartPosition.getReal() + startFrameZoomDistanceX);
	        	complexStartPosition.setImaginary(complexStartPosition.getImaginary() + startFrameZoomDistanceY);
		        complexEndPosition.setReal(complexEndPosition.getReal() + endFrameZoomDistanceX);
		        complexEndPosition.setImaginary(complexEndPosition.getImaginary() + endFrameZoomDistanceY);
		        oldComplexStartPosition = complexStartPosition;
		        
		        //Calculate the length of both axis
		        xAxisLength = complexEndPosition.getReal() - complexStartPosition.getReal();
		        yAxisLength = complexEndPosition.getImaginary() - complexStartPosition.getImaginary();
		        
		        repaint();
		        
		        counter--;
	    	}
			
		}
    	
    }
     
    /**
     * Convert from pixel position to complex number
     * @param pixelPosition     Pixel position to be converted.
     * @return  A complex number for complex position.
     */
    public Complex getComplexPosition (Complex pixelPosition){
        Complex complexPosition = new Complex(0,0);
         
        //Convert user click from pixel measurement to complex number
        complexPosition.setReal((pixelPosition.getReal()/(IMAGE_WIDTH/this.xAxisLength)) + oldComplexStartPosition.getReal());
        complexPosition.setImaginary((pixelPosition.getImaginary()/(IMAGE_HEIGHT/this.yAxisLength)) + oldComplexStartPosition.getImaginary());
         
         
        return new Complex(complexPosition.getReal(),complexPosition.getImaginary());
    }
    
    
     
    /**
     * Draw Mandelbrot.
     */
    @Override
    public void paint(Graphics g) {
    	Graphics2D g2 = (Graphics2D) g;
        super.paint(g2);

        //g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
 
        iterationCounter = 0;
        for (int y = 0; y < IMAGE_HEIGHT; y++) {
            for (int x = 0; x < IMAGE_WIDTH; x++) {
                pixel.reset();
                 
                //Set current complex number
                c.setReal(complexStartPosition.getReal() + x/(IMAGE_WIDTH/xAxisLength));
                c.setImaginary(complexStartPosition.getImaginary() + y/(IMAGE_HEIGHT/yAxisLength));
                 
                iterationCounter = 0;
                                
                while(pixel.modulusSquared() < 4 && iterationCounter < MAX_ITERATION){
                    //Square the Z(i)
                    if(mode.equals("Mandelbrot")){
                        pixel = pixel.square();
                    }
                    if(mode.equals("Burning Ship")){
                        pixel = pixel.burningShipSquare();
                    }
                    if(mode.equals("Newton")){
                        pixel = pixel.newton();
                    }
                    //Add constant
                    pixel.add(c);
                    
                    //Orbit Trap equations
                    if(visual.equals("Line Orbit Trap")){
	                    if(Math.abs(pixel.getReal())<0.01) break;
	                    if(Math.abs(pixel.getImaginary())<0.01) break;
                    }else if(visual.equals("Circular Orbit Trap")){
	                    if(pixel.modulusSquared()<0.5*0.5) break;
                    }else if(visual.equals("Line Region Splits")){
	                    if(Math.abs(pixel.getReal())>0.8) break;
	                    if(Math.abs(pixel.getImaginary())>0.8) break;
                    }else if(visual.equals("Circular Region Splits")){
	                    if(pixel.modulusSquared()>0.5*0.5) break;
                    }
                    
                    iterationCounter++;
                }
                 
                iterationCounter = iterationCounter*(765/MAX_ITERATION);
                 
                if(iterationCounter < 255){
                    color = new Color(iterationCounter,0,0);
                }else if(iterationCounter < 510){
                    color = new Color(255,iterationCounter % 255,0);
                }else if(iterationCounter < 765){
                    color = new Color(255,255,iterationCounter%255);
                }else{
                    color = new Color(255,255,255);
                }
                
                
                //color = Color.getHSBColor((float)iterationCounter / 30,  1.0f, 0.8f);
                
                rgb = color.getRGB();
                image.setRGB(x, y, rgb);
            }
        }
         
        g2.drawImage(image, 0, 0, this);
    }
 
}