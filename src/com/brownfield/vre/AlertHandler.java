package com.brownfield.vre;

import static com.brownfield.vre.VREConstants.APP_BASE_URL;
import static com.brownfield.vre.VREConstants.DSBPM_BASE_URL;
import static com.brownfield.vre.VREConstants.DSBPM_EMAIL_TEMPLATE;
import static com.brownfield.vre.VREConstants.DSBPM_PROCESS_NAME;
import static com.brownfield.vre.VREConstants.EMAIL_GROUP;
import static com.brownfield.vre.VREConstants.TEIID_PASSWORD;
import static com.brownfield.vre.VREConstants.TEIID_USER;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
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
		// Response clientResponse = null;
		try {
			/*
			 * clientResponse = AlertHandler.notifyByEmail(EMAIL_GROUP,
			 * DSBPM_EMAIL_TEMPLATE, APP_BASE_URL, "This is awesome !!!",
			 * "It Works !!!");
			 */
			String mailBody = "<table border=\\\\\\\"1\\\\\\\" width=\\\\\\\"100%\\\\\\\">"
					+ "<tr><th>Test Date</th><th>WellName</th><th>Validated</th></tr><tr><td>2016-10-15</td><td>UZ270X</td><td>Valid</td>"
					+ "</tr></table>";
			System.out.println(mailBody);
			AlertHandler.notifyByEmail(EMAIL_GROUP, DSBPM_EMAIL_TEMPLATE, APP_BASE_URL, "TEST2", mailBody);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
		}
		// LOGGER.info("The status is " + clientResponse.getStatus());
	}

	/**
	 * Obsolete.
	 *
	 * @param emailIDs
	 *            the email i ds
	 * @param template
	 *            the template
	 * @param appURL
	 *            the app url
	 * @param body
	 *            the body
	 * @param subject
	 *            the subject
	 * @return the response
	 */
	@Deprecated
	public static Response obsolete(String emailIDs, String template, String appURL, String body, String subject) {
		Response response = null;
		try {
			ResteasyClient client = new ResteasyClientBuilder().build();
			ResteasyWebTarget target = client.target(DSBPM_REST_URL);
			LOGGER.info("Triggerring BPM workflow for notification emails..." + DSBPM_REST_URL);
			target.register(new BasicAuthentication(TEIID_USER, TEIID_PASSWORD));
			// String data =
			// "{\"p_templateName\":\"notification.ftl\",\"p_fromEmailAddress\":\"noreply@lgc.com\",\"p_link\":\"portallinkforwelltest\",\"p_ToEmailAddress\":\"Jayaseelan.Samuel@halliburton.com\"}";
			String data = "{\"p_toEmailAddress\":\"" + emailIDs + "\",\"p_templateName\":\"" + template + "\","
					+ "\"p_portalLink\":\"" + appURL + "\",\"p_mailText\":\"" + body + "\"," + "\"p_subject\":\""
					+ subject + "\"}";
			response = target.request(MediaType.APPLICATION_JSON_TYPE)
					.put(Entity.entity(data, MediaType.APPLICATION_JSON_TYPE));
			int status = response.getStatus();
			LOGGER.info("Triggerred BPM workflow for notification emails. Status code : " + status);
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
			e.printStackTrace();
		}
		return response;
	}

	/**
	 * Notify by email.
	 *
	 * @param emailIDs the email i ds
	 * @param template the template
	 * @param appURL the app url
	 * @param subject the subject
	 * @param body the body
	 */
	public static void notifyByEmail(String emailIDs, String template, String appURL, String subject, String body) {
		try {

			URL url = new URL(DSBPM_REST_URL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("PUT");
			conn.setRequestProperty("Content-Type", "application/json");

			Authenticator.setDefault(new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(TEIID_USER, TEIID_PASSWORD.toCharArray());
				}
			});

			String data = "{\"p_toEmailAddress\":\"" + emailIDs + "\",\"p_templateName\":\"" + template + "\","
					+ "\"p_portalLink\":\"" + appURL + "\"," + "\"p_subject\":\"" + subject + "\",\"p_mailText\":\""
					+ body + "\"}";

			LOGGER.info("Triggerring BPM workflow for notification emails..." + DSBPM_REST_URL);

			LOGGER.info("\n\n" + data + "\n\n");

			OutputStream os = conn.getOutputStream();
			os.write(data.getBytes());
			os.flush();

			LOGGER.info("Triggerred BPM workflow for notification emails. Status code : " + conn.getResponseCode());

			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				LOGGER.severe("Failed : HTTP error code : " + conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output;
			while ((output = br.readLine()) != null) {
				LOGGER.fine(output);
			}

			conn.disconnect();

		} catch (MalformedURLException e) {
			LOGGER.severe(e.getMessage());
			e.printStackTrace();

		} catch (IOException e) {
			LOGGER.severe(e.getMessage());
			e.printStackTrace();

		}
	}

}
