import {FormPost, Get} from "../../../http";

export const getUserList = async (page) => {
    return Get({
        url: '/user/list',
        params: {page}
    })
}
export const closeAccount = async (id) => {
    return FormPost({
        url: '/user/close',
        data: {id: id}
    })
}
export const activeAccount = async (id) => {
    return FormPost({
        url: '/user/active',
        data: {id: id}
    })
}
export const getRegCountByDay = async (num) => {
    return Get({
        url: '/user/count',
        params: {num}
    })
}
export const getUserCount = async () => {
    return Get({
        url: '/user/count'
    })
}