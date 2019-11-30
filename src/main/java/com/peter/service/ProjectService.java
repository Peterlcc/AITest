package com.peter.service;

import java.util.List;

import com.github.pagehelper.PageInfo;
import com.peter.bean.Project;

public interface ProjectService {
	void save(Project project);
	Project getProjectById(Integer id);
	Project getProjectByName(String name);
	List<Project> getAll();
	PageInfo<Project> getProjects(int pc, int ps);
	
	void delete(Integer id,String basePath);
}
