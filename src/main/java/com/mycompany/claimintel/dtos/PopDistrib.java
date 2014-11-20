package com.mycompany.claimintel.dtos;

import java.util.List;

public class PopDistrib {
  
  private String encodedData;
  private List<IDataStats> stats;
  private long total;
  
  public String getEncodedData() {
    return encodedData;
  }
  
  public void setEncodedData(String encodedData) {
    this.encodedData = encodedData;
  }
  
  public List<IDataStats> getStats() {
    return stats;
  }
  
  public void setStats(List<IDataStats> stats) {
    this.stats = stats;
  }

  public long getTotal() {
    return total;
  }

  public void setTotal(long total) {
    this.total = total;
  }
}
