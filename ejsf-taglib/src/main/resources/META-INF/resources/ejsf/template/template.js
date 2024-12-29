/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */


class EJSFTemplate {

    element;
    template;
    callback;
    document;

    constructor(element, template, callback) {
        this.element = element;
        this.template = template;
        this.callback = callback;
    }

    init() {
        console.log("init");
        let domParser = new DOMParser();
        this.document = domParser.parseFromString(this.template, "application/xml");
        console.log(this.document);
        this.element.innerHTML = "";
        this.toComponents(this.document.documentElement, this.element);
    }

    toComponents(parentNode, parentComponent, inputComponentIndex = 0) {
        let childNodes = parentNode.childNodes;
        for (var idx = 0; idx < childNodes.length; idx++) {
            let childNode = childNodes[idx];
            switch (childNode.nodeType) {
                case Node.TEXT_NODE:
                    parentComponent.appendChild(document.createTextNode(childNode.textContent));
                    break;
                case Node.ELEMENT_NODE:
                    if (childNode.localName === "xref") {
                        parentComponent.appendChild(document.createTextNode(childNode.getAttribute("id").toUpperCase()));
                    } else if (childNode.localName === "assignment") {
                        let input = document.createElement("input");
                        input.setAttribute("type", "text");
                        input.size = 60;
                        input.className = "ui-inputfield ui-inputtext ui-widget ui-state-default ui-corner-all";
                        let inputId = "input-" + inputComponentIndex;
                        inputComponentIndex++;
                        childNode.setAttribute("ejsf-input", inputId);
                        input.dataset.ejsfInput = inputId;
                        let $this = this;
                        input.addEventListener("input", function (event) {
                            console.log("input: " + input.value);
                            childNode.setAttribute("ejsf-input-value", input.value);
                            $this.invokeCallback();
                        });
                        input.addEventListener("focus", function (event) {
                            input.classList.add("ui-state-focus");
                        });
                        input.addEventListener("blur", function (event) {
                            input.classList.remove("ui-state-focus");
                        });
                        input.addEventListener("mouseover", function (event) {
                            input.classList.add("ui-state-hover");
                        });
                        input.addEventListener("mouseout", function (event) {
                            input.classList.remove("ui-state-hover");
                        });
                        parentComponent.appendChild(input);
                    } else if (childNode.localName === "list") {
                        let type = childNode.getAttribute("type");
                        if ("itemized" === type) {
                            let ul = document.createElement("ul");
                            parentComponent.appendChild(ul);
                            let itemNodes = childNode.childNodes;
                            for (let itemIdx = 0; itemIdx < itemNodes.length; itemIdx++) {
                                let itemNode = itemNodes[itemIdx];
                                if (itemNode.nodeType === Node.ELEMENT_NODE) {
                                    if ("item" === itemNode.localName) {
                                        let li = document.createElement("li");
                                        ul.appendChild(li);
                                        inputComponentIndex = this.toComponents(itemNode, li, inputComponentIndex);
                                    }
                                }
                            }
                        }
                    } else if (childNode.localName === "selection") {
                        let exclusive = childNode.getAttribute("exclusive");
                        if (exclusive === "YES") {
                            let radioId = "input-" + inputComponentIndex;
                            inputComponentIndex++;
                            childNode.setAttribute("ejsf-input", radioId);
                            let ul = document.createElement("ul");
                            ul.setAttribute("style", "list-style-type: none;");
                            parentComponent.appendChild(ul);
                            let itemIndex = 0;
                            let itemNodes = childNode.childNodes;
                            let radioBoxSet = new Set();
                            let radioIconSet = new Set();
                            for (let itemIdx = 0; itemIdx < itemNodes.length; itemIdx++) {
                                let itemNode = itemNodes[itemIdx];
                                if (itemNode.nodeType === Node.ELEMENT_NODE) {
                                    if ("selectionitem" === itemNode.localName) {
                                        let li = document.createElement("li");
                                        ul.appendChild(li);
                                        let radio = document.createElement("div");
                                        radio.className = "ui-radiobutton ui-widget";
                                        let radioBox = document.createElement("div");
                                        radioBox.dataset.ejsfInput = radioId;
                                        radioBoxSet.add(radioBox);
                                        radioBox.className = "ui-radiobutton-box ui-widget ui-corner-all ui-state-default";
                                        radio.appendChild(radioBox);
                                        let itemValue = "item-" + itemIndex;
                                        itemIndex++;
                                        itemNode.setAttribute("ejsf-input-item", itemValue);
                                        let radioIcon = document.createElement("span");
                                        radioIconSet.add(radioIcon);
                                        radioIcon.className = "ui-radiobutton-icon ui-icon ui-c ui-icon-blank";
                                        radioBox.appendChild(radioIcon);
                                        let $this = this;
                                        radio.addEventListener("click", function (event) {
                                            childNode.setAttribute("ejsf-input-value", itemValue);
                                            $this.invokeCallback();
                                            radioBoxSet.forEach((rb) => {
                                                rb.classList.remove("ui-state-active");
                                            });
                                            radioIconSet.forEach((ri) => {
                                                ri.classList.remove("ui-icon-blank");
                                            });
                                            radioBox.classList.add("ui-state-active");
                                            radioIcon.classList.add("ui-icon-bullet");
                                        });
                                        radio.addEventListener("focus", function (event) {
                                            radioBox.classList.add("ui-state-focus");
                                        });
                                        radio.addEventListener("blur", function (event) {
                                            radioBox.classList.remove("ui-state-focus");
                                        });
                                        radio.addEventListener("mouseover", function (event) {
                                            radioBox.classList.add("ui-state-hover");
                                        });
                                        radio.addEventListener("mouseout", function (event) {
                                            radioBox.classList.remove("ui-state-hover");
                                        });
                                        li.appendChild(radio);
                                        inputComponentIndex = this.toComponents(itemNode, li, inputComponentIndex);
                                    }
                                }
                            }
                        } else {
                            // exclusive="NO"
                            let checkboxId = "input-" + inputComponentIndex;
                            inputComponentIndex++;
                            childNode.setAttribute("ejsf-input", checkboxId);
                            let ul = document.createElement("ul");
                            ul.setAttribute("style", "list-style-type: none;");
                            parentComponent.appendChild(ul);
                            let itemIndex = 0;
                            let itemNodes = childNode.childNodes;
                            for (let itemIdx = 0; itemIdx < itemNodes.length; itemIdx++) {
                                let itemNode = itemNodes[itemIdx];
                                if (itemNode.nodeType === Node.ELEMENT_NODE) {
                                    if ("selectionitem" === itemNode.localName) {
                                        let li = document.createElement("li");
                                        ul.appendChild(li);
                                        let checkbox = document.createElement("div");
                                        checkbox.className = "ui-chkbox ui-widget";
                                        let checkboxBox = document.createElement("div");
                                        checkboxBox.dataset.ejsfInput = checkboxId;
                                        checkboxBox.className = "ui-chkbox-box ui-widget ui-corner-all ui-state-default";
                                        checkbox.appendChild(checkboxBox);
                                        let itemValue = "item-" + itemIndex;
                                        itemIndex++;
                                        itemNode.setAttribute("ejsf-input-item", itemValue);
                                        let checkboxIcon = document.createElement("span");
                                        checkboxIcon.className = "ui-chkbox-icon ui-icon ui-c ui-icon-blank";
                                        checkboxBox.appendChild(checkboxIcon);
                                        let $this = this;
                                        checkbox.addEventListener("click", function (event) {
                                            let value = childNode.getAttribute("ejsf-input-value");
                                            let checked;
                                            if (value === null) {
                                                checked = false;
                                            } else {
                                                checked = value.includes(itemValue);
                                            }
                                            checked = !checked;
                                            if (checked) {
                                                if (null === value) {
                                                    value = itemValue;
                                                } else {
                                                    if (!value.includes(itemValue)) {
                                                        value += "," + itemValue;
                                                    }
                                                }
                                                childNode.setAttribute("ejsf-input-value", value);
                                                checkboxIcon.classList.remove("ui-icon-blank");
                                                checkboxIcon.classList.add("ui-icon-check");
                                                checkboxBox.classList.add("ui-state-active");
                                            } else {
                                                if (value.includes(itemValue)) {
                                                    let values = value.split(",");
                                                    let valuesSet = new Set(values);
                                                    valuesSet.delete(itemValue);
                                                    if (valuesSet.size === 0) {
                                                        childNode.removeAttribute("ejsf-input-value");
                                                    } else {
                                                        value = null;
                                                        valuesSet.forEach((v) => {
                                                            if (null === value) {
                                                                value = v;
                                                            } else {
                                                                value += "," + v;
                                                            }
                                                        });
                                                        childNode.setAttribute("ejsf-input-value", value);
                                                    }
                                                }
                                                checkboxIcon.classList.remove("ui-icon-check");
                                                checkboxIcon.classList.add("ui-icon-blank");
                                                checkboxBox.classList.remove("ui-state-active");
                                            }
                                            $this.invokeCallback();
                                        });
                                        checkbox.addEventListener("focus", function (event) {
                                            checkboxBox.classList.add("ui-state-focus");
                                        });
                                        checkbox.addEventListener("blur", function (event) {
                                            checkboxBox.classList.remove("ui-state-focus");
                                        });
                                        checkbox.addEventListener("mouseover", function (event) {
                                            checkboxBox.classList.add("ui-state-hover");
                                        });
                                        checkbox.addEventListener("mouseout", function (event) {
                                            checkboxBox.classList.remove("ui-state-hover");
                                        });
                                        li.appendChild(checkbox);
                                        inputComponentIndex = this.toComponents(itemNode, li, inputComponentIndex);
                                    }
                                }
                            }
                        }
                    }
                    break;
            }
        }
        return inputComponentIndex;
    }

    updateRequired(elements) {
        for (let idx = 0; idx < elements.length; idx++) {
            let element = elements.item(idx);
            let ejsfInput = element.getAttribute("ejsf-input");
            if (ejsfInput) {
                let required = this.isRequired(element);
                let valueError;
                if (required) {
                    console.log("required: " + ejsfInput);
                    let value = element.getAttribute("ejsf-input-value");
                    if (null === value || value.length === 0) {
                        console.log("missing value: " + ejsfInput);
                        valueError = true;
                    } else {
                        valueError = false;
                    }
                } else {
                    valueError = false;
                }
                let inputElements = this.element.querySelectorAll("[data-ejsf-input='" + ejsfInput + "']");
                for (let inputIdx = 0; inputIdx < inputElements.length; inputIdx++) {
                    let inputElement = inputElements.item(inputIdx);
                    if (valueError) {
                        inputElement.classList.add("ui-state-error");
                    } else {
                        inputElement.classList.remove("ui-state-error");
                    }
                }
            }
            this.updateRequired(element.children);
        }
    }

    isRequired(element) {
        let node = element.parentNode;
        let itemValue = null;
        while (node !== null) {
            if (node.nodeType === Node.ELEMENT_NODE) {
                let localName = node.localName;
                if ("selectionitem" === localName) {
                    if (null === itemValue) {
                        itemValue = node.getAttribute("ejsf-input-item");
                    }
                } else if ("selection" === localName) {
                    let inputValue = node.getAttribute("ejsf-input-value");
                    if (null === inputValue) {
                        return false;
                    }
                    let values = inputValue.split(",");
                    for (let valueIdx = 0; valueIdx < values.length; valueIdx++) {
                        let selectedValue = values[valueIdx];
                        if (selectedValue === itemValue) {
                            return true;
                        }
                    }
                    return false;
                }
            }
            node = node.parentNode;
        }
        return true;
    }

    invokeCallback() {
        if (this.callback) {
            let xmlSerializer = new XMLSerializer();
            let result = xmlSerializer.serializeToString(this.document.documentElement);
            this.callback(result);
        }
    }

    getResult() {
        this.updateRequired(this.document.documentElement.children);
        let xmlSerializer = new XMLSerializer();
        return xmlSerializer.serializeToString(this.document.documentElement);
    }
}