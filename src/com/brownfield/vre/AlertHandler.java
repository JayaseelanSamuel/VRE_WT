package com.brownfield.vre;

import static com.brownfield.vre.VREConstants.APP_BASE_URL;
import static com.brownfield.vre.VREConstants.DSBPM_BASE_URL;
import static com.brownfield.vre.VREConstants.DSBPM_EMAIL_TEMPLATE;
import static com.brownfield.vre.VREConstants.DSBPM_PROCESS_NAME;
import static com.brownfield.vre.VREConstants.EMAIL_GROUP;
import static com.brownfield.vre.VREConstants.TEIID_PASSWORD;
import static com.brownfield.vre.VREConstants.TEIID_USER;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.BasicAuthentication;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

/**
 * The Class AlertHandler.
 * 
 * @author Onkar Dhuri <onkar.dhuri@synerzip.com>
 */
public class AlertHandler {

	/** The logger. */
	private static final Logger LOGGER = Logger.getLogger(AlertHandler.class.getName());

	/** The Constant DSBPM_REST_URL. */
	private static final String DSBPM_REST_URL = DSBPM_BASE_URL + "/process/instance?processDefinitionId="
			+ DSBPM_PROCESS_NAME;

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {

		// "com.zadco.vre.ReminderEmail.ReminderEmailProcess";
		Response clientResponse = null;
		try {
			clientResponse = AlertHandler.notifyByEmail(EMAIL_GROUP, DSBPM_EMAIL_TEMPLATE, APP_BASE_URL,
					"This is awesome !!!", "It Works !!!");
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
		}
		LOGGER.info("The status is " + clientResponse.getStatus());
	}

	/**
	 * Creates the process.
	 *
	 * @param emailIDs
	 *            the email i ds
	 * @param template
	 *            the email template
	 * @param appURL
	 *            the app url
	 * @param body
	 *            the email text
	 * @param subject
	 *            the email subject
	 * @return the response
	 * @throws Exception
	 *             the exception
	 */
	public static Response notifyByEmail(String emailIDs, String template, String appURL, String body, String subject)
			throws Exception {
		ResteasyClient client = new ResteasyClientBuilder().build();
		ResteasyWebTarget target = client.target(DSBPM_REST_URL);
		LOGGER.info("Triggerring BPM workflow for notification emails..." + DSBPM_REST_URL);
		target.register(new BasicAuthentication(TEIID_USER, TEIID_PASSWORD));
		// String data =
		// "{\"p_templateName\":\"notification.ftl\",\"p_fromEmailAddress\":\"noreply@lgc.com\",\"p_link\":\"portallinkforwelltest\",\"p_ToEmailAddress\":\"Jayaseelan.Samuel@halliburton.com\"}";
		String data = "{\"p_toEmailAddress\":\"" + emailIDs + "\",\"p_templateName\":\"" + template + "\","
				+ "\"p_portalLink\":\"" + appURL + "\",\"p_mailText\":\"" + body + "\"," + "\"p_subject\":\"" + subject
				+ "\"}";
		Response response = target.request(MediaType.APPLICATION_JSON_TYPE)
				.put(Entity.entity(data, MediaType.APPLICATION_JSON_TYPE));
		int status = response.getStatus();
		LOGGER.info("Triggerred BPM workflow for notification emails. Status code : " + status);
		return response;
	}

}
