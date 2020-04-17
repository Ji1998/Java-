//Guangsen Ji @03/20/2020  Lab10 
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;




public class RefreshingGraphPanel extends JPanel implements MouseListener
{
	private String              expression;
	private double[]            xValues;
	private double[]            yValues;
	private CalculatorInterface calculator;
	private JFrame              graphWindow;
	private String[]            xValuesStrings;
	private int[]     	       xClickOffsets; 
	private JFrame     xyWindow    = new JFrame();
	private JPanel     xyPanel     = new JPanel();
	private JTextField xTextField  = new JTextField();
	private JTextField yTextField  = new JTextField();
	   






	public RefreshingGraphPanel(String expression,double[]xValues, double[]yValues, CalculatorInterface calculator, JFrame graphWindow)
	{

		this.expression = expression;
		this.xValues    = xValues;
		this.yValues    = yValues;
		this.calculator = calculator;
		this.graphWindow= graphWindow;

		xClickOffsets = new int [xValues.length]; 
		xValuesStrings = new String[xValues.length];
		for (int i = 0; i < xValuesStrings.length; i++)
			xValuesStrings[i] = String.valueOf(xValues[i]);
		addMouseListener(this);
		
		
		 // Build the tiny x,y values display window (but don't show it here!)
		 // This window will be REUSED (OPENED in mousePressed() and CLOSED in mouseReleased()).
		 xTextField.setHorizontalAlignment(SwingConstants.LEFT);
		 yTextField.setHorizontalAlignment(SwingConstants.LEFT);
		 xyPanel.setLayout(new GridLayout(2,1)); // rows. columns
		 xyPanel.add(xTextField);
		 xyPanel.add(yTextField);
		 xyWindow.getContentPane().add(xyPanel,"Center");
		 xyWindow.setSize(100,75);
		 // setLocation() and setVisible() will be done when the mouse is clicked. 
		
		
		
		



	}

	@Override
	public void paint(Graphics g)
	{
		int windowWidth  = getSize().width; //getSize() of "this" JPanel in pixels
		int windowHeight = getSize().height;//these methods are in the JPanel class.
		int margin         = 50;
		int xAxisZeroPoint = 0;
		int xAxisXstart    = margin;
		int xAxisXstop     = windowWidth - margin;
		int xAxisYlocation = windowHeight- margin; 
		int xAxisLength    = windowWidth - (2*margin);
		int adjustIndex    = -1;
		int pixelsToAdd    = 0;
		int xClickBump     = xAxisLength/(xValues.length-1); 
		int xClickOffset   = xAxisXstart;
		xClickOffsets= new int[xValues.length]; 
		int yAxisYstart    = windowHeight - margin;
		int yAxisYstop     = margin;
		
		int yAxisXlocation = margin; 
		int yAxisLength    = windowHeight - (2*margin);
		int yClickOffset   = yAxisYstart;
		System.out.println("drawing "+expression+" in "+windowWidth+" by "+windowHeight+" window.");
		// SHOW the EXPRESSION at the TOP
		g.setColor(Color.RED);
		g.setFont(new Font("Times Roman", Font.BOLD, 20)); // medium size
		g.drawString(expression, windowWidth/2-20, 20);				//(x,y)


		double smallestY = Double.MAX_VALUE;
		double biggestY  = -Double.MAX_VALUE; // not Double.MIN_VALUE !
		for (double y : yValues)
		{
			if (y < smallestY) smallestY = y;
			if (y > biggestY)  biggestY  = y;
		}
		String yTopScale    = "Y = " + String.valueOf(biggestY);
		String yBottomScale = "Y = " + String.valueOf(smallestY);


		int yAxisZeroPoint = yAxisYstart;
		if ((smallestY < 0) && (biggestY > 0))
		{
			double percentToYzeroPoint = Math.abs(smallestY)/(Math.abs(smallestY) + Math.abs(biggestY));
			int pixelsToSubtract = (int) (percentToYzeroPoint * yAxisLength); 
			yAxisZeroPoint -= pixelsToSubtract;
			System.out.println("Window height is " + windowHeight + " yAxisZeroPoint is at y = " + yAxisZeroPoint);
		}
		
		g.setColor(Color.BLACK);
		g.setFont(new Font("Times Roman", Font.BOLD, 20)); // medium
		
		
	//	g.drawLine(xAxisXstart, xAxisYlocation,  // from left (x,y) 
	//	           xAxisXstop,  xAxisYlocation); // to right (x,y)
	//	g.drawString("X", xAxisXstop+10, xAxisYlocation ); // String, x, y
		   
		if(yAxisZeroPoint == yAxisYstart)
		{g.drawLine(xAxisXstart, xAxisYlocation, xAxisXstop, xAxisYlocation);
			g.drawString("X",xAxisXstop+10,xAxisYlocation);
			
			
			xClickOffset = xAxisXstart;
			for (int i = 0; i < xValuesStrings.length; i++)
			    {
			    xClickOffsets[i] = xClickOffset; // save offsets for later graphing!
			    g.drawString("|", xClickOffset, xAxisYlocation+5);//down a little
			    g.drawString(xValuesStrings[i], xClickOffset-5, xAxisYlocation+25);//down a little
			    xClickOffset += xClickBump;
			    }
			
		}
		else 
		{
			g.drawLine(xAxisXstart,yAxisZeroPoint,xAxisXstop,yAxisZeroPoint);
			g.drawString("X",xAxisXstop+10,yAxisZeroPoint);
			
			
			

			xClickOffset = xAxisXstart;
			for (int i = 0; i < xValuesStrings.length; i++)
			    {
			    xClickOffsets[i] = xClickOffset; // save offsets for later graphing!
			    g.drawString("|", xClickOffset, yAxisZeroPoint+5);//down a little
			    g.drawString(xValuesStrings[i], xClickOffset-5, yAxisZeroPoint+25);//down a little
			    xClickOffset += xClickBump;
			    }
			
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
	

		
		
		if ((xValues[0] < 0) && (xValues[10] > 0)) // determine if there IS a zero point to find!
		   { 
		   // find the x offset where the xValue changes from - to +
			xClickOffset = xAxisXstart;
		   int i;
		   for (i=0; i < xValues.length; i++)
		       {
		       if ((xValues[i]<=0) && (xValues[i+1]>=0)) break;
		       xClickOffset += xClickBump;
		       }
		    adjustIndex = i;
		    double percentToXzeroPoint = Math.abs(xValues[i])/(Math.abs(xValues[i]) + Math.abs(xValues[i+1]));
		    pixelsToAdd = (int) (percentToXzeroPoint * xClickBump); 
		    xAxisZeroPoint = xClickOffset + pixelsToAdd;
		    System.out.println("Window width is " + windowWidth + " xAxisZeroPoint is at x = " + xAxisZeroPoint);
		    } 
		    
		double smallestYY = Double.MAX_VALUE;
		double biggestYY  = -Double.MAX_VALUE; // not Double.MIN_VALUE !
		for (double y : yValues)
		    {
		    if (y < smallestYY) smallestYY = y;
		    if (y > biggestYY)  biggestYY  = y;
		    }
		String yTopScalee    = "Y = " + String.valueOf(biggestY);
		String yBottomScalee = "Y = " + String.valueOf(smallestY);
		
 
		g.setColor(Color.BLACK);
		g.setFont(new Font("Times Roman", Font.BOLD, 20)); // medium
		if (xAxisZeroPoint == 0) // draw Y axis at left margin
		   {
		   g.drawLine(yAxisXlocation, yAxisYstart, // from lower (x,y) 
		              yAxisXlocation, yAxisYstop); // to upper (x,y)
		   g.drawString(yTopScale,    xAxisXstart+10, yAxisYstop + 10); // String
		   g.drawString(yBottomScale, xAxisXstart+10, windowHeight);    // at (x,y)
		   }
		 else // x axis has a 0 point, so draw Y axis at the x=0 point
		   { 
		   g.drawLine(xAxisZeroPoint, yAxisYstart,
		              xAxisZeroPoint, yAxisYstop);
		   g.drawString(yTopScale,    xAxisZeroPoint, yAxisYstop+10);
		   g.drawString(yBottomScale, xAxisZeroPoint, yAxisYstart);
		   }
		   
		// Convert yValues to y print coordinates (pixels).
		int[]  yPixelOffsets = new int[yValues.length];
		double valueRange  = biggestY - smallestY;
		int    pixelRange  = yAxisYstart - yAxisYstop;

		for (int i = 0; i < yValues.length; i++)
		    {
		    double valuePercent = (yValues[i]-smallestY)/valueRange;
		    int    pixelOffset  = (int)(pixelRange * valuePercent);
		    yPixelOffsets[i] = yAxisYstart - pixelOffset;
		    System.out.println("xValue is "       + xValuesStrings[i] 
		                    + " xPixelOffset is " + xClickOffsets[i]);
		    System.out.println("yValue is "       + yValues[i] 
		                    + " yPixelOffset is " + yPixelOffsets[i]);
		    }
		g.setColor(Color.red); // setColor for drawing the graph lines
		for (int i = 0; i < yPixelOffsets.length; i++)
		    {
		    g.drawOval(xClickOffsets[i]-2, yPixelOffsets[i]-2, 4,4); // tiny 4x4 circle	  
		    // Connect the dots! Draw lines between the points
		    if (i>0) g.drawLine(xClickOffsets[i-1],yPixelOffsets[i-1],
			                xClickOffsets[i],  yPixelOffsets[i]);
		    } 
		int[]  yPixelOffsetss = new int[yValues.length];
		double valueRangee  = biggestY - smallestY;
		int    pixelRangee  = yAxisYstart - yAxisYstop;

		for (int i = 0; i < yValues.length; i++)
		    {
		    double valuePercent = (yValues[i]-smallestY)/valueRangee;
		    int    pixelOffset  = (int)(pixelRangee * valuePercent);
		    yPixelOffsetss[i] = yAxisYstart - pixelOffset;
		    System.out.println("xValue is "       + xValuesStrings[i] 
		                    + " xPixelOffset is " + xClickOffsets[i]);
		    System.out.println("yValue is "       + yValues[i] 
		                    + " yPixelOffset is " + yPixelOffsets[i]);
		    }
		    
		  
		g.setColor(Color.red); // setColor for drawing the graph lines
		for (int i = 0; i < yPixelOffsets.length; i++)
		    {
		    g.drawOval(xClickOffsets[i]-2, yPixelOffsets[i]-2, 4,4); // tiny 4x4 circle	  
		    // Connect the dots! Draw lines between the points
		    if (i>0) g.drawLine(xClickOffsets[i-1],yPixelOffsets[i-1],
			                xClickOffsets[i],  yPixelOffsets[i]);
		    } 
		    
		
		
		
		
		
		
		

	}





	@Override
	public void mouseClicked(MouseEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent me)
	{
		
		int xLocalPixelLocation = me.getX();
		  int yLocalPixelLocation = me.getY();
		  System.out.println("Mouse pressed at local pixel location " + xLocalPixelLocation + "," + yLocalPixelLocation);
		  int graphWindowXlocation= (int) graphWindow.getLocation().getX();
		  int graphWindowYlocation= (int) graphWindow.getLocation().getY();
		  System.out.println("GraphWindow is at screen location " + graphWindowXlocation + "," + graphWindowYlocation);
		  int xScreenPixelLocation   = graphWindowXlocation + xLocalPixelLocation;
		  int yScreenPixelLocation   = graphWindowYlocation + yLocalPixelLocation;
		  System.out.println("Net screen x,y  pixel location of mouse click is " + xScreenPixelLocation + "," + yScreenPixelLocation);
		  
		  
		  
		  // show mini x,y display window
		  xyWindow.setLocation(xScreenPixelLocation, yScreenPixelLocation);
		  xyWindow.setVisible(true);

		  // set the x,y values to be displayed in the xyWindow
		  xTextField.setText("X = " + String.valueOf(xScreenPixelLocation));
		  yTextField.setText("Y = " + String.valueOf(yScreenPixelLocation));
		  
		  double xPixelRange = xClickOffsets[xValues.length-1] - xClickOffsets[0];   
		  double xPixelValue = xLocalPixelLocation - xClickOffsets[0]; // - bottomXpixel;
		  double xPercent = xPixelValue/xPixelRange;
		  double xExpressionRange = xValues[xValues.length-1] - xValues[0];    
		  double xValue = xValues[0] + (xPercent * xExpressionRange) ;
		  
		  System.out.println(" xPixelRange="               + xPixelRange 
	                 + " xPixelValue="               + xPixelValue
	                 + " xPixelValue%OfxPixelRange=" + xPercent 
	                 + " xExpressionRange= "         + xExpressionRange
	                 + " xValue="                    + xValue);
		  System.out.println(expression);
		  String yValueString = calculator.calculate(expression,xValue);
		  String xValueString = String.valueOf(xValue);
		  
		// Restrict the decimal precision of displayed x & y values to 4 decimal places
		  int xDecimalOffset = xValueString.indexOf(".");
		  if (xDecimalOffset >= 0) // found a decimal point
		     {
		     String xWholePart   = xValueString.substring(0,xDecimalOffset);
		     String xDecimalPart = xValueString.substring(xDecimalOffset+1);
		     if (xDecimalPart.length() > 4)
		         xDecimalPart = xDecimalPart.substring(0,4);//drop after 4th digit
		     xValueString = xWholePart + "." + xDecimalPart;
		     }
		  int yDecimalOffset = yValueString.indexOf(".");
		  if (yDecimalOffset >= 0) // found a decimal point
		     {
		     String yWholePart   = yValueString.substring(0,yDecimalOffset);
		     String yDecimalPart = yValueString.substring(yDecimalOffset+1);
		     if (yDecimalPart.length() > 4)
		         yDecimalPart = yDecimalPart.substring(0,4);//drop after 4th digit
		     yValueString = yWholePart + "." + yDecimalPart;
		     }
		      
  
		  
		  
		  
		  
		  xTextField.setText("X = " + xValueString);
		  yTextField.setText("Y = " + yValueString);
		  
	  
		  
		
	
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		System.out.println("mouse released");
		 xyWindow.setVisible(false);

	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e)
	{
		// TODO Auto-generated method stub

	}

}
