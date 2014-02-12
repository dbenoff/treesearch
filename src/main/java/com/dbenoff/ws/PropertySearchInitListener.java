package com.dbenoff.ws;

import org.apache.log4j.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;

/**
 * @author dbenoff
 * Startup listener to instantiate and configure the propertySearchWrapper
 */
public class PropertySearchInitListener implements ServletContextListener {

	Logger log = Logger.getLogger(this.getClass());	
	private static String property_data = "property_data";
	private static String page_size = "page_size";
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		//noop
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		try {
			String propertyPath = sce.getServletContext().getInitParameter(property_data);
			int pageSize = Integer.parseInt(sce.getServletContext().getInitParameter(page_size));
			String realPath = sce.getServletContext().getRealPath(propertyPath);
			PropertySearchFactory.setFile(new File(realPath));
			PropertySearchFactory.setPageSize(pageSize);
			PropertySearchFactory.init();
		} catch (Exception e) {
			log.warn(e.getMessage());
		}
	}

}
