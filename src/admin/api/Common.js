import {FormPost, Get} from "../../../http";

export const getAdList = async () => {
    return Get({
        url: '/statistic/ad'
    })
}
export const addAd = async (ad, image) => {
    let data = new FormData
    data.append("ad", JSON.stringify(ad))
    data.append("image", image)
    return FormPost({
        url: '/statistic/ad/add',
        data: data
    })
}
export const updateAd = async (ad, image) => {
    let data = new FormData;
    data.append("ad", JSON.stringify(ad));
    data.append("image", image);
    return FormPost({
        url: '/statistic/ad/update',
        data: data
    })
}
export const delAd = async (id) => {
    return FormPost({
        url: '/statistic/ad/delete',
        data: {
            id: id
        }
    })
}
export const getViews = async () => {
    return Get({
        url: '/statistic/view'
    })
}
