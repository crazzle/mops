var http_requestImage = false;
var imagetagID = false;
var imageUserID = false;
function getImage(tag,userID){
	imagetagID = tag;
	imageUserID = userID;
	doRequestImage("image.do");
}

function doRequestImage(url) {
	http_requestImage = false;
	// Test whether the used browser can provide a XMLHttpRequest object
	// (Mozilla and other)
	if (window.XMLHttpRequest) {
		http_requestImage = new XMLHttpRequest();
		if (http_requestImage.overrideMimeType) {
			// The result should be an XML-document
			http_requestImage.overrideMimeType('text/xml');
		}
		// If the browser is Internet Explorer, different objects need to be used
	} else if (window.ActiveXObject) {
		try {
			http_requestImage = new ActiveXObject("Msxml2.XMLHTTP");
		} catch (e) {
			try {
				http_requestImage = new ActiveXObject("Microsoft.XMLHTTP");
			} catch (e) {}
		}
	}

	// If no request object is provided at all, this won't work - give an alert.
	if (!http_requestImage) {
		alert('Es kann kein Request Objekt erzeugt werden.');
		return false;
	}
	
	parameter = "user="+imageUserID;
	// A callback function to react to events from the request object:
	http_requestImage.open('POST', url,true);
	http_requestImage.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	http_requestImage.setRequestHeader("Content-length", parameter.length);
	http_requestImage.setRequestHeader("Connection", "close");
	http_requestImage.onreadystatechange = myImageHandler;
	http_requestImage.send(parameter);
}


function myImageHandler() {
	if(http_requestImage.readyState == 2){
		document.getElementById(imagetagID).innerHTML='<div id="spinner" align="center"><img src="grafiken/loading.gif" height="19px"></div>';
	    }	
	if (http_requestImage.readyState == 4) {
		// Success?
		if (http_requestImage.status == 200) {
			setImage();
		} else {
			document.getElementById(imagetagID).innerHTML="Image Service not available";
		}
	}
}

function setImage(){
	var resp = http_requestImage.responseText;
	document.getElementById(imagetagID).innerHTML=resp;
}