package com.mycompany.claimintel

import java.net.URLEncoder
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import scala.Array.canBuildFrom
import scala.collection.JavaConversions._
import scala.util.parsing.json.JSON
import scala.util.parsing.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.stereotype.Service
import org.springframework.ui.Model
import org.springframework.web.bind.ServletRequestUtils
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import com.mycompany.claimintel.dtos.CategoryStats
import com.mycompany.claimintel.dtos.PopDistrib
import scala.util.parsing.json.JSONArray

@Controller
class ClaimController @Autowired() (
    solrService: SolrService, 
    chartService: ChartService){
  
  @RequestMapping(value = Array("/index.html"), 
      method = Array(RequestMethod.GET))
  def index(req: HttpServletRequest, res: HttpServletResponse, 
      model: Model): String = population(req, res, model)

  @RequestMapping(value = Array("/population.html"), 
      method = Array(RequestMethod.GET))
  def population(req: HttpServletRequest, res: HttpServletResponse, 
      model: Model): String = {
    val popFilters = buildPopulationFilter(req)
    val popFacets = solrService.populationFacets(popFilters)
    popFacets.keys.foreach(pfname => {
      val pfdata = popFacets(pfname)
      val popDistrib = new PopDistrib()
      popDistrib.setEncodedData(URLEncoder.encode(
        JSONObject(pfdata).toString(), "UTF-8"))
      val total = pfdata.values.foldLeft(0L)(_ + _)
      popDistrib.setStats(seqAsJavaList(pfdata.map(entry => {
          val cstats = new CategoryStats()
          cstats.setName(entry._1)
          cstats.setCount(entry._2)
          cstats.setPcount(1.0D * entry._2 / total)
          cstats.setSelected(solrService.isSelected(
            popFilters, pfname, entry._1))
          cstats
        })
        .toList
        .sortWith((a,b) => a.getName() < b.getName())))
      popDistrib.setTotal(total)
      model.addAttribute(pfname, popDistrib)
    })
    
    // the age facet is the only one that we will treat as
    // a continuous variable, so we distinguish by name
    val agedata = solrService.populationAgeFacet(popFilters)
    val ageDistrib = new PopDistrib()
    ageDistrib.setEncodedData(URLEncoder.encode(
      JSONObject(agedata).toString(), "UTF-8"))
    val ageTotal = agedata.values.foldLeft(0L)(_ + _)
    ageDistrib.setStats(seqAsJavaList(agedata.map(entry => {
      val cstats = new CategoryStats()
      cstats.setName(entry._1)
      cstats.setCount(entry._2)
      cstats.setPcount(1.0D * entry._2 / ageTotal)
      val query = solrService.intervalToQuery(entry._1).split(":")
      cstats.setSelected(solrService.isSelected(
        popFilters, query(0), query(1)))
      cstats
    })
    .toList
    .sortWith((a,b) => a.getName().split("-")(0).toInt < 
      b.getName().split("-")(0).toInt)))
    ageDistrib.setTotal(ageTotal)
    model.addAttribute("bene_age", ageDistrib)
    
    "index"
  }
  
  def buildPopulationFilter(req: HttpServletRequest): 
      List[(String,String)] = {
    val shouldFilter = ServletRequestUtils.getBooleanParameter(
      req, "filter", false)
    if (! shouldFilter) List.empty[(String,String)]
    else {
      val pmap = req.getParameterMap()
      req.getParameterNames()
        .filter(pname => (pname.startsWith("bene_") || 
            pname.startsWith("sp_")))
        .map(pname => if (pname.equals("bene_age"))
          pmap(pname).map(pval => {
            val query = solrService.intervalToQuery(pval).split(":")
            (query(0), query(1))
          })
          else pmap(pname).map(pval => (pname, pval)))
        .flatten
        .toList
    }
  }
  
  @RequestMapping(value = Array("/chart.html"),
      method = Array(RequestMethod.GET))
  def chart(req: HttpServletRequest, res: HttpServletResponse, 
      model: Model): String = {
    val chartType = ServletRequestUtils.getRequiredStringParameter(req, "type")
    val data = JSON.parseFull(
      ServletRequestUtils.getRequiredStringParameter(req, "data"))
      .get.asInstanceOf[Map[String,Double]]
    val title = ServletRequestUtils.getStringParameter(req, "title", "")
    val xtitle = ServletRequestUtils.getStringParameter(req, "xtitle", "")
    val ytitle = ServletRequestUtils.getStringParameter(req, "ytitle", "")
    val width = ServletRequestUtils.getIntParameter(req, "width", 500)
    val height = ServletRequestUtils.getIntParameter(req, "height", 300)
    chartType match {
      case "bar" => chartService.bar(
        data, title, xtitle, ytitle, true, width, height, 
        res.getOutputStream())
 
    }
    null
  }
}
