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
	private int tagTypeID;


	/**
	 * Instantiates a new tag type.
	 *
	 * @param tagTypeID the tag type id
	 */
	TagType(int tagTypeID) {
		this.tagTypeID = tagTypeID;
	}


	/**
	 * Gets the tag type id.
	 *
	 * @return the tag type id
	 */
	public int getTagTypeID() {
		return tagTypeID;
	}


	/**
	 * Gets the tag type from id.
	 *
	 * @param tagTypeID the tag type id
	 * @return the tag type from id
	 */
	public static TagType getTagTypeFromID(final int tagTypeID) {
		switch (tagTypeID) {
		case 1:
			return WHP;
		case 2:
			return WHT;
		case 3:
			return CHOKE_SIZE;
		case 4:
			return DOWNHOLE_PRESSURE;
		case 5:
			return GASLIFT_INJ_RATE;
		case 6:
			return WATER_VOL_RATE;
		case 7:
			return OIL_VOL_RATE;
		case 8:
			return ANN_PRESSURE_A;
		case 9:
			return ANN_PRESSURE_B;
		case 10:
			return LIQUID_RATE;
		case 11:
			return GAS_RATE;
		case 12:
			return WATERCUT;
		case 13:
			return HEADER_PRESSURE;
		case 14:
			return INJ_HEADER_PRESSURE;
		case 15:
			return VRE6_CALC;
		case 16:
			return SEPARATOR_PRESSURE;
		case 17:
			return WATER_INJ_RATE;
		case 18:
			return WATERCUT_HONEYWELL;
		case 19:
			return GOR;
		case 20:
			return PI;
		case 21:
			return HOLDUP;
		case 22:
			return FRICTION_FACTOR;
		case 23:
			return RESERVOIR_PRESSURE;
		case 24:
			return CHOKE_SETTING;
		case 25:
			return TECHNICAL_RATE;
		default:
			return null;
		}
	}
}