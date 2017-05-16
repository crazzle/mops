

import java.util.ArrayList;


public class Project {
	// Attributes
	private int id;
	private ArrayList<Integer> resources;
	private int[] resourcesIntArray;
	private ArrayList<Job> jobs;
	private Job[] jobArray;
	
	// Constructor
	public Project(int _id, ArrayList<Integer> _resources) {
		this.id = _id;
		this.resources = _resources;
		this.jobs = new ArrayList<Job>();
		
	}
	public void finallySetUpProject(){
		jobArray = fillJobArray(this);
		jobArray = repairProject(jobArray);
		convertResourcesArrayToIntArray();
	}
	
	
	/**
	 * converts a specific ArrayList<Job> to a Job array
	 * @param p
	 * @return Job[] 
	 */
	private Job[] fillJobArray(Project p){
		Job[] jarray = new Job[p.getJobs().size()];
		for(int i = 0; i < p.getJobs().size(); i++){
			jarray[i] = p.getJobs().get(i);
		}
		return jarray;
	}
	/**
	 * calculates and saves the PREDECESSORS of jobs. 
	 * @param jobs
	 * @return
	 */
	private Job[] repairProject(Job[] jobs){

		int length = jobs.length;
		for (int i = 0; i < length; i++){
			for(int j = 0; j < jobs[i].getNrSuccessors(); j++){
				int a = jobs[i].getSuccessors().get(j);
				jobs[a-1].addPredecessor(i+1);
			}
			jobs[i].convertArrayListToIntArray();
		}
		
		
		return jobs;
	}

	private void convertResourcesArrayToIntArray(){
		this.resourcesIntArray = new int[this.resources.size()];
		for(int i = 0; i < this.resourcesIntArray.length; i++){
			this.resourcesIntArray[i] = this.resources.get(i);
		}
	}
	
	// Getter/Setter
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ArrayList<Integer> getResources() {
		return resources;
	}

	public void setResources(ArrayList<Integer> resources) {
		this.resources = resources;
	}

	public ArrayList<Job> getJobs() {
		return jobs;
	}
	
	// Add jobs
	public void addJobs(ArrayList<Job> _jobs) {
		this.jobs = _jobs;
	}
	
	// Add job
	public void addJob(Job newJob) {
		this.jobs.add(newJob);
	}
	
	public int[] getResourcesIntArray(){
		try{
			return this.resourcesIntArray;
		}
		catch (NullPointerException e){
			System.out.println("ResourcesIntArray is NULL at this time. Aborting...");
			System.exit(0);
			return null; // should not be necessary...
		}
	}
	
}
