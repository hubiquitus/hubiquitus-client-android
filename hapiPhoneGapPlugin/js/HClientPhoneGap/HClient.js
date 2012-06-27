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
				this._connectionStatus = codes.statuses.DISCONNECTED; //update by phonegap plugin
			};

			HClient.prototype = {
					connect : function(publisher, password, options){
						this.publisher = publisher;
						this.options = options;
						
						return cordova.exec(null, null, 'HClientPhoneGapPlugin', 'connect', [{publisher: publisher, password: password, options:options}]);
					},
					disconnect : function(){
						this.publisher = null;
						return cordova.exec(null, null, 'HClientPhoneGapPlugin', 'disconnect', []);
					},
					

					command: function(cmd, callback){
						cordova.exec(null, null, 'HClientPhoneGapPlugin', 'command', [{hcommand: cmd, callback: String(callback)}]);
					},

					subscribe : function(channel, callback){
						cordova.exec(null, null, 'HClientPhoneGapPlugin', 'subscribe', [{chid: channel, callback: String(callback)}]);
					},

					unsubscribe : function(channel, callback){
						cordova.exec(null, null, 'HClientPhoneGapPlugin', 'unsubscribe', [{chid: channel, callback: String(callback)}]);
					},

					publish : function(hmessage, callback){
						cordova.exec(null, null, 'HClientPhoneGapPlugin', 'publish', [{hmessage: hmessage, callback: String(callback)}]);
					},

					getSubscriptions: function(callback){
						cordova.exec(null, null, 'HClientPhoneGapPlugin', 'getSubscriptions', [{callback: String(callback)}]);
					},

					getLastMessages: function(chid, nbLastMsg, callback){
						cordova.exec(null, null, 'HClientPhoneGapPlugin', 'getLastMessages', [{chid: chid, nbLastMsg: quantity, callback: String(callback)}]);
					},
					
					getLastMessages: function(chid, callback){
						cordova.exec(null, null, 'HClientPhoneGapPlugin', 'getLastMessages', [{chid: chid, nbLastMsg: -1, callback: String(callback)}]);
					},
					
					getThread: function(chid, convid, callback){
						cordova.exec(null, null, 'HClientPhoneGapPlugin', 'getThread', [{chid: chid, convid: convid, callback: String(callback)}]);
					},

					buildMessage: function(chid, type, payload, options){
		                options = options || {};

		                if(!chid)
		                    throw new Error('missing chid');

		                return {
		                    chid: chid,
		                    convid: options.convid,
		                    type: type,
		                    priority: options.priority,
		                    relevance: options.relevance,
		                    transient: options.transient,
		                    location: options.location,
		                    author: options.author,
		                    published: options.published,
		                    headers: options.headers,
		                    payload: payload
		                };
		            },

		            buildMeasure: function(chid, value, unit, options){
		                if(!value)
		                    throw new Error('missing value');
		                else if (!unit)
		                    throw new Error('missing unit');

		                return this.buildMessage(chid, 'hMeasure', {unit: unit, value: value}, options);
		            },

		            buildAlert: function(chid, alert, options){
		                if(!alert)
		                    throw new Error('missing alert');

		                return this.buildMessage(chid, 'hAlert', {alert: alert}, options);
		            },

		            buildAck: function(chid, ackid, ack, options){
		                if(!ackid)
		                    throw new Error('missing ackid');
		                else if(!ack)
		                    throw new Error('missing ack');
		                else if(!/recv|read/i.test(ack))
		                    throw new Error('ack does not match "recv" or "read"');

		                return this.buildMessage(chid, 'hAck', {ackid: ackid, ack: ack}, options);
		            },
		            
		            buildConvState: function(chid, convid, status, options){
		                if(!convid)
		                    throw new Error('missing convid');
		                else if(!status)
		                    throw new Error('missing status');
		                if(!options)
		                    options = {};

		                options.convid = convid;

		                return this.buildMessage(chid, 'hConvState', {status: status}, options);
		            },
		            
		            checkJID: function(jid){
		                return new RegExp("^(?:([^@/<>'\"]+)@)([^@/<>'\"]+)(?:/([^/<>'\"]*))?$").test(jid);
		            },

		            splitJID: function(jid){
		                return jid.match(new RegExp("^(?:([^@/<>'\"]+)@)([^@/<>'\"]+)(?:/([^/<>'\"]*))?$")).splice(1, 3);
		            },

					errors: codes.errors,
					statuses: codes.statuses,
					hResultStatus: codes.hResultStatus
			};


			cordova.addConstructor(function() {
				hClient = new HClient();
				cordova.addPlugin("hClient", hClient);
			});
		}
);
