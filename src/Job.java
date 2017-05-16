
import java.util.ArrayList;

public class Job {
	// Attributes
	private int id;
	private ArrayList<Integer> successors;
	private int duration;
	private ArrayList<Integer> resources;
	private int projectReference;
	private int next = 0;
	private int startPeriod;
	private int[] res;
	private ArrayList<Integer> predecessors;
	private int[] predecessorIntArray;
	
	// Constructor
	public Job(int _id, ArrayList<Integer> _successsors, int _duration, ArrayList<Integer> _resources, int _projectReference){
		this.id = _id;
		this.successors = _successsors;
		this.duration = _duration;
		arrayListToArray(_resources);
		this.resources = _resources;
		this.projectReference = _projectReference;
		this.predecessors = new ArrayList<Integer>();
	}
	
	private void arrayListToArray(ArrayList<Integer> res){
		this.res = new int[res.size()];
		for(int i = 0; i < res.size(); i++){
			this.res[i] = res.get(i);
		}
	}
	
	public void convertArrayListToIntArray(){

		this.predecessorIntArray = new int[this.predecessors.size()];
		for(int i = 0; i < this.predecessors.size(); i++){
			this.predecessorIntArray[i] = this.predecessors.get(i);
		}
	}
	
	// Getter/Setter
	public int getId(){
		return id;
	}
	
	public ArrayList<Integer> getSuccessors(){
		return successors;
	}
	
	public void addPredecessor(int x){
		this.predecessors.add((Integer)x);
	}
	
	public ArrayList<Integer> getPredecessors(){
		return this.predecessors;
	}
	
	public int[] getPredecessorIntArray(){
		return this.predecessorIntArray;
	}
	
	public int getNrPredecessors(){
		if(this.predecessors == null){
			return 0;
		}
		else{
			return this.predecessors.size();
		}
	}
	
	public int getNrSuccessors(){
		if(successors == null){
			return 0;
		} else {
			return successors.size();	
		}
	}
	
	public int getDuration(){
		return duration;
	}
	
	public int getNext(){
		next++;
		return next-1;
	}
	
	public void setStartperiode(int period){
		startPeriod = period;
	}
	
	public int getStartPeriod(){
		return startPeriod;
	}
	
	public ArrayList<Integer> getResources(){
		return resources;
	}
	
	public int getProjectReference(){
		return projectReference;
	}
	
	public int[] getResourcesArray(){
		return this.res;
	}
	
	public void setResourcesArray(int[] array){
		this.res = array;
	}
	
}
