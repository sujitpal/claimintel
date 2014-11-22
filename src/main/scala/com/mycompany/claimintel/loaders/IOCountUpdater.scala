package com.mycompany.claimintel.loaders

import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.util.concurrent.atomic.AtomicInteger

import scala.collection.JavaConversions.asScalaBuffer
import scala.collection.JavaConversions.mapAsJavaMap
import scala.collection.JavaConversions.seqAsJavaList

import org.apache.commons.codec.digest.DigestUtils
import org.apache.solr.client.solrj.SolrQuery
import org.apache.solr.client.solrj.SolrQuery.ORDER
import org.apache.solr.client.solrj.SolrQuery.SortClause
import org.apache.solr.client.solrj.impl.HttpSolrServer
import org.apache.solr.common.SolrInputDocument

object IOCountUpdater extends App {

  val SolrUrl = "http://localhost:8983/solr"
    
  val updater = new IOCountUpdater(SolrUrl)
  updater.update()
  
}

class IOCountUpdater(val solrUrl: String) {

  val server = new HttpSolrServer(solrUrl)
  val updateCount = new AtomicInteger(0)
  val logger = new PrintWriter(new FileWriter(new File("/tmp/log.txt")))
  
  def update(): Unit = {
    val numrecs = numIORecs()
    val rowsPerPage = 1000
    val numpages = (numrecs / rowsPerPage).toInt + 1
    var count = 0
    var prevId: String = null
    var cursorMark = "*"
    (0 until numpages).foreach(page => {
      val start = (page) * rowsPerPage
      val (nextCursorMark, ids) = results(cursorMark, rowsPerPage)
      cursorMark = nextCursorMark
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
    server.commit()
    server.shutdown()
    logger.flush()
    logger.close()
  }
  
  def numIORecs(): Long = {
    val query = new SolrQuery()
    query.setQuery("*:*")
    query.setFilterQueries("rec_type:(I OR O)")
    query.setRows(0)
    val resp = server.query(query)
    resp.getResults().getNumFound()
  }
  
  def results(cursorMark: String, rows: Int): (String,List[String]) = {
    val query = new SolrQuery()
    query.setQuery("*:*")
    query.setFilterQueries("rec_type:(I OR O)",
      "clm_from_dt:[* TO *]")
    query.setFields("desynpuf_id")
    query.setStart(0)
    query.setRows(rows)
    query.setSorts(List(
      new SortClause("desynpuf_id", ORDER.asc),
      new SortClause("id", ORDER.asc)))
    query.add("cursorMark", cursorMark)
    val resp = server.query(query)
    val nextCursorMark = resp.getNextCursorMark()
    val results = resp.getResults().map(doc => 
      doc.getFieldValue("desynpuf_id").asInstanceOf[String])
      .toList
    (nextCursorMark, results)
  }
  
  def addIOCount(desynpufId: String, iocount: Int): Unit = {
    val ucount = updateCount.addAndGet(1)
    Console.println("%s\t%d\t(%d)".format(desynpufId, iocount, ucount))
    logger.println("%s\t%d".format(desynpufId, iocount))
    val partialUpdate = mapAsJavaMap(List(("set", iocount)).toMap)
    val doc = new SolrInputDocument()
    doc.addField("id", DigestUtils.md5Hex("B:" + desynpufId))
    doc.addField("num_io", partialUpdate)
    server.add(doc)
    if (ucount % 1000 == 0) server.commit() else {}
  }
}