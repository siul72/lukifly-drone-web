ch_railtec_diagnostics_web_ui_vehicle_instruments_gauge_Gauge = function () {

    var domId = this.getState().domId;
    console.log("in gauge connector for " + domId);
    var canvasElement = document.createElement('canvas');
    canvasElement.setAttribute('id', domId);
    this.getElement().appendChild(canvasElement);

    //get json string for highlight
    var h = JSON.parse(this.getState().highlights);
    //parse it
    console.log("New json data" + h);
    //send it

    var gauge = new Gauge({
        renderTo: domId,
        gwidth: this.getState().gsize,
        gheight: this.getState().gsize,
        title: this.getState().title,
        units: this.getState().units,
        maxValue: this.getState().maxValue,
        minValue: this.getState().minValue,
        majorTicks: this.getState().majorTicks,
        minorTicks: this.getState().minorTicks,
        highlights: h,
        value: this.getState().value


    });


    gauge.draw();

    this.onStateChange = function () {

        switch (this.getState().changeType) {
            case 0:
                gauge.setValue(this.getState().value);
                break;
            case 1:
                break;
            case 2:
                gauge.setAlarm(this.getState().alarm);
                break;
            case 3:
                gauge.setTitle(this.getState().title);
                break;

        }


    };
};

