import java.util.ArrayList;

public class JobInitiator {

	private final Project userProject;
	private final ArrayList<Job> allJobs;
	private final ArrayList<ArrayList<Job>> switchList = new ArrayList<ArrayList<Job>>();

	public JobInitiator(Project userProj) {
		this.userProject = userProj;
		this.allJobs = userProject.getJobs();
	}

	public ArrayList<ArrayList<Job>> createSwitchList() {
		if (allJobs.get(0).getId() == 1) {
			ArrayList<Job> firstList = new ArrayList<Job>();
			firstList.add(allJobs.get(0));
			switchList.add(firstList);
		}
		ArrayList<Job> firstSuccessors = new ArrayList<Job>();
		for (int i = 0; i < allJobs.get(0).getSuccessors().size(); i++) {
			for (int j = 0; j < allJobs.size(); j++) {
				if (allJobs.get(j).getId() == allJobs.get(0).getSuccessors()
						.get(i)) {
					firstSuccessors.add(allJobs.get(j));
				}
			}
		}

		createSwitchList(firstSuccessors);
		return switchList;
	}

	private boolean containsPredecessors(Job job) {
		int PREDCOUNT = job.getPredecessorIntArray().length;
		int existing_preds = 0;
		for (int i = 0; i < switchList.size(); i++) {
			for (int j = 0; j < switchList.get(i).size(); j++) {
				for (int k = 0; k < job.getPredecessors().size(); k++) {
					if (switchList.get(i).get(j).getId() == job
							.getPredecessors().get(k)) {
						existing_preds++;
					}
				}
			}
		}
		return PREDCOUNT == existing_preds;
	}

	/*
	 * How to create the switch list: Turn big list in sub lists where
	 * exchanging every element within these sublists is possible. Look for
	 * level, which stands for next sub list build on successors. if a successor
	 * needs also another predecessor that is not yet in list, do nothing with
	 * it, put it in after putting in its pred job, as another successor
	 */

	private void createSwitchList(ArrayList<Job> allSuccessors) {

		// List for the Successors of the Job
		ArrayList<Job> validSuccessors = new ArrayList<Job>();
		ArrayList<Job> nexts = new ArrayList<Job>();
		nexts = getNextSuccessors(allSuccessors);

		// Just to put successors as Job Elements in a List
		for (int i = 0; i < allSuccessors.size(); i++) {
			for (int j = 0; j < allJobs.size(); j++) {
				if (allJobs.get(j).getId() == allSuccessors.get(i).getId()) {
					if (containsPredecessors(allJobs.get(j))) {
						if (!validSuccessors.contains(allJobs.get(j)))
							validSuccessors.add(allJobs.get(j));
						break;
					}
				}
			}
		}
		if (validSuccessors.size() != 0) {
			switchList.add(validSuccessors);
		}
		if (nexts.size() != 0) {
			createSwitchList(nexts);
		}
	}

	private ArrayList<Job> getNextSuccessors(ArrayList<Job> allSuccessors) {
		ArrayList<Job> nexts = new ArrayList<Job>();
		for (int i = 0; i < allSuccessors.size(); i++) {
			for (int j = 0; j < allSuccessors.get(i).getSuccessors().size(); j++) {
				for (int k = 0; k < allJobs.size(); k++) {
					if (allJobs.get(k).getId() == allSuccessors.get(i)
							.getSuccessors().get(j)) {
						nexts.add(allJobs.get(k));
					}
				}

			}
		}
		return nexts;
	}

}
