import { NotificationData, TypeToCss, useNotificator } from "./Notificator";
import "../../css/components/notificator.css"

const NotificationElement = ({data} : {data : NotificationData}) => {
    
    const {deleteById} = useNotificator();
    
    return (
        <div className={`notification ${TypeToCss.get(data.config.type) ?? ""}`}>

            <strong>{data.config.message.length > 50 ? data.config.message.substring(0, 48) + "..." : data.config.message}</strong>

            <button className="btn btn-danger" onClick={() => deleteById(data.id)}>X</button>

        </div>
    );
}


export default NotificationElement;