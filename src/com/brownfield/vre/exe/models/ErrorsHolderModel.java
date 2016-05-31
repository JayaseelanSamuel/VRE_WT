package com.brownfield.vre.exe.models;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class ErrorsHolderModel.
 * 
 * @author Onkar Dhuri <onkar.dhuri@synerzip.com>
 */
@XmlRootElement(name = "errors")
public class ErrorsHolderModel {

	/** The errors. */
	private List<String> errors = new ArrayList<String>();

	/**
	 * Gets the errors.
	 *
	 * @return the errors
	 */
	@XmlElement(name = "error")
	public List<String> getErrors() {
		return errors;
	}

	/**
	 * Sets the errors.
	 *
	 * @param list
	 *            the new errors
	 */
	public void setErrors(List<String> list) {
		this.errors = list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (this.errors != null && this.errors.size() > 0) {
			for (String error : errors) {
				sb.append(error).append(",");
			}
		}
		if (sb.length() > 0) {
			sb.replace(sb.length() - 1, sb.length(), "");
		}
		return sb.toString();
	}

}
