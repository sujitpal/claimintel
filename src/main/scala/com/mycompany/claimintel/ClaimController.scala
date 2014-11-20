package com.mycompany.claimintel

import java.net.URLEncoder
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
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
import com.mycompany.claimintel.dtos.PopFilter
import com.mycompany.claimintel.dtos.ContinuousStats

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
    model.addAttribute("filters", popFilters.map(nv => 
      nv._1 + "=" + URLEncoder.encode(nv._2, "UTF-8"))
      .mkString("&"))
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
    
    "population"
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
    val sortByCount = ServletRequestUtils
      .getStringParameter(req, "csort", "normal") match {
      case "normal" => ChartSorting.Normal
      case "range" => ChartSorting.StrRange
      case "int" => ChartSorting.IntRange
      case "count" => ChartSorting.Count
    }
    chartType match {
      case "bar" => chartService.bar(
        data, title, xtitle, ytitle, true, width, height, 
        sortByCount, res.getOutputStream())
    }
    null
  }

  @RequestMapping(value = Array("/codes.html"),
      method = Array(RequestMethod.GET))
  def codes(req: HttpServletRequest, res: HttpServletResponse, 
      model: Model): String = {
    val popFilters = buildPopulationFilter(req)
    model.addAttribute("filters", 
      seqAsJavaList(popFilters.map(pf => new PopFilter(pf._1, pf._2))))
    model.addAttribute("filterStr", 
      popFilters.map(f => 
        f._1 + "=" + URLEncoder.encode(f._2, "UTF-8"))
        .mkString("&"))
    val ttype = ServletRequestUtils.getStringParameter(req, "ttype", "B")
    model.addAttribute("ttype", ttype)
    val codeFacets = solrService.codeFacets(popFilters, ttype)
    codeFacets.keys.foreach(cfname => {
      val cfdata = codeFacets(cfname)
      val popDistrib = new PopDistrib()
      popDistrib.setEncodedData(URLEncoder.encode(
        JSONObject(cfdata).toString(), "UTF-8"))
      val total = cfdata.values.foldLeft(0L)(_ + _)
      popDistrib.setStats(seqAsJavaList(cfdata.map(entry => {
          val cstats = new CategoryStats()
          cstats.setName(entry._1)
          cstats.setCount(entry._2)
          cstats.setPcount(1.0D * entry._2 / total)
          cstats.setSelected(solrService.isSelected(
            popFilters, cfname, entry._1))
          cstats
        })
        .toList
        .sortWith((a,b) => a.getCount() > b.getCount())))
      popDistrib.setTotal(total)
      model.addAttribute(cfname, popDistrib)
    })
    "codes"
  }

  @RequestMapping(value = Array("/costs.html"),
      method = Array(RequestMethod.GET))
  def costs(req: HttpServletRequest, res: HttpServletResponse, 
      model: Model): String = {
    val popFilters = buildPopulationFilter(req)
    model.addAttribute("filters", 
      seqAsJavaList(popFilters.map(pf => new PopFilter(pf._1, pf._2))))
    model.addAttribute("filterStr", 
      popFilters.map(f => 
        f._1 + "=" + URLEncoder.encode(f._2, "UTF-8"))
        .mkString("&"))
    val ttype = ServletRequestUtils.getStringParameter(req, "ttype", "B")
    model.addAttribute("ttype", ttype)
    val costdata = solrService.costFacets(popFilters, ttype)
    val costDistrib = new PopDistrib()
    costDistrib.setEncodedData(URLEncoder.encode(
      JSONObject(costdata).toString(), "UTF-8"))
    val costTotal = costdata.values.foldLeft(0L)(_ + _)
    costDistrib.setStats(seqAsJavaList(costdata.map(entry => {
      val cstats = new CategoryStats()
      cstats.setName(entry._1)
      cstats.setCount(entry._2)
      cstats.setPcount(1.0D * entry._2 / costTotal)
      val query = solrService.costToQuery(entry._1.toInt).split(":")
      cstats.setSelected(solrService.isSelected(
        popFilters, query(0), query(1)))
      cstats
    })
    .toList
    .sortWith((a,b) => a.getName().split("-")(0).toInt < 
      b.getName().split("-")(0).toInt)))
    costDistrib.setTotal(costTotal)
    model.addAttribute("clm_pmt_amt", costDistrib)
    
    val costStats = solrService.costStatistics(popFilters, ttype)
    model.addAttribute("clm_pmt_amt_stats", 
      new ContinuousStats(costStats))
      
    "costs"
  }
  
  def buildPopulationFilter(req: HttpServletRequest): 
      List[(String,String)] = {
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
