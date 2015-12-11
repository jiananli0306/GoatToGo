package gtg_view_subsystem;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JScrollPane;

/**
 */
public class ResultMapDisplayPanel extends MapDisplayPanel{
	private Image locationImage, locationEndImage;
	private String map;
	private ResultPage parent;
	private ArrayList<Point> pathPoints = new ArrayList<Point>();
	private int circleWidthHeight = 10;
	/**
	 * Create the panel.
	
	
	 * @param parent ResultPage
	 * @param mapPanelHolder JScrollPane
	 * @param mapName String
	 * @param mapurl String
	 */
	public ResultMapDisplayPanel(ResultPage parent, JScrollPane mapPanelHolder, String mapName, String mapurl) {
		super(mapPanelHolder, mapurl);
		this.parent = parent;
		this.map = mapName;

		super.loadImage(mapurl);
		this.loadLocationImage();
		this.loadLocationEndImage();
	}
	    
	/**
	 * Method paintComponent.
	 * @param g Graphics
	 */
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D)g;
        for(int i= 0; i < pathPoints.size();i++){
        	Point p = pathPoints.get(i);
        	if(i == 0){
        		g2.drawImage(this.locationImage, (int)p.getX() - 10, (int)p.getY() - 25, 20, 25, null);
        	} else if(i == pathPoints.size() - 1){
        		g2.drawImage(this.locationEndImage, (int)p.getX() - 10, (int)p.getY() - 25, 20, 25, null);
        	}
        	
        	int j = i+1;
        	if(j < pathPoints.size()){
        		Point q = pathPoints.get(j);
        		g2.setColor(Color.RED);
        		Stroke stroke = new BasicStroke(3f);
        		g2.setStroke(stroke);
        		g2.drawLine((int)p.getX(), (int)p.getY(), (int)q.getX(), (int)q.getY());
        	}
        }
	}

	/**
	 * Method loadLocationImage.
	 * @param none
	 * Loads the image used for displaying start location on the map image
	 */
	public void loadLocationImage() {
        try {
            this.locationImage = ImageIO.read(new File(ImageURLS.LOCATION_IMAGE));
        }
        catch(MalformedURLException mue) {
            System.out.println("URL trouble: " + mue.getMessage());
        }
        catch(IOException ioe) {
        	System.out.println("read trouble: " + ioe.getMessage());
        }
	}

	/**
	 * Method loadLocationEndImage.
	 * @param none
	 * Loads the image used for displaying end location on the map image
	 */
	public void loadLocationEndImage() {
        try {
            this.locationEndImage = ImageIO.read(new File(ImageURLS.LOCATION_END_ICON));
        }
        catch(MalformedURLException mue) {
            System.out.println("URL trouble: " + mue.getMessage());
        }
        catch(IOException ioe) {
        	System.out.println("read trouble: " + ioe.getMessage());
        }
	}

	/**
	 * Method displayPoints.
	 * @param arrayOfPoints ArrayList<Point>
	 */
	public void displayPoints(ArrayList<Point> arrayOfPoints) {
		System.out.println("inside display Points");
		// TODO Auto-generated method stub
		pathPoints = arrayOfPoints;
		revalidate();
		repaint();
		
	}
}
