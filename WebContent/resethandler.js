var http_requestReset = false;

function reset(){
	dorequestReset("reset.do");
	isUser = false;
	limit = false;
	window.clearInterval(active);
	
}

// doRequest(url) creates a request object (depending on the browser)
// and sends a request to the passed URL
function dorequestReset(url) {
	http_requestReset = false;
	// Test whether the used browser can provide a XMLHttpRequest object
	// (Mozilla and other)
	if (window.XMLHttpRequest) {
		http_requestReset = new XMLHttpRequest();
		if (http_requestReset.overrideMimeType) {
			// The result should be an XML-document
			http_requestReset.overrideMimeType('text/xml');
		}
		// If the browser is Internet Explorer, different objects need to be used
	} else if (window.ActiveXObject) {
		try {
			http_requestReset = new ActiveXObject("Msxml2.XMLHTTP");
		} catch (e) {
			try {
				http_requestReset = new ActiveXObject("Microsoft.XMLHTTP");
			} catch (e) {}
		}
	}

	// If no request object is provided at all, this won't work - give an alert.
	if (!http_requestReset) {
		alert('Es kann kein Request Objekt erzeugt werden.');
		return false;
	}
	
	// A callback function to react to events from the request object:
	http_requestReset.open('POST', url,true);
	http_requestReset.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	http_requestReset.setRequestHeader("Connection", "close");
	http_requestReset.onreadystatechange = myResetHandler;
	http_requestReset.send(null);
}


function myResetHandler() {
	if (http_requestReset.readyState == 4) {
		// Success?
		if (http_requestReset.status == 200) {
			setReset();
		}
	}
}

function setReset(){
	var resp = http_requestReset.responseText;
	usrAr = resp.split("$!");
	document.getElementById("liID1").innerHTML = "<a href='#user' onclick='getUser(\"id1\",\"user\")'>"+usrAr[0]+"</a>";
	document.getElementById("liID2").innerHTML = "<a href='#user' onclick='getUser(\"id2\",\"user\")'>"+usrAr[1]+"</a>";
}