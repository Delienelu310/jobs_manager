import { AxiosResponse } from "axios";
import apiClient from "../ApiClient";


export async function deleteChannel(projectId : string, channelId : string) : Promise<AxiosResponse<void>>{
    return apiClient.delete(`/projects/${projectId}/channels/${channelId}`);
}