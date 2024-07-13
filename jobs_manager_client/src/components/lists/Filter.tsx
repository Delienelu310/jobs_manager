import { useState } from "react";


export enum FieldType{
    SingleInput, MultipleInput, SingleSelection, MultipleSelection
}

export interface FieldValue{
    value : string[],
    setter: React.Dispatch<React.SetStateAction<string[]>>,       
    fieldType: FieldType,                                      
    additionalData: string[]                                        
}


export interface FilterProperties{
    parameters : Map<string, FieldValue>
}

const Filter = ({parameters} : FilterProperties) => {

    const [multipleInput, setMultipleInput] = useState<string>("");
    const [multipleInputList, setMultipleInputList] = useState<string[]>([]);


    return (
        <div>
            {Array.from(parameters.entries()).map( ([label, fieldValue]) => (
                <div>
                    {fieldValue.fieldType == FieldType.SingleInput ?
                        <div>
                            <label>{label}</label>
                            <input value={fieldValue.value[0] ? fieldValue.value[0] : ""} onChange={(e) => fieldValue.setter([e.target.value])}/>
                        </div>
                        :
                        fieldValue.fieldType == FieldType.MultipleInput ?
                        <div>
                            <label>{label}</label>
                            <input value={multipleInput} onChange={(e) => setMultipleInput(e.target.value)}/>
                            <button onClick={(e) => {
                                setMultipleInputList([...multipleInputList, multipleInput]);
                                fieldValue.setter([...multipleInputList, multipleInput]);
                            }}>Add</button>

                            {multipleInputList.map(input => <div key={label + "_" +input}>
                                <span>{input}</span>
                                <button 
                                    onClick={(e) => setMultipleInputList(multipleInputList.filter(val => val != input))} 
                                    className="btn btn-danger"
                                >X</button>
                            </div>)}
                        </div>
                        :
                        fieldValue.fieldType == FieldType.SingleSelection ?
                        <div>
                            <label>{label}</label>
                            <select 
                                value={fieldValue.value[0] ? fieldValue.value[0] : ""}
                                onChange={(e) => fieldValue.setter([e.target.value])}
                            >
                                {fieldValue.additionalData.map(str => <option value={str}>
                                    {str}
                                </option>)}
                            </select>
                        </div>
                        :
                        fieldValue.fieldType == FieldType.MultipleSelection ? 
                        <div>

                        </div>
                        :
                        <div>
                            Invalid Field Type: {fieldValue.fieldType}
                        </div>
                    }
                </div>
            ) )}
        </div>
    );
};

export default Filter;