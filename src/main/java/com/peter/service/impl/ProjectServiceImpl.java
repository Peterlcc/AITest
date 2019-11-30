package com.peter.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.peter.bean.Project;
import com.peter.bean.ProjectExample;
import com.peter.bean.ProjectExample.Criteria;
import com.peter.mapper.ProjectMapper;
import com.peter.service.ProjectService;

@Service
public class ProjectServiceImpl implements ProjectService {

	private Log LOG = LogFactory.getLog(ProjectServiceImpl.class);
	
	@Autowired
	private ProjectMapper projectMapper;

	@Override
	public void save(Project project) {
		ProjectExample example = new ProjectExample();
		Criteria criteria = example.createCriteria();
		criteria.andNameEqualTo(project.getName());
		List<Project> list = projectMapper.selectByExample(example);
		if (list == null || list.size() == 0) {
			projectMapper.insertSelective(project);
		} else {
			projectMapper.updateByPrimaryKey(list.get(0));
		}
	}

	@Override
	public Project getProjectById(Integer id) {
		
		return projectMapper.selectByPrimaryKey(id);
	}

	@Override
	public Project getProjectByName(String name) {
		ProjectExample example = new ProjectExample();
		Criteria criteria = example.createCriteria();
		criteria.andNameEqualTo(name);
		List<Project> list = projectMapper.selectByExample(example);
		return (list==null||list.size()==0)?null:list.get(0);
	}

	@Override
	public List<Project> getAll() {
		
		return projectMapper.selectByExample(null);
	}

	@Override
	public PageInfo<Project> getProjects(int pc, int ps) {
		PageHelper.startPage(pc, ps);
		List<Project> projects = projectMapper.selectByExample(null);
		PageInfo<Project> pageInfo = new PageInfo<Project>(projects);
		return pageInfo;
	}

	@Override
	public void delete(Integer id,String basePath) {
		Project project = projectMapper.selectByPrimaryKey(id);
		String name = project.getName();
		if (basePath.endsWith("/")) {
			basePath=basePath.substring(0, basePath.length()-1);
		}
		try {
			FileUtils.deleteDirectory(new File(basePath+File.separator+"src"+File.separator+name));
			LOG.info(basePath+File.separator+"src"+File.separator+name+" is deleted");
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
		projectMapper.deleteByPrimaryKey(id);
	}

}
