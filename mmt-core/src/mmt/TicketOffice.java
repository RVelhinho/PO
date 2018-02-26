package mmt;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import mmt.exceptions.BadDateSpecificationException;
import mmt.exceptions.BadTimeSpecificationException;
import mmt.exceptions.ImportFileException;
import mmt.exceptions.InvalidPassengerNameException;
import mmt.exceptions.MissingFileAssociationException;
import mmt.exceptions.NoSuchPassengerIdException;
import mmt.exceptions.NoSuchServiceIdException;
import mmt.exceptions.NoSuchStationNameException;
import mmt.exceptions.NoSuchItineraryChoiceException;
import mmt.exceptions.NonUniquePassengerNameException;
import java.util.Collection;
import java.util.Collections;
import java.lang.NullPointerException;
import java.util.Map;
import java.util.TreeMap;


/**
 * Façade for handling persistence and other functions.
 */
public class TicketOffice {

  /** The object doing most of the actual work. */
  private TrainCompany _trains= new TrainCompany();

  /* Vai conter os services a copiar durante o Reset */
  private Map<Integer,Services> _servicestocopy= new TreeMap<Integer,Services>();

  /* String que vai conter o nome do ficheiro em que foi guardado durante o Save */
  private String _newFileSave;

  /* Atributos e metodos que permitem verificar se o ficheiro se encontra de momento gravado pelo _save  */

  private boolean _saved=false;


 
  public String getNewFileSave(){
    return _newFileSave;
  }

  public void setNewFileSave(String filename){
    _newFileSave=filename;
  }

  public void setSavedTrue(){
  _saved=true;
 }

 public void setSavedFalse(){
  _saved=false;
 }

 public boolean getSaved(){
  return _saved;
 }

  /* Vai criar uma copia dos Services da TrainCompany para uma nova TrainCompany */
 public void reset() {
  _servicestocopy=_trains.getServices2();
  _trains = new TrainCompany();
  _trains.setServicesCopy(_servicestocopy);
  setNewFileSave(null);
  setSavedFalse();
 }

 /* Metodo auxiliar que permite obter a copia dos Services. Usado no Reset */
 public Map<Integer,Services> getServices2(){
  return _trains.getServices2();
 }

 /* Vai gravar a TrainCompany , guardando no disco */
 public void save(String filename) throws IOException {
	ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(filename)));
	oos.writeObject(_trains);
	oos.close();
  setNewFileSave(filename);
  setSavedTrue();
}

  /* Vai ler do disco, verificando se existe ou nao uma versao anterior */
 public void load(String filename) throws FileNotFoundException, IOException, ClassNotFoundException {
  if(filename==null){
    throw new FileNotFoundException();
  }
	ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(filename)));
	TrainCompany trains = (TrainCompany)  ois.readObject();
	ois.close();

  if(trains != null){
    _trains=trains;
  }
  setSavedTrue();
  setNewFileSave(filename);
  
 }

 public void importFile(String datafile) throws ImportFileException {
  try{
    _trains.importFile(datafile);
 }catch(IOException e){e.printStackTrace();}

 }

 /* Sempre que se regista um novo Passenger ou se se altera o nome de algum vai colocar o estado de _saved a false */

 public void registerPassenger(String name) throws  NonUniquePassengerNameException{
  _trains.registerPassenger2(name);
  setSavedFalse();

 }

 public void removePassenger(int id){
  _trains.removePassenger(id);
 }

 public void changePassengerName(int id,String name) throws NoSuchPassengerIdException , NonUniquePassengerNameException {
  _trains.changePassengerName(id,name);
  setSavedFalse();
 }

 public String getPassenger(int id) throws NoSuchPassengerIdException {
  return _trains.getPassenger(id);
 }

 public String getPassengers() { 
  return _trains.getPassengers();
 }


 public String getServices() {
  return _trains.getServices();
 }

 public String getService(int id) throws NoSuchServiceIdException {
  return _trains.getService(id);
 }

 public String getServiceFromDepartingStation(String name) throws NoSuchStationNameException{
  return _trains.getServiceFromDepartingStation(name);
 }

 public String getServiceFromArrivingStation(String name) throws NoSuchStationNameException{
  return _trains.getServiceFromArrivingStation(name);
 }

 public String getItineraries(){
  return _trains.getItineraries();
 }


 public boolean PassengerHasItineraries(int passengerId) throws NoSuchPassengerIdException{
  _trains.getPassengerExistence(passengerId);
  boolean verify=false;
  if(_trains.PassengerNumberOfItineraries(passengerId)>0)
    verify=true;
  return verify;
 }
 public String getPassengerItineraries(int id) {
  return _trains.getPassengerItineraries(id);
 }


 public void commitItinerary(int passengerId,int itineraryNumber) throws NoSuchPassengerIdException,NoSuchItineraryChoiceException {
  if(itineraryNumber==0){
    return ;
  }
  else if(itineraryNumber<0 || (_trains.getItineraryFromChoices(passengerId,itineraryNumber)==null)){
    throw new NoSuchItineraryChoiceException(passengerId,itineraryNumber);
  }
  Itinerary it=_trains.getItineraryFromChoices(passengerId,itineraryNumber);
  _trains.commitItinerary(passengerId,it);
 }

 public String search(int passengerId, String departureStation, String arrivalStation,String departureDate,String departureTime) {
  return _trains.searchItineraries(passengerId,departureStation,arrivalStation,departureDate,departureTime);
 }


}
