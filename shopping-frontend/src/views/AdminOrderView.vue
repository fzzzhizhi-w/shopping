<template>
  <div class="admin-order-page page-container">
    <h2 class="page-heading">订单管理</h2>

    <!-- Filter Bar -->
    <div class="filter-bar">
      <el-select v-model="statusFilter" placeholder="订单状态" clearable style="width: 140px" @change="loadOrders">
        <el-option label="待付款" :value="0" />
        <el-option label="已付款" :value="1" />
        <el-option label="已发货" :value="2" />
        <el-option label="已收货" :value="3" />
        <el-option label="已取消" :value="4" />
      </el-select>
      <el-button type="primary" @click="loadOrders">刷新</el-button>
    </div>

    <!-- Order Table -->
    <el-table :data="orders" v-loading="loading" border stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="orderNo" label="订单号" min-width="180" show-overflow-tooltip />
      <el-table-column prop="userId" label="用户ID" width="90" />
      <el-table-column prop="totalAmount" label="总金额" width="110">
        <template #default="{ row }">¥{{ row.totalAmount }}</template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">{{ getStatusLabel(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="receiverName" label="收件人" width="100" />
      <el-table-column prop="receiverPhone" label="联系电话" width="130" />
      <el-table-column prop="createTime" label="下单时间" width="160">
        <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="showDetail(row.id)">详情</el-button>
          <el-button
            v-if="row.status === 1"
            size="small"
            type="primary"
            @click="handleShip(row.id)"
          >发货</el-button>
          <el-button
            v-if="row.status === 2"
            size="small"
            type="success"
            @click="handleDeliver(row.id)"
          >确认收货</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- Pagination -->
    <el-pagination
      v-model:current-page="pageNum"
      v-model:page-size="pageSize"
      :total="total"
      :page-sizes="[10, 20, 50]"
      layout="total, sizes, prev, pager, next"
      class="pagination"
      @size-change="loadOrders"
      @current-change="loadOrders"
    />

    <!-- Order Detail Dialog -->
    <el-dialog v-model="detailVisible" title="订单详情" width="660px">
      <div v-if="currentOrder" class="order-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="订单号">{{ currentOrder.order?.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(currentOrder.order?.status)">
              {{ getStatusLabel(currentOrder.order?.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="用户ID">{{ currentOrder.order?.userId }}</el-descriptions-item>
          <el-descriptions-item label="总金额">¥{{ currentOrder.order?.totalAmount }}</el-descriptions-item>
          <el-descriptions-item label="收件人">{{ currentOrder.order?.receiverName }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ currentOrder.order?.receiverPhone }}</el-descriptions-item>
          <el-descriptions-item label="收货地址" :span="2">{{ currentOrder.order?.address }}</el-descriptions-item>
        </el-descriptions>

        <h4 class="items-title">商品明细</h4>
        <el-table :data="currentOrder.items" border>
          <el-table-column prop="productName" label="商品" min-width="160" />
          <el-table-column prop="price" label="单价" width="100">
            <template #default="{ row }">¥{{ row.price }}</template>
          </el-table-column>
          <el-table-column prop="quantity" label="数量" width="80" />
          <el-table-column label="小计" width="100">
            <template #default="{ row }">¥{{ (row.price * row.quantity).toFixed(2) }}</template>
          </el-table-column>
        </el-table>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminListOrders, adminGetOrderDetail, adminShipOrder, adminDeliverOrder } from '@/api/admin'

const loading = ref(false)
const orders = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const statusFilter = ref(null)
const detailVisible = ref(false)
const currentOrder = ref(null)

const STATUS_MAP = {
  0: { label: '待付款', type: 'warning' },
  1: { label: '已付款', type: 'primary' },
  2: { label: '已发货', type: '' },
  3: { label: '已收货', type: 'success' },
  4: { label: '已取消', type: 'info' }
}

const getStatusLabel = (status) => STATUS_MAP[status]?.label || '未知'
const getStatusType = (status) => STATUS_MAP[status]?.type || ''

const formatTime = (t) => {
  if (!t) return ''
  return new Date(t).toLocaleString('zh-CN', { hour12: false })
}

const loadOrders = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      status: statusFilter.value !== null && statusFilter.value !== undefined ? statusFilter.value : undefined
    }
    const res = await adminListOrders(params)
    const list = res.data?.list || []
    orders.value = list.map((item) => ({ ...(item.order || item), items: item.items || [] }))
    total.value = res.data?.total || 0
  } catch {
    // handled by interceptor
  } finally {
    loading.value = false
  }
}

const showDetail = async (id) => {
  try {
    const res = await adminGetOrderDetail(id)
    currentOrder.value = res.data
    detailVisible.value = true
  } catch {
    // handled
  }
}

const handleShip = async (id) => {
  await ElMessageBox.confirm('确认发货？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'info'
  }).catch(() => { throw new Error('cancel') })
  try {
    await adminShipOrder(id)
    ElMessage.success('已发货')
    loadOrders()
  } catch (e) {
    if (e.message !== 'cancel') { /* handled */ }
  }
}

const handleDeliver = async (id) => {
  await ElMessageBox.confirm('确认已收货？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'info'
  }).catch(() => { throw new Error('cancel') })
  try {
    await adminDeliverOrder(id)
    ElMessage.success('已确认收货')
    loadOrders()
  } catch (e) {
    if (e.message !== 'cancel') { /* handled */ }
  }
}

onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
.admin-order-page {
  padding: 24px 0;
}
.page-heading {
  margin-bottom: 16px;
  font-size: 22px;
  font-weight: 700;
  color: #333;
}
.filter-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}
.pagination {
  margin-top: 20px;
  justify-content: flex-end;
}
.order-detail {
  padding: 0 4px;
}
.items-title {
  margin: 16px 0 8px;
  font-size: 15px;
  color: #333;
}
</style>
