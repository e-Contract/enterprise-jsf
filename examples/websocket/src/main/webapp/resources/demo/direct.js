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
        let webSocket = new WebSocket("ws://localhost" + port + "/websocket-demo/websocket/" + group);
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
