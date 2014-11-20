package com.mycompany.claimintel.dtos;

import java.util.Map;

public class ContinuousStats implements IDataStats {

  private double min;
  private double max;
  private long count;
  private double mean;
  private double stddev;
  
  public ContinuousStats() {}
  
  public ContinuousStats(Map<String,Object> data) {
    setMin((Double) data.get("min"));
    setMax((Double) data.get("max"));
    setCount((Long) data.get("count"));
    setMean((Double) data.get("mean"));
    setStddev((Double) data.get("stddev"));
  }
  
  public double getMin() {
    return min;
  }
  
  public void setMin(double min) {
    this.min = min;
  }
  
  public double getMax() {
    return max;
  }
  
  public void setMax(double max) {
    this.max = max;
  }
  
  public long getCount() {
    return count;
  }
  
  public void setCount(long count) {
    this.count = count;
  }
  
  public double getMean() {
    return mean;
  }
  
  public void setMean(double mean) {
    this.mean = mean;
  }
  
  public double getStddev() {
    return stddev;
  }

  public void setStddev(double stddev) {
    this.stddev = stddev;
  }
}
