<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		
		<script type="text/javascript" language="JavaScript" src="statushandler.js"></script>
		<script type="text/javascript" language="JavaScript" src="userhandler.js"></script>
		<script type="text/javascript" language="JavaScript" src="negotiationhandler.js"></script>
		<script type="text/javascript" language="JavaScript" src="imagehandler.js"></script>
		<script type="text/javascript" language="JavaScript" src="resethandler.js"></script>
		<script type="text/javascript" language="JavaScript">var switchList = new Array();</script>
		
		
		<meta name="viewport" content="width=device-width; initial-scale=1.0; maximum-scale=1.0; user-scalable=0;"/>
		<link rel="icon" type="image/png" href="grafiken/iui-favicon.png">
		<link rel="apple-touch-icon" href="grafiken/iui-logo-touch-icon.png" />
  		
  		<meta name="apple-mobile-web-app-capable" content="yes" />
 		<link rel="stylesheet" href="css/iui.css" type="text/css" />
  		<link rel="stylesheet" title="Default" href="css/default-theme.css"  type="text/css"/>
  		<link rel="stylesheet" href="css/iui-panel-list.css" type="text/css" />
  		
  		<script type="application/x-javascript" src="js/iui.js"></script>
		
		<title>MOPS 1.0 - Multi Office Project Scheduling</title>
		
	</head>
	<body>
		<div class="toolbar">
	        <h1 id="pageTitle"></h1>
	        <a id="backButton" class="button" href="#"></a>
    	</div>
		
		<ul id="home" title="MOPS 1.0" selected="true">
			
			<div style="text-align:center; padding:5%;">
				<i>Please identify yourself by choosing your name,<br />
			 	to negotiate a project delay.</i>
			</div>
			<li></li>
			<li id="liID1"><a href="#user" onclick="getUser('id1','user')">${initParam.id1}</a></li>
			<li id="liID2"><a href="#user" onclick="getUser('id2','user')">${initParam.id2}</a></li> 
			<li id="heyho" class="whiteButton" onclick="reset()">Reset</li>
		</ul>
		
		
		
		<ul id="user" title="User">
		  
		      
		</ul>
		
		<ul id="user-1" title="Negotiate">
			
		</ul>
		
		<ul id="statistic" title="Statistics"></ul>
		
	</body>
</html>