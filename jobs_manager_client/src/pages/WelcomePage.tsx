import { useAuth } from "../authentication/AuthContext";
import { Navigate } from "react-router-dom";
import { NotificationType, useNotificator } from "../components/notifications/Notificator";
import { useState } from "react";


export default function WelcomePage() {
    const {authentication} = useAuth();
    const {pushNotification} = useNotificator();
    
    
    
    const [isError, setIsError] = useState<boolean>(false);
    const [time, setTime] = useState<number>(5);
    
    
    
    if(authentication == null){
        return <Navigate to="/login"/>
    }

    return (
        <div>
            <h2>Welcome</h2>

            <input type="checkbox" checked={isError} onChange={(e) => setIsError(e.target.checked)}/>
            <br/>
            <input type="number" value={time} onChange={e => setTime(Number(e.target.value))}/>
            <br/>
            <button className="btn btn-primary m-3" onClick={() => {
                pushNotification({
                    message: `test message ${Math.random()}`,
                    time: time,
                    type: isError ? NotificationType.ERROR : NotificationType.INFO 
                })
            }}>Test Notifications</button>
        </div>
    );
}