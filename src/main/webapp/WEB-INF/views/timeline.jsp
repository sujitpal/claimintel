<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
  <head>
    <title>ClaimIntel :: Patient Timeline</title>
  </head>
  <body>
    <h2>Patient Timeline</h2>
    <p>Timeline for <b>[${patient.desynpufId}]</b>, a ${patient.age} year old ${patient.race} ${patient.sex} from ${patient.state}.<br/><b>Coverages:</b> Part A: ${patient.partACoverageMos}mo., Part B: ${patient.partBCoverageMos}mo., HMO: ${patient.hmoCoverageMos}mo., Part D: ${patient.partDCoverageMos}mo.<br/><b>Comorbidities:</b> <c:out value="${fn:join(patient.comorbs, ', ')}"/></p><hr/> 

    <table cellspacing="2" cellpadding="2" border="1" width="100%">
      <tr>
        <td width="8%"><b>Type</b></td>
        <td width="8%"><b>Claim From</b></td>
        <td width="8%"><b>Claim Thru</b></td>
        <td width="8%"><b>Provider</b></td>
        <td width="8%"><b>Amount Paid</b></td>
        <td width="20%"><b>ICD-9 Diag Codes</b></td>
        <td width="20%"><b>ICD-9 Proc Codes</b></td>
        <td width="20%"><b>HCPCS Codes</b></td>
      </tr>
      <c:forEach items="${transactions}" var="t">
      <tr>
        <td>${t.ioro}</td>
        <td><fmt:formatDate type="date" value="${t.claimFrom}"/></td>
        <td><fmt:formatDate type="date" value="${t.claimThru}"/></td>
        <td>${t.provider}</td>
        <td><fmt:formatNumber type="currency" value="${t.clmPmtAmt}"/></td>
        <td><c:out value="${fn:join(t.diagCodes, ', ')}"/></td>
        <td><c:out value="${fn:join(t.prcCodes, ', ')}"/></td>
        <td><c:out value="${fn:join(t.hcpcsCodes, ', ')}"/></td>
      </tr>
      </c:forEach>
    </table><hr/>

    <a href="/patients.html?${filterStr}">Back to Patient Cohort</a>&nbsp;||&nbsp;
    <a href="/population.html?${filterStr}">Back to Demographics</a>

  </body>
</html>
