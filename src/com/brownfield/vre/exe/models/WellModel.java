package com.brownfield.vre.exe.models;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class WellModel.
 * 
 * @author Onkar Dhuri <onkar.dhuri@synerzip.com>
 */
@XmlRootElement(name = "well")
public class WellModel {

	/** The model name. */
	private String modelName;

	/** The model location. */
	private String modelLocation;

	/** The vre1. */
	private VREModel vre1;

	/** The vre2. */
	private VREModel vre2;

	/** The vre3. */
	private VREModel vre3;

	/** The vre4. */
	private VREModel vre4;

	/** The vre5. */
	private VREModel vre5;

	/** The recal. */
	private RecalModel recal;

	/** The properties. */
	private PropertiesModel properties;

	/** The errors. */
	private ErrorsHolderModel errors;

	/**
	 * Gets the model name.
	 *
	 * @return the model name
	 */
	@XmlElement(name = "model")
	public String getModelName() {
		return modelName;
	}

	/**
	 * Sets the model name.
	 *
	 * @param modelName
	 *            the new model name
	 */
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	/**
	 * Gets the model location.
	 *
	 * @return the model location
	 */
	public String getModelLocation() {
		return modelLocation;
	}

	/**
	 * Sets the model location.
	 *
	 * @param modelLocation
	 *            the new model location
	 */
	public void setModelLocation(String modelLocation) {
		this.modelLocation = modelLocation;
	}

	/**
	 * Gets the vre1.
	 *
	 * @return the vre1
	 */
	@XmlElement(name = "vre1", type = VREModel.class)
	public VREModel getVre1() {
		return vre1;
	}

	/**
	 * Sets the vre1.
	 *
	 * @param vre1
	 *            the new vre1
	 */
	public void setVre1(VREModel vre1) {
		this.vre1 = vre1;
	}

	/**
	 * Gets the vre2.
	 *
	 * @return the vre2
	 */
	@XmlElement(name = "vre2", type = VREModel.class)
	public VREModel getVre2() {
		return vre2;
	}

	/**
	 * Sets the vre2.
	 *
	 * @param vre2
	 *            the new vre2
	 */
	public void setVre2(VREModel vre2) {
		this.vre2 = vre2;
	}

	/**
	 * Gets the vre3.
	 *
	 * @return the vre3
	 */
	@XmlElement(name = "vre3", type = VREModel.class)
	public VREModel getVre3() {
		return vre3;
	}

	/**
	 * Sets the vre3.
	 *
	 * @param vre3
	 *            the new vre3
	 */
	public void setVre3(VREModel vre3) {
		this.vre3 = vre3;
	}

	/**
	 * Gets the vre4.
	 *
	 * @return the vre4
	 */
	@XmlElement(name = "vre4", type = VREModel.class)
	public VREModel getVre4() {
		return vre4;
	}

	/**
	 * Sets the vre4.
	 *
	 * @param vre4
	 *            the new vre4
	 */
	public void setVre4(VREModel vre4) {
		this.vre4 = vre4;
	}

	/**
	 * Gets the vre5.
	 *
	 * @return the vre5
	 */
	@XmlElement(name = "vre5", type = VREModel.class)
	public VREModel getVre5() {
		return vre5;
	}

	/**
	 * Sets the vre5.
	 *
	 * @param vre5
	 *            the new vre5
	 */
	public void setVre5(VREModel vre5) {
		this.vre5 = vre5;
	}

	/**
	 * Gets the recal.
	 *
	 * @return the recal
	 */
	@XmlElement(name = "recal", type = RecalModel.class)
	public RecalModel getRecal() {
		return recal;
	}

	/**
	 * Sets the recal.
	 *
	 * @param recal
	 *            the new recal
	 */
	public void setRecal(RecalModel recal) {
		this.recal = recal;
	}

	/**
	 * Gets the properties.
	 *
	 * @return the properties
	 */
	@XmlElement(name = "properties", type = PropertiesModel.class)
	public PropertiesModel getProperties() {
		return properties;
	}

	/**
	 * Sets the properties.
	 *
	 * @param properties
	 *            the new properties
	 */
	public void setProperties(PropertiesModel properties) {
		this.properties = properties;
	}

	/**
	 * Gets the errors.
	 *
	 * @return the errors
	 */
	@XmlElement(name = "errors", type = ErrorsHolderModel.class)
	public ErrorsHolderModel getErrors() {
		return errors;
	}

	/**
	 * Sets the errors.
	 *
	 * @param errors
	 *            the new errors
	 */
	public void setErrors(ErrorsHolderModel errors) {
		this.errors = errors;
	}

}
