/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */

var ejsf = ejsf || {};

(function () {

    ejsf.storageSetItem = function (name, type, value) {
        let storage = ejsf.getStorage(type);
        try {
            storage.setItem(name, value);
        } catch (error) {
            console.error("Could not set item on storage.");
        }
    };

    ejsf.storageRemoveItem = function (name, type) {
        let storage = ejsf.getStorage(type);
        storage.removeItem(name);
    };

    ejsf.getStorage = function (type) {
        let storage;
        if (type === "session") {
            storage = window.sessionStorage;
        } else {
            storage = window.localStorage;
        }
        return storage;
    };

    ejsf.clearStorage = function (type) {
        let storage = ejsf.getStorage(type);
        storage.clear();
    };

})();

$(document).ready(function () {
    let storageGetItemsElements = $("[data-ejsf-storage-get-items]");
    storageGetItemsElements.each(function () {
        let storageGetItemsElement = $(this);
        setTimeout(function () {
            let storageGetItemsElementId = storageGetItemsElement.attr("id");
            let storageItemsParam = storageGetItemsElement.attr("data-ejsf-storage-get-items");
            let storageItemsParamArray = storageItemsParam.split(",");
            let storageItems = [];
            storageItemsParamArray.forEach((storageItemParam) => {
                let storageItem = {};
                let storageItemParamArray = storageItemParam.split(":");
                storageItem.name = storageItemParamArray[0];
                storageItem.type = storageItemParamArray[1];
                storageItems.push(storageItem);
            });
            storageItems.forEach((storageItem) => {
                console.log(storageItem.name + ":" + storageItem.type);
                let storage = ejsf.getStorage(storageItem.type);
                let value = storage.getItem(storageItem.name);
                storageItem.value = value;
            });
            let render = storageGetItemsElement.attr("data-ejsf-storage-get-items-render");
            let ajaxRequestOptions = {
                source: storageGetItemsElementId,
                process: "@form",
                update: render,
                event: "items",
                params: storageItems
            };
            PrimeFaces.ajax.Request.handle(ajaxRequestOptions);
        }, 0);
    });
});
