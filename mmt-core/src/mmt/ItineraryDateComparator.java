package mmt;

import java.util.Comparator;

public class ItineraryDateComparator implements Comparator<Itinerary>{
	public int compare(Itinerary it1, Itinerary it2){
		return it1.getItineraryLocalDate().compareTo(it2.getItineraryLocalDate());
	}
}
