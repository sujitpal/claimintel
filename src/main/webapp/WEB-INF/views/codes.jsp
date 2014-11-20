<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
  <head>
    <title>ClaimIntel :: Codes Distributions</title>
  </head>
  <body>
    <h2>Distribution of Codes for Selected Member Cohort</h2>
    <hr/>

    <form method="GET" action="/codes.html">
      
      <c:forEach items="${filters}" var="pf">
        <input type="hidden" name="${pf.name}" value="${pf.value}"/>
      </c:forEach>

      <input type="radio" name="ttype" value="I" <c:if test="${ttype eq 'I'}">checked</c:if>/>Inpatient Only<br/>
      <input type="radio" name="ttype" value="O" <c:if test="${ttype eq 'O'}">checked</c:if>/>Outpatient Only<br/>
      <input type="radio" name="ttype" value="B" <c:if test="${ttype eq 'B'}">checked</c:if>/>Both<br/>
      <hr/>

      <h3>ICD-9 Diagnostic Codes</h3>
      <table cellspacing="3" cellpadding="3" border="0" width="100%">
        <tr>
          <td width="50%" valign="top"><img src="/chart.html?type=bar&data=${icd9_dgns_cds.encodedData}&height=1500&csort=count"/></td>
          <td width="50%" valign="top">
            <table cellspacing="1" cellpadding="0" border="1">
              <tr>
                <td><b>Code</b></td>
                <td><b>Frequency</b></td>
                <td><b>Relative Frequency (%)</b></td>
              </tr>
              <c:forEach items="${icd9_dgns_cds.stats}" var="stat">
              <tr>
                <td><a href="http://www.cms.gov/medicare-coverage-database/staticpages/icd-9-code-lookup.aspx?KeyWord=${stat.name}">${stat.name}</a></td>
                <td><fmt:formatNumber type="number" maxFractionDigits="0" value="${stat.count}"/></td>
                <td><fmt:formatNumber type="percent" maxFractionDigits="2" value="${stat.pcount}"/></td>
              </tr>
              </c:forEach>
              <tr>
                <td><b>Total</b></td>
                <td><b><fmt:formatNumber type="number" maxFractionDigits="0" value="${icd9_dgns_cds.total}"/></b></td>
                <td><b>100.00%</b></td>
              </tr>
            </table>
          </td>
        </tr>
      </table>
  
      <h3>ICD-9 Procedure Codes</h3>
      <table cellspacing="3" cellpadding="3" border="0" width="100%">
        <tr>
          <td width="50%" valign="top"><img src="/chart.html?type=bar&data=${icd9_prcdr_cds.encodedData}&height=1500&csort=count"/></td>
          <td width="50%" valign="top">
            <table cellspacing="1" cellpadding="0" border="1">
              <tr>
                <td><b>Code</b></td>
                <td><b>Frequency</b></td>
                <td><b>Relative Frequency (%)</b></td>
              </tr>
              <c:forEach items="${icd9_prcdr_cds.stats}" var="stat">
              <tr>
                <td><a href="http://www.cms.gov/medicare-coverage-database/staticpages/icd-9-code-lookup.aspx?KeyWord=${stat.name}">${stat.name}</a></td>
                <td><fmt:formatNumber type="number" maxFractionDigits="0" value="${stat.count}"/></td>
                <td><fmt:formatNumber type="percent" maxFractionDigits="2" value="${stat.pcount}"/></td>
              </tr>
              </c:forEach>
              <tr>
                <td><b>Total</b></td>
                <td><b><fmt:formatNumber type="number" maxFractionDigits="0" value="${icd9_prcdr_cds.total}"/></b></td>
                <td><b>100.00%</b></td>
              </tr>
            </table>
          </td>
        </tr>
      </table>
  
      <c:if test="${ttype ne 'I'}">
        <h3>HCPCS Codes</h3>
        <table cellspacing="3" cellpadding="3" border="0" width="100%">
          <tr>
            <td width="50%" valign="top"><img src="/chart.html?type=bar&data=${hcpcs_cds.encodedData}&height=1500&csort=count"/></td>
            <td width="50%" valign="top">
              <table cellspacing="1" cellpadding="0" border="1">
                <tr>
                  <td><b>Code</b></td>
                  <td><b>Frequency</b></td>
                  <td><b>Relative Frequency (%)</b></td>
                </tr>
                <c:forEach items="${hcpcs_cds.stats}" var="stat">
                <tr>
                  <td><a href="http://www.findacode.com/code.php?set=CPT&c=${stat.name}">${stat.name}</a></td>
                  <td><fmt:formatNumber type="number" maxFractionDigits="0" value="${stat.count}"/></td>
                  <td><fmt:formatNumber type="percent" maxFractionDigits="2" value="${stat.pcount}"/></td>
                </tr>
                </c:forEach>
                <tr>
                  <td><b>Total</b></td>
                  <td><b><fmt:formatNumber type="number" maxFractionDigits="0" value="${hcpcs_cds.total}"/></b></td>
                  <td><b>100.00%</b></td>
                </tr>
              </table>
            </td>
          </tr>
        </table>
      </c:if>

      <input type="submit" name="submit" value="Filter by Patient Type"/>&nbsp;||&nbsp;
      <a href="/population.html?${filterStr}">Clear Patient Type Filter</a>
      
    </form>
    
  </body>
</html>

