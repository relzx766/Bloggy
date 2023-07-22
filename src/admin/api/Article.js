import {FormPost, Get} from "../../../http";

export const articleRemove = async (id, reason) => {
    return FormPost({
        url: '/article/remove',
        data: {
            id: id,
            reason: reason
        }
    })
}
export const getArticlePage = async (page) => {
    return Get({
        url: '/article/admin/page',
        params: {page}
    })
}
export const activeArticle = async (id) => {
    return FormPost({
        url: '/article/active',
        data: {
            id: id
        }
    })
}
export const getDetail = async (id) => {
    return Get({
        url: '/article/admin/detail',
        params: {id}
    })
}
export const getPublishCountByDay = async (num) => {
    return Get({
        url: '/article/count',
        params: {num}
    })
}
export const getRecordCount = async () => {
    return Get({
        url: '/article/count'
    })
}