package com.mycompany.claimintel.dtos;

import java.util.List;

public class PopDistrib {
  
  private String encodedData;
  private List<CategoryStats> stats;
  private long total;
  
  public String getEncodedData() {
    return encodedData;
  }
  
  public void setEncodedData(String encodedData) {
    this.encodedData = encodedData;
  }
  
  public List<CategoryStats> getStats() {
    return stats;
  }
  
  public void setStats(List<CategoryStats> stats) {
    this.stats = stats;
  }

  public long getTotal() {
    return total;
  }

  public void setTotal(long total) {
    this.total = total;
  }
}
