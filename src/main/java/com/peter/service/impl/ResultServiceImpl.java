package com.peter.service.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.peter.bean.Result;
import com.peter.bean.ResultExample;
import com.peter.bean.ResultExample.Criteria;
import com.peter.controller.ResultController;
import com.peter.mapper.ResultMapper;
import com.peter.service.ResultService;

@Service
public class ResultServiceImpl implements ResultService{
	private Log LOG=LogFactory.getLog(ResultController.class);
	@Autowired
	private ResultMapper resultMapper;
	@Override
	public List<Result> getAll() {
		return resultMapper.selectByExample(null);
	}
	@Override
	public PageInfo<Result> getResults(int pc, int ps) {
		PageHelper.startPage(pc, ps);
		List<Result> list = resultMapper.selectByExample(null);
		PageInfo<Result> pageInfo = new PageInfo<Result>(list);
		LOG.debug("result query list,size="+list.size());
		return pageInfo;
	}
	@Override
	public void save(Result result) {
		ResultExample resultExample = new ResultExample();
		Criteria criteria = resultExample.createCriteria();
		criteria.andProjectIdEqualTo(result.getProjectId());
		List<Result> list = resultMapper.selectByExample(resultExample);
		if (list == null || list.size() == 0) {
			resultMapper.insertSelective(result);
		}else {
			resultMapper.updateByPrimaryKey(list.get(0));
		}
	}
	@Override
	public void delete(Integer id) {
		resultMapper.deleteByPrimaryKey(id);
	}

}
