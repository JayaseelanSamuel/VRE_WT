package com.brownfield.vre.exe.models;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class PropertiesModel.
 * 
 * @author Onkar Dhuri <onkar.dhuri@synerzip.com>
 */
@XmlRootElement(name = "properties")
public class PropertiesModel {

	/** The reservoir. */
	private String reservoir;

	/** The gor. */
	private double gor;

	/** The gas. */
	private double gas;

	/** The water. */
	private double water;

	/** The api. */
	private double api;

	/** The hold upv. */
	private double holdUPV;

	/** The hold uph. */
	private double holdUPH;

	/** The ffv. */
	private double ffv;

	/** The ffh. */
	private double ffh;

	/** The reservoir pressure. */
	private double reservoirPressure;

	/** The reservoir temperature. */
	private double reservoirTemperature;

	/** The water cut. */
	private double waterCut;
	
	/** The ii. */
	private double ii;

	/** The pi. */
	private double pi;

	/** The qgi. */
	private double qgi;

	/** The whp. */
	private double whp;

	/** The pdg depth. */
	private double pdgDepth;

	/** The pdg pressure. */
	private double pdgPressure;

	/** The divergence. */
	private double divergence;

	/** The convergence. */
	private double convergence;

	/** The tolerance. */
	private double tolerance;

	/**
	 * Gets the reservoir.
	 *
	 * @return the reservoir
	 */
	@XmlElement(name="reservoir")
	public String getReservoir() {
		return reservoir;
	}

	/**
	 * Sets the reservoir.
	 *
	 * @param reservoir the new reservoir
	 */
	public void setReservoir(String reservoir) {
		this.reservoir = reservoir;
	}

	/**
	 * Gets the gor.
	 *
	 * @return the gor
	 */
	@XmlElement(name="gor")
	public double getGor() {
		return gor;
	}

	/**
	 * Sets the gor.
	 *
	 * @param gor the new gor
	 */
	public void setGor(double gor) {
		this.gor = gor;
	}

	/**
	 * Gets the gas.
	 *
	 * @return the gas
	 */
	@XmlElement(name="gas")
	public double getGas() {
		return gas;
	}

	/**
	 * Sets the gas.
	 *
	 * @param gas the new gas
	 */
	public void setGas(double gas) {
		this.gas = gas;
	}

	/**
	 * Gets the water.
	 *
	 * @return the water
	 */
	@XmlElement(name="water")
	public double getWater() {
		return water;
	}

	/**
	 * Sets the water.
	 *
	 * @param water the new water
	 */
	public void setWater(double water) {
		this.water = water;
	}

	/**
	 * Gets the api.
	 *
	 * @return the api
	 */
	@XmlElement(name="api")
	public double getApi() {
		return api;
	}

	/**
	 * Sets the api.
	 *
	 * @param api the new api
	 */
	public void setApi(double api) {
		this.api = api;
	}

	/**
	 * Gets the hold upv.
	 *
	 * @return the hold upv
	 */
	@XmlElement(name="holdupv")
	public double getHoldUPV() {
		return holdUPV;
	}

	/**
	 * Sets the hold upv.
	 *
	 * @param holdUPV the new hold upv
	 */
	public void setHoldUPV(double holdUPV) {
		this.holdUPV = holdUPV;
	}

	/**
	 * Gets the hold uph.
	 *
	 * @return the hold uph
	 */
	@XmlElement(name="holduph")
	public double getHoldUPH() {
		return holdUPH;
	}

	/**
	 * Sets the hold uph.
	 *
	 * @param holdUPH the new hold uph
	 */
	public void setHoldUPH(double holdUPH) {
		this.holdUPH = holdUPH;
	}

	/**
	 * Gets the ffv.
	 *
	 * @return the ffv
	 */
	@XmlElement(name="ffv")
	public double getFfv() {
		return ffv;
	}

	/**
	 * Sets the ffv.
	 *
	 * @param ffv the new ffv
	 */
	public void setFfv(double ffv) {
		this.ffv = ffv;
	}

	/**
	 * Gets the ffh.
	 *
	 * @return the ffh
	 */
	@XmlElement(name="ffh")
	public double getFfh() {
		return ffh;
	}

	/**
	 * Sets the ffh.
	 *
	 * @param ffh the new ffh
	 */
	public void setFfh(double ffh) {
		this.ffh = ffh;
	}

	/**
	 * Gets the reservoir pressure.
	 *
	 * @return the reservoir pressure
	 */
	@XmlElement(name="reservoir_pressure")
	public double getReservoirPressure() {
		return reservoirPressure;
	}

	/**
	 * Sets the reservoir pressure.
	 *
	 * @param reservoirPressure the new reservoir pressure
	 */
	public void setReservoirPressure(double reservoirPressure) {
		this.reservoirPressure = reservoirPressure;
	}

	/**
	 * Gets the reservoir temperature.
	 *
	 * @return the reservoir temperature
	 */
	@XmlElement(name="reservoir_temperature")
	public double getReservoirTemperature() {
		return reservoirTemperature;
	}

	/**
	 * Sets the reservoir temperature.
	 *
	 * @param reservoirTemperature the new reservoir temperature
	 */
	public void setReservoirTemperature(double reservoirTemperature) {
		this.reservoirTemperature = reservoirTemperature;
	}

	/**
	 * Gets the water cut.
	 *
	 * @return the water cut
	 */
	@XmlElement(name="watercut")
	public double getWaterCut() {
		return waterCut;
	}

	/**
	 * Sets the water cut.
	 *
	 * @param waterCut the new water cut
	 */
	public void setWaterCut(double waterCut) {
		this.waterCut = waterCut;
	}
	
	
	/**
	 * Gets the ii.
	 *
	 * @return the ii
	 */
	@XmlElement(name="ii")
	public double getIi() {
		return ii;
	}
	
	/**
	 * Sets the ii.
	 *
	 * @param ii the new ii
	 */
	public void setIi(double ii) {
		this.ii = ii;
	}

	/**
	 * Gets the pi.
	 *
	 * @return the pi
	 */
	@XmlElement(name="pi")
	public double getPi() {
		return pi;
	}

	/**
	 * Sets the pi.
	 *
	 * @param pi the new pi
	 */
	public void setPi(double pi) {
		this.pi = pi;
	}

	/**
	 * Gets the qgi.
	 *
	 * @return the qgi
	 */
	@XmlElement(name="qgi")
	public double getQgi() {
		return qgi;
	}

	/**
	 * Sets the qgi.
	 *
	 * @param qgi the new qgi
	 */
	public void setQgi(double qgi) {
		this.qgi = qgi;
	}

	/**
	 * Gets the whp.
	 *
	 * @return the whp
	 */
	@XmlElement(name="whp")
	public double getWhp() {
		return whp;
	}

	/**
	 * Sets the whp.
	 *
	 * @param whp the new whp
	 */
	public void setWhp(double whp) {
		this.whp = whp;
	}

	/**
	 * Gets the pdg depth.
	 *
	 * @return the pdg depth
	 */
	@XmlElement(name="pdg_depth")
	public double getPdgDepth() {
		return pdgDepth;
	}

	/**
	 * Sets the pdg depth.
	 *
	 * @param pdgDepth the new pdg depth
	 */
	public void setPdgDepth(double pdgDepth) {
		this.pdgDepth = pdgDepth;
	}

	/**
	 * Gets the pdg pressure.
	 *
	 * @return the pdg pressure
	 */
	@XmlElement(name="pdg_pressure")
	public double getPdgPressure() {
		return pdgPressure;
	}

	/**
	 * Sets the pdg pressure.
	 *
	 * @param pdgPressure the new pdg pressure
	 */
	public void setPdgPressure(double pdgPressure) {
		this.pdgPressure = pdgPressure;
	}

	/**
	 * Gets the divergence.
	 *
	 * @return the divergence
	 */
	@XmlElement(name="divergence")
	public double getDivergence() {
		return divergence;
	}

	/**
	 * Sets the divergence.
	 *
	 * @param divergence the new divergence
	 */
	public void setDivergence(double divergence) {
		this.divergence = divergence;
	}

	/**
	 * Gets the convergence.
	 *
	 * @return the convergence
	 */
	@XmlElement(name="convergence")
	public double getConvergence() {
		return convergence;
	}

	/**
	 * Sets the convergence.
	 *
	 * @param convergence the new convergence
	 */
	public void setConvergence(double convergence) {
		this.convergence = convergence;
	}

	/**
	 * Gets the tolerance.
	 *
	 * @return the tolerance
	 */
	@XmlElement(name="tolerance")
	public double getTolerance() {
		return tolerance;
	}

	/**
	 * Sets the tolerance.
	 *
	 * @param tolerance the new tolerance
	 */
	public void setTolerance(double tolerance) {
		this.tolerance = tolerance;
	}

}
