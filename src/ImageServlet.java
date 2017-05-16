import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class ImageServlet
 */
public class ImageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ImageServlet() {
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
		int id;
		int[] graph;
		String fileLoc = "";
		int suffix;

		if (userID.equals("id1"))
			id = 0;
		else
			id = 1;

		File imgFileOld = null;
		if ((imgFileOld = (File) getServletContext().getAttribute(
				"imageUserFile" + userID)) != null) {
			if (imgFileOld.isFile()) {
				imgFileOld.delete();
			}
		}

		ImageProcessor iproc = (ImageProcessor) getServletContext()
				.getAttribute("iproc");

		synchronized (iproc) {
			ArrayList<Integer> graphValues = m.getOptDauerList(id);
			if (graphValues != null)
				graph = new int[graphValues.size()];
			else
				graph = new int[0];

			for (int i = 0; i < graph.length; i++) {
				graph[i] = graphValues.get(i);
			}

			iproc.createDiagram(graph);
			suffix = (int) Math.round(Math.random() * Integer.MAX_VALUE);
			fileLoc = getServletContext().getRealPath("/") + "grafiken/img"
					+ userID + suffix + ".png";

			iproc.saveAsPicture(fileLoc);
		}

		File file = new File(fileLoc);
		getServletContext().setAttribute("imageUserFile" + userID, file);

		String image = "<div style='text-align:center; padding:5%;'>"
				+ "	<i>"
				+ "This statistic shows you the gradual development of your project delay."
				+ "	</i>"
				+ "	<div id='status'></div>"
				+ "</div>"
				+ "<li class='group'>Current State of Delay</li>"
				+ "<li style='text-align:center; padding-top:5%;'>"
				+ "		<img src='grafiken/img"
				+ userID
				+ suffix
				+ ".png' border='1' alt='' style='width: 95%; max-width: 430px;'></img>"
				+ "</li>";

		response.getWriter().println(image);
		response.getWriter().flush();
	}

}
