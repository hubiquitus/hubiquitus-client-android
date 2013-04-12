/*
 * Copyright (c) Novedia Group 2012.
 *
 *    This file is part of Hubiquitus
 *
 *    Permission is hereby granted, free of charge, to any person obtaining a copy
 *    of this software and associated documentation files (the "Software"), to deal
 *    in the Software without restriction, including without limitation the rights
 *    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 *    of the Software, and to permit persons to whom the Software is furnished to do so,
 *    subject to the following conditions:
 *
 *    The above copyright notice and this permission notice shall be included in all copies
 *    or substantial portions of the Software.
 *
 *    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 *    INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 *    PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 *    FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 *    ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *    You should have received a copy of the MIT License along with Hubiquitus.
 *    If not, see <http://opensource.org/licenses/mit-license.php>.
 */

//Make it compatible with node and web browser
if (typeof define !== 'function') {
    var define = require('amdefine')(module)
}


define(
    ['./codes'],
    function (codes) {

        /**
         * Creates a new client that manages a connection and connects to the
         * hNode Server.
         */
        var HClient = function () {
            this._connectionStatus = codes.statuses.DISCONNECTED; //update by phonegap plugin
        };

        HClient.prototype = {
            connect:function (publisher, password, options) {
                this.publisher = publisher;
                this.options = options;

                //get domain
                var jid = this.splitJID(publisher);
                this.domain = jid[1];
                this.resource = jid[2];
                if(this.resource == null || this.resource=='')
                {
                    this.fullJid = jid[0] + '@' + this.domain;
                }
                else
                {
                    this.fullJid = jid[0] + '@' + this.domain + '/' + this.resource;
                }


                return cordova.exec(null, null, 'HClientPhoneGapPlugin', 'connect', [
                    {publisher:publisher, password:password, options:options, authCB:String(options.authCb) }
                ]);
            },
            login:function(publisher, password){ //only used to log in with username and password after authentication callback

                return cordova.exec(null, null, 'HClientPhoneGapPlugin', 'login', [
                    {publisher: publisher, password:password}
                ]);
            },
            disconnect:function () {
                this.publisher = null;
                return cordova.exec(null, null, 'HClientPhoneGapPlugin', 'disconnect', []);
            },

            subscribe:function (actor, callback) {
                cordova.exec(null, null, 'HClientPhoneGapPlugin', 'subscribe', [
                    {actor:actor, callback:String(callback)}
                ]);
            },

            unsubscribe:function (actor, callback) {
                cordova.exec(null, null, 'HClientPhoneGapPlugin', 'unsubscribe', [
                    {actor:actor, callback:String(callback)}
                ]);
            },

            send:function (hmessage, callback) {
                cordova.exec(null, null, 'HClientPhoneGapPlugin', 'send', [
                    {hmessage:hmessage, callback:String(callback)}
                ]);
            },

            getSubscriptions:function (callback) {
                cordova.exec(null, null, 'HClientPhoneGapPlugin', 'getSubscriptions', [
                    {callback:String(callback)}
                ]);
            },

            setFilter:function (filter, callback) {
                cordova.exec(null, null, 'HClientPhoneGapPlugin', 'setFilter', [
                    {filter:filter, callback:String(callback)}
                ]);
            },

            buildMessage:function (actor, type, payload, options) {
                options = options || {};

                if (!actor)
                    throw new Error('missing actor');

                if (!options.relevanceOffset)
                    return {
                        actor:actor,
                        convid:options.convid,
                        type:type,
                        priority:options.priority,
                        ref:options.ref,
                        relevance:options.relevance,
                        persistent:options.persistent,
                        location:options.location,
                        author:options.author,
                        published:options.published,
                        headers:options.headers,
                        timeout:options.timeout,
                        payload:payload
                    };
                else{
                    var x=new Date();
                    var y=x.getTime()+ parseFloat(options.relevanceOffset);
                    return{
                        actor:actor,
                        convid:options.convid,
                        type:type,
                        priority:options.priority,
                        ref:options.ref,
                        relevance:(new Date(y)),
                        persistent:options.persistent,
                        location:options.location,
                        author:options.author,
                        published:options.published,
                        headers:options.headers,
                        timeout:options.timeout,
                        payload:payload
                    };
                }

            },

            buildMeasure:function (actor, value, unit, options) {
                if (!value)
                    throw new Error('missing value');
                else if (!unit)
                    throw new Error('missing unit');

                return this.buildMessage(actor, 'hMeasure', {unit:unit, value:value}, options);
            },

            buildAlert:function (actor, alert, options) {
                if (!alert)
                    throw new Error('missing alert');

                return this.buildMessage(actor, 'hAlert', {alert:alert}, options);
            },

            buildAck:function (actor, ref, ack, options) {
                if (!ref)
                    throw new Error('missing ref');
                else if (!ack)
                    throw new Error('missing ack');
                else if (!/recv|read/i.test(ack))
                    throw new Error('ack does not match "recv" or "read"');
                if (!options)
                    options = {};
                options.ref = ref;

                return this.buildMessage(actor, 'hAck', {ack:ack}, options);
            },

            buildConvState:function (actor, convid, status, options) {
                if (!convid)
                    throw new Error('missing convid');
                else if (!status)
                    throw new Error('missing status');
                if (!options)
                    options = {};

                options.convid = convid;

                return this.buildMessage(actor, 'hConvState', {status:status}, options);
            },

            buildCommand:function (actor, cmd, params, options) {
                if (!actor)
                    throw new Error('missing actor');
                else if (!cmd)
                    throw new Error('missing cmd');

                return this.buildMessage(actor, 'hCommand', {cmd:cmd, params:params}, options);
            },

            buildResult:function (actor, ref, status, result, options) {
                if (!actor)
                    throw new Error('missing actor');
                else if (!ref)
                    throw new Error('missing ref');
                else if (status == null)
                    throw new Error('missing status');
                if (!options)
                    options = {};
                options.ref = ref;
                return this.buildMessage(actor, 'hResult', {status:status, result:result}, options);
            },

            checkJID:function (jid) {
                return new RegExp("^(?:([^@/<>'\"]+)@)([^@/<>'\"]+)(?:/([^/<>'\"]*))?$").test(jid);
            },

            splitJID:function (jid) {
                return jid.match(new RegExp("^(?:([^@/<>'\"]+)@)([^@/<>'\"]+)(?:/([^/<>'\"]*))?$")).splice(1, 3);
            },

            errors:codes.errors,
            statuses:codes.statuses,
            hResultStatus:codes.hResultStatus
        };

        cordova.addConstructor(function () {
            if (!window.plugins) {
                window.plugins = {};
            }
            window.plugins.hClient = new HClient();
        });
    }
)
