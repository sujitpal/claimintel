package com.mycompany.claimintel.loaders

import org.apache.solr.client.solrj.impl.HttpSolrServer
import org.apache.solr.client.solrj.SolrQuery
import org.apache.solr.client.solrj.SolrQuery.ORDER
import scala.collection.JavaConversions._
import java.io.PrintWriter
import java.io.FileWriter
import java.io.File

object IOCountUpdater extends App {

  val SolrUrl = "http://localhost:8983/solr"
  val updater = new IOCountUpdater(SolrUrl)
  updater.update()
  
}

class IOCountUpdater(val solrUrl: String) {

  val solr = new HttpSolrServer(solrUrl)
  
  def update(): Unit = {
    val numrecs = numIORecs()
    val rowsPerPage = 1000
    val numpages = (numrecs / rowsPerPage).toInt + 1
//    val numpages = 10
    val writer = new PrintWriter(new FileWriter(new File("/tmp/stuff.txt")), true)
    var count = 0
    var prevId: String = null
    (1 to numpages).foreach(page => {
      val start = (page - 1) * rowsPerPage
      val ids = results(start, rowsPerPage)
      ids.foreach(id => {
        if (id.equals(prevId)) count = count + 1
        else {
          if (prevId != null) {
            writer.println(prevId + "\t" + count)
          }
          count = 1
        }
        prevId = id
      })
      if (count > 0) writer.println(prevId + "\t" + count)
    })
    writer.flush()
    writer.close()
  }
  
  def numIORecs(): Long = {
    val query = new SolrQuery()
    query.setQuery("*:*")
    query.setFilterQueries("rec_type:(I OR O)")
    query.setFields("desynpuf_id")
    query.setRows(0)
    val resp = solr.query(query)
    resp.getResults().getNumFound()
  }
  
  def results(start: Int, rows: Int): List[String] = {
    val query = new SolrQuery()
    query.setQuery("*:*")
    query.setFilterQueries("rec_type:(I OR O)")
    query.setFields("desynpuf_id")
    query.setStart(start)
    query.setRows(rows)
    query.setSort("desynpuf_id", ORDER.asc)
    val resp = solr.query(query)
    resp.getResults().map(doc => 
      doc.getFieldValue("desynpuf_id").asInstanceOf[String])
      .toList
  }
}