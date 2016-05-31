package com.brownfield.vre.rest;

import static com.brownfield.vre.VREConstants.VRE_JNDI_NAME;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;

import com.brownfield.vre.PropertyReader;
import com.brownfield.vre.VREConstants;

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
	private static Logger LOGGER = Logger.getLogger(VREContextListener.class.getName());

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.
	 * ServletContextEvent)
	 */
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {

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
			PropertyReader.loadProperties(event.getServletContext());
			VREConstants.PHD_TEIID_URL = PropertyReader.getProperty("PHD_TEIID_URL");
			VREConstants.TEIID_USER = PropertyReader.getProperty("TEIID_USER");
			VREConstants.TEIID_PASSWORD = PropertyReader.getProperty("TEIID_PASSWORD");
			VREConstants.VRE_JNDI_NAME = PropertyReader.getProperty("VRE_JNDI_NAME");

			Context initialContext = new InitialContext();
			DataSource datasource = (DataSource) initialContext.lookup(VRE_JNDI_NAME);
			if (datasource != null) {
				Connection conn = datasource.getConnection();
				VREConstants.refreshVariables(conn);

			} else {
				LOGGER.log(Level.SEVERE, "Failed to lookup datasource.");
			}
		} catch (NamingException ex) {
			LOGGER.log(Level.SEVERE, "Cannot get connection: " + ex);
		} catch (SQLException ex) {
			LOGGER.log(Level.SEVERE, "Cannot get connection: " + ex);
		} catch (IOException ex) {
			LOGGER.log(Level.SEVERE, "Cannot get connection: " + ex);
		}

	}

}
