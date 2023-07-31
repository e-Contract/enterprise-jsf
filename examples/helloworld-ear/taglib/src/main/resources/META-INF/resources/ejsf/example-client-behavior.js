function exampleClientBehavior(event) {
    if (event.type === "mouseover") {
        event.target.classList.add("example-client-behavior-mouseover");
        return;
    }
    if (event.type === "mouseout") {
        event.target.classList.remove("example-client-behavior-mouseover");
        return;
    }
}
