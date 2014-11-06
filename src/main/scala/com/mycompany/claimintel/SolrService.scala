package com.mycompany.claimintel

import org.springframework.stereotype.Service
import org.apache.solr.client.solrj.impl.HttpSolrServer
import org.apache.solr.client.solrj.SolrQuery

import scala.collection.JavaConversions._

@Service
class SolrService {

  val server = new HttpSolrServer("http://localhost:8983/solr/collection1")
  
  def name = "SolrService"
    
  def populationFacets(): Map[String,Map[String,Long]] = {
    val query = new SolrQuery()
    query.setQuery("*:*")
    query.setFilterQueries("rec_type:B")
    query.setFacet(true)
    query.addFacetField("bene_sex", "bene_race")
    query.setRows(0)
    val resp = server.query(query)
    resp.getFacetFields().map(ff =>
      (ff.getName(), ff.getValues()
        .map(fv => (fv.getName(), fv.getCount())).toMap))
      .toMap
  }
}