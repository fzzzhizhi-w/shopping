import request from './request'

export const createGroupBuy = (data) => request.post('/groupbuy/create', data)
export const joinGroupBuy = (shareCode) => request.post(`/groupbuy/join/${shareCode}`)
export const getGroupBuyDetail = (shareCode) => request.get(`/groupbuy/${shareCode}`)
export const getProductGroupBuys = (productId) => request.get(`/groupbuy/product/${productId}`)
export const getMyGroupBuys = () => request.get('/groupbuy/my')
