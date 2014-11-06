package com.mycompany.claimintel

import org.springframework.stereotype.Service
import java.io.OutputStream
import org.jfree.data.category.DefaultCategoryDataset
import org.jfree.chart.ChartFactory
import org.jfree.chart.plot.PlotOrientation
import java.awt.Color
import org.jfree.chart.ChartRenderingInfo
import org.jfree.chart.entity.StandardEntityCollection
import org.jfree.chart.plot.CategoryPlot

@Service
class ChartService {

  def name = "ChartService"
    
  def bar(data: Map[String,Int], title: String,
      xtitle: String, ytitle: String,
      ostream: OutputStream): Unit = {
    val dataset = new DefaultCategoryDataset()
    data.keys.toList.sorted
      .map(k => dataset.addValue(data(k), title, k))
    val chart = ChartFactory.createBarChart(title, xtitle, ytitle, 
      dataset, PlotOrientation.HORIZONTAL, false, true, false)
    val plot = chart.getPlot().asInstanceOf[CategoryPlot]
    
    plot.setBackgroundPaint(Color.WHITE)
    val info = new ChartRenderingInfo(new StandardEntityCollection())
    val image = 
  }
}