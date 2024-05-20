/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */

function updateOutputCurrency() {
    let outputCurrencyWidget = PF("outputCurrency");
    let inputNumberWidget = PF("inputNumber");
    let inputNumber = inputNumberWidget.jq.get(0).value;
    console.log("input number: " + inputNumber);
    console.log("typeof input number: " + typeof inputNumber);
    let currentCurrencyValue = outputCurrencyWidget.getValue();
    console.log("current currency value: " + currentCurrencyValue);
    console.log("typeof current currency value: " + typeof currentCurrencyValue);
    outputCurrencyWidget.setValue(inputNumber);
}
