
import java.util.ArrayList;


public class ProjectBuilder {
	
	// Definiere Konstanten
	static int ANZAHL_PERIODEN = 10;
	private final static int ANZAHL_RESSOURCEN = 4; 
	
	private int kapazitaeten[];
	
	// Stapel initialisieren
	private Job[] aStapel;
	private Job[] bStapel;
	
	
	// Erstelle Ressourcen
	private ArrayList<ArrayList<ArrayList<Job>>> ressource = erstelleRessourcen();
	
	// Erstelle Token
	private Token token;
	private int gesResA;
	private int gesResB;
	
	
	public ProjectBuilder(Job[] _aStapel, Job[] _bStapel, int[] _kapazitaeten){
		this.aStapel = _aStapel;
		this.bStapel = _bStapel;
		this.token = new Token(aStapel, bStapel);	
		this.kapazitaeten = _kapazitaeten;
	}
	
	
	/**
	 * Berechnet einen Projektplan aus 2 �bergebenen Job Stapeln
	 * @return R�ckgabewert ist eine 3 dimensionale Ressourcen ArrayList
	 * @throws Exception
	 */
	public ArrayList<ArrayList<ArrayList<Job>>> buildProject() throws Exception{
	
		// Initialisiere St�nde
		// Die St�nde geben den n�chsten einzuplanenden Job aus dem Stapel an.
		int aStand=0;
		int bStand=0;
		
		// F�r die Anzahl der Summe aus beiden Stapeln werden Jobs eingeplant		
		for(int i=0; i<aStapel.length + bStapel.length; i++){
			
			// Wir pr�fen welcher Agent den Token bekommt
			int agent = token.getToken(getUsedRes(AccessMediator.ID_A), getUsedRes(AccessMediator.ID_B));
			
			// Wenn Agent 1 ist, dann benutze den A Stapel
			if(agent==AccessMediator.ID_A){
				// Finde fr�hest m�gliche Periode, in den der Job eingeplant werden kann
				int periode = findeFreiePeriode(aStapel[aStand]);
				// Buche Ressource zum ermittelten Zeitpunkt
				bucheRessource(aStapel[aStand], periode);
				// Setze Startperiode in Job Objekt
				aStapel[aStand].setStartperiode(periode);
				
				// Erh�he verbrauchte Kapazit�t f�r Token
				setUsedRes(agent, aStapel[aStand]);
				
				// Erh�he Z�hlerstand f�r Stapelabarbeitung
				aStand++;
			}
			
			// Wenn Agent 2 ist, dann benutze den B Stapel
			if(agent==AccessMediator.ID_B){
				int periode = findeFreiePeriode(bStapel[bStand]);
				bucheRessource(bStapel[bStand], periode);
				bStapel[bStand].setStartperiode(periode);
				setUsedRes(agent, bStapel[bStand]);
				bStand++;
			}			
		}
			return ressource;
	} // Ende Main	
	
	/**
	 * Setter Methode zum Hinterlegen verbrauchter Ressourcen eines Agenten
	 * @param id ist die ID des Agenten (Stapels)
	 * @param aktJob ist der Job um dessen Ressourcen erh�ht werden soll
	 */
	private void setUsedRes(int id, Job aktJob){
		
		// Unterscheidung zwischen den beiden Agenten
		if (id == AccessMediator.ID_A){
			for (int i= 0; i < aktJob.getResourcesArray().length; i++){
				gesResA += aktJob.getDuration() * aktJob.getResourcesArray()[i];
			}
		} else if (id == AccessMediator.ID_B){
			for (int i= 0; i < aktJob.getResourcesArray().length; i++){
				gesResB += aktJob.getDuration() * aktJob.getResourcesArray()[i];
			}
		} else{
			// Exception Fehler
			throw new IllegalArgumentException("Fehler: Es wurde eine falsche ID uebergeben!");
		} // Ende Agentenunterscheidung		
	} // Ende Methode
	
	/**
	 * Getter Methode zur Ermittlung der verbrauchten Ressourcen pro Agent
	 * @param id ist die ID des Agenten
	 * @return ist die Gesamtzahl an ben�tigten Ressourcen
	 */
	private int getUsedRes(int id){
		if (id ==AccessMediator.ID_A){
			return gesResA;
		} else if (id == AccessMediator.ID_B){
			return gesResB;
		} else{
			// Exception Fehler
			throw new IllegalArgumentException("Fehler: Es wurde eine falsche ID uebergeben!"); 
		}
		
		
	}

/**
 * Die Methode findeFreiePeriode() liefert die n�chst m�gliche Periode zur�ck, zu der ein �bergebenes Job Objekt starten kann
 * @param job ist das Job Objekt zu dem der fr�heste Starttermin gefunden werden soll
 * @return ist die Periode zu der der Job fr�hestens starten kann.<br />Dabei werden freie Resourcenkapazit�ten und Abh�ngigkeiten bereits ber�cksichtigt.
 * @throws Exception 
 */
	private int findeFreiePeriode(Job job) throws Exception{
	boolean okay = true;
		// Wir beginnen damit einen freien Zeit-Slot zu finden.
		// Theoretisch kommen alle Perioden als Startperiode in Betracht. Wir fangen vorne an -> Startposition
		for(int iStartPeriode=0; iStartPeriode<ANZAHL_PERIODEN; iStartPeriode++){
			
			// Initialisierung der "okay"-Variable. 
			// Diese bleibt auf true, sofern ein Startperioden Vorschlag alle Restiktionspr�fungen bestanden haben.
			okay = true;

			// Ab der Startposition, �berpr�fen wir �ber die L�nge der Dauer in den Folgeperioden, ob Kapazit�ten frei sind
			// Daher gehen wir diese ab der Startperiode f�r n Perioden nach hinten durch
		
			int iPruefPeriode=iStartPeriode;
			do {
				
					
			
			//for(int iPruefPeriode=iStartPeriode; iPruefPeriode<(iStartPeriode+job.getDuration()); iPruefPeriode++){
				
				// Kapazit�ten sind frei, wenn alle beteiligten Ressourcen zur Periode frei sind
				// Um das zu pr�fen m�ssen wir in jeder Periode die Verf�gbarkeit aller Ressourcen pr�fen
				for(int iRessource=0; iRessource<ANZAHL_RESSOURCEN; iRessource++){
					
					// Pr�fung der Ressource 'iRessource' zum Zeitpunkt 'iPruefPeriode'
					if(checkAvailability(iRessource,iPruefPeriode, job)){

						// Ressourcen sind frei! 
						
						// Wenn der Job Vorg�nger besitzt, dann muss zus�tzlich gepr�ft werden,
						// ob diese zum Startzeitpunkt des neuen Jobs bereits beendet sein werden.
						//System.out.println("# Vorg�nger bei Job " + job.getProjectReference()+ "/" + job.getId() + ": " + job.getNrPredecessors());
						if(job.getNrPredecessors()>0){
							// Job hat Vorg�nger. Aufruf der Methode pruefeVorgaenger
							if(!pruefeVorgaenger(job,iStartPeriode)) okay = false;
						}  // Ende hat Job Vorg�nger?
						
					} else { 
						
						// Ressourcen sind belegt.
						// Diese Startperiode kann nicht benutzt werden.
						// okay ist false; Erneuter Au�enschleifenaufruf mit sp�terer Startperiode
						okay = false;  
						
						
					} // Ende if(checkAvailability)
				}	 // Ende Ressourcenschleife
				iPruefPeriode++;
			} while (iPruefPeriode<(iStartPeriode+job.getDuration()));
//			} // Ende Folgeperiodenschleife
			
			// Wenn die Variable 'okay' die Folgeperiodenschleife mit true verl�sst,
			// so gab es keine Beanstandungen. Die gepr�fte Startperiode ist g�ltig und kann zur�ck gegeben werden.
			if(okay) return iStartPeriode;
		} // Ende Startperioden Schleife
	
		// Exception ausspucken
		throw new Exception("Fehler: Es kann keine freie Periode gefunden werden!");
	} // Ende findeFreiePeriode()
	
	/**
	 * checkAvailability pr�ft, ob eine Ressource zu einer bestimmten Periode noch verf�gbart ist / gen�gend freie Kapazit�ten hat
	 * @param ressourcenId ist die ID der zu pr�fenden Ressource
	 * @param periode ist die zu pr�fende Periode
	 * @param job ist der Job f�r den freier Platz gesucht werden soll. Methode muss ja schlie�lich wissen wieviel Platz ben�tigt wird
	 * @return 'true', wenn die Ressource zur Periode noch frei ist, ansonsten 'false'
	 */
	private boolean checkAvailability(int ressourcenId, int periode, Job job){
		if(periode+job.getDuration() >= ressource.get(ressourcenId).size()){
			enlargeRessourceArray(job.getDuration());
		}
		// Wenn zur �bergebenen Periode noch kein weiterer Job eingeplant ist, dann ist auf jeden Fall noch Platz
		if(ressource.get(ressourcenId).get(periode).size()==0){
			return true; // Geht klar!
		} else {
			// Wenn schon Jobs eingeplant sind, dann muss die Summe der verbrauchten Kapazit�ten ermittelt werden.
			// Liegt die Summe der verbrauchten Kapazit�t zzgl. der einzuplanenden Kapazit�t nicht �ber der Kapazit�tsobergrenze,
			// so ist in dieser Periode noch Platz verf�gbar.
			
			// Initialisieren der Summe
			int summe = 0;
			
			// Wir addieren zur Summe die Kapazit�ten aller eingeplanten Jobs
			for(int z=0; z<ressource.get(ressourcenId).get(periode).size();z++){
				summe += ressource.get(ressourcenId).get(periode).get(z).getResourcesArray()[ressourcenId];
			}
			
			// Pr�fen ob noch gen�gend Kapazit�t zur Verf�gung steht
			if(summe+job.getResourcesArray()[ressourcenId]>kapazitaeten[ressourcenId]){
				return false; // nicht gen�gend Kapazit�t frei
			} else {
				return true; // Kapazit�t reicht noch aus
			}
			
		} // Ende IF(Bereits Jobs in Periode verplant)	
	} // Ende Methode checkAvailability()
	
	
	/**
	 * Die Methode bucht alle Ressourcen f�r einen Job ab einer bestimmten startPeriode
	 * @param job ist der Job der eingeplant werden soll
	 * @param startPeriode ist die Periode ab der der Job starten soll
	 */
	private void bucheRessource(Job job, int startPeriode){
		
		// Wir buchen Ressource f�r Ressource. Wir gehen jede einmal durch
		for(int i = 0; i<ANZAHL_RESSOURCEN;i++){
			
			// Pr�fe, ob die aktuelle Ressource �berhaupt beansprucht wird
			if(job.getResourcesArray()[i]>0){
				// Wenn die Ressource beansprucht wird,
				// dann gehe sie Periode f�r Periode durch (Ab Startperiode bis Startperiode+Dauer)
				for(int j=startPeriode; j<(startPeriode+job.getDuration()); j++){
					
					// Verkette den zu buchenden Job mit der aktuellen Periode in der aktuellen Ressource
					ressource.get(i).get(j).add(job);
					
				} // Ende Periodenschleife
			} // Ende IF Ressource betroffen

		} // Ende Ressourcenschleife

	} // Ende bucheRessource();
	
	/**
	 * Diese Methode pr�ft, ob alle definierten Vorg�nger zum Zeitpunkt x erfolgreich beendet wurden.
	 * @param job ist der Job dessen Vorg�nger gepr�ft werden sollen
	 * @param startPeriode ist die Periode zu der die Vorg�nger beendet sein m�ssen
	 * @return 'true', wenn alle Vorg�nger erfolgreich fertig sind, ansonsten 'false'
	 * @throws Exception 
	 */
	private boolean pruefeVorgaenger(Job job, int startPeriode) throws Exception{
		
		// Initialisierung der okay Variable
		// Bleibt true, wenn alle Pr�fprozesse durchlaufen werden, ohne auf Konflikte (unbeendete Vorg�nger) zu treffen
		boolean okay = true;

		
		// Zum Pr�fen gehen wir alle ben�tigten Vorg�nger einmal durch und schauen sie uns an...
		for(int iVorgaenger=0;  iVorgaenger<job.getNrPredecessors(); iVorgaenger++){
		
			
			// Ermittle die letzte Periode, in der der Vorg�nger bearbeitet wird
			int letztePeriode = getJobObject(job.getPredecessors().get(iVorgaenger), job.getProjectReference()).getStartPeriod()+getJobObject(job.getPredecessors().get(iVorgaenger), job.getProjectReference()).getDuration();
			
			// Schaue, ob die vorgeschlagene Startperiode nach der letzten Periode des Vorg�ngers liegt
			if(letztePeriode>startPeriode){
				// Wenn nicht, so kann der Nachfolger noch nicht starten.
				okay = false;
			} // Ende Pr�fe Abschluss des Vorg�ngers
		} // Ende Alle Vorg�nger Schleife
		
		// Gebe 'okay' Variable zur�ck. Ist diese 'false', so waren die Vorg�nger noch nicht beendet.
		// Die aufrufende Methode wird daraufhin die Startperiode nach hinten verschieben und diese Methode erneut aufrufen
		return okay;
	} // Ende pruefeVorgaenger()
	
	
	/**
	 * Diese Methode erstellt die ArrayList f�r die Ressourcen.<br />
	 * Die erste Menge ist die RessourcenID, die zweite ist die Periode,<br />
	 * die dritte ist die Menge aller eingeplanten Jobs.
	 * @return gibt eine initialisierte ArrayList f�r die Ressourcen zur�ck.
	 */
	private ArrayList<ArrayList<ArrayList<Job>>> erstelleRessourcen(){
		// Erzeuge neue zweidimensionale ArrayList
		// Die erste Menge ist die Ressource, die zweite Menge ist die Periode
		ArrayList<ArrayList<ArrayList<Job>>> ressources = new ArrayList<ArrayList<ArrayList<Job>>>();
		
		// Initialisiere den Shit
		for(int i=0; i<ANZAHL_RESSOURCEN; i++){
			ressources.add(new ArrayList<ArrayList<Job>>());
			for(int j=0; j<ANZAHL_PERIODEN;j++){
				ressources.get(i).add(new ArrayList<Job>());
			}
		}
		return ressources;
	}
	
	private void enlargeRessourceArray(int jobduration){
		for(int i = 0; i < jobduration; i++){
		 ressource.get(0).add(new ArrayList<Job>());
		 ressource.get(1).add(new ArrayList<Job>());
		 ressource.get(2).add(new ArrayList<Job>());
		 ressource.get(3).add(new ArrayList<Job>());
		 ANZAHL_PERIODEN++;
		}
	}

	/**
	 * Methode liefert das Job Objekt zu einer Job ID zur�ck.<br /> Da sich die Jobs in zwei verschiedenen Stapeln befinden,
	 * ist zus�tzlich die Besitzer ID (1 oder 2) mitzugeben
	 * @param id ist die Job ID
	 * @param agent ist die Besitzer ID.
	 * @return R�ckgabewert ist das entsprechende Objekt vom Datentyp Job
	 * @throws Exception 
	 */
	private Job getJobObject(int id, int agent) throws Exception{
		// Wenn agent == 1, dann handelt es sich um ein Objekt aus dem A Stapel
		if(agent == AccessMediator.ID_A){
			
			// Gehe Stapel komplett durch und suche nach �bereinstimmung mit JobID und BesitzerID
			for(int i=0; i<aStapel.length;i++){
				if(aStapel[i].getId() == id && aStapel[i].getProjectReference()==agent){
					return aStapel[i]; // Bei Treffer, gebe Objekt zur�ck
				}
			}
			
		// Wenn agent == 2, dann handelt es sich um ein Objekt aus dem B Stapel
		} else {
			
			// Gehe Stapel komplett durch und suche nach �bereinstimmung mit JobID und BesitzerID
			for(int i=0; i<bStapel.length;i++){
				if(bStapel[i].getId() == id && bStapel[i].getProjectReference()==agent){
					return bStapel[i]; // Bei Treffer, gebe Objekt zur�ck
				}
			}
		} // Ende Agentunterscheidung
		// Kein passendes Objekt gefunden
		
		// Exception ausspucken
		throw new Exception("Fehler: Es kann kein passendes Objekt gefunden werden!");
	}
	
	/**
	 * Methode zur Ausgabe der Projektdauer eines Agenten
	 * @param agent ist die ID des Agenten
	 * @return gibt die Projektdauer des Agenten zur�ck
	 */
	public int getProjektDauer(int agent){
		// Max wird die Endperiode des Jobs sein, der am sp�testen eingeplant/fertig wird. Er ist damit der letzte im Projekt
		int max = 0;
		
		// Unterscheidung zwischen den Agenten
		if(agent==AccessMediator.ID_A){
			// Gehe alle eingeplanten Jobs im Stapel durch
			for(int i=0; i<aStapel.length;i++){
				// Ist der aktuelle Job sp�ter als das bisherige Maximum?
				if((aStapel[i].getStartPeriod()+aStapel[i].getDuration())>max){
					// Setze das neue Maximum
					max=(aStapel[i].getStartPeriod()+aStapel[i].getDuration());
				}
			}
		} else {
			for(int i=0; i<bStapel.length;i++){
				if((bStapel[i].getStartPeriod()+bStapel[i].getDuration())>max){
					max=(bStapel[i].getStartPeriod()+bStapel[i].getDuration());
				}
			}	
		}
		return max; // Ausgabe des Resultats
	} // Ende Methode getProjektDauer()
	
} // Ende Klasse
