import React, { useEffect, useState } from "react";
import { PrimeReactProvider } from "primereact/api";
import { DataTable } from "primereact/datatable";
import { Column } from "primereact/column";
import "primereact/resources/primereact.min.css";
import "primereact/resources/themes/lara-light-indigo/theme.css";
import { Button } from "primereact/button";
import { Dialog } from "primereact/dialog";
import "primeflex/primeflex.css";
import { InputText } from "primereact/inputtext";
import { createRoot } from 'react-dom/client';

const App = () => {
    console.log("initialization...");
    const [items, setItems] = useState([]);
    const [addDialogVisible, setAddDialogVisible] = useState<boolean>(false);
    const [removeDialogVisible, setRemoveDialogVisible] = useState<boolean>(false);
    const [addDialogName, setAddDialogName] = useState<string>("");
    const [addDialogAmount, setAddDialogAmount] = useState<string>("");

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
                    setAddDialogName("");
                    setAddDialogAmount("");
                    setAddDialogVisible(false);
                    loadTableData();
                }
            });
    };

    function ItemRemoveButton(rowData) {
        function itemRemoveButtonClicked(rowData) {
            console.log("remove: " + rowData.name);
            fetch("http://localhost:8080/react/api/item/remove?name=" + rowData.name)
                .then((response: Response) => {
                    loadTableData();
                });
        };
        return (
            <Button label="Remove" onClick={() => itemRemoveButtonClicked(rowData)} />
        );
    }

    return (
        <PrimeReactProvider>
            <DataTable value={items}>
                <Column field="name" header="Name" />
                <Column field="amount" header="Amount" />
                <Column header="Actions" body={ItemRemoveButton} />
            </DataTable>
            <Button label="Add" onClick={() => setAddDialogVisible(true)} />
            <Dialog header="Add Item" visible={addDialogVisible} onHide={() => setAddDialogVisible(false)}>
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
                <div>
                    <Button label="Add" onClick={addItemOnClickListener} />
                    <Button label="Dismiss" onClick={() => {
                        setAddDialogName("");
                        setAddDialogAmount("");
                        setAddDialogVisible(false)
                    }} />
                </div>
            </Dialog>
            <Dialog header="Remove Item" visible={removeDialogVisible} onHide={() => setRemoveDialogVisible(false)}>
                TODO
            </Dialog>
        </PrimeReactProvider>
    );
};

let container: HTMLElement = document.getElementById("app") as HTMLElement;
let root = createRoot(container);
root.render(<App />);
