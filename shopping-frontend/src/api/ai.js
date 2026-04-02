import request from './request'

export const chat = (data) => request.post('/ai/chat', data)
export const getChatHistory = (sessionId) => request.get('/ai/history', { params: { sessionId } })
export const clearHistory = (sessionId) => request.delete('/ai/history', { params: { sessionId } })
export const getRecommendations = (params) => request.get('/recommend', { params })
