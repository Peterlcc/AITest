package com.peter.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.peter.bean.Project;
import com.peter.bean.Result;
import com.peter.mapper.ResultMapper;
import com.peter.service.CheckService;
import com.peter.utils.CmdEnv;
import com.peter.utils.MemoryCheck;

@Service
public class CheckServiceImpl implements CheckService{
	private final Log LOG =LogFactory.getLog(CheckServiceImpl.class);
	
	@Value("${growingai.upload.path}")
	private String basePath;
	@Autowired
	private ResultMapper resultMapper;
	
	@Override
	@Async
	public void runCodeTest(Project project) {
		LOG.info("start code test");
		Result result = simpleTest(project);
		resultMapper.insertSelective(result);
		LOG.info("end code test");
	}
	public Result simpleTest(Project project) {
		Result result=new Result();
		result.setId(null);
		result.setProjectId(project.getId());
		
		String compileCommand="source /opt/ros/kinetic/setup.bash && catkin_make";
		testCommand(CmdEnv.bash.getEnv(),compileCommand, result);
		if (result.getSuccess()) {
			String runCommand="source /opt/ros/kinetic/setup.bash && source /home/peter/tmp/catkin_ws/devel/setup.bash && roslaunch "+project.getPath();
			MemoryCheck memoryCheck = new MemoryCheck(project.getName());
			memoryCheck.start();
			testCommand(CmdEnv.bash.getEnv(),runCommand, result);
			memoryCheck.setFinish(true);
			if (result.getSuccess()) {
				result.setSpace((long) memoryCheck.getMaxUsed());
			}else {
				//运行出错
				result.setSpace((long) memoryCheck.getMaxUsed());
				result.setOutput("运行出错，"+result.getOutput());
			}
		} else {
			//编译阶段出错
			result.setOutput("编译出错，"+result.getOutput());
		}
		return result;
	}
	private void testCommand(String cmdEnv,String command,Result result) {
		Process process = null;
		Runtime runtime = Runtime.getRuntime();
		StringBuilder stringBuilder = new StringBuilder();
		StringBuilder errorStringBuilder = new StringBuilder();
		BufferedReader reader = null;
		BufferedReader errorReader = null;
		boolean succeed=false;
		try {
			long starttime = System.currentTimeMillis();
			process = runtime.exec(new String[] {cmdEnv,"-c",command},null,new File(basePath));
			try {
				process.waitFor();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			long endtime = System.currentTimeMillis();
			long runingTime = endtime - starttime;
			result.setTime((double)runingTime);
			LOG.info("run time="+runingTime+"ms");
			List<String> inputLines = IOUtils.readLines(process.getInputStream(), "utf-8");
			for (String line : inputLines)
				stringBuilder.append(line);
			List<String> errorLines = IOUtils.readLines(process.getErrorStream(), "utf-8");
			for (String line : errorLines)
				errorStringBuilder.append(line);
		} catch (IOException e) {
			succeed=false;
			e.printStackTrace();
			LOG.error("运行命令出错:"+e.getMessage());
		} finally {
			try {
				if (reader != null)
					reader.close();
				if (errorReader != null)
					errorReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if ((!succeed)&&(!StringUtils.isEmpty(errorStringBuilder.toString()))) {
			result.setSuccess(false);
			result.setOutput(stringBuilder.toString()+System.lineSeparator()+"-----------"+System.lineSeparator()+errorStringBuilder.toString());
		}
		else {
			result.setSuccess(true);
			result.setOutput(stringBuilder.toString());
		}
		LOG.info("cmd:"+command+",output:[正常输出："+stringBuilder.toString()+System.lineSeparator()+"错误输出："+errorStringBuilder.toString()+"]");
	}
}
