import React, { useEffect, useRef, useState, forwardRef, useImperativeHandle } from "react";
import { PrimeReactProvider } from "primereact/api";
import { DataTable } from "primereact/datatable";
import { Column } from "primereact/column";
import "primereact/resources/primereact.min.css";
import "primereact/resources/themes/saga-blue/theme.css";
import { Button } from "primereact/button";
import { Dialog } from "primereact/dialog";
import "primeflex/primeflex.min.css";
import { InputText } from "primereact/inputtext";
import { createRoot } from "react-dom/client";
import "primeicons/primeicons.css";
import { Messages } from "primereact/messages";
import { Message } from "primereact/message";

type ErrorMessageProps = {
    message: string
}

const ErrorMessage = (props: ErrorMessageProps) => {
    let style: React.CSSProperties;
    if (props.message.length === 0) {
        style = {
            display: "none"
        };
    } else {
        style = {
            display: "block"
        }
    }
    return (
        <Message text={props.message} style={style} severity="error" />
    );
}

type AddItemDialogHandle = {
    show: () => void;
};

type AddItemDialogProps = {
    onAdded: (name: string) => void;
};

const AddItemDialog = forwardRef<AddItemDialogHandle, AddItemDialogProps>((props, ref) => {
    const [visible, setVisible] = useState<boolean>(false);
    const [name, setName] = useState<string>("");
    const [nameClass, setNameClass] = useState<string>("");
    const [amount, setAmount] = useState<string>("");
    const [amountClass, setAmountClass] = useState<string>("");
    const [nameMessage, setNameMessage] = useState<string>("");
    const [amountMessage, setAmountMessage] = useState<string>("");

    useImperativeHandle(ref, () => {
        return {
            show() {
                setName("");
                setNameValid();
                setAmount("");
                setAmountValid();
                setVisible(true);
            }
        };
    });

    function setNameValid() {
        setNameClass("");
        setNameMessage("");
    }

    function setAmountValid() {
        setAmountClass("");
        setAmountMessage("");
    }

    function setAmountInvalid(message: string) {
        setAmountClass("p-invalid");
        setAmountMessage(message);
    }

    function setNameInvalid(message: string) {
        setNameClass("p-invalid");
        setNameMessage(message);
    }

    function addItemOnClickListener() {
        fetch("http://localhost:8080/react/api/item/add?name=" + name + "&amount=" + amount, {
            method: "post"
        })
            .then((response: Response) => {
                console.log("response status: " + response.status);
                if (response.status === 204) {
                    let nameAdded = name;
                    setName("");
                    setNameValid();
                    setAmount("");
                    setAmountValid();
                    setVisible(false);
                    props.onAdded(nameAdded);
                } else if (response.status === 400) {
                    setNameValid();
                    setAmountValid();
                    response.json().then((addErrors) => {
                        if (addErrors.errors.includes("MISSING_NAME")) {
                            setNameInvalid("Missing name");
                        }
                        if (addErrors.errors.includes("EXISTING_NAME")) {
                            setNameInvalid("Existing name");
                        }
                        if (addErrors.errors.includes("MISSING_AMOUNT")) {
                            setAmountInvalid("Missing amount");
                        }
                        if (addErrors.errors.includes("AMOUNT_MINIMUM")) {
                            setAmountInvalid("Amount smaller than 1.");
                        }
                    });
                } else if (response.status === 404) {
                    setNameClass("p-invalid");
                    setAmountClass("p-invalid");
                }
            });
    };

    return (
        <Dialog header="Add Item" visible={visible}
            onHide={() => setVisible(false)}>
            <div className="p-fluid">
                <div className="p-field">
                    <label htmlFor="name">Name</label>
                    <InputText className={nameClass}
                        value={name}
                        onChange={(e) => setName(e.target.value)} />
                    <ErrorMessage message={nameMessage} />
                </div>
                <div className="p-field">
                    <label htmlFor="amount">Amount</label>
                    <InputText className={amountClass}
                        value={amount}
                        onChange={(e) => setAmount(e.target.value)} />
                    <ErrorMessage message={amountMessage} />
                </div>
            </div>
            <div style={{ marginTop: "10px" }}>
                <Button label="Add" onClick={addItemOnClickListener}
                    style={{ marginRight: "10px" }} icon="pi pi-plus-circle" />
                <Button label="Dismiss" onClick={() => setVisible(false)} icon="pi pi-times" />
            </div>
        </Dialog>
    );
});

type RemoveItemDialogHandle = {
    show: (name: string) => void;
};

type RemoveItemDialogProps = {
    onRemoved: (name: string) => void;
}

const RemoveItemDialog = forwardRef<RemoveItemDialogHandle, RemoveItemDialogProps>((props, ref) => {
    const [visible, setVisible] = useState<boolean>(false);
    const [itemName, setItemName] = useState<string>();

    useImperativeHandle(ref, () => {
        return {
            show(name: string) {
                setItemName(name);
                setVisible(true);
            }
        };
    });
    function removeItem() {
        fetch("http://localhost:8080/react/api/item/remove?name=" + itemName, {
            method: "post"
        })
            .then((response: Response) => {
                if (response.status === 204) {
                    setVisible(false);
                    props.onRemoved(itemName!);
                }
            });
    }
    return (
        <Dialog header="Remove Item" visible={visible} onHide={() => setVisible(false)}>
            Do you want to remove {itemName}?
            <div className="mt-2">
                <Button label="Remove" onClick={removeItem}
                    className="mr-2" icon="pi pi-trash" />
                <Button label="Dismiss" icon="pi pi-times"
                    onClick={() => setVisible(false)} />
            </div>
        </Dialog>
    );
});

type ItemDataTableProps = {
    items: any;
    removeCallback: (itemName: string) => void;
}

const ItemDataTable = (props: ItemDataTableProps) => {
    function ItemRemoveButton(rowData) {
        return (
            <Button label="Remove" onClick={() => {
                props.removeCallback(rowData.name);
            }} icon="pi pi-trash" />
        );
    }
    return (
        <DataTable value={props.items}>
            <Column field="name" header="Name" />
            <Column field="amount" header="Amount" />
            <Column header="Actions" body={ItemRemoveButton} />
        </DataTable>
    );
};

const App = () => {
    console.log("initialization...");
    const [items, setItems] = useState([]);
    const messages = useRef<Messages>(null);
    const removeItemDialog = useRef<RemoveItemDialogHandle>(null);
    const addItemDialog = useRef<AddItemDialogHandle>(null);

    useEffect(() => {
        console.log("useEffect");
        loadItems();
    }, []); // []: run only once

    function loadItems() {
        fetch("http://localhost:8080/react/api/item/list")
            .then((response: Response) => {
                response.json().then(data => {
                    setItems(data);
                })
            });
    }

    return (
        <PrimeReactProvider>
            <Messages ref={messages} />
            <ItemDataTable items={items}
                removeCallback={(itemName: string) => removeItemDialog.current!.show(itemName)} />
            <Button label="Add"
                onClick={() => addItemDialog.current!.show()}
                className="mt-2" icon="pi pi-plus-circle" />
            <AddItemDialog ref={addItemDialog} onAdded={(name: string) => {
                messages.current!.show({
                    severity: "info",
                    summary: "Item " + name + " added."
                });
                loadItems();
            }} />
            <RemoveItemDialog ref={removeItemDialog}
                onRemoved={(name: string) => {
                    messages.current!.show({
                        severity: "info",
                        summary: "Item " + name + " removed."
                    });
                    loadItems();
                }} />
        </PrimeReactProvider>
    );
};

let container: HTMLElement = document.getElementById("app") as HTMLElement;
let root = createRoot(container);
root.render(<App />);
