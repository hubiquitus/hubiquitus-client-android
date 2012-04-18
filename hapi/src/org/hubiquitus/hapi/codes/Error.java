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

package org.hubiquitus.hapi.codes;

public enum Error {

    NO_ERROR,
    ALREADY_SUBSCRIBED,
    GET_SUBS_FAILED,
    ATTACH_FAILED,
    CONNECTION_FAILED,
    CONNECTION_TIMEOUT,
    AUTH_FAILED, 
    UNKNOWN_ERROR,
    NOT_SUBSCRIBED;
    
    /**
	 * Method to set the value of Error
	 * @return Error's value
	 */
	public static Error setValue(int value){
		Error error = null;
		if(value == 0) error = NO_ERROR;
		else if(value == 1) error = ALREADY_SUBSCRIBED;
		else if(value == 2) error = GET_SUBS_FAILED;
		else if(value == 3) error = ATTACH_FAILED;
		else if(value == 4) error = CONNECTION_FAILED;
		else if(value == 5) error = CONNECTION_TIMEOUT;
		else if(value == 6) error = AUTH_FAILED;
		else if(value == 7) error = UNKNOWN_ERROR;
		else if(value == 8) error = NOT_SUBSCRIBED;
		return error;
	}
    
}
