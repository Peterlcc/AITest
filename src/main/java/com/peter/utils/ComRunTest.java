package com.peter.utils;

import com.peter.bean.Result;
import org.apache.commons.io.IOUtils;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class ComRunTest {
    private static String basePath="catkin_ws";
    public static void main(String[] args) {
        String compileCommand="source /opt/ros/kinetic/setup.bash && catkin_make";
        String launchParam="";
        //编译
        testCommand(CmdEnv.bash.getEnv(),compileCommand);
        String runCommand = "source /opt/ros/kinetic/setup.bash && source /home/peter/tmp/catkin_ws/devel/setup.bash " +
                "&& roslaunch " + launchParam;
        //运行
        testCommand(CmdEnv.bash.getEnv(), runCommand);
    }
    private static void testCommand(String cmdEnv, String command) {
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
            List<String> inputLines = IOUtils.readLines(process.getInputStream(), "utf-8");
            for (String line : inputLines)
                stringBuilder.append(line);
            List<String> errorLines = IOUtils.readLines(process.getErrorStream(), "utf-8");
            for (String line : errorLines)
                errorStringBuilder.append(line);
        } catch (IOException e) {
            succeed=false;
            e.printStackTrace();
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
        System.out.println("cmd:"+command+",output:[正常输出："+stringBuilder.toString()+System.lineSeparator()+"错误输出："+errorStringBuilder.toString()+"]");
    }
}
