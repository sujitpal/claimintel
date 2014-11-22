package com.mycompany.claimintel.dtos;

import java.util.Date;

public class PatientTransaction {

  private String ioro;
  private String claimId;
  private Date claimFrom;
  private Date claimThru;
  private String provider;
  private Float clmPmtAmt;
  private String[] diagCodes;
  private String[] prcCodes;
  private String[] hcpcsCodes;
  
  public String getIoro() {
    return ioro;
  }
  
  public void setIoro(String ioro) {
    this.ioro = ioro;
  }
  
  public String getClaimId() {
    return claimId;
  }
  
  public void setClaimId(String claimId) {
    this.claimId = claimId;
  }
  
  public Date getClaimFrom() {
    return claimFrom;
  }
  
  public void setClaimFrom(Date claimFrom) {
    this.claimFrom = claimFrom;
  }
  
  public Date getClaimThru() {
    return claimThru;
  }
  
  public void setClaimThru(Date claimThru) {
    this.claimThru = claimThru;
  }
  
  public String getProvider() {
    return provider;
  }
  
  public void setProvider(String provider) {
    this.provider = provider;
  }
  
  public Float getClmPmtAmt() {
    return clmPmtAmt;
  }
  
  public void setClmPmtAmt(Float clmPmtAmt) {
    this.clmPmtAmt = clmPmtAmt;
  }
  
  public String[] getDiagCodes() {
    return diagCodes;
  }
  
  public void setDiagCodes(String[] diagCodes) {
    this.diagCodes = diagCodes;
  }
  
  public String[] getPrcCodes() {
    return prcCodes;
  }
  
  public void setPrcCodes(String[] prcCodes) {
    this.prcCodes = prcCodes;
  }
  
  public String[] getHcpcsCodes() {
    return hcpcsCodes;
  }

  public void setHcpcsCodes(String[] hcpcsCodes) {
    this.hcpcsCodes = hcpcsCodes;
  }
}
