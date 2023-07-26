import {FormPost, Get} from "../../http";

export const uploadFile = async (data = {}) => {
    return FormPost({
        url: '/file/upload/image',
        data: data
    })
}
export const getAd = async () => {
    return Get({
        url: '/statistic/ad'
    })
}
export const getTrend=async ()=>{
    return Get({
        url:'/statistic/trend'
    })
}