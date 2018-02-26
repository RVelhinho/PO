package mmt;

import java.util.Comparator;

public class DepartureLocalTimeComparator implements Comparator<Services>{
	public int compare(Services s1,Services s2){
		Station st1 = s1.getDepartureStation();
		Station st2 = s2.getDepartureStation();
		return st1.getStationLocalTime().compareTo(st2.getStationLocalTime());
	}
}
