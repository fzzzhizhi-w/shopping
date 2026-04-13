<template>
  <div class="group-buy-page page-container">
    <h2 class="page-heading">我的拼团</h2>

    <div v-loading="loading">
      <el-empty v-if="!loading && groupBuys.length === 0" description="暂无拼团记录" />

      <div v-for="item in groupBuys" :key="item.groupBuy?.id" class="group-buy-card">
        <div class="card-header">
          <div class="product-info">
            <img
              :src="item.product?.mainImage || 'https://via.placeholder.com/60x60'"
              class="product-img"
              @error="onImgError"
            />
            <div class="product-text">
              <div class="product-name">{{ item.product?.name || '商品已下架' }}</div>
              <div class="group-price">拼团价：<span>¥{{ item.groupBuy?.groupPrice }}</span></div>
            </div>
          </div>
          <el-tag :type="getStatusType(item.groupBuy?.status)" size="large">
            {{ getStatusLabel(item.groupBuy?.status) }}
          </el-tag>
        </div>

        <div class="card-body">
          <div class="info-row">
            <span class="label">分享码：</span>
            <span class="share-code">{{ item.groupBuy?.shareCode }}</span>
            <el-button size="small" @click="copyCode(item.groupBuy?.shareCode)">复制</el-button>
          </div>
          <div class="info-row">
            <span class="label">进度：</span>
            <el-progress
              :percentage="Math.min(100, Math.round((item.groupBuy?.currentMembers / item.groupBuy?.minMembers) * 100))"
              :status="item.groupBuy?.status === 1 ? 'success' : undefined"
              style="flex: 1"
            />
            <span class="progress-text">{{ item.groupBuy?.currentMembers }}/{{ item.groupBuy?.minMembers }}人</span>
          </div>
          <div class="info-row">
            <span class="label">到期时间：</span>
            <span>{{ formatTime(item.groupBuy?.expireTime) }}</span>
          </div>
          <div class="info-row">
            <span class="label">身份：</span>
            <el-tag size="small" :type="item.role === 'creator' ? 'danger' : 'info'">
              {{ item.role === 'creator' ? '发起人' : '参与者' }}
            </el-tag>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getMyGroupBuys } from '@/api/groupbuy'

const loading = ref(false)
const groupBuys = ref([])

const STATUS_MAP = {
  0: { label: '进行中', type: 'warning' },
  1: { label: '已成团', type: 'success' },
  2: { label: '已失败', type: 'danger' },
  3: { label: '已过期', type: 'info' }
}

const getStatusLabel = (s) => STATUS_MAP[s]?.label || '未知'
const getStatusType = (s) => STATUS_MAP[s]?.type || ''

const formatTime = (t) => {
  if (!t) return ''
  return new Date(t).toLocaleString('zh-CN', { hour12: false })
}

const onImgError = (e) => {
  e.target.src = 'https://via.placeholder.com/60x60?text=图'
}

const copyCode = (code) => {
  if (!code) return
  navigator.clipboard.writeText(code).then(() => {
    ElMessage.success('分享码已复制')
  }).catch(() => {
    ElMessage.info(`分享码：${code}`)
  })
}

onMounted(async () => {
  loading.value = true
  try {
    const res = await getMyGroupBuys()
    groupBuys.value = res.data || []
  } catch {
    // ignore
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.group-buy-page {
  padding: 24px 0;
}
.page-heading {
  font-size: 22px;
  font-weight: 700;
  color: #333;
  margin-bottom: 20px;
}
.group-buy-card {
  background: #fff;
  border-radius: 10px;
  padding: 20px;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.product-info {
  display: flex;
  align-items: center;
  gap: 12px;
}
.product-img {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 6px;
  border: 1px solid #eee;
}
.product-name {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}
.group-price {
  font-size: 13px;
  color: #666;
}
.group-price span {
  color: #c0392b;
  font-weight: 700;
}
.card-body {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.info-row {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
}
.label {
  color: #999;
  flex-shrink: 0;
  width: 70px;
}
.share-code {
  font-family: monospace;
  font-size: 16px;
  font-weight: 700;
  color: #c0392b;
  letter-spacing: 2px;
}
.progress-text {
  flex-shrink: 0;
  font-size: 13px;
  color: #555;
}
</style>
