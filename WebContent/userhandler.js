var http_requestUser = false;
var tagID = false;
var userID = false;
var isUser = false;

function getUser(ID, tag){
	userID = ID;
	tagID = tag;
	if(isUser == false){
		isUser = true;
		handleStatus('status', userID);
	}
	doRequestUser("user.do");
	
	if(userID == "id1"){
		document.getElementById("liID2").innerHTML = "<span style='color:gray;'>P.Mgr.2</span>";
	}
	else if(userID == "id2"){
		document.getElementById("liID1").innerHTML = "<span style='color:gray;'>P.Mgr.1</span>";
	}
}

// doRequest(url) creates a request object (depending on the browser)
// and sends a request to the passed URL
function doRequestUser(url) {
	http_requestUser = false;
	// Test whether the used browser can provide a XMLHttpRequest object
	// (Mozilla and other)
	if (window.XMLHttpRequest) {
		http_requestUser = new XMLHttpRequest();
		if (http_requestUser.overrideMimeType) {
			// The result should be an XML-document
			http_requestUser.overrideMimeType('text/xml');
		}
		// If the browser is Internet Explorer, different objects need to be used
	} else if (window.ActiveXObject) {
		try {
			http_requestUser = new ActiveXObject("Msxml2.XMLHTTP");
		} catch (e) {
			try {
				http_requestUser = new ActiveXObject("Microsoft.XMLHTTP");
			} catch (e) {}
		}
	}

	// If no request object is provided at all, this won't work - give an alert.
	if (!http_requestUser) {
		alert('Es kann kein Request Objekt erzeugt werden.');
		return false;
	}
	
	parameter = "user="+userID;
	// A callback function to react to events from the request object:
	http_requestUser.open('POST', url,true);
	http_requestUser.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	http_requestUser.setRequestHeader("Content-length", parameter.length);
	http_requestUser.setRequestHeader("Connection", "close");
	http_requestUser.onreadystatechange = myUserHandler;
	http_requestUser.send(parameter);
}


function myUserHandler() {
	if(http_requestUser.readyState == 2){
		document.getElementById(tagID).innerHTML='<div id="spinner" align="center"><img src="grafiken/loading.gif" height="19px"></div>';
	    }	
	if (http_requestUser.readyState == 4) {
		// Success?
		if (http_requestUser.status == 200) {
			setUser();
			
		} else {
			document.getElementById(tagID).innerHTML="User Service not available";
		}
	}
}

function setUser(){
	var resp = http_requestUser.responseText;
	
	var respelements = resp.split("$!");
	
	document.getElementById(tagID).title = respelements[0];
	document.getElementById(tagID).innerHTML=respelements[1];
	
	listString = respelements[2];
	listAr = listString.split("p.");
	for(i = 0; i < listAr.length; i++){
		listEl=listAr[i].split(",");
		switchList[i] = new Array();
		switchList[i] = listEl;
	}
}