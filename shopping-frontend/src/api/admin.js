import request from './request'

// Admin Product APIs
export const adminListProducts = (params) => request.get('/admin/products', { params })
export const adminCreateProduct = (data) => request.post('/admin/products', data)
export const adminUpdateProduct = (id, data) => request.put(`/admin/products/${id}`, data)
export const adminDeleteProduct = (id) => request.delete(`/admin/products/${id}`)
export const adminUpdateProductStatus = (id, status) => request.put(`/admin/products/${id}/status`, { status })

// Admin Order APIs
export const adminListOrders = (params) => request.get('/admin/orders', { params })
export const adminGetOrderDetail = (id) => request.get(`/admin/orders/${id}`)
export const adminShipOrder = (id) => request.put(`/admin/orders/${id}/ship`)
export const adminDeliverOrder = (id) => request.put(`/admin/orders/${id}/deliver`)

// Admin Category APIs
export const adminListCategories = () => request.get('/admin/categories')
export const adminCreateCategory = (data) => request.post('/admin/categories', data)
export const adminUpdateCategory = (id, data) => request.put(`/admin/categories/${id}`, data)
export const adminDeleteCategory = (id) => request.delete(`/admin/categories/${id}`)
