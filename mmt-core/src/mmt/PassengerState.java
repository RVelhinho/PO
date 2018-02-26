package mmt;

import java.util.Map;
import java.util.TreeMap;
import java.time.LocalTime;
import java.io.Serializable;

public abstract class PassengerState implements Serializable{
	private Passenger _passenger;
	private Double _discount;
	public PassengerState(Passenger passenger,Double discount){
		_passenger=passenger;
		_discount=discount;
	}
	public Passenger getPassenger(){
		return _passenger;
	}
	public Double getDiscount(){
		return _discount;
	}
	public abstract void buyItinerary();
	public abstract String getStateString();
}