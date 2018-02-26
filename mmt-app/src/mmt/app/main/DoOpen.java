package mmt.app.main;

import mmt.TicketOffice;
import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.Input;
import java.io.FileNotFoundException;
import java.io.IOException;
import mmt.exceptions.ImportFileException;




/**
 * §3.1.1. Open existing document.
 */

public class DoOpen extends Command<TicketOffice> {

   Input<String> _filename;
  /**
   * @param receiver
   */
  public DoOpen(TicketOffice receiver) {
    super(Label.OPEN, receiver);
    _filename = _form.addStringInput(Message.openFile());
  }


  /** @see pt.tecnico.po.ui.Command#execute()*/
  @Override
  public final void execute() {
    	try{
        _form.parse();
        _receiver.load(_filename.value());
  	   }
      catch(FileNotFoundException e){_display.popup(Message.fileNotFound());}
      catch(IOException e){e.printStackTrace();}
      catch(ClassNotFoundException e){e.printStackTrace();}
  }

}
