window.addEventListener("load", () => {
    if (typeof jsf === "undefined") {
        return;
    }
    jsf.ajax.addOnEvent(function (data) {
        if (data.status === "success") {
            queueMicrotask(() => {
                let extension = data.responseXML.querySelector("extension#example");
                let extensionData = JSON.parse(extension.textContent);
                for (const [clientId, data] of Object.entries(extensionData)) {
                    console.log(clientId + " update data:");
                    console.log(data);
                }
            });
        }
    });
    let template = document.querySelector("template#example-extension");
    let extensionData = JSON.parse(template.content.textContent);
    for (const [clientId, data] of Object.entries(extensionData)) {
        console.log(clientId + " init data:");
        console.log(data);
    }
});
