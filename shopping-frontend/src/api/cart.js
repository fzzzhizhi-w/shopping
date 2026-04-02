import request from './request'

export const getCart = () => request.get('/cart')
export const addToCart = (data) => request.post('/cart/add', data)
export const updateCart = (cartId, data) => request.put(`/cart/${cartId}`, data)
export const removeFromCart = (cartId) => request.delete(`/cart/${cartId}`)
export const clearCart = () => request.delete('/cart/clear')
