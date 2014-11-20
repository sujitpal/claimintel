package com.mycompany.claimintel.dtos;

import java.util.List;

public class PatientGroup {

  private Patient patient;
  private List<PatientTransaction> transactions;

  public Patient getPatient() {
    return patient;
  }
  
  public void setPatient(Patient patient) {
    this.patient = patient;
  }
  
  public List<PatientTransaction> getTransactions() {
    return transactions;
  }
  
  public void setTransactions(List<PatientTransaction> transactions) {
    this.transactions = transactions;
  }
}
