package com.peter.service;

import java.util.List;

import com.github.pagehelper.PageInfo;
import com.peter.bean.Result;

public interface ResultService {
	void save(Result result);
	List<Result> getAll();
	PageInfo<Result> getResults(int pc, int ps);
	void delete(Integer id);
}
