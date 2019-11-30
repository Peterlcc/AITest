package com.peter.controller;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.github.pagehelper.PageInfo;
import com.peter.bean.Result;
import com.peter.service.ResultService;
import com.peter.utils.URLUtil;

@Controller
@RequestMapping("results")
public class ResultController {
	private Log LOG=LogFactory.getLog(ResultController.class);
	@Autowired
	private HttpServletRequest request;
	
	@Autowired ResultService resultService;
	@GetMapping("results")
	public String results(Model model) {
		String pcstr = request.getParameter("pc");
		if (pcstr==null||pcstr.equals("")) {
			pcstr="1";
		}
		int pc=Integer.parseInt(pcstr);
		String psstr = request.getParameter("ps");
		if (psstr==null||psstr.equals("")) {
			psstr="10";
		}
		int ps=Integer.parseInt(psstr);
		model.addAttribute("pageInfo",resultService.getResults(pc, ps));
		List<String> fieldNames=new ArrayList<String>();
		Field[] fields = Result.class.getDeclaredFields();
		for (Field field : fields) {
			fieldNames.add(field.getName());
		}
		model.addAttribute("fieldNames", fieldNames);
		model.addAttribute("requestPath", URLUtil.getUrl(request));
		return "result/list";
	}
	@DeleteMapping("result/{id}")
	public String deleteResult(@PathVariable("id") Integer id) {
		resultService.delete(id);
		return "redirect:/results/results";
	}
}
