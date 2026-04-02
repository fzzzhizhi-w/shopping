<template>
  <div class="product-list-page page-container">
    <div class="page-layout">
      <!-- Left Sidebar -->
      <aside class="sidebar">
        <el-card class="sidebar-card">
          <template #header>
            <span class="sidebar-title">商品分类</span>
          </template>
          <ul class="cat-list">
            <li
              class="cat-item"
              :class="{ active: !selectedCategoryId }"
              @click="selectCategory(null)"
            >
              全部分类
            </li>
            <li
              v-for="cat in categories"
              :key="cat.id"
              class="cat-item"
              :class="{ active: selectedCategoryId === cat.id }"
              @click="selectCategory(cat.id)"
            >
              {{ cat.name }}
            </li>
          </ul>
        </el-card>

        <el-card class="sidebar-card price-filter">
          <template #header>
            <span class="sidebar-title">价格区间</span>
          </template>
          <div class="price-range">
            <el-input v-model="priceMin" placeholder="最低" size="small" />
            <span class="range-sep">—</span>
            <el-input v-model="priceMax" placeholder="最高" size="small" />
          </div>
          <el-button size="small" type="primary" class="price-btn" @click="applyFilter">确定</el-button>
        </el-card>
      </aside>

      <!-- Right Content -->
      <main class="content-area">
        <!-- Filter Bar -->
        <div class="filter-bar">
          <div class="filter-left">
            <span class="results-count">共 {{ total }} 件商品</span>
            <span class="keyword-tag" v-if="keyword">
              搜索: "{{ keyword }}"
              <el-icon class="close-icon" @click="clearKeyword"><Close /></el-icon>
            </span>
          </div>
          <div class="filter-right">
            <span class="sort-label">排序：</span>
            <el-radio-group v-model="sortBy" size="small" @change="fetchProducts">
              <el-radio-button label="">默认</el-radio-button>
              <el-radio-button label="price_asc">价格↑</el-radio-button>
              <el-radio-button label="price_desc">价格↓</el-radio-button>
              <el-radio-button label="sales">销量</el-radio-button>
              <el-radio-button label="new">最新</el-radio-button>
            </el-radio-group>
          </div>
        </div>

        <!-- Product Grid -->
        <div class="product-grid" v-loading="loading">
          <ProductCard
            v-for="product in products"
            :key="product.id"
            :product="product"
          />
          <el-empty v-if="!loading && products.length === 0" description="没有找到相关商品" />
        </div>

        <!-- Pagination -->
        <div class="pagination-wrap" v-if="total > 0">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[12, 24, 48]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total"
            background
            @size-change="fetchProducts"
            @current-change="fetchProducts"
          />
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import ProductCard from '@/components/ProductCard.vue'
import { getProducts, getCategories } from '@/api/product'

const route = useRoute()
const router = useRouter()

const products = ref([])
const categories = ref([])
const total = ref(0)
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(12)
const sortBy = ref('')
const priceMin = ref('')
const priceMax = ref('')
const keyword = ref('')
const selectedCategoryId = ref(null)

const clearKeyword = () => {
  keyword.value = ''
  router.replace({ query: { ...route.query, keyword: undefined } })
  fetchProducts()
}

const selectCategory = (id) => {
  selectedCategoryId.value = id
  currentPage.value = 1
  router.replace({ query: { ...route.query, categoryId: id || undefined } })
  fetchProducts()
}

const applyFilter = () => {
  currentPage.value = 1
  fetchProducts()
}

const fetchProducts = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value
    }
    if (selectedCategoryId.value) params.categoryId = selectedCategoryId.value
    if (keyword.value) params.keyword = keyword.value
    if (sortBy.value) params.sort = sortBy.value
    if (priceMin.value) params.minPrice = priceMin.value
    if (priceMax.value) params.maxPrice = priceMax.value

    const res = await getProducts(params)
    const data = res.data || {}
    products.value = data.records || data.list || data.content || data || []
    total.value = data.total || products.value.length
  } catch {
    ElMessage.error('获取商品列表失败')
  } finally {
    loading.value = false
  }
}

const fetchCategories = async () => {
  try {
    const res = await getCategories()
    categories.value = res.data || []
  } catch {
    // ignore
  }
}

onMounted(() => {
  keyword.value = route.query.keyword || ''
  selectedCategoryId.value = route.query.categoryId ? Number(route.query.categoryId) : null
  fetchCategories()
  fetchProducts()
})

watch(
  () => route.query,
  (q) => {
    keyword.value = q.keyword || ''
    selectedCategoryId.value = q.categoryId ? Number(q.categoryId) : null
    currentPage.value = 1
    fetchProducts()
  }
)
</script>

<style scoped>
.product-list-page {
  padding-top: 24px;
  padding-bottom: 40px;
}
.page-layout {
  display: flex;
  gap: 20px;
  align-items: flex-start;
}
.sidebar {
  width: 200px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.sidebar-card {
  border-radius: 8px;
}
.sidebar-title {
  font-size: 15px;
  font-weight: 700;
  color: #333;
}
.cat-list {
  list-style: none;
  margin: 0;
  padding: 0;
}
.cat-item {
  padding: 9px 12px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 13px;
  color: #555;
  transition: all 0.15s;
}
.cat-item:hover {
  background: #fff0f0;
  color: #c0392b;
}
.cat-item.active {
  background: #c0392b;
  color: #fff;
  font-weight: 600;
}
.price-range {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
}
.range-sep {
  color: #999;
  flex-shrink: 0;
}
.price-btn {
  width: 100%;
  background: #c0392b;
  border-color: #c0392b;
}
.content-area {
  flex: 1;
  min-width: 0;
}
.filter-bar {
  background: #fff;
  border-radius: 8px;
  padding: 12px 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.06);
  flex-wrap: wrap;
  gap: 8px;
}
.filter-left {
  display: flex;
  align-items: center;
  gap: 12px;
}
.results-count {
  font-size: 13px;
  color: #666;
}
.keyword-tag {
  display: flex;
  align-items: center;
  gap: 4px;
  background: #fff0f0;
  color: #c0392b;
  padding: 2px 10px;
  border-radius: 12px;
  font-size: 13px;
}
.close-icon {
  cursor: pointer;
  font-size: 12px;
}
.close-icon:hover {
  color: #e74c3c;
}
.sort-label {
  font-size: 13px;
  color: #666;
}
.filter-right {
  display: flex;
  align-items: center;
  gap: 8px;
}
.product-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  min-height: 200px;
}
.pagination-wrap {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}
</style>
