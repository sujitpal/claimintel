package com.mycompany.claimintel

import org.springframework.context.annotation.ComponentScan
import org.springframework.web.servlet.view.InternalResourceViewResolver
import org.springframework.web.servlet.view.JstlView
import org.springframework.context.annotation.Bean

@ComponentScan(basePackages = Array("com.mycompany.claimintel"))
class Config {

  @Bean
  def viewResolver = {
    val viewResolver = new InternalResourceViewResolver()
    viewResolver.setViewClass(classOf[JstlView])
    viewResolver.setPrefix("/WEB-INF/views/")
    viewResolver.setSuffix(".jsp")
    viewResolver
  }
}