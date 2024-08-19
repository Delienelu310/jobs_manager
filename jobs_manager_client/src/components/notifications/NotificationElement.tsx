import { NotificationData, TypeToCss, useNotificator } from "./Notificator";
import "../../css/components/notificator.css"

const NotificationElement = ({data} : {data : NotificationData}) => {
    
    const {deleteById} = useNotificator();
    
    return (
        <div className={`notification ${TypeToCss.get(data.config.type) ?? ""}`}>
            <button className="btn btn-danger" onClick={() => deleteById(data.id)}>X</button>

            <strong><pre>{data.config.message || "No message specified"}</pre></strong>

        </div>
    );
}


export default NotificationElement;