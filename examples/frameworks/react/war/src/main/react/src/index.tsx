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
import { Root, createRoot } from "react-dom/client";
import "primeicons/primeicons.css";
import { Messages } from "primereact/messages";
import { Message } from "primereact/message";

type ErrorMessageProps = {
    message: string
}

const ErrorMessage = (props: ErrorMessageProps): React.JSX.Element => {
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
    return <>
        <Message text={props.message} style={style} severity="error" />
    </>;
}

type AddItemDialogHandle = {
    show: () => void;
};

type AddItemDialogProps = {
    onAdded: (itemName: string) => void;
};

const AddItemDialog = forwardRef<AddItemDialogHandle, AddItemDialogProps>((props: AddItemDialogProps, ref: React.ForwardedRef<AddItemDialogHandle>): React.JSX.Element => {
    const [visible, setVisible] = useState<boolean>(false);
    const [name, setName] = useState<string>("");
    const [nameClass, setNameClass] = useState<string>("");
    const [amount, setAmount] = useState<string>("");
    const [amountClass, setAmountClass] = useState<string>("");
    const [nameMessage, setNameMessage] = useState<string>("");
    const [amountMessage, setAmountMessage] = useState<string>("");
    const nameInputText = useRef<HTMLInputElement>(null);

    useImperativeHandle(ref, (): AddItemDialogHandle => {
        return {
            show(): void {
                setName("");
                setNameValid();
                setAmount("");
                setAmountValid();
                setVisible(true);
            }
        };
    });

    function setNameValid(): void {
        setNameClass("");
        setNameMessage("");
    }

    function setAmountValid(): void {
        setAmountClass("");
        setAmountMessage("");
    }

    function setAmountInvalid(message: string): void {
        setAmountClass("p-invalid");
        setAmountMessage(message);
    }

    function setNameInvalid(message: string): void {
        setNameClass("p-invalid");
        setNameMessage(message);
    }

    function addItemOnClickListener(): void {
        fetch("http://localhost:8080/react/api/item/add?name=" + name + "&amount=" + amount, {
            method: "post"
        })
            .then((response: Response) => {
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

    return <>
        <Dialog header="Add Item" visible={visible}
            onHide={() => setVisible(false)}
            onShow={() => { nameInputText.current?.focus() }}>
            <div className="p-fluid">
                <div className="p-field">
                    <label htmlFor="name">Name</label>
                    <InputText className={nameClass}
                        value={name} ref={nameInputText}
                        onChange={(e: React.ChangeEvent<HTMLInputElement>) => setName(e.target.value)} />
                    <ErrorMessage message={nameMessage} />
                </div>
                <div className="p-field">
                    <label htmlFor="amount">Amount</label>
                    <InputText className={amountClass}
                        value={amount}
                        onChange={(e: React.ChangeEvent<HTMLInputElement>) => setAmount(e.target.value)} />
                    <ErrorMessage message={amountMessage} />
                </div>
            </div>
            <div style={{ marginTop: "10px" }}>
                <Button label="Add" onClick={addItemOnClickListener}
                    style={{ marginRight: "10px" }} icon="pi pi-plus-circle" />
                <Button label="Dismiss" onClick={() => setVisible(false)} icon="pi pi-times" />
            </div>
        </Dialog>
    </>;
});

type RemoveItemDialogHandle = {
    show: (itemName: string) => void;
};

type RemoveItemDialogProps = {
    onRemoved: (itemName: string) => void;
}

const RemoveItemDialog = forwardRef<RemoveItemDialogHandle, RemoveItemDialogProps>((props: RemoveItemDialogProps, ref: React.ForwardedRef<RemoveItemDialogHandle>): React.JSX.Element => {
    const [visible, setVisible] = useState<boolean>(false);
    const [itemName, setItemName] = useState<string>();

    useImperativeHandle(ref, (): RemoveItemDialogHandle => {
        return {
            show(name: string): void {
                setItemName(name);
                setVisible(true);
            }
        };
    });

    function removeItem(): void {
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

    return <>
        <Dialog header="Remove Item" visible={visible} onHide={() => setVisible(false)}>
            Do you want to remove {itemName}?
            <div className="mt-2">
                <Button label="Remove" onClick={removeItem}
                    className="mr-2" icon="pi pi-trash" />
                <Button label="Dismiss" icon="pi pi-times"
                    onClick={() => setVisible(false)} />
            </div>
        </Dialog>
    </>;
});

type ItemDataTableProps = {
    items: any;
    removeCallback: (itemName: string) => void;
    addCallback: () => void;
}

const ItemDataTable = (props: ItemDataTableProps): React.JSX.Element => {
    function ItemRemoveButton(rowData: any): React.JSX.Element {
        return <>
            <Button onClick={() => props.removeCallback(rowData.name)}
                icon="pi pi-trash" tooltip="Remove item..." />
        </>;
    }

    function ItemAddButton(): React.JSX.Element {
        return <>
            <Button onClick={() => props.addCallback()}
                icon="pi pi-plus-circle" tooltip="Add item..." />
        </>;
    }

    return <>
        <DataTable value={props.items} footer={ItemAddButton}>
            <Column field="name" header="Name" />
            <Column field="amount" header="Amount" />
            <Column header="Actions" body={ItemRemoveButton} />
        </DataTable>
    </>;
};

const App = (): React.JSX.Element => {
    const [items, setItems] = useState([]);
    const messages = useRef<Messages>(null);
    const removeItemDialog = useRef<RemoveItemDialogHandle>(null);
    const addItemDialog = useRef<AddItemDialogHandle>(null);

    useEffect((): void => {
        loadItems();
    }, []); // []: run only once

    function loadItems(): void {
        fetch("http://localhost:8080/react/api/item/list")
            .then((response: Response) => {
                response.json().then(data => {
                    setItems(data);
                })
            });
    }

    return <>
        <PrimeReactProvider>
            <Messages ref={messages} />
            <ItemDataTable items={items}
                addCallback={(): void => addItemDialog.current!.show()}
                removeCallback={(itemName: string): void => removeItemDialog.current!.show(itemName)} />
            <AddItemDialog ref={addItemDialog} onAdded={(name: string): void => {
                messages.current!.show({
                    severity: "info",
                    summary: "Item " + name + " added."
                });
                loadItems();
            }} />
            <RemoveItemDialog ref={removeItemDialog}
                onRemoved={(name: string): void => {
                    messages.current!.show({
                        severity: "info",
                        summary: "Item " + name + " removed."
                    });
                    loadItems();
                }} />
        </PrimeReactProvider>
    </>;
};

let container: HTMLElement = document.getElementById("app") as HTMLElement;
let root: Root = createRoot(container);
root.render(<App />);
