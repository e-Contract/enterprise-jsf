import React, { useEffect, useRef, useState } from "react";
import { PrimeReactProvider } from "primereact/api";
import { DataTable } from "primereact/datatable";
import { Column } from "primereact/column";
import "primereact/resources/primereact.min.css";
import "primereact/resources/themes/saga-blue/theme.css";
import { Button } from "primereact/button";
import { Dialog } from "primereact/dialog";
import "primeflex/primeflex.min.css";
import { InputText } from "primereact/inputtext";
import { createRoot } from 'react-dom/client';
import 'primeicons/primeicons.css';
import { Messages } from 'primereact/messages';

const App = () => {
    console.log("initialization...");
    const [items, setItems] = useState([]);
    const [addDialogVisible, setAddDialogVisible] = useState<boolean>(false);
    const [removeDialogVisible, setRemoveDialogVisible] = useState<boolean>(false);
    const [addDialogName, setAddDialogName] = useState<string>("");
    const [addDialogAmount, setAddDialogAmount] = useState<string>("");
    const [removeItem, setRemoveItem] = useState<string>("");
    const messages = useRef<Messages>(null);

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
        console.log("add item name: " + addDialogName);
        console.log("add item amount: " + addDialogAmount);
        fetch("http://localhost:8080/react/api/item/add?name=" + addDialogName + "&amount=" + addDialogAmount)
            .then((response: Response) => {
                console.log("response status: " + response.status);
                if (response.status === 204) { // not in line with OpenAPI 200 status code
                    messages.current!.show({
                        severity: "info",
                        summary: "Item " + addDialogName + " added."
                    });
                    setAddDialogName("");
                    setAddDialogAmount("");
                    setAddDialogVisible(false);
                    loadTableData();
                }
            });
    };

    function removeItemFunc() {
        fetch("http://localhost:8080/react/api/item/remove?name=" + removeItem)
            .then((response: Response) => {
                setRemoveDialogVisible(false);
                loadTableData();
                messages.current!.show({
                    severity: "info",
                    summary: "Item " + removeItem + " removed."
                });
            });
    }

    function ItemRemoveButton(rowData) {
        return (
            <Button label="Remove" onClick={() => {
                setRemoveItem(rowData.name);
                setRemoveDialogVisible(true)
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
                <div className="field grid">
                    <label htmlFor="name" className="col-fixed">Name</label>
                    <div className="col">
                        <InputText className="p-2"
                            value={addDialogName}
                            onChange={(e) => setAddDialogName(e.target.value)} />
                    </div>
                </div>
                <div className="field grid">
                    <label htmlFor="amount" className="col-fixed">Amount</label>
                    <div className="col">
                        <InputText className="p-2"
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
            <Dialog header="Remove Item"
                visible={removeDialogVisible}
                onHide={() => setRemoveDialogVisible(false)}>
                Do you want to remove {removeItem}?
                <div className="mt-2">
                    <Button label="Remove" onClick={removeItemFunc}
                        className="mr-2" icon="pi pi-trash" />
                    <Button label="Dismiss" icon="pi pi-times"
                        onClick={() => setRemoveDialogVisible(false)} />
                </div>
            </Dialog>
        </PrimeReactProvider>
    );
};

let container: HTMLElement = document.getElementById("app") as HTMLElement;
let root = createRoot(container);
root.render(<App />);
