
import java.io.BufferedReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class Parser {
	
	// Parse multi project data
	public static ArrayList<Project> parseProjectData(String xmlFilePath, String jobFilePath) throws ParserConfigurationException, SAXException, IOException {
		// Read multi project xml
		File multiProjectData = new File(xmlFilePath);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(multiProjectData);
		doc.getDocumentElement().normalize();
		
		// Get resource list
		ArrayList<Integer> globalResources = new ArrayList<Integer>();	// Array for global resources
		NodeList resourceList = doc.getElementsByTagName("resource");	// Extract resource nodes from XML file
		for(int i = 0; i < resourceList.getLength(); i++) {	// Iterate over NodeList
			Node resource = resourceList.item(i);
			Element resourceElement = (Element) resource;
			NodeList textResourceList = resourceElement.getChildNodes();
			String globalResource = textResourceList.item(0).getNodeValue().trim();
			globalResources.add(i, Integer.parseInt(globalResource));	// Put the value for the global resource into the specified array
		}
		
		// Read project jobs
		File jobData = new File(jobFilePath);
		FileReader fr = new FileReader(jobData);
		BufferedReader br = new BufferedReader(fr);
		
		int counter = 0;	// Counter for detecting the star rows
		ArrayList<ArrayList<Integer>> relations = new ArrayList<ArrayList<Integer>>();	// Stores the text lines containing the job relations
		ArrayList<ArrayList<Integer>> durations = new ArrayList<ArrayList<Integer>>();	// Stores the text lines containing the job duration and resource demand
		ArrayList<Integer> localResources = new ArrayList<Integer>();	// Stores the text lines containing the local project resources
		String input = new String();	// Placeholder for read lines from buffered reader
		
		while((input = br.readLine()) != null) {
			if(input.startsWith("*") == true) {
				counter++;	// Increment counter if the line starts with *, in order to split the file into the different parts
				if(counter == 4) {	// Indicates the start of the part containing the job relations
					br.readLine();	// Skip two lines
					br.readLine();
					input = br.readLine();
				}
				if(counter == 5) {	// Indicates the start of the part that contains the job durations and resource need
					br.readLine();	// Skip three lines
					br.readLine();
					br.readLine();
					input = br.readLine();
				}
				if(counter == 6) {	// Indicates the start of the part that contains the resource definition
					br.readLine();	// Skip two lines
					br.readLine();
					input = br.readLine();
				}
				if(counter == 7) {	// Indicates the end of the file
					br.close();
					break;
				}
			}
			if(counter == 4) {	// Process the part that contains the job relations
				relations = splitBufferedString(input, relations);
			}
			if(counter == 5) {	// Process the part that contains the job durations and resources need
				durations = splitBufferedString(input, durations);
			}
			if(counter == 6) {	// Process the part that contains the resource definition
				String spacesReplaced = input.replaceAll(" +", " ");	// Replace several spaces by only one space
				String trimmed = spacesReplaced.trim();	// Remove leading and trailing spaces
				String[] splitInput = trimmed.split(" ");	// Split string
				
				// Parse string array to integer ArrayList
				for(int i = 0; i < splitInput.length; i++) {
					localResources.add(i, Integer.parseInt(splitInput[i]));
				}
			}
		}
		
		// Create projects out of parsed data
		ArrayList<Project> projects = createProjects(globalResources, relations, durations, localResources);
		for(int k = 0; k < projects.size(); k++){
			projects.get(k).finallySetUpProject();
		}
		return projects;
	}
	
	
	// Split buffered string
	private static ArrayList<ArrayList<Integer>> splitBufferedString(String bufferedString, ArrayList<ArrayList<Integer>> storage) {
		String spacesReplaced = bufferedString.replaceAll(" +", " ");	// Replace several spaces by only one space
		String trimmed = spacesReplaced.trim();	// Remove leading and trailing spaces
		String[] splitInput = trimmed.split(" ");	// Split string
		
		// Parse string array to integer ArrayList
		ArrayList<Integer> dataRow = new ArrayList<Integer>();
		for(int i = 0; i < splitInput.length; i++) {
			dataRow.add(i, Integer.parseInt(splitInput[i]));
		}
		storage.add(dataRow);	// Add split string to array
		return storage;	// Return array
	}
	
	// Create project and job objects
	private static ArrayList<Project> createProjects(ArrayList<Integer> globalResources, ArrayList<ArrayList<Integer>> relations, ArrayList<ArrayList<Integer>> durations, ArrayList<Integer> localResources) {
		// Create projects
		Project project1;
		Project project2;
		int projectId1 = AccessMediator.ID_A;
		int projectId2 = AccessMediator.ID_B;
		
		// Examine global resources and compare them with the local resources
		// If a global resource is 0, use the defined local resource for the project
		ArrayList<Integer> projectResources = new ArrayList<Integer>();
		for(int i = 0; i < globalResources.size(); i++) {
			if(globalResources.get(i) != 0) {
				projectResources.add(i, globalResources.get(i));
			} else {
				projectResources.add(i, localResources.get(i));
			}
		}
		
		// Initialize projects
		project1 = new Project(projectId1, projectResources);
		project2 = new Project(projectId2, projectResources);
		
		// Create project jobs
		for(int j = 0; j < relations.size(); j++) {
			int id = relations.get(j).get(0);	// Job id
			
			ArrayList<Integer> successors = new ArrayList<Integer>();	// Job successors
			for(int k = 3; k < relations.get(j).size(); k++) {
				successors.add(relations.get(j).get(k));
			}
			
			int duration = durations.get(j).get(2);	// Job duration
			
			ArrayList<Integer> jobResources = new ArrayList<Integer>();	// Job resources
			for(int l = 3; l < durations.get(j).size(); l++) {
				jobResources.add(durations.get(j).get(l));
			}
			
			// Create new job for project 1 and 2
			Job newJobProject1 = new Job(id, successors, duration, jobResources, projectId1);
			Job newJobProject2 = new Job(id, successors, duration, jobResources, projectId2);
			
			// Add jobs to the job lists of the projects
			project1.addJob(newJobProject1);
			project2.addJob(newJobProject2);
		}
		
		// Put projects into an array to return them to the caller
		ArrayList<Project> projects = new ArrayList<Project>();
		projects.add(0, project1);
		projects.add(1, project2);
		return projects;
	}
}
