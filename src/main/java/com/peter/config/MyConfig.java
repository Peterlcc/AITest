package com.peter.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.Servlet;
import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;

@Configuration
public class MyConfig {

    @Bean
    public WebMvcConfigurer webMvcConfigurer(){
        WebMvcConfigurer webMvcConfigurer = new WebMvcConfigurer(){
            @Override
            public void addViewControllers(ViewControllerRegistry registry) {
                registry.addViewController("/").setViewName("login");
                registry.addViewController("/index").setViewName("login");
                registry.addViewController("/index.html").setViewName("login");
                registry.addViewController("/main.html").setViewName("dashboard");
            }
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
            	registry.addInterceptor(new LoginHandlerInterceptor()).addPathPatterns("/**")
            	.excludePathPatterns("/","/index","/druid/*","*.ico","/asserts/**","/index.html","/login/user","/webjars/**");
            }
        };
        return webMvcConfigurer;
    }
    //国际化处理
    @Bean
    public LocaleResolver localeResolver(){
        return new MyLocaleResolver();
    }
    
    //配置文件加载
  	@Bean
  	@ConfigurationProperties(prefix = "spring.datasource")
  	public DataSource dataSource() {
  		return new DruidDataSource();
  	}
  	
  	//Druid监控Servlet
  	@Bean
  	public ServletRegistrationBean<Servlet> statViewServlet(){
  		ServletRegistrationBean<Servlet> bean = 
  				new ServletRegistrationBean<Servlet>(
  						new StatViewServlet(), "/druid/*");
  		Map<String, String> initParameters =new HashMap<String, String>();
  		initParameters.put("loginUsername","admin");
  		initParameters.put("loginPassword","cheng0526");
  		initParameters.put("allow","");//默认或者不写允许所有
  		initParameters.put("deny","192.168.1.2");
  		
  		bean.setInitParameters(initParameters);
  		return bean;
  	}
  	
  	//监控Filter
  	@Bean
  	public FilterRegistrationBean<Filter> addBean(){
  		FilterRegistrationBean<Filter> bean = new FilterRegistrationBean<Filter>();
  		bean.setFilter(new WebStatFilter());
  		Map<String, String> initParameters =new HashMap<String, String>();
  		initParameters.put("exclusions","*.js,/static/*,*.css.*.img,*.svg,*.ico,/druid/*");
  		bean.setInitParameters(initParameters);
  		bean.setUrlPatterns(Arrays.asList("/*"));
  		return bean;
  	}
}
