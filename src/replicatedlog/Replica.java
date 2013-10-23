package replicatedlog;

import java.util.ArrayList;
import java.util.HashMap;

import replicatedlog.Event;
import replicatedlog.Event.EventType;

/**
 * A replica in a distributed implementation of a Replicated Log
 * 
 * @author Mark Feaver
 *
 */
public class Replica {
	private int m_id; // A unique ID
	private int m_localClock; // The local clock/counter of the replica
	private ArrayList<Event> m_localLog; // The local log holding the history of events  
	private HashMap<String, Integer> m_values; // The values of each key
	private int[][] m_timeTable; 
	
	int DEFAULT_INCR_VALUE = 1; // The default value to increment by
	
	/**
	 * Constructor
	 * @param id - The unique ID of the Replica
	 */
	public Replica(int id, int numberOfReplicas){
		this.m_id = id;
		this.m_localClock = 0;
		this.m_localLog = new ArrayList<Event>();
		this.m_values = new HashMap<String, Integer>();
		this.m_timeTable = new int[numberOfReplicas][numberOfReplicas];
	}
	
	public ArrayList<Event> getLog(){
		return this.m_localLog;
	}
	
	public int[][] getTimeTable(){
		return this.m_timeTable;
	}
	
	/**
	 * Adds the event to the log, and executes it, maintaining the 
	 * TimeTable & clock in the process - i.e. oper(p)
	 * @param eventType - The type of the event {INCREMENT, DECREMENT}
	 * @param key - The key of the value to increment/decrement
	 */
	public void addEventAndExecute(EventType eventType, String key){
		// Update the local clock and TimeTable
		m_localClock++;
		m_timeTable[m_id-1][m_id-1] = m_localClock;
				
		Event event = new Event(eventType, key, this.m_id, this.m_localClock);
		
		// Add the event to the local log
		m_localLog.add(event);
		
		// Execute the event
		executeEvent(event);
	}
	
	/**
	 * Executes a given event (which contains a key)
	 * @param event - The Event to execute
	 */
	private void executeEvent(Event event){
		switch(event.getEventType()){
		case INCREMENT:
			increment(event.getKey());
			break;
		case DECREMENT:
			decrement(event.getKey());
			break;
		}
	}
	
	/**
	 * Increments the value stored for the given key
	 * @param key - The key of the value to increment
	 */
	public void increment(String key){
		if (m_values.containsKey(key)){
			m_values.put(key, m_values.get(key) + DEFAULT_INCR_VALUE);
		}
		else{
			m_values.put(key, DEFAULT_INCR_VALUE);
		}
	}
	
	/**
	 * Decrements the value stored for the given key
	 * @param key - The key of the value to decrement
	 */
	public void decrement(String key){
		if (m_values.containsKey(key)){
			m_values.put(key, m_values.get(key) - DEFAULT_INCR_VALUE);
		}
//		else{
//			m_values.put(key, DEFAULT_INCR_VALUE);
//		}
	}
	
	public Integer getValue(String key){
		if (m_values.containsKey(key)){
			return m_values.get(key);
		}
		else{
			return null;
		}
	}
	
	public void printState(){
		// Print the Log
		StringBuilder builder = new StringBuilder();
		builder.append("Log: {");
		
		for (int i = 0; i < m_localLog.size() - 1; i++){
			Event e = m_localLog.get(i);
			
			switch (e.getEventType()) {
			case INCREMENT:
				builder.append("\"increment(");
				break;

			case DECREMENT:
				builder.append("\"decrement(");
				break;
			}
			
			builder.append(e.getKey() + ")\", ");
		}
		
		if (m_localLog.size() > 0){
			Event e = m_localLog.get(m_localLog.size() - 1);
			
			switch (e.getEventType()) {
			case INCREMENT:
				builder.append("\"increment(");
				break;

			case DECREMENT:
				builder.append("\"decrement(");
				break;
			}
			
			builder.append(e.getKey() + ")\"");
		}
		
		builder.append("}");
		
		System.out.println(builder.toString());
		
		// Print the TimeTable
		System.out.println("TimeTable:");
		
		for (int y = 0; y < m_timeTable.length; y++){
			builder = new StringBuilder();
			builder.append("|");
			for (int x = 0; x < m_timeTable[y].length; x++){
				builder.append(" " + m_timeTable[y][x] + " |");
			}
			System.out.println(builder.toString());
		}
	}
	
	/**
	 * Determines whether or not a Replica already has an event, based on
	 * its TimeTable entry
	 * @param k - The ID of the Replica in question 
	 * @param e - The Event in question 
	 * @return - True if the Replica does have the event, false otherwise
	 */
	public boolean hasRec(int k, Event e){
		// These are minus one, as indexes are 0-based
		int ttEntry = m_timeTable[k-1][e.getReplicaId()-1];
		int eventTime = e.getEventTime();
		
		boolean hasRec = (ttEntry >= eventTime); 
		return hasRec;
	}
	
	/**
	 * "Sends" a transmission. This does not actually send anything, 
	 * but simply adds the correct log entries and time table to the 
	 * already-created transmission, which is then added to the network in 
	 * the ReplicatedLog.
	 * @param trans - The transmission that we're going to "send"
	 */
	public Transmission sendTrans(Transmission trans){
		/* This will contain only the events that the destination Replica 
		 * does not know about yet */
		ArrayList<Event> outLog = new ArrayList<Event>(m_localLog.size());
		
		// Our destination Replica id - i.e. "k" 
		int destId = trans.getDestId();
		
		/* Check to see if each event from the local log needs to 
		 * be added to the transmission */
		for (Event e : m_localLog){
			if (!hasRec(destId, e)){
				outLog.add(e);
			}
		}
		
		// Set the transmissions log to the subset that isn't already in the destination replica
		trans.setLog(outLog);
		
		// Piggy-back the time table
		trans.setTimeTable(m_timeTable);
		
		// "Send" the transmission
		return trans;
	}
	
	/**
	 * Receive the transmission, updating the events in our local log, 
	 * as well as our local time table.
	 * @param trans - The received transmission
	 */
	public void receiveTrans(Transmission trans){
		// Add the received logs to our local log
		ArrayList<Event> receivedLog = trans.getLog();
		for (Event e : receivedLog){
			// Adds the event to the local log, and executes it
			m_localLog.add(e);
			executeEvent(e);
		}
		
		// The ID of the source replica
		int sourceId = trans.getSourceId();
		int[][] receivedTimeTable = trans.getTimeTable();
		
		// Update the row specific to the source Receiver
		for (int i = 0; i < m_timeTable[m_id-1].length; i++){
			int currVal = m_timeTable[m_id-1][i];
			int newVal = receivedTimeTable[sourceId-1][i];
			m_timeTable[m_id-1][i] = Math.max(currVal, newVal);
		}
		
		// Update the rest of the entries
		for (int j = 0; j < m_timeTable.length; j++){
			for (int i = 0; i < m_timeTable[j].length; i++){
				int oldVal = m_timeTable[j][i];
				int newVal = receivedTimeTable[j][i];
				m_timeTable[j][i] = Math.max(oldVal, newVal);
			}
		}
	}
}
