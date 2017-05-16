import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class NegotiationServlet
 */
public class NegotiationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public NegotiationServlet() {
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
		String userID = request.getParameter("user");
		String resp = request.getParameter("resp");
		String listString = request.getParameter("switchlist");
		String[] listAr = listString.split(",");
		int[] switchlist = new int[listAr.length];
		boolean respCheck;
		boolean finish = false;
		int actRound = m.getActRunde();
		String data;
		int id;
		if (userID.equals("id1"))
			id = 0;
		else
			id = 1;
		int duration = m.getResultDauer(id);

		for (int i = 0; i < listAr.length; i++) {
			switchlist[i] = Integer.parseInt(listAr[i].trim());
		}

		@SuppressWarnings("unchecked")
		ArrayList<Integer> durationValues = (ArrayList<Integer>) getServletContext()
				.getAttribute("gu" + id);

		if (!resp.equals("start")) {
			if (actRound < (Integer) getServletContext().getAttribute(
					"actRoundTemp"))
				;
			durationValues.add(m.getOptDauer(id));

			getServletContext().setAttribute("gu" + id, durationValues);
			if (resp.equals("no")) {
				respCheck = false;
			} else {
				respCheck = true;
			}
			m.respond(id, respCheck, switchlist);
		} else {
			if (actRound > 0
					&& (Boolean) getServletContext().getAttribute(
							"firstCall" + id)) {
				m.respond(id, false, switchlist);
				getServletContext().setAttribute("firstCall" + id, false);
			} else if (actRound <= 0) {
				finish = true;
			}
		}

		if (!finish) {
			data = setContent(m, userID, id, duration, actRound);
		} else {
			int finalDuration = duration;

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
					+ "<li class='group'>Information</li>"
					+ "<li>Project-ID: "
					+ projectID
					+ "</li>"
					+ "<li>Delay: "
					+ finalDuration
					+ "</li>"
					+ "<li>Job count: "
					+ jobNumber
					+ "</li>"
					+ "<li class='group'>Current delay</li>"
					+ "<a class='whiteButton' type='submit' onclick='getImage(\"statistic\",\""
					+ userID + "\")' href='#statistic'>Statistic</a>";

			data = "finish" + "$!" + code + "$!";
		}
		response.getWriter().println(data);
		response.getWriter().flush();
	}

	private String setContent(AccessMediator m, String userID, int id,
			int duration, int actRound) {
		int delta;
		String data;

		int optDauer = m.getOptDauer(id);
		delta = optDauer - duration;

		/*
		 * now, save duration as new oldduration for user
		 */

		String code = "<div style='text-align:center; padding:2%;'>" + "	<i>"
				+ "		<p>Hello "
				+ getServletContext().getInitParameter(userID)
				+ ", "
				+ "		<p>Here you can see the recommendation of the mediator.	"
				+ "		<br>Please choose whether you apply with the recommendation or not."
				+ "	</i>"
				+ "		<div id='status'><div id='spinner'><img src='grafiken/loading.gif' height='19px'></div></div>"
				+ "</div>"
				+ "<li class='group'>Duration</li>"
				+ "<li id='optduration'>Current Delay: "
				+ optDauer
				+ "</li>"
				+ "<li id='newduration'>New Delay: "
				+ duration
				+ "</li>"
				+ "<li class='group'>Difference</li>"
				+ "<li id='delta'>No previous periods exist.</li>"
				+ "<li id='recom'></li>"
				+ "<div id='buttonDiv' style='text-align:center; padding-top:1%; padding-left:10%; padding-right:10%;'>"
				+ "	<a class='whiteButton' type='submit' onclick='getImage(\"statistic\",\""
				+ userID
				+ "\")' href='#statistic'>Statistic</a>"
				+ "	<br>"
				+ "	<a class='grayButton' id='yesButton' type='submit' href='#n' style='color: gray;'>Yes</a>"
				+ "	<br>"
				+ "	<a class='grayButton' id='noButton' type='submit' href='#n'  style='color: gray;'>No</a>"
				+ "</div>";

		data = code + "$!" + delta + "$!" + actRound;

		return data;
	}

}
