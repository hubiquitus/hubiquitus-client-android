/*
 * Copyright (c) Novedia Group 2012.
 *
 *     This file is part of Hubiquitus.
 *
 *     Hubiquitus is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Hubiquitus is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Hubiquitus.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.hubiquitus.hapi.hStructures;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @version 0.5 
 * This structure describe the location
 */

public class HLocation extends JSONObject {

	final Logger logger = LoggerFactory.getLogger(HLocation.class);

	public HLocation() {
		super();
	};

	public HLocation(JSONObject jsonObj) throws JSONException {
		super(jsonObj.toString());
	}

	/* Getters & Setters */
	/**
	 * @return hGeo which specifies the exacte longitude and latitude of the location. Null if undefined.
	 */
	public HGeo getPos() {
		HGeo pos;
		try {
			pos = new HGeo(this.getJSONObject("pos"));
		} catch (Exception e) {
			pos = null;
		}
		return pos;
	}

	public void setPos(HGeo pos) {
		try {
			if (pos == null) {
				this.remove("pos");
			} else {
				this.put("pos", pos);
			}
		} catch (JSONException e) {
			logger.warn("message: ", e);
		}
	}

	/**
	 * @return the zip code of the location. NULL if undefined
	 */
	public String getZip() {
		String zip;
		try {
			zip = this.getString("zip");
		} catch (Exception e) {
			zip = null;
		}
		return zip;
	}

	public void setZip(String zip) {
		try {
			if (zip == null) {
				this.remove("zip");
			} else {
				this.put("zip", zip);
			}
		} catch (JSONException e) {
			logger.warn("message: ", e);
		}
	}

	/**
	 * @return the way number of the location. NULL if undefined
	 */
	public String getNum() {
		String num;
		try {
			num = this.getString("num");
		} catch (Exception e) {
			num = null;
		}
		return num;
	}

	public void setNum(String num) {
		try {
			if (num == null) {
				this.remove("num");
			} else {
				this.put("num", num);
			}
		} catch (JSONException e) {
			logger.warn("message: ", e);
		}
	}

	/**
	 * @return the type of the way of the location. NULL if undefined
	 */
	public String getWayType() {
		String wayType;
		try {
			wayType = this.getString("wayType");
		} catch (Exception e) {
			wayType = null;
		}
		return wayType;
	}

	public void setWayType(String wayType) {
		try {
			if (wayType == null) {
				this.remove("wayType");
			} else {
				this.put("wayType", wayType);
			}
		} catch (JSONException e) {
			logger.warn("message: ", e);
		}
	}

	/**
	 * @return the name of the street/way of the location. NULL if undefined
	 */
	public String getWay() {
		String way;
		try {
			way = this.getString("way");
		} catch (Exception e) {
			way = null;
		}
		return way;
	}

	public void setWay(String way) {
		try {
			if (way == null) {
				this.remove("way");
			} else {
				this.put("way", way);
			}
		} catch (JSONException e) {
			logger.warn("message: ", e);
		}
	}

	/**
	 * @return the address complement of the location. NULL if undefined
	 */
	public String getAddr() {
		String addr;
		try {
			addr = this.getString("addr");
		} catch (Exception e) {
			addr = null;
		}
		return addr;
	}

	public void setAddr(String addr) {
		try {
			if (addr == null) {
				this.remove("addr");
			} else {
				this.put("addr", addr);
			}
		} catch (JSONException e) {
			logger.warn("message: ", e);
		}
	}

	/**
	 * @return the floor number of the location. NULL if undefined
	 */
	public String getFloor() {
		String floor;
		try {
			floor = this.getString("floor");
		} catch (Exception e) {
			floor = null;
		}
		return floor;
	}

	public void setFloor(String floor) {
		try {
			if (floor == null) {
				this.remove("floor");
			} else {
				this.put("floor", floor);
			}
		} catch (JSONException e) {
			logger.warn("message: ", e);
		}
	}

	/**
	 * @return the building’s identifier of the location. NULL if undefined
	 */
	public String getBuilding() {
		String building;
		try {
			building = this.getString("building");
		} catch (Exception e) {
			building = null;
		}
		return building;
	}

	public void setBuilding(String building) {
		try {
			if (building == null) {
				this.remove("building");
			} else {
				this.put("building", building);
			}
		} catch (JSONException e) {
			logger.warn("message: ", e);
		}
	}

	/**
	 * @return city of the location. NULL if undefined
	 */
	public String getCity() {
		String city;
		try {
			city = this.getString("city");
		} catch (Exception e) {
			city = null;
		}
		return city;
	}

	public void setCity(String city) {
		try {
			if (city == null) {
				this.remove("city");
			} else {
				this.put("city", city);
			}
		} catch (JSONException e) {
			logger.warn("message: ", e);
		}
	}

	/**
	 * @return countryCode of the location. NULL if undefined
	 */
	public String getCountryCode() {
		String countryCode;
		try {
			countryCode = this.getString("countryCode");
		} catch (Exception e) {
			countryCode = null;
		}
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		try {
			if (countryCode == null) {
				this.remove("countryCode");
			} else {
				this.put("countryCode", countryCode);
			}
		} catch (JSONException e) {
			logger.warn("message: ", e);
		}
	}
}