<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
  <head>
    <title>ClaimIntel :: Population Statistics</title>
  </head>
  <body>
    <h2>Distribution of Member Population across different dimensions</h2>
    <hr/>

    <form method="GET" action="/population.html">

      <p>
      <h3>Distribution by Gender</h3>
      <table cellspacing="3" cellpadding="0" border="0" width="100%">
        <tr>
          <td width="50%" valign="top">
            <img src="/chart.html?type=bar&data=${bene_sex.encodedData}&height=200"/>
          </td>
          <td width="50%" valign="top">
            <table cellspacing="1" cellpadding="0" border="1">
              <tr>
                <td><b>Gender</b></td>
                <td><b>Frequency</b></td>
                <td><b>Relative Frequency (%)</b></td>
                <td><b>Filter</b></td>
              </tr>
              <c:forEach items="${bene_sex.stats}" var="stat">
              <tr>
                <td>${stat.name}</td>
                <td><fmt:formatNumber type="number" maxFractionDigits="0" value="${stat.count}"/></td>
                <td><fmt:formatNumber type="percent" maxFractionDigits="2" value="${stat.pcount}"/></td>
                <td><input type="checkbox" name="bene_sex" value="${stat.name}" <c:if test="${stat.selected}">checked</c:if> /></td>
              </tr>
              </c:forEach>
              <tr>
                <td><b>Total</b></td>
                <td><b><fmt:formatNumber type="number" maxFractionDigits="0" value="${bene_sex.total}"/></b></td>
                <td><b>100.00%</b></td>
                <td/>
              </tr>
            </table><br/><br/>
          </td>
        </tr>
      </table>
      </p>

      <p>
      <h3>Distribution by Age</h3>
      <table cellspacing="3" cellpadding="0" border="0" width="100%">
        <tr>
          <td width="50%" valign="top">
            <img src="/chart.html?type=bar&data=${bene_age.encodedData}&csort=range"/>
          </td>
          <td width="50%" valign="top">
            <table cellspacing="1" cellpadding="0" border="1">
              <tr>
                <td><b>Age</b></td>
                <td><b>Frequency</b></td>
                <td><b>Relative Frequency (%)</b></td>
                <td><b>Filter</b></td>
              </tr>
              <c:forEach items="${bene_age.stats}" var="stat">
              <tr>
                <td>${stat.name}</td>
                <td><fmt:formatNumber type="number" maxFractionDigits="0" value="${stat.count}"/></td>
                <td><fmt:formatNumber type="percent" maxFractionDigits="2" value="${stat.pcount}"/></td>
                <td><input type="checkbox" name="bene_age" value="${stat.name}" <c:if test="${stat.selected}">checked</c:if> /></td>
              </tr>
              </c:forEach>
              <tr>
                <td><b>Total</b></td>
                <td><b><fmt:formatNumber type="number" maxFractionDigits="0" value="${bene_sex.total}"/></b></td>
                <td><b>100.00%</b></td>
                <td/>
              </tr>
            </table><br/><br/>
          </td>
        </tr>
      </table>
      </p>

      <p>
      <h3>Distribution by Ethnicity</h3>
      <table cellspacing="3" cellpadding="0" border="0" width="100%">
        <tr>
          <td width="50%" valign="top">
            <img src="/chart.html?type=bar&data=${bene_race.encodedData}"/>
          </td>
          <td width="50%" valign="top">
            <table cellspacing="1" cellpadding="0" border="1">
              <tr>
                <td><b>Ethnicity</b></td>
                <td><b>Frequency</b></td>
                <td><b>Relative Frequency (%)</b></td>
                <td><b>Filter</b></td>
              </tr>
              <c:forEach items="${bene_race.stats}" var="stat">
              <tr>
                <td>${stat.name}</td>
                <td><fmt:formatNumber type="number" maxFractionDigits="0" value="${stat.count}"/></td>
                <td><fmt:formatNumber type="percent" maxFractionDigits="2" value="${stat.pcount}"/></td>
                <td><input type="checkbox" name="bene_race" value="${stat.name}" <c:if test="${stat.selected}">checked</c:if> /></td>
              </tr>
              </c:forEach>
              <tr>
                <td><b>Total</b></td>
                <td><b><fmt:formatNumber type="number" maxFractionDigits="0" value="${bene_race.total}"/></b></td>
                <td><b>100.00%</b></td>
                <td/>
              </tr>
            </table><br/><br/>
          </td>
        </tr>
      </table>
      </p>

      <p>
      <h3>Distribution by State</h3>
      <table cellspacing="3" cellpadding="0" border="0" width="100%">
        <tr>
          <td width="50%" valign="top">
            <img src="/chart.html?type=bar&data=${sp_state.encodedData}&height=1200"/>
          </td>
          <td width="50%" valign="top">
            <table cellspacing="1" cellpadding="0" border="1">
              <tr>
                <td><b>State</b></td>
                <td><b>Frequency</b></td>
                <td><b>Relative Frequency (%)</b></td>
                <td><b>Filter</b></td>
              </tr>
              <c:forEach items="${sp_state.stats}" var="stat">
              <tr>
                <td>${stat.name}</td>
                <td><fmt:formatNumber type="number" maxFractionDigits="0" value="${stat.count}"/></td>
                <td><fmt:formatNumber type="percent" maxFractionDigits="2" value="${stat.pcount}"/></td>
                <td><input type="checkbox" name="sp_state" value="${stat.name}" <c:if test="${stat.selected}">checked</c:if> /></td>
              </tr>
              </c:forEach>
              <tr>
                <td><b>Total</b></td>
                <td><b><fmt:formatNumber type="number" maxFractionDigits="0" value="${sp_state.total}"/></b></td>
                <td><b>100.00%</b></td>
                <td/>
              </tr>
            </table><br/><br/>
          </td>
        </tr>
      </table>
      </p>

      <p>
      <h3>Distribution by Disease</h3>
      <table cellspacing="3" cellpadding="0" border="0" width="100%">
        <tr>
          <td width="50%" valign="top">
            <img src="/chart.html?type=bar&data=${bene_comorbs.encodedData}"/>
          </td>
          <td width="50%" valign="top">
            <table cellspacing="1" cellpadding="0" border="1">
              <tr>
                <td><b>Disease</b></td>
                <td><b>Frequency</b></td>
                <td><b>Relative Frequency (%)</b></td>
                <td><b>Filter</b></td>
              </tr>
              <c:forEach items="${bene_comorbs.stats}" var="stat">
              <tr>
                <td>${stat.name}</td>
                <td><fmt:formatNumber type="number" maxFractionDigits="0" value="${stat.count}"/></td>
                <td><fmt:formatNumber type="percent" maxFractionDigits="2" value="${stat.pcount}"/></td>
                <td><input type="checkbox" name="bene_comorbs" value="${stat.name}" <c:if test="${stat.selected}">checked</c:if> /></td>
              </tr>
              </c:forEach>
              <tr>
                <td><b>Total</b></td>
                <td><b><fmt:formatNumber type="number" maxFractionDigits="0" value="${bene_comorbs.total}"/></b></td>
                <td><b>${bene_comorbs.total}</b></td>
                <td><b>100.00%</b></td>
                <td/>
              </tr>
            </table><br/><br/>
          </td>
        </tr>
      </table>
      </p>

      <input type="submit" value="Filter by Demographic"/>&nbsp;||&nbsp;
      <a href="/population.html">Clear Demographic Filters</a>&nbsp;||&nbsp;
      <a href="/mortality.html?${filters}">Mortality</a>&nbsp;||&nbsp;
      <a href="/codes.html?${filters}">Codes</a>&nbsp;||&nbsp;
      <a href="/costs.html?${filters}">Costs</a>&nbsp;||&nbsp;
      <a href="/patients.html?${filters}">Patients</a>

    </form>

  </body>
</html>
