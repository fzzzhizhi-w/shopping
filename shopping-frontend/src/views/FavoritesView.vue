<template>
  <div class="favorites-page page-container">
    <h2 class="page-heading">我的收藏</h2>

    <div v-loading="loading" class="product-grid">
      <el-empty v-if="!loading && list.length === 0" description="暂无收藏商品" />
      <div v-for="item in list" :key="item.favoriteId" class="fav-card">
        <div class="fav-img-wrap" @click="router.push(`/products/${item.product?.id}`)">
          <img
            :src="item.product?.mainImage || 'https://via.placeholder.com/200x200'"
            class="fav-img"
            @error="onImgError"
          />
        </div>
        <div class="fav-info">
          <div class="fav-name" @click="router.push(`/products/${item.product?.id}`)">
            {{ item.product?.name }}
          </div>
          <div class="fav-price">¥{{ item.product?.price }}</div>
          <div class="fav-actions">
            <el-button type="primary" size="small" @click="router.push(`/products/${item.product?.id}`)">查看</el-button>
            <el-button type="danger" size="small" @click="handleRemove(item)">取消收藏</el-button>
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
      @current-change="loadFavorites"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getFavorites, removeFavorite } from '@/api/user'

const router = useRouter()
const loading = ref(false)
const list = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(12)

const loadFavorites = async () => {
  loading.value = true
  try {
    const res = await getFavorites({ pageNum: pageNum.value, pageSize: pageSize.value })
    list.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch {
    // handled
  } finally {
    loading.value = false
  }
}

const handleRemove = async (item) => {
  try {
    await removeFavorite(item.product?.id)
    ElMessage.success('已取消收藏')
    loadFavorites()
  } catch {
    // handled
  }
}

const onImgError = (e) => {
  e.target.src = 'https://via.placeholder.com/200x200?text=No+Image'
}

onMounted(loadFavorites)
</script>

<style scoped>
.favorites-page {
  padding: 24px 0;
}
.page-heading {
  font-size: 22px;
  font-weight: 700;
  color: #333;
  margin-bottom: 20px;
}
.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
}
.fav-card {
  border: 1px solid #eee;
  border-radius: 8px;
  overflow: hidden;
  background: #fff;
  transition: box-shadow 0.2s;
}
.fav-card:hover {
  box-shadow: 0 4px 16px rgba(0,0,0,0.12);
}
.fav-img-wrap {
  cursor: pointer;
  height: 180px;
  overflow: hidden;
}
.fav-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}
.fav-img-wrap:hover .fav-img {
  transform: scale(1.05);
}
.fav-info {
  padding: 10px;
}
.fav-name {
  font-size: 13px;
  color: #333;
  cursor: pointer;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-bottom: 6px;
}
.fav-name:hover {
  color: #c0392b;
}
.fav-price {
  color: #c0392b;
  font-size: 15px;
  font-weight: 600;
  margin-bottom: 8px;
}
.fav-actions {
  display: flex;
  gap: 6px;
}
.pagination {
  margin-top: 24px;
  justify-content: center;
}
</style>
