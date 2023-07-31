class ExampleWebComponent extends HTMLElement {
    constructor() {
        super();
        console.log("constructor");
    }
    connectedCallback() {
        console.log("connectedCallback");
        let value = this.getAttribute("data-value");
        this.innerHTML = value;
        jsf.ajax.addOnEvent(this.jsfAjaxEvent);
    }
    jsfAjaxEvent(data) {
        console.log("JSF AJAX event");
        console.log(data);
    }
    disconnectedCallback() {
        console.log("disconnectedCallback");
    }
    adoptedCallback() {
        console.log("adoptedCallback");
    }
    attributeChangedCallback() {
        console.log("attributeChangedCallback");
    }
}
customElements.define("example-web-component", ExampleWebComponent);

