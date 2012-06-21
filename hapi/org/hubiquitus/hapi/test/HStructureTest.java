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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.hubiquitus.hapi.hStructures.ConnectionError;
import org.hubiquitus.hapi.hStructures.ConnectionStatus;
import org.hubiquitus.hapi.hStructures.HAck;
import org.hubiquitus.hapi.hStructures.HAckValue;
import org.hubiquitus.hapi.hStructures.HAlert;
import org.hubiquitus.hapi.hStructures.HCommand;
import org.hubiquitus.hapi.hStructures.HConvState;
import org.hubiquitus.hapi.hStructures.HJsonObj;
import org.hubiquitus.hapi.hStructures.HLocation;
import org.hubiquitus.hapi.hStructures.HMeasure;
import org.hubiquitus.hapi.hStructures.HMessage;
import org.hubiquitus.hapi.hStructures.HMessagePriority;
import org.hubiquitus.hapi.hStructures.HResult;
import org.hubiquitus.hapi.hStructures.HStatus;
import org.hubiquitus.hapi.hStructures.ResultStatus;
import org.hubiquitus.hapi.util.DateISO8601;
import org.hubiquitus.hapi.util.HJsonDictionnary;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

/**
 * @cond internal
 */

public class HStructureTest {
	
	//HMessage test
	@Test
	public void HMessageGetTest() {
		JSONObject jsonObj = new JSONObject();
		try {
			String msgid = "msgid:123456789";
			jsonObj.put("msgid", msgid);
			
			String chid = "chid:123456789";
			jsonObj.put("chid", chid);
			
			String convid = "convid:123456789";
			jsonObj.put("convid", convid);
			
			String type = "type:123456";
			jsonObj.put("type", type);
			
			jsonObj.put("priority", 1);
			
			String dateIso = DateISO8601.now();
			Calendar date = DateISO8601.toCalendar(dateIso);
			jsonObj.put("relevance", dateIso);
			
			Boolean _transient = false;
			jsonObj.put("transient", _transient);
			
			HLocation location = new HLocation();
			location.setLat(100);
			location.setLng(100);
			location.setZip("79000");
			jsonObj.put("location",location.toJSON());
			
			String author = "Mysth";
			jsonObj.put("author", author);
			
			String publisher = "j.desousag";
			jsonObj.put("publisher", publisher);
			
			jsonObj.put("published", dateIso);

			List<HJsonObj> headers = new ArrayList<HJsonObj>();
			HJsonDictionnary header1 = new HJsonDictionnary();
			header1.put("header1", "1");
			HJsonDictionnary header2 = new HJsonDictionnary();
			header2.put("header2", "2");
			headers.add(header1);
			headers.add(header2);
			JSONArray headersArray = new JSONArray();
			for(HJsonObj header : headers) {
				headersArray.put(header.toJSON());
			}
			jsonObj.put("headers", headersArray);
			
			JSONObject payload = new JSONObject();
			payload.put("payload", "payload");
			HJsonDictionnary payloadResult = new HJsonDictionnary();
			payloadResult.put("payload", "payload");
			jsonObj.put("payload", payload);
			
			HMessage hmessage =  new HMessage(jsonObj);
			
			jsonObj = hmessage.toJSON();
			
			Assert.assertEquals(hmessage.getAuthor(), author);
			Assert.assertEquals(hmessage.getChid(), chid);
			Assert.assertEquals(hmessage.getConvid(), convid);
			Assert.assertEquals(hmessage.getMsgid(), msgid);
			Assert.assertEquals(hmessage.getPublisher(), publisher);
			Assert.assertEquals(hmessage.getType(), type);
			Assert.assertEquals(hmessage.getHeaders().toString(), headers.toString());
			Assert.assertEquals(hmessage.getLocation().toString(), location.toString());
			Assert.assertEquals(hmessage.getPayload().toString(), payloadResult.toString());
			Assert.assertEquals(hmessage.getPriority(), HMessagePriority.INFO);
			Assert.assertEquals(hmessage.getPublished(), date);
			Assert.assertEquals(hmessage.getRelevance(), date);
			Assert.assertEquals(hmessage.getTransient(), _transient);
			
		} catch (JSONException e) {
			e.printStackTrace();
			fail("fail");
		}
	}
	
	@Test
	public void HMessageSetTest() {
		JSONObject jsonObj = new JSONObject();
		try {
			String msgid = "msgid:123456789";
			
			String chid = "chid:123456789";
			
			String convid = "convid:123456789";
			
			String type = "type:123456";
			
			HMessagePriority priority = HMessagePriority.INFO;
			
			String dateIso = DateISO8601.now();
			Calendar date = DateISO8601.toCalendar(dateIso);
			
			Boolean _transient = false;
			
			HLocation location = new HLocation();
			location.setLat(100);
			location.setLng(100);
			location.setZip("79000");
			
			String author = "Mysth";
			
			String publisher = "j.desousag";

			List<HJsonObj> headers = new ArrayList<HJsonObj>();
			HJsonDictionnary header1 = new HJsonDictionnary();
			header1.put("header1", "1");
			HJsonDictionnary header2 = new HJsonDictionnary();
			header2.put("header2", "2");
			headers.add(header1);
			headers.add(header2);
			JSONArray headersArray = new JSONArray();
			for(HJsonObj header : headers) {
				headersArray.put(header.toJSON());
			}
			
			HJsonDictionnary payload = new HJsonDictionnary();
			payload.put("payload", "payload");
			
			HMessage hmessage =  new HMessage();
			hmessage.setAuthor(author);
			hmessage.setChid(chid);
			hmessage.setConvid(convid);
			hmessage.setHeaders(headers);
			hmessage.setLocation(location);
			hmessage.setMsgid(msgid);
			hmessage.setPayload(payload);
			hmessage.setPriority(priority);
			hmessage.setPublished(date);
			hmessage.setPublisher(publisher);
			hmessage.setRelevance(date);
			hmessage.setTransient(_transient);
			hmessage.setType(type);
			
			jsonObj = hmessage.toJSON();
			
			Assert.assertEquals(jsonObj.get("author"), author);
			Assert.assertEquals(jsonObj.get("chid"), chid);
			Assert.assertEquals(jsonObj.get("convid"), convid);
			Assert.assertEquals(jsonObj.get("msgid"), msgid);
			Assert.assertEquals(jsonObj.get("publisher"), publisher);
			Assert.assertEquals(jsonObj.get("type"), type);
			Assert.assertEquals(jsonObj.get("headers").toString(), headersArray.toString());
			Assert.assertEquals(jsonObj.get("location").toString(), location.toString());
			Assert.assertEquals(jsonObj.get("payload"), payload.toJSON());
			Assert.assertEquals(jsonObj.get("priority"), priority.value());
			Assert.assertEquals(jsonObj.get("published"), dateIso);
			Assert.assertEquals(jsonObj.get("relevance"), dateIso);
			Assert.assertEquals(jsonObj.get("transient"), _transient);
		} catch (JSONException e) {
			e.printStackTrace();
			fail("fail");
		}
	}
	
	//HConv test
	@Test
	public void HConvStateGetTest() {
		JSONObject jsonObj = new JSONObject();
		try {
			String status = "test";
			jsonObj.put("status", status);
			
			HConvState hconvstate = new HConvState(jsonObj);
			Assert.assertEquals(hconvstate.getStatus(), status);
		} catch (JSONException e) {
			e.printStackTrace();
			fail("fail");
		}
	}
	
	@Test
	public void HConvStateSetTest() {
		JSONObject jsonObj = new JSONObject();
		try {
			String status = "test";
			
			HConvState hconvstate = new HConvState();
			hconvstate.setStatus(status);
			
			jsonObj = hconvstate.toJSON();
			
			Assert.assertEquals(jsonObj.get("status"), status);
		} catch (JSONException e) {
			e.printStackTrace();
			fail("fail");
		}
	}
	
	//HLocation test
	@Test
	public void HlocationGetTest() {
		JSONObject jsonObj = new JSONObject();
		try {
			Double longitude = 100.0;
			jsonObj.put("lng", longitude);
			
			Double latitude = 100.0;
			jsonObj.put("lat", latitude);
			
			String zip = "79000";
			jsonObj.put("zip", zip);
			
			String address = "48 rue des Anges";
			jsonObj.put("addr", address);
			
			String country = "France";
			jsonObj.put("country", country);
			
			String city = "Paris";
			jsonObj.put("city", city);
			
			HJsonDictionnary extra = new HJsonDictionnary();
			extra.put("test", "temp");
			
			HLocation hlocation = new HLocation(jsonObj);

			Assert.assertEquals(hlocation.getLng(), longitude, 0);
			Assert.assertEquals(hlocation.getLat(), latitude, 0);
			Assert.assertEquals(hlocation.getZip(), zip);
			Assert.assertEquals(hlocation.getAddress(), address);
			Assert.assertEquals(hlocation.getCountry(), country);
			Assert.assertEquals(hlocation.getCity(), city);
		} catch (JSONException e) {
			e.printStackTrace();
			fail("fail");
		}
	}
	
	@Test
	public void HlocationSetTest() {
		JSONObject jsonObj = new JSONObject();
		try {
			Double longitude = 100.0;
			
			Double latitude = 100.0;
			
			String zip = "79000";
			
			String address = "48 rue des Anges";
			
			String country = "France";
			
			String city = "Paris";
			
			HJsonDictionnary extra = new HJsonDictionnary();
			extra.put("test", "temp");
			
			HLocation hlocation = new HLocation();
			hlocation.setAddress(address);
			hlocation.setCity(city);
			hlocation.setCountry(country);
			hlocation.setLat(latitude);
			hlocation.setLng(longitude);
			hlocation.setZip(zip);
			jsonObj = hlocation.toJSON();
			
			Assert.assertEquals(jsonObj.getDouble("lng"), longitude, 0);
			Assert.assertEquals(jsonObj.getDouble("lat"), latitude, 0);
			Assert.assertEquals(jsonObj.get("zip"), zip);
			Assert.assertEquals(jsonObj.get("addr"), address);
			Assert.assertEquals(jsonObj.get("country"), country);
			Assert.assertEquals(jsonObj.get("city"), city);
		} catch (JSONException e) {
			e.printStackTrace();
			fail("fail");
		}
	}
	
	//HAck test
	@Test
	public void HAckSetTest() {
		JSONObject jsonObj = new JSONObject();
		try {
			String ack = "read";
			jsonObj.put("ack", ack);
			
			String ackid = "ackid:123456789";
			jsonObj.put("ackid", ackid);
						
			HAck hack = new HAck(jsonObj);

			Assert.assertEquals(hack.getAck(), HAckValue.READ);
			Assert.assertEquals(hack.getAckid(), ackid);
		} catch (JSONException e) {
			e.printStackTrace();
			fail("fail");
		}
	}
	
	@Test
	public void HAckGetTest() {
		JSONObject jsonObj = new JSONObject();
		try {
			HAckValue ack = HAckValue.READ;			
			String ackid = "ackid:123456789";
						
			HAck hack = new HAck();
			hack.setAck(ack);
			hack.setAckid(ackid);
			
			jsonObj = hack.toJSON();
			Assert.assertEquals(jsonObj.get("ack"), ack.value());
			Assert.assertEquals(jsonObj.get("ackid"), ackid);
		} catch (JSONException e) {
			e.printStackTrace();
			fail("fail");
		}
	}
	
	//HCommand test
	@Test
	public void HCommandGetTest() {
		JSONObject jsonObj = new JSONObject();
		try {
			String cmd = "publish";
			jsonObj.put("cmd", cmd);
			
			String entity = "me";
			jsonObj.put("entity", entity);
			
			String reqid = "reqid:123456789";
			jsonObj.put("reqid", reqid);
			
			String requester = "you";
			jsonObj.put("requester", requester);
			
			String sender = "him";
			jsonObj.put("sender", sender);
			
			String dateIso = DateISO8601.now();
			Calendar date = DateISO8601.toCalendar(dateIso);
			jsonObj.put("sent", dateIso);
			
			HJsonDictionnary params = new HJsonDictionnary();
			params.put("text", "test");
			jsonObj.put("params", params.toJSON());
			
			Boolean _transient = false; 
			jsonObj.put("transient", _transient);
			
			HCommand hcommand = new HCommand(jsonObj);

			Assert.assertEquals(hcommand.getCmd(), cmd);
			Assert.assertEquals(hcommand.getEntity(), entity);
			Assert.assertEquals(hcommand.getReqid(), reqid);
			Assert.assertEquals(hcommand.getRequester(), requester);
			Assert.assertEquals(hcommand.getSender(), sender);
			Assert.assertEquals(hcommand.getSent(), date);
			Assert.assertEquals(hcommand.getParams().toString(), params.toString());
			Assert.assertEquals(hcommand.getTransient(), _transient);
			
		} catch (JSONException e) {
			e.printStackTrace();
			fail("fail");
		}
	}
	
	@Test
	public void HCommandSetTest() {
		JSONObject jsonObj = new JSONObject();
		try {
			String cmd = "publish";
			
			String entity = "me";
			
			String reqid = "reqid:123456789";
			
			String requester = "you";
			
			String sender = "him";
			
			String dateIso = DateISO8601.now();
			Calendar date = DateISO8601.toCalendar(dateIso);
			
			Boolean _transient = false; 
			
			HCommand hcommand = new HCommand();
			hcommand.setCmd(cmd);
			hcommand.setEntity(entity);
			hcommand.setReqid(reqid);
			hcommand.setRequester(requester);
			hcommand.setSender(sender);
			hcommand.setSent(date);
			hcommand.setTransient(_transient);
			
			jsonObj = hcommand.toJSON();

			Assert.assertEquals(jsonObj.get("cmd"), cmd);
			Assert.assertEquals(jsonObj.get("entity"), entity);
			Assert.assertEquals(jsonObj.get("reqid"), reqid);
			Assert.assertEquals(jsonObj.get("requester"), requester);
			Assert.assertEquals(jsonObj.get("sender"), sender);
			Assert.assertEquals(jsonObj.get("sent"), dateIso);
			Assert.assertEquals(jsonObj.get("transient"), _transient);
		} catch (JSONException e) {
			e.printStackTrace();
			fail("fail");
		}
	}
	
	//HAlert test
	@Test
	public void HAlertSetTest() {
		JSONObject jsonObj = new JSONObject();
		try {
			String alert = "warning";
			jsonObj.put("alert", alert);
						
			HAlert halert = new HAlert(jsonObj);

			Assert.assertEquals(halert.getAlert(), alert);
		} catch (JSONException e) {
			e.printStackTrace();
			fail("fail");
		}
	}
	
	@Test
	public void HAlertGetTest() {
		JSONObject jsonObj = new JSONObject();
		try {
			String alert = "warning";
						
			HAlert halert = new HAlert();
			halert.setAlert(alert);
			
			jsonObj = halert.toJSON();
			
			Assert.assertEquals(jsonObj.get("alert"), alert);
		} catch (JSONException e) {
			e.printStackTrace();
			fail("fail");
		}
	}

	//HMeasure test
	@Test
	public void HMeasureSetTest() {
		JSONObject jsonObj = new JSONObject();
		try {
			String unit = "metre";
			jsonObj.put("unit", unit);
			
			String value ="17";
			jsonObj.put("value", value);
						
			HMeasure hmeasure = new HMeasure(jsonObj);

			Assert.assertEquals(hmeasure.getUnit(), unit);
			Assert.assertEquals(hmeasure.getValue(), value);
		} catch (JSONException e) {
			e.printStackTrace();
			fail("fail");
		}
	}
	
	@Test
	public void HMeasureGetTest() {
		JSONObject jsonObj = new JSONObject();
		try {
			String unit = "metre";
			
			String value ="17";
						
			HMeasure hmeasure = new HMeasure();
			hmeasure.setUnit(unit);
			hmeasure.setValue(value);
			jsonObj = hmeasure.toJSON();

			Assert.assertEquals(jsonObj.get("unit"), unit);
			Assert.assertEquals(jsonObj.get("value"), value);
		} catch (JSONException e) {
			e.printStackTrace();
			fail("fail");
		}
	}
	
	//HStatus test
	@Test
	public void HStatusSetTest() {
		JSONObject jsonObj = new JSONObject();
		try {
			ConnectionStatus status = ConnectionStatus.CONNECTING;
			jsonObj.put("status", status.value());
			
			ConnectionError errorCode = ConnectionError.CONN_PROGRESS;
			jsonObj.put("errorCode", errorCode.value());
			
			String errorMsg ="message d'erreur";
			jsonObj.put("errorMsg", errorMsg);
						
			HStatus hstatus = new HStatus(jsonObj);

			Assert.assertEquals(hstatus.getStatus(), status);
			Assert.assertEquals(hstatus.getErrorCode(), errorCode);
			Assert.assertEquals(hstatus.getErrorMsg(), errorMsg);
		} catch (JSONException e) {
			e.printStackTrace();
			fail("fail");
		}
	}
	
	@Test
	public void HStatusGetTest() {
		JSONObject jsonObj = new JSONObject();
		try {
			ConnectionStatus status = ConnectionStatus.CONNECTING;
			
			ConnectionError errorCode = ConnectionError.CONN_PROGRESS;
			
			String errorMsg ="message d'erreur";
						
			HStatus hstatus = new HStatus();
			hstatus.setErrorCode(errorCode);
			hstatus.setStatus(status);
			hstatus.setErrorMsg(errorMsg);
			jsonObj = hstatus.toJSON();

			Assert.assertEquals(jsonObj.get("status"), status.value());
			Assert.assertEquals(jsonObj.get("errorCode"), errorCode.value());
			Assert.assertEquals(jsonObj.get("errorMsg"), errorMsg);
		} catch (JSONException e) {
			e.printStackTrace();
			fail("fail");
		}
	}
	
	//HResult test
	@Test
	public void HResultSetTest() {
		JSONObject jsonObj = new JSONObject();
		try {
			ResultStatus status = ResultStatus.NO_ERROR;
			jsonObj.put("status", status.value());
			
			String cmd = "hpublish";
			jsonObj.put("cmd", cmd);
			
			String reqid ="reqid:123456789";
			jsonObj.put("reqid", reqid);
			
			HJsonDictionnary result = new HJsonDictionnary();
			result.put("test", "test");
			jsonObj.put("result", result.toJSON());
						
			HResult hresult = new HResult(jsonObj);

			Assert.assertEquals(hresult.getStatus(), status);
			Assert.assertEquals(hresult.getCmd(), cmd);
			Assert.assertEquals(hresult.getReqid(), reqid);
			Assert.assertEquals(hresult.getResult().toString(), result.toString());
		} catch (JSONException e) {
			e.printStackTrace();
			fail("fail");
		}
	}
	
	@Test
	public void HResultGetTest() {
		JSONObject jsonObj = new JSONObject();
		try {
			ResultStatus status = ResultStatus.NO_ERROR;
			
			String cmd = "hpublish";
			
			String reqid ="reqid:123456789";
			
			HJsonDictionnary result = new HJsonDictionnary();
			result.put("test", "test");
						
			HResult hresult = new HResult();
			hresult.setCmd(cmd);
			hresult.setReqid(reqid);
			hresult.setResult(result);
			hresult.setStatus(status);
			jsonObj = hresult.toJSON();

			Assert.assertEquals(jsonObj.get("status"), status.value());
			Assert.assertEquals(jsonObj.get("cmd"), cmd);
			Assert.assertEquals(jsonObj.get("reqid"), reqid);
			Assert.assertEquals(jsonObj.get("result"), result.toJSON());
		} catch (JSONException e) {
			e.printStackTrace();
			fail("fail");
		}
	}
	
	
	
}

/**
 * @endcond
 */
