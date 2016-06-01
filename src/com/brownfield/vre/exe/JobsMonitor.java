package com.brownfield.vre.exe;

import static com.brownfield.vre.VREConstants.*;
import static com.brownfield.vre.VREConstants.DSRTA_STATUS_ID;
import static com.brownfield.vre.VREConstants.FILE_MONITORING_SERVICE;
import static com.brownfield.vre.VREConstants.JOBS_REMARK;
import static com.brownfield.vre.VREConstants.REMARK;
import static com.brownfield.vre.VREConstants.ROW_CHANGED_BY;
import static com.brownfield.vre.VREConstants.ROW_CHANGED_DATE;
import static com.brownfield.vre.VREConstants.SQL_DRIVER_NAME;
import static com.brownfield.vre.VREConstants.STRING_NAME;
import static com.brownfield.vre.VREConstants.VRE6_EXE_OUTPUT;
import static com.brownfield.vre.VREConstants.VRE6_OUTPUT_FOLDER;
import static com.brownfield.vre.VREConstants.VRE_DB_URL;
import static com.brownfield.vre.VREConstants.VRE_JOBS_IN_PROGRESS_QUERY;
import static com.brownfield.vre.VREConstants.VRE_PASSWORD;
import static com.brownfield.vre.VREConstants.VRE_USER;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.brownfield.vre.VREConstants.DSIS_JOB_TYPE;
import com.brownfield.vre.VREConstants.DSRTA_JOB_TYPE;

/**
 * The Class JobsMonitor.
 * 
 * @author Onkar Dhuri <onkar.dhuri@synerzip.com>
 */
public class JobsMonitor {

	/** The logger. */
	private static Logger LOGGER = Logger.getLogger(JobsMonitor.class.getName());

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {

		try {
			Class.forName(SQL_DRIVER_NAME);
			try (Connection vreConn = DriverManager.getConnection(VRE_DB_URL, VRE_USER, VRE_PASSWORD)) {

				JobsMonitor jm = new JobsMonitor();
				jm.updateVRE6Output(vreConn);
			} catch (SQLException e) {
				LOGGER.log(Level.SEVERE, e.getMessage());
			}

		} catch (ClassNotFoundException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}

	}

	/**
	 * Update vre6 output.
	 *
	 * @param vreConn
	 *            the vre conn
	 */
	public void updateVRE6Output(Connection vreConn) {
		try (PreparedStatement statement = vreConn.prepareStatement(VRE_JOBS_IN_PROGRESS_QUERY,
				ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
				ResultSet rset = statement.executeQuery()) {
			if (rset != null) {
				while (rset.next()) {
					String outputFile = VRE6_OUTPUT_FOLDER + rset.getString(STRING_NAME) + CSV_EXTENSION;
					File f = new File(outputFile);
					if (f.exists() && !f.isDirectory()) {
						LOGGER.log(Level.INFO, " File available - " + outputFile);
						String content = this.getFileContent(f);
						if (content != null) {
							String remark = String.format(JOBS_REMARK, DSIS_JOB_TYPE.FINISHED, new Date());
							// file read successfully
							//rset.updateInt(DSIS_STATUS_ID, DSIS_JOB_TYPE.FINISHED.getNumVal());
							//rset.updateInt(DSRTA_STATUS_ID, DSRTA_JOB_TYPE.READY.getNumVal());
							rset.updateString(VRE6_EXE_OUTPUT, content);
							rset.updateString(REMARK, remark);
							rset.updateString(ROW_CHANGED_BY, FILE_MONITORING_SERVICE);
							rset.updateTimestamp(ROW_CHANGED_DATE, new Timestamp(new Date().getTime()));
							rset.updateRow();
							// Delete the output file
							FileUtils.deleteQuietly(f);
							LOGGER.log(Level.INFO, " Updated job entry for - " + rset.getString(STRING_NAME));
						}
					}else{
						LOGGER.log(Level.INFO, " File not yet available - " + outputFile);
					}
				}
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
	}

	/**
	 * Gets the file content.
	 *
	 * @param file
	 *            the file
	 * @return the file content
	 */
	private String getFileContent(File file) {
		String content = null;
		try {
			// see if file is being written or not
			FileUtils.touch(file);
			// this will be called only when file is not locked by another
			// process (in this case VRE6 exe)
			content = IOUtils.toString(new FileInputStream(file));
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, file.getName()
					+ " : Can't read the file; file is already opened by another process.\n" + e.getMessage());
		}
		return content;
	}

}
