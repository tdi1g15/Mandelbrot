import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
 

import javax.swing.JPanel;
  
public class Julia extends JPanel {
     

	private BufferedImage image;
    private final int MAX_ITERATION = 560;
    private final int IMAGE_WIDTH = 600;
    private final int IMAGE_HEIGHT = 600;
    private Complex centerPoint;
    private Complex c;
    private Complex pixel;
     
    private double xAxisLength;
    private double yAxisLength;
    private double xResize;
    private double yResize;
     
    private int iterationCounter;
    
    private String mode;
     
    private Color color;
    private int rgb;
     
    public Julia(double xAxisLength,double yAxisLength){
        //super("Mandelbrot Set");
        //setBounds(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);
        //setResizable(false);
        //setDefaultCloseOperation(EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(IMAGE_WIDTH, IMAGE_HEIGHT));
         
        this.xAxisLength = xAxisLength;
        this.yAxisLength = yAxisLength;
         
        mode = "Mandelbrot";
        image = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
         
        pixel = new Complex(0,0);
        c = new Complex(0,0);
         
        centerPoint = new Complex(IMAGE_WIDTH/2, IMAGE_HEIGHT/2);

    }
     
    /**
     * Set the new constant Complex number to build Julia Fractal.
     * @param c		Complex Number.
     */
    public void setC(Complex c){
        this.c.setReal(c.getReal());
        this.c.setImaginary(c.getImaginary());
    }
     
    /**
     * Get Current complex number of the Julia Fractal.
     * @return	The current complex number.
     */
    public Complex getComplex(){
        return new Complex(c.getReal(),c.getImaginary());
    }
    
    /**
     * Change calculation mode.
     * @param mode	The name of the calculation mode.
     */
    public void changeMode(String mode){
        this.mode = mode;
    }
    
    /**
     * Paint the Julia Fractal.
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);
         
        xResize = (int)Math.round(IMAGE_WIDTH/xAxisLength);
        yResize = (int)Math.round(IMAGE_HEIGHT/yAxisLength);
         
        iterationCounter = 0;
         
        for (int y = 0; y < IMAGE_HEIGHT; y++) {
             
            for (int x = 0; x < IMAGE_WIDTH; x++) {
                pixel.reset();
                 
                pixel.setReal((x - (centerPoint.getReal()))/xResize);
                pixel.setImaginary((y - (centerPoint.getImaginary()))/yResize);
                 
                iterationCounter = 0;
                while(pixel.modulusSquared() < 4 && iterationCounter < MAX_ITERATION){
                	if(mode.equals("Mandelbrot")){
                        pixel = pixel.square();
                    }
                	if(mode.equals("Burning Ship")){
                        pixel = pixel.burningShipSquare();
                    }
                	if(mode.equals("Newton")){
                        pixel = pixel.newton();
                    }
                    pixel.add(c);
                    iterationCounter++;
                }
                
                color = Color.getHSBColor((float)iterationCounter / 1000,  1.0f, 0.8f);
                rgb = color.getRGB();
                image.setRGB(x, y, rgb);
            }
        }
         
         
        g.drawImage(image, 0, 0, this);
    }

 
}