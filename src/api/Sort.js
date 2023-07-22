import {FormPost, Get} from "../../http";

export const createSort = async (title, cover) => {
    let data = new FormData
    data.append("title", title);
    data.append("cover", cover);
    return FormPost({
        url: '/sort/create',
        data: data
    })
}
export const append = async (sortId, articleId) => {
    return FormPost({
        url: '/sort/append',
        data: {
            sortId: sortId,
            articleId: articleId
        }
    })
}
export const remove = async (sortId, articleId) => {
    return FormPost({
        url: '/sort/remove',
        data: {
            sortId: sortId,
            articleId: articleId
        }
    })
}
export const getByUser = async (userId) => {
    return Get({
        url: '/sort/user',
        params: {userId}
    })
}
export const cancelSort = async (articleId) => {
    return FormPost({
        url: '/sort/cancel',
        data: {
            articleId: articleId
        }
    })
}
export const deleteSort = async (id) => {
    return FormPost({
        url: '/sort/del',
        data: {
            id: id
        }
    })
}
export const updateSort = async (id, title, cover) => {
    let data = new FormData();
    data.append("id", id);
    data.append("title", title);
    data.append("cover", cover);
    return FormPost({
        url: '/sort/update',
        data: data
    })
}
export const getDetail = async (id) => {
    return Get({
        url: '/sort/detail',
        params: {id}
    })
}