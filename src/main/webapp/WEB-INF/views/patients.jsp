<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
  <head>
    <title>ClaimIntel :: Patients List</title>
  </head>
  <body>
    <h2>Patients in Selected Member Cohort</h2>

    <b><c:out value="${start + 1}"/>-<c:out value="${start + 25}"/></b> of <b>${numPatients}</b> results.
    <hr/>

    <ol start="<c:out value='${start + 1}'/>">
    <c:forEach items="${patients}" var="p">
      <li><p><b>[${p.desynpufId}]</b> ${p.age} year old ${p.race} ${p.sex} from ${p.state} (<a href="/timeline.html?pid=${p.desynpufId}&${filterStr}">${p.numTransactions} transactions)</a><br/><b>Coverages:</b> Part A: ${p.partACoverageMos}mo.; Part B: ${p.partBCoverageMos}mo.; HMO: ${p.hmoCoverageMos}mo.; Part D: ${p.partDCoverageMos}mo.<br/><b>Comorbidities:</b> <c:out value="${fn:join(p.comorbs, ', ')}"/></p></li>
    </c:forEach>
    </ol>

    <c:if test="${not empty prev}"><a href="/patients.html?start=${prev}&${filterStr}">Prev</a>&nbsp;||&nbsp;</c:if>
    <c:if test="${not empty next}"><a href="/patients.html?start=${next}&${filterStr}">Next</a>&nbsp;||&nbsp;</c:if>
    <a href="/population.html?${filterStr}">Back to Demographics</a>

  </body>
</html>
