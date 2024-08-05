import {  useState } from "react";


export enum FieldType{
    SingleInput, MultipleInput, SingleSelection, MultipleSelection, SingleDate
}

export interface FieldValue{   
    setter : (val : string[]) => void   
    fieldType: FieldType,                                      
    additionalData: string[] ,                                       
}


export interface FilterProperties{
    parameters : Map<string, FieldValue>
    values : Map<string, string[]>
}

const Filter = ({parameters, values} : FilterProperties) => {

    const [multipleInput, setMultipleInput] = useState<string>("");
    const [multipleInputList, setMultipleInputList] = useState<string[]>([]);


    function getDate(dateParameter : string){
        if(!dateParameter) return "";

        const date = new Date(Number(dateParameter));
        return date.toISOString().split("T")[0];
    }

    function dateFormattedToStr(dateParameter : string) : string{
        return new Date(dateParameter).getTime().toString();
    }

    return (
        <div>
            {Array.from(parameters.entries()).map( ([label, fieldValue]) => (
                <div>
                    {fieldValue.fieldType == FieldType.SingleInput ?
                        <div>
                            <strong>{label}</strong>
                            <input className="form-control m-2" value={values.get(label)} onChange={(e) => fieldValue.setter([e.target.value])}/>
                        </div>
                        :
                        fieldValue.fieldType == FieldType.MultipleInput ?
                        <div>
                            <strong>{label}</strong>
                            <input className="form-control m-2" value={multipleInput} onChange={(e) => setMultipleInput(e.target.value)}/>
                            <button className="btn btn-success" onClick={(e) => {
                                fieldValue.setter([...multipleInputList, multipleInput]);
                                setMultipleInputList([...multipleInputList, multipleInput]);
                                
                            }}>Add</button>

                            {multipleInputList.map(input => <div key={label + "_" +input}>
                                <span>{input}</span>
                                <button 
                                    onClick={(e) => {
                                        fieldValue.setter(multipleInputList.filter(val => val != input));
                                        setMultipleInputList(multipleInputList.filter(val => val != input));
                                    }} 
                                    className="btn btn-danger"
                                >X</button>
                            </div>)}
                        </div>
                        :
                        fieldValue.fieldType == FieldType.SingleSelection ?
                        <div>
                            <strong>{label}</strong>
                            <select 
                                className="form-control m-2"
                                value={values.get(label)}
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
                            <strong>{label}</strong>

                            <select className="form-control m-2" multiple value={values.get(label)} onChange={e => {
                                const currentValue = values.get(label) ?? [];
                                console.log(currentValue);
                                if(currentValue.includes(e.target.value)){
                                    fieldValue.setter(currentValue.filter(v => v != e.target.value));
                                }else{
                                    fieldValue.setter([...currentValue, e.target.value]);
                                }
                            }}>
                                {fieldValue.additionalData.map(str => <option value={str}>
                                    {str}
                                </option>)}
                            </select>
                            <br/>
                            <button className="btn btn-primary" onClick={e => fieldValue.setter([])}>Deselect</button>

                        </div>
                        :
                        fieldValue.fieldType == FieldType.SingleDate ? 
                        <div>
                            <strong>{label}</strong>
                            <br/>
                            <strong>{ (values.get(label) || [""])[0] 
                                && new Date(Number( (values.get(label) || ["0"])[0] )).toUTCString() }
                            </strong>
                            <br/>
                            Date:
                            <input type="date" className="form-control m-2" 
                                value={getDate((values.get(label) || [""])[0])}
                                onChange={e => {
                                    fieldValue.setter([dateFormattedToStr(e.target.value)]);
                                }}
                                style={{
                                    display: "inline-block",
                                    width: "20%",
                                    margin: "30px"
                                }}
                            />
                            Hours:
                            <input disabled={!((values.get(label) || [""])[0])} type="number" min="0" max="23" className="form-control m-2"
                                value={new Date( Number((values.get(label) || ["0"])[0]) ).getUTCHours()}
                                onChange={e => {
                                    const previousValue = (values.get(label) || [""])[0];
                                    if(previousValue == "") return;
                                    const date = new Date(Number(previousValue));

                                    
                                    date.setUTCHours(Number(e.target.value));
                         
                                    fieldValue.setter([date.getTime().toString()]);
                                }}
                                style={{
                                    display: "inline-block",
                                    width: "20%",
                                    margin: "30px"
                                }}
                            />
                            Minutes:
                            <input disabled={!((values.get(label) || [""])[0])} type="number" min="0" max="60" className="form-control m-2"
                                value={new Date( Number((values.get(label) || ["0"])[0]) ).getMinutes()}
                                onChange={e => {
                                    const previousValue = (values.get(label) || [""])[0];
                                    if(previousValue == "") return;
                                    const date = new Date(Number(previousValue));
                                    date.setMinutes(Number(e.target.value));
                         
                                    fieldValue.setter([date.getTime().toString()]);
                                }}
                                style={{
                                    display: "inline-block",
                                    width: "20%",
                                    margin: "30px"
                                }}
                            />
                            Seconds:
                            <input disabled={!((values.get(label) || [""])[0])} type="number" min="0" max="60" className="form-control m-2"
                                value={new Date( Number((values.get(label) || ["0"])[0]) ).getSeconds()}
                                onChange={e => {
                                    const previousValue = (values.get(label) || [""])[0];
                                    if(previousValue == "") return;
                                    const date = new Date(Number(previousValue));
                                    date.setSeconds(Number(e.target.value));
                         
                                    fieldValue.setter([date.getTime().toString()]);
                                }}
                                style={{
                                    display: "inline-block",
                                    width: "20%",
                                    margin: "30px"
                                }}
                            />
                            Milliseconds:
                            <input disabled={!((values.get(label) || [""])[0])} type="number" min="0" max="999" className="form-control m-2"
                                value={new Date( Number((values.get(label) || ["0"])[0]) ).getMilliseconds()}
                                onChange={e => {
                                    const previousValue = (values.get(label) || [""])[0];
                                    if(previousValue == "") return;
                                    const date = new Date(Number(previousValue));
                                    date.setMilliseconds(Number(e.target.value));
                         
                                    fieldValue.setter([date.getTime().toString()]);
                                }}
                                style={{
                                    display: "inline-block",
                                    width: "20%",
                                    margin: "30px"
                                }}
                            />
                           
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