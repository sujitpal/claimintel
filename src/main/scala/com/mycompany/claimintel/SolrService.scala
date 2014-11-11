package com.mycompany.claimintel

import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.regex.Pattern

import scala.collection.JavaConversions._

import org.apache.solr.client.solrj.SolrQuery
import org.apache.solr.client.solrj.SolrQuery.ORDER
import org.apache.solr.client.solrj.impl.HttpSolrServer
import org.springframework.stereotype.Service

@Service
class SolrService {
  
  val server = new HttpSolrServer("http://localhost:8983/solr/collection1")
  
  def name = "SolrService"
    
  def populationFacets(filters: List[(String,String)]): 
      Map[String,Map[String,Long]] = {
    val query = new SolrQuery()
    query.setQuery("*:*")
    query.setFilterQueries("rec_type:B")
    groupFilters(filters).foreach(nv => 
      query.addFilterQuery(List(nv._1, nv._2).mkString(":")))
    query.setFacet(true)
    query.addFacetField("bene_sex", "bene_race", "sp_state", "bene_comorbs")
    query.setRows(0)
    val resp = server.query(query)
    resp.getFacetFields().map(ff =>
      (ff.getName(), ff.getValues()
        .map(fv => (fv.getName(), fv.getCount())).toMap))
      .toMap
  }

  def findDateOfBirth(youngest: Boolean): Date = {
    val query = new SolrQuery()
    query.setQuery("*:*")
    query.setFilterQueries("rec_type:B")
    query.setRows(1)
    query.setFields("bene_birth_date")
    query.setSortField("bene_birth_date", 
      if (youngest) ORDER.asc else ORDER.desc)
    val resp = server.query(query)
    resp.getResults()
      .head
      .getFieldValue("bene_birth_date")
      .asInstanceOf[Date]
  }
  
  def populationAgeFacet(filters: List[(String,String)]):
      Map[String,Long] = {
    // find top and bottom birth dates
    val youngestDob = findDateOfBirth(true)
    val oldestDob = findDateOfBirth(false)
    val now = new Date()
    val lb = round(yearsBetween(oldestDob, now), 10, false)
    val ub = round(yearsBetween(youngestDob, now), 10, true)
    val fqs = (lb to ub by 10)
      .sliding(2)
      .toList
      .map(v => intervalToQuery(v.mkString("-")))
    // now make the query
    val query = new SolrQuery()
    query.setQuery("*:*")
    query.setFilterQueries("rec_type:B")
    groupFilters(filters).foreach(fq => 
      query.addFilterQuery(List(fq._1, fq._2).mkString(":")))
    query.setRows(0)
    query.setFacet(true)
    fqs.foreach(fq => query.addFacetQuery(fq))
    val resp = server.query(query)
    resp.getFacetQuery().entrySet()
      .map(e => (queryToInterval(e.getKey()), e.getValue().toLong))
      .toMap
  }

  def groupFilters(filters: List[(String,String)]): 
      List[(String,String)] = {
    filters.groupBy(_._1)
      .map(group => (group._1, 
        "(" + group._2.map(_._2).mkString(" OR ") + ")"))
      .toList
  }
  
  def yearsBetween(d1: Date, d2: Date): Int = {
    val cal1 = Calendar.getInstance(Locale.getDefault())
    cal1.setTime(d1)
    val cal2 = Calendar.getInstance(Locale.getDefault())
    cal2.setTime(d2)
    cal2.get(Calendar.YEAR) - cal1.get(Calendar.YEAR)
  }
  
  def round(v: Int, nearest: Int, roundUp: Boolean): Int =
    if (roundUp) (Math.ceil(1.0D * v / nearest) * nearest).toInt
    else (Math.floor(1.0D * v / nearest) * nearest).toInt
    
  val IntervalQueryTemplate = 
    "bene_birth_date:[NOW-%dYEAR TO NOW-%dYEAR]"
  val IntervalQueryPattern = Pattern.compile(
    """bene_birth_date:\[NOW-(\d+)YEAR TO NOW-(\d+)YEAR\]""")
    
  def queryToInterval(q: String): String = {
    val m = IntervalQueryPattern.matcher(q)
    if (m.matches()) List(m.group(2), m.group(1)).mkString("-")
    else "0-0"
  }
  
  def intervalToQuery(interval: String): String = {
    val bounds = interval.split("-").map(_.toInt)
    IntervalQueryTemplate.format(bounds(1), bounds(0))
  }

  def isSelected(facetFilters: List[(String,String)], 
      klhs: String, krhs: String): Boolean = 
    facetFilters.filter(nvp => 
      (klhs.equals(nvp._1) && krhs.equals(nvp._2)))
      .size > 0
}