var startpage = {};

window.onload = function() {
	var subPath=document.location.hash.split('#')[1];
	if (typeof subPath != 'undefined') {
		console.log("Loading subPath: "+subPath);
		startpage.setContent(subPath);		
	}
}

startpage.resizeIframe = function(obj) {	
	try {
		var win = obj.contentWindow || obj.contentDocument;
		obj.style.height = win.document.body.offsetHeight + 50 + 'px';
	} catch(err) {
		console.log('An error occurred during optimising size of iFrame - using default settings instead.')
	}
};

startpage.setContent = function(path) {
	$("#mainContent").attr("src", path);
};

startpage.openRestDemos = function() {
	startpage.setContent('./clientExample/client.html');
    $("#submenuRest").attr("hidden", false);
};

startpage.openSolutionDemos = function() {
    $("#submenuSolutionDemos").attr("hidden", false);
};