/*
 * Enterprise JSF project.
 *
 * Copyright 2025-2026 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */

export default {
    entry: "./index.js",
    output: {
        filename: "highlight.min.js",
        path: process.cwd(),
        library: {
            name: "hljs",
            type: "var"
        }
    },
    mode: "production"
};
