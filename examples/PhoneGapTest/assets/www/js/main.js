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


function connect(){
    var endpoint = document.getElementById('endpoint').value;
    var endpoints = endpoint ? [endpoint] : undefined;

    var transports =  document.getElementsByName('transport');
    var transport = undefined;
    for (var i=0; i < transports.length; i++)
        if(transports[i].checked)
            transport = transports[i].value;

    var hOptions = {
        transport: transport,
        endpoints: endpoints,
        timeout: 3000
    };

    var username = document.getElementById('username').value;
    var password = document.getElementById('password').value;

    window.plugins.hClient.onMessage = onMessage;
    window.plugins.hClient.onStatus = onStatus;
    window.plugins.hClient.connect(username, password, hOptions);
}

function disconnect(){
    window.plugins.hClient.disconnect();
}

function send(){
    var actor = document.getElementById('actor').value;
    var msg = document.getElementById('message').value;
    var cb = function (hMessage) {callback(hMessage)};
    var msgOptions = {
        persistent:document.getElementById('hMessagePersistent').checked,
        timeout:document.getElementById('timeout').value,
        relevanceOffset:document.getElementById('relevanceoffset').value
    }
    window.plugins.hClient.send(window.plugins.hClient.buildMessage(actor, 'string', msg, msgOptions), cb);
}

function subscribe(){
    var actor = document.getElementById('actor').value;
    var cb = function (hMessage) {callback(hMessage)};
    window.plugins.hClient.subscribe(actor, cb)
}

function unsubscribe(){
    var actor = document.getElementById('actor').value;
    var cb = function (hMessage) {callback(hMessage)};
    window.plugins.hClient.unsubscribe(actor, cb)
}

function get_messages(){
    var actor = document.getElementById('actor').value;
    var nblastmsg = document.getElementById('nblastmsg').value;
    var cb = function (hMessage) {callback(hMessage)};
    window.plugins.hClient.getLastMessages(actor, nblastmsg, cb);
}

function get_relevant_message(){
    var actor = document.getElementById('actor').value;
    var cb = function(hMessage){callback(hMessage)}
    window.plugins.hClient.getRelevantMessage(actor,cb);
}

function pub_convstate(){
    var actor = document.getElementById('actor').value;
    var convid = document.getElementById('convid').value;
    var convstate = document.getElementById('convstatus').value;
    var msgOption = {
        persistent:document.getElementById('hMessagePersistent').checked,
        timeout: 3000
    };
    var cb = function(hMessage){callback(hMessage)}
    var convstatemsg = window.plugins.hClient.buildConvState(actor, convid, convstate, msgOption);
    window.plugins.hClient.send(convstatemsg,cb);
}

function set_filter(){
    var filter = {
        in:{
            publisher:['u2@localhost']
        }
    };
    var cb = function(hMessage){callback(hMessage)}
    window.plugins.hClient.setFilter(filter,cb);
}

function get_subscriptions(){
	var cb = function (hMessage) {callback(hMessage)};
    window.plugins.hClient.getSubscriptions(cb);
}

function get_thread(){
    var actor = document.getElementById('actor').value;
    var convid = prompt('Convid:');
    var cb = function (hMessage) {callback(hMessage)};
    window.plugins.hClient.getThread(actor, convid, cb);
}

function get_threads(){
    var actor = document.getElementById('actor').value;
    var convState = prompt('ConvState:');
    var cb = function (hMessage) {callback(hMessage)};
    window.plugins.hClient.getThreads(actor, convState, cb);
}

function clear_divs(){
    document.getElementById("status").innerHTML = 'Status: ';
    document.getElementById("hMessage").innerHTML = '';
}


function build_test(){
    console.log('-------build test-------');
    var options = {
        relevance: new Date(),
        relevanceOffset: '120000',
        ref: 'msgidref123'
    }
    console.log('build_measure: ');
    console.log('-----> ' + JSON.stringify(window.plugins.hClient.buildMeasure('u1@test', 'value123','unit123',options)));
    console.log('build_alert: ');
    console.log('-----> ' + JSON.stringify(window.plugins.hClient.buildAlert('u1@test','alert123',options)));
    console.log('build_ack: ');
    console.log('-----> ' + JSON.stringify(window.plugins.hClient.buildAck('u1@test','ref123','read',options)));
    console.log('build_convstate: ');
    console.log('-----> ' + JSON.stringify(window.plugins.hClient.buildConvState('u1@test','convid123','status123',options)));
    console.log('build_command: ');
    console.log('-----> ' + JSON.stringify(window.plugins.hClient.buildCommand('u1@test','cmd123',{params:'params123'},options)));
    console.log('build_result: ');
    console.log('-----> ' + JSON.stringify(window.plugins.hClient.buildResult('u1@test', 'ref123', 0, {result:'result123'},options)));
}


function build_measure(){
    var value = prompt('Value:');
    var unit = prompt('Unit:');
    var actor = prompt('Channel:');
    var hMessage = window.plugins.hClient.buildMeasure(actor, value, unit, {
        persistent: !!document.getElementById("hMessagePersistent").checked
    });
    var fct = function (hMessage) {onMessage(hMessage)};
    if(hMessage)
    	document.getElementById("resultsDiv").innerHTML = JSON.stringify(hMessage);
    if(document.getElementById("sendBuiltMessage").checked)
        window.plugins.hClient.send(hMessage, fct);
}

function build_alert(){
    var alert = prompt('Alert:');
    var actor = prompt('Channel:');
    var hMessage = window.plugins.hClient.buildAlert(actor, alert, {
        persistent: !!document.getElementById("hMessagePersistent").checked
    });
    var fct = function (hMessage) {onMessage(hMessage)};
    if(hMessage)
    	document.getElementById("resultsDiv").innerHTML = JSON.stringify(hMessage);
    if(document.getElementById("sendBuiltMessage").checked)
        window.plugins.hClient.send(hMessage, fct);
}

function build_ack(){
    var ackID = prompt('AckID:');
    var ack= prompt('Ack (recv|read):');
    var actor = prompt('Channel:');
    var hMessage = window.plugins.hClient.buildAck(actor, ackID, ack, {
        persistent: !!document.getElementById("hMessagePersistent").checked
    });
    var fct = function (hMessage) {onMessage(hMessage)};
    if(hMessage)
    	document.getElementById("resultsDiv").innerHTML = JSON.stringify(hMessage);
    if(document.getElementById("sendBuiltMessage").checked)
        window.plugins.hClient.send(hMessage, fct);
}

function build_convstate(){
    var status = prompt('status:');
    var convid = prompt('conversation id:');
    var actor = prompt('channel:');
    var hMessage = window.plugins.hClient.buildConvState(actor, convid, status, {
        persistent: !!document.getElementById("hMessagePersistent").checked
    });
    var fct = function (hMessage) {onMessage(hMessage)};
    if(hMessage)
    	document.getElementById("resultsDiv").innerHTML = JSON.stringify(hMessage);
    if(document.getElementById("sendBuiltMessage").checked)
        window.plugins.hClient.send(hMessage, fct);
}

function onStatus(hStatus){
    var status,error;

    switch(hStatus.status){
        case window.plugins.hClient.statuses.CONNECTED:
            status = 'Connected';
            break;
        case window.plugins.hClient.statuses.CONNECTING:
            status = 'Connecting';
            break;
        case window.plugins.hClient.statuses.DISCONNECTING:
            status = 'Disconnecting';
            break;
        case window.plugins.hClient.statuses.DISCONNECTED:
            status = 'Disconnected';
            break;
    }

    switch(hStatus.errorCode){
        case window.plugins.hClient.errors.NO_ERROR:
            error = 'No Error Detected';
            break;
        case window.plugins.hClient.errors.JID_MALFORMAT:
            error = 'JID Malformat';
            break;
        case window.plugins.hClient.errors.CONN_TIMEOUT:
            error = 'Connection timed out';
            break;
        case window.plugins.hClient.errors.AUTH_FAILED:
            error = 'Authentication failed';
            break;
        case window.plugins.hClient.errors.ATTACH_FAILED:
            error = 'Attach failed';
            break;
        case window.plugins.hClient.errors.ALREADY_CONNECTED:
            error = 'A connection is already opened';
            break;
        case window.plugins.hClient.errors.TECH_ERROR:
            error = 'Technical Error: ';
            error += hStatus.errorMsg;
            break;
        case window.plugins.hClient.errors.NOT_CONNECTED:
            error = 'Not connected';
            break;
        case window.plugins.hClient.errors.CONN_PROGRESS:
            error = 'A connection is already in progress';
            break;
    }

    document.getElementById("status").innerHTML = 'Status: ' + status + ' / ' + error;
}

function callback(hMessage){
    document.getElementById('callback').innerHTML = "Callback : " + JSON.stringify(hMessage);
}

function onMessage(hMessage){
    document.getElementById("hMessage").innerHTML = "Message : " + JSON.stringify(hMessage);
}