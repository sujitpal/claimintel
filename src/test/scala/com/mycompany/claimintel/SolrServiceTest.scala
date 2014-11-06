package com.mycompany.claimintel

import org.junit.Test

class SolrServiceTest {

  val solrService = new SolrService()
  
  @Test
  def testPopulationFacets(): Unit = {
    val facets = solrService.populationFacets
    facets.keys.map(facetName => {
      val counts = facets(facetName)
      Console.println(facetName)
      counts.keys.map(k => Console.println(k + ": " + counts(k)))
    })
  }
}