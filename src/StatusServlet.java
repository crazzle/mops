import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class StatusServlet
 */
public class StatusServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public StatusServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		AccessMediator m = (AccessMediator) getServletContext().getAttribute(
				"mediator");
		boolean stat = m.checkStatus();
		int id = -1;
		String userID = request.getParameter("user");
		if (userID.equals("id1"))
			id = 0;
		else
			id = 1;

		boolean statIndividual = m.didIAnswer(id);
		int optd = m.getOptDauer(id);
		int newd = m.getResultDauer(id);
		int actR = m.getActRunde();
		String data = "";

		if (actR <= 0) {

			int finalDuration = optd;

			Project userProject = m.getProjectOverview(id);
			int projectID = userProject.getId();
			int jobNumber = userProject.getJobs().size();

			String code = "<div style='text-align:center; padding:2%;'>"
					+ "<i>"
					+ "		<p>"
					+ getServletContext().getInitParameter(userID)
					+ "'s Projectplan "
					+ "	</i>"
					+ "		<div id='result'></p></div>"
					+ "</div>"
					+ "<li class='group'>Project data</li>"
					+ "<li>Project-ID: "
					+ projectID
					+ "</li>"
					+ "<li>Delay: "
					+ finalDuration
					+ "</li>"
					+ "<li>Job count: "
					+ jobNumber
					+ "</li>"
					+ "<li class='group'>Period progression</li>"
					+ "<li id='periods'><a class='whiteButton' type='submit' onclick='getImage(\"statistic\",\""
					+ userID + "\")' href='#statistic'>Statistic</a></li>";
			data = code;
		}

		response.getWriter().println(
				stat + "$!" + statIndividual + "$!" + optd + "$!" + newd + "$!"
						+ actR + "$!" + data);
		response.getWriter().flush();
	}

}
