import esbuild from "esbuild";
import { copy } from "esbuild-plugin-copy";
import express from "express";
import open from "open";

const esBuildOptions = {
    entryPoints: ["src/index.tsx"],
    outdir: "build",
    logLevel: "info",
    loader: {
        ".ttf": "file",
        ".woff": "file",
        ".woff2": "file",
        ".eot": "file",
        ".svg": "file",
    },
    bundle: true,
    target: [
        "chrome58",
        "firefox57",
        "safari11",
        "edge18",
    ],
    plugins: [
        copy({
            resolveFrom: "cwd",
            assets: {
                from: ["./public/*"],
                to: ["build"],
            },
            watch: true,
            verbose: true,
        }),
    ],
};

let argv = process.argv.slice(2);
let mode = argv[0];

const PORT = 3000;

if ("build" === mode) {
    await esbuild.build({
        ...esBuildOptions,
        minify: true
    });
} else if ("dev" === mode) {
    async function serve() {
        let server = express();
        server.use(express.static("build"));
        server.listen(PORT);
    }

    async function watch() {
        let context = await esbuild.context({ ...esBuildOptions });
        serve();
        open("http://localhost:" + PORT);
        await context.watch();
    }
    watch();
} else {
    console.log("missing option: build or dev");
}
