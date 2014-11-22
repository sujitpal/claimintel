<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
  <head>
    <title>ClaimIntel :: Claim Payment Distribution</title>
  </head>
  <body>
    <h2>Distribution of Claim Payments</h2>
    <hr/>

    <form method="GET" action="/costs.html">

      <c:forEach items="${filters}" var="pf">
        <input type="hidden" name="${pf.name}" value="${pf.value}"/>
      </c:forEach>

      <table cellspacing="3" cellpadding="3" border="0" width="100%">
        <tr>
          <td valign="top" width="50%">
            <input type="radio" name="ttype" value="I" <c:if test="${ttype eq 'I'}">checked</c:if>/>Inpatient Only</br/>
            <input type="radio" name="ttype" value="O" <c:if test="${ttype eq 'O'}">checked</c:if>/>Outpatient Only</br/>
            <input type="radio" name="ttype" value="B" <c:if test="${ttype eq 'B'}">checked</c:if>/>Both</br/>
          </td>
          <td valign="top" width="50%">
            Lower Bound: $<input type="text" name="costlb" value="${costlb}"/><br/>
            Upper Bound: $<input type="text" name="costub" value="${costub}"/><br/>
          </td>
        </tr>
      </table>

      <table cellspacing="3" cellpadding="3" border="0" width="100%">
        <tr>
          <td valign="top" width="50%"><img src="/chart.html?type=bar&data=${clm_pmt_amt.encodedData}&height=1000&csort=int&title=Claim+Payment+Amounts"/></td>
          <td valign="top" width="50%">
            <b>Statistics</b><br/>
            <table cellspacing="0" cellpadding="0" border="1">
              <tr>
                <td><b>Min</b></td>
                <td><b>Max</b></td>
                <td><b>Count</b></td>
                <td><b>Mean</b></td>
                <td><b>StdDev</b></td>
              </tr>
              <tr>
                <td><fmt:formatNumber type="number" maxFractionDigits="2" value="${clm_pmt_amt_stats.min}"/></td>
                <td><fmt:formatNumber type="number" maxFractionDigits="2" value="${clm_pmt_amt_stats.max}"/></td>
                <td><fmt:formatNumber type="number" maxFractionDigits="2" value="${clm_pmt_amt_stats.count}"/></td>
                <td><fmt:formatNumber type="number" maxFractionDigits="2" value="${clm_pmt_amt_stats.mean}"/></td>
                <td><fmt:formatNumber type="number" maxFractionDigits="2" value="${clm_pmt_amt_stats.stddev}"/></td>
              </tr>
            </table><br/>
            <b>Distribution</b><br/>
            <table cellspacing="0" cellpadding="0" border="1">
              <tr>
                <td><b>Amount</b></td>
                <td><b>Frequency</b></td>
                <td><b>Relative Frequency (%)</b></td>
              </tr>
              <c:forEach items="${clm_pmt_amt.stats}" var="dstat">
              <tr>
                <td>${dstat.name}</td>
                <td><fmt:formatNumber type="number" maxFractionDigits="0" value="${dstat.count}"/></td>
                <td><fmt:formatNumber type="percent" maxFractionDigits="2" value="${dstat.pcount}"/>
              </tr>
              </c:forEach>
              <tr>
                <td><b>Total</b></td>
                <td><b><fmt:formatNumber type="number" maxFractionDigits="0" value="${clm_pmt_amt.total}"/></b></td>
                <td><b>100.00%</b></td>
              </tr>
            </table>
          </td>
        </tr>
      </table>

       <input type="submit" name="submit" value="Filter"/>&nbsp;||&nbsp;
       <a href="/population.html?${filterStr}">Clear Filter</a>
     </form> 
  </body>
</html>

