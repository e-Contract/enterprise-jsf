function addDialogOnComplete(xhr, status, args) {
    if (args.validationFailed) {
        return;
    }
    if (!args.itemAdded) {
        return;
    }
    PF('addDialog').hide();
}
