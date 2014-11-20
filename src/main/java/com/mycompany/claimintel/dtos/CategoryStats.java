package com.mycompany.claimintel.dtos;

public class CategoryStats implements IDataStats {

  private String name;
  private long count;
  private double pcount;
  private boolean selected;
  
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public long getCount() {
    return count;
  }
  
  public void setCount(long count) {
    this.count = count;
  }
  
  public double getPcount() {
    return pcount;
  }
  
  public void setPcount(double pcount) {
    this.pcount = pcount;
  }

  public boolean isSelected() {
    return selected;
  }

  public void setSelected(boolean selected) {
    this.selected = selected;
  }
}
