<template>
  <div class="merchant-order-page page-container">
    <h2 class="page-heading">我的订单（商家）</h2>

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

    <el-table :data="orders" v-loading="loading" border stripe>
      <el-table-column prop="order.id" label="ID" width="80" />
      <el-table-column prop="order.orderNo" label="订单号" min-width="180" show-overflow-tooltip />
      <el-table-column prop="order.totalAmount" label="总金额" width="110">
        <template #default="{ row }">¥{{ row.order?.totalAmount }}</template>
      </el-table-column>
      <el-table-column prop="order.status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.order?.status)">{{ getStatusLabel(row.order?.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="order.receiverName" label="收件人" width="100" />
      <el-table-column prop="order.receiverPhone" label="联系电话" width="130" />
      <el-table-column prop="order.createTime" label="下单时间" width="160">
        <template #default="{ row }">{{ formatTime(row.order?.createTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{ row }">
          <el-button
            v-if="row.order?.status === 1"
            size="small"
            type="primary"
            @click="handleShip(row.order.id)"
          >发货</el-button>
          <el-tag v-else-if="row.order?.status === 2" type="">已发货</el-tag>
          <el-tag v-else-if="row.order?.status === 3" type="success">已完成</el-tag>
        </template>
      </el-table-column>
    </el-table>

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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/api/request'

const loading = ref(false)
const orders = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const statusFilter = ref(null)

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
    const res = await request.get('/merchant/orders', { params })
    orders.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch {
    // handled
  } finally {
    loading.value = false
  }
}

const handleShip = async (id) => {
  await ElMessageBox.confirm('确认发货？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'info'
  }).catch(() => { throw new Error('cancel') })
  try {
    await request.put(`/merchant/orders/${id}/ship`)
    ElMessage.success('已发货')
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
.merchant-order-page {
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
</style>
