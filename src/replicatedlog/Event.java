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
	
	public Event(EventType eventType, String key){
		this.m_eventType = eventType;
		this.m_key = key;
	}
	
	public EventType getEventType(){
		return this.m_eventType;
	}
	
	public String getKey(){
		return this.m_key;
	}
}
