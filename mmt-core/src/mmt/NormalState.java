package mmt;

import java.util.Map;
import java.util.TreeMap;
import java.time.LocalTime;
import java.io.Serializable;

public class NormalState extends PassengerState implements Serializable{
	public NormalState(Passenger passenger,Double discount){
		super(passenger,discount);
	}
	public void buyItinerary(){
		double d=getPassenger().getLast10();
		if(d>2500){
			getPassenger().setState(new SpecialState(getPassenger(),d));
		}
		else if(d>250){
			getPassenger().setState(new FrequentState(getPassenger(),d));

		}
		else{
			getPassenger().setState(new NormalState(getPassenger(),d));
		}
	}
	public String getStateString(){
		return "NORMAL";
	}
}