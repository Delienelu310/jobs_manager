
export interface SearchBarParameters{
    queue : string,
    setQueue: React.Dispatch<React.SetStateAction<string>>

}  

const SearchBar = ({queue, setQueue} : SearchBarParameters) => {
    return (
        <div>
            <input value={queue} onChange={e => setQueue(queue)}/>
        </div>
    );
};


export default SearchBar;