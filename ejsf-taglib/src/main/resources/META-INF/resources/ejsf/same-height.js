/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */

var ejsf = ejsf || {};
(function () {

    ejsf.sameHeight = function (groupName = "default") {
        let sameHeightElements = $("[data-ejsf-same-height='" + groupName + "']");
        let maxHeight = 0;
        sameHeightElements.each(function () {
            let sameHeightElement = $(this);
            let parentElement = sameHeightElement.parent();
            let parentHeight = parentElement.height();
            if (parentHeight > maxHeight) {
                maxHeight = parentHeight;
            }
        });
        sameHeightElements.each(function () {
            let sameHeightElement = $(this);
            let parentElement = sameHeightElement.parent();
            parentElement.height(maxHeight);
        });
    };
})();

$(document).ready(function () {
    let sameHeightElements = $("[data-ejsf-same-height]");
    let groupNames = new Set();
    sameHeightElements.each(function () {
        let sameHeightElement = $(this);
        let groupName = sameHeightElement.data("ejsf-same-height");
        groupNames.add(groupName);
    });
    for (const groupName of groupNames) {
        ejsf.sameHeight(groupName);
    }
    let mutationObserver = new MutationObserver(function (mutationList) {
        let updateGroupNames = new Set();
        for (const mutation of mutationList) {
            for (const removedNode of mutation.removedNodes) {
                if (removedNode.nodeType !== Node.ELEMENT_NODE) {
                    continue;
                }
                let sameHeightDataAttr = removedNode.attributes.getNamedItem("data-ejsf-same-height");
                if (null === sameHeightDataAttr) {
                    continue;
                }
                let groupName = sameHeightDataAttr.value;
                updateGroupNames.add(groupName);
            }
        }
        for (const updateGroupName of updateGroupNames) {
            queueMicrotask(() => {
                ejsf.sameHeight(updateGroupName);
            });
        }
    });
    mutationObserver.observe(document, {
        childList: true,
        subtree: true
    });
});
