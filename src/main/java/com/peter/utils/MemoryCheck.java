package com.peter.utils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

public class MemoryCheck extends Thread {

	private int maxUsed = 0;
	private int minUsed = 0;
	private boolean finish = false;
	private String processName = null;

	public MemoryCheck(String processName) {
		this.processName = processName;
	}

	public int getMaxUsed() {
		return maxUsed;
	}

	public void setMaxUsed(int maxUsed) {
		this.maxUsed = maxUsed;
	}

	public boolean isFinish() {
		return finish;
	}

	public void setFinish(boolean finish) {
		this.finish = finish;
	}

	@Override
	public void run() {
		String cmd = "ps -e -o 'pid,comm,rss,vsz' | grep " + processName + "|awk '{print $3}'";
		String[] commands=new String[] { "/bin/bash", "-c", cmd };
		while (!finish) {
			Process process = null;
			Runtime runtime = Runtime.getRuntime();
			StringBuilder stringBuilder = new StringBuilder();
			try {
				process = runtime.exec(commands);
				List<String> inputLines = IOUtils.readLines(process.getInputStream(), "utf-8");
				for (String line : inputLines)
					stringBuilder.append(line);
			} catch (IOException e) {
				e.printStackTrace();
			}
			String resultSpace=stringBuilder.toString();
			maxUsed = Math.max(maxUsed, resultSpace.equals("") ? 0 : Integer.parseInt(resultSpace));
			minUsed = Math.min(minUsed, resultSpace.equals("") ? 0 : Integer.parseInt(resultSpace));
		}
	}

}
