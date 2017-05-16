import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class AccessMediator {

	// Singleton-Objekt dieser Klasse
	private static AccessMediator accessMediator = null;

	/**
	 * fixe Anzahl von Runden, wird runtergez�hlt.
	 */
	static final int ANZAHL_RUNDEN = 15;
	/**
	 * aktuelle Runde
	 */
	private int actRunde;

	// In diese Arrays wird nach Aufruf des Benutzers die JobLISTE (d.h. die
	// gueltige Liste der
	// Abfolge gespeichert.
	private int[] arrayA = null;
	private int[] arrayB = null;

	// Diese Arrays enthalten die gueltige JobLISTE der aktuellen OPTIMALLOESUNG
	private int[] arrayAOld = null;
	private int[] arrayBOld = null;
	private int[] answerArrayA = null;
	private int[] answerArrayB = null;

	// Diese Arrays sind ein Zwischenspeicher fuer die JobArrays, die spaeter an
	// die berechnende Klasse
	// uebergeben werden.
	private Job[] bufferArrayA = null;
	private Job[] bufferArrayB = null;

	/**
	 * int-Array, dass die beiden Dauern der Plaene enthaelt.
	 */
	private final int dauer[];
	/**
	 * enth�lt die optimalen Dauern: jeweils nur die optimalen Dauern der
	 * aktuellen Runde.
	 */
	private int optDauer[];

	/**
	 * dieses Array wird am Ende der Verhandlung gefuellt, 1. Dimension =
	 * Periode, 2. Dimension = Jobs in der Periode
	 */
	private ArrayList<ArrayList<Job>> finalResultArrayListA;
	private ArrayList<ArrayList<Job>> finalResultArrayListB;

	/**
	 * Standardantworten (standardmaessig ist keine Antwort vorhanden - wird
	 * gesetzt in processAnswer())
	 */
	private Answers answerA = Answers.noAnswer;
	private Answers answerB = Answers.noAnswer;

	/**
	 * Indikator f�r die erste Runde. Wichtig f�r die Speicherung der
	 * Optimalloesung in der ersten Runde.
	 */
	private boolean firstRound = false;

	/**
	 * enthaelt alle optimalen Dauern des jeweiligen Agenten. Die optimale Dauer
	 * wird in jeder Runde hinterlegt, um einen Verlauf darstellen zu k�nnen
	 * (d.h. es wird auch gespeichert wenn keine neue optimale Dauer erreicht
	 * wurde).
	 */
	private ArrayList<Integer> optDurationsA;
	private ArrayList<Integer> optDurationsB;

	/**
	 * enthaelt die beteiligten Projekte
	 */
	private final ArrayList<Project> projectList;

	// Konstanten, die als Benutzer-IDs verwendet werden. Bis jetzt gibt es nur
	// zwei Benutzer!!
	public static final int ID_A = 0;
	public static final int ID_B = 1;

	/**
	 * Rueckgabewert des ProjectBuilder
	 */
	private ArrayList<ArrayList<ArrayList<Job>>> ressource;

	// --------------------------------------------------------------------------------------------
	// CONSTRUCTOR / SINGLETON
	// --------------------------------------------------------------------

	/**
	 * Konstruktor - setzt die Dateinamen und uebergibt sie an den Parser.
	 * Bekommt dann die zwei Projekte zurueck und initialisiert die
	 * bufferArrays.
	 * 
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	private AccessMediator(String location)
			throws ParserConfigurationException, SAXException, IOException {
		// Dateipfade setzen
		String xmlFilePath = location + "mp_j30_a2_nr1.xml";
		String jobFilePath = location + "j3015_5.sm";

		// Projekt parsen - beide Projekte werden in einer ArrayList<Project>
		// gespeichert
		this.projectList = Parser.parseProjectData(xmlFilePath, jobFilePath);
		Project projectA = projectList.get(0);
		Project projectB = projectList.get(1);

		// Initialisieren zweier Job-Arrays, die spaeter eine sortierte Jobliste
		// darstellen
		// und so an den ProjectBuilder uebergeben werden.
		this.bufferArrayA = new Job[projectA.getJobs().size()];
		this.bufferArrayB = new Job[projectB.getJobs().size()];

		// Initialisieren des Arrays, das spaeter die Dauer beider Projekte
		// erhalten wird
		this.dauer = new int[2];

		// fuellen der zuvor erstellten JobArrays mit den Jobs aus den Projekten
		fillJobArrays(projectA, projectB);

		this.actRunde = ANZAHL_RUNDEN;
	}

	/**
	 * singleton...
	 * 
	 * @return
	 */
	public static AccessMediator getAccessMediatorObject(String location) {
		if (accessMediator == null) {
			try {
				accessMediator = new AccessMediator(location);
			} catch (ParserConfigurationException e) {
				System.out.println(e);
			} catch (SAXException e) {
				System.out.println(e);
			} catch (IOException e) {
				System.out.println(e);
			}
			return accessMediator;
		} else {
			return accessMediator;
		}
	}

	// --------------------------------------------------------------------------------------------
	/**
	 * Fuellt die zwei JobArrays der Klasse AccessMediator mit den Jobs aus den
	 * eingelesenen Projekten. Sortierung von 1 - 32
	 * 
	 * @param projectA
	 * @param projectB
	 */
	private void fillJobArrays(Project projectA, Project projectB) {
		for (int i = 0; i < this.bufferArrayA.length; i++) {
			this.bufferArrayA[i] = projectA.getJobs().get(i);
			this.bufferArrayB[i] = projectB.getJobs().get(i);
		}
	}

	/**
	 * nimmt die uebergebene Liste und das Job-Array des aktuellen Projekts, und
	 * erstellt ein Job-Array in dem die Jobs gemaess der Liste enthalten sind.
	 * Muss fuer beide Agenten aufgerufen werden.
	 * 
	 * @param order
	 * @param jobs
	 * @return das gueltige Job-Array, das zur weiteren Berechnung benoetigt
	 *         wird
	 */
	private Job[] sortJobs(int[] order, Job[] jobs) {
		Job[] newArray = new Job[order.length];
		for (int i = 0; i < newArray.length; i++) {
			newArray[i] = jobs[order[i] - 1];
		}
		return newArray;
	}

	/**
	 * Ueberprueft, ob beide JobListenArrays schon gesetzt wurden
	 * 
	 * @return true nur, wenn beide Arrays gesetzt wurden (= beide Antworten
	 *         sind da)
	 */
	public boolean checkArrayStatus() {
		if (arrayA != null && arrayB != null)
			return true;
		else
			return false;
	}

	/**
	 * Ueberprueft den Status der "Alten" Arrays - das sind die Arrays, die die
	 * Sortierung der Optimalloesung enthalten. Wenn diese Arrays Null sind,
	 * wurde noch ueberhaupt nicht mit der Verhandlung begonnen - die
	 * Optimalloesung wird aus den ersten beiden Listen berechnet.
	 * 
	 * @return false wenn eins oder beide Arrays Null sind. True heisst, dass
	 *         mit der Verhandlung begonnen werden kann.
	 */
	private boolean checkOldArrayStatus() {
		if (arrayAOld == null && arrayBOld == null && arrayA != null
				&& arrayB != null) {
			return true; // set optimal solution!!
		} else {
			return false;
		}
	}

	/**
	 * weist das uebergebene Array dem entsprechenden Array zu. Ruft dann die
	 * entsprechende Setter-Methode auf.
	 * 
	 * @param id
	 * @param list
	 */
	private void setArray(int id, int[] list) {
		if (id == ID_A) {
			setArrayA(list);
		} else if (id == ID_B) {
			setArrayB(list);
		}
	}

	// --------------------------------------------------------------------------------------------
	// Getter / Setter fuer die Arrays:
	private void setArrayA(int[] list) {
		this.arrayA = list;
	}

	private void setArrayB(int[] list) {
		this.arrayB = list;
	}

	private void setArrayAOld(int[] list) {
		this.arrayAOld = list;
	}

	public int[] getArrayAOld() {
		return this.arrayAOld;
	}

	private void setArrayBOld(int[] list) {
		this.arrayBOld = list;
	}

	public int[] getArrayBOld() {
		return this.arrayBOld;
	}

	// --------------------------------------------------------------------------------------------
	/**
	 * Setzt die Optimalloesung, d.h. der aktuelle Inhalt der int-arrays wird in
	 * die oldArrays geschrieben.
	 */
	private void setOptimalSolution(int[] listA, int[] listB) {
		setArrayAOld(listA);
		setArrayBOld(listB);
		this.optDauer = new int[2];
		this.optDauer[0] = this.dauer[0];
		this.optDauer[1] = this.dauer[1];
	}

	/**
	 * @param id
	 * @return die zuletzt gespeicherte optimale Dauer des angegebenen Agenten
	 */
	public int getOptDauer(int id) {
		if (this.optDauer == null)
			return -1;
		return this.optDauer[id];
	}

	/**
	 * Methode zum Aufruf / Start der Berechnung. Sie uebergibt die Job-Arrays
	 * an die berechnende Klasse ProjectBuilder.
	 * 
	 * @throws Exception
	 */
	private void startCalculations() throws Exception {
		ProjectBuilder projectBuilder = new ProjectBuilder(this.bufferArrayA,
				this.bufferArrayB, this.projectList.get(0)
						.getResourcesIntArray());
		try {
			this.ressource = projectBuilder.buildProject();
		} catch (Exception e) {
			System.out
					.println("Fehler in projectBuilder.buildProject. Evtl. ungueltige Files.");
			e.printStackTrace();
			System.exit(0);
		}

		this.dauer[ID_A] = projectBuilder.getProjektDauer(ID_A);
		this.dauer[ID_B] = projectBuilder.getProjektDauer(ID_B);
		if (firstRound) {
			this.optDauer[0] = this.dauer[0];
			this.optDauer[1] = this.dauer[1];
			this.optDurationsA = new ArrayList<Integer>();
			this.optDurationsB = new ArrayList<Integer>();
			firstRound = false;
		}

		this.finalResultArrayListA = new ArrayList<ArrayList<Job>>();
		this.finalResultArrayListB = new ArrayList<ArrayList<Job>>();

		this.finalResultArrayListA = fillFinalResultArrayList(ID_A);
		this.finalResultArrayListB = fillFinalResultArrayList(ID_B);

		setActRunde();
		addOptDauer(this.optDauer[ID_A], ID_A);
		addOptDauer(this.optDauer[ID_B], ID_B);
	}

	/**
	 * Fuellt die "finalResultArrayList"s mit den Daten aus dem von
	 * projectBuilder.buildProject zu rueckgegebenen Array (dreidimensional) -
	 * Umwandlung zu zweidimensionaler ArrayList
	 * 
	 * @param id
	 * @return
	 */
	private ArrayList<ArrayList<Job>> fillFinalResultArrayList(int id) {
		ArrayList<ArrayList<Job>> periodenArrayList = new ArrayList<ArrayList<Job>>();
		periodenArrayList.add(new ArrayList<Job>());
		periodenArrayList.get(0).add(this.projectList.get(id).getJobs().get(0));

		for (int i = 0; i < ProjectBuilder.ANZAHL_PERIODEN; i++) {
			// wenn eine der Resourcen diese Periode besitzt...
			if (null != this.ressource.get(0).get(i)
					|| null != this.ressource.get(1).get(i)
					|| null != this.ressource.get(2).get(i)
					|| null != this.ressource.get(3).get(i)) {
				// ...fuege der Liste eine neue Liste hinzu...
				if (i != 0)
					periodenArrayList.add(i, new ArrayList<Job>());
				for (int j = 0; j < this.ressource.size(); j++) {
					for (int k = 0; k < this.ressource.get(j).get(i).size(); k++) {
						// ...und fuege der zugefuegten Liste die Jobs hinzu,
						// wenn sie der ID entsprechen, vorhanden sind und noch
						// nicht in der Liste stehen
						if (!isAlreadyInThisPeriod(periodenArrayList.get(i),
								this.ressource.get(j).get(i).get(k))
								&& this.ressource.get(j).get(i).get(k)
										.getProjectReference() == id) {
							periodenArrayList.get(i).add(
									this.ressource.get(j).get(i).get(k));
						}

					}
				}
			}
		}
		periodenArrayList.add(new ArrayList<Job>());
		int size = this.projectList.get(0).getJobs().size();
		periodenArrayList.get(this.dauer[id] - 1).add(
				this.projectList.get(0).getJobs().get(size - 1));
		return periodenArrayList;
	}

	/**
	 * Checker-Methode, die ueberprueft ob der angegebene Job schon in der
	 * angegebenen Liste vorhanden ist
	 * 
	 * @param periodenList
	 * @param job
	 * @return true falls ja.
	 */
	private boolean isAlreadyInThisPeriod(ArrayList<Job> periodenList, Job job) {
		for (int i = 0; i < periodenList.size(); i++) {
			if (periodenList.get(i).getId() == job.getId()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Diese Methode setzt die temporaeren Arrays auf Null zurueck, damit bei
	 * der naechsten Verhandlungsrunde wieder ueberprueft werden kann, ob schon
	 * Antworten vorhanden sind.
	 */
	private void resetArrays() {
		this.arrayA = null;
		this.arrayB = null;
	}

	// ------------------------------------------------------------------------------
	// respond
	// ------------------------------------------------------------------------------

	/**
	 * Diese Methode wird aufgerufen von respond() und setzt die
	 * Antwort-Variablen (Answer).
	 * 
	 * @param id
	 *            user ID
	 * @param answer
	 *            true = Loesung akzeptiert, false = Loesung abgelehnt
	 */
	private void processAnswer(int id, boolean answer) {
		if (answer == true) { // candidate agreed but wants to change his list
			if (id == AccessMediator.ID_A) {
				this.answerA = Answers.yes;
			} else if (id == AccessMediator.ID_B) {
				this.answerB = Answers.yes;
			} else
				throw new IllegalArgumentException("ID not valid.");
		}

		else if (answer == false) { // list should be different... will be
									// changed anyways.
			if (id == AccessMediator.ID_A) {
				this.answerA = Answers.no;
			} else if (id == AccessMediator.ID_B) {
				this.answerB = Answers.no;
			} else
				throw new IllegalArgumentException("ID not valid.");
		}
	}

	/**
	 * setzt beide Antworten auf "keine Antwort" (noAnswer) zurueck.
	 */
	private void resetAnswers() {
		this.answerA = Answers.noAnswer;
		this.answerB = Answers.noAnswer;
	}

	/**
	 * Moeglichkeit fuer die Clients, zu ueberpruefen ob der Partner schon
	 * geantwortet hat - und wenn ja, ob beide zugestimmt oder einer abgelehnt
	 * hat.
	 * 
	 * @return Answers.agreed, wenn beide zustimmen, Answers.noAnswer, wenn noch
	 *         keiner geantwortet hat, Answers.disagreed wenn einer nicht
	 *         zugestimmt hat, und Answers.waiting, wenn noch eine Antwort
	 *         fehlt.
	 */
	public Answers checkAnswerStatus() {
		if (answerA == Answers.yes && answerB == Answers.yes) {
			return Answers.agreed;
		}
		if (answerA == Answers.noAnswer && answerB == Answers.noAnswer) {
			return Answers.noAnswer;
		} else if (answerA != Answers.noAnswer && answerB != Answers.noAnswer) {
			return Answers.disagreed;
		} else
			return Answers.waiting;
	}

	// oeffentliche Methoden:
	/**
	 * gibt ein Project-Objekt zurueck, das alle relevanten Daten enthaelt.
	 * Benoetigt die Benutzer-ID!
	 */
	public Project getProjectOverview(int id) {
		checkID(id);
		return projectList.get(id);
	}

	/**
	 * actRunde - 1;
	 */
	private void setActRunde() {
		this.actRunde = this.actRunde - 1;
	}

	public int getActRunde() {
		return this.actRunde;
	}

	/**
	 * Diese Methode kann jederzeit aufgerufen werden, um den aktuellen Status
	 * des Programms abzurufen. Ist irgendein Status auf False gesetzt, wird
	 * auch false zurueckgegeben
	 * 
	 * @return
	 */
	public boolean checkStatus() {
		try {
			if ((checkAnswerStatus() == Answers.waiting)) {
				return false;
			}
			if (checkAnswerStatus() == Answers.disagreed
					|| checkAnswerStatus() == Answers.agreed) {
				return true;
			} else if (!checkArrayStatus()) {
				return false;
			}
		} catch (NullPointerException e) { // if no array has been submitted and
											// nothing is ready
			System.out.println("NullPointerException...No valid state. " + e);
			return false;
		}
		return true;
	}

	/**
	 * Benutzer ruft diese Methode auf, um seine gueltige JobListe abzuschicken.
	 * 
	 * @param id
	 *            Benutzer-ID
	 * @param list
	 *            GUELTIGE Job-Liste
	 * @return true, wenn beide arrays schon da sind - sonst false
	 */
	public synchronized void submitList(int id, int[] list) {
		checkID(id);
		resetAnswers();
		setArray(id, list);
		if (checkOldArrayStatus()) {
			firstRound = true;
			setOptimalSolution(this.arrayA, this.arrayB); // set best solution
		}
		if (checkArrayStatus()) {
			this.bufferArrayA = sortJobs(this.arrayA, this.bufferArrayA);
			this.bufferArrayB = sortJobs(this.arrayB, this.bufferArrayB);
			try {
				startCalculations();
			} catch (Exception e) {
				System.out
						.println("Could not start calculations... aborting (AccessMediator.submitList)");
				e.printStackTrace();
				System.exit(0);
			}
		} else
			return;
	}

	/**
	 * Utility um die angegebene ID zu validieren. Systemabbruch wenn die ID
	 * ungueltig ist!
	 * 
	 * @param id
	 */
	private void checkID(int id) {
		if (id != ID_A && id != ID_B) {
			System.out
					.println("Ungueltige ID in respond(...)-Methode angegeben. Abbruch...");
			System.exit(0);
		}
	}

	/**
	 * Schnittstelle nach aussen: Hiermit wird geantwortet.
	 * 
	 * @param id
	 * @param answer
	 */
	public synchronized void respond(int id, boolean answer, int[] liste) {
		checkID(id);
		processAnswer(id, answer);
		saveAnswerArray(id, liste);
		Answers status = checkAnswerStatus();
		if (answerA == Answers.yes && answerB == Answers.yes) {
			setOptimalSolution(this.arrayA, this.arrayB);

		}
		if (status != Answers.waiting && status != Answers.noAnswer) {
			resetArrays();
		}
		if (answerA != Answers.noAnswer && answerB != Answers.noAnswer) {
			goOnAndSubmitLists();
		}

	}

	/**
	 * wird von respond() aufgerufen, damit die Clients nicht extra noch
	 * submitList aufrufen muessen. Leitet also einfach nur weiter.
	 */
	private void goOnAndSubmitLists() {
		submitList(ID_A, this.answerArrayA);
		submitList(ID_B, this.answerArrayB);
	}

	/**
	 * speichert beim Aufruf von respond() die mitgeschickte Liste, da ja noch
	 * auf den Partner gewartet werden muss.
	 * 
	 * @param id
	 * @param list
	 */
	private void saveAnswerArray(int id, int[] list) {
		if (id == ID_A) {
			this.answerArrayA = list;
		}
		if (id == ID_B) {
			this.answerArrayB = list;
		}
	}

	// --------------------------------------------------------------------------------------------
	// RESULTS
	// ------------------------------------------------------------------------------------
	/**
	 * Dauer des Projekts
	 * 
	 * @param id
	 * @return -1 wenn noch keine Dauer angegeben wurde.
	 */
	public int getResultDauer(int id) {
		checkID(id);
		if (this.dauer[id] == 0) {
			return -1;
		}
		return this.dauer[id];
	}

	/**
	 * Gibt die Loesung basierend auf der zuletzt gespeicherten LOESUNG (NICHT
	 * ZWINGEND OPTIMALL�SUNG) zurueck. Diese Methode sollte erst aufgerufen
	 * werden, wenn alle Verhandlungen abgeschlossen sind.
	 * 
	 * @param id
	 * @return eine zweidimensionale ArrayList, wobei die erste Dimension die
	 *         Periode darstellt, die zweite die Jobs die in der Periode
	 *         ausgefuehrt werden.
	 */
	public ArrayList<ArrayList<Job>> getFinalResult(int id) {
		checkID(id);
		if (id == ID_A) {
			return this.finalResultArrayListA;
		} else if (id == ID_B) {
			return this.finalResultArrayListB;
		} else {
			throw new IllegalArgumentException("ID ist nicht g�ltig.");
		}

	}

	/**
	 * Gibt die Loesung basierend auf der zuletzt gespeicherten OPTIMALLOESUNG
	 * zurueck.
	 * 
	 * @param id
	 * @return eine zweidimensionale ArrayList, wobei die erste Dimension die
	 *         Periode darstellt, die zweite die Jobs die in der Periode
	 *         ausgefuehrt werden.
	 */
	public ArrayList<ArrayList<Job>> getOptimalResult(int id) {
		checkID(id);
		this.arrayA = this.arrayAOld;
		this.arrayB = this.arrayBOld;
		try {
			startCalculations();
		} catch (Exception e) {
			System.out
					.println("Something went wrong in calculation... called by getOptimalResult."
							+ e);
		}
		if (id == ID_A)
			return this.finalResultArrayListA;
		else
			return this.finalResultArrayListB;
	}

	/**
	 * wird vom Client aufgerufen, um zu ueberpruefen, ob er schon geantwortet
	 * hat.
	 * 
	 * @param id
	 * @return
	 */
	public synchronized boolean didIAnswer(int id) {
		checkID(id);
		if (id == ID_A) {
			if (answerA == Answers.noAnswer)
				return false;
			else
				return true;
		} else {
			if (answerB == Answers.noAnswer)
				return false;
			else
				return true;
		}
	}

	/**
	 * kitisch: muss aber public sein, weil vom Client aufgerufen. Setzt alle
	 * Werte zurueck, sodass eine neue Verhandlung gestartet werden kann.
	 */
	public void resetAll() {
		actRunde = ANZAHL_RUNDEN;
		arrayAOld = null;
		arrayBOld = null;
		arrayA = null;
		arrayB = null;

		if (dauer != null)
			for (int i = 0; i < dauer.length; i++)
				dauer[i] = 0;

		if (optDauer != null)
			for (int i = 0; i < optDauer.length; i++)
				optDauer[i] = -1;

		finalResultArrayListA = null;
		finalResultArrayListB = null;
		this.optDurationsA = null;
		this.optDurationsB = null;
	}

	/**
	 * fuegt den int-Wert der berechneten optimalen Dauer fuer die angegebene ID
	 * einer entsprechenden ArrayList hinzu
	 * 
	 * @param dauer
	 * @param id
	 */
	private void addOptDauer(int dauer, int id) {
		if (id == ID_A) {
			this.optDurationsA.add((dauer));
		} else {
			this.optDurationsB.add((dauer));
		}
	}

	/**
	 * Gibt die der ID entsprechende Liste aller bisher gespeicherten optimalen
	 * Dauern zurueck.
	 * 
	 * @param id
	 * @return !! null wenn die Liste noch nicht initialisiert worden ist... sie
	 *         wird initialisiert beim ersten Aufruf von startCalculations.
	 */
	public ArrayList<Integer> getOptDauerList(int id) {
		checkID(id);
		if (id == ID_A) {

			return this.optDurationsA;
		} else {
			return this.optDurationsB;
		}
	}

}
