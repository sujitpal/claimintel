package com.mycompany.claimintel.dtos;

public class PopFilter {

  private String name;
  private String value;
  
  public PopFilter() {}
  
  public PopFilter(String name, String value) {
    setName(name);
    setValue(value);
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public String getValue() {
    return value;
  }
  
  public void setValue(String value) {
    this.value = value;
  }
}
