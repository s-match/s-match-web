<%@page contentType="text/html;charset=UTF-8" language="java" session="false" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@include file="include/resources.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>S-Match Framework Demo Web API</title>
    <link href='//fonts.googleapis.com/css?family=Droid+Sans:400,700' rel='stylesheet' type='text/css'/>
    <link href='css/highlight.default.css' media='screen' rel='stylesheet' type='text/css'/>
    <link href='css/screen.css' media='screen' rel='stylesheet' type='text/css'/>
    <script type="text/javascript" src="lib/shred.bundle.js"></script>
    <script type="text/javascript" src="lib/url.bundle.js"></script>
    <script src='lib/jquery-1.8.0.min.js' type='text/javascript'></script>
    <script src='lib/jquery.slideto.min.js' type='text/javascript'></script>
    <script src='lib/jquery.wiggle.min.js' type='text/javascript'></script>
    <script src='lib/jquery.ba-bbq.min.js' type='text/javascript'></script>
    <script src='lib/handlebars-1.0.0.js' type='text/javascript'></script>
    <script src='lib/underscore-min.js' type='text/javascript'></script>
    <script src='lib/backbone-min.js' type='text/javascript'></script>
    <script src='lib/swagger.js' type='text/javascript'></script>
    <script src='swagger-ui.js' type='text/javascript'></script>
    <script src='lib/highlight.7.3.pack.js' type='text/javascript'></script>
    <script type="text/javascript">
        $(function () {
            window.swaggerUi = new SwaggerUi({
                url: "${appRoot}apidocs/api-docs",
                dom_id: "swagger-ui-container",
                supportedSubmitMethods: ['get', 'post'],
                onComplete: function (swaggerApi, swaggerUi) {
                    if (console) {
                        console.log("Loaded SwaggerUI")
                    }
                    $('pre code').each(function (i, e) {
                        hljs.highlightBlock(e)
                    });
                },
                onFailure: function (data) {
                    if (console) {
                        console.log("Unable to Load SwaggerUI");
                        console.log(data);
                    }
                },
                docExpansion: "none"
            });

            $('.input').hide();
            window.swaggerUi.load();
        });

    </script>
</head>

<body>
<div id='header'>
    <div class="swagger-ui-wrap">
        <a id="logo" href="http://???">S-Match Framework Demo Web API</a>

        <form id='api_selector'>
            <div class='input'><input placeholder="login" id="input_consumerKey" name="consumerKey" type="text"/></div>
            <div class='input'><input placeholder="password" id="input_consumerSecret" name="consumerSecret"
                                      type="text"/></div>
        </form>
    </div>
</div>

<div id="message-bar" class="swagger-ui-wrap">
    &nbsp;
</div>

<div id="swagger-ui-container" class="swagger-ui-wrap">

</div>
<%@ include file="include/analytics.jsp" %>
</body>

</html>
