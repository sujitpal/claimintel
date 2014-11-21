package com.mycompany.claimintel.loaders

import java.util.Calendar
import java.util.Date
import java.util.Locale

import scala.collection.JavaConversions._

import org.apache.solr.client.solrj.SolrQuery
import org.apache.solr.client.solrj.impl.ConcurrentUpdateSolrServer
import org.apache.solr.client.solrj.impl.HttpSolrServer
import org.apache.solr.common.SolrInputDocument

object MortalityUpdater extends App {

  val SolrUrl = "http://localhost:8983/solr"
  
  val updater = new MortalityUpdater(SolrUrl)
  updater.update()
  
}

class MortalityUpdater(solrUrl: String) {

  val qserver = new HttpSolrServer(solrUrl)
  val userver = new ConcurrentUpdateSolrServer(solrUrl, 1000, 4)
  
  def update(): Unit = {
    // find how many records to update
    val nquery = new SolrQuery()
    nquery.setQuery("*:*")
    nquery.setFilterQueries("rec_type:B", 
      "bene_death_date:[* TO *]")
    nquery.setRows(0)
    val nresp = qserver.query(nquery)
    val numfound = nresp.getResults().getNumFound()
    // rebuild the query, paging through the resultset
    // 1000 records at a time
    val rowsPerPage = 1000
    val numpages = (numfound / rowsPerPage) + 1
    (0L until numpages).foreach(pagenum => {
      if (pagenum > 0 && pagenum % 10 == 0) userver.commit()
      else {}
      val query = new SolrQuery()
      query.setQuery("*:*")
      query.setFilterQueries("rec_type:B", 
        "bene_death_date:[* TO *]")
      query.setStart(pagenum.toInt * rowsPerPage)
      query.setRows(rowsPerPage)
      query.setFields("id", "bene_birth_date", "bene_death_date")
      val resp = qserver.query(query)
      resp.getResults().map(result => {
        val id = result.getFieldValue("id").asInstanceOf[String]
        val beneBirthDate = result.getFieldValue("bene_birth_date").asInstanceOf[Date]
        val beneDeathDate = result.getFieldValue("bene_death_date").asInstanceOf[Date]
        val calBirth = Calendar.getInstance(Locale.getDefault())
        calBirth.setTime(beneBirthDate)
        val calDeath = Calendar.getInstance(Locale.getDefault())
        calDeath.setTime(beneDeathDate)
        val ageAtDeath = calDeath.get(Calendar.YEAR) - calBirth.get(Calendar.YEAR)
        Console.println("%s\t%d".format(id, ageAtDeath))
        updateRecord(id, ageAtDeath)
      })
      query.clear()
    })
    userver.commit()
    userver.shutdown()
    qserver.shutdown()
  }
  
  def updateRecord(id: String, ageAtDeath: Integer): Unit = {
    val partialUpdate = mapAsJavaMap(List(("set", ageAtDeath)).toMap)
    val doc = new SolrInputDocument()
    doc.addField("id", id)
    doc.addField("age_at_death", partialUpdate)
    userver.add(doc)
  }
}