import request from './request'

// Favorites
export const getFavorites = (params) => request.get('/favorites', { params })
export const addFavorite = (productId) => request.post(`/favorites/${productId}`)
export const removeFavorite = (productId) => request.delete(`/favorites/${productId}`)
export const getFavoriteStatus = (productId) => request.get(`/favorites/${productId}/status`)

// Browse history
export const getHistory = (params) => request.get('/history', { params })
export const recordHistory = (productId) => request.post(`/history/${productId}`)
export const deleteHistory = (productId) => request.delete(`/history/${productId}`)
export const clearHistory = () => request.delete('/history')

// Addresses
export const getAddresses = () => request.get('/addresses')
export const addAddress = (data) => request.post('/addresses', data)
export const updateAddress = (id, data) => request.put(`/addresses/${id}`, data)
export const deleteAddress = (id) => request.delete(`/addresses/${id}`)
export const setDefaultAddress = (id) => request.put(`/addresses/${id}/default`)

// Reviews
export const createReview = (orderId, data) => request.post(`/reviews/orders/${orderId}`, data)
export const getOrderReviews = (orderId) => request.get(`/reviews/orders/${orderId}`)
export const getProductReviews = (productId, params) => request.get(`/reviews/products/${productId}`, { params })
