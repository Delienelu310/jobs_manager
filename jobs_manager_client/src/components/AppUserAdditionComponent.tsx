import { JobNodePageRefresh } from "../pages/JobNodePage"


export interface AppUserAdditionComponentContext{
    jobNodePageRefresh : JobNodePageRefresh
}

export interface AppUserAdditionComponentArgs{
    context : AppUserAdditionComponentContext
}

const AppUserAdditionComponent = ({context} : AppUserAdditionComponentArgs) => {
    return (
        <div>

        </div>
    );
    
}

export default AppUserAdditionComponent;