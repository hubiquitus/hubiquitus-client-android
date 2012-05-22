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

//Make it compatible with node and web browser
if (typeof define !== 'function') { var define = require('amdefine')(module) }

define(
    ['./codes'],
    function(codes){

        /**
         * Creates a new client that manages a connection and connects to the
         * hNode Server.
         */
        var HClient = function(){
        	
        };

        HClient.prototype = {
            connect : function(publisher, password, hCallback, hOptions){
            	return cordova.exec(null, null, 'HClientPhoneGapPlugin', 'connect', [{publisher: publisher, password: password, callback: String(hCallback), options:hOptions}]);
            },
            disconnect : function(){
            	return cordova.exec(null, null, 'HClientPhoneGapPlugin', 'disconnect', []);
            },
            
            command: function(hCommand){
            	return cordova.exec(null, null, 'HClientPhoneGapPlugin', 'hcommand', [{hcommand: hCommand}]);
            },
            
                errors: codes.errors,
                status: codes.statuses,
                hResultStatus: codes.hResultStatus
        };
            
            
        cordova.addConstructor(function() {
        	hClient = new HClient();
        	cordova.addPlugin("hClient", hClient);
        });
    }
);
