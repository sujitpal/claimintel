package com.mycompany.claimintel

import java.util.Date

import org.junit.Assert
import org.junit.Test
import org.springframework.stereotype.Service

class SolrServiceTest {

  val solrService = new SolrService()
  
  @Test
  def testPopulationFacets(): Unit = {
    val facets = solrService.populationFacets(List.empty)
    facets.keys.map(facetName => {
      val counts = facets(facetName)
      Console.println(facetName)
      counts.keys.map(k => Console.println(k + ": " + counts(k)))
    })
  }
  
  @Test
  def testFindDateOfBirth(): Unit = {
    val youngestDob = solrService.findDateOfBirth(true)
    val oldestDob = solrService.findDateOfBirth(false)
    val now = new Date()
    val lb = solrService.round(solrService.yearsBetween(
      oldestDob, now), 10, false)
    val ub = solrService.round(solrService.yearsBetween(
      youngestDob, now), 10, true)
    Assert.assertEquals(9, (lb to ub by 10).toList.size)
  }
  
  @Test
  def testQueryIntervalParsingFormatting(): Unit = {
    val s = "bene_birth_date:[NOW-110YEAR TO NOW-100YEAR]"
    val interval = solrService.queryToInterval(s)
    Assert.assertEquals("100-110", interval)
    Console.println("interval=" + interval)
    val news = solrService.intervalToQuery(interval)
    Console.println("new_s=" + news)
    Assert.assertEquals(s, news)
  }

  @Test
  def testPopulationAgeFacet(): Unit = {
    val ageFacets = solrService.populationAgeFacet(List.empty)
    Console.println(ageFacets)
  }

  @Test
  def testCostAgeFacet(): Unit = {
    val lb = solrService.findCostBoundary(true, 0.0F)
    val ub = solrService.findCostBoundary(false, 0.0F)
    Console.println("lb=" + lb + ", ub=" + ub)
    solrService.costFacets(List.empty, "B", 0.0F, 0.0F)
  }

  @Test
  def testCostStatistics(): Unit = {
    solrService.costStatistics(List.empty, "B")
  }
  
  @Test
  def testPatients(): Unit = {
    val presults = solrService.patients(List.empty, 0)
    val patients = presults._2
    patients.foreach(p => {
      Console.println("[%s] %d-year old %s %s from %s (%d transactions)"
        .format(p.getDesynpufId(), p.getAge(), p.getRace(), 
                p.getSex(), p.getState(), p.getNumTransactions()))
      Console.println("Part A: %dmo., Part B: %dmo., HMO: %dmo., Part D: %dmo."
        .format(p.getPartACoverageMos(), p.getPartBCoverageMos(),
                p.getHmoCoverageMos(), p.getPartDCoverageMos()))
      Console.println()
    })
  }

  @Test
  def testTimeline(): Unit = {
    val tresults = solrService.timeline("5395C6DAF4445846")
    val patient = tresults._1
    val transactions = tresults._2
    Assert.assertEquals(transactions.size, patient.getNumTransactions())
  }

  @Test
  def testCorrelation(): Unit = {
    val corrDataWithoutAge = solrService.correlation(List.empty, "bene_comorbs", "sp_state")
    Console.println("dataWithoutAge=" + corrDataWithoutAge)
    Assert.assertTrue(corrDataWithoutAge.contains("Cancer::CA"))
    val corrDataWithAge = solrService.correlation(List.empty, "bene_age", "bene_sex")
    Console.println("dataWithAge=" + corrDataWithAge)
    Assert.assertTrue(corrDataWithAge.contains("90-100::Female"))
  }
}