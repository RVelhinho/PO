package mmt;
import java.time.LocalTime;
import java.io.Serializable;

public class Station implements Serializable{
	private String _name;
	private LocalTime _stationTime;

	public Station(String name,LocalTime stationTime){
		_name=name;
		_stationTime=stationTime;
	}
	public String getStationName(){
		return _name;
	}
	public void setStationName(String name){
		_name=name;
	}
	public LocalTime getStationLocalTime(){
		return _stationTime;
	}
	public void setStationLocalTime(LocalTime stationTime){
		_stationTime=stationTime;
	}
	
}