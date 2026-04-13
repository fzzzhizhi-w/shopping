<template>
  <div class="admin-review-page page-container">
    <h2 class="page-heading">评价管理</h2>

    <el-table :data="reviews" v-loading="loading" border stripe>
      <el-table-column prop="review.id" label="ID" width="70" />
      <el-table-column prop="productName" label="商品" min-width="160" show-overflow-tooltip />
      <el-table-column prop="review.userId" label="用户ID" width="90" />
      <el-table-column prop="review.rating" label="评分" width="100">
        <template #default="{ row }">
          <el-rate :model-value="row.review?.rating" disabled size="small" />
        </template>
      </el-table-column>
      <el-table-column prop="review.content" label="内容" min-width="200" show-overflow-tooltip />
      <el-table-column prop="review.createTime" label="时间" width="160">
        <template #default="{ row }">{{ formatTime(row.review?.createTime) }}</template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="pageNum"
      v-model:page-size="pageSize"
      :total="total"
      :page-sizes="[10, 20, 50]"
      layout="total, sizes, prev, pager, next"
      class="pagination"
      @size-change="load"
      @current-change="load"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { adminListReviews } from '@/api/admin'

const loading = ref(false)
const reviews = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const formatTime = (t) => t ? new Date(t).toLocaleString('zh-CN', { hour12: false }) : ''

const load = async () => {
  loading.value = true
  try {
    const res = await adminListReviews({ pageNum: pageNum.value, pageSize: pageSize.value })
    reviews.value = res.data?.list || []
    total.value = res.data?.total || 0
  } finally { loading.value = false }
}

onMounted(load)
</script>

<style scoped>
.admin-review-page { padding: 24px 0; }
.page-heading { font-size: 22px; font-weight: 700; color: #333; margin-bottom: 16px; }
.pagination { margin-top: 20px; justify-content: flex-end; }
</style>
