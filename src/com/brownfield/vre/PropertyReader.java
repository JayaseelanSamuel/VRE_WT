package com.brownfield.vre;

import java.io.File;
import java.io.FileInputStream;
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

	/** The Constant CONFIG_DIR. */
	private static final String CONFIG_DIR = "jboss.server.config.dir";

	/** The Constant propertyFileName. */
	private static final String PROPERTY_FILE_NAME = "vre.properties";

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
		try (InputStream is = servletContext.getResourceAsStream("/WEB-INF/classes/" + PROPERTY_FILE_NAME);) {
			props = new Properties();
			props.load(is);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
	}

	/**
	 * Load properties from outside of war.
	 */
	public static void loadProperties() {
		String configDir = System.getProperty(CONFIG_DIR);

		try (InputStream is = new FileInputStream(configDir + File.separator + PROPERTY_FILE_NAME);) {
			props = new Properties();
			props.load(is);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
	}
}