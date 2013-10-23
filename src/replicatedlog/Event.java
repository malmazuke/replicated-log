package replicatedlog;

/**
 * A wrapper class for events
 * @author Mark Feaver
 *
 */
public class Event {
	public enum EventType{
		INCREMENT, DECREMENT;
	};
	private EventType m_eventType;
	private String m_key;
	private int m_replicaId;
	private int m_time;
	
	/**
	 * The Event constructor
	 * @param eventType - The type of Event (INCREMENT, DECREMENT)
	 * @param key - The key of the event
	 * @param replicaId - The ID of the replica where the event occurred
	 * @param time - The local replica's time at which the event occured
	 */
	public Event(EventType eventType, String key, int replicaId, int eventTime){
		this.m_eventType = eventType;
		this.m_key = key;
		this.m_replicaId = replicaId;
		this.m_time = eventTime;
	}
	
	/**
	 * Returns the event/operation type - i.e. op(e)
	 * @return
	 */
	public EventType getEventType(){
		return this.m_eventType;
	}
	
	public String getKey(){
		return this.m_key;
	}
	
	/**
	 * Returns the replica id - i.e. node(e)
	 * @return - The replica id
	 */
	public int getReplicaId(){
		return this.m_replicaId;
	}
	
	/**
	 * Returns the time that the event occured - i.e. time(e)
	 * @return
	 */
	public int getEventTime(){
		return this.m_time;
	}
}
