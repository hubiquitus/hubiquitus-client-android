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
