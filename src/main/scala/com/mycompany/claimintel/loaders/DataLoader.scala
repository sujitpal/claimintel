package com.mycompany.claimintel.loaders

import org.apache.solr.client.solrj.impl.HttpSolrServer
import java.io.File
import scala.io.Source
import scala.collection.JavaConversions._
import org.apache.commons.lang3.StringUtils
import org.apache.solr.common.SolrDocument
import org.apache.solr.common.SolrInputDocument
import java.util.Date
import java.util.Calendar
import java.util.Locale
import org.apache.commons.codec.digest.DigestUtils
import org.apache.solr.common.params.SolrParams
import org.apache.solr.common.params.MapSolrParams

object DataLoader extends App {

  val DataDir = "/home/sujit/Projects/med_data/cms_gov"
  val SolrUrl = "http://localhost:8983/solr"
    
  val loader = new DataLoader(DataDir, SolrUrl)
  loader.load()
}

class DataLoader(datadir: String, solrUrl: String) {

  val Genders = Array("Male", "Female")
  val Ethnicities = Array("White", "Black", "Other", "Other2", "Hispanic")
  val States = Array("AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE",
                     "DC", "FL", "GA", "HI", "ID", "IL", "IN", "IA",
                     "KS", "KY", "LA", "ME", "MD", "MA", "MI", "MN",
                     "MS", "MO", "MT", "NE", "NV", "NH", "NJ", "NM",
                     "NY", "NC", "ND", "OH", "OK", "OR", "PA", "XX", 
                     "RI", "SC", "SD", "TN", "TX", "UT", "VT", "XX",
                     "VA", "WA", "WV", "WI", "WY", "Other")
  val Comorbidities = Array("Alzheimers", "CHF", "CKD", "Cancer", 
                     "COPD", "Depression", "Diabetes", "IHD", 
                     "Osteoporosis", "RA/OA", "Stroke/TIA")

  val solr = new HttpSolrServer(solrUrl)
  val subdirs = Array(
//      "benefit_summary", 
      "inpatient_claims", 
      "outpatient_claims")
  
  def load(): Unit = {
//    solr.deleteByQuery("rec_type:O")
    solr.commit()
    subdirs.foreach(subdir => {
      new File(datadir, subdir).listFiles().foreach(file => {
        Console.println("Loading file: " + 
          StringUtils.join(List(subdir, file.getName()), File.separator))
        Source.fromFile(file).getLines().foreach(line => {
          if (!line.startsWith("\"")) {
            val cols = StringUtils.splitPreserveAllTokens(line, ",")
            val solrDoc = cols.length match {
              case 32 => parseBenefitSummary(cols)
              case 81 => parseInpatientClaim(cols)
              case 76 => parseOutpatientClaim(cols)
            }
            solr.add(solrDoc, 5000)
          }
        })
      })
      solr.commit()
    })
  }
  
  def parseBenefitSummary(cols: Array[String]): SolrInputDocument = {
    val doc = new SolrInputDocument()
    doc.addField("id", DigestUtils.md5Hex("B:" + cols(0)))
    doc.addField("rec_type", "B")
    doc.addField("desynpuf_id", cols(0))
    doc.addField("bene_birth_date", toDate(cols(1)))
    if (StringUtils.isNotEmpty(cols(2))) 
      doc.addField("bene_death_date", toDate(cols(2)))
    doc.addField("bene_sex", decode(cols(3), Genders))
    doc.addField("bene_race", decode(cols(4), Ethnicities))
    doc.addField("bene_esrd_ind", "Y".equals(cols(5)))
    doc.addField("sp_state", decode(cols(6), States))
    doc.addField("bene_county_cd", cols(7))
    if (StringUtils.isNotEmpty(cols(8)))
      doc.addField("bene_hi_cvrage_tot_mons", cols(8).toInt)
    if (StringUtils.isNotEmpty(cols(9)))
      doc.addField("bene_smi_cvrage_tot_mons", cols(9).toInt)
    if (StringUtils.isNotEmpty(cols(10)))
      doc.addField("bene_hmo_cvrage_tot_mons", cols(10).toInt)
    if (StringUtils.isNotEmpty(cols(11)))
      doc.addField("bene_cvrg_mos_num", cols(11).toInt)
    (12 to 22).foreach(colnum => 
      if ("1".equals(cols(colnum)))
        doc.addField("bene_comorbs", Comorbidities(colnum - 12)))
    if (StringUtils.isNotEmpty(cols(23)))
      doc.addField("medreimb_ip", cols(23).toFloat)
    if (StringUtils.isNotEmpty(cols(24)))
      doc.addField("benres_ip", cols(24).toFloat)
    if (StringUtils.isNotEmpty(cols(25)))
      doc.addField("pp_pymt_ip", cols(25).toFloat)
    if (StringUtils.isNotEmpty(cols(26)))
      doc.addField("medreimb_op", cols(26).toFloat)
    if (StringUtils.isNotEmpty(cols(27)))
      doc.addField("benres_op", cols(27).toFloat)
    if (StringUtils.isNotEmpty(cols(28)))
      doc.addField("pp_pymt_op", cols(28).toFloat)
    if (StringUtils.isNotEmpty(cols(29)))
      doc.addField("medreimb_car", cols(29).toFloat)
    if (StringUtils.isNotEmpty(cols(30)))
      doc.addField("benres_car", cols(30).toFloat)
    if (StringUtils.isNotEmpty(cols(31)))
      doc.addField("pp_pymt_car", cols(31).toFloat)
    doc
  }
  
  def parseInpatientClaim(cols: Array[String]): SolrInputDocument = {
    val doc = new SolrInputDocument()
    doc.addField("id", DigestUtils.md2Hex(
      List("I", cols(0), cols(1), cols(2)).mkString(":")))
    doc.addField("rec_type", "I")
    val bsfs = getBenefitSummary(cols(0))
    bsfs.entrySet().foreach(entry => 
      doc.addField(entry.getKey(), entry.getValue()))
    doc.addField("clm_id", cols(1))
    doc.addField("segment", cols(2))
    if (StringUtils.isNotEmpty(cols(4)))
      doc.addField("clm_from_dt", toDate(cols(3)))
    if (StringUtils.isNotEmpty(cols(4)))
      doc.addField("clm_thru_dt", toDate(cols(4)))
    if (StringUtils.isNotEmpty(cols(5)))
      doc.addField("prvdr_num", cols(5))
    if (StringUtils.isNotEmpty(cols(6)))
      doc.addField("clm_pmt_amt", cols(6).toFloat)
    if (StringUtils.isNotEmpty(cols(7)))
      doc.addField("nch_prmry_pyr_clm_pd_amt", cols(7).toFloat)
    if (StringUtils.isNotEmpty(cols(8)))
      doc.addField("at_physn_npi", cols(8))
    if (StringUtils.isNotEmpty(cols(9)))
      doc.addField("op_physn_npi", cols(9))
    if (StringUtils.isNotEmpty(cols(10)))
      doc.addField("ot_physn_npi", cols(10))
    if (StringUtils.isNotEmpty(cols(11)))
      doc.addField("clm_admsn_dt", toDate(cols(11)))
    if (StringUtils.isNotEmpty(cols(12)))
      doc.addField("admtng_icd9_dgns_cd", cols(12))
    if (StringUtils.isNotEmpty(cols(13)))
      doc.addField("clm_pass_thru_per_diem_amt", cols(13).toFloat)
    if (StringUtils.isNotEmpty(cols(14)))
      doc.addField("nch_bene_ip_ddctbl_amt", cols(14).toFloat)
    if (StringUtils.isNotEmpty(cols(15)))
      doc.addField("nch_bene_pta_coins_lblty_amt", cols(15).toFloat)
    if (StringUtils.isNotEmpty(cols(16)))
      doc.addField("nch_bene_blood_ddctbl_lblty_amt", cols(16).toFloat)
    if (StringUtils.isNotEmpty(cols(17)))
      doc.addField("clm_utlztn_day_cnt", cols(17).toInt)
    if (StringUtils.isNotEmpty(cols(18)))
      doc.addField("nch_bene_dschrg_dt", toDate(cols(18)))
    if (StringUtils.isNotEmpty(cols(19)))
      doc.addField("clm_drg_cd", cols(19))
    (20 to 29).foreach(colnum => 
      if (StringUtils.isNotEmpty(cols(colnum)))
    	doc.addField("icd9_dgns_cds", cols(colnum)))
    (30 to 35).foreach(colnum => 
      if (StringUtils.isNotEmpty(cols(colnum)))
        doc.addField("icd9_prcdr_cds", cols(colnum)))
    (36 to 80).foreach(colnum => 
      if (StringUtils.isNotEmpty(cols(colnum)))
        doc.addField("hcpcs_cds", cols(colnum)))
    doc
  }
  
  def parseOutpatientClaim(cols: Array[String]): SolrInputDocument = {
        val doc = new SolrInputDocument()
    doc.addField("id", DigestUtils.md2Hex(
      List("O", cols(0), cols(1), cols(2)).mkString(":")))
    doc.addField("rec_type", "O")
    val bsfs = getBenefitSummary(cols(0))
    bsfs.entrySet().foreach(entry => 
      doc.addField(entry.getKey(), entry.getValue()))
    doc.addField("clm_id", cols(1))
    doc.addField("segment", cols(2))
    if (StringUtils.isNotEmpty(cols(3)))
      doc.addField("clm_from_dt", toDate(cols(3)))
    if (StringUtils.isNotEmpty(cols(4)))
      doc.addField("clm_thru_dt", toDate(cols(4)))
    if (StringUtils.isNotEmpty(cols(5)))
      doc.addField("prvdr_num", cols(5))
    if (StringUtils.isNotEmpty(cols(6)))
      doc.addField("clm_pmt_amt", cols(6).toFloat)
    if (StringUtils.isNotEmpty(cols(7)))
      doc.addField("nch_prmry_pyr_clm_pd_amt", cols(7).toFloat)
    if (StringUtils.isNotEmpty(cols(8)))
      doc.addField("at_physn_npi", cols(8))
    if (StringUtils.isNotEmpty(cols(9)))
      doc.addField("op_physn_npi", cols(9))
    if (StringUtils.isNotEmpty(cols(10)))
      doc.addField("ot_physn_npi", cols(10))
    if (StringUtils.isNotEmpty(cols(11)))
      doc.addField("nch_bene_blood_ddctbl_lblty_amt", cols(11).toFloat)
    (12 to 21).foreach(colnum => 
       if (StringUtils.isNotEmpty(cols(colnum)))
         doc.addField("icd9_dgns_cds", cols(colnum)))
    (22 to 27).foreach(colnum => 
       if (StringUtils.isNotEmpty(cols(colnum)))
         doc.addField("icd9_prcdr_cds", cols(colnum)))
    if (StringUtils.isNotEmpty(cols(28)))
      doc.addField("nch_bene_ptb_ddctbl_amt", cols(28).toFloat)
    if (StringUtils.isNotEmpty(cols(29)))
      doc.addField("nch_bene_ptb_coins_lblty_amt", cols(29).toFloat)
    if (StringUtils.isNotEmpty(cols(30)))
      doc.addField("admtng_icd9_dgns_cd", cols(30))
    (31 to 75).foreach(colnum => 
      if (StringUtils.isNotEmpty(cols(colnum)))
        doc.addField("hcpcs_cds", cols(colnum)))
    doc
  }
  
  def getBenefitSummary(id: String): Map[String,_] = {
    val params = Map(
      ("q", "desynpuf_id:" + id),
      ("fq", "rec_type:B"),
      ("start", "0"),
      ("rows", "1")
    )
    val solrParams = new MapSolrParams(params)
    val resp = solr.query(solrParams)
    val sdoc = resp.getResults().head
    val keys = sdoc.getFieldNames().filter(fn => 
      (!(fn.equals("id") || 
          fn.equals("rec_type") ||
          fn.equals("_version_"))))
    keys.map(key => (key, sdoc.getFieldValue(key))).toMap
  }
  
  def toDate(str: String): Date = {
    val cal = Calendar.getInstance(Locale.getDefault())
    cal.set(Calendar.YEAR, str.slice(0, 4).toInt)
    cal.set(Calendar.MONTH, str.slice(5, 6).toInt)
    cal.set(Calendar.DAY_OF_MONTH, str.slice(7, 8).toInt)
    cal.getTime()
  }
  
  def decode(code: String, key: Array[String]): String = {
    key(code.toInt - 1)
  }
}