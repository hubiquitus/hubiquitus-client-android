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

import static org.junit.Assert.fail;

import org.hubiquitus.hapi.hStructures.ConnectionError;
import org.hubiquitus.hapi.hStructures.ConnectionStatus;
import org.hubiquitus.hapi.hStructures.HAck;
import org.hubiquitus.hapi.hStructures.HAckValue;
import org.hubiquitus.hapi.hStructures.HAlert;
import org.hubiquitus.hapi.hStructures.HArrayOfValue;
import org.hubiquitus.hapi.hStructures.HCommand;
import org.hubiquitus.hapi.hStructures.HCondition;
import org.hubiquitus.hapi.hStructures.HConvState;
import org.hubiquitus.hapi.hStructures.HGeo;
import org.hubiquitus.hapi.hStructures.HLocation;
import org.hubiquitus.hapi.hStructures.HMeasure;
import org.hubiquitus.hapi.hStructures.HMessage;
import org.hubiquitus.hapi.hStructures.HMessagePriority;
import org.hubiquitus.hapi.hStructures.HResult;
import org.hubiquitus.hapi.hStructures.HStatus;
import org.hubiquitus.hapi.hStructures.HValue;
import org.hubiquitus.hapi.hStructures.OperandNames;
import org.hubiquitus.hapi.hStructures.ResultStatus;
import org.joda.time.DateTime;
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
			
			String actor = "actor:123456789";
			jsonObj.put("actor", actor);
			
			String convid = "convid:123456789";
			jsonObj.put("convid", convid);
			
			String type = "type:123456";
			jsonObj.put("type", type);
			
			jsonObj.put("priority", 1);
			
			DateTime date = new DateTime();
			jsonObj.put("relevance", date);
			
			Boolean _persistent = false;
			jsonObj.put("persistent", _persistent);
			
			HLocation location = new HLocation();
			HGeo pos = new HGeo(100,100);
			location.setPos(pos);
			location.setZip("79000");
			jsonObj.put("location",location);
			
			String author = "Mysth";
			jsonObj.put("author", author);
			
			String publisher = "j.desousag";
			jsonObj.put("publisher", publisher);
			
			jsonObj.put("published", date);

			JSONObject headers = new JSONObject();
			headers.put("header1", "1");
			headers.put("header2", "2");
			jsonObj.put("headers", headers);
			
			JSONObject payload = new JSONObject();
			payload.put("payload", "payload");
			JSONObject payloadResult = new JSONObject();
			payloadResult.put("payload", "payload");
			jsonObj.put("payload", payload);
			
			HMessage hmessage =  new HMessage(jsonObj);
			
			jsonObj = hmessage;
			
			Assert.assertEquals(hmessage.getAuthor(), author);
			Assert.assertEquals(hmessage.getActor(), actor);
			Assert.assertEquals(hmessage.getConvid(), convid);
			Assert.assertEquals(hmessage.getMsgid(), msgid);
			Assert.assertEquals(hmessage.getPublisher(), publisher);
			Assert.assertEquals(hmessage.getType(), type);
			Assert.assertEquals(hmessage.getHeaders().toString(), headers.toString());
			Assert.assertEquals(hmessage.getLocation().toString(), location.toString());
			Assert.assertEquals(hmessage.getPayloadAsJSONObject().toString(), payloadResult.toString());
			Assert.assertEquals(hmessage.getPriority(), HMessagePriority.INFO);
			Assert.assertEquals(hmessage.getPublished().toString(), date.toString());
			Assert.assertEquals(hmessage.getRelevance().toString(), date.toString());
			Assert.assertEquals(hmessage.getPersistent(), _persistent);
			
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
			
			String actor = "actor:123456789";
			
			String convid = "convid:123456789";
			
			String type = "type:123456";
			
			HMessagePriority priority = HMessagePriority.INFO;
			
			DateTime date = new DateTime();
			
			Boolean _persistent = false;
			
			HLocation location = new HLocation();
			HGeo pos = new HGeo(100,100);
			location.setPos(pos);
			location.setZip("79000");
			
			String author = "Mysth";
			
			String publisher = "j.desousag";

			JSONObject headers = new JSONObject();
			try {
				headers.put("header1", "1");
				headers.put("header2", "2");
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			
			
			JSONObject payload = new JSONObject();
			payload.put("payload", "payload");
			
			HMessage hmessage =  new HMessage();
			hmessage.setAuthor(author);
			hmessage.setActor(actor);
			hmessage.setConvid(convid);
			hmessage.setHeaders(headers);
			hmessage.setLocation(location);
			hmessage.setMsgid(msgid);
			hmessage.setPayload(payload);
			hmessage.setPriority(priority);
			hmessage.setPublished(date);
			hmessage.setPublisher(publisher);
			hmessage.setRelevance(date);
			hmessage.setPersistent(_persistent);
			hmessage.setType(type);
			
			jsonObj = hmessage;
			
			System.out.println("-->get :  " + jsonObj.get("published"));
			System.out.println("-->date : " + date.toString());
			
			Assert.assertEquals(jsonObj.get("author"), author);
			Assert.assertEquals(jsonObj.get("actor"), actor);
			Assert.assertEquals(jsonObj.get("convid"), convid);
			Assert.assertEquals(jsonObj.get("msgid"), msgid);
			Assert.assertEquals(jsonObj.get("publisher"), publisher);
			Assert.assertEquals(jsonObj.get("type"), type);
			Assert.assertEquals(jsonObj.get("headers").toString(), headers.toString());
			Assert.assertEquals(jsonObj.get("location").toString(), location.toString());
			Assert.assertEquals(jsonObj.get("payload"), payload);
			Assert.assertEquals(jsonObj.get("priority"), priority.value());
			Assert.assertEquals(jsonObj.get("published").toString(), date.toString());
			Assert.assertEquals(jsonObj.get("relevance").toString(), date.toString());
			Assert.assertEquals(jsonObj.get("persistent"), _persistent);
		} catch (Exception e) {
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
			
			jsonObj = hconvstate;
			
			Assert.assertEquals(jsonObj.get("status"), status);
		} catch (Exception e) {
			e.printStackTrace();
			fail("fail");
		}
	}
	
	//HLocation test
	@Test
	public void HlocationGetTest() {
		JSONObject jsonObj = new JSONObject();
		try {
			HGeo pos = new HGeo(100.0, 100.0);
			jsonObj.put("pos", pos);
			
			String zip = "79000";
			jsonObj.put("zip", zip);
			
			String addr = "48 rue des Anges";
			jsonObj.put("addr", addr);
			
			String countryCode = "France";
			jsonObj.put("countryCode", countryCode);
			
			String city = "Paris";
			jsonObj.put("city", city);
			
			JSONObject extra = new JSONObject();
			extra.put("test", "temp");
			
			String num = "24";
			jsonObj.put("num", num);
			
			String floor = "3eme";
			jsonObj.put("floor", floor);
			
			String way = "ange";
			jsonObj.put("way", way);
			
			String wayType = "boulevard";
			jsonObj.put("wayType", wayType);
			
			String building = "tour Montparnasse";
			jsonObj.put("building", building);
	
			
			HLocation hlocation = new HLocation(jsonObj);

			Assert.assertEquals(hlocation.getPos().toString(), pos.toString());
			Assert.assertEquals(hlocation.getZip(), zip);
			Assert.assertEquals(hlocation.getAddr(), addr);
			Assert.assertEquals(hlocation.getCountryCode(), countryCode);
			Assert.assertEquals(hlocation.getCity(), city);
			Assert.assertEquals(hlocation.getNum(), num);
			Assert.assertEquals(hlocation.getFloor(), floor);
			Assert.assertEquals(hlocation.getWay(), way);
			Assert.assertEquals(hlocation.getWayType(), wayType);
			Assert.assertEquals(hlocation.getBuilding(), building);
		} catch (JSONException e) {
			e.printStackTrace();
			fail("fail");
		}
	}
	
	@Test
	public void HlocationSetTest() {
		JSONObject jsonObj = new JSONObject();
		try {
			HGeo pos = new HGeo(100.0,100.0);
			
			String num = "24";
			
			String zip = "79000";
			
			String addr = "48 rue des Anges";
			
			String countryCode = "France";
			
			String city = "Paris";
			
			String floor = "3eme";
			
			String way = "ange";
			
			String wayType = "boulevard";
			
			String building = "tour Montparnasse";
						
			HLocation hlocation = new HLocation();
			hlocation.setPos(pos);
			hlocation.setAddr(addr);
			hlocation.setBuilding(building);
			hlocation.setCity(city);
			hlocation.setCountryCode(countryCode);
			hlocation.setFloor(floor);
			hlocation.setPos(pos);
			hlocation.setNum(num);
			hlocation.setWay(way);
			hlocation.setWayType(wayType);
			hlocation.setZip(zip);
			jsonObj = hlocation;
			
			Assert.assertEquals(jsonObj.getJSONObject("pos").toString(), pos.toString());
			Assert.assertEquals(jsonObj.get("floor"), floor);
			Assert.assertEquals(jsonObj.get("building"), building);
			Assert.assertEquals(jsonObj.get("way"), way);
			Assert.assertEquals(jsonObj.get("wayType"), wayType);
			Assert.assertEquals(jsonObj.get("num"), num);
			Assert.assertEquals(jsonObj.get("zip"), zip);
			Assert.assertEquals(jsonObj.get("addr"), addr);
			Assert.assertEquals(jsonObj.get("countryCode"), countryCode);
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

			HAck hack = new HAck(jsonObj);

			Assert.assertEquals(hack.getAck(), HAckValue.READ);
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
						
			HAck hack = new HAck();
			hack.setAck(ack);
			
			jsonObj = hack;
			Assert.assertEquals(jsonObj.get("ack"), ack.value());
		} catch (Exception e) {
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
		
					
			JSONObject params = new JSONObject();
			params.put("text", "test");
			jsonObj.put("params", params);
			
		
			HCommand hcommand = new HCommand(jsonObj);

			Assert.assertEquals(hcommand.getCmd(), cmd);
			Assert.assertEquals(hcommand.getParams().toString(), params.toString());
			
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
			
			
			HCommand hcommand = new HCommand();
			hcommand.setCmd(cmd);
			
			jsonObj = hcommand;

			Assert.assertEquals(jsonObj.get("cmd"), cmd);
		} catch (Exception e) {
			e.printStackTrace();
			fail("fail");
		} 
	}
	
	//HAlert test
	@Test
	public void HAlertSetTest() {
		JSONObject jsonObj = new JSONObject();
		try {
			String alert = "Failure";
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
			
			jsonObj = halert;
			
			Assert.assertEquals(jsonObj.get("alert"), alert);
		} catch (Exception e) {
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
			jsonObj = hmeasure;

			Assert.assertEquals(jsonObj.get("unit"), unit);
			Assert.assertEquals(jsonObj.get("value"), value);
		} catch (Exception e) {
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
			jsonObj = hstatus;

			Assert.assertEquals(jsonObj.get("status"), status.value());
			Assert.assertEquals(jsonObj.get("errorCode"), errorCode.value());
			Assert.assertEquals(jsonObj.get("errorMsg"), errorMsg);
		} catch (Exception e) {
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

            JSONObject result = new JSONObject();
			result.put("test", "test");
			jsonObj.put("result", result);
						
			HResult hresult = new HResult(jsonObj);

			Assert.assertEquals(hresult.getStatus(), status);
			Assert.assertEquals(hresult.getResultAsJSONObject(), result);
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


            JSONObject result = new JSONObject();
			result.put("test", "test");
						
			HResult hresult = new HResult();
			hresult.setResult(result);
			hresult.setStatus(status);
			jsonObj = hresult;

			Assert.assertEquals(jsonObj.get("status"), status.value());
			Assert.assertEquals(jsonObj.get("result"), result);
		} catch (Exception e) {
			e.printStackTrace();
			fail("fail");
		}
	}
	
	@Test
	public void HConditionTest(){
		HCondition condition = new HCondition();
		System.out.println("--> condition vide : " + condition);
		HValue value = new HValue();
		value.setName("auther");
		value.setValue("u1@localhost");
		condition.setValue(OperandNames.EQ, value);
		HArrayOfValue values = new HArrayOfValue();
		JSONArray jsonArray = new JSONArray();
		jsonArray.put("u1@localhost");
		jsonArray.put("u2@localhost");
		values.setName("auther");
		values.setValues(jsonArray);
		condition.setValueArray(OperandNames.IN, values);
		HCondition cond = new HCondition();
		cond.setValue(OperandNames.NE, value);
		cond.setValueArray(OperandNames.NIN, values);
		condition.setCondition(OperandNames.NOT, cond);
		JSONArray conditionArray = new JSONArray();
		conditionArray.put(cond);
		conditionArray.put(cond);
		conditionArray.put(cond);
		condition.setConditionArray(OperandNames.AND, conditionArray);
		System.out.println("--> :\n" + condition.toString());
		Assert.assertEquals(condition.getValue(OperandNames.EQ).toString(), value.toString());
		Assert.assertEquals(condition.getArrayOfValue(OperandNames.IN).toString(), values.toString());
		Assert.assertEquals(condition.getCondition(OperandNames.NOT).toString(), cond.toString());
		Assert.assertEquals(condition.getConditionArray(OperandNames.AND).toString(), conditionArray.toString());
		
	}
	
	
	
}

/**
 * @endcond
 */
