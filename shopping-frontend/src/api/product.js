import request from './request'

export const getProducts = (params) => request.get('/products', { params })
export const getProductDetail = (id) => request.get(`/products/${id}`)
export const getHotProducts = () => request.get('/products/hot')
export const getNewProducts = () => request.get('/products/new')
export const searchProducts = (params) => request.get('/products/search', { params })
export const getCategories = () => request.get('/categories')
