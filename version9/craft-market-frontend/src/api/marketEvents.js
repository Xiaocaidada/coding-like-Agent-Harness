import http from '../utils/http'

/**
 * 市场活动相关API
 */

/**
 * 获取市场活动列表
 * @param {Object} params - 查询参数
 * @param {number} params.current - 当前页码
 * @param {number} params.size - 每页数量
 * @param {string} params.eventName - 活动名称（可选）
 * @param {string} params.status - 活动状态（可选）
 * @returns {Promise} 返回市场活动列表
 */
export const getMarketEvents = (params) => {
  return http.get('/api/market-events', { params })
}

/**
 * 根据ID获取市场活动
 * @param {number} id - 活动ID
 * @returns {Promise} 返回市场活动详情
 */
export const getMarketEventById = (id) => {
  return http.get(`/api/market-events/${id}`)
}

/**
 * 创建市场活动
 * @param {Object} marketEvent - 市场活动对象
 * @param {string} marketEvent.eventName - 活动名称
 * @param {string} marketEvent.eventTime - 活动时间
 * @param {string} marketEvent.location - 活动地点
 * @param {string} marketEvent.description - 活动简介
 * @param {string} marketEvent.posterUrl - 海报URL
 * @param {string} marketEvent.status - 活动状态
 * @returns {Promise} 返回操作结果
 */
export const createMarketEvent = (marketEvent) => {
  return http.post('/api/market-events', marketEvent)
}

/**
 * 更新市场活动
 * @param {number} id - 活动ID
 * @param {Object} marketEvent - 市场活动对象
 * @param {string} marketEvent.eventName - 活动名称
 * @param {string} marketEvent.eventTime - 活动时间
 * @param {string} marketEvent.location - 活动地点
 * @param {string} marketEvent.description - 活动简介
 * @param {string} marketEvent.posterUrl - 海报URL
 * @param {string} marketEvent.status - 活动状态
 * @returns {Promise} 返回操作结果
 */
export const updateMarketEvent = (id, marketEvent) => {
  return http.put(`/api/market-events/${id}`, marketEvent)
}

/**
 * 删除市场活动
 * @param {number} id - 活动ID
 * @returns {Promise} 返回操作结果
 */
export const deleteMarketEvent = (id) => {
  return http.delete(`/api/market-events/${id}`)
}

/**
 * 发布活动
 * @param {number} id - 活动ID
 * @returns {Promise} 返回操作结果
 */
export const publishMarketEvent = (id) => {
  return http.put(`/api/market-events/${id}/publish`)
}

/**
 * 取消活动
 * @param {number} id - 活动ID
 * @returns {Promise} 返回操作结果
 */
export const cancelMarketEvent = (id) => {
  return http.put(`/api/market-events/${id}/cancel`)
}

/**
 * 启用活动
 * @param {number} id - 活动ID
 * @returns {Promise} 返回操作结果
 */
export const enableMarketEvent = (id) => {
  return http.put(`/api/market-events/${id}/enable`)
}

/**
 * 禁用活动
 * @param {number} id - 活动ID
 * @returns {Promise} 返回操作结果
 */
export const disableMarketEvent = (id) => {
  return http.put(`/api/market-events/${id}/disable`)
}

/**
 * 根据状态查询市场活动
 * @param {string} status - 活动状态
 * @param {Object} params - 其他查询参数
 * @returns {Promise} 返回市场活动列表
 */
export const getMarketEventsByStatus = (status, params) => {
  return http.get('/api/market-events', { params: { ...params, status } })
}

/**
 * 搜索市场活动
 * @param {string} keyword - 关键词
 * @param {Object} params - 其他查询参数
 * @returns {Promise} 返回市场活动列表
 */
export const searchMarketEvents = (keyword, params) => {
  return http.get('/api/market-events', { params: { ...params, eventName: keyword } })
}