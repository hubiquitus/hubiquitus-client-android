package org.hubiquitus.hapi.structures;

import org.json.JSONObject;

/**
 * JSON serializable interface for hstructures
 * Should be implemented by all hstructures
 *
 */
public interface HJSONSerializable {
	public JSONObject toJSON();
	public void fromJSON(JSONObject jsonObj)  throws Exception;
}
