package mmt.app.itineraries;

import mmt.TicketOffice;
import mmt.exceptions.NoSuchPassengerIdException;
import mmt.app.exceptions.NoSuchPassengerException;
import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.DialogException;
import pt.tecnico.po.ui.Input;

/**
 * §3.4.2. Show all itineraries (for a specific passenger).
 */
public class DoShowPassengerItineraries extends Command<TicketOffice> {

  Input<Integer> _passengerId;

  /**
   * @param receiver
   */
  public DoShowPassengerItineraries(TicketOffice receiver) {
    super(Label.SHOW_PASSENGER_ITINERARIES, receiver);
    _passengerId=_form.addIntegerInput(Message.requestPassengerId());
  }

  /** @see pt.tecnico.po.ui.Command#execute() */
  @Override
  public final void execute() throws DialogException {
   try{
      _form.parse();
      if(_receiver.PassengerHasItineraries(_passengerId.value())==false){
        _display.popup(Message.noItineraries(_passengerId.value()));
        return;
      }
      _display.popup(_receiver.getPassengerItineraries(_passengerId.value()).replace(",","."));
   }catch(NoSuchPassengerIdException e){throw new NoSuchPassengerException(_passengerId.value());}
  }

}
