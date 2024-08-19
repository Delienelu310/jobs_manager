import "../../css/components/notificator.css"


import { createContext, ReactNode, useContext, useEffect, useState } from "react";
import NotificationElement from "./NotificationElement";
import { AxiosError } from "axios";


export interface NotificatorContextValues {
    pushNotification : (config : NotificationConfig ) => void,
    deleteById : (id : number) => void,
    clearNotifications : () => void,
    catchRequestError : (e : AxiosError<String>) => void
}

const NotificatorContext = createContext<NotificatorContextValues>({
    pushNotification: (cfg) => {},
    deleteById: (id) => {},
    clearNotifications : () => {},
    catchRequestError: (e) => {}
});
export const useNotificator = () => useContext(NotificatorContext);

export enum NotificationType{
    ERROR, INFO
}

export const TypeToCss = new Map<NotificationType, string>([
    [NotificationType.ERROR ,"notification_error"],
    [NotificationType.INFO, "notification_info"]
]);

export interface NotificationConfig{
    message : string,
    time : number | null,
    type: NotificationType
}

export interface NotificationData{
    id : number,
    config : NotificationConfig
}

const Notificator = ({children} : {children : ReactNode}) => {

    const [notifications, setNotifications] = useState<Map<number, NotificationData>>(new Map<number, NotificationData>());
    const [timers, setTimers] = useState<Map<number, NodeJS.Timeout>>(new Map<number, NodeJS.Timeout>());

    const [counter, setCounter] = useState<number>(0);

    function generateNotificationId() : number{
        const result = counter;
        setCounter(counter + 1);
        return result;
    }

    function pushNotification(config : NotificationConfig) : void{
        
        const id = generateNotificationId();

        if(config.time != null && config.time< 0.5) config.time = 0.5;

        const data : NotificationData = {
            id : id,
            config : config
        }

        const newMap = new Map<number, NotificationData>(notifications);
        newMap.set(id, data);
        setNotifications(newMap);

        if(config.time != null){
            const timeOutId = setTimeout(() => {
                deleteById(id);
            }, 1000 * config.time);
            
            const newTimers = new Map<number, NodeJS.Timeout>(timers);
            newTimers.set(id, timeOutId);
            setTimers(newTimers);
        }

    }

    function deleteById(id : number) : void{


        //remove from notifications map objects

        

        setNotifications(oldMap => {
            const newMap = new Map<number, NotificationData>(oldMap);
            newMap.delete(id);
            return newMap;
        });

        //delete timer and remove it

        const timer = timers.get(id);
        if(timer !== undefined){
            clearTimeout(timer);
        } 

        setTimers(oldTimers => {
            const newTimerMap = new Map<number, NodeJS.Timeout>(oldTimers);
            newTimerMap.delete(id);
            return newTimerMap;
        });

    }


    function clearNotifications() : void{
        //clear notification map
        setNotifications(new Map<number, NotificationData>());
    
        //cancel all the timers
        timers.forEach(timer => clearTimeout(timer));

        setTimers(new Map<number, NodeJS.Timeout>())

    }

    
    function catchRequestError(e : AxiosError<String>){
        pushNotification({
            message: (e.response?.data ?? "Unexpected Error") + "",
            time : 5,
            type: NotificationType.ERROR
        });
    }

    return (
        <NotificatorContext.Provider
            value={{
                pushNotification,
                deleteById,
                clearNotifications,
                catchRequestError

            }}
        >
            {notifications.size > 0 && <div className="notificator">

                <button className="btn btn-danger" onClick={clearNotifications}>Clear All</button>

                {Array.from(notifications.entries()).sort(( [key1], [key2]) => key1 - key2).map( ([id, data]) => (
                    <NotificationElement key={id} data={data}/>
                ))}

            </div>}


            {children}
        </NotificatorContext.Provider>
        
    );
}

export default Notificator;