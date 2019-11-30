package com.peter.controller;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.github.pagehelper.PageInfo;
import com.peter.bean.Project;
import com.peter.service.CheckService;
import com.peter.service.ProjectService;
import com.peter.utils.FileUtil;
import com.peter.utils.URLUtil;

@Controller
@RequestMapping("projects")
public class ProjectController {
	private Log LOG = LogFactory.getLog(ProjectController.class);

	@Value("${growingai.upload.path}")
	private String basePath;

	@Autowired
	private HttpServletRequest request;

	@Autowired
	private ProjectService projectService;

	@Autowired
	private CheckService checkService;

	@RequestMapping("project")
	public String toUploadPage() {
		return "project/add";
	}

	@GetMapping("projects")
	public String show(Model model) {
		String pcstr = request.getParameter("pc");
		if (pcstr == null || pcstr.equals("")) {
			pcstr = "1";
		}
		int pc = Integer.parseInt(pcstr);
		String psstr = request.getParameter("ps");
		if (psstr == null || psstr.equals("")) {
			psstr = "10";
		}
		int ps = Integer.parseInt(psstr);
		PageInfo<Project> pageInfo = projectService.getProjects(pc, ps);
		model.addAttribute("pageInfo", pageInfo);
		List<String> fieldNames = new ArrayList<String>();
		Field[] fields = Project.class.getDeclaredFields();
		for (Field field : fields) {
			fieldNames.add(field.getName());
		}
		model.addAttribute("fieldNames", fieldNames);
		model.addAttribute("requestPath", URLUtil.getUrl(request));
		return "project/list";
	}

	@PostMapping("project")
	public String addProject(@RequestParam("dire") MultipartFile[] dire) {
		if (dire == null || dire.length == 0) {
			return "***";
		}
		StringBuilder uploadFiles = new StringBuilder();
		uploadFiles.append("upload files:[");
		for (MultipartFile file : dire) {
			uploadFiles.append(file.getOriginalFilename());
		}
		uploadFiles.append("]");
		LOG.info(uploadFiles.toString());
		String msg = FileUtil.save(basePath + File.separator + "src", dire);
		if (!msg.equals("上传成功")) {
			throw new RuntimeException(msg);
		}
		// String pname = dire[0].getOriginalFilename().substring(0,
		// dire[0].getOriginalFilename().lastIndexOf("/"));
		String launchFileName = null;
		for (MultipartFile multipartFile : dire) {
			if (multipartFile.getOriginalFilename().endsWith(".launch")) {
				launchFileName = multipartFile.getOriginalFilename()
						.substring(multipartFile.getOriginalFilename().lastIndexOf("/") + 1);
				break;
			}
		}
		String pname = dire[0].getOriginalFilename().substring(0, dire[0].getOriginalFilename().indexOf("/"));
		Project project = new Project();
		project.setId(null);
		project.setCreateTime(new Date());
		project.setPath(pname + " " + launchFileName);
		project.setName(pname);

		projectService.save(project);
		// System.out.println(project);
		LOG.info("save project:" + project.toString());

		return "redirect:/projects/projects";
	}

	@DeleteMapping("project/{id}")
	public String deleteProject(@PathVariable("id") Integer id) {
		projectService.delete(id,basePath);
		LOG.info("project " + id + " deleted");
		return "redirect:/projects/projects";
	}

	@GetMapping("test/{id}")
	public String test(@PathVariable("id") Integer id, RedirectAttributes model) {
		Project project = projectService.getProjectById(id);
		try {
			checkService.runCodeTest(project);
			model.addFlashAttribute("msg", "正在测试");
			return "redirect:/results/results";
		} catch (RuntimeException e) {
			model.addFlashAttribute("msg", "系统错误，请联系管理员！");
			return "redirect:/projects/projects";
		}
	}
}
