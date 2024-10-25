/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */

const fs = require("fs");
console.log("Converting CSS font URLs to JSF resource URLs...");
let content = fs.readFileSync("node_modules/katex/dist/katex.min.css", {encoding: "utf8"});
function convertToResourceUrl(match, p1, offset) {
    return "url(\"#{resource['ejsf:katex/" + p1 + "']}\")";
}
let newContent = content.replace(/url\(([^)]+)\)/g, convertToResourceUrl);
fs.writeFileSync("node_modules/katex/dist/katex.min.css", newContent);
