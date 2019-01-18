var demo1, demo4, demo5;
var demo1_circuit, demo4_circuit, demo5_circuit;
var demo2, demo2_led, demo2_circuit, demo2_animation, demo2_circuit_led, demo2_circuit_status=false;
var demo3, demo2_led, demo3_circuit, demo3_animation, demo3_circuit_led, demo3_circuit_text, demo3_circuit_status=false, demo3_dutycycle=100;
var demo4_graph, demo4_chart, demo4_graph_data, demo4_graph_data_y, demo4_animation, demo4_idx;
var demo5, demo5_rotor, demo5_circuit, demo5_circuit_text, demo5_status=false, demo5_degree=0;

var animation_default_speed = 1000;

function defaultSuccessCallback(data, status) { 
    // console.log("Success message: "+data); 
}
function defaultErrorCallback(status, e) { 
    // alert("Error message: "+ e);
    // alert("Connection with ARE failed.\n Check if your ARE is running on your machine and retry.");
    console.log("Connection with ARE failed.\n Check if your ARE is running on your machine and retry. Error: "+e);
}
function defaultUpdatePropertyCallback (data, status) {
    if (JSON.parse(data).length == 0) {
        alert("The property settings could not be applied.");
    }
    console.log('The following properties could be set: ' + data);
}
function load_svg(id) {
    item_obj = document.getElementById(id);
    try {
        item_svg = item_obj.getSVGDocument();
    } catch (item_svg) {
        item_svg = item_obj.contentDocument;
    }
    return item_svg;
}
function toggle_button_state(btn) {
    $(btn).find('[data-fa-i2svg]')
    .toggleClass('fa-play')
    .toggleClass('fa-stop')
    .toggleClass('button-start')
    .toggleClass('button-stop');
}
function demo4_init_socket() {
    var wsUri = "ws://localhost:8082/ws/astericsData";
    websocket = new WebSocket(wsUri);

    websocket.onopen = function(evt) {
        // noop
    };

    websocket.onclose = function(evt) {
        // noop
    };

    websocket.onmessage = function(evt) {
        // demo4_graph.data = Number(evt.data);
        demo4_graph_data_y = Number(evt.data);
    }

    websocket.onerror = function(evt) {
        defaultErrorCallback();
    }
}
function demo4_get_cfg(text, color) {
    function initArray(max) {
        var result = [];

        for (var i = 0; i < max; i++) {
            result.push({
                x: i*20,
                y: 0
            });
        }

        return result;
    }

    function initLabels(max) {
        var result = [];

        for (var i = 0; i < max; i++) {
            result.push(String(i*20));
        }

        return result;
    }

    var demo4_graph_cfg = {
        type: 'line',
        data: {
            labels: initLabels(1176),
            datasets: [{
                data: initArray(1176),
                type: 'line',
                pointRadius: 0,
                lineTension: 0,
                borderColor: 'rgb(48, 179, 218)',
                backgroundColor: 'rgba(156, 209, 225, 0.4)',
            }]
        },
        options: {
            responsive: true,
            legend: {
                display: false
            },
            title: {
                display: true,
                fontColor: color,
                text: text,
            },
            layout: {
                padding: {
                    left: 10,
                    right: 0,
                    top: 0,
                    bottom: 20
                }
            },
            scales: {
                xAxes: [{
                    display: true,
                    scaleLabel: {
                        display: true,
                        labelString: 'Time (ms)',
                        fontStyle: 'bold'
                    },
                    ticks: {
                        // Custom x axis label
                        callback: function(value, index, values) {
                            return parseFloat(20 * index).toFixed(0);// + " ms";
                        },
                        autoSkip: true,
                        maxTicksLimit: 20,
                        stepSize: 2000
                    }
                }],
                yAxes: [{
                    scaleLabel: {
                        display: true,
                        labelString: 'Voltage level (V)'
                    },
                    ticks: {
                        min: 0,
                        max: 1200,
                        maxTicksLimit: 11,
                        stepSize: 200,
                    }
                }]
            }
        }
    };

    return demo4_graph_cfg;
}
function demo4_chart_init() {
    demo4_graph = document.getElementById("demo4_graph_canvas").getContext('2d');
    demo4_graph.canvas.width = 1000;
    demo4_graph.canvas.height = 600;

    demo4_chart = new Chart(demo4_graph, demo4_get_cfg('Demo 4: ADC','black'));   
    
    demo4_init_socket();
}
function demo4_start() {
    var xmlHTTP = new XMLHttpRequest();
    xmlHTTP.open("GET","models/ArduinoADC.acs",false);
    xmlHTTP.send(null);
    
    uploadModel(defaultSuccessCallback, defaultErrorCallback, xmlHTTP.responseText);
    startModel(defaultSuccessCallback, defaultErrorCallback);

    demo4_chart = new Chart(demo4_graph, demo4_get_cfg('Demo 4: ADC - active','green'));
    demo4_chart.update();

    demo4_idx = 0;
    demo4_chart.config.data.datasets[0].data.length = 0;
    demo4_chart.update();
    demo4_animation = setInterval(demo4_update,20);
}
function demo4_update() {

    // Charts gets updated all 20ms. 
    // Reset chart data after `(1176` - 1) * 20ms (=23.5s) to avoid buffer overflows.
    if (demo4_chart.config.data.datasets[0].data.length > 1176) {
        demo4_chart.config.data.datasets[0].data = [];
        demo4_chart.config.data.datasets[0].data.length = 0;
        demo4_chart.update();
    }

    var t = new Date();
    var sec = t.getSeconds();
    var milis = t.getMilliseconds();
    var sec_milis = sec * 1000 + milis;
    
    var n = demo4_chart.config.data.datasets[0].data.length;
    demo4_chart.data.datasets[0].data[n] = {
        x: n * 20,
        y: demo4_graph_data_y
    };
    demo4_chart.update();
}
function demo4_stop() {
    
    var store_data = demo4_chart.config.data.datasets[0].data;
    demo4_chart = new Chart(demo4_graph, demo4_get_cfg('Demo 4: ADC - stopped','red'));
    demo4_chart.data.datasets[0].data = store_data;
    
    demo4_chart.update();

    // Remove demo 4
    clearInterval(demo4_animation);
    stopModel(defaultSuccessCallback, defaultErrorCallback); 
}

/*
 * Default Animation
 */
window.addEventListener("load", function load(event) {

    // Remove listener again. One-time execution.
    window.removeEventListener("load", load, false); 

    // Demo 1: Digital Input
    demo1 = load_svg("svg_demo1");
    demo1_circuit = load_svg("svg_demo1_circuit");
    
    $('#demo1_start').on('click', function () {
        toggle_button_state(this);

        // Play
        if ($(this).find('[data-fa-i2svg]').hasClass('fa-stop')) {
            var xmlHTTP = new XMLHttpRequest();
            xmlHTTP.open("GET","models/ArduinoDigitalInput.acs",false);
            xmlHTTP.send(null);
            
            uploadModel(defaultSuccessCallback, defaultErrorCallback, xmlHTTP.responseText);
            startModel(defaultSuccessCallback, defaultErrorCallback);
            
        } else 
        // Stop
        if ($(this).find('[data-fa-i2svg]').hasClass('fa-play')) {
            stopModel(defaultSuccessCallback, defaultErrorCallback); 
        }
    });

    // Demo 2: Digital Ouput    
    demo2 = load_svg("svg_demo2");
    demo2_circuit = load_svg("svg_demo2_circuit");

    $('#demo2_start').on('click', function () {

        toggle_button_state(this);

        // Play
        if ($(this).find('[data-fa-i2svg]').hasClass('fa-stop')) {
            var xmlHTTP = new XMLHttpRequest();
            xmlHTTP.open("GET","models/ArduinoDigitalOutput.acs",false);
            xmlHTTP.send(null);
            
            uploadModel(defaultSuccessCallback, defaultErrorCallback, xmlHTTP.responseText);
            startModel(defaultSuccessCallback, defaultErrorCallback);
                        
        } else 
        // Stop
        if ($(this).find('[data-fa-i2svg]').hasClass('fa-play')) {
            stopModel(defaultSuccessCallback, defaultErrorCallback); 
        }
    });

    // Load svg elements
    demo2_led = demo2.getElementById("demo2_led");
    demo2_circuit_led = demo2_circuit.getElementById("demo2_led");
    
    // Set demo 2 default animation
    demo2_animation = setInterval(function demo2_animation() {
        if (demo2_circuit_status) {
            demo2_circuit_status = false;
            demo2_led.setAttribute("fill","rgb(90%,20%,20%)");
            demo2_circuit_led.setAttribute("fill","red");
        } else {
            demo2_circuit_status = true;
            demo2_circuit_led.setAttribute("fill","white");
            demo2_led.setAttribute("fill","rgb(45%,45%,45%)");
        }
    },animation_default_speed);

    // Demo 3: PWM
    demo3 = load_svg("svg_demo3");
    demo3_circuit = load_svg("svg_demo3_circuit");

    $('#demo3_start').on('click', function () {

        toggle_button_state(this);

        // Play
        if ($(this).find('[data-fa-i2svg]').hasClass('fa-stop')) {
            var xmlHTTP = new XMLHttpRequest();
            xmlHTTP.open("GET","models/ArduinoPWM.acs",false);
            xmlHTTP.send(null);
            
            uploadModel(defaultSuccessCallback, defaultErrorCallback, xmlHTTP.responseText);
            startModel(defaultSuccessCallback, defaultErrorCallback);

        } else 
        // Stop
        if ($(this).find('[data-fa-i2svg]').hasClass('fa-play')) {
            stopModel(defaultSuccessCallback, defaultErrorCallback); 
        }
    });

    // Load svg elements
    demo3_led = demo3.getElementById("demo3_led");
    demo3_circuit_led = demo3_circuit.getElementById("demo3_led");
    demo3_slider = document.getElementById("demo3slider");
    document.getElementById("demo3label").innerHTML = "Duty Cycle: " + demo3_slider.value + "%";

    // Create text (status: duty cycle)
    demo3_circuit_text = document.createElementNS("http://www.w3.org/2000/svg", 'text');
    demo3_circuit_text.setAttribute("x","90");
    demo3_circuit_text.setAttribute("y","83");
    demo3_circuit_text.setAttribute("style","font-style:normal;font-weight:normal;font-size:24px;line-height:125%;font-family:sans-serif;letter-spacing:0px;word-spacing:0px;fill:#000000;fill-opacity:1;stroke:none;stroke-width:1px;stroke-linecap:butt;stroke-linejoin:miter;stroke-opacity:1");
    demo3_circuit.getElementById("demo3circuitlayer").appendChild(demo3_circuit_text);
    
    // Set demo 3 default animation
    demo3_animation = setInterval(function demo3_animation() {
        gb_value = 100 - demo3_dutycycle;
        rgb_value = "rgb(100%," + gb_value + "%," + gb_value + "%)";

        gb_value_ard = 20 + 25 * (gb_value/100);
        r_value_ard = 45 + 45 * ((100-gb_value)/100);
        rgb_value_ard = "rgb(" + r_value_ard + "%," + gb_value_ard + "%," + gb_value_ard + "%)";

        demo3_led.setAttribute("fill",rgb_value_ard);
        demo3_circuit_led.setAttribute("fill",rgb_value);
        demo3_circuit_text.innerHTML = String(demo3_dutycycle) + "%";
        
        if (demo3_dutycycle > 0) {
            demo3_dutycycle -= 10; 
        } else {
            demo3_dutycycle = 100;
        }
    },animation_default_speed);

    // Demo 4: ADC
    demo4 = load_svg("svg_demo4");
    demo4_circuit = load_svg("svg_demo4_circuit");
    demo4_chart_init();

    $('#demo4_start').on('click', function () {

        toggle_button_state(this);

        // Play
        if ($(this).find('[data-fa-i2svg]').hasClass('fa-stop')) {
            demo4_start();
            
        } else 
        // Stop
        if ($(this).find('[data-fa-i2svg]').hasClass('fa-play')) {
            demo4_stop();
        }
    });
    
    // Demo 5: Servo PWM
    demo5 = load_svg("svg_demo5");
    demo5_circuit = load_svg("svg_demo5_circuit");

    $('#demo5_start').on('click', function () {

        toggle_button_state(this);

        // Play
        if ($(this).find('[data-fa-i2svg]').hasClass('fa-stop')) {
            var xmlHTTP = new XMLHttpRequest();
            xmlHTTP.open("GET","models/ArduinoServoPWM.acs",false);
            xmlHTTP.send(null);
            
            uploadModel(defaultSuccessCallback, defaultErrorCallback, xmlHTTP.responseText);
            startModel(defaultSuccessCallback, defaultErrorCallback);

        } else 
        // Stop
        if ($(this).find('[data-fa-i2svg]').hasClass('fa-play')) {
            stopModel(defaultSuccessCallback, defaultErrorCallback); 
        }
    });

    // demo4_animation = setInterval(function demo4_animation() {
    //     // noop
    // },animation_default_speed);

    // Load svg elements
    demo5_rotor = demo5.getElementById("demo5_rotor");
    demo5_slider = document.getElementById("demo5slider");

    // Create text (status: duty cycle)
    demo5_circuit_text = document.createElementNS("http://www.w3.org/2000/svg", 'text');
    demo5_circuit_text.setAttribute("x","90");
    demo5_circuit_text.setAttribute("y","50");
    demo5_circuit_text.setAttribute("style","font-style:normal;font-weight:normal;font-size:24px;line-height:125%;font-family:sans-serif;letter-spacing:0px;word-spacing:0px;fill:#000000;fill-opacity:1;stroke:none;stroke-width:1px;stroke-linecap:butt;stroke-linejoin:miter;stroke-opacity:1");
    demo5_circuit.getElementById("demo5circuitlayer").appendChild(demo5_circuit_text);

    // Set demo 5 default animation
    demo5_animation = setInterval(function demo5_animation() {
        
        $(demo5_rotor).attr({transform: "rotate(" + demo5_degree + " 200 362.36221)"});
        demo5_circuit_text.innerHTML = String(demo5_degree) + "°";
        if (demo5_degree < 90) {
            demo5_degree += 10;
        } else {
            demo5_degree = 0;
        }
    },animation_default_speed);
    
});

/* 
 * Demo 2: Digital Output
 */
function demo2_application(id) {
    // Remove startup animation
    clearInterval(demo2_animation);

    if (id == "demo2buttonon") {
        demo2_circuit_led.setAttribute("fill","red");
        demo2_led.setAttribute("fill","rgb(90%,20%,20%)");

        triggerEvent(defaultSuccessCallback, defaultErrorCallback, 'Arduino.1', 'setPin2');

    } else if (id == "demo2buttonoff") {
        demo2_circuit_led.setAttribute("fill","white");
        demo2_led.setAttribute("fill","rgb(45%,45%,45%)");

        triggerEvent(defaultSuccessCallback, defaultErrorCallback, 'Arduino.1', 'clearPin2');

    }
}

/* 
 * Demo 3: PWM
 */
function demo3_application(id) {
    // Remove startup animation
    clearInterval(demo3_animation);

    slider = document.getElementById("demo3slider");
    label = document.getElementById("demo3label");
    dutycycle = slider.value;

    if (id == "demo3buttonon") {
        demo3_circuit_status = true;
    } else if (id == "demo3buttonoff") {
        demo3_circuit_status = false;
    } else if (id == "demo3slider") {
    }
    
    label.innerHTML = "Duty Cycle: " + dutycycle + "%";
    
    var demo3_property_map;

    if (demo3_circuit_status) {
        // Color value circuit
        gb_value = 100 - dutycycle;
        rgb_value = "rgb(100%," + gb_value + "%," + gb_value + "%)";

        // Color value arduino
        gb_value_ard = 20 + 25 * (gb_value/100);
        r_value_ard = 45 + 45 * ((100-gb_value)/100);
        rgb_value_ard = "rgb(" + r_value_ard + "%," + gb_value_ard + "%," + gb_value_ard + "%)";

        sendDataToInputPort(defaultSuccessCallback, defaultErrorCallback, 'Arduino.1', 'pwm3', String(dutycycle*255/100));
        triggerEvent(defaultSuccessCallback, defaultErrorCallback, 'ConstantDispatcher.1', 'dispatchSlot1');
    } else {
        rgb_value = "rgb(100%,100%,100%)";
        rgb_value_ard = "rgb(45%,45%,45%)";

        // No ErrorCallback if application was not started
        sendDataToInputPort(defaultSuccessCallback, defaultErrorCallback, 'Arduino.1', 'pwm3', "0");
        triggerEvent(defaultSuccessCallback, defaultErrorCallback, 'ConstantDispatcher.1', 'dispatchSlot2');
    }
    demo3_circuit_text.innerHTML = String(dutycycle) + "%";
    demo3_led.setAttribute("fill",rgb_value_ard);
    demo3_circuit_led.setAttribute("fill",rgb_value);

}

/*
 * Demo 4: ADC
 */
function demo4_application(id) {
    // Remove startup animation
    clearInterval(demo3_animation);

    if (id == "demo4buttonon") {
        triggerEvent(defaultSuccessCallback, defaultErrorCallback, 'Arduino.1', 'setPin2');

    } else if (id == "demo4buttonoff") {
        triggerEvent(defaultSuccessCallback, defaultErrorCallback, 'Arduino.1', 'clearPin2');
    } 
}

/*
 * Demo 5: Servo PWM
 */
function demo5_application(id) {
    // Remove startup application
    clearInterval(demo5_animation);

    slider = document.getElementById("demo5slider");
    label = document.getElementById("demo5label");
    degree = slider.value;
    
    if (id == "demo5buttonon") {
        demo5_status = true;
    } else if (id == "demo5buttonoff") {
        demo5_status = false;
    } else if (id == "demo5slider") {
    }

    label.innerHTML = "Angle: " + degree + "°";
    demo5_circuit_text.innerHTML = String(degree) + "°";
    
    if (demo5_status) {
        sendDataToInputPort(defaultSuccessCallback, defaultErrorCallback, 'Arduino.1', 'pwm3', String(degree*255/90));
    } else {
        degree = 0; 

        // No ErrorCallback if application was not started
        sendDataToInputPort(defaultSuccessCallback, defaultErrorCallback, 'Arduino.1', 'pwm3', "0");
    }
    
    $(demo5_rotor).attr({transform: "rotate(-" + degree + " 200 362.36221)"}); 
}
