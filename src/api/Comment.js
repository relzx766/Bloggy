import {FormPost, Get, JsonPost} from "../../http";

export const getComment = async (id, page) => {
    return Get({
        url: '/comment/article',
        params: {id, page}
    })
}
export const getReply = async (id) => {
    return Get({
        url: '/comment/reply',
        params: {id}
    })
}
export const postComment = async (id, content, type) => {
    return JsonPost({
        url: '/comment/article/post',
        data: {
            articleId: id,
            content: content,
            type: type
        }
    })
}
export const postReply = async (id, content, type) => {
    return JsonPost({
        url: 'comment/reply/post',
        data: {
            commentId: id,
            content: content,
            type: type
        }

    })
}
export const likeComment = async (id) => {
    return FormPost({
        url: '/comment/like',
        params: {
            commentId: id
        }
    })
}
export const cancelLikeComment = async (id) => {
    return FormPost({
        url: '/comment/like/cancel',
        params: {
            commentId: id
        }
    })
}
export const getIsLikeComment = async (id) => {
    return Get({
        url: '/comment/like/get',
        params: {id}
    })
}
export const likeReply = async (id) => {
    return FormPost({
        url: '/comment/like/reply',
        params: {
            replyId: id
        }
    })
}
export const cancelLikeReply = async (id) => {
    return FormPost({
        url: '/comment/like/reply/cancel',
        params: {
            replyId: id
        }
    })
}
export const getIsLikeReply = async (id) => {
    return Get({
        url: '/comment/like/reply/get',
        params: {id}
    })
}
