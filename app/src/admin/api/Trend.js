import {FormPost, Get} from "../../../http";

export const getAdList=async ()=>{
    return Get({
        url:'/trend/ad'
    })
}
export const addAd=async (ad,image)=>{
    let data=new FormData
    data.append("ad",JSON.stringify(ad))
    data.append("image",image)
    return FormPost({
        url:'/trend/ad/add',
        data:data
    })
}