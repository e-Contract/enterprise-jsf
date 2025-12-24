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
