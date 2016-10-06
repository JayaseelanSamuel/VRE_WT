package com.brownfield.vre;

/**
 * The Enum TagType.
 * 
 * @author Onkar Dhuri <onkar.dhuri@synerzip.com>
 */
public enum TagType {

	/** The whp. */
	WHP(1),

	/** The wht. */
	WHT(2),

	/** The choke size. */
	CHOKE_SIZE(3),

	/** The downhole pressure. */
	DOWNHOLE_PRESSURE(4),

	/** The gaslift inj rate. */
	GASLIFT_INJ_RATE(5),

	/** The water vol rate. */
	WATER_VOL_RATE(6),

	/** The oil vol rate. */
	OIL_VOL_RATE(7),

	/** The ann pressure a. */
	ANN_PRESSURE_A(8),

	/** The ann pressure b. */
	ANN_PRESSURE_B(9),

	/** The liquid rate. */
	LIQUID_RATE(10),

	/** The gas rate. */
	GAS_RATE(11),

	/** The watercut. */
	WATERCUT(12),

	/** The header pressure. */
	HEADER_PRESSURE(13),

	/** The inj header pressure. */
	INJ_HEADER_PRESSURE(14),

	/** The VR e6_ calc. */
	VRE6_CALC(15),

	/** The separator pressure. */
	SEPARATOR_PRESSURE(16),

	/** The water inj rate. */
	WATER_INJ_RATE(17),

	/** The watercut honeywell. */
	WATERCUT_HONEYWELL(18),

	/* Model -> */

	/** The gor. */
	GOR(19),

	/** The pi. */
	PI(20),

	/** The holdup. */
	HOLDUP(21),

	/** The friction factor. */
	FRICTION_FACTOR(22),

	/** The reservoir pressure. */
	RESERVOIR_PRESSURE(23),

	/** The choke setting. */
	CHOKE_SETTING(24),

	/** The technical rate. */
	TECHNICAL_RATE(25);

	/** The num val. */
	private int numVal;

	/**
	 * Instantiates a new tag type.
	 *
	 * @param numVal
	 *            the num val
	 */
	TagType(int numVal) {
		this.numVal = numVal;
	}

	/**
	 * Gets the num val.
	 *
	 * @return the num val
	 */
	public int getNumVal() {
		return numVal;
	}
}