package com.peter.utils;

public class MemoryUtil {
	private static Runtime runtime = null;
	static {
		runtime = Runtime.getRuntime();
	}

	public static double getMemory() {
		long totalMemory = runtime.totalMemory();
		long freeMemory = runtime.freeMemory();
		long used = totalMemory - freeMemory;

		return 0.0;
	}
}
