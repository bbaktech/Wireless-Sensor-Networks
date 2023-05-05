//WSNSimulation.java
//ClusterHead
import java.io.*;
import java.util.*;
import java.util.List;
import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;

public class WSNSimulation extends JFrame {
	
	static List <SensoreNode> wsnnodes = new LinkedList <SensoreNode> ();
	static List <SensoreNode> CHnodes = new LinkedList <SensoreNode> ();	
	static WSNSimulation simFrame;
	static WSNBaseStation bs;
	
	JPanel DrawingAria;
	int BS_ID;
	String output;
	
	WSNSimulation() {
			setSize(new Dimension(900, 900));
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setVisible(true);	
			DrawingAria = new JPanel() {
				@Override
				public void paintComponent(Graphics g) {
					Graphics2D g2 = (Graphics2D) g;
//base station 
					Shape roundRect = new Rectangle(2, 2, 25,25);
					g2.setColor(Color.CYAN);
					g2.draw(roundRect);
					g2.fill(roundRect);
					g2.drawString("BaseStation", 25, 25);
					
					for (int i=0; i< CHnodes.size(); i++) {
						SensoreNode ch = CHnodes.get(i);
						if (ch != null && ch.battary_strenth > 0 && config.WITH_IN_DIST > ch.distFromBS()){
							g2.setColor(Color.GRAY);
							g2.drawLine(ch.xposition,ch.yposition, 0,0);
						}
					}
					
					for (int i=0; i< config.NO_NODES; i++) {						
						SensoreNode sn1 =  wsnnodes.get(i);
						SensoreNode ch = CHnodes.get(sn1.CH_ID);
						
						if (sn1.battary_strenth > 0) {
							g2.setColor(Color.GRAY);
							//line is the path to RN (relay node or BS)
							if (sn1.line !=null) g2.drawLine(sn1.line.x1,sn1.line.y1,sn1.line.x2,sn1.line.y2 );
							g2.setColor(Color.blue);
							//connection line to CH
							g2.drawLine(sn1.xposition,sn1.yposition, ch.xposition,ch.yposition);
							
							if (sn1.CH){				
								Shape rect = new Rectangle(sn1.xposition,sn1.yposition, 20, 20);
								g2.draw(rect);
								if (sn1.battary_strenth >config.MIN_POWER)  g2.setColor(Color.blue);
								else  g2.setColor(Color.YELLOW);
						        g2.fill(rect);
						        g2.drawString(Integer.toString(sn1.SN_ID) , sn1.xposition, sn1.yposition);
							} else {
								Shape circle = new Ellipse2D.Double(sn1.xposition,sn1.yposition, 10, 10);
								g2.draw(circle);						
								if (sn1.battary_strenth >config.MIN_POWER)  g2.setColor(Color.blue);
								else  g2.setColor(Color.YELLOW);
						        g2.fill(circle);
							}
						}
						else {
							Shape rect = new Ellipse2D.Double(sn1.xposition,sn1.yposition, 10, 10);
							g2.draw(rect);
							g2.setColor(Color.RED);
					        g2.fill(rect);
					        g2.drawString(Integer.toString(sn1.SN_ID) , sn1.xposition, sn1.yposition);							
						}
					}
				
				}
			};
			
			setTitle("WSNs Simulation - Slot No:0");
			this.getContentPane().add(DrawingAria);
	}
	
	public static void InitaliseNodes() {
		Random rand = new Random();
        SensoreNode node = null;
        int nodeid = 0;
		for (int i = 0; i< config.NO_TYPE1_NODES; i++ ) {		
	        int x = rand.nextInt(config.X_RANGE);		
	        int y = rand.nextInt(config.Y_RANGE);	        
	        x = x+50;
	        y = y+50;
	        node =  new SensoreNode(nodeid,config.MAX_POWER,x,y);
	        nodeid++;
	        wsnnodes.add(node);
		}	
		for (int i = 0; i< config.NO_TYPE2_NODES; i++ ) {		
	        int x = rand.nextInt(config.X_RANGE);		
	        int y = rand.nextInt(config.Y_RANGE);	        
	        x = x+50;
	        y = y+50;
	        node = null;
	        node =  new SensoreNode(nodeid,config.MIN_POWER,x,y);
	        nodeid++;
	        wsnnodes.add(node);
		}
	}

	public static void main(String[] args) {
		System.out.println("WSNs Simulation Started...");
		bs = new WSNBaseStation();
		InitaliseNodes();
		CreateClusters();
		simFrame = new WSNSimulation();
		StartSimulation();
	}
		
	private static void CreateClusters() {
		// TODO Auto-generated method stub	

		for (int i=0;i< config.NO_NODES;i++) {
			SensoreNode node =  wsnnodes.get(i);
			int x = node.xposition;
			int y = node.yposition;
			
			if (x <200 &&  y>500 ) {
				node.CH_ID = 0;
			} else if (x <200 &&  y>200 ) {
				node.CH_ID = 1;
			} else 	if (x <200 ) {
				node.CH_ID = 2;				
			} else if (x <500 &&  y>500 ) {
				node.CH_ID = 3;
			} else if (x <500 &&  y>200 ) {
				node.CH_ID = 4;
			} else 	if (x <500 ) {
				node.CH_ID = 5;
			}			
			else if ( y>500 ) {
				node.CH_ID = 6;
			} else if ( y>200 ) {
				node.CH_ID = 7;
			} else 	 {
				node.CH_ID = 8;
			}						
		}
		//select the cluster head
		for (int i=0; i< config.NO_CLUSTERS; i++) {
			SensoreNode ch = SelectClusterHead( i );
			if (ch != null && ch.battary_strenth >0) {
				ch.CH = true;
				CHnodes.add( ch);
			}			
		}		
	}

	
	static SensoreNode SelectClusterHead (int CID) {
		int maxpower = -100;
		SensoreNode sn = null;// wsnnodes.get(0);
		SensoreNode sn1 =  wsnnodes.get(0);
		for (int i=0; i< config.NO_NODES; i++) {
			sn1 =  wsnnodes.get(i);
			//cluster head selected based on power strength
			if ((CID == sn1.CH_ID) && ( sn1.battary_strenth > maxpower) ){				
					sn = sn1;
					maxpower = sn1.battary_strenth;
			}	
		}
		return sn;
	}

	private static void StartSimulation() {

		int Slot = 0;
		
		System.out.println("No of Nodes:"+config.NO_NODES+ "  Total Power:"+config.TOTAL_POWER);
		
		while (Slot < config.NO_SLOTS) {
			
			//one Unit Time for each slot
			for (int i=0; i< config.NO_NODES; i++) {			
		        wsnnodes.get(i).SendSensedData(Slot);			       
		    }	
			
			for (int i=0;i< CHnodes.size();i++) {
				CHnodes.get(i).ForwardAgregatedDataToBS();
			}		
			
			simFrame.setTitle("WSNs Simulation - Slot No:"+Slot);
			simFrame.invalidate();
			simFrame.validate();
			simFrame.repaint();
						
			int energy_balance = 0;
			int no_nodes_dried  = 0;
					
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			for (int i=0; i< config.NO_NODES; i++) {			
				energy_balance = energy_balance + wsnnodes.get(i).battary_strenth;	
				if ( wsnnodes.get(i).battary_strenth <= 0) no_nodes_dried++;
		    }	
			bs.addpoint(Slot, no_nodes_dried);
			System.out.println("SlotNo:"+Slot+"  RemainingPower:" + energy_balance + "   PowerUtilisd:"+ (config.TOTAL_POWER - energy_balance) +" Dried Nodes:"+no_nodes_dried);
			Slot++;
		}
		bs.DrawGraph();
	
	}
} 
