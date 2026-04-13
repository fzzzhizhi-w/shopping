<template>
  <div class="history-page page-container">
    <div class="page-header">
      <h2 class="page-heading">浏览历史</h2>
      <el-button type="danger" plain size="small" :disabled="list.length === 0" @click="handleClear">清空历史</el-button>
    </div>

    <div v-loading="loading" class="product-grid">
      <el-empty v-if="!loading && list.length === 0" description="暂无浏览记录" />
      <div v-for="item in list" :key="item.historyId" class="history-card">
        <div class="img-wrap" @click="router.push(`/products/${item.product?.id}`)">
          <img
            :src="item.product?.mainImage || 'https://via.placeholder.com/200x200'"
            class="product-img"
            @error="onImgError"
          />
        </div>
        <div class="card-info">
          <div class="product-name" @click="router.push(`/products/${item.product?.id}`)">
            {{ item.product?.name }}
          </div>
          <div class="product-price">¥{{ item.product?.price }}</div>
          <div class="view-time">{{ formatTime(item.viewTime) }}</div>
          <div class="card-actions">
            <el-button size="small" type="primary" @click="router.push(`/products/${item.product?.id}`)">再看看</el-button>
            <el-button size="small" @click="handleDelete(item)">删除</el-button>
          </div>
        </div>
      </div>
    </div>

    <el-pagination
      v-if="total > pageSize"
      v-model:current-page="pageNum"
      :page-size="pageSize"
      :total="total"
      layout="prev, pager, next"
      class="pagination"
      @current-change="loadHistory"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getHistory, deleteHistory, clearHistory } from '@/api/user'

const router = useRouter()
const loading = ref(false)
const list = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(20)

const formatTime = (t) => {
  if (!t) return ''
  return new Date(t).toLocaleString('zh-CN', { hour12: false })
}

const loadHistory = async () => {
  loading.value = true
  try {
    const res = await getHistory({ pageNum: pageNum.value, pageSize: pageSize.value })
    list.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch {
    // handled
  } finally {
    loading.value = false
  }
}

const handleDelete = async (item) => {
  try {
    await deleteHistory(item.product?.id)
    ElMessage.success('已删除')
    loadHistory()
  } catch {
    // handled
  }
}

const handleClear = async () => {
  await ElMessageBox.confirm('确定清空所有浏览历史吗？', '提示', {
    confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
  }).catch(() => { throw new Error('cancel') })
  try {
    await clearHistory()
    ElMessage.success('已清空')
    loadHistory()
  } catch (e) {
    if (e.message !== 'cancel') { /* handled */ }
  }
}

const onImgError = (e) => {
  e.target.src = 'https://via.placeholder.com/200x200?text=No+Image'
}

onMounted(loadHistory)
</script>

<style scoped>
.history-page {
  padding: 24px 0;
}
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.page-heading {
  font-size: 22px;
  font-weight: 700;
  color: #333;
  margin: 0;
}
.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
}
.history-card {
  border: 1px solid #eee;
  border-radius: 8px;
  overflow: hidden;
  background: #fff;
}
.img-wrap {
  cursor: pointer;
  height: 160px;
  overflow: hidden;
}
.product-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}
.img-wrap:hover .product-img {
  transform: scale(1.05);
}
.card-info {
  padding: 10px;
}
.product-name {
  font-size: 13px;
  color: #333;
  cursor: pointer;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-bottom: 4px;
}
.product-name:hover { color: #c0392b; }
.product-price {
  color: #c0392b;
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 4px;
}
.view-time {
  font-size: 11px;
  color: #999;
  margin-bottom: 8px;
}
.card-actions {
  display: flex;
  gap: 6px;
}
.pagination {
  margin-top: 24px;
  justify-content: center;
}
</style>
