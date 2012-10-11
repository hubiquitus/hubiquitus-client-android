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

package org.hubiquitus.hapi.test;

import org.hubiquitus.hapi.client.HClient;
import org.hubiquitus.hapi.exceptions.MissingAttrException;
import org.hubiquitus.hapi.hStructures.HAck;
import org.hubiquitus.hapi.hStructures.HAckValue;
import org.hubiquitus.hapi.hStructures.HAlert;
import org.hubiquitus.hapi.hStructures.HConvState;
import org.hubiquitus.hapi.hStructures.HGeo;
import org.hubiquitus.hapi.hStructures.HLocation;
import org.hubiquitus.hapi.hStructures.HMeasure;
import org.hubiquitus.hapi.hStructures.HMessage;
import org.hubiquitus.hapi.hStructures.HMessageOptions;
import org.hubiquitus.hapi.hStructures.HMessagePriority;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

/**
 * @cond internal
 */

public class HBuilderTest {

	@Test
	public void HMessageBuildTest() {
		HClient hclient = new HClient();

		HMessageOptions hmessageOption = new HMessageOptions();

		hmessageOption.setAuthor("me");
		hmessageOption.setConvid("convid:123456789");

		JSONObject headers = new JSONObject();
		try {
			headers.put("header1", "1");
			headers.put("header2", "2");
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		
		hmessageOption.setHeaders(headers);

		HLocation location = new HLocation();
		HGeo pos = new HGeo(12.32,56.23);
		location.setPos(pos);
		hmessageOption.setLocation(location);

		hmessageOption.setPriority(HMessagePriority.INFO);

		DateTime date = new DateTime();
		hmessageOption.setRelevance(date);

		hmessageOption.setPersistent(false);

		JSONObject payload = new JSONObject();
		try {
			payload.put("test", "test");
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		HMessage hmessage = null;
		try {
			hmessage = hclient.buildMessage("chid:123456789", "string",
					payload, hmessageOption);
		} catch (MissingAttrException e) {
			Assert.fail();
		}

		
		Assert.assertEquals(hmessage.getAuthor(), "me");
		Assert.assertEquals(hmessage.getActor(), "chid:123456789");
		Assert.assertEquals(hmessage.getConvid(), "convid:123456789");
		Assert.assertEquals(hmessage.getMsgid(), null);
		Assert.assertEquals(hmessage.getType(), "string");
		Assert.assertEquals(hmessage.getHeaders().toString(),
				headers.toString());
		Assert.assertEquals(hmessage.getLocation().toString(),
				location.toString());
		
		Assert.assertEquals(hmessage.getPayloadAsJSONObject().toString(),
				payload.toString());
		Assert.assertEquals(hmessage.getPriority(), HMessagePriority.INFO);
		Assert.assertEquals(hmessage.getPublished(), null);
		Assert.assertEquals(hmessage.getPersistent(), false);
		Assert.assertEquals(hmessage.getRelevance(), date); 

	}

	@Test
	public void HConvStateBuildTest() {
		HClient hclient = new HClient();

		HMessageOptions hmessageOption = new HMessageOptions();

		hmessageOption.setAuthor("me");

		JSONObject headers = new JSONObject();
		try {
			headers.put("header1", "1");
			headers.put("header2", "2");
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		
		hmessageOption.setHeaders(headers);

		HLocation location = new HLocation();
		hmessageOption.setLocation(location);

		hmessageOption.setPriority(HMessagePriority.INFO);

		DateTime date = new DateTime();
		hmessageOption.setRelevance(date);

		hmessageOption.setPersistent(false);

		HMessage hmessage = null;
		try {
			hmessage = hclient.buildConvState("test channel", "test conv id",
					"test status", hmessageOption);
		} catch (MissingAttrException e) {
			Assert.fail();
		}

		HConvState hconvstate = new HConvState();
		try {
			hconvstate.setStatus("test status");
		} catch (MissingAttrException e) {
			Assert.fail();
			e.printStackTrace();
		}

		Assert.assertEquals(hmessage.getType(), "hConvState");
		Assert.assertEquals(hmessage.getPayloadAsJSONObject().toString(),
				hconvstate.toString());
	}

	@Test
	public void HAckBuildTest() {
		HClient hclient = new HClient();

		HMessageOptions hmessageOption = new HMessageOptions();

		hmessageOption.setAuthor("me");
		hmessageOption.setConvid("convid:123456789");

		JSONObject headers = new JSONObject();
		try {
			headers.put("header1", "1");
			headers.put("header2", "2");
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		hmessageOption.setHeaders(headers);

		HLocation location = new HLocation();
		hmessageOption.setLocation(location);

		hmessageOption.setPriority(HMessagePriority.INFO);

		DateTime date = new DateTime();
		hmessageOption.setRelevance(date);

		hmessageOption.setPersistent(false);

		HAckValue ackvalue = HAckValue.READ;

		HMessage hmessage = null;
		try {
			hmessage = hclient.buildAck("chid:123456789", "chid:123456789", ackvalue,
					hmessageOption);
		} catch (MissingAttrException e) {
			Assert.fail();
		}

		HAck hack = new HAck();
		try {
			hack.setAck(ackvalue);
		} catch (MissingAttrException e) {
			e.printStackTrace();
			Assert.fail();
		}

		Assert.assertEquals(hmessage.getType(), "hAck");
		Assert.assertEquals(hmessage.getPayloadAsJSONObject().toString(), hack.toString());
	}

	@Test
	public void HAlertBuildTest() {
		HClient hclient = new HClient();

		HMessageOptions hmessageOption = new HMessageOptions();

		hmessageOption.setAuthor("me");
		hmessageOption.setConvid("convid:123456789");

		JSONObject headers = new JSONObject();
		try {
			headers.put("header1", "1");
			headers.put("header2", "2");
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		hmessageOption.setHeaders(headers);

		HLocation location = new HLocation();
		hmessageOption.setLocation(location);

		hmessageOption.setPriority(HMessagePriority.INFO);

		DateTime date = new DateTime();
		hmessageOption.setRelevance(date);

		hmessageOption.setPersistent(false);

		String alert = "WARNING WARNING";

		HMessage hmessage = null;
		try {
			hmessage = hclient.buildAlert("chid:123456789", alert,
					hmessageOption);
		} catch (MissingAttrException e) {
			Assert.fail();
		}

		HAlert halert = new HAlert();
		try {
			halert.setAlert(alert);
		} catch (MissingAttrException e) {
			e.printStackTrace();
			Assert.fail();
		}

		Assert.assertEquals(hmessage.getType(), "hAlert");
		Assert.assertEquals(hmessage.getPayloadAsJSONObject().toString(), halert.toString());
	}

	@Test
	public void HBuildMeasureTest() {
		HClient hclient = new HClient();

		HMessageOptions hmessageOption = new HMessageOptions();

		hmessageOption.setAuthor("me");
		hmessageOption.setConvid("convid:123456789");

		JSONObject headers = new JSONObject();
		try {
			headers.put("header1", "1");
			headers.put("header2", "2");
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		hmessageOption.setHeaders(headers);

		HLocation location = new HLocation();
		hmessageOption.setLocation(location);

		hmessageOption.setPriority(HMessagePriority.INFO);

		DateTime date = new DateTime();
		hmessageOption.setRelevance(date);

		hmessageOption.setPersistent(false);

		String unit = "metre";
		String value = "100";

		HMessage hmessage = null;
		try {
			hmessage = hclient.buildMeasure("chid:123456789", value, unit,
					hmessageOption);
		} catch (MissingAttrException e) {
			Assert.fail();
		}

		HMeasure hmeasure = new HMeasure();
		try {
			hmeasure.setUnit(unit);
			hmeasure.setValue(value);
		} catch (MissingAttrException e) {
			e.printStackTrace();
			Assert.fail();
		}
		

		Assert.assertEquals(hmessage.getType(), "hMeasure");
		Assert.assertEquals(hmessage.getPayloadAsJSONObject().toString(),
				hmeasure.toString());
	}
}

/**
 * @endcond
 */
