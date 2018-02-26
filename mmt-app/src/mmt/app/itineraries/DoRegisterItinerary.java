package mmt.app.itineraries;

import mmt.TicketOffice;
import mmt.app.exceptions.BadDateException;
import mmt.app.exceptions.BadTimeException;
import mmt.app.exceptions.NoSuchItineraryException;
import mmt.app.exceptions.NoSuchPassengerException;
import mmt.app.exceptions.NoSuchStationException;
import mmt.exceptions.BadDateSpecificationException;
import mmt.exceptions.BadTimeSpecificationException;
import mmt.exceptions.NoSuchPassengerIdException;
import mmt.exceptions.NoSuchStationNameException;
import mmt.exceptions.NoSuchItineraryChoiceException;
import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.DialogException;
import pt.tecnico.po.ui.Input;

//FIXME import other classes if necessary

/**
 * §3.4.3. Add new itinerary.
 */
public class DoRegisterItinerary extends Command<TicketOffice> {

  //FIXME define input fields
  Input<Integer> _passengerId;
  Input<Integer> _itineraryNumber;
  Input<String> _departureStationName;
  Input<String> _departureDate;
  Input<String> _departureTime;
  Input<String> _arrivalStationName;

  /**
   * @param receiver
   */
  public DoRegisterItinerary(TicketOffice receiver) {
    super(Label.REGISTER_ITINERARY, receiver);
    _passengerId=_form.addIntegerInput(Message.requestPassengerId());
    _departureStationName=_form.addStringInput(Message.requestDepartureStationName());
    _arrivalStationName=_form.addStringInput(Message.requestArrivalStationName());
    _departureDate=_form.addStringInput(Message.requestDepartureDate());
    _departureTime=_form.addStringInput(Message.requestDepartureTime());
  }

  /** @see pt.tecnico.po.ui.Command#execute() */
  @Override
  public final void execute() throws DialogException {
    try{
      _form.clear();
      _passengerId=_form.addIntegerInput(Message.requestPassengerId());
      _departureStationName=_form.addStringInput(Message.requestDepartureStationName());
      _arrivalStationName=_form.addStringInput(Message.requestArrivalStationName());
      _departureDate=_form.addStringInput(Message.requestDepartureDate());
      _departureTime=_form.addStringInput(Message.requestDepartureTime());
      _form.parse();
      if(_receiver.search(_passengerId.value(),_departureStationName.value(),_arrivalStationName.value(),_departureDate.value(),_departureTime.value()).equals(""))
      	return;
      _display.popup(_receiver.search(_passengerId.value(),_departureStationName.value(),_arrivalStationName.value(),_departureDate.value(),_departureTime.value()).replace(",","."));
      _form.clear();
      _itineraryNumber=_form.addIntegerInput(Message.requestItineraryChoice());
      _form.parse();
      _receiver.commitItinerary(_passengerId.value(),_itineraryNumber.value());
    }catch(NoSuchItineraryChoiceException e){throw new NoSuchItineraryException(_passengerId.value(),_itineraryNumber.value());}
    catch(NoSuchPassengerIdException e){throw new NoSuchPassengerException(_passengerId.value());}
  }
}
