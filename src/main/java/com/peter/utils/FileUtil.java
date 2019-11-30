package com.peter.utils;

import java.io.File;
import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public class FileUtil {
	public static String saveSimple(String dir,MultipartFile file) {
		try {
			File dire = new File(dir);
			if (!dire.exists()) {
				dire.mkdirs();
			}
			String originalFilename = file.getOriginalFilename();
			File destFile = new File(dir+originalFilename);
			if (destFile.exists()) {
				return "文件已存在";
			}
			else {
				file.transferTo(destFile);
				return "上传成功";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "上传失败";
		}
	}

	public static String save(String basePath, MultipartFile[] dire) {
		if (basePath.endsWith("/")) {
			basePath=basePath.substring(0, basePath.length()-1);
		}
		for (MultipartFile file : dire) {
			String filePath=basePath+File.separator+file.getOriginalFilename();
			File destFile=new File(filePath);
			if (destFile.exists()) {
				return "文件已存在";
			}
			else {
				mkdirs(filePath);
				try {
					file.transferTo(destFile);
				} catch (IOException e) {
					e.printStackTrace();
					return filePath+"上传失败,上传终止";
				}
			}
		}
		return "上传成功";
	}

	private static void mkdirs(String filePath) {
		if (filePath.lastIndexOf("/")>0) {
			String direPath=filePath.substring(0, filePath.lastIndexOf("/"));
			File direFile = new File(direPath);
			if (!direFile.exists()) {
				direFile.mkdirs();
			}
		}
	}
}
