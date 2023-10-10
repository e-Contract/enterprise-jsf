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
    },

    webAuthnRegistration: function () {
        console.log("initiating WebAuthn registration...");
        if (!window.PublicKeyCredential) {
            let options = {
                params: [
                    {
                        name: $this.id + "_error",
                        value: "WebAuthn not supported."
                    }
                ]
            };
            this.callBehavior("error", options);
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
                console.log("oncomplete");
                let publicKeyCredentialCreationOptions = JSON.parse(args.publicKeyCredentialCreationOptions);
                console.log(publicKeyCredentialCreationOptions);
                webauthnJSON.create({
                    publicKey: publicKeyCredentialCreationOptions
                }).then((credential) => {
                    console.log("credential received");
                    console.log(credential);
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
                    console.log(error);
                    console.log("error: " + error);
                    let options = {
                        params: [
                            {
                                name: $this.id + "_error",
                                value: error
                            }
                        ]
                    };
                    $this.callBehavior("error", options);
                });
            }
        };
        PrimeFaces.ajax.Request.handle(createAjaxRequestOptions);
    },

    webAuthnAuthentication: function () {
        console.log("authenticate");
        if (!window.PublicKeyCredential) {
            let options = {
                params: [
                    {
                        name: $this.id + "_error",
                        value: "WebAuthn not supported."
                    }
                ]
            };
            this.callBehavior("error", options);
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
                console.log("oncomplete");
                let publicKeyCredentialRequestOptions = JSON.parse(args.publicKeyCredentialRequestOptions);
                console.log(publicKeyCredentialRequestOptions);
                webauthnJSON.get(publicKeyCredentialRequestOptions).then((credential) => {
                    console.log("credential received");
                    console.log(credential);
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
                    console.log(error);
                    console.log("error: " + error);
                    let options = {
                        params: [
                            {
                                name: $this.id + "_error",
                                value: error
                            }
                        ]
                    };
                    $this.callBehavior("error", options);
                });
            }
        };
        PrimeFaces.ajax.Request.handle(getAjaxRequestOptions);
    }
});