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
        serverHost: document.getElementById('serverHost').value,
        serverPort: document.getElementById('serverPort').value,
        transport: transport,
        endpoints: endpoints
    };
    var username = document.getElementById('username').value;
    var password = document.getElementById('password').value;
    hClient.connect(username, password, hCallback, hOptions);
}

function disconnect(){
    hClient.disconnect();
}

function clear_divs(){
    document.getElementById("status").innerHTML = '';
    document.getElementById("fetched").innerHTML = '';
}

function hCallback(msg){
    //console.log(JSON.stringify(msg));
    //console.log("callback message is : " + msg);
	var status = '';
    var error = '';
    if(msg.type == 'hStatus'){
        switch(msg.data.status){
            case hClient.ConnectionStatus.CONNECTED:
                status = 'Connected';
                break;
            case hClient.ConnectionStatus.CONNECTING:
                status = 'Connecting';
                break;
            case hClient.ConnectionStatus.REATTACHING:
                status = 'Reattaching';
                break;
            case hClient.ConnectionStatus.REATTACHED:
                status = 'Reattached';
                break;
            case hClient.ConnectionStatus.DISCONNECTING:
                status = 'Disconnecting';
                break;
            case hClient.ConnectionStatus.DISCONNECTED:
                status = 'Disconnected';
                break;
        }

        switch(msg.data.errorCode){
            case hClient.ConnectionError.NO_ERROR:
                error = 'No Error Detected';
                break;
            case hClient.ConnectionError.JID_MALFORMAT:
                error = 'JID Malformat';
                break;
            case hClient.ConnectionError.CONN_TIMEOUT:
                error = 'Connection timed out';
                break;
            case hClient.ConnectionError.AUTH_FAILED:
                error = 'Authentication failed';
                break;
            case hClient.ConnectionError.ATTACH_FAILED:
                error = 'Attach failed';
                break;
            case hClient.ConnectionError.ALREADY_CONNECTED:
                error = 'A connection is already opened';
                break;
            case hClient.ConnectionError.TECH_ERROR:
                error = 'Technical Error: ';
                error += msg.data.errorMsg;
                break;
            case hClient.ConnectionError.NOT_CONNECTED:
                error = 'Not connected';
                break;
            case hClient.ConnectionError.CONN_PROGRESS:
                error = 'A connection is already in progress';
                break;
        }
        //console.log("status is : ", status);
        document.getElementById("status").innerHTML = JSON.stringify(status + ' : ' + error);
    }
    else if (msg.type == 'hResult')
        document.getElementById("fetched").innerHTML = JSON.stringify(msg.data);
    else if (msg.context == 'message')
        document.getElementById("fetched").innerHTML = msg.data.message;
}
