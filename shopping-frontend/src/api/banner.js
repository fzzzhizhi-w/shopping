import request from './request'

export const getBanners = () => request.get('/public/banners')
export const getAds = (position) => request.get('/public/ads', { params: position ? { position } : {} })
