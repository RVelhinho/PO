package mmt;

import java.util.Map;
import java.util.TreeMap;
import java.time.Duration;
import java.time.LocalTime;
import java.time.LocalDate;
import java.text.DecimalFormat;
import java.io.Serializable;

public class Services implements Serializable{
	private int _id;
	private double _cost;
	private Station _departureStation;
	private Station _arrivalStation;
	private Map<LocalTime,Station> _stations = new TreeMap<LocalTime,Station>();
	private DecimalFormat df = new DecimalFormat("#.00");

	public Services(int id,double cost){
		_id=id;
		_cost=cost;
	}
	public int getServiceId(){
		return _id;
	}
	public void setServiceId(int id){
		_id=id;
	}
	public Double getServiceCost(){
		return _cost;
	}
	public void setServiceCost(double cost){
		_cost=cost;
	}
	public void addStation(Station s){
		_stations.put(s.getStationLocalTime(),s);
	}
	public void addDepartureStation(Station s){
		_departureStation=s;
	}
	public void addArrivalStation(Station s){
		_arrivalStation=s;
	}
	public Station getDepartureStation(){
		return _departureStation;
	}
	public Station getArrivalStation(){
		return _arrivalStation;
	}
	@SuppressWarnings("nls")
	@Override
	public String toString(){
		String s = "Serviço # " + _id + " @ " + _cost + "\n";
		for (LocalTime key : _stations.keySet()){
     		Station st = (Station) _stations.get(key);
     		s+=st.getStationLocalTime().toString() + " " + st.getStationName() + "\n";
     	}
     	return s;
	}
	public Map<LocalTime,Station> getStations(){
		return _stations;
	}
}