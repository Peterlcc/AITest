package com.peter.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.apache.commons.io.IOUtils;

public class MessageUtil {
	public Map<String, String> RunCommand(String command) {
		System.out.println("command:"+command);
		Process process = null;
		Runtime runtime = Runtime.getRuntime();
		StringBuilder stringBuilder = new StringBuilder();
		StringBuilder errorStringBuilder = new StringBuilder();
		BufferedReader reader = null;
		BufferedReader errorReader = null;
		Map<String, String> runMsg = new HashMap<>();
		
		try {
			long starttime = System.currentTimeMillis();
			process = runtime.exec(command);
			reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "utf-8"));
			errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream(), "utf-8"));
			String line = null;
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line+"\n");
			}
			String strerror=null;
			while((strerror=errorReader.readLine())!=null)
			{
				errorStringBuilder.append(strerror+"\n");
			}
			long endtime = System.currentTimeMillis();
			long runingTime = endtime - starttime;
			runMsg.put("runtime", runingTime + "ms");
		} catch (IOException e) {
			runMsg.put("exception", e.getMessage());
		} finally {
			try {
				if(reader!=null) reader.close();
				if(errorReader!=null) errorReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		runMsg.put("result", stringBuilder.toString());
		runMsg.put("error", errorStringBuilder.toString());
		return runMsg;
	}
	public Map<String, String> RunCommand(String[] commands) {
		//System.out.println("command:"+commands);
		Process process = null;
		Runtime runtime = Runtime.getRuntime();
		StringBuilder stringBuilder = new StringBuilder();
		StringBuilder errorStringBuilder = new StringBuilder();
		BufferedReader reader = null;
		BufferedReader errorReader = null;
		Map<String, String> runMsg = new HashMap<>();
		
		try {
			long starttime = System.currentTimeMillis();
			process = runtime.exec(commands);
			long endtime = System.currentTimeMillis();
			long runingTime = endtime - starttime;
			runMsg.put("runtime", runingTime + "ms");
			List<String> inputLines = IOUtils.readLines(process.getInputStream(), "utf-8");
			for(String line:inputLines) stringBuilder.append(line);
			List<String> errorLines = IOUtils.readLines(process.getErrorStream(), "utf-8");
			for(String line:errorLines) errorStringBuilder.append(line);
		} catch (IOException e) {
			runMsg.put("exception", e.getMessage());
		} finally {
			try {
				if(reader!=null) reader.close();
				if(errorReader!=null) errorReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		runMsg.put("result", stringBuilder.toString());
		runMsg.put("error", errorStringBuilder.toString());
		return runMsg;
	}
}
