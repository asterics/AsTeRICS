/**
 * JS file that provides a simple map object
 */

function Map() {
	this.map = new Object();
	
	this.get = function (key) {
        return this.map[key];
    }

	
	this.add = function (newKey, newValue) {
		this.map[newKey] = newValue;
    }
	
	
	this.remove = function (key) {
		if (key in this.map) {
			temp = this.map[key];
			delete this.map[key];
			return temp;
		}
		else {
			return null;
		}
    }
	
	this.containsKey = function (key) {
		if (key in this.map) {
			return true;
		}
		else {
			return false;
		}
	}
	
	this.size = function () {
	    var size = 0;
	    for (key in this.map) {
	        if (this.map.hasOwnProperty(key)) {
	        	size++;
	        }
	    }
	    return size;
	}

	this.clear = function () {
		this.map = new Object();
	}
	
	this.consolePrint = function () {
		for (var key in this.map) {
			if (this.map.hasOwnProperty(key)) {
				console.log("[" +key + ": " + this.map[key] + "]");
			}
		}
	}
	
	this.keySet = function () {
		var keys = [];
		i = 0;
		for (keys[i++] in this.map) {}
		
		return keys;
	}
	
}

	
/*
for (var key in _map) {
	if (_map.hasOwnProperty(key)) {
		if (key == newKey) {
			_map[key] = newValue;
		}
	}
}*/

