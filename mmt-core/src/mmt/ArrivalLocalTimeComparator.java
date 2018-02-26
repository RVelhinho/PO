package mmt;

import java.util.Comparator;

public class ArrivalLocalTimeComparator implements Comparator<Services>{
	public int compare(Services s1,Services s2){
		Station st1 = s1.getArrivalStation();
		Station st2 = s2.getArrivalStation();
		return st1.getStationLocalTime().compareTo(st2.getStationLocalTime());
	}
}
