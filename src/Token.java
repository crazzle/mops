
public class Token {


	int anzahlA; //Gesamtanzahl der Jobs in Projekt A
	int anzahlB;
	int standA=0; //Anzahl der bisher eingebauten Jobs
	int standB=0;
	
	//nur für den Test
	public Token(){
		anzahlA = 1;
		anzahlB = 5;
	}
	
	public Token(Job[] _aStapel, Job[] _bStapel){
		anzahlA = _aStapel.length;
		anzahlB = _bStapel.length;
	}
	
	public int getToken(int gesResA, int gesResB){
		
		//getToken(getUsedRes(1), getUsedRes(2));
		
		
		
		if(gesResA < gesResB && standA < anzahlA){
			
			standA++;
			return AccessMediator.ID_A; //A ist dran, wenn gesDauerA < gesDauerB
		}
		if(gesResB < gesResA && standB < anzahlB){
			
			standB++;
			return AccessMediator.ID_B;
		}
		if(standA<anzahlA){
			standA++;
			return AccessMediator.ID_A;	
		}
		if(standB<anzahlB){
			standB++;
			return AccessMediator.ID_B;	
		}
		throw new IllegalArgumentException("Du Arsch hast eine falsche ID eingegeben!");
		
	}
	
}
