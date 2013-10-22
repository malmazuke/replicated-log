package replicatedlog;

import java.util.HashMap;
import java.util.Scanner;

import replicatedlog.Event.EventType;

public class ReplicatedLog {

	private HashMap<Integer, Replica> m_replicas;
	private int m_numberOfReplicas;
	
	public ReplicatedLog(){
		this(3);
	}
	
	public ReplicatedLog(int numberOfReplicas){
		this.m_replicas = new HashMap<Integer, Replica>(numberOfReplicas);
		this.m_numberOfReplicas = numberOfReplicas;
		
		// create the replicas
		for (int i = 1; i <= 3; i++){
			Replica r = new Replica(i, numberOfReplicas);
			m_replicas.put(i, r);
		}
	}
	
	public void addEventToReplicaAndExecute(EventType eventType, int replicaId, String key){
		m_replicas.get(replicaId).addEventAndExecute(eventType, key);
	}
	
	public Integer getValueFromReplica(int replicaId, String key){
		return m_replicas.get(replicaId).getValue(key);
	}
	
	public void printState(int replicaId){
		m_replicas.get(replicaId).printState();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ReplicatedLog repLog = new ReplicatedLog();
		
		Scanner in = new Scanner(System.in);
		String line;
		while (!(line = in.nextLine()).equals("")){
			line = line.replaceAll("\\s","");
			line = line.replaceAll("\"","");
			String[] vals = line.split("[(),]");
			
			if (vals[0].equals("Increment")){
				repLog.addEventToReplicaAndExecute(EventType.INCREMENT, Integer.parseInt(vals[1]), vals[2]);
			}
			else if (vals[0].equals("getValue")){
				System.out.println(repLog.getValueFromReplica(Integer.parseInt(vals[1]), vals[2]));
			}
			else if (vals[0].equals("PrintState")){
				repLog.printState(Integer.parseInt(vals[1]));
			}
			
		}
//		Increment(1, ÒXÓ)
//		getValue(1,ÓXÓ)
//			1
//		getValue(2,ÓXÓ)
//			null
//		PrintState(1)
//			Log: {Òincrement(x)Ó} TimeTable:
//			| 1 0 0|
//			| 0 0 0|
//			| 0 0 0|
//		SendLog(1,2)
//			Transmission number: 1
//		Increment(1, ÒYÓ)
//		PrintState(2)
//			Log: {} TimeTable:
//			| 0 0 0| 
//			| 0 0 0| 
//			| 0 0 0|
//		ReceiveLog(1)
//		PrintState(2)
//			Log: {Òincrement(x)Ó} TimeTable:
//			| 1 0 0|
//			| 1 0 0|
//			| 0 0 0|
//		getValue(2,ÓXÓ)
//			1
	}

}
