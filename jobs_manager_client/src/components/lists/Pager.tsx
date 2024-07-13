import React, { useState } from "react";


export interface PagerProperties{
    elementsCount : number,

    pageSize : number,
    setPageSize : React.Dispatch<React.SetStateAction<number>>
    pageChosen : number,
    setPageChosen : React.Dispatch<React.SetStateAction<number>>
}

const Pager = ({pageSize, setPageSize, elementsCount, pageChosen, setPageChosen} : PagerProperties) => {

    return (
        <div>
            <label>Page Size:</label>
            <input type="number" value={pageSize} onChange={(e) => setPageSize(Number.parseInt(e.target.value))}/>

            {Array.of(Math.ceil(elementsCount / pageSize), 0).map((val, index) => (
                <button 
                    onClick={
                        e => setPageChosen(index)
                    }
                    className={pageChosen == index ? "btn btn-success" : "btn btn-primary"}
                >
                    {index + 1}
                </button>
            ))}
        </div>
    )
};

export default Pager;