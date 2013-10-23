package replicatedlog;

import java.util.HashMap;
import java.util.Scanner;

import replicatedlog.Event.EventType;

public class ReplicatedLog {

	private HashMap<Integer, Replica> m_replicas;
	private HashMap<Integer, Transmission> m_transmissions;
	private int m_transNumber;
	
	public ReplicatedLog(){
		this(3);
	}
	
	public ReplicatedLog(int numberOfReplicas){
		this.m_replicas = new HashMap<Integer, Replica>(numberOfReplicas);
		this.m_transmissions = new HashMap<Integer, Transmission>();
		this.m_transNumber = 1;
		
		// create the replicas
		for (int i = 1; i <= 3; i++){
			Replica r = new Replica(i, numberOfReplicas);
			m_replicas.put(i, r);
		}
	}
	
	/**
	 * Adds an event with the specified key to the specified Replica, and executes it 
	 * @param eventType - The event type (INCREMENT/DECREMENT)
	 * @param replicaId - The ID of the Replica
	 * @param key - The key of the value to increment/decrement
	 */
	public void addEventToReplicaAndExecute(EventType eventType, int replicaId, String key){
		Replica replica = m_replicas.get(replicaId);
		if (replica != null){
			replica.addEventAndExecute(eventType, key);
		}
		else{
			System.err.println("Error: Replica with ID \"" + replicaId + "\" does not exist");
		}
	}
	
	public Integer getValueFromReplica(int replicaId, String key){
		Replica replica = m_replicas.get(replicaId);
		
		Integer val = -1;
		if (replica != null){
			val = replica.getValue(key);
			System.out.println(val);
		}
		else{
			System.err.println("Error: Replica with ID \"" + replicaId + "\" does not exist");
		}
		
		return val;
	}
	
	/**
	 * Prints the state of the specified Replica
	 * @param replicaId - The ID of the Replica
	 */
	public void printState(int replicaId){
		Replica replica = m_replicas.get(replicaId);
		if (replica != null){
			replica.printState();
		}
		else{
			System.err.println("Error: Replica with ID \"" + replicaId + "\" does not exist");
		}
	}
	
	/**
	 * Put the current state of the log and time table of 
	 * the replica with ID sourceReplicaId in the simulated 
	 * network data structure to be received by destReplicaId.
	 * @param sourceReplicaId - The source Replica's ID
	 * @param destReplicaId - The destination Replica's ID
	 * @return - The unique Transmission ID
	 */
	public int sendLog(int sourceReplicaId, int destReplicaId){
		Replica rep1 = m_replicas.get(sourceReplicaId);
		int transId = -1;
		
		if (!m_replicas.containsKey(destReplicaId)){
			System.err.println("Error: Replica with ID \"" + destReplicaId + "\" does not exist");
		}
		else if (rep1 != null){
			/* Create an "empty" transmission, and get the relevant log entries from
			 * the correct Replica for it */
			transId = m_transNumber;
			Transmission trans = new Transmission(transId, sourceReplicaId, destReplicaId);
			trans = rep1.sendTrans(trans);
			
			// Add the transmission to the "network", and increment the transmission number
			m_transmissions.put(transId, trans);
			System.out.println("Transmission number: " + transId);
			m_transNumber++;
		}
		else{
			System.err.println("Error: Replica with ID \"" + sourceReplicaId + "\" does not exist");
		}
		
		return transId;
	}
	
	/**
	 * This function will make a transmission with id transmissionNumber 
	 * be received by the destination from the simulated network data 
	 * structure. The transmissionNumber is the returned value from the 
	 * sendLog(int,int) function. This two-step transmission is required 
	 * so that we can be able to simulate lost and reordered transmissions.
	 * @param transNumber - The unique transmission number
	 */
	public void receiveLog(int transNumber){
		Transmission trans = m_transmissions.get(transNumber);
		
		if (trans != null){
			int repId = trans.getDestId();
			Replica destRep = m_replicas.get(repId);
			
			// If the replica exists, receive the transmission
			if (destRep != null){
				destRep.receiveTrans(trans);
			}
		}
		else{
			System.err.println("Error: Transmission with ID \"" + transNumber + "\" does not exist");
		}
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
			line = line.replaceAll("Ò", "");
			line = line.replaceAll("Ó", "");
			String[] vals = line.split("[(),]");
			
			String methodName = vals[0];
			methodName = methodName.toLowerCase();
			
			// A bit messy, but it works
			if (methodName.equals("increment")){
				if (vals.length == 3){
					repLog.addEventToReplicaAndExecute(EventType.INCREMENT, Integer.parseInt(vals[1]), vals[2]);
				}
				else{
					System.err.println("Error: incorrect arguments");
				}
			}
			else if (methodName.equals("decrement")){
				if (vals.length == 3){
					repLog.addEventToReplicaAndExecute(EventType.DECREMENT, Integer.parseInt(vals[1]), vals[2]);
				}
				else{
					System.err.println("Error: incorrect arguments");
				}
			}
			else if (methodName.equals("getvalue")){
				if (vals.length == 3){
					repLog.getValueFromReplica(Integer.parseInt(vals[1]), vals[2]);
				}
				else{
					System.err.println("Error: incorrect arguments");
				}
			}
			else if (methodName.equals("printstate")){
				if (vals.length == 2){
					repLog.printState(Integer.parseInt(vals[1]));
				}
				else{
					System.err.println("Error: incorrect arguments");
				}
			}
			else if (methodName.equals("sendlog")){
				if (vals.length == 3){
					repLog.sendLog(Integer.parseInt(vals[1]), Integer.parseInt(vals[2]));
				}
				else{
					System.err.println("Error: incorrect arguments");
				}
			}
			else if (methodName.equals("receivelog")){
				if (vals.length == 2){
					repLog.receiveLog(Integer.parseInt(vals[1]));
				}
				else{
					System.err.println("Error: incorrect arguments");
				}
			}
			else if (methodName.equals("help")){
				System.out.println("-- Help --\nThe following commands are accepted:\n" +
						"Increment(replicaId, key) -- Increments the value stored at 'key' (string) in Replica 'replicaId' (int)\n" +
						"Decrement(replicaId, key) -- Decrements the value stored at 'key' (string) in Replica 'replicaId' (int)\n" +
						"GetValue(replicaId, key) -- Prints the value stored at 'key' (string) in Replica 'replicaId' (int)\n" +
						"PrintState(replicaId) -- Prints the current state of the Replica 'replicaId'" +
						"SendLog(srcReplicaId, destReplicaId) -- Sends the local log of 'srcReplicaId' to 'destReplicaId'. Prints the Transmission ID for the transmission, which is used in 'ReceiveLog(transmissionId)'" +
						"ReceiveLog(transmissionId) -- Receives the transmission with 'transmissionId'");
			}
			else{
				System.err.println("Error: unknown command.");
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
