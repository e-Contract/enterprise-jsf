function exampleAjaxEventOnClick(id, event, execute, render) {
    let options = {
        'javax.faces.behavior.event': 'click'
    };
    if (execute !== null) {
        options['execute'] = execute;
    }
    if (render !== null) {
        options['render'] = render;
    }
    options.params = {
    };
    options.params[id + "_parameter"] = new Date().toString();
    console.log("id: " + id);
    console.log(event);
    console.log(options);
    jsf.ajax.request(id, event, options);
}
