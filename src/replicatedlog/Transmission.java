package replicatedlog;

import java.util.ArrayList;

public class Transmission {
	private int m_id;
	private int m_sourceId;
	private int m_destId;
	private ArrayList<Event> m_log;
	private int[][] m_timeTable;
	
	public Transmission(int transId, int sourceId, int destId){
		this.m_id = transId;
		this.m_sourceId = sourceId;
		this.m_destId = destId;
		this.m_log = null;
		this.m_timeTable = null;
	}
	
	public int getId(){
		return this.m_id;
	}
	
	public int getSourceId(){
		return this.m_sourceId;
	}
	
	public int getDestId(){
		return this.m_destId;
	}
	
	/**
	 * Sets the log of the transmission, which is from the source Replica
	 * @param log - the source's log
	 */
	@SuppressWarnings("unchecked")
	public void setLog(ArrayList<Event> log){
		this.m_log = (ArrayList<Event>) log.clone();
	}
	
	public ArrayList<Event> getLog(){
		return this.m_log;
	}
	
	/**
	 * Sets the time table of the transmission, which is from the source Replica
	 * @param timeTable - the source's time table
	 */
	public void setTimeTable(int[][] timeTable){
		this.m_timeTable = new int[timeTable.length][timeTable[0].length];
		
		for (int y = 0; y < m_timeTable.length; y++){
			for (int x = 0; x < m_timeTable[y].length; x++){
				this.m_timeTable[y][x] = timeTable[y][x];
			}
		}
	}
	
	public int[][] getTimeTable(){
		return this.m_timeTable;
	}
}
