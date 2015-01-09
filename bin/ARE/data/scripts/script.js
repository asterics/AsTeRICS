// the script is provided with the following external vars:
// output:   an array of size 8 representing 8 IRuntimeOutputPorts
// eventout: an array of size 8 representing 8 IRuntimeEventTriggererPorts
// property: an array of size 8 holding strings with the property inputs from the components property fields 

function clazz(dataout, evout) {
 
	this.dataInput = function(in_nb, in_data) {
		in_data = in_data.concat(property[in_nb]);
		str = new java.lang.String(in_data);
		output[in_nb].sendData(str.getBytes());
	};
	
	this.eventInput = function(ev_nb) {
		eventout[ev_nb].raiseEvent();
	};
};

var scriptclass = new clazz();
