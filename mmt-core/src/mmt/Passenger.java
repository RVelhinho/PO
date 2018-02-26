package mmt;

import java.util.Map;
import java.util.TreeMap;
import java.time.LocalTime;
import java.io.Serializable;
import java.time.Duration;
import java.util.*;

public class Passenger implements Serializable{
	private int _id;
	private int _itineraryNumber=1;
	private String _name;
	private PassengerState _state = new NormalState(this,0.0);
	private int _nitin=0;
	private double _cost=0.0;
	private LocalTime _totalTime=LocalTime.of(0,0,0,0);
	private Map<Integer,Itinerary> _itineraries= new TreeMap<Integer,Itinerary>();

	public Passenger(String name,int id){
		_name=name;	
		_id=id;
	}
	public int getPassengerId(){
		return _id;
	}
	public String getPassengerName(){
		return _name;
	}
	public void setPassengerName(String name){
		_name=name;
	}
	public double getPassengerCost(){
		return _cost;
	}
	public int getPassengerNitin(){
		return _nitin;
	}
	public String getPassengerState(){
		return _state.getStateString();
	}
	public LocalTime getPassengerTotalTime(){
		return _totalTime;
	}
	public void addToPassengerTotalTime(Duration totaltime){
		_totalTime=_totalTime.plus(totaltime);
	}
	public int getItineraryNumber(){
		return _itineraryNumber;
	}
	public void setItineraryNumber(int i){
		_itineraryNumber=i;
	}
	public void addItinerary(Itinerary it){
		_nitin++;
		_cost+=it.getItineraryCost();
		Map<Integer,Services> _services= it.getServices();
		for(Integer key : _services.keySet()){
			Services se = (Services) _services.get(key);
			Duration dtotal=Duration.between(se.getDepartureStation().getStationLocalTime(), se.getArrivalStation().getStationLocalTime());
			addToPassengerTotalTime(dtotal);
		}
		it.setItineraryId(_itineraryNumber);
		_itineraries.put(_itineraryNumber++,it);
		_state.buyItinerary();
	}
	public Map<Integer,Itinerary> getItineraries(){
		return _itineraries;
  	}
  	public void setState(PassengerState state){
  		_state=state;
  	}
  	public PassengerState getState(){
  		return _state;
  	}
  	public Double getLast10(){
  		double last10=0.0;
  		List<Itinerary> _list = new ArrayList<Itinerary> (_itineraries.values());
  		for(int i=_list.size()-1;i>=0;i--){
  			Itinerary it = _list.get(i);
  			Map<Integer,Services> _services= it.getServices();
  			for(Integer key : _services.keySet()){
  				Services se = (Services) _services.get(key);
  				last10+=se.getServiceCost();
  			}
  		}
  		return last10;
  	}
	@SuppressWarnings("nls")
	@Override
	public String toString(){
		return _id + "|" + _name + "|" + _state.getStateString() + "|" + _nitin + "|" + _cost + "|" + _totalTime;
	}
}