ch_railtec_diagnostics_web_ui_vehicle_vnc_VncComponent = function () {
	var domId = this.getState().domId;
	//console.log("in vnc connector for " + domId);
	//var canvasElement = document.createElement('canvas');
	//canvasElement.setAttribute('id', domId);
	//this.getElement().appendChild(canvasElement);
    this.getElement().setAttribute('id', domId);
    var display = document.getElementById(domId);
    //console.log("vnc step 1 " + display);
    var guac = new Guacamole.Client( new Guacamole.HTTPTunnel("tunnel", this.getState().port) );
    //console.log("vnc step 2 ");
    // Add client to display div
    display.appendChild(guac.getDisplay().getElement());
    //console.log("vnc step 3 ");
    //this.getElement().appendChild(guac.getDisplay.getElement());

    guac.onerror = function(error) {
        this.getState().status = 3;
        //alert(error);

        // Pass user interaction to the server-side
        //var self = this;
        this.onError(this.getState().status);
        console.log("error " +  this.getState().status);


    };

    console.log("vnc on display" + guac.getDisplay());
    // Mouse
    var mouse = new Guacamole.Mouse(guac.getDisplay().getElement());
    mouse.onmousedown =
        mouse.onmouseup   =
            mouse.onmousemove = function(mouseState) {
                guac.sendMouseState(mouseState);
            };

// Keyboard
    var keyboard = new Guacamole.Keyboard(document);

    keyboard.onkeydown = function (keysym) {
        guac.sendKeyEvent(1, keysym);
    };

    keyboard.onkeyup = function (keysym) {
        guac.sendKeyEvent(0, keysym);
    };

// Disconnect on close
    window.onunload = function() {
        guac.disconnect();
        console.log("vnc disconnected on unload");
    }

    this.onStateChange = function () {
    console.log("vnc statechange actionType=" + this.getState().actionType);
    switch (this.getState().actionType){

        //do nothing
        case 0:
            console.log("vnc none ");
            break;
        //connect
        case 1:
            guac.connect();
            console.log("vnc connected ");
        break;
        //disconnect
        case 2:
            guac.disconnect();
            console.log("vnc disconnected ");
        break;


    }

    };


};

