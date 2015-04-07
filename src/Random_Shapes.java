
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;

public class Random_Shapes
{
	public JFrame myWindow;
	public ShapeCanvas rs;
	Polygon newShape;
	
	public static void main(String[] args)
	{
		Random_Shapes p = new Random_Shapes();
		p.gui();
	}
	
	
	
	public void gui()
	{
		myWindow = new JFrame("Enemy Evolver");
		myWindow.setSize(600, 600);
		myWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Enemy lastEnemy = new Enemy();
		// generation
		for(int i = 0; i < 100; i++){
			Enemy highest = new Enemy(lastEnemy);
			int highestScore = highest.fitnessFunc();
			// spawn enemies / population
			for(int j = 1; j < 10; j++){
				Enemy childEnemy = new Enemy(lastEnemy);
				childEnemy.fitnessFunc();
				if(highestScore < childEnemy.fitnessFunc()){
					highest = childEnemy;
					highestScore = childEnemy.fitnessFunc();
				}
			}
			lastEnemy = highest;
		}
		
		lastEnemy.drawPoly();

		myWindow.setVisible(true);

	}
	
	@SuppressWarnings("serial")
	class ShapeCanvas extends JComponent
	{
		private BufferedImage outputImage;
		private Shape shape;

		public ShapeCanvas(Shape s)
		{
			outputImage = null;
			this.shape = s;
		}
		
		@Override
		public void paintComponent(Graphics g)
		{
			this.draw(g);
		}

		public void draw(Graphics g)
		{
			// get the graphics for drawing to the screen
			Graphics2D g2d = (Graphics2D)g;
				
			// get the graphics for drawing to a buffer
			outputImage = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2dOutput = (Graphics2D)outputImage.getGraphics();
				
			int width = this.getWidth();
			int height = this.getHeight();

			// fill the background to start
			g2d.setColor(Color.white);
			g2d.fillRect(0, 0, width, height);
			g2dOutput.setColor(Color.white);
			g2dOutput.fillRect(0, 0, width, height);
			
			// set the draw color to random list of colors
			g2d.setColor(Color.BLACK);
			g2dOutput.setColor(Color.BLACK);
			
			

			// draw the shape
			g2d.draw(shape);
			g2dOutput.draw(shape);
			rs.save("ship2.png");

		}
		
		public void save(String filename)
		{
			if(outputImage != null)
			{
				try
				{
					ImageIO.write(outputImage, "png", new File(filename));
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
				
		}
	}

	class Enemy {

		List<Point> listPoints = new ArrayList<>();
		Point newPoint;
		private Polygon polygon = new Polygon();
		private int score = 0;

		public Enemy() {
			for (int i = 0; i < 6; i++) {
				int x = (int)Math.floor(Math.random() * 600);
				int y = (int)Math.floor(Math.random() * 600);
				newPoint = new Point(x, y);
				listPoints.add(newPoint);
			}
		}
		
		public Enemy(Enemy parent){

			for(int i = 0; i < parent.listPoints.size(); i++){
				listPoints.add((Point)parent.listPoints.get(i).clone()); //deep copies
			}
		
			for(int i = 0; i < parent.listPoints.size(); i++){
				listPoints.get(i).x += (int)Math.floor(Math.random() * 10.0 - 5.0);
				listPoints.get(i).y += (int)Math.floor(Math.random() * 10.0 - 5.0);
				listPoints.get(i).x = Math.min(600, Math.max(300, listPoints.get(i).x));
				listPoints.get(i).y = Math.min(600, Math.max(300, listPoints.get(i).y));
			}
		}
		
		public void drawPoly(){
			for(Point p : listPoints){
				polygon.addPoint(p.y, p.x);
			}
			rs = new ShapeCanvas(polygon);
			myWindow.add(rs);
		}
		
		public int fitnessFunc(){
			score = 0;
			
			Point leftTip = new Point(10, 5);
			Point bottomLeft = new Point(100, 50);
			Point middle = new Point(300, 5);
			Point bottomRight = new Point(450, 50);
			Point topRight = new Point(600, 100);

			score -= (int)listPoints.get(0).distance(leftTip);
			score -= (int)listPoints.get(1).distance(bottomLeft);
			score -= (int)listPoints.get(2).distance(middle);
			score -= (int)listPoints.get(3).distance(bottomRight);
			score -= (int)listPoints.get(4).distance(topRight);

			return score;
		}
	}

}
