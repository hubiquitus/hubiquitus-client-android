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

import java.util.Calendar;
import org.hubiquitus.hapi.client.HClient;
import org.hubiquitus.hapi.hStructures.HAck;
import org.hubiquitus.hapi.hStructures.HAckValue;
import org.hubiquitus.hapi.hStructures.HAlert;
import org.hubiquitus.hapi.hStructures.HConvState;
import org.hubiquitus.hapi.hStructures.HLocation;
import org.hubiquitus.hapi.hStructures.HMeasure;
import org.hubiquitus.hapi.hStructures.HMessage;
import org.hubiquitus.hapi.hStructures.HMessageOptions;
import org.hubiquitus.hapi.hStructures.HMessagePriority;
import org.hubiquitus.hapi.util.DateISO8601;
import org.hubiquitus.hapi.util.HJsonDictionnary;
import org.junit.Assert;
import org.junit.Test;
import exceptions.MissingAttrException;

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
		
		HJsonDictionnary headers = new HJsonDictionnary();
		headers.put("header1", "1");
		headers.put("header2", "2");
		hmessageOption.setHeaders(headers);		
		
		HLocation location = new HLocation();
		hmessageOption.setLocation(location);
		
		hmessageOption.setPriority(HMessagePriority.INFO);
		
		String dateIso = DateISO8601.now();
		Calendar date = DateISO8601.toCalendar(dateIso);
		hmessageOption.setRelevance(date);
		
		hmessageOption.setTransient(false);
		
		HJsonDictionnary payload = new HJsonDictionnary();
		payload.put("test", "test");
		HMessage hmessage = null;
		try {
			hmessage = hclient.buildMessage("chid:123456789", "string", payload, hmessageOption);
		} catch (MissingAttrException e) {
			Assert.fail();
		}
		
		Assert.assertEquals(hmessage.getAuthor(), "me");
		Assert.assertEquals(hmessage.getChid(), "chid:123456789");
		Assert.assertEquals(hmessage.getConvid(), "convid:123456789");
		Assert.assertEquals(hmessage.getMsgid(), null);
		Assert.assertEquals(hmessage.getType(), "string");
		Assert.assertEquals(hmessage.getHeaders().toString(), headers.toString());
		Assert.assertEquals(hmessage.getLocation().toString(), location.toString());
		Assert.assertEquals(hmessage.getPayload().toString(), payload.toString());
		Assert.assertEquals(hmessage.getPriority(), HMessagePriority.INFO);
		Assert.assertEquals(hmessage.getPublished(), null);
		Assert.assertEquals(hmessage.getRelevance(), date);
		Assert.assertEquals(hmessage.getTransient(), false);
	}

	
	@Test
	public void HConvStateBuildTest() {
		HClient hclient = new HClient();
		
		HMessageOptions hmessageOption = new HMessageOptions();
		
		hmessageOption.setAuthor("me");
		
		HJsonDictionnary headers = new HJsonDictionnary();
		headers.put("header1", "1");
		headers.put("header2", "2");
		hmessageOption.setHeaders(headers);		
		
		HLocation location = new HLocation();
		hmessageOption.setLocation(location);
		
		hmessageOption.setPriority(HMessagePriority.INFO);
		
		String dateIso = DateISO8601.now();
		Calendar date = DateISO8601.toCalendar(dateIso);
		hmessageOption.setRelevance(date);
		
		hmessageOption.setTransient(false);	
		
		HMessage hmessage = null;
		try {
			hmessage = hclient.buildConvState("test channel", "test conv id" , "test status", hmessageOption);
		} catch (MissingAttrException e) {
			Assert.fail();
		}
		
		HConvState hconvstate = new HConvState();
		hconvstate.setStatus("test status");
		
		Assert.assertEquals(hmessage.getType(),"hconvstate");
		Assert.assertEquals(hmessage.getPayload().toString(),hconvstate.toString());
	}
	
	@Test
	public void HAckBuildTest() {
		HClient hclient = new HClient();
		
		HMessageOptions hmessageOption = new HMessageOptions();
		
		hmessageOption.setAuthor("me");
		hmessageOption.setConvid("convid:123456789");
		
		HJsonDictionnary headers = new HJsonDictionnary();
		headers.put("header1", "1");
		headers.put("header2", "2");
		hmessageOption.setHeaders(headers);		
		
		HLocation location = new HLocation();
		hmessageOption.setLocation(location);
		
		hmessageOption.setPriority(HMessagePriority.INFO);
		
		String dateIso = DateISO8601.now();
		Calendar date = DateISO8601.toCalendar(dateIso);
		hmessageOption.setRelevance(date);
		
		hmessageOption.setTransient(false);
		
		HAckValue ackvalue = HAckValue.READ;
		String hackid = "ackid:123456789";
		
		HMessage hmessage = null;
		try {
			hmessage = hclient.buildAck("chid:123456789", hackid , ackvalue, hmessageOption);
		} catch (MissingAttrException e) {
			Assert.fail();
		}
		
		HAck hack = new HAck();
		hack.setAck(ackvalue);
		hack.setAckid(hackid);
		
		Assert.assertEquals(hmessage.getType(),"hack");
		Assert.assertEquals(hmessage.getPayload().toString(),hack.toString());
	}
	
	@Test
	public void HAlertBuildTest() {
		HClient hclient = new HClient();
		
		HMessageOptions hmessageOption = new HMessageOptions();
		
		hmessageOption.setAuthor("me");
		hmessageOption.setConvid("convid:123456789");
		
		HJsonDictionnary headers = new HJsonDictionnary();
		headers.put("header1", "1");
		headers.put("header2", "2");
		hmessageOption.setHeaders(headers);		
		
		HLocation location = new HLocation();
		hmessageOption.setLocation(location);
		
		hmessageOption.setPriority(HMessagePriority.INFO);
		
		String dateIso = DateISO8601.now();
		Calendar date = DateISO8601.toCalendar(dateIso);
		hmessageOption.setRelevance(date);
		
		hmessageOption.setTransient(false);
		
		String alert = "WARNING WARNING";
		
		HMessage hmessage = null;
		try {
			hmessage = hclient.buildAlert("chid:123456789",alert, hmessageOption);
		} catch (MissingAttrException e) {
			Assert.fail();
		}
		
		HAlert halert = new HAlert();
		halert.setAlert(alert);
		
		Assert.assertEquals(hmessage.getType(),"halert");
		Assert.assertEquals(hmessage.getPayload().toString(),halert.toString());
	}
	
	@Test
	public void HBuildMeasureTest() {
		HClient hclient = new HClient();
		
		HMessageOptions hmessageOption = new HMessageOptions();
		
		hmessageOption.setAuthor("me");
		hmessageOption.setConvid("convid:123456789");
		
		HJsonDictionnary headers = new HJsonDictionnary();
		headers.put("header1", "1");
		headers.put("header2", "2");
		hmessageOption.setHeaders(headers);		
		
		HLocation location = new HLocation();
		hmessageOption.setLocation(location);
		
		hmessageOption.setPriority(HMessagePriority.INFO);
		
		String dateIso = DateISO8601.now();
		Calendar date = DateISO8601.toCalendar(dateIso);
		hmessageOption.setRelevance(date);
		
		hmessageOption.setTransient(false);
		
		String unit = "metre";
		String value = "100";
		
		HMessage hmessage = null;
		try {
			hmessage = hclient.buildMeasure("chid:123456789",value,unit, hmessageOption);
		} catch (MissingAttrException e) {
			Assert.fail();
		}
		
		HMeasure hmeasure = new HMeasure();
		hmeasure.setUnit(unit);
		hmeasure.setValue(value);
		
		Assert.assertEquals(hmessage.getType(),"hmeasure");
		Assert.assertEquals(hmessage.getPayload().toString(),hmeasure.toString());
	}
}

/**
 * @endcond
 */
