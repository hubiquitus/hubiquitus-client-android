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

package org.hubiquitus.hapi.util;


public class ErrorMsg {
	public final static String noConnectivity = "No connectivity! Please check your network connection.";
	public final static String alreadyDisconn = "Already disconnected!";
	public final static String alreadyConn = "Already connected!";
	public final static String disconnWhileDisconnecting = "Can not disconnect while a disconnection is in progress!";
	public final static String disconnWhileConnecting = "Can not disconnect while a connection is in progress!";
	public final static String connWhileConnecting = "Can not connect while a connection is in progress!";
	public final static String reconnIn5s = "Try to reconnect in 5s";
	public final static String notConn = "Not connected.";
	public final static String nullMessage = "Provided message is null";
	public final static String missingActor = "Actor is missing.";
	public final static String missingConvid = "Convid is missing.";
	public final static String missingStatus = "Status is missing.";
	public final static String timeout = "The response of message is time out!";
}
