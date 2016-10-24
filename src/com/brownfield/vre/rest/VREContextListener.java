package com.brownfield.vre.rest;

import static com.brownfield.vre.VREConstants.VRE_JNDI_NAME;
import java.util.concurrent.ExecutorService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;

import com.brownfield.vre.Utils;

/**
 * The listener interface for receiving VREContext events. The class that is
 * interested in processing a VREContext event implements this interface, and
 * the object created with that class is registered with a component using the
 * component's <code>addVREContextListener<code> method. When the VREContext
 * event occurs, that object's appropriate method is invoked.
 *
 * @see VREContextEvent
 */
public class VREContextListener implements ServletContextListener {

	/** The logger. */
	private static final Logger LOGGER = Logger.getLogger(VREContextListener.class.getName());
	
	public static final List<ExecutorService> executorList = new ArrayList<>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.
	 * ServletContextEvent)
	 */
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		LOGGER.log(Level.INFO, "Destroying active execution queues");
		for(ExecutorService executor : executorList){ 
			executor.shutdownNow();
			while (!executor.isTerminated()) {
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.ServletContextListener#contextInitialized(javax.servlet.
	 * ServletContextEvent)
	 */
	@Override
	public void contextInitialized(ServletContextEvent event) {

		try {
			Utils.refreshProperties();
			// update VRE variables from database by JNDI lookup
			Context initialContext = new InitialContext();
			DataSource datasource = (DataSource) initialContext.lookup(VRE_JNDI_NAME);
			if (datasource != null) {
				Connection conn = datasource.getConnection();
				Utils.refreshVariables(conn);

			} else {
				LOGGER.log(Level.SEVERE, "Failed to lookup datasource.");
			}
		} catch (NamingException ex) {
			LOGGER.log(Level.SEVERE, "Cannot get connection: " + ex);
			ex.printStackTrace();
		} catch (SQLException ex) {
			LOGGER.log(Level.SEVERE, "Cannot get connection: " + ex);
			ex.printStackTrace();
		}

	}

}
