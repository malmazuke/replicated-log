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
	
	/**
	 * Adds the event to the log, and executes it, maintaining the 
	 * TimeTable & clock in the process
	 * @param eventType - The type of the event {INCREMENT, DECREMENT}
	 * @param key - The key of the value to increment/decrement
	 */
	public void addEventAndExecute(EventType eventType, String key){
		Event event = new Event(eventType, key);
		
		// Add the event to the local log
		m_localLog.add(event);
		
		// Update the local clock and Timetable
		m_localClock++;
		m_timeTable[m_id-1][m_id-1] = m_localClock;
		
		// Execute the event
		executeEvent(event);
	}
	
	/**
	 * Executes a given event (which contains a key)
	 * @param event - The Event to execute
	 */
	public void executeEvent(Event event){
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
		builder.append("Log: {\"");
		
		for (int i = 0; i < m_localLog.size() - 1; i++){
			Event e = m_localLog.get(i);
			
			switch (e.getEventType()) {
			case INCREMENT:
				builder.append("increment(");
				break;

			case DECREMENT:
				builder.append("decrement(");
				break;
			}
			
			builder.append(e.getKey() + ")\", ");
		}
		
		if (m_localLog.size() > 0){
			Event e = m_localLog.get(m_localLog.size() - 1);
			
			switch (e.getEventType()) {
			case INCREMENT:
				builder.append("increment(");
				break;

			case DECREMENT:
				builder.append("decrement(");
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
}
