window.addEventListener("load", () => {
    jsf.ajax.addOnEvent(function (data) {
        if (data.status === "success") {
            queueMicrotask(() => {
                let extensionNodeList = data.responseXML.querySelectorAll("extension#example");
                if (extensionNodeList.length === 1) {
                    let extensionNode = extensionNodeList.item(0);
                    let clientIds = JSON.parse(extensionNode.textContent);
                    clientIds.forEach((clientId) => {
                        console.log("extension client id: " + clientId);
                    });
                }
            });
        }
    });
});
