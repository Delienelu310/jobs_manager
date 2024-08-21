
export const validatePassword = (value : string) : undefined | string => {


    const symbols = [ 
        '!', '"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/',
        ':', ';', '<', '=', '>', '?', '@', '[', '\\', ']', '^', '_', '`', '{', '|', '}', '~'
    ];
    //1. check sizes

    if(!value) return "Password must not be empty";
    if(value.length < 8 || value.length > 50) return "Password size must be between 8 and 50";
    

    //2. check if the symbols required are there

    let isUpperCasePresent = false;
    let isLowerCasePresent = false;
    let isDigitPresent = false;
    let isSymbolPresent = false;

    for(let char of value){
        if(char >= 'A' && char <= "Z") isUpperCasePresent = true;
        else if(char >= 'a' && char <= 'z') isLowerCasePresent = true;
        else if(char >= '0' && char <= '9') isDigitPresent = true;
        else if(symbols.includes(char)) isSymbolPresent = true;
        else return `Invalid Password: Unexpected letter is used: ${char}`;
    }

    if(!isUpperCasePresent) return "Invalid Password: at least 1 upper case letter is requried";
    if(!isLowerCasePresent) return "Invalid Password: at least 1 lower case letter is rqeuired";
    if(!isDigitPresent) return "Invalid Password: at least 1 digit is required";
    if(!isSymbolPresent) return "Invalid Password: at least 1 symbol is required"; 

    return undefined;
}



export const validateJsonString  = (value : string) : undefined | string => {
    if(value == "") return undefined;

    try{
        let parsed = JSON.parse(value);
        
        return typeof parsed === 'object' && parsed !== null && !Array.isArray(parsed) ? 
            undefined 
            :
            "Not JSON object or empty string";

    }catch(e){
       return  "Not JSON object or empty string";
    }
}