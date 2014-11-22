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
import org.apache.solr.common.util.NamedList
import com.mycompany.claimintel.dtos.Patient
import org.apache.solr.common.SolrDocument
import com.mycompany.claimintel.dtos.PatientTransaction

@Service
class SolrService {
  
  val server = new HttpSolrServer("http://localhost:8983/solr/collection1")
  
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
    val ub = round(yearsBetween(youngestDob, now), 10, true) + 10
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
    "bene_birth_date:[NOW-%dYEAR TO NOW-%dYEAR}"
  val IntervalQueryPattern = Pattern.compile(
    """bene_birth_date:\[NOW-(\d+)YEAR TO NOW-(\d+)YEAR\}""")
    
  def queryToInterval(q: String): String = {
    val m = IntervalQueryPattern.matcher(q)
    if (m.matches()) List(m.group(2), m.group(1)).mkString("-")
    else "0-0"
  }
  
  def intervalToQuery(interval: String): String = {
    val bounds = interval.split("-").map(_.toInt)
    IntervalQueryTemplate.format(bounds(1), bounds(0))
  }
  
  def codeFacets(filters: List[(String,String)], ttype: String): 
      Map[String,Map[String,Long]] = {
    val codeFields = List("icd9_dgns_cds", "icd9_prcdr_cds", "hcpcs_cds")
    val query = new SolrQuery()
    query.setQuery("*:*")
    query.setFilterQueries(if ("B".equals(ttype)) "rec_type:(I OR O)" 
                           else "rec_type:" + ttype)
    groupFilters(filters).foreach(fq => 
      query.addFilterQuery(List(fq._1, fq._2).mkString(":")))
    query.setRows(0)
    query.setFacet(true)
    codeFields.foreach(codeField => query.addFacetField(codeField))
    val resp = server.query(query)
    resp.getFacetFields().map(ff => 
      (ff.getName(), ff.getValues().map(fval => 
        (reformat(fval.getName(), ff.getName()), 
        fval.getCount())).toMap))
      .toMap
  }
  
  def mortalityFacets(filters: List[(String,String)]): 
      Map[String,Long] = {
    val query = new SolrQuery()
    query.setQuery("*:*")
    query.setFilterQueries("rec_type:B", "age_at_death:[1 TO *]")
    groupFilters(filters).foreach(fq => 
      query.addFilterQuery(List(fq._1, fq._2).mkString(":")))
    query.setRows(0)
    query.setFacet(true)
    val minAge = findMortalityAgeBoundary(true)
    val maxAge = findMortalityAgeBoundary(false) + 10
    val binSize = (maxAge - minAge) / 10
    (minAge to maxAge by binSize)
      .sliding(2)
      .toList
      .map(v => mortalityToQuery(v(0), v(1)))
      .foreach(fq => query.addFacetQuery(fq))
    val resp = server.query(query)
    resp.getFacetQuery().entrySet()
      .map(e => (queryToMortality(e.getKey()).toString, 
        e.getValue().toLong))
      .toMap
  }

  val MortalityQueryPattern = Pattern.compile(
    """age_at_death:\[(\d+) TO (\d+)}""")

  def queryToMortality(q: String): String = {
    val m = MortalityQueryPattern.matcher(q)
    val bounds = if (m.matches()) ((m.group(1).toInt, m.group(2).toInt))
                 else (0, 0)
    List(bounds._1, bounds._2).mkString("-")
  }
  
  def mortalityToQuery(lb: Int, ub: Int): String =
    "age_at_death:[%d TO %d}".format(lb, ub)

  def findMortalityAgeBoundary(lowest: Boolean): Int = {
    val query = new SolrQuery()
    query.setQuery("*:*")
    query.setFilterQueries("rec_type:B", "age_at_death:[1 TO *]")
    query.setRows(1)
    query.setFields("age_at_death")
    query.setSort("age_at_death", if (lowest) ORDER.asc 
                                  else ORDER.desc)
    val resp = server.query(query)
    resp.getResults().head
      .getFieldValue("age_at_death")
      .asInstanceOf[Int]
  }
  
  def mortalityStatistics(filters: List[(String,String)]):
      Map[String,Object] = {
    val query = new SolrQuery()
    query.setQuery("*:*")
    query.setFilterQueries("rec_type:B", "age_at_death:[1 TO *]")
    groupFilters(filters).foreach(fq => 
      query.addFilterQuery(List(fq._1, fq._2).mkString(":")))
    query.setRows(0)
    query.add("stats", "true")
    query.add("stats.field", "age_at_death")
    val resp = server.query(query)
    resp.getResponse()
      .get("stats").asInstanceOf[NamedList[Object]]
      .get("stats_fields").asInstanceOf[NamedList[Object]]
      .get("age_at_death").asInstanceOf[NamedList[Object]]
      .map(e => (e.getKey(), e.getValue()))
      .toMap
  }
  
  def costFacets(filters: List[(String,String)], 
      ttype: String, costLb: Float, costUb: Float): 
      (Float,Float,Map[String,Long]) = {
    val query = new SolrQuery()
    query.setQuery("*:*")
    query.setFilterQueries(if ("B".equals(ttype)) "rec_type:(I OR O)" 
                           else "rec_type:" + ttype)
    groupFilters(filters).foreach(fq => 
      query.addFilterQuery(List(fq._1, fq._2).mkString(":")))
    query.setRows(0)
    query.setFacet(true)
    val minCost = findCostBoundary(true, costLb)
    val maxCost = findCostBoundary(false, costUb) + 50
    val binSize = (maxCost - minCost) / 50
    (minCost to maxCost by binSize)
      .sliding(2).toList
      .map(v => costToQuery(v(0), v(1)))
      .foreach(fq => query.addFacetQuery(fq))
    val resp = server.query(query)
    val facets = resp.getFacetQuery().entrySet()
      .map(e => (queryToCost(e.getKey()).toString, 
        e.getValue().toLong))
      .toMap
    (minCost, maxCost, facets)
  }
  
  def findCostBoundary(lowest: Boolean, dflt: Float): Float = {
    if (dflt > 0) dflt
    else {
      val query = new SolrQuery();
      query.setQuery("*:*")
      query.setFilterQueries(
        "rec_type:(I OR O)", 
        "clm_pmt_amt:[0 TO *]")
      query.setSort("clm_pmt_amt", if (lowest) ORDER.asc 
                                   else ORDER.desc)
      query.setFields("clm_pmt_amt")
      query.setRows(1)
      val resp = server.query(query)
      val result = resp.getResults().head
      result.getFieldValue("clm_pmt_amt").asInstanceOf[Float]
    }
  }
  
  val CostQueryPattern = Pattern.compile(
    """clm_pmt_amt:\[(\d+) TO (\d+)}""")

  def queryToCost(q: String): Int = {
    val m = CostQueryPattern.matcher(q)
    if (m.matches()) ((m.group(1).toInt + m.group(2).toInt) / 2).toInt
    else 0
  }
  
  def costToQuery(lb: Float, ub: Float): String =
    "clm_pmt_amt:[%d TO %d}".format(lb.toInt, ub.toInt)
    
  def costToQuery(cost: Float): String = 
    costToQuery(cost - 25, cost + 25) 
  
  def costStatistics(filters: List[(String,String)], 
      ttype: String): Map[String,Object] = {
    val query = new SolrQuery()
    query.setQuery("*:*")
    query.setFilterQueries(if ("B".equals(ttype)) "rec_type:(I OR O)" 
                           else "rec_type:" + ttype)
    query.addFilterQuery("clm_pmt_amt:[0 TO *]") // ensure +ve
    groupFilters(filters).foreach(fq => 
      query.addFilterQuery(List(fq._1, fq._2).mkString(":")))
    query.setRows(0)
    query.add("stats", "true")
    query.add("stats.field", "clm_pmt_amt")
    val resp = server.query(query)
    resp.getResponse()
      .get("stats").asInstanceOf[NamedList[Object]]
      .get("stats_fields").asInstanceOf[NamedList[Object]]
      .get("clm_pmt_amt").asInstanceOf[NamedList[Object]]
      .map(e => (e.getKey(), e.getValue()))
      .toMap
  }
  
  def patients(filters: List[(String,String)], 
      start: Int): (Long,List[Patient]) = {
    // get patients matching filter sorted by num_io
    val query = new SolrQuery()
    query.setQuery("*:*")
    query.setFilterQueries("rec_type:B")
    groupFilters(filters).foreach(fq => 
      query.addFilterQuery(List(fq._1, fq._2).mkString(":")))
    query.setStart(start)
    query.setRows(25)
    query.setSort("num_io", ORDER.desc)
    query.setFields("desynpuf_id", "bene_birth_date", 
      "bene_sex", "bene_race", "sp_state", 
      "bene_hi_cvrage_tot_mons", "bene_smi_cvrage_tot_mons",
      "bene_hmo_cvrage_tot_mons", "bene_cvrg_mos_num",
      "bene_comorbs", "num_io")
    val resp = server.query(query)
    val now = new Date()
    val numFound = resp.getResults().getNumFound()
    val patients = resp.getResults()
      .map(doc => populatePatient(doc, now))
      .toList
    (numFound, patients)
  }
  
  def timeline(deSynpufId: String): 
      (Patient,List[PatientTransaction]) = {
    // get patient info
    val pquery = new SolrQuery()
    pquery.setQuery("*:*")
    pquery.setFilterQueries("rec_type:B", 
      "desynpuf_id:%s".format(deSynpufId))
    pquery.setRows(1)
    pquery.setFields("desynpuf_id", "bene_birth_date", 
      "bene_sex", "bene_race", "sp_state", 
      "bene_hi_cvrage_tot_mons", "bene_smi_cvrage_tot_mons",
      "bene_hmo_cvrage_tot_mons", "bene_cvrg_mos_num",
      "bene_comorbs", "num_io")
    val presp = server.query(pquery)
    val now = new Date()
    val patient = populatePatient(presp.getResults().head, now)
    // get transactions for patient
    val tquery = new SolrQuery()
    tquery.setQuery("*:*")
    tquery.setFilterQueries("rec_type:(I OR O)", 
      "desynpuf_id:%s".format(deSynpufId),
      "clm_from_dt:[* TO *]")
    tquery.setRows(patient.getNumTransactions())
    tquery.setSort("clm_from_dt", ORDER.asc)
    tquery.setFields("rec_type", "clm_id", "clm_from_dt", 
      "clm_thru_dt", "prvdr_num", "clm_pmt_amt", 
      "icd9_dgns_cds", "icd9_prcdr_cds", "hcpcs_cds")
    val tresp = server.query(tquery)
    val transactions = tresp.getResults().map(doc => {
      val tx = new PatientTransaction()
      tx.setIoro(doc.getFieldValue("rec_type").asInstanceOf[String] match {
        case "I" => "Inpatient"
        case "O" => "Outpatient"
      })
      tx.setClaimId(doc.getFieldValue("clm_id").asInstanceOf[String])
      tx.setClaimFrom(doc.getFieldValue("clm_from_dt").asInstanceOf[Date])
      tx.setClaimThru(doc.getFieldValue("clm_thru_dt").asInstanceOf[Date])
      tx.setProvider(doc.getFieldValue("prvdr_num").asInstanceOf[String])
      tx.setClmPmtAmt(doc.getFieldValue("clm_pmt_amt").asInstanceOf[Float])
      val dgnsCds = doc.getFieldValues("icd9_dgns_cds")
      if (dgnsCds != null) tx.setDiagCodes(
        dgnsCds.map(_.asInstanceOf[String])
        .map(reformat(_, "icd9_dgns_cds")).toArray)
      val prcdrCds = doc.getFieldValues("icd9_prcdr_cds")
      if (prcdrCds != null) tx.setPrcCodes(
        prcdrCds.map(_.asInstanceOf[String])
        .map(reformat(_, "icd9_prcdr_cds")).toArray)
      val hcpcsCds = doc.getFieldValues("hcpcs_cds")
      if (hcpcsCds != null) tx.setHcpcsCodes(
        hcpcsCds.map(_.asInstanceOf[String])
        .map(reformat(_, "hcpcs_cds")).toArray)
      tx
    }).toList
    (patient, transactions)
  }
  
  def populatePatient(doc: SolrDocument, now: Date): Patient = {
    val patient = new Patient()
    patient.setDesynpufId(doc.getFieldValue("desynpuf_id")
      .asInstanceOf[String])
    patient.setNumTransactions(doc.getFieldValue("num_io")
      .asInstanceOf[Int])
    patient.setAge(yearsBetween( 
      doc.getFieldValue("bene_birth_date")
      .asInstanceOf[Date], now))
    patient.setSex(doc.getFieldValue("bene_sex")
      .asInstanceOf[String])
    patient.setRace(doc.getFieldValue("bene_race")
      .asInstanceOf[String])
    patient.setState(doc.getFieldValue("sp_state")
      .asInstanceOf[String])
    patient.setPartACoverageMos(doc.getFieldValue("bene_hi_cvrage_tot_mons")
      .asInstanceOf[Int])
    patient.setPartBCoverageMos(doc.getFieldValue("bene_smi_cvrage_tot_mons")
      .asInstanceOf[Int])
    patient.setHmoCoverageMos(doc.getFieldValue("bene_hmo_cvrage_tot_mons")
      .asInstanceOf[Int])
    patient.setPartDCoverageMos(doc.getFieldValue("bene_cvrg_mos_num")
      .asInstanceOf[Int])
    val comorbs = doc.getFieldValues("bene_comorbs")
    if (comorbs != null) patient.setComorbs(comorbs
      .map(_.asInstanceOf[String])
      .toArray)
    else {}
    patient
  }
  
  def groupFilters(filters: List[(String,String)]): 
      List[(String,String)] = {
    filters.groupBy(_._1)
      .map(group => (group._1, 
        "(" + group._2.map(_._2).mkString(" OR ") + ")"))
      .toList
  }

  def isSelected(facetFilters: List[(String,String)], 
      klhs: String, krhs: String): Boolean = 
    facetFilters.filter(nvp => 
      (klhs.equals(nvp._1) && krhs.equals(nvp._2)))
      .size > 0

  def reformat(code: String, codeType: String): String = {
    if (codeType.startsWith("icd9_")) {
      if (code.length() > 3) {
        val (a, b) = code.splitAt(3)
        List(a, b).mkString(".")
      } else code
    } else code
  }
}