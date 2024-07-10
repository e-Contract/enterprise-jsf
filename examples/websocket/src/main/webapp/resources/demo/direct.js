class ReconnectingWebSocket {
    static #MAX_RECONNECT_ATTEMPTS = 20;
    static #RECONNECT_INTERVAL = 500;

    constructor(url) {
        this.eventListeners = new Map();
        this.reconnectAttempts = 0;
        this.url = url;
        this.#open();
    }

    #open() {
        let $this = this;
        this.webSocket = new WebSocket(this.url);
        this.webSocket.addEventListener("close", function (event) {
            $this.#closeCallback(event);
        });
        this.#proxyEvents("open");
        this.#proxyEvents("error");
        this.#proxyEvents("message");
    }

    #proxyEvents(type) {
        let $this = this;
        this.webSocket.addEventListener(type, function (event) {
            $this.#callEventListeners(type, event);
        });
    }

    #closeCallback(event) {
        console.log("close callback");
        console.log("code: " + event.code);
        console.log("reason: " + event.reason);
        if (this.reconnectAttempts >= ReconnectingWebSocket.#MAX_RECONNECT_ATTEMPTS) {
            this.#callEventListeners("close", event);
            return;
        }
        if (event.code === 1008) {
            // VIOLATED_POLICY
            this.#callEventListeners("close", event);
            return;
        }
        let delay = this.reconnectAttempts * ReconnectingWebSocket.#RECONNECT_INTERVAL;
        this.reconnectAttempts++;
        let $this = this;
        setTimeout(function () {
            console.log("trying to reconnect");
            $this.#open();
        }, delay);
    }

    #callEventListeners(type, event) {
        if (!this.eventListeners.has(type)) {
            return;
        }
        let callbacks = this.eventListeners.get(type);
        callbacks.forEach((callback) => {
            callback(event);
        });
    }

    addEventListener(type, callback) {
        if (!this.eventListeners.has(type)) {
            let callbacks = [];
            this.eventListeners.set(type, callbacks);
        }
        let callbacks = this.eventListeners.get(type);
        callbacks.push(callback);
    }
}

document.addEventListener("DOMContentLoaded", () => {
    let connectButton = document.getElementById("connectButton");
    connectButton.addEventListener("click", () => {
        let groupInput = document.getElementById("groupInput");
        let group = groupInput.value;
        if ("" === group) {
            console.log("missing group");
            return;
        }
        let port = window.location.port;
        if ("" !== port) {
            port = ":" + port;
        }
        let webSocket = new ReconnectingWebSocket("ws://localhost" + port + "/websocket-demo/websocket/" + group);
        webSocket.addEventListener("message", (event) => {
            let output = document.getElementById("output");
            output.innerHTML = output.innerHTML + event.data + "<br/>";
        });
        webSocket.addEventListener("open", () => {
            console.log("websocket open");
            refreshGroupsDataTable();
        });
        webSocket.addEventListener("error", () => {
            console.log("websocket error");
        });
        webSocket.addEventListener("close", (event) => {
            console.log("websocket closed");
            console.log("code: " + event.code);
            console.log("reason: " + event.reason);
        });
    });

    let clearOutputButton = document.getElementById("clearOutputButton");
    clearOutputButton.addEventListener("click", () => {
        let output = document.getElementById("output");
        output.innerHTML = "";
    });
});
