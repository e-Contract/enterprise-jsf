function onMessage(message, channel, event) {
    console.log("onMessage");
    console.log("message: " + message);
    console.log("channel: " + channel);
    console.log("event: " + event);
    console.log(event);
    let outputElement = document.getElementById("output");
    outputElement.innerHTML = message;
}

function onOpen(channel) {
    console.log("onOpen");
    console.log("channel: " + channel);
}
