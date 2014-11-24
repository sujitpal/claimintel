<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
  <head>
    <title>ClaimIntel :: Feature Correlations</title>
  </head>
  <body>
    <h2>Feature Correlations for Selected Member Cohort</h2>
    <hr/>

    <!-- input form -->
    <table cellspacing="1" cellpadding="1" border="1" width="100%">
      <tr>
        <td width="20%"><b>&nbsp;</b></td>
        <td width="16%" align="center"><b>Gender</b></td>
        <td width="16%" align="center"><b>Age</b></td>
        <td width="16%" align="center"><b>Ethnicity</b></td>
        <td width="16%" align="center"><b>State</b></td>
        <td width="16%" align="center"><b>Comorbidity</b></td>
      </tr>
      <tr>
        <td width="20%" align="right"><b>Gender</b></td>
        <td width="16%" align="center">&nbsp;</td>
        <td width="16%" align="center">&nbsp;</td>
        <td width="16%" align="center">&nbsp;</td>
        <td width="16%" align="center">&nbsp;</td>
        <td width="16%" align="center">&nbsp;</td>
      </tr>
      <tr>
        <td width="20%" align="right"><b>Age</b></td>
        <td width="16%" align="center"><a href="/corrs.html?xcorr=bene_age&ycorr=bene_sex&title=Age+vs+Gender&width=1000${filterStr}">Correlate</a></td>
        <td width="16%" align="center">&nbsp;</td>
        <td width="16%" align="center">&nbsp;</td>
        <td width="16%" align="center">&nbsp;</td>
        <td width="16%" align="center">&nbsp;</td>
      </tr>
      <tr>
        <td width="20%" align="right"><b>Ethnicity</b></td>
        <td width="16%" align="center"><a href="/corrs.html?xcorr=bene_race&ycorr=bene_sex&title=Ethnicity+vs+Gender&${filterStr}">Correlate</a></td>
        <td width="16%" align="center"><a href="/corrs.html?xcorr=bene_age&ycorr=bene_race&title=Age+vs+Ethnicity&width=1000&${filterStr}">Correlate</a></td>
        <td width="16%" align="center">&nbsp;</td>
        <td width="16%" align="center">&nbsp;</td>
        <td width="16%" align="center">&nbsp;</td>
      </tr>
      <tr>
        <td width="20%" align="right"><b>State</b></td>
        <td width="16%" align="center"><a href="/corrs.html?xcorr=sp_state&ycorr=bene_sex&title=Gender+vs+State&width=1000&height=800&${filterStr}">Correlate</a></td>
        <td width="16%" align="center"><a href="/corrs.html?xcorr=sp_state&ycorr=bene_age&title=Age+vs+State&width=1000&height=800&${filterStr}">Correlate</a></td>
        <td width="16%" align="center"><a href="/corrs.html?xcorr=sp_state&ycorr=bene_race&title=Ethnicity+vs+State&width=1000&height=800&${filterStr}">Correlate</a></td>
        <td width="16%" align="center">&nbsp;</td>
        <td width="16%" align="center">&nbsp;</td>
      </tr>
      <tr>
        <td width="20%" align="right"><b>Comorbidity</b></td>
        <td width="16%" align="center"><a href="/corrs.html?xcorr=bene_comorbs&ycorr=bene_sex&title=Disease+vs+Gender&width=1000&${filterStr}">Correlate</a></td>
        <td width="16%" align="center"><a href="/corrs.html?xcorr=bene_comorbs&ycorr=bene_age&title=Age+vs+Disease&width=1000&height=600&${filterStr}">Correlate</a></td>
        <td width="16%" align="center"><a href="/corrs.html?xcorr=bene_comorbs&ycorr=bene_race&title=Age+vs+Ethnicity&width=1000&${filterStr}">Correlate</a></td>
        <td width="16%" align="center"><a href="/corrs.html?xcorr=bene_comorbs&ycorr=sp_state&title=Disease+vs+State&width=1000&height=800&${filterStr}">Correlate</a></td>
        <td width="16%" align="center">&nbsp;</td>
      </tr>
    </table>
    <hr/>

    <img src="/chart.html?type=bubble&data=${corrdataEncoded}"/>

    <hr/>
    <a href="/population.html?${filterStr}">Back to Demographic Page</a>
  </body>
</html>

