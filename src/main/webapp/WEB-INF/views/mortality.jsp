<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
  <head>
    <title>ClaimIntel :: Cohort Mortality Statistics</title>
  </head>
  <body>
    <h2>Distribution of Age at Death for Selected Member Cohort</h2>
    <hr/>
    
      <table cellspacing="3" cellpadding="0" border="0" width="100%">
        <tr>
          <td width="50%" valign="top">
            <img src="/chart.html?type=bar&data=${age_at_death.encodedData}&csort=range"/>
          </td>
          <td width="50%" valign="top">
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
                <td><fmt:formatNumber type="number" maxFractionDigits="2" value="${age_at_death_stats.min}"/></td>
                <td><fmt:formatNumber type="number" maxFractionDigits="2" value="${age_at_death_stats.max}"/></td>
                <td><fmt:formatNumber type="number" maxFractionDigits="2" value="${age_at_death_stats.count}"/></td>
                <td><fmt:formatNumber type="number" maxFractionDigits="2" value="${age_at_death_stats.mean}"/></td>
                <td><fmt:formatNumber type="number" maxFractionDigits="2" value="${age_at_death_stats.stddev}"/></td>
              </tr>
            </table><br/>
            <b>Distribution</b><br/>
            <table cellspacing="1" cellpadding="0" border="1">
              <tr>
                <td><b>Age at Death</b></td>
                <td><b>Frequency</b></td>
                <td><b>Relative Frequency (%)</b></td>
              </tr>
              <c:forEach items="${age_at_death.stats}" var="stat">
              <tr>
                <td>${stat.name}</td>
                <td><fmt:formatNumber type="number" maxFractionDigits="0" value="${stat.count}"/></td>
                <td><fmt:formatNumber type="percent" maxFractionDigits="2" value="${stat.pcount}"/></td>
              </tr>
              </c:forEach>
              <tr>
                <td><b>Total</b></td>
                <td><b><fmt:formatNumber type="number" maxFractionDigits="0" value="${age_at_death.total}"/></b></td>
                <td><b>100.00%</b></td>
                <td/>
              </tr>
            </table><br/><br/>
          </td>
        </tr>
      </table>
      </p>

      <a href="/population.html?${filters}">Back to Demographics</a>

  </body>
</html>
