var http_requestNeg = false;
var negTagID = false;
var negUserID = false;
var negResponse = false;
var limit = false;

function negotiateProject(user, tag, response){
	negUserID = user;
	negTagID = tag;
	negResponse = response;
	generateList();
	doRequestNegotiation("negotiate.do");
}

// doRequest(url) creates a request object (depending on the browser)
// and sends a request to the passed URL
function doRequestNegotiation(url) {
	http_requestNeg = false;
	// Test whether the used browser can provide a XMLHttpRequest object
	// (Mozilla and other)
	if (window.XMLHttpRequest) {
		http_requestNeg = new XMLHttpRequest();
		if (http_requestNeg.overrideMimeType) {
			// The result should be an XML-document
			http_requestNeg.overrideMimeType('text/xml');
		}
		// If the browser is Internet Explorer, different objects need to be used
	} else if (window.ActiveXObject) {
		try {
			http_requestNeg = new ActiveXObject("Msxml2.XMLHTTP");
		} catch (e) {
			try {
				http_requestNeg = new ActiveXObject("Microsoft.XMLHTTP");
			} catch (e) {}
		}
	}

	// If no request object is provided at all, this won't work - give an alert.
	if (!http_requestNeg) {
		alert('Es kann kein Request Objekt erzeugt werden.');
		return false;
	}
	
	list = "";
	for(i = 0; i < switchList.length; i++){
		if(i!=0){
			list+=",";
		}
		for(j = 0; j < switchList[i].length; j++){
				if(j<switchList[i].length-1){
					list+=switchList[i][j]+",";
				}
				else{
					list+=switchList[i][j];
				}
		}
	}
	
	
	
	
	parameter = "user="+negUserID + "&resp=" + negResponse + "&switchlist="+list;
	// A callback function to react to events from the request object:
	http_requestNeg.open('POST', url,true);
	http_requestNeg.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	http_requestNeg.setRequestHeader("Content-length", parameter.length);
	http_requestNeg.setRequestHeader("Connection", "close");
	http_requestNeg.onreadystatechange = myNegotiationHandler;
	http_requestNeg.send(parameter);
}


function myNegotiationHandler() {
	if(http_requestNeg.readyState == 2){
		document.getElementById(negTagID).innerHTML='<div id="spinner" align="center"><img src="grafiken/loading.gif" height="19px"></div>';
	    }	
	if (http_requestNeg.readyState == 4) {
		// Success?
		if (http_requestNeg.status == 200) {
			setNegotiation();
			
		} else {
			document.getElementById(negTagID).innerHTML="Negotiation Service not available";
		}
	}
}

function setNegotiation(){
	var resp = http_requestNeg.responseText;
	var respelements = resp.split("$!");
	if(respelements[0]!="finish"){
		document.getElementById(negTagID).innerHTML=respelements[0];
		var actRound = parseInt(respelements[2]);
		var deltavalue = parseInt(respelements[1]);
		var delta = "";
			
		calcProbability(deltavalue, actRound);
	}
	else{
		document.getElementById("user-1").title = "Result";
		document.getElementById("user-1").innerHTML = statusAr[5];
		var periods = respelements[6].split("P.");
		
		var lines = "";
		for(var z = 1; z < periods.length; z++){
				lines += z + ". Period:&emsp;" + periods[z] + "<br>";
		}
		
		document.getElementById("periods").innerHTML = lines;
		document.getElementById("result").innerHTML = "";
	}
		
}

function calcProbability(deltavalue, actRound){
	var probability; 
		
	if(deltavalue > 0){	
		delta = deltavalue + " Day(s) <img src='grafiken/thumb_up.png' border='0'>";
		deltavalue = 0;
	} else if (deltavalue < 0){
		deltavalue = 0 - deltavalue;
		delta = deltavalue + " Day(s) <img src='grafiken/thumb_down.png' border='0'>";
	} else {
		delta = deltavalue + " Day(s) <img src='grafiken/thumb_right.png' border='0'>";
	}
	
	document.getElementById("delta").innerHTML=delta;
	
	probability = new Number(Math.exp(-(deltavalue/actRound)));
	probability = probability.toFixed(2);
	
	if(limit == false){
		limit = probability/2;
		limit = limit.toFixed(2);
	}
	
	if(probability >= limit){
		document.getElementById("recom").innerHTML = "Capability: " + parseInt(probability*100) + "%<br>Recommendation: <img src='grafiken/accept.png' border='0'>";
	} else {
		document.getElementById("recom").innerHTML = "Capability: " + parseInt(probability*100) + "%<br>Recommendation: <img src='grafiken/cancel.png' border='0'>";
	}
	
}

function generateList(){
	
	randLevel = Math.round(Math.random() * (switchList.length - 1));
	while(switchList[randLevel].length <= 1){
		randLevel = Math.floor(Math.random() * (switchList.length - 1));
	}
	sub1 = Math.round(Math.random() * (switchList[randLevel].length - 1));
	sub2 = Math.round(Math.random() * (switchList[randLevel].length - 1));
	
	temp = switchList[randLevel][sub1];
	switchList[randLevel][sub1] = switchList[randLevel][sub2];
	switchList[randLevel][sub2] = temp;
}