<%@ page contentType="text/html;charset=UTF-8" language="java" session="true" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!doctype html>
<!-- The DOCTYPE declaration above will set the     -->
<!-- browser's rendering engine into                -->
<!-- "Standards Mode". Replacing this declaration   -->
<!-- with a "Quirks Mode" doctype is not supported. -->

<html>
<head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">

    <!--                                                               -->
    <!-- Consider inlining CSS to reduce the number of requested files -->
    <!--                                                               -->
    <link type="text/css" rel="stylesheet" href="smatchweb.css">

    <!--                                           -->
    <!-- Any title is fine                         -->
    <!--                                           -->
    <title>S-Match Web</title>

    <!--                                           -->
    <!-- This script loads your compiled module.   -->
    <!-- If you add any GWT meta tags, they must   -->
    <!-- be added before this line.                -->
    <!--                                           -->
    <script type="text/javascript" language="javascript" src="smatchweb/smatchweb.nocache.js"></script>
</head>

<!--                                           -->
<!-- The body can have arbitrary html, or      -->
<!-- you can leave the body empty if you want  -->
<!-- to create a completely dynamic UI.        -->
<!--                                           -->
<body>

<!-- OPTIONAL: include this if you want history support -->
<iframe src="javascript:''" id="__gwt_historyFrame" tabIndex='-1'
        style="position:absolute;width:0;height:0;border:0"></iframe>

<!-- RECOMMENDED if your web app will not function without JavaScript enabled -->
<noscript>
    <div style="width: 22em; position: absolute; left: 50%; margin-left: -11em; color: red; background-color: white; border: 1px solid red; padding: 4px; font-family: sans-serif">
        Your web browser must have JavaScript enabled
        in order for this application to display correctly.
    </div>
</noscript>

<h1>Web Application Starter Project</h1>

<table align="center">
    <tr>
        <td colspan="2" style="font-weight:bold;">Please enter your task id:</td>
    </tr>
    <tr>
        <td id="taskFieldContainer"></td>
        <td id="sendButtonContainer"></td>
    </tr>
    <tr>
        <td colspan="2" style="color:red;" id="errorLabelContainer"></td>
    </tr>
    <tr>
        <td id="sendTaskButtonContainer"></td>
    </tr>
</table>
<%@ include file="include/analytics.jsp" %>
</body>
</html>