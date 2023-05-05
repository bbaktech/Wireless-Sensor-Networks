//WSNBaseStation
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.io.*;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class WSNBaseStation {
	
	String BS_ID;
	String output;
	int x,y;
	static String fileName = "BSReceivedData.txt";
	static String fileName1 = "BSGraphData.txt";
	
	FileWriter theFile;
//	int UtilisedPower;
	class Point {
		int slot_no;
		int no_livenodes;
	}
	static List <Point> points = new LinkedList <Point> ();
	
    JFrame testFrame;
	JPanel DrawingAria;
	
	WSNBaseStation(){
		
		BS_ID = "BS";
		x =0; y=0;

		try {
			BufferedWriter out = new BufferedWriter(
	                new FileWriter(fileName));			
			 out.write("WSN Base Station Started...\n\nNo of Nodes:"+config.NO_NODES+ " TotalPower:"+config.TOTAL_POWER + " NoSlots:" +config.NO_SLOTS+ "\n");
			 out.close();
		}
        catch (IOException e) {
            // Display message when error occurs
            System.out.println("Exception Occurred" + e);
        }
	}
	
	void ReceivedData(String s){
	       FileOutputStream objectOut = null;
	       appendStrToFile(s + "\n");
	}

	public static void appendStrToFile( String str)
	{
		try {	
			BufferedWriter out = new BufferedWriter(new FileWriter(fileName, true));
			out.write(str);
			out.close();
		}
		catch (IOException e) {	
		// Display message when exception occurs
			System.out.println("exception occurred" + e);
		}
	}
	void addpoint(int slotno,int no_deadnodes) {
		Point p = new Point();
		p.slot_no = slotno;
		p.no_livenodes = (config.NO_NODES -no_deadnodes);
		points.add(p);		
	}
	
	public void DrawGraph() {
		// TODO Auto-generated method stub

		try {
			BufferedWriter out = new BufferedWriter(
	                new FileWriter(fileName1));
			//list of live Nodes in each iteration
			for (int i= 0; i< points.size(); i++) {
				String s = points.get(i).slot_no +","+ points.get(i).no_livenodes +"\n";
				out.write(s); 
			}
			out.close();
		}
        catch (IOException e) {
            // Display message when error occurs
            System.out.println("Exception Occurred" + e);
        }

        testFrame = new JFrame();
        testFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        DrawingAria = new JPanel () {
			@Override
			public void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g;
				for (int i=0; i< points.size(); i++) {
					Shape circle = new Ellipse2D.Double(points.get(i).slot_no ,config.NO_NODES-points.get(i).no_livenodes, 1, 1);
					g2.draw(circle);						
					g2.setColor(Color.RED);
					g2.fill(circle);
				}
			}
		};
		testFrame.setTitle("Slot No Vs Living Nodes");
        testFrame.add(DrawingAria);
        Dimension d = new Dimension(config.NO_SLOTS + 10, config.NO_NODES+10);
        testFrame.setSize(d);
        testFrame.setVisible(true);	
	}
}
