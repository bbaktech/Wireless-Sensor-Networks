//SensoreNode.java
import java.lang.*;
//WSNBaseStation
import java.util.LinkedList;
import java.util.List;

public class SensoreNode {
	
	class Line {
		int x1,x2,y1,y2;
		boolean draw;
		Line(){
			x1 = 0; x2 =0;y1 =0;y2=0;draw = false;
		}
	}

	//these variables used if node selected as CH
	String AggregatedData;	
    Line line;
	int CH_ID;
	boolean CH;
	
	int SN_ID;
	int battary_strenth;
	int xposition ,yposition;
	String value;
	int deadATslot ;
	
	SensoreNode(int id, int batary, int xpos,int ypos){
		 SN_ID = id;
		 battary_strenth = batary;
		 xposition = xpos;
		 yposition = ypos;
		 value = null;
		 CH_ID= 0;
		 CH = false;
		 line = null;
		 deadATslot = -1;
		 AggregatedData = "";

	}
	
	void printStatus() {
		System.out.println("ID:"+ SN_ID+ " Cluster:"+ CH_ID+ "DEAD at Slot:"+ deadATslot);
	}
	
	void SetAsCH(){
		CH = true;
	}
	void SendSensedData(int slot) {
		if (battary_strenth>0) {
			deadATslot = slot;
			battary_strenth--;
			String s= "[NodeId:"+ SN_ID+ " Slot:"+ slot + " Power:"+ battary_strenth+"] ";
			WSNSimulation.CHnodes.get(CH_ID).AgregateReceivedData(s);
		}
//		else System.out.println("Node not Active:" + SN_ID + " Dead at slot:"+ deadATslot);
	}
	
	void ForwardReceivedDataToBS(String s) {
		//one Unit for Relay Transmit
		if (battary_strenth > 0) {	
			battary_strenth=battary_strenth - 1;
			WSNSimulation.bs.ReceivedData("RN_ID:"+ SN_ID + s);
		} else System.out.println("Packet Droped at RN Node ID:" + SN_ID + " Dead at slot:"+ deadATslot);
	}
	
	void ForwardAgregatedDataToBS(){
		//two units for Agregate and Transmit
		
		if (battary_strenth>0) {		
			battary_strenth = battary_strenth - 2;
			line = new Line();
			line.draw = true;
			line.x1 = this.xposition;
			line.y1 = this.yposition;
				
			double bsd = distFromBS();
			if (bsd < config.WITH_IN_DIST) {
				line.x2 = WSNSimulation.bs.x;
				line.y2 = WSNSimulation.bs.y;			
				WSNSimulation.bs.ReceivedData("(CH_ID:"+ SN_ID+")" + "[Cluster no:"+ CH_ID +"]" + AggregatedData );
			} else {
				for (int i=0; i< WSNSimulation.CHnodes.size() ;i++) {
					double hub = distFrom(WSNSimulation.CHnodes.get(i));
					if (hub >0 && hub <config.WITH_IN_DIST && WSNSimulation.CHnodes.get(i).battary_strenth >0) {
						if (xposition > WSNSimulation.CHnodes.get(i).xposition && yposition > WSNSimulation.CHnodes.get(i).yposition ) {						
							line.x2 = WSNSimulation.CHnodes.get(i).xposition;
							line.y2 = WSNSimulation.CHnodes.get(i).yposition;			
							WSNSimulation.CHnodes.get(i).ForwardReceivedDataToBS(" (CH_ID:"+ SN_ID+")"  + "[Cluster no:"+ CH_ID +"]"+AggregatedData);
							break;
						}
					}
				}	
			}	

			if (battary_strenth < config.MIN_POWER  ) {
				// send balace packets to BS then change CH
				CH = false;			
				line = null;
				SensoreNode ch = WSNSimulation.SelectClusterHead(CH_ID);
				if (ch != null) {
					ch.CH = true;			
					WSNSimulation.CHnodes.remove(CH_ID);
					WSNSimulation.CHnodes.add(CH_ID, ch);
				}				
			}
		} else System.out.println("Packet Droped at CH Node ID:" + SN_ID+ " Dead at slot:"+ deadATslot);
		AggregatedData = "";

	}
	
	String AgregateReceivedData(String S) {
		
		AggregatedData = AggregatedData + S;		
		return S;
	}
	
	double distFrom(SensoreNode sn){
		int dx = xposition - sn.xposition;
		int dy = yposition - sn.yposition;
		double dist = Math.sqrt(dx*dx + dy*dy);
		return dist;		
	}
	
	double distFromBS(){
		int dx = xposition;
		int dy = yposition;
		double dist = Math.sqrt(dx*dx + dy*dy);
		return dist;
	}

} 
