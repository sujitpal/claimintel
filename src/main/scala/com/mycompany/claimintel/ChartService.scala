package com.mycompany.claimintel

import java.awt.Color
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date

import scala.collection.mutable.ArrayBuffer

import org.jfree.chart.ChartFactory
import org.jfree.chart.ChartRenderingInfo
import org.jfree.chart.ChartUtilities
import org.jfree.chart.axis.AxisLocation
import org.jfree.chart.axis.SymbolAxis
import org.jfree.chart.entity.StandardEntityCollection
import org.jfree.chart.plot.CategoryPlot
import org.jfree.chart.plot.PlotOrientation
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer
import org.jfree.data.category.DefaultCategoryDataset
import org.jfree.data.xy.DefaultXYZDataset
import org.jfree.data.xy.XYSeries
import org.jfree.data.xy.XYSeriesCollection
import org.springframework.stereotype.Service

@Service
class ChartService {

  def bar(data: Map[String,Double], title: String,
      xtitle: String, ytitle: String,
      horizontal: Boolean, width: Int, height: Int,
      sortType: ChartSorting.Value, 
      ostream: OutputStream): Unit = {
    val dataset = new DefaultCategoryDataset()
    // if data is a range, then sort them differently
    val sortedKeys = sortType match {
      case ChartSorting.Normal => data.keys.toList.sorted
      case ChartSorting.StrRange => data.keys.toList
        .sortWith((a,b) => 
          a.split("-")(0).toInt < b.split("-")(0).toInt)
      case ChartSorting.IntRange => data.keys.toList
        .sortWith((a,b) => a.toInt < b.toInt)
      case ChartSorting.Count => data.keys.toList
        .sortWith((a,b) => data(a) > data(b))
    }
    sortedKeys.map(k => dataset.addValue(data(k), title, k))
    val orientation = if (horizontal) PlotOrientation.HORIZONTAL 
                      else PlotOrientation.VERTICAL
    val chart = ChartFactory.createBarChart(title, xtitle, ytitle, 
      dataset, orientation, false, true, false)
    val plot = chart.getPlot().asInstanceOf[CategoryPlot]
    plot.setBackgroundPaint(Color.WHITE)
    plot.setRangeGridlinePaint(Color.WHITE)
    plot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_LEFT)
    plot.setNoDataMessage(if (title.isEmpty) "Please Wait" else title) 
    val info = new ChartRenderingInfo(new StandardEntityCollection())
    val image = chart.createBufferedImage(width, height, info)
    ChartUtilities.writeBufferedImageAsPNG(ostream, image)
    ostream.flush()
  }
  
  val dateFormatter = new SimpleDateFormat("yyyyMMdd")
  
  def line(data: Map[String,Double], title: String, 
      xtitle: String, ytitle: String, 
      horizontal: Boolean, width: Int, 
      height: Int, ostream: OutputStream): Unit = {
    val cdata = data.map(kv => 
      (dateFormatter.parse(kv._1), kv._2))
    val ckeys = cdata.keys.toList.sorted
    val startDate = ckeys.head
    val series = new XYSeries(title)
    ckeys.map(k => (daysBetween(startDate, k), cdata(k)))
      .foreach(xy => series.add(xy._1, xy._2))
    val dataset = new XYSeriesCollection()
    dataset.addSeries(series)
    val chart = ChartFactory.createXYLineChart(
      title, xtitle, ytitle, dataset, 
      if (horizontal) PlotOrientation.HORIZONTAL 
      else PlotOrientation.VERTICAL, 
      false, false, false)
    chart.setBackgroundPaint(Color.WHITE)
    val plot = chart.getXYPlot()
    plot.setBackgroundPaint(Color.WHITE)
    plot.setDomainGridlinePaint(Color.LIGHT_GRAY)
    plot.setRangeGridlinePaint(Color.LIGHT_GRAY)
    plot.setNoDataMessage(if (title.isEmpty) "Please Wait" else title) 
    val info = new ChartRenderingInfo(new StandardEntityCollection())
    val image = chart.createBufferedImage(width, height, info)
    ChartUtilities.writeBufferedImageAsPNG(ostream, image)
    ostream.flush()
  }
  
  def daysBetween(refDt: Date, dt: Date): Int =
    return ((dt.getTime() - refDt.getTime()) / 86400000).toInt
  
  def bubble(data: Map[String,Double], title: String, 
      xtitle: String, ytitle: String, 
      horizontal: Boolean, width: Int, 
      height: Int, ostream: OutputStream): Unit = {
    
    val xkeys = ArrayBuffer[String]()
    val ykeys = ArrayBuffer[String]()
    data.keys.map(k => {
      val Array(x, y) = k.split("::")
      xkeys += x
      ykeys += y
    })
    // symbols for x and y axes
    val xks = sortIntelligently(xkeys)
    val yks = sortIntelligently(ykeys)
    // mapping from symbol to numbers
    val xmap = xks.zipWithIndex.toMap
    val ymap = yks.zipWithIndex.toMap
    // instantiate and populate dataset
    val dataset = new DefaultXYZDataset()
    val zmax = data.values.max
    val xs = ArrayBuffer[Double]()
    val ys = ArrayBuffer[Double]()
    val zs = ArrayBuffer[Double]()
    data.foreach(e => {
      val Array(xkey, ykey) = e._1.split("::")
      xs += xmap(xkey)
      ys += ymap(ykey)
      zs += e._2 / zmax
    })
    dataset.addSeries(title, Array(xs.toArray, ys.toArray, zs.toArray))
    // draw the chart
    val chart = ChartFactory.createBubbleChart(
      title, xtitle, ytitle, dataset, 
      if (horizontal) PlotOrientation.HORIZONTAL 
      else PlotOrientation.VERTICAL, 
      false, false, false)
    chart.setBackgroundPaint(Color.WHITE)
    val renderer = new XYLineAndShapeRenderer()
    val plot = chart.getXYPlot()
    renderer.setSeriesPaint(0, Color.RED)
    plot.setRenderer(1, renderer)
    plot.setDomainAxis(0, new SymbolAxis(xtitle, xks.toArray))
    plot.setRangeAxis(0, new SymbolAxis(ytitle, yks.toArray))
    plot.setBackgroundPaint(Color.WHITE)
    plot.setDomainGridlinePaint(Color.LIGHT_GRAY)
    plot.setRangeGridlinePaint(Color.LIGHT_GRAY)
    plot.setBackgroundPaint(Color.WHITE)
    plot.setDomainGridlinePaint(Color.LIGHT_GRAY)
    plot.setRangeGridlinePaint(Color.LIGHT_GRAY)
    plot.setNoDataMessage(if (title.isEmpty) "Please Wait" else title) 
    val info = new ChartRenderingInfo(new StandardEntityCollection())
    val image = chart.createBufferedImage(width, height, info)
    ChartUtilities.writeBufferedImageAsPNG(ostream, image)
    ostream.flush()
  }
    
  def sortIntelligently(keys: ArrayBuffer[String]): ArrayBuffer[String] = 
    if (keys.head.indexOf("-") > -1)
      keys.distinct.sortWith((a,b) => 
        a.split("-")(0).toInt < b.split("-")(0).toInt)
    else keys.distinct.sorted
}

object ChartSorting extends Enumeration {
  type ChartSorting = Value
  val Normal, StrRange, IntRange, Count = Value
}
