import java.util.ArrayList;

public class TestProjectBuilder {
	// Definiere Konstanten
	private static int ANZAHL_PERIODEN = 100;

	// Erstelle 2 Dummy Stapel
	private static Job[] aStapel = erstelleStapelA();
	private static Job[] bStapel = erstelleStapelB();

	// Erstelle Dummy Kapazitaeten
	private static int kapazitaeten[] = { 10, 8, 9, 12 };

	// main() Methode
	public static void main(String[] args) throws Exception {
		ProjectBuilder jan = new ProjectBuilder(aStapel, bStapel, kapazitaeten);
		ArrayList<ArrayList<ArrayList<Job>>> ressource = jan.buildProject();
		System.out.println("Projektdauer A: " + jan.getProjektDauer(0));
		System.out.println("Projektdauer B: " + jan.getProjektDauer(1));

		macheDemoAusgabe(ressource);

	}

	// Demoausgabe
	private static void macheDemoAusgabe(
			ArrayList<ArrayList<ArrayList<Job>>> ressource) {
		System.out
				.println("\n------------------- Projektplan -----------------\n");

		for (int i = 0; i < 4; i++) {
			System.out.println("Ressource " + i + ":");

			System.out.print("Periode ");

			// Legende
			for (int z = 0; z < ANZAHL_PERIODEN; z++) {
				if (z < 100) {
					System.out.print("00000");
				}
				if (z < 10) {
					System.out.print("0");
				}
				System.out.print(z + "  ");
			}
			System.out.print("\n");

			System.out.print("# Jobs  ");

			for (int j = 0; j < ANZAHL_PERIODEN; j++) {

				if (ressource.get(i).get(j).size() == 0) {
					System.out.print("-------  ");
				} else {
					if (ressource.get(i).get(j).size() < 100) {
						System.out.print("     ");
					}
					if (ressource.get(i).get(j).size() < 10) {
						System.out.print(" ");
					}
					System.out.print(ressource.get(i).get(j).size() + "  ");
				}

			}
			System.out.print("\n");

			System.out.print("Verbr.  ");

			for (int j = 0; j < ANZAHL_PERIODEN; j++) {

				int summe = 0;

				for (int y = 0; y < ressource.get(i).get(j).size(); y++) {

					summe += ressource.get(i).get(j).get(y).getResourcesArray()[i];
				}
				if (summe < 10) {
					System.out.print("    ");
				}
				System.out.print("  " + summe + "  ");

			}
			System.out.print("\n");

			System.out.print("Jobs    ");
			for (int j = 0; j < ANZAHL_PERIODEN; j++) {
				String anz = new String();

				for (int y = 0; y < ressource.get(i).get(j).size(); y++) {
					if (anz.length() == 0) {
						anz = "" + ressource.get(i).get(j).get(y).getId();
					} else {
						anz += "," + ressource.get(i).get(j).get(y).getId();
					}

				}

				int space = 7;
				space -= anz.length();
				while (space > 0) {
					System.out.print(" ");
					space--;
				}
				System.out.print(anz + "  ");

			}
			System.out.println("\n");

		}

	}

	// ############################### DUMMY STAPEL ###########################
	// Dummy Stapel A erstellen
	private static Job[] erstelleStapelA() {

		Job[] aStapel = new Job[5];

		// A Stapel
		// 1. Element

		ArrayList<Integer> resources1 = new ArrayList<Integer>();
		ArrayList<Integer> vorgaenger1 = new ArrayList<Integer>();
		vorgaenger1.add(1);
		resources1.add(0, 5);
		resources1.add(1, 1);
		resources1.add(2, 2);
		resources1.add(3, 3);
		aStapel[0] = new Job(1, null, 2, resources1, 1);

		// 2.Element
		ArrayList<Integer> resources2 = new ArrayList<Integer>();
		ArrayList<Integer> vorgaenger2 = new ArrayList<Integer>();
		vorgaenger2.add(1);
		resources2.add(0, 3);
		resources2.add(1, 4);
		resources2.add(2, 5);
		resources2.add(3, 1);
		aStapel[1] = new Job(2, vorgaenger2, 1, resources2, 1);

		// 3.Element
		ArrayList<Integer> resources3 = new ArrayList<Integer>();
		ArrayList<Integer> vorgaenger3 = new ArrayList<Integer>();
		vorgaenger3.add(2);

		resources3.add(0, 6);
		resources3.add(1, 4);
		resources3.add(2, 3);
		resources3.add(3, 3);
		aStapel[2] = new Job(3, vorgaenger3, 3, resources3, 1);

		// 4.Element
		ArrayList<Integer> resources4 = new ArrayList<Integer>();
		ArrayList<Integer> vorgaenger4 = new ArrayList<Integer>();
		vorgaenger4.add(1);

		resources4.add(0, 3);
		resources4.add(1, 3);
		resources4.add(2, 3);
		resources4.add(3, 3);
		aStapel[3] = new Job(4, vorgaenger4, 9, resources4, 1);

		// 5.Element
		ArrayList<Integer> resources5 = new ArrayList<Integer>();
		ArrayList<Integer> vorgaenger5 = new ArrayList<Integer>();

		vorgaenger5.add(3);
		vorgaenger5.add(4);

		resources5.add(0, 6);
		resources5.add(1, 4);
		resources5.add(2, 4);
		resources5.add(3, 4);
		aStapel[4] = new Job(5, vorgaenger5, 4, resources5, 1);

		return aStapel;
	}

	// Dummy Stapel B erstellen
	private static Job[] erstelleStapelB() {

		Job[] bStapel = new Job[5];

		// B Stapel
		// 1. Element

		ArrayList<Integer> resources1 = new ArrayList<Integer>();
		ArrayList<Integer> vorgaenger1 = new ArrayList<Integer>();
		vorgaenger1.add(1);
		resources1.add(0, 5);
		resources1.add(1, 1);
		resources1.add(2, 2);
		resources1.add(3, 3);
		bStapel[0] = new Job(6, null, 2, resources1, 2);

		// 2.Element
		ArrayList<Integer> resources2 = new ArrayList<Integer>();
		ArrayList<Integer> vorgaenger2 = new ArrayList<Integer>();
		vorgaenger2.add(6);
		resources2.add(0, 3);
		resources2.add(1, 4);
		resources2.add(2, 5);
		resources2.add(3, 1);
		bStapel[1] = new Job(7, vorgaenger2, 1, resources2, 2);

		// 3.Element
		ArrayList<Integer> resources3 = new ArrayList<Integer>();
		ArrayList<Integer> vorgaenger3 = new ArrayList<Integer>();
		vorgaenger3.add(7);

		resources3.add(0, 6);
		resources3.add(1, 4);
		resources3.add(2, 3);
		resources3.add(3, 3);
		bStapel[2] = new Job(8, vorgaenger3, 3, resources3, 2);

		// 4.Element
		ArrayList<Integer> resources4 = new ArrayList<Integer>();
		ArrayList<Integer> vorgaenger4 = new ArrayList<Integer>();
		vorgaenger4.add(6);

		resources4.add(0, 3);
		resources4.add(1, 3);
		resources4.add(2, 3);
		resources4.add(3, 3);
		bStapel[3] = new Job(9, vorgaenger4, 9, resources4, 2);

		// 5.Element
		ArrayList<Integer> resources5 = new ArrayList<Integer>();
		ArrayList<Integer> vorgaenger5 = new ArrayList<Integer>();

		vorgaenger5.add(8);
		vorgaenger5.add(9);

		resources5.add(0, 6);
		resources5.add(1, 4);
		resources5.add(2, 4);
		resources5.add(3, 4);
		bStapel[4] = new Job(10, vorgaenger5, 14, resources5, 2);

		return bStapel;
	}
	// ############################### ENDE DUMMY STAPEL
	// ###########################

}