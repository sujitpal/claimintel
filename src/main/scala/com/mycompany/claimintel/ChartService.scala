package com.mycompany.claimintel

import java.awt.Color
import java.io.OutputStream
import org.jfree.chart.ChartFactory
import org.jfree.chart.ChartRenderingInfo
import org.jfree.chart.ChartUtilities
import org.jfree.chart.axis.AxisLocation
import org.jfree.chart.entity.StandardEntityCollection
import org.jfree.chart.plot.CategoryPlot
import org.jfree.chart.plot.PlotOrientation
import org.jfree.data.category.DefaultCategoryDataset
import org.springframework.stereotype.Service
import org.apache.commons.lang3.StringUtils

@Service
class ChartService {

  def name = "ChartService"
    
  def bar(data: Map[String,Double], title: String,
      xtitle: String, ytitle: String,
      horizontal: Boolean, width: Int, height: Int,
      ostream: OutputStream): Unit = {
    val dataset = new DefaultCategoryDataset()
    // if data is a range, then sort them differently
    val sortedKeys = if (data.keys.head.indexOf("-") > -1)
      data.keys.toList.sortWith((a,b) => 
        a.split("-")(0).toInt < b.split("-")(0).toInt)
      else data.keys.toList.sorted
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
  
}