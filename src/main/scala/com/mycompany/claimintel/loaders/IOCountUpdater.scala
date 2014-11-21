package com.mycompany.claimintel.loaders

import org.apache.solr.client.solrj.impl.HttpSolrServer
import org.apache.solr.client.solrj.SolrQuery
import org.apache.solr.client.solrj.SolrQuery.ORDER
import scala.collection.JavaConversions._
import java.io.PrintWriter
import java.io.FileWriter
import java.io.File
import org.apache.solr.client.solrj.impl.ConcurrentUpdateSolrServer
import java.util.concurrent.atomic.AtomicInteger
import org.apache.solr.common.SolrInputDocument
import org.apache.commons.codec.digest.DigestUtils

object IOCountUpdater extends App {

  val SolrUrl = "http://localhost:8983/solr"
    
  val updater = new IOCountUpdater(SolrUrl)
  updater.update()
  
}

class IOCountUpdater(val solrUrl: String) {

  val qserver = new HttpSolrServer(solrUrl)
  val userver = new ConcurrentUpdateSolrServer(solrUrl, 1000, 4)
  val updateCount = new AtomicInteger(0)
  
  def update(): Unit = {
    val numrecs = numIORecs()
    val rowsPerPage = 1000
    val numpages = (numrecs / rowsPerPage).toInt + 1
    var count = 0
    var prevId: String = null
    (0 until numpages).foreach(page => {
      val start = (page) * rowsPerPage
      val ids = results(start, rowsPerPage)
      ids.foreach(id => {
        if (id.equals(prevId)) count = count + 1
        else {
          if (prevId != null) {
            addIOCount(prevId, count)
          }
          count = 1
        }
        prevId = id
      })
      if (count > 0) addIOCount(prevId, count)
    })
    userver.commit()
    userver.shutdown()
    qserver.shutdown()
  }
  
  def numIORecs(): Long = {
    val query = new SolrQuery()
    query.setQuery("*:*")
    query.setFilterQueries("rec_type:(I OR O)")
    query.setRows(0)
    val resp = qserver.query(query)
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
    val resp = qserver.query(query)
    resp.getResults().map(doc => 
      doc.getFieldValue("desynpuf_id").asInstanceOf[String])
      .toList
  }
  
  def addIOCount(desynpufId: String, iocount: Int): Unit = {
    val partialUpdate = mapAsJavaMap(List(("set", iocount)).toMap)
    val doc = new SolrInputDocument()
    doc.addField("id", DigestUtils.md5Hex("B:" + desynpufId))
    doc.addField("num_io", partialUpdate)
    userver.add(doc)
    val ucount = updateCount.addAndGet(1)
    if (ucount % 1000 == 0) userver.commit() else {}
  }
}