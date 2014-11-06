package com.mycompany.claimintel

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.stereotype.Service
import org.springframework.ui.Model
import org.springframework.web.bind.ServletRequestUtils
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
class ClaimController @Autowired() (
    solr: SolrService, chart: ChartService){
  @RequestMapping(value = Array("/index.html"), 
      method = Array(RequestMethod.GET))
  def index(model: Model): String = {
    model.addAttribute("name", solr.name + "," + chart.name)
    "index"
  }

  @RequestMapping(value = Array("/population.html"), 
      method = Array(RequestMethod.GET))
  def test(req: HttpServletRequest, res: HttpServletResponse, 
      model: Model): String = {
    val foo = ServletRequestUtils.getRequiredStringParameter(req, "foo")
    model.addAttribute("name", foo + ":" + solr.name + "," + chart.name)
    "index"
  }
}