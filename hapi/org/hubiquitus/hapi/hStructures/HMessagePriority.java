package org.hubiquitus.hapi.hStructures;

public enum HMessagePriority {
	TRACE(0),
	INFO(1),
	WARNING(2),
	ALERT(3),
	CRITICAL(4),
	PANIC(5);
	
	private int value;
	
	private HMessagePriority(int value) {
		this.value = value;
	}
	
	/**
	 * @return int equivalent.
	 */
	public int value() {
		return value;
	}
	
	/**
	 * Get constant for value
	 * @param value
	 * @return
	 */
	public static HMessagePriority constant(int value) {
		HMessagePriority [] _values = HMessagePriority.values();
		return _values[value];
	}
}
