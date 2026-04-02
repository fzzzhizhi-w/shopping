<template>
  <div class="order-detail-page page-container" v-loading="loading">
    <div class="page-header">
      <el-button link @click="router.back()">
        <el-icon><ArrowLeft /></el-icon> 返回
      </el-button>
      <h2 class="page-heading">订单详情</h2>
    </div>

    <template v-if="!loading && order.id">
      <!-- Status Banner -->
      <div class="status-banner" :class="`status-${order.status?.toLowerCase()}`">
        <el-icon :size="36" class="status-icon"><component :is="getStatusIcon(order.status)" /></el-icon>
        <div>
          <div class="status-label">{{ getStatusLabel(order.status) }}</div>
          <div class="status-desc">{{ getStatusDesc(order.status) }}</div>
        </div>
        <div class="status-actions">
          <el-button
            type="danger"
            v-if="order.status === 'PENDING'"
            @click="handlePay"
            :loading="actionLoading"
          >
            立即支付
          </el-button>
          <el-button
            v-if="order.status === 'PENDING'"
            @click="handleCancel"
            :loading="actionLoading"
          >
            取消订单
          </el-button>
        </div>
      </div>

      <div class="detail-sections">
        <!-- Order Info -->
        <el-card class="info-card">
          <template #header><span class="card-title">订单信息</span></template>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="订单编号">
              <span class="order-no">{{ order.orderNo || order.id }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="下单时间">{{ formatTime(order.createTime) }}</el-descriptions-item>
            <el-descriptions-item label="订单状态">
              <el-tag :type="getStatusType(order.status)" size="small">
                {{ getStatusLabel(order.status) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="支付时间" v-if="order.payTime">
              {{ formatTime(order.payTime) }}
            </el-descriptions-item>
          </el-descriptions>
        </el-card>

        <!-- Delivery Info -->
        <el-card class="info-card">
          <template #header><span class="card-title">收货信息</span></template>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="收货人">{{ order.receiverName }}</el-descriptions-item>
            <el-descriptions-item label="手机号">{{ order.receiverPhone }}</el-descriptions-item>
            <el-descriptions-item label="收货地址" :span="2">{{ order.receiverAddress }}</el-descriptions-item>
            <el-descriptions-item label="备注" v-if="order.remark" :span="2">{{ order.remark }}</el-descriptions-item>
          </el-descriptions>
        </el-card>

        <!-- Order Items -->
        <el-card class="info-card">
          <template #header><span class="card-title">商品清单</span></template>
          <el-table :data="order.orderItems || order.items || []" style="width: 100%">
            <el-table-column label="商品" min-width="280">
              <template #default="{ row }">
                <div class="product-cell" @click="router.push(`/products/${row.productId || row.product?.id}`)">
                  <img
                    :src="row.product?.mainImage || row.image || 'https://via.placeholder.com/70x70'"
                    class="item-thumb"
                    @error="onImgError"
                  />
                  <span class="item-name">{{ row.product?.name || row.productName || row.name }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="单价" width="120">
              <template #default="{ row }">
                <span class="item-price">¥{{ formatPrice(row.price) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="数量" width="80" prop="quantity" align="center" />
            <el-table-column label="小计" width="120">
              <template #default="{ row }">
                <span class="subtotal">¥{{ formatPrice(row.price * row.quantity) }}</span>
              </template>
            </el-table-column>
          </el-table>

          <!-- Total -->
          <div class="order-totals">
            <div class="total-row">
              <span>商品合计</span>
              <span>¥{{ formatPrice(order.totalAmount) }}</span>
            </div>
            <div class="total-row">
              <span>运费</span>
              <span>¥0.00</span>
            </div>
            <div class="total-row final">
              <span>实付金额</span>
              <span class="final-amount">¥{{ formatPrice(order.totalAmount) }}</span>
            </div>
          </div>
        </el-card>
      </div>
    </template>

    <el-empty v-if="!loading && !order.id" description="订单不存在" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrderDetail, payOrder, cancelOrder } from '@/api/order'

const route = useRoute()
const router = useRouter()

const order = ref({})
const loading = ref(false)
const actionLoading = ref(false)

const statusMap = {
  PENDING: { label: '待付款', type: 'warning', desc: '请尽快完成支付', icon: 'Timer' },
  PAID: { label: '待发货', type: 'primary', desc: '商家正在处理您的订单', icon: 'Goods' },
  SHIPPED: { label: '待收货', type: 'primary', desc: '商品正在配送中，请耐心等待', icon: 'Van' },
  COMPLETED: { label: '已完成', type: 'success', desc: '订单已完成，感谢您的购买！', icon: 'CircleCheck' },
  CANCELLED: { label: '已取消', type: 'info', desc: '订单已取消', icon: 'CircleClose' }
}

const getStatusLabel = (s) => statusMap[s]?.label || s || '未知'
const getStatusType = (s) => statusMap[s]?.type || 'info'
const getStatusDesc = (s) => statusMap[s]?.desc || ''
const getStatusIcon = (s) => statusMap[s]?.icon || 'InfoFilled'

const formatPrice = (v) => (v == null ? '0.00' : Number(v).toFixed(2))

const formatTime = (t) => {
  if (!t) return '-'
  return new Date(t).toLocaleString('zh-CN')
}

const onImgError = (e) => { e.target.src = 'https://via.placeholder.com/70x70?text=图' }

const handlePay = async () => {
  actionLoading.value = true
  try {
    await payOrder(order.value.id)
    ElMessage.success('支付成功！')
    const res = await getOrderDetail(order.value.id)
    order.value = res.data || {}
  } catch {
    // ignore
  } finally {
    actionLoading.value = false
  }
}

const handleCancel = async () => {
  await ElMessageBox.confirm('确定要取消该订单吗？', '提示', { type: 'warning' })
  actionLoading.value = true
  try {
    await cancelOrder(order.value.id)
    ElMessage.success('订单已取消')
    const res = await getOrderDetail(order.value.id)
    order.value = res.data || {}
  } catch {
    // ignore
  } finally {
    actionLoading.value = false
  }
}

onMounted(async () => {
  loading.value = true
  try {
    const res = await getOrderDetail(route.params.id)
    order.value = res.data || {}
  } catch {
    ElMessage.error('获取订单详情失败')
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.order-detail-page {
  padding-top: 24px;
  padding-bottom: 40px;
}
.page-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
}
.page-heading {
  font-size: 22px;
  font-weight: 700;
  color: #333;
  margin: 0;
}
.status-banner {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 24px 28px;
  border-radius: 10px;
  margin-bottom: 20px;
  color: #fff;
}
.status-banner.status-pending { background: linear-gradient(135deg, #e67e22, #f39c12); }
.status-banner.status-paid { background: linear-gradient(135deg, #2980b9, #3498db); }
.status-banner.status-shipped { background: linear-gradient(135deg, #16a085, #1abc9c); }
.status-banner.status-completed { background: linear-gradient(135deg, #27ae60, #2ecc71); }
.status-banner.status-cancelled { background: linear-gradient(135deg, #7f8c8d, #95a5a6); }
.status-icon {
  opacity: 0.9;
}
.status-label {
  font-size: 22px;
  font-weight: 700;
  margin-bottom: 4px;
}
.status-desc {
  font-size: 13px;
  opacity: 0.85;
}
.status-actions {
  margin-left: auto;
  display: flex;
  gap: 12px;
}
.detail-sections {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.info-card {
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}
.card-title {
  font-size: 16px;
  font-weight: 700;
  color: #333;
}
.order-no {
  font-family: monospace;
  font-size: 13px;
  color: #555;
}
.product-cell {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
}
.item-thumb {
  width: 64px;
  height: 64px;
  object-fit: cover;
  border-radius: 6px;
  border: 1px solid #eee;
}
.item-name {
  font-size: 13px;
  color: #333;
  line-height: 1.4;
}
.item-name:hover { color: #c0392b; }
.item-price { color: #666; font-size: 14px; }
.subtotal { color: #c0392b; font-weight: 600; }
.order-totals {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 8px;
}
.total-row {
  display: flex;
  gap: 40px;
  font-size: 14px;
  color: #666;
  min-width: 250px;
  justify-content: space-between;
}
.total-row.final {
  font-size: 16px;
  font-weight: 700;
  color: #333;
}
.final-amount {
  color: #c0392b;
  font-size: 24px;
  font-weight: 800;
}
</style>
