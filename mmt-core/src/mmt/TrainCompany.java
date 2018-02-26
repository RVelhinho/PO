package mmt;

import mmt.exceptions.BadDateSpecificationException;
import mmt.exceptions.BadEntryException;
import mmt.exceptions.BadTimeSpecificationException;
import mmt.exceptions.InvalidPassengerNameException;
import mmt.exceptions.NoSuchDepartureException;
import mmt.exceptions.NoSuchPassengerIdException;
import mmt.exceptions.NoSuchServiceIdException;
import mmt.exceptions.NoSuchStationNameException;
import mmt.exceptions.NoSuchItineraryChoiceException;
import mmt.exceptions.NonUniquePassengerNameException;
import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.regex.Pattern;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Collections;
import java.io.FileNotFoundException;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import java.time.Duration;
import java.time.LocalDate;

/**
 * A train company has schedules (services) for its trains and passengers that
 * acquire itineraries based on those schedules.
 */
public class TrainCompany implements Serializable {

  /** Serial number for serialization. */
  private static final long serialVersionUID = 201708301010L;

  /** TreeMap onde vao ser guardados os passageiros, estando ordenados pelo seu id */
  private Map<Integer,Passenger> _passengers = new TreeMap<Integer,Passenger>();
  
  /** TreeMap onde vao ser guardados todos os servicos, estando ordenados pelo seu id */
  private Map<Integer,Services> _services = new TreeMap<Integer,Services>();

  /** Treemap onde vao ser guardados os itinerarios a mostrar na funcao search, en que se mostra todas as possibilidades */
  private Map<Integer,Itinerary> _itineraries = new TreeMap<Integer,Itinerary>();

  /** Contador usado para imprimir os itinerarios por ordem */
  private int _itcounter=1;

  /** Contador de Id dos passageiros */
  private int _passengerNumber = 0;

  /** Contador de Id dos itinerarios guardados para cada passageiro*/
  private int _itineraryNumber = 1;

  /** Tempos que correspondem ao departureTime e arrivalTime */
  private LocalTime _departureTime;
  private LocalTime _arrivalTime;

  private int soma_max=0;
  private int longestServiceId;


  /**
   * Importa os dados do ficheiro import
   * 
   * @param filename
   *              o nome do ficheiro
   */

  public void importFile(String filename) throws IOException{

                BufferedReader reader = new BufferedReader(new FileReader(filename));
                String line;
                while ((line = reader.readLine()) != null) {
                        String[] fields = line.split("/|\\|");
                        try {
                              registerFromFields(fields);
                        } catch (BadEntryException e) {
                                e.printStackTrace();}
                }
                reader.close();
  }
               
  
  
  /**
   * Divide a informacao e procede ao registo 
   * 
   * @param fields
   *              a string que contem toda a informacao
   */

  public void registerFromFields(String[] fields) throws BadEntryException{
  
          Pattern patPassenger = Pattern.compile("^(PASSENGER)");        
          Pattern patService = Pattern.compile("^(SERVICE)");         
          Pattern patItinerary = Pattern.compile("^(ITINERARY)");

          if  (patPassenger.matcher(fields[0]).matches()) {
                  registerPassenger(fields);} 
          else if (patService.matcher(fields[0]).matches()) {
                  registerService(fields);}
          else if (patItinerary.matcher(fields[0]).matches()){
                  registerItinerary(fields);}
          else {
                  throw new BadEntryException(fields[0]);
          }
  }

  public void registerPassenger(String... fields) {
     for (Integer key : _passengers.keySet()){
      Passenger p_dup = (Passenger) _passengers.get(key);
      if(p_dup.getPassengerName().equals(fields[0])){
        return;
      }
    }
    Passenger p = new Passenger(fields[1],_passengerNumber++);
    _passengers.put(p.getPassengerId(),p);
  }

  public void registerService(String... fields) {
    Services ser = new Services(Integer.parseInt(fields[1]),Double.parseDouble(fields[2]));
    for(int i=3;i<fields.length;i=i+2){
      Station st = new Station(fields[i+1],LocalTime.parse(fields[i]));
      if(i==3){
        ser.addDepartureStation(st);
      }
      if(i==(fields.length-2))
        ser.addArrivalStation(st);
      ser.addStation(st);
    }
    _services.put(ser.getServiceId(),ser);
  }

  public void registerItinerary(String... fields){
    Passenger p=(Passenger) _passengers.get(Integer.parseInt(fields[1]));
    Itinerary it=new Itinerary(Integer.parseInt(fields[1]));
    for(int i=3;i<fields.length;i=i+3){
      Services se1=(Services) _services.get(Integer.parseInt(fields[i]));
      Services se2=new Services(se1.getServiceId(),se1.getServiceCost());
      Station dep=se1.getDepartureStation();
      Station ari=se1.getArrivalStation();
      Duration dtotal=Duration.between(dep.getStationLocalTime(),ari.getStationLocalTime());
      Double costtotal=se1.getServiceCost();
      Map<LocalTime,Station> _stations=se1.getStations();
      for(LocalTime key : _stations.keySet()){
        Station s=_stations.get(key);
        if (s.getStationName().equals(fields[i+1]))
          _departureTime=s.getStationLocalTime();
        if(s.getStationName().equals(fields[i+2]))
          _arrivalTime=s.getStationLocalTime();
      }
      for(LocalTime key : _stations.keySet()){
        Station s=_stations.get(key);
        if(s.getStationLocalTime().equals(_departureTime)){
          se2.addStation(s);
          se2.addDepartureStation(s);
        }
        if(s.getStationLocalTime().equals(_arrivalTime)){
          se2.addStation(s);
          se2.addArrivalStation(s);
        }
        if(s.getStationLocalTime().isAfter(_departureTime) && s.getStationLocalTime().isBefore(_arrivalTime)){
          se2.addStation(s);
        }
      }
      Station dep1=se2.getDepartureStation();
      Station ari1=se2.getArrivalStation();
      Duration dparcial=Duration.between(se2.getDepartureStation().getStationLocalTime(),se2.getArrivalStation().getStationLocalTime());
      Long dparcial1=dparcial.toMinutes();
      Long dtotal1=dtotal.toMinutes();
      Double dparcial2=Double.longBitsToDouble(dparcial1);
      Double dtotal2=Double.longBitsToDouble(dtotal1);
      Double costit=costtotal*(dparcial2/dtotal2);
      PassengerState state = p.getState();
      se2.setServiceCost(costit);
      if(state.getStateString().equals("ESPECIAL")){
        costit*=0.5;
      }
      else if(state.getStateString().equals("FREQUENTE")){
        costit*=0.85;
      }
      it.addToItineraryCost(costit);
      it.setItineraryLocalDate(LocalDate.parse(fields[2]));
      it.addService(se2);
    }
    p.addItinerary(it);
  }



  /**
   * Funcao que regista um passageiro atraves do menu
   * 
   * @param name
   *              nome do passageiro
   */

  public void registerPassenger2(String name) throws  NonUniquePassengerNameException{
    for (Integer key : _passengers.keySet()){
      Passenger p_dup = (Passenger) _passengers.get(key);
      if(p_dup.getPassengerName().equals(name)){
        throw new NonUniquePassengerNameException(name);
      }
    }
    Passenger p = new Passenger(name,_passengerNumber++);
    _passengers.put(p.getPassengerId(),p);
  }

  /**
   * Funcao que altera o nome de um passageiro especifico
   * 
   * @param name
   *              nome do passageiro
   */
  
  public void changePassengerName(int id,String name) throws NoSuchPassengerIdException ,NonUniquePassengerNameException {
    Passenger p =  _passengers.get(id);
    if(_passengers.get(id)==null){
      throw new NoSuchPassengerIdException(id);
    }
    for (Integer key : _passengers.keySet()){
      Passenger p_dup = (Passenger) _passengers.get(key);
      if(p_dup.getPassengerName().equals(name)){
        throw new NonUniquePassengerNameException(name);
      }
    }
    Passenger p_novo = new Passenger(name,id);
    _passengers.put(id,p_novo);
  }

  /**
   * Funcao que vai buscar um determinado passageiro pelo seu id
   * 
   * @param id
   *              id do passageiro
   * @return string que contem a informacao relevante do passageiro especifico
   */

  public String getPassenger(int id) throws NoSuchPassengerIdException {
    String s = "";
    if(_passengers.get(id)==null){
      throw new NoSuchPassengerIdException(id);
    }
    else{
    Passenger p = (Passenger) _passengers.get(id);
      s += p.getPassengerId() + "|" + p.getPassengerName() + "|" + p.getPassengerState() + "|" + p.getPassengerNitin() + "|" + String.format("%.2f",p.getPassengerCost()) + "|" + p.getPassengerTotalTime()+ "\n";
    return s;
    }
  }

  public void removePassenger(int id){
    _passengers.remove(id);
  }

  public String getLongestService(){
    int soma=0;
    String s="";
    for(Integer key : _services.keySet()){
      soma=0;
      Services se = _services.get(key);
      Map<LocalTime,Station> stations = se.getStations();
      for(LocalTime keyz : stations.keySet()){
        soma+=1;
      }
      if(soma>soma_max){
          soma_max =soma;
          longestServiceId=se.getServiceId();
      }
    }
    Services se1 = _services.get(longestServiceId);
    return s;
  }

  /**
   * Funcao que verifica se um passageiro existe
   * 
   * @param id
   *              id do passageiro
   */

  public void getPassengerExistence(int id) throws NoSuchPassengerIdException{
    if(_passengers.get(id)==null)
      throw new NoSuchPassengerIdException(id);
  }

  /** @return a string que contem toda a informacao relevante dos passageiros */


  public String getPassengers() { 
    String s = "";
    for (Integer key : _passengers.keySet()){
      Passenger p = (Passenger) _passengers.get(key);
      s += p.getPassengerId() + "|" + p.getPassengerName() + "|" + p.getPassengerState() + "|" + p.getPassengerNitin() + "|" + String.format("%.2f",p.getPassengerCost()) + "|" + p.getPassengerTotalTime()+ "\n";
    }
    return s;
  }

  /**
   * Funcao que vai buscar a informacao relevante de um determinado servico
   * 
   * @param id
   *              id do servico
   * @return string que contem a informacao relevante dos servico especifico
   */

  public String getService(int id) throws NoSuchServiceIdException{
    String s="";
    if(_services.get(id)==null){
      throw new NoSuchServiceIdException(id);
    }
    else{
    Services se = (Services) _services.get(id);
    s += "Serviço #" + se.getServiceId() + " @ " + String.format("%.2f",se.getServiceCost()) + "\n";
    Map<LocalTime,Station> _stations = se.getStations();
    for (LocalTime keys : _stations.keySet()){
      Station st = (Station) _stations.get(keys);
      s+=st.getStationLocalTime().toString() + " " + st.getStationName() + "\n";
    }
    return s;
    }
  }

  /** @return a string que contem toda a informacao relevante dos servicos */


  public String getServices() {
    String s = "";
    for (Integer key : _services.keySet()){
      Services se = (Services) _services.get(key);
       s += "Serviço #" + se.getServiceId() + " @ " + String.format("%.2f",se.getServiceCost()) + "\n";
      Map<LocalTime,Station> _stations = se.getStations();
      for (LocalTime keys : _stations.keySet()){
          Station st = (Station) _stations.get(keys);
          s+=st.getStationLocalTime().toString() + " " + st.getStationName() + "\n";
        }
      
    }
    return s;
  }


  /** @return todos os servicos */


  public Map<Integer,Services> getServices2(){
    return _services;
  }

  /**
   * Funcao que vai substituir o TreeMap que contem os servicos
   * 
   * @param t
   *              TreeMap de servicos
   */

  public void setServicesCopy(Map<Integer,Services> t){
    _services=t;
  }

  /**
   * Funcao que buscar toda a informacao relevante dos servicos que comecam numa determinada estacao 
   * 
   * @param name
   *              nome da estacao de inicio
   * @return string que contem a informacao relevante dos servicos especificos
   */

  public String getServiceFromDepartingStation(String name)throws NoSuchStationNameException{
    List<Services> list = new ArrayList<Services>(_services.values());
    Collections.sort(list,new DepartureLocalTimeComparator());
    String s="";
    boolean v=false;
    for (Services se : list){
      Map<LocalTime,Station> _stationsv = se.getStations();
      for (LocalTime keys : _stationsv.keySet()){
          Station st = (Station) _stationsv.get(keys);
          if(st.getStationName().equals(name))
            v=true;
      }
    }
    if (v==false) throw new NoSuchStationNameException(name);
    for (Services se : list){
      if(se.getDepartureStation().getStationName().equals(name)){
        s += "Serviço #" + se.getServiceId() + " @ " + String.format("%.2f",se.getServiceCost()) + "\n";
        Map<LocalTime,Station> _stations = se.getStations();
        for (LocalTime keys : _stations.keySet()){
          Station st = (Station) _stations.get(keys);
          s+=st.getStationLocalTime().toString() + " " + st.getStationName() + "\n";
        }
      }
    }
    return s;
  }

  /**
   * Funcao que buscar toda a informacao relevante dos servicos que acabam numa determinada estacao 
   * 
   * @param name
   *              nome da estacao de fim
   * @return string que contem a informacao relevante dos servicos especificos
   */

  public String getServiceFromArrivingStation(String name)throws NoSuchStationNameException{
    List<Services> list = new ArrayList<Services>(_services.values());
    Collections.sort(list,new ArrivalLocalTimeComparator());
    String s="";
    boolean v=false;
    for (Services se : list){
      Map<LocalTime,Station> _stationsv = se.getStations();
      for (LocalTime keys : _stationsv.keySet()){
          Station st = (Station) _stationsv.get(keys);
          if(st.getStationName().equals(name))
            v=true;
      }
    }
    if (v==false) throw new NoSuchStationNameException(name);
    for (Services se : list){
      if(se.getArrivalStation().getStationName().equals(name)){
        s += "Serviço #" + se.getServiceId() + " @ " + String.format("%.2f",se.getServiceCost()) + "\n";
        Map<LocalTime,Station> _stations = se.getStations();
        for (LocalTime keys : _stations.keySet()){
          Station st = (Station) _stations.get(keys);
          s+=st.getStationLocalTime().toString() + " " + st.getStationName() + "\n";
        }
      }
    }
    return s;
  }



  /** @return a string que contem toda a informacao relevante dos itinerarios */

  public String getItineraries(){
    _itcounter=1;
    String s = "";
    for (Integer key : _passengers.keySet()){
      Passenger p = (Passenger) _passengers.get(key);
      s += "== Passageiro " + p.getPassengerId() + ": " + p.getPassengerName() + " ==\n\n";
      Map<Integer,Itinerary> _itineraries = p.getItineraries();
      for (Integer keys : _itineraries.keySet()){
          Itinerary it = (Itinerary) _itineraries.get(keys);
          s+="Itinerário "+ _itcounter++ + " para " + it.getItineraryLocalDate() + " @ " +  String.format("%.2f",it.getItineraryCost()) + "\n";
          s+= it.getItineraryServices();
      }
      
    }
    return s;
  }


  /**
     * Funcao que vai retornar o numero de itinerarios de um passageiro
     * 
     * @param id
     *              id do passageiro
     * @return inteiro que corresponde ao total de itinerarios de um passageiro
     */



  public int PassengerNumberOfItineraries(int id){
    int soma=0;
    Passenger p = _passengers.get(id);
    Map<Integer,Itinerary> _itineraries = p.getItineraries();
    for (Integer keys : _itineraries.keySet()){
      soma+=1;
    }
    return soma;
  }

  /**
   * Funcao que vai buscar a informacao relevante dos itinerarios de um passageiro especifico
   * 
   * @param id
   *              id do passageiro
   * @return string que contem a informacao relevante dos itinerarios
   */


  public String getPassengerItineraries(int id) {
    _itcounter=1;
    String s="";
    Passenger p = _passengers.get(id);
    s += "== Passageiro " + p.getPassengerId() + ": " + p.getPassengerName() + " ==\n";
    Map<Integer,Itinerary> _itineraries = p.getItineraries();
    List<Itinerary> list = new ArrayList<Itinerary>(_itineraries.values());
    Collections.sort(list,new ItineraryDateComparator());
      for (Itinerary it : list){
          s+="\nItinerário "+ _itcounter++ + " para " + it.getItineraryLocalDate() + " @ " +  String.format("%.2f",it.getItineraryCost()) + "\n";
          s+= it.getItineraryServices() ;
      }
    return s;
  }




  /**
   * Funcao que vai retornar um determinado itinerario
   * 
   * @param passengerId
   *              id do passageiro
   * @param itineraryNumber
   *              id do itinerario 
   * @return itinerario
   */

  public Itinerary getItineraryFromChoices(int passengerId,int itineraryNumber){
    Itinerary it = _itineraries.get(itineraryNumber);
    return it;
  }

  /**
   * Funcao que vai associar um itinerario a um passageiro
   * 
   * @param id
   *              id do passageiro
   * @param it
   *              itinerario a associar
   */


  public void commitItinerary(int id,Itinerary it){
    Passenger p = _passengers.get(id);
    p.addItinerary(it);
  }


  /**
   * Funcao que vai procurar quais os melhores itinerarios que podem ser escolhidos
   * 
   * @param passengerId
   *              id do passageiro
   * @param departureStation
   *              nome da estacao de partida
   * @param arrivalStation
   *              nome da estacao de chegada
   * @param departureDate
   *              data de partida
   * @param departureTime
   *              hora minima de partida
   * @return string que contem a informacao dos itinerarios que podem ser escolhidos
   */

  public String searchItineraries(int passengerId, String departureStation, String arrivalStation,String departureDate,String departureTime){
      _itineraries.clear();
      _itineraryNumber=1;
      boolean _departure=false;
      boolean _arrival=false;
      boolean direto=false;
      List<Services> list = new ArrayList<Services>(_services.values());
      for(Services se1 : list){
        Services se2=new Services(se1.getServiceId(),se1.getServiceCost());
        Map<LocalTime,Station> stations = se1.getStations();
        for(LocalTime keys : stations.keySet()){
          Station st=stations.get(keys);
          if(st.getStationName().equals(departureStation) && (st.getStationLocalTime().isAfter(LocalTime.parse(departureTime)) || st.getStationLocalTime().equals(LocalTime.parse(departureTime)))){
            _departure=true;
            _departureTime=st.getStationLocalTime();
          }
          if(st.getStationName().equals(arrivalStation)){
            _arrival=true;
            _arrivalTime=st.getStationLocalTime();
          }
        }
        if(_departure==true && _arrival==true){
          direto=true;
          if(_arrivalTime.isBefore(_departureTime)) return "";
          DirectConnectionAux(se1,se2,stations,passengerId);
        }
        _departure=false;
        _arrival=false;
      }
      if(direto==true) return ShowItineraryChoices(departureDate);
       for(Services se1 : list){
        Services se2=new Services(se1.getServiceId(),se1.getServiceCost());
        Map<LocalTime,Station> stations = se1.getStations();
        for(LocalTime keys : stations.keySet()){
          Station st=stations.get(keys);
          if(st.getStationName().equals(departureStation) && (st.getStationLocalTime().isAfter(LocalTime.parse(departureTime)) || st.getStationLocalTime().equals(LocalTime.parse(departureTime)))){
            _departure=true;
            _departureTime=st.getStationLocalTime();
          }
          if(st.getStationName().equals(arrivalStation)){
            _arrival=true;
            _arrivalTime=st.getStationLocalTime();
          }
        }
        if(_departure==true && _arrival==false){
          List<Station> stations_list = new ArrayList<Station>(stations.values());
          List<Services> services_list = new ArrayList<Services>(_services.values());
          stations_list.remove(departureStation);
          services_list.remove(se1);
          for(Station st2 : stations_list){
            if(searchItinerariesAux(passengerId,st2.getStationName(),arrivalStation,departureDate,st2.getStationLocalTime().toString(),services_list) ==1)
              return ShowItineraryChoices(departureDate);
          }
        }
        _departure=false;
        _arrival=false;
        }
      return "";
  }

  /**
   * Funcao que vai procurar quais os melhores itinerarios que podem ser escolhidos
   * 
   * @param departureDate
   *              data de partida
   * @return string que contem a informacao relevante dos itinerarios que podem ser escolhidos
   */

  public String ShowItineraryChoices(String departureDate){
    _itcounter=1;
    String s="";
    for (Integer keys : _itineraries.keySet()){
        Itinerary it = (Itinerary) _itineraries.get(keys);
        s+="\nItinerário "+ _itcounter++ + " para " + departureDate + " @ " +  String.format("%.2f",it.getItineraryCost()) + "\n";
        s+= it.getItineraryServices();
    }
    return s;
  }

  /**
   * Funcao auxiliar que vai procurar quais os melhores itinerarios que podem ser escolhidos
   * 
   * @param passengerId
   *              id do passageiro
   * @param departureStation
   *              nome da estacao de partida
   * @param arrivalStation
   *              nome da estacao de chegada
   * @param departureDate
   *              data de partida
   * @param departureTime
   *              hora minima de partida
   * @param services
   *              lista de servicos que podem ser percorridos
   * @return inteiro que indica se encontrou um caminho direto ou nao
   */


  public int searchItinerariesAux(int passengerId, String departureStation, String arrivalStation,String departureDate,String departureTime,List<Services> services){
      boolean _departure=false;
      boolean _arrival=false;
      boolean direto=false;
      for(Services se1 : services){
        Services se2=new Services(se1.getServiceId(),se1.getServiceCost());
        Map<LocalTime,Station> stations = se1.getStations();
        for(LocalTime keys : stations.keySet()){
          Station st=stations.get(keys);
          if(st.getStationName().equals(departureStation) && (st.getStationLocalTime().isAfter(LocalTime.parse(departureTime)) || st.getStationLocalTime().equals(LocalTime.parse(departureTime)))){
            _departure=true;
            _departureTime=st.getStationLocalTime();
          }
          if(st.getStationName().equals(arrivalStation)){
            _arrival=true;
            _arrivalTime=st.getStationLocalTime();
          }
        }
        if(_departure==true && _arrival==true){
          direto=true;
          if(_arrivalTime.isBefore(_departureTime)) return 0 ;
          DirectConnectionAux(se1,se2,stations,passengerId);
          return 1;
        }
        _departure=false;
        _arrival=false;
      }
  
      return 0;
  }

  /**
   * Funcao auxiliar que vai procurar quais os melhores itinerarios que podem ser escolhidos
   * 
   * @param se1
   *              servico especifico
   * @param se2
   *              servico especifico
   * @param stations
   *              conjunto de estacoes a serem percorridas
   * @param passengerId
   *              id do passageiro
   */

  public void DirectConnectionAux(Services se1,Services se2,Map<LocalTime,Station> stations,int passengerId){
          Passenger p = _passengers.get(passengerId);
          Itinerary it = new Itinerary(passengerId);
          for(LocalTime keyz : stations.keySet()){
            Station s= stations.get(keyz);
            if(s.getStationLocalTime().equals(_departureTime)){
              se2.addStation(s);
              se2.addDepartureStation(s);
            }
            if(s.getStationLocalTime().equals(_arrivalTime)){
              se2.addStation(s);
              se2.addArrivalStation(s);
            }
            if(s.getStationLocalTime().isAfter(_departureTime) && s.getStationLocalTime().isBefore(_arrivalTime)){
              se2.addStation(s);
            }
          }
          Station d_orig_station=se1.getDepartureStation();
          Station a_orig_station=se1.getArrivalStation();
          Duration dtotal=Duration.between(d_orig_station.getStationLocalTime(), a_orig_station.getStationLocalTime());
          Duration dparcial=Duration.between(_departureTime,_arrivalTime);
          Double costtotal=se1.getServiceCost();
          Long dparcial1=dparcial.toMinutes();
          Long dtotal1=dtotal.toMinutes();
          Double dparcial2=Double.longBitsToDouble(dparcial1);
          Double dtotal2=Double.longBitsToDouble(dtotal1);
          Double costit=costtotal*(dparcial2/dtotal2);
          se2.setServiceCost(costit);
          it.addToItineraryCost(costit);
          it.addService(se2);
          it.setItineraryId(_itineraryNumber);
          _itineraries.put(_itineraryNumber++,it);
  }


}
