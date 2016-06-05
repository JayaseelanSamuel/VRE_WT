package com.brownfield.vre;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;

/**
 * The Class PropertyReader.
 * 
 * @author Onkar Dhuri <onkar.dhuri@synerzip.com>
 */
public class PropertyReader {

	/** The props. */
	private static Properties props = null;

	/** The Constant propertyFileName. */
	private static final String propertyFileName = "vre.properties";

	/** The logger. */
	private static final Logger LOGGER = Logger.getLogger(PropertyReader.class.getName());

	/**
	 * Instantiates a new property reader.
	 */
	private PropertyReader() {
	}

	/**
	 * Gets the property.
	 *
	 * @param key
	 *            the key
	 * @return the property
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static String getProperty(final String key) throws IOException {
		return props.getProperty(key);
	}

	/**
	 * Load properties.
	 *
	 * @param servletContext
	 *            the servlet context
	 */
	public static void loadProperties(ServletContext servletContext) {
		try (InputStream is = servletContext.getResourceAsStream("/WEB-INF/classes/" + propertyFileName);) {
			props = new Properties();
			props.load(is);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
	}
}