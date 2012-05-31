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

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @version 0.3
 * This kind of payload is used to describe the metadata of a conversation identified by its convid.
 * A conversation can have different instance of hConv object, the last one will be considered as the most relevant. 
 */

public class HConv implements HJsonObj{

	private JSONObject hconv = new JSONObject();
		
	public HConv() {};
	
	public HConv(JSONObject jsonObj){
		fromJSON(jsonObj);
	}
	
	/* HJsonObj interface */
	
	public JSONObject toJSON() {
		return hconv;
	}
	
	public void fromJSON(JSONObject jsonObj) {
		if(jsonObj != null) {
			this.hconv = jsonObj; 
		} else {
			this.hconv = new JSONObject();
		}
	}
	
	public String getHType() {
		return "hconv";
	}
	
	@Override
	public String toString() {
		return hconv.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		return hconv.equals(obj);
	}
	
	@Override
	public int hashCode() {
		return hconv.hashCode();
	}
	
	/* Getters & Setters */
	
	/**
	 * The description of the topic of the conversation in a comprehensible form.
	 * @return topic description. NULL if undefined
	 */
	public String getTopic() {
		String topic;
		try {
			topic = hconv.getString("topic");
		} catch (Exception e) {
			topic = null;			
		}
		return topic;
	}

	public void setTopic(String topic) {
		try {
			if(topic == null) {
				hconv.remove("topic");
			} else {
				hconv.put("topic", topic);
			}
		} catch (JSONException e) {
		}
	}
	
	/**
	 * The list of JIDs of the entities participating in this conversation
	 * (i.e. authorized to update the conversation).
	 * @return Headers. NULL if undefined
	 */
	public List<String> getParticipants() {
		List<String> participants = new ArrayList<String>();
		try {
			JSONArray participantsArray = hconv.getJSONArray("participants");
			for(int i = 0; i < participantsArray.length() ; i++) {
				String participant = participantsArray.getString(i); 
				participants.add(participant);
			}
		} catch (JSONException e) {
			participants = null;
		}
		return participants;
	}

	public void setParticipants(List<String> participants) {
		try {
			if(participants == null) {
				hconv.remove("participants");
			} else {
				JSONArray participantsArray = new JSONArray();
				for(String participant : participants) {
					participantsArray.put(participant);
				}
				hconv.put("participants", participantsArray);		
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
}

