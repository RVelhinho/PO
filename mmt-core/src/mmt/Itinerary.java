package mmt;

import java.util.Map;
import java.util.TreeMap;
import java.io.Serializable;
import java.time.Duration;
import java.time.LocalTime;
import java.time.LocalDate;



public class Itinerary implements Serializable{
	private int _id;
	private int _passengerId;
	private Double _cost=0.0;
	private Station _departureStation;
	private Station _arrivalStation;
	private LocalDate _date;
	private LocalTime _totalTime;
	private Map<Integer,Services> _services = new TreeMap<Integer,Services>();

	public Itinerary(int passengerId){
		_passengerId=passengerId;
	}
	public int getItineraryId(){
		return _id;
	}
	public void setItineraryId(int id){
		_id=id;
	}
	public Double getItineraryCost(){
		return _cost;
	}
	public void addToItineraryCost(Double cost){
		_cost+=cost;
	}
	public LocalDate getItineraryLocalDate(){
		return _date;
	}
	public void setItineraryLocalDate(LocalDate date){
		_date=date;
	}
	public LocalTime getItineraryTotalTime(){
		return _totalTime;
	}
	public void addToItineraryTotalTime(Duration totalTime){
		_totalTime=_totalTime.plus(totalTime);
	}
	public String getItineraryServices(){
		String s = "";
    	for (Integer key : _services.keySet()){
      		Services se = (Services) _services.get(key);
       		s += "Serviço #" + se.getServiceId() + " @ " + String.format("%.2f",se.getServiceCost()) + "\n";
      		Map<LocalTime,Station> _stations = se.getStations();
      		for (LocalTime keys : _stations.keySet()){
          		Station st = (Station) _stations.get(keys);
          		s+=st.getStationLocalTime().toString() + " " + st.getStationName() + "\n";
        	}
      
    	}
    	return s;
	}
	public void addService(Services s){
		_services.put(s.getServiceId(),s);
	}
	public Map<Integer,Services> getServices(){
		return _services;
	}
	
}