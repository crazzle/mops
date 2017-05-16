var httprequestStatus = false;
var active = false;
var divID = false;

function handleStatus(ID,userID){
	divID = ID;
	active = window.setInterval('doRequestStatus("status.do","'+userID+'")', 2000);
}

// doRequest(url) creates a request object (depending on the browser)
// and sends a request to the passed URL
function doRequestStatus(url, userID) {
	httprequestStatus = false;
	// Test whether the used browser can provide a XMLHttpRequest object
	// (Mozilla and other)
	if (window.XMLHttpRequest) {
		httprequestStatus = new XMLHttpRequest();
		if (httprequestStatus.overrideMimeType) {
			// The result should be an XML-document
			httprequestStatus.overrideMimeType('text/xml');
		}
		// If the browser is Internet Explorer, different objects need to be used
	} else if (window.ActiveXObject) {
		try {
			httprequestStatus = new ActiveXObject("Msxml2.XMLHTTP");
		} catch (e) {
			try {
				httprequestStatus = new ActiveXObject("Microsoft.XMLHTTP");
			} catch (e) {}
		}
	}

	// If no request object is provided at all, this won't work - give an alert.
	if (!httprequestStatus) {
		window.clearInterval(active);
		alert('Es kann kein Request Objekt erzeugt werden.');
		return false;
	}
	// A callback function to react to events from the request object:
	parameter = "user="+userID;
	
	httprequestStatus.open('POST', url,true);
	
	httprequestStatus.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	
	httprequestStatus.setRequestHeader("Connection", "close");
	
	httprequestStatus.onreadystatechange = myStatusHandler;
	
	httprequestStatus.send(parameter);
}


function myStatusHandler() {
	if(httprequestStatus.readyState == 2){
		document.getElementById(divID).innerHTML='<div id="spinner"><img src="grafiken/loading.gif" height="19px"></div>';
	    }	
	if (httprequestStatus.readyState == 4) {
		// Success? 
		if (httprequestStatus.status == 200) {
			setStatus();
		} else {
			window.clearInterval(active);
			document.getElementById(divID).innerHTML="Status Service not available";
		}
	}
}

function setStatus(){
	str = httprequestStatus.responseText;
	statusAr = str.split("$!");
	round = parseInt(statusAr[4]);
	done = false;
	
	
	if(statusAr[0].search("false") != -1){
		if ((statusAr[1].search("false") != -1) && round <= 0){
			done = true;
		}
		else if(statusAr[1].search("true") != -1){
			document.getElementById("buttonDiv").innerHTML =	"	<a class='whiteButton' type='submit' onclick='getImage(\"statistic\",\""+userID+"\")' href='#statistic'>Statistic</a>" +
																"	<br>" +
																"	<a class='grayButton' id='yesButton' type='submit' href='#n' style='color: gray;'>Yes</a>" +
																"	<br>" +
																"	<a class='grayButton' id='noButton' type='submit' href='#n' style='color: gray;'>No</a>";
			document.getElementById(divID).innerHTML="Please wait...";
		} 
		else if ((statusAr[1].search("false") != -1) && round > 0){
			document.getElementById("buttonDiv").innerHTML =	"	<a class='whiteButton' type='submit' onclick='getImage(\"statistic\",\""+userID+"\")' href='#statistic'>Statistic</a>" +
																"	<br>" +
																"	<a class='whiteButton' id='yesButton' type='submit' href='#yes' onclick='negotiateProject(\"" + userID + "\", \"user-1\", \"yes\")'>Yes</a>" +
																"	<br>" +
																"	<a class='whiteButton' id='noButton' type='submit' href='#no' onclick='negotiateProject(\"" + userID + "\", \"user-1\", \"no\")'>No</a>";
			document.getElementById(divID).innerHTML="Please respond.";
		}
		
	} 
	else if(statusAr[0].search("true") != -1){
		if ((statusAr[1].search("false") != -1) && round <= 0){
			done = true;
		}
		else if(statusAr[1].search("false") != -1 && round > 0){
			document.getElementById("buttonDiv").innerHTML =	"	<a class='whiteButton' type='submit' onclick='getImage(\"statistic\",\""+userID+"\")' href='#statistic'>Statistic</a>" +
																"	<br>" +
																"	<a class='whiteButton' id='yesButton' type='submit' href='#yes' onclick='negotiateProject(\"" + userID + "\", \"user-1\", \"yes\")'>Yes</a>" +
																"	<br>" +
																"	<a class='whiteButton' id='noButton' type='submit' href='#no' onclick='negotiateProject(\"" + userID + "\", \"user-1\", \"no\")'>No</a>";
			document.getElementById(divID).innerHTML="Please respond.";
		}
		else if(statusAr[1].search("true") != -1){
			document.getElementById("buttonDiv").innerHTML =	"	<a class='whiteButton' type='submit' onclick='getImage(\"statistic\",\""+userID+"\")' href='#statistic'>Statistic</a>" +
																"	<br>" +
																"	<a class='grayButton' id='yesButton' type='submit' href='#n' style='color: gray;'>Yes</a>" +
																"	<br>" +
																"	<a class='grayButton' id='noButton' type='submit' href='#n' style='color: gray;'>No</a>";
			document.getElementById(divID).innerHTML="Please wait...";
		}
		
	}
	else{
		document.getElementById(divID).innerHTML=str;
	}	
	if(!done){
		document.getElementById("optduration").innerHTML = "Current delay: " + statusAr[2];
		document.getElementById("newduration").innerHTML = "New delay: " + statusAr[3];
		calcProbability(statusAr[2]-statusAr[3], statusAr[4]);
	}else if(done){
		window.clearInterval(active);
		
		document.getElementById("user-1").title = "Result";
		document.getElementById("user-1").innerHTML = statusAr[5];
	}
}