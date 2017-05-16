import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Application Lifecycle Listener implementation class
 * ProjectServletContextListener
 * 
 */
public class ProjectServletContextListener implements ServletContextListener {

	/**
	 * @see ServletContextListener#contextInitialized(ServletContextEvent)
	 */
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		ServletContext sc = arg0.getServletContext();

		String location = sc.getRealPath("/") + "projektdaten/";

		AccessMediator m = AccessMediator.getAccessMediatorObject(location);
		sc.setAttribute("mediator", m);

		ImageProcessor iproc = new ImageProcessor();
		sc.setAttribute("iproc", iproc);

		ArrayList<Integer> graphUser0 = new ArrayList<Integer>();
		sc.setAttribute("gu0", graphUser0);

		ArrayList<Integer> graphUser1 = new ArrayList<Integer>();
		sc.setAttribute("gu1", graphUser1);

		sc.setAttribute("firstCall0", true);
		sc.setAttribute("firstCall1", true);
		sc.setAttribute("actRoundTemp", AccessMediator.ANZAHL_RUNDEN);

	}

	/**
	 * @see ServletContextListener#contextDestroyed(ServletContextEvent)
	 */
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {

	}

}
