package com.mycompany.claimintel

import org.junit.Test
import java.util.Calendar
import java.util.Locale
import java.util.Date
import org.junit.Assert

class SolrServiceTest {

  val solrService = new SolrService()
  
//  @Test
//  def testPopulationFacets(): Unit = {
//    val facets = solrService.populationFacets(List.empty)
//    facets.keys.map(facetName => {
//      val counts = facets(facetName)
//      Console.println(facetName)
//      counts.keys.map(k => Console.println(k + ": " + counts(k)))
//    })
//  }
  
//  @Test
//  def testFindDateOfBirth(): Unit = {
//    val youngestDob = solrService.findDateOfBirth(true)
//    val oldestDob = solrService.findDateOfBirth(false)
//    val now = new Date()
//    val lb = solrService.round(solrService.yearsBetween(
//      oldestDob, now), 10, false)
//    val ub = solrService.round(solrService.yearsBetween(
//      youngestDob, now), 10, true)
//    Assert.assertEquals(9, (lb to ub by 10).toList.size)
//  }
  
//  @Test
//  def testQueryIntervalParsingFormatting(): Unit = {
//    val s = "bene_birth_date:[NOW-110YEAR TO NOW-100YEAR]"
//    val interval = solrService.queryToInterval(s)
//    Assert.assertEquals("100-110", interval)
//    Console.println("interval=" + interval)
//    val news = solrService.intervalToQuery(interval)
//    Console.println("new_s=" + news)
//    Assert.assertEquals(s, news)
//  }

//  @Test
//  def testPopulationAgeFacet(): Unit = {
//    val ageFacets = solrService.populationAgeFacet(List.empty)
//    Console.println(ageFacets)
//  }

//  @Test
//  def testCostAgeFacet(): Unit = {
//    val lb = solrService.findCostBoundary(true)
//    val ub = solrService.findCostBoundary(false)
//    Console.println("lb=" + lb + ", ub=" + ub)
//    solrService.costFacets(List.empty, "B")
//  }

  @Test
  def testCostStatistics(): Unit = {
    solrService.costStatistics(List.empty, "B")
  }
}