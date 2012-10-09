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
						
						//get domain
						var jid = this.splitJID(publisher); 
						this.domain = jid[1];
						
						return cordova.exec(null, null, 'HClientPhoneGapPlugin', 'connect', [{publisher: publisher, password: password, options:options}]);
					},
					disconnect : function(){
						this.publisher = null;
						return cordova.exec(null, null, 'HClientPhoneGapPlugin', 'disconnect', []);
					},

					subscribe : function(actor, callback){
						cordova.exec(null, null, 'HClientPhoneGapPlugin', 'subscribe', [{actor: actor, callback: String(callback)}]);
					},

					unsubscribe : function(actor, callback){
						cordova.exec(null, null, 'HClientPhoneGapPlugin', 'unsubscribe', [{actor: actor, callback: String(callback)}]);
					},

					send : function(hmessage, callback){
						cordova.exec(null, null, 'HClientPhoneGapPlugin', 'send', [{hmessage: hmessage, callback: String(callback)}]);
					},

					getSubscriptions: function(callback){
						cordova.exec(null, null, 'HClientPhoneGapPlugin', 'getSubscriptions', [{callback: String(callback)}]);
					},

					getLastMessages: function(actor, nbLastMsg, callback){
						cordova.exec(null, null, 'HClientPhoneGapPlugin', 'getLastMessages', [{actor: actor, nbLastMsg: quantity, callback: String(callback)}]);
					},
					
					getLastMessages: function(actor, callback){
						cordova.exec(null, null, 'HClientPhoneGapPlugin', 'getLastMessages', [{actor: actor, nbLastMsg: -1, callback: String(callback)}]);
					},
					
					getThread: function(actor, convid, callback){
						cordova.exec(null, null, 'HClientPhoneGapPlugin', 'getThread', [{actor: actor, convid: convid, callback: String(callback)}]);
					},
					
					getThreads: function(actor, convState, callback){
						cordova.exec(null, null, 'HClientPhoneGapPlugin', 'getThreads', [{actor: actor, convState: convState, callback: String(callback)}]);
					},

					buildMessage: function(actor, type, payload, options){
		                options = options || {};

		                if(!actor)
		                    throw new Error('missing actor');

		                return {
                            actor: actor,
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

		            buildMeasure: function(actor, value, unit, options){
		                if(!value)
		                    throw new Error('missing value');
		                else if (!unit)
		                    throw new Error('missing unit');

		                return this.buildMessage(actor, 'hMeasure', {unit: unit, value: value}, options);
		            },

		            buildAlert: function(actor, alert, options){
		                if(!alert)
		                    throw new Error('missing alert');

		                return this.buildMessage(actor, 'hAlert', {alert: alert}, options);
		            },

		            buildAck: function(actor, ackid, ack, options){
		                if(!ackid)
		                    throw new Error('missing ackid');
		                else if(!ack)
		                    throw new Error('missing ack');
		                else if(!/recv|read/i.test(ack))
		                    throw new Error('ack does not match "recv" or "read"');

		                return this.buildMessage(actor, 'hAck', {ackid: ackid, ack: ack}, options);
		            },
		            
		            buildConvState: function(actor, convid, status, options){
		                if(!convid)
		                    throw new Error('missing convid');
		                else if(!status)
		                    throw new Error('missing status');
		                if(!options)
		                    options = {};

		                options.convid = convid;

		                return this.buildMessage(actor, 'hConvState', {status: status}, options);
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
