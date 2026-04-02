<template>
  <div class="orders-page page-container">
    <h2 class="page-heading">我的订单</h2>

    <el-tabs v-model="activeStatus" @tab-change="handleTabChange" class="order-tabs">
      <el-tab-pane label="全部" name="" />
      <el-tab-pane label="待付款" name="PENDING" />
      <el-tab-pane label="待发货" name="PAID" />
      <el-tab-pane label="待收货" name="SHIPPED" />
      <el-tab-pane label="已完成" name="COMPLETED" />
      <el-tab-pane label="已取消" name="CANCELLED" />
    </el-tabs>

    <div v-loading="loading" class="orders-list">
      <el-empty v-if="!loading && orders.length === 0" description="暂无订单" />

      <div v-for="order in orders" :key="order.id" class="order-card">
        <!-- Order Header -->
        <div class="order-header">
          <div class="order-meta">
            <span class="order-no">订单号: {{ order.orderNo || order.id }}</span>
            <span class="order-time">{{ formatTime(order.createTime) }}</span>
          </div>
          <el-tag :type="getStatusType(order.status)" size="small">
            {{ getStatusLabel(order.status) }}
          </el-tag>
        </div>

        <!-- Order Items Preview -->
        <div class="order-items" @click="router.push(`/orders/${order.id}`)">
          <div
            v-for="item in (order.orderItems || order.items || []).slice(0, 3)"
            :key="item.id"
            class="order-item"
          >
            <img
              :src="item.product?.mainImage || item.image || 'https://via.placeholder.com/60x60'"
              class="item-thumb"
              @error="onImgError"
            />
            <div class="item-info">
              <div class="item-name">{{ item.product?.name || item.productName || item.name }}</div>
              <div class="item-price-qty">¥{{ formatPrice(item.price) }} × {{ item.quantity }}</div>
            </div>
          </div>
          <div v-if="(order.orderItems || order.items || []).length > 3" class="more-items">
            +{{ (order.orderItems || order.items || []).length - 3 }} 件
          </div>
        </div>

        <!-- Order Footer -->
        <div class="order-footer">
          <div class="order-total">
            共 {{ (order.orderItems || order.items || []).reduce((s, i) => s + i.quantity, 0) || 0 }} 件商品，
            合计: <span class="total-amount">¥{{ formatPrice(order.totalAmount) }}</span>
          </div>
          <div class="order-actions">
            <el-button size="small" @click="router.push(`/orders/${order.id}`)">查看详情</el-button>
            <el-button
              size="small"
              type="danger"
              v-if="order.status === 'PENDING'"
              @click="handlePay(order)"
              :loading="payingId === order.id"
            >
              去付款
            </el-button>
            <el-button
              size="small"
              v-if="order.status === 'PENDING'"
              @click="handleCancel(order)"
            >
              取消订单
            </el-button>
            <el-button
              size="small"
              type="success"
              v-if="order.status === 'SHIPPED'"
              @click="handleConfirmReceive(order)"
            >
              确认收货
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <div class="pagination-wrap" v-if="total > 0">
      <el-pagination
        v-model:current-page="currentPage"
        :page-size="pageSize"
        layout="prev, pager, next"
        :total="total"
        background
        @current-change="fetchOrders"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrders, cancelOrder, payOrder } from '@/api/order'

const router = useRouter()

const orders = ref([])
const loading = ref(false)
const activeStatus = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const payingId = ref(null)

const statusMap = {
  PENDING: { label: '待付款', type: 'warning' },
  PAID: { label: '待发货', type: 'primary' },
  SHIPPED: { label: '待收货', type: 'primary' },
  COMPLETED: { label: '已完成', type: 'success' },
  CANCELLED: { label: '已取消', type: 'info' }
}

const getStatusLabel = (s) => statusMap[s]?.label || s || '未知'
const getStatusType = (s) => statusMap[s]?.type || 'info'

const formatPrice = (v) => (v == null ? '0.00' : Number(v).toFixed(2))

const formatTime = (t) => {
  if (!t) return ''
  const d = new Date(t)
  return d.toLocaleString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })
}

const onImgError = (e) => { e.target.src = 'https://via.placeholder.com/60x60?text=图' }

const fetchOrders = async () => {
  loading.value = true
  try {
    const params = { page: currentPage.value, size: pageSize.value }
    if (activeStatus.value) params.status = activeStatus.value
    const res = await getOrders(params)
    const data = res.data || {}
    orders.value = data.records || data.list || data.content || data || []
    total.value = data.total || orders.value.length
  } catch {
    // ignore
  } finally {
    loading.value = false
  }
}

const handleTabChange = () => {
  currentPage.value = 1
  fetchOrders()
}

const handlePay = async (order) => {
  payingId.value = order.id
  try {
    await payOrder(order.id)
    ElMessage.success('支付成功！')
    fetchOrders()
  } catch {
    // ignore
  } finally {
    payingId.value = null
  }
}

const handleCancel = async (order) => {
  await ElMessageBox.confirm('确定要取消该订单吗？', '提示', { type: 'warning' })
  try {
    await cancelOrder(order.id)
    ElMessage.success('订单已取消')
    fetchOrders()
  } catch {
    // ignore
  }
}

const handleConfirmReceive = async (order) => {
  await ElMessageBox.confirm('确认已收到商品？', '提示', { type: 'info' })
  try {
    const { default: req } = await import('@/api/request')
    await req.put(`/orders/${order.id}/complete`)
    ElMessage.success('确认收货成功')
    fetchOrders()
  } catch {
    // ignore
  }
}

onMounted(fetchOrders)
</script>

<style scoped>
.orders-page {
  padding-top: 24px;
  padding-bottom: 40px;
}
.page-heading {
  font-size: 22px;
  font-weight: 700;
  color: #333;
  margin-bottom: 16px;
}
.order-tabs {
  background: #fff;
  border-radius: 10px 10px 0 0;
  padding: 0 16px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}
.orders-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding-top: 16px;
  min-height: 200px;
}
.order-card {
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
  overflow: hidden;
  transition: box-shadow 0.2s;
}
.order-card:hover {
  box-shadow: 0 4px 16px rgba(0,0,0,0.1);
}
.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 20px;
  background: #fafafa;
  border-bottom: 1px solid #f0f0f0;
}
.order-meta {
  display: flex;
  align-items: center;
  gap: 20px;
}
.order-no {
  font-size: 13px;
  color: #666;
  font-family: monospace;
}
.order-time {
  font-size: 12px;
  color: #999;
}
.order-items {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 20px;
  cursor: pointer;
  flex-wrap: wrap;
}
.order-item {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-shrink: 0;
}
.item-thumb {
  width: 64px;
  height: 64px;
  object-fit: cover;
  border-radius: 6px;
  border: 1px solid #eee;
}
.item-info {
  max-width: 140px;
}
.item-name {
  font-size: 12px;
  color: #333;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.item-price-qty {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}
.more-items {
  font-size: 12px;
  color: #999;
  padding: 4px 8px;
  background: #f5f5f5;
  border-radius: 4px;
}
.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 20px;
  border-top: 1px solid #f0f0f0;
  background: #fafafa;
}
.order-total {
  font-size: 13px;
  color: #666;
}
.total-amount {
  color: #c0392b;
  font-weight: 700;
  font-size: 18px;
}
.order-actions {
  display: flex;
  gap: 8px;
}
.pagination-wrap {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}
</style>
