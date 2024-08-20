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

 
    function pushNotification(config : NotificationConfig) : void{
        
        setCounter(oldCounter => {

            if(config.time != null && config.time< 0.5) config.time = 0.5;

            const data : NotificationData = {
                id : oldCounter,
                config : config
            }

            
            setNotifications(oldMap => {
                const newMap = new Map<number, NotificationData>(oldMap);
                newMap.set(oldCounter, data);
                return newMap;
            });

            if(config.time != null){
                const timeOutId = setTimeout(() => {
                    deleteById(oldCounter);
                }, 1000 * config.time);
                
                
                setTimers(oldTimers => {
                    const newTimers = new Map<number, NodeJS.Timeout>(oldTimers);
                    newTimers.set(oldCounter, timeOutId);
                    return newTimers
                });
            }

            return oldCounter + 1;
        })

        

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
        setNotifications(old => new Map<number, NotificationData>());
    
        //cancel all the timers
      
        setTimers(old => {
            old.forEach(timer => clearTimeout(timer));
            return new Map<number, NodeJS.Timeout>()
        })

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