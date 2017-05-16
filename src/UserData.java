

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class UserData
 */
public class UserData extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserData() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		AccessMediator m = (AccessMediator) getServletContext().getAttribute("mediator");
		String userID = request.getParameter("user");
		
		int jobNumber = 0;
		int id = 0;
		int projectID;
		if(userID.equals("id1"))
			id = 0;
		else
			id = 1;
		
		Project userProject = m.getProjectOverview(id);
		
		projectID = userProject.getId();
		jobNumber = userProject.getJobs().size();
		
		JobInitiator jobIni = new JobInitiator(userProject);
		ArrayList<ArrayList<Job>> switchList = jobIni.createSwitchList();
		
		String name = getServletContext().getInitParameter(userID);
		
		String code =	"	<div style='text-align:center; padding:5%;'>" +
						"		<i> Welcome " + name + "!" +
						"			<p>This area gives you information about the project.</p>" +
						"		</i>" +
						"	</div>" +
						"	<li class='group'>Project information</li>" +
						"	<li id='projectID'>Project-ID: " + projectID + "</li>" +
						"   <li id='jobNumber'>Job count: " + jobNumber + "</li>" +
						"	<div style='text-align:center; padding-top:5%; padding-left:10%; padding-right:10%;'>" +
						"		<a class='whiteButton' type='submit' href='#user-1' onClick='negotiateProject(\"" + request.getParameter("user") + "\", \"user-1\", \"start\")'>Plan Project</a>" +
						"	</div>";
		
		String list = "";
		for(int i = 0; i < switchList.size(); i++){
			for(int j = 0; j < switchList.get(i).size(); j++){
				if(j < switchList.get(i).size()-1)
					list+=switchList.get(i).get(j).getId()+",";
				else
					list+=switchList.get(i).get(j).getId();
			}
			if(i < switchList.size()-1)
				list+="p.";
		}
		
		String data = name + "$!" + code + "$!" + list;
		
		
		response.getWriter().println(data);
		response.getWriter().flush();
	}

}
