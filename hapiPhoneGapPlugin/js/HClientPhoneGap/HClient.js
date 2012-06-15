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
					connect : function(publisher, password, hOptions){
						this.publisher = publisher;
						this.options = hOptions;
						
						//get domain
						var jid = publisher.split('@');
						this.domain = jid[1];
						
						return cordova.exec(null, null, 'HClientPhoneGapPlugin', 'connect', [{publisher: publisher, password: password, options:hOptions}]);
					},
					disconnect : function(){
						this.publisher = null;
						return cordova.exec(null, null, 'HClientPhoneGapPlugin', 'disconnect', []);
					},
					

					command: function(hCommand, callback){
						cordova.exec(null, null, 'HClientPhoneGapPlugin', 'command', [{hcommand: hCommand, callback: String(callback)}]);
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

					getLastMessages: function(chid, quantity, callback){
						cordova.exec(null, null, 'HClientPhoneGapPlugin', 'getLastMessages', [{chid: chid, nbLastMsg: quantity, callback: String(callback)}]);
					},

					buildMessage: function(chid, type, payload, options){
						options = options || {};

						if(!chid){
							if(this.hCallback)
								this.hCallback({
									type : codes.types.hResult,
									data : {
										cmd : 'hPublish',
										status : codes.hResultStatus.MISSING_ATTR,
										result : 'missing chid'
									}
								});
							return;
						}

						if(this._checkConnected()) 
						return {
							chid: chid,
							convid: options.convid,
							type: type,
							priority: options.priority,
							relevance: options.relevance,
							transient: options.transient,
							location: options.location,
							author: options.author,
							publisher: this.publisher,
							headers: options.headers,
							payload: payload
						};
					},

					buildMeasure: function(chid, value, unit, options){

						if(!value || !unit){
							if(this.hCallback)
								this.hCallback({
									type : codes.types.hResult,
									data : {
										cmd : 'hPublish',
										status : codes.hResultStatus.MISSING_ATTR,
										result : 'missing value or unit'
									}
								});
							return;
						}

						return this.buildMessage(chid, 'hMeasure', {unit: unit, value: value}, options);
					},

					buildAlert: function(chid, alert, options){
						if(!alert){
							if(this.hCallback)
								this.hCallback({
									type : codes.types.hResult,
									data : {
										cmd : 'hPublish',
										status : codes.hResultStatus.MISSING_ATTR,
										result : 'missing alert'
									}
								});
							return;
						}

						return this.buildMessage(chid, 'hAlert', {alert: alert}, options);
					},

					buildAck: function(chid, ackid, ack, options){
						var status = null;
						var result = null;

						if(!ackid || !ack){
							status = codes.hResultStatus.MISSING_ATTR;
							result = 'missing ackid or ack';
						} else if(!/recv|read/i.test(ack)) {
							status = codes.hResultStatus.INVALID_ATTR;
							result = 'ack does not match "recv" or "read"';
						}

						if( status != null ){
							if(this.hCallback)
								this.hCallback({
									type : codes.types.hResult,
									data : {
										cmd : 'hPublish',
										status: status,
										result: result
									}
								});
							return;
						}

						return this.buildMessage(chid, 'hAck', {ackid: ackid, ack: ack}, options);
					},

					buildConv: function(chid, topic, participants, options){

						return this.buildMessage(chid, 'hConv', {topic: topic, participants: participants}, options);
					},
					
					_checkConnected: function() {				
						if (this._connectionStatus == codes.statuses.CONNECTED || this._connectionStatus == codes.statuses.REATTACHED) {
							return true;
						} else {
							if(this.hCallback){
			                    var currentStatus = this._connectionStatus;
			                    var code = currentStatus == codes.statuses.DISCONNECTED ?
			                        codes.errors.NOT_CONNECTED : codes.errors.CONN_PROGRESS;
			                    this.hCallback({
			                        type: codes.types.hStatus,
			                        data : {
			                            status: currentStatus,
			                            errorCode: code
			                        }
			                    });
			                }
							return false;
						}
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
