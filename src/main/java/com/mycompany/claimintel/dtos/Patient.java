package com.mycompany.claimintel.dtos;


public class Patient {

  private String desynpufId;
  private int age;
  private String sex;
  private String race;
  private String state;
  private int partACoverageMos;
  private int partBCoverageMos;
  private int hmoCoverageMos;
  private int partDCoverageMos;
  private String[] comorbs;
  private int numTransactions;
  
  public String getDesynpufId() {
    return desynpufId;
  }
  
  public void setDesynpufId(String desynpufId) {
    this.desynpufId = desynpufId;
  }
  
  public int getAge() {
    return age;
  }
  
  public void setAge(int age) {
    this.age = age;
  }
  
  public String getSex() {
    return sex;
  }
  
  public void setSex(String sex) {
    this.sex = sex;
  }
  
  public String getRace() {
    return race;
  }
  
  public void setRace(String race) {
    this.race = race;
  }
  
  public String getState() {
    return state;
  }
  
  public void setState(String state) {
    this.state = state;
  }
  
  public int getPartACoverageMos() {
    return partACoverageMos;
  }
  
  public void setPartACoverageMos(int partACoverageMos) {
    this.partACoverageMos = partACoverageMos;
  }
  
  public int getPartBCoverageMos() {
    return partBCoverageMos;
  }
  
  public void setPartBCoverageMos(int partBCoverageMos) {
    this.partBCoverageMos = partBCoverageMos;
  }
  
  public int getHmoCoverageMos() {
    return hmoCoverageMos;
  }
  
  public void setHmoCoverageMos(int hmoCoverageMos) {
    this.hmoCoverageMos = hmoCoverageMos;
  }
  
  public int getPartDCoverageMos() {
    return partDCoverageMos;
  }
  
  public void setPartDCoverageMos(int partDCoverageMos) {
    this.partDCoverageMos = partDCoverageMos;
  }

  public String[] getComorbs() {
    return comorbs;
  }
  
  public void setComorbs(String[] comorbs) {
    this.comorbs = comorbs;
  }

  public int getNumTransactions() {
    return numTransactions;
  }

  public void setNumTransactions(int numTransactions) {
    this.numTransactions = numTransactions;
  }
}
