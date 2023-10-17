/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */

PrimeFaces.widget.EJSFWebAuthn = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function (cfg) {
        this._super(cfg);
        let $this = this;
        webauthnJSON.schema.credentialCreationOptions.publicKey.schema.extensions.schema.prf = {
            required: false,
            schema: {
                eval: {
                    required: false,
                    schema: {
                        first: {
                            required: true,
                            schema: "convert"
                        },
                        second: {
                            required: false,
                            schema: "convert"
                        }
                    }
                },
                evalByCredential: {
                    required: false,
                    schema: "copy",
                    derive: function (input) {
                        let evalByCredentialObject = input.evalByCredential;
                        if (!(evalByCredentialObject instanceof Object)) {
                            return;
                        }
                        for (const [key, value] of Object.entries(evalByCredentialObject)) {
                            value.first = $this.base64urlToBuffer(value.first);
                            if (typeof value.second !== "undefined") {
                                value.second = $this.base64urlToBuffer(value.second);
                            }
                        }
                        return evalByCredentialObject;
                    }
                }
            }
        };
        webauthnJSON.schema.credentialCreationOptions.publicKey.schema.extensions.schema.uvm = {
            required: false,
            schema: "copy"
        };
        webauthnJSON.schema.publicKeyCredentialWithAttestation.clientExtensionResults.schema.prf = {
            required: false,
            schema: {
                enabled: {
                    required: false,
                    schema: "copy"
                },
                results: {
                    required: false,
                    schema: {
                        first: {
                            required: true,
                            schema: "convert"
                        },
                        second: {
                            required: false,
                            schema: "convert"
                        }
                    }
                }
            }
        };
        webauthnJSON.schema.publicKeyCredentialWithAttestation.clientExtensionResults.schema.uvm = {
            required: false,
            schema: "copy"
        };
    },

    webAuthnRegistration: function () {
        if (!webauthnJSON.supported()) {
            this.sendError("WebAuthn not supported.");
            return;
        }
        let $this = this;
        let createAjaxRequestOptions = {
            source: this.id,
            process: this.id,
            async: true,
            global: false,
            params: [
                {
                    name: this.id + "_registration_request",
                    value: true
                }
            ],
            oncomplete: function (xhr, status, args, data) {
                if (typeof args.publicKeyCredentialCreationOptions === "undefined") {
                    $this.sendError("Internal error.");
                    return;
                }
                let publicKeyCredentialCreationOptions = JSON.parse(args.publicKeyCredentialCreationOptions);
                console.log(publicKeyCredentialCreationOptions);
                webauthnJSON.create({
                    publicKey: publicKeyCredentialCreationOptions
                }).then((credential) => {
                    let options = {
                        params: [
                            {
                                name: $this.id + "_registration_response",
                                value: JSON.stringify(credential)
                            }
                        ]
                    };
                    $this.callBehavior("registered", options);
                }).catch((error) => {
                    console.log("error on creating credential");
                    $this.sendError(error);
                });
            }
        };
        PrimeFaces.ajax.Request.handle(createAjaxRequestOptions);
    },

    webAuthnAuthentication: function () {
        if (!webauthnJSON.supported()) {
            this.sendError("WebAuthn not supported.");
            return;
        }
        let $this = this;
        let getAjaxRequestOptions = {
            source: this.id,
            process: this.id,
            async: true,
            global: false,
            params: [
                {
                    name: this.id + "_authentication_request",
                    value: true
                }
            ],
            oncomplete: function (xhr, status, args, data) {
                if (typeof args.publicKeyCredentialRequestOptions === "undefined") {
                    $this.sendError("Internal error.");
                    return;
                }
                let publicKeyCredentialRequestOptions = JSON.parse(args.publicKeyCredentialRequestOptions);
                webauthnJSON.get(publicKeyCredentialRequestOptions).then((credential) => {
                    let options = {
                        params: [
                            {
                                name: $this.id + "_authentication_response",
                                value: JSON.stringify(credential)
                            }
                        ]
                    };
                    $this.callBehavior("authenticated", options);
                }).catch((error) => {
                    console.log("error on creating credential");
                    $this.sendError(error);
                });
            }
        };
        PrimeFaces.ajax.Request.handle(getAjaxRequestOptions);
    },

    sendError: function (error) {
        console.log(error);
        console.log("error: " + error);
        console.log("error type: " + typeof error);
        let options = {
            params: [
                {
                    name: this.id + "_error",
                    value: error
                }
            ]
        };
        this.callBehavior("error", options);
    },

    base64urlToBuffer: function (baseurl64String) {
        const padding = "==".slice(0, (4 - baseurl64String.length % 4) % 4);
        const base64String = baseurl64String.replace(/-/g, "+").replace(/_/g, "/") + padding;
        const str = atob(base64String);
        const buffer = new ArrayBuffer(str.length);
        const byteView = new Uint8Array(buffer);
        for (let i = 0; i < str.length; i++) {
            byteView[i] = str.charCodeAt(i);
        }
        return buffer;
    }
});
