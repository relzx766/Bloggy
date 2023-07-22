import {FormPost, Get, JsonPost} from "../../http";

export const getArticlePage = async (page) => {
    return Get({
        url: '/article/page',
        params: {page}
    })
}
export const getArticleDetail = async (id) => {
    return Get({
        url: '/article/detail',
        params: {id}
    })
}
export const getArticleTrend = async () => {
    return Get({
        url: "/statistic/article"
    })
}
export const postArticle = async (data = {}) => {
    return JsonPost({
        url: "/article/publish",
        data: data
    })
}
export const getArticleIsLike = async (id) => {
    return Get({
        url: '/article/like/get',
        params: {id}
    })
}
export const likeArticle = async (id) => {
    return Get({
        url: '/article/like',
        params: {id}
    })
}
export const cancelLikeArticle = async (id) => {
    return Get({
        url: '/article/like/cancel',
        params: {id}
    })
}
export const getUserArticleList = async (id, page) => {
    return Get({
        url: '/article/user',
        params: {id, page}
    })
}
export const getUserTimeline = async (id) => {
    return Get({
        url: '/article/user/year',
        params: {id}
    })
}
export const getUserArticleByYear = async (id, year) => {
    return Get({
        url: '/article/user/year',
        params: {id, year}
    })
}
export const deleteArticle = async (id) => {
    return FormPost({
        url: '/article/delete',
        data: {
            id: id
        }
    })
}
export const getByTag = async (data) => {
    return FormPost({
        url: '/article/tag',
        data: data
    })
}
export const fuzzySearch = async (keyword, page) => {
    return Get({
        url: '/article/search',
        params: {
            keyword, page
        }
    })
}
export const update = async (data = {}) => {
    return JsonPost({
        url: '/article/update',
        data: data
    })
}
