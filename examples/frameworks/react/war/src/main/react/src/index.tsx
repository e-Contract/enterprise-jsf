import React, { useEffect, useRef, useState, forwardRef, useImperativeHandle, RefObject } from "react";
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

type RemoveItemDialogHandle = {
    show: (name:string) => void;
};

type RemoveItemDialogProps = {
    messages: RefObject<Messages>;
    onRemoved: () => void;
}

const RemoveItemDialog = forwardRef<RemoveItemDialogHandle, RemoveItemDialogProps>((props, ref) => {
    const [visible, setVisible] = useState<boolean>(false);
    const [itemName, setItemName] = useState<string>();

    useImperativeHandle(ref, () => {
        return {
            show(name:string) {
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
                    props.onRemoved();
                    props.messages.current!.show({
                        severity: "info",
                        summary: "Item " + itemName + " removed."
                    });
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

const App = () => {
    console.log("initialization...");
    const [items, setItems] = useState([]);
    const [addDialogVisible, setAddDialogVisible] = useState<boolean>(false);
    const [addDialogName, setAddDialogName] = useState<string>("");
    const [addDialogNameClass, setAddDialogNameClass] = useState<string>("");
    const [addDialogAmount, setAddDialogAmount] = useState<string>("");
    const [addDialogAmountClass, setAddDialogAmountClass] = useState<string>("");
    const messages = useRef<Messages>(null);
    const removeItemDialog = useRef<RemoveItemDialogHandle>(null);

    useEffect(() => {
        console.log("useEffect");
        loadTableData();
    }, []); // []: run only once

    function loadTableData() {
        fetch("http://localhost:8080/react/api/item/list")
            .then((response: Response) => {
                response.json().then(data => {
                    setItems(data);
                })
            });
    }

    function addItemOnClickListener() {
        fetch("http://localhost:8080/react/api/item/add?name=" + addDialogName + "&amount=" + addDialogAmount, {
            method: "post"
        })
            .then((response: Response) => {
                console.log("response status: " + response.status);
                if (response.status === 204) {
                    messages.current!.show({
                        severity: "info",
                        summary: "Item " + addDialogName + " added."
                    });
                    setAddDialogName("");
                    setAddDialogNameClass("");
                    setAddDialogAmount("");
                    setAddDialogAmountClass("");
                    setAddDialogVisible(false);
                    loadTableData();
                } else if (response.status === 400) {
                    setAddDialogNameClass("");
                    setAddDialogAmountClass("");
                    response.json().then((addErrors) => {
                        if (addErrors.errors.includes("MISSING_NAME")) {
                            setAddDialogNameClass("p-invalid");
                        }
                        if (addErrors.errors.includes("EXISTING_NAME")) {
                            setAddDialogNameClass("p-invalid");
                        }
                        if (addErrors.errors.includes("MISSING_AMOUNT")) {
                            setAddDialogAmountClass("p-invalid");
                        }
                        if (addErrors.errors.includes("AMOUNT_MINIMUM")) {
                            setAddDialogAmountClass("p-invalid");
                        }
                    });
                } else if (response.status === 404) {
                    setAddDialogNameClass("p-invalid");
                    setAddDialogAmountClass("p-invalid");
                }
            });
    };


    function ItemRemoveButton(rowData) {
        return (
            <Button label="Remove" onClick={() => {
                removeItemDialog.current!.show(rowData.name);
            }} icon="pi pi-trash" />
        );
    }

    return (
        <PrimeReactProvider>
            <Messages ref={messages} />
            <DataTable value={items}>
                <Column field="name" header="Name" />
                <Column field="amount" header="Amount" />
                <Column header="Actions" body={ItemRemoveButton} />
            </DataTable>
            <Button label="Add"
                onClick={() => setAddDialogVisible(true)}
                className="mt-2" icon="pi pi-plus-circle" />
            <Dialog header="Add Item" visible={addDialogVisible}
                onHide={() => setAddDialogVisible(false)}>
                <div className="p-fluid">
                    <div className="p-field">
                        <label htmlFor="name">Name</label>
                        <InputText className={addDialogNameClass}
                            value={addDialogName}
                            onChange={(e) => setAddDialogName(e.target.value)} />
                    </div>
                    <div className="p-field">
                        <label htmlFor="amount">Amount</label>
                        <InputText className={addDialogAmountClass}
                            value={addDialogAmount}
                            onChange={(e) => setAddDialogAmount(e.target.value)} />
                    </div>
                </div>
                <div style={{ marginTop: "10px" }}>
                    <Button label="Add" onClick={addItemOnClickListener}
                        style={{ marginRight: "10px" }} icon="pi pi-plus-circle" />
                    <Button label="Dismiss" onClick={() => {
                        setAddDialogName("");
                        setAddDialogAmount("");
                        setAddDialogVisible(false)
                    }} icon="pi pi-times" />
                </div>
            </Dialog>
            <RemoveItemDialog ref={removeItemDialog} messages={messages}
                onRemoved={() => loadTableData()} />
        </PrimeReactProvider>
    );
};

let container: HTMLElement = document.getElementById("app") as HTMLElement;
let root = createRoot(container);
root.render(<App />);
