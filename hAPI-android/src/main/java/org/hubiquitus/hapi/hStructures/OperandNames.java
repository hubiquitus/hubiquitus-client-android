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

package org.hubiquitus.hapi.hStructures;
/**
 * @version v0.5
 * Enumeration of available operand names for hCondition.
 */
public enum OperandNames {
	/**
	 * eq : equals. Only apply on hValue.
	 */
	EQ("eq"),
	/**
	 * ne : not equals. Only apply on hValue. 
	 */
	NE("ne"),
	/**
	 * gt : greater than. Only apply on hValue.
	 */
	GT("gt"),
	/**
	 * gte : greater than or equals. Only apply on hValue.
	 */
	GTE("gte"),
	/**
	 * lt : less than. Only apply on hValue.
	 */
	LT("lt"),
	/**
	 * lte : less than or equals. Only apply on hValue.
	 */
	LTE("lte"),
	/**
	 * in : The attribute must be equals to one of the values. Only apply on hArrayOfValue.
	 */
	IN("in"),
	/**
	 * nin : The attribute must be different with all the values. Only apply on hArrayOfValue.
	 */
	NIN("nin"),
	/**
	 * and : All the conditions must be true. Only apply on array of hCondition.
	 */
	AND("and"),
	/**
	 * or : One of the conditions must be true. Only apply on array of hCondition.
	 */
	OR("or"),
	/**
	 * nor : All the conditions must be false. Only apply on array of hCondition.
	 */
	NOR("nor"),
	/**
	 * not : The condition must be false. Only apply on hCondition.
	 */
	NOT("not");
	
	private String value;
	
	private OperandNames(final String value){
		this.value = value;
	}
	
	@Override
	public String toString(){
		return this.value;
	}
}
