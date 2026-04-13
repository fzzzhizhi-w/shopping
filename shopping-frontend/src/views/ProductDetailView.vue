<template>
  <div class="product-detail-page page-container" v-loading="loading">
    <el-breadcrumb separator="/" class="breadcrumb">
      <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/products' }">商品列表</el-breadcrumb-item>
      <el-breadcrumb-item>{{ product.name }}</el-breadcrumb-item>
    </el-breadcrumb>

    <div class="detail-main" v-if="!loading && product.id">
      <!-- Left: Images -->
      <div class="image-section">
        <div class="main-image-wrap">
          <img
            :src="activeImage || `https://via.placeholder.com/480x480?text=${encodeURIComponent(product.name || '商品')}`"
            :alt="product.name"
            class="main-image"
            @error="onImgError"
          />
        </div>
        <div class="thumbnail-list" v-if="images.length > 1">
          <div
            v-for="(img, idx) in images"
            :key="idx"
            class="thumbnail"
            :class="{ active: activeImage === img }"
            @click="activeImage = img"
          >
            <img :src="img" :alt="`图片${idx + 1}`" @error="onThumbError" />
          </div>
        </div>
      </div>

      <!-- Right: Info -->
      <div class="info-section">
        <h1 class="product-name">{{ product.name }}</h1>

        <div class="product-tags" v-if="product.isHot || product.isNew">
          <el-tag type="danger" size="small" v-if="product.isHot">热销</el-tag>
          <el-tag type="warning" size="small" v-if="product.isNew">新品</el-tag>
        </div>

        <div class="price-box">
          <span class="price-label">价格</span>
          <span class="price-value">
            <span class="price-symbol">¥</span>
            <span class="price-num">{{ formatPrice(product.price) }}</span>
          </span>
          <span class="original-price" v-if="product.originalPrice && product.originalPrice > product.price">
            ¥{{ formatPrice(product.originalPrice) }}
          </span>
        </div>

        <div class="meta-row">
          <div class="meta-item">
            <span class="meta-label">销量</span>
            <span class="meta-val">{{ product.sales || 0 }} 件</span>
          </div>
          <div class="meta-item">
            <span class="meta-label">库存</span>
            <span class="meta-val" :class="{ 'low-stock': product.stock < 10 }">
              {{ product.stock > 0 ? product.stock + ' 件' : '售罄' }}
            </span>
          </div>
          <div class="meta-item" v-if="product.category">
            <span class="meta-label">分类</span>
            <span class="meta-val">{{ product.category?.name || product.categoryName }}</span>
          </div>
        </div>

        <div class="divider"></div>

        <div class="quantity-row">
          <span class="qty-label">数量</span>
          <el-input-number
            v-model="quantity"
            :min="1"
            :max="product.stock || 999"
            size="large"
            controls-position="right"
          />
          <span class="stock-hint">库存 {{ product.stock || 0 }} 件</span>
        </div>

        <div class="action-row">
          <el-button
            size="large"
            class="btn-cart"
            :icon="ShoppingCart"
            @click="handleAddToCart"
            :loading="cartLoading"
            :disabled="!product.stock"
          >
            加入购物车
          </el-button>
          <el-button
            size="large"
            type="danger"
            class="btn-buy"
            @click="handleBuyNow"
            :loading="buyLoading"
            :disabled="!product.stock"
          >
            立即购买
          </el-button>
          <el-button
            size="large"
            :type="isFavorited ? 'warning' : ''"
            :icon="isFavorited ? StarFilled : Star"
            :loading="favLoading"
            @click="handleToggleFavorite"
            class="btn-fav"
          >
            {{ isFavorited ? '已收藏' : '收藏' }}
          </el-button>
        </div>

        <!-- Group Buy Section -->
        <div class="group-buy-section" v-if="product.id">
          <div class="group-buy-header">
            <el-icon><UserFilled /></el-icon> 拼团优惠
          </div>
          <div v-if="groupBuys.length > 0" class="group-buy-list">
            <div v-for="gb in groupBuys" :key="gb.id" class="group-buy-item">
              <span class="group-price">¥{{ gb.groupPrice }}</span>
              <span class="group-progress">{{ gb.currentMembers }}/{{ gb.minMembers }}人</span>
              <el-button size="small" type="warning" @click="handleJoinGroupBuy(gb.shareCode)">加入拼团</el-button>
            </div>
          </div>
          <el-button
            size="large"
            type="success"
            class="btn-group"
            @click="groupBuyDialogVisible = true"
            :disabled="!product.stock"
          >
            发起拼团
          </el-button>
        </div>

        <div class="guarantee-bar">
          <span><el-icon><CircleCheck /></el-icon> 正品保证</span>
          <span><el-icon><Van /></el-icon> 快速发货</span>
          <span><el-icon><RefreshLeft /></el-icon> 7天无理由退换</span>
          <span><el-icon><Service /></el-icon> 24h客服</span>
        </div>
      </div>
    </div>

    <!-- Description -->
    <div class="description-section" v-if="!loading && product.id">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="商品详情" name="detail">
          <div class="desc-content">
            <p v-if="product.description">{{ product.description }}</p>
            <el-empty v-else description="暂无详情描述" />
          </div>
        </el-tab-pane>
        <el-tab-pane label="规格参数" name="specs">
          <div class="specs-content">
            <table class="specs-table" v-if="product.specifications && product.specifications.length">
              <tr v-for="spec in product.specifications" :key="spec.key">
                <td class="spec-key">{{ spec.key }}</td>
                <td class="spec-val">{{ spec.value }}</td>
              </tr>
            </table>
            <el-empty v-else description="暂无规格参数" />
          </div>
        </el-tab-pane>
        <el-tab-pane label="商品评价" name="reviews">
          <div class="reviews-content">
            <div v-if="reviewsLoading" v-loading="true" style="height:100px" />
            <el-empty v-else-if="reviews.length === 0" description="暂无评价" />
            <div v-else>
              <div v-for="r in reviews" :key="r.id" class="review-item">
                <div class="review-header">
                  <el-rate :model-value="r.rating" disabled size="small" />
                  <span class="review-time">{{ formatReviewTime(r.createTime) }}</span>
                </div>
                <p class="review-content">{{ r.content }}</p>
              </div>
              <el-pagination
                v-if="reviewTotal > reviewPageSize"
                v-model:current-page="reviewPageNum"
                :page-size="reviewPageSize"
                :total="reviewTotal"
                layout="prev, pager, next"
                @current-change="loadReviews"
              />
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>

    <el-empty v-if="!loading && !product.id" description="商品不存在" />

    <!-- Start Group Buy Dialog -->
    <el-dialog v-model="groupBuyDialogVisible" title="发起拼团" width="400px">
      <el-form :model="groupBuyForm" label-width="80px">
        <el-form-item label="拼团价格">
          <el-input-number v-model="groupBuyForm.groupPrice" :min="0.01" :precision="2" style="width:100%" />
        </el-form-item>
        <el-form-item label="成团人数">
          <el-input-number v-model="groupBuyForm.minMembers" :min="2" :max="10" style="width:100%" />
        </el-form-item>
        <el-form-item label="有效时间">
          <el-select v-model="groupBuyForm.expireHours" style="width:100%">
            <el-option label="12小时" :value="12" />
            <el-option label="24小时" :value="24" />
            <el-option label="48小时" :value="48" />
            <el-option label="72小时" :value="72" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="groupBuyDialogVisible = false">取消</el-button>
        <el-button type="success" @click="submitGroupBuy" :loading="groupBuyLoading">发起拼团</el-button>
      </template>
    </el-dialog>

    <!-- Share Code Dialog -->
    <el-dialog v-model="shareCodeVisible" title="拼团发起成功！" width="400px">
      <div class="share-code-box">
        <p>您的拼团已成功发起，分享码：</p>
        <div class="share-code">{{ currentShareCode }}</div>
        <p class="share-hint">将此分享码发送给好友，好友可在商品页面点击"加入拼团"并输入分享码参与！</p>
        <el-button type="primary" @click="copyShareCode">复制分享码</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ShoppingCart, Star, StarFilled } from '@element-plus/icons-vue'
import { getProductDetail } from '@/api/product'
import { addToCart } from '@/api/cart'
import { createOrder } from '@/api/order'
import { useUserStore } from '@/stores/user'
import { useCartStore } from '@/stores/cart'
import { getCart } from '@/api/cart'
import { addFavorite, removeFavorite, getFavoriteStatus, recordHistory, getProductReviews } from '@/api/user'
import { createGroupBuy, joinGroupBuy, getProductGroupBuys } from '@/api/groupbuy'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()

const product = ref({})
const loading = ref(false)
const cartLoading = ref(false)
const buyLoading = ref(false)
const quantity = ref(1)
const activeTab = ref('detail')
const activeImage = ref('')
const isFavorited = ref(false)
const favLoading = ref(false)
const reviews = ref([])
const reviewsLoading = ref(false)
const reviewTotal = ref(0)
const reviewPageNum = ref(1)
const reviewPageSize = ref(5)

const groupBuys = ref([])
const groupBuyDialogVisible = ref(false)
const shareCodeVisible = ref(false)
const groupBuyLoading = ref(false)
const currentShareCode = ref('')
const groupBuyForm = ref({
  groupPrice: 0,
  minMembers: 2,
  expireHours: 24
})

const images = computed(() => {
  const imgs = []
  if (product.value.mainImage) imgs.push(product.value.mainImage)
  if (product.value.images && Array.isArray(product.value.images)) {
    imgs.push(...product.value.images.filter(i => i !== product.value.mainImage))
  }
  return imgs
})

const formatPrice = (price) => {
  if (price == null) return '0.00'
  return Number(price).toFixed(2)
}

const onImgError = (e) => {
  e.target.src = 'https://via.placeholder.com/480x480?text=暂无图片'
}

const onThumbError = (e) => {
  e.target.src = 'https://via.placeholder.com/80x80?text=图'
}

const handleAddToCart = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  cartLoading.value = true
  try {
    await addToCart({ productId: product.value.id, quantity: quantity.value })
    ElMessage.success('已加入购物车')
    const res = await getCart()
    cartStore.setCart(res.data || [])
  } catch {
    // error shown by interceptor
  } finally {
    cartLoading.value = false
  }
}

const handleBuyNow = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  buyLoading.value = true
  try {
    await addToCart({ productId: product.value.id, quantity: quantity.value })
    const res = await getCart()
    cartStore.setCart(res.data || [])
    router.push('/cart')
  } catch {
    // ignore
  } finally {
    buyLoading.value = false
  }
}

const handleToggleFavorite = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  favLoading.value = true
  try {
    if (isFavorited.value) {
      await removeFavorite(product.value.id)
      isFavorited.value = false
      ElMessage.success('已取消收藏')
    } else {
      await addFavorite(product.value.id)
      isFavorited.value = true
      ElMessage.success('已加入收藏')
    }
  } catch {
    // handled
  } finally {
    favLoading.value = false
  }
}

const loadReviews = async () => {
  reviewsLoading.value = true
  try {
    const res = await getProductReviews(product.value.id, { pageNum: reviewPageNum.value, pageSize: reviewPageSize.value })
    reviews.value = res.data?.list || []
    reviewTotal.value = res.data?.total || 0
  } catch {
    // ignore
  } finally {
    reviewsLoading.value = false
  }
}

const formatReviewTime = (t) => t ? new Date(t).toLocaleDateString('zh-CN') : ''

const loadGroupBuys = async () => {
  try {
    const res = await getProductGroupBuys(product.value.id)
    groupBuys.value = res.data || []
  } catch {
    // ignore
  }
}

const handleJoinGroupBuy = async (shareCode) => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  try {
    await joinGroupBuy(shareCode)
    ElMessage.success('成功加入拼团！')
    loadGroupBuys()
  } catch {
    // handled by interceptor
  }
}

const submitGroupBuy = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  groupBuyLoading.value = true
  try {
    const res = await createGroupBuy({
      productId: product.value.id,
      groupPrice: groupBuyForm.value.groupPrice,
      minMembers: groupBuyForm.value.minMembers,
      expireHours: groupBuyForm.value.expireHours
    })
    currentShareCode.value = res.data?.shareCode || res.data?.groupBuy?.shareCode
    groupBuyDialogVisible.value = false
    shareCodeVisible.value = true
    loadGroupBuys()
  } catch {
    // handled
  } finally {
    groupBuyLoading.value = false
  }
}

const copyShareCode = () => {
  navigator.clipboard.writeText(currentShareCode.value).then(() => {
    ElMessage.success('分享码已复制')
  }).catch(() => {
    ElMessage.info(`分享码：${currentShareCode.value}`)
  })
}

onMounted(async () => {
  loading.value = true
  try {
    const res = await getProductDetail(route.params.id)
    product.value = res.data || {}
    if (images.value.length > 0) {
      activeImage.value = images.value[0]
    }
    // Record browse history (silently, only if logged in)
    if (userStore.isLoggedIn && product.value.id) {
      recordHistory(product.value.id).catch(() => {})
      getFavoriteStatus(product.value.id).then((r) => {
        isFavorited.value = !!r.data
      }).catch(() => {})
    }
    // Load reviews
    if (product.value.id) {
      loadReviews()
      loadGroupBuys()
      groupBuyForm.value.groupPrice = product.value.price || 0
    }
  } catch {
    ElMessage.error('获取商品详情失败')
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.product-detail-page {
  padding-top: 24px;
  padding-bottom: 40px;
}
.breadcrumb {
  margin-bottom: 20px;
}
.detail-main {
  display: flex;
  gap: 40px;
  background: #fff;
  border-radius: 10px;
  padding: 30px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
  margin-bottom: 24px;
}
.image-section {
  flex-shrink: 0;
  width: 440px;
}
.main-image-wrap {
  width: 440px;
  height: 440px;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #eee;
  background: #f8f8f8;
}
.main-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}
.main-image:hover {
  transform: scale(1.05);
}
.thumbnail-list {
  display: flex;
  gap: 8px;
  margin-top: 12px;
  flex-wrap: wrap;
}
.thumbnail {
  width: 70px;
  height: 70px;
  border-radius: 4px;
  overflow: hidden;
  border: 2px solid transparent;
  cursor: pointer;
  transition: border-color 0.2s;
}
.thumbnail.active {
  border-color: #c0392b;
}
.thumbnail img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.info-section {
  flex: 1;
  min-width: 0;
}
.product-name {
  font-size: 22px;
  font-weight: 700;
  color: #333;
  line-height: 1.4;
  margin-bottom: 10px;
}
.product-tags {
  margin-bottom: 12px;
  display: flex;
  gap: 6px;
}
.price-box {
  background: #fff8f8;
  border-radius: 6px;
  padding: 16px 20px;
  margin-bottom: 16px;
  display: flex;
  align-items: baseline;
  gap: 12px;
}
.price-label {
  font-size: 13px;
  color: #999;
  width: 40px;
  flex-shrink: 0;
}
.price-value {
  color: #c0392b;
}
.price-symbol {
  font-size: 18px;
  font-weight: 600;
}
.price-num {
  font-size: 36px;
  font-weight: 800;
}
.original-price {
  font-size: 14px;
  color: #999;
  text-decoration: line-through;
}
.meta-row {
  display: flex;
  gap: 24px;
  margin-bottom: 16px;
}
.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
}
.meta-label {
  font-size: 13px;
  color: #999;
}
.meta-val {
  font-size: 13px;
  color: #555;
  font-weight: 500;
}
.low-stock {
  color: #e67e22;
}
.divider {
  height: 1px;
  background: #f0f0f0;
  margin: 16px 0;
}
.quantity-row {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
}
.qty-label {
  font-size: 13px;
  color: #999;
  width: 40px;
  flex-shrink: 0;
}
.stock-hint {
  font-size: 12px;
  color: #999;
}
.action-row {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
}
.btn-cart {
  flex: 1;
  background: #f39c12;
  border-color: #f39c12;
  color: #fff;
  font-size: 16px;
  height: 50px;
}
.btn-cart:hover {
  background: #e67e22;
  border-color: #e67e22;
}
.btn-buy {
  flex: 1;
  font-size: 16px;
  height: 50px;
  background: #c0392b;
  border-color: #c0392b;
}
.btn-fav {
  height: 50px;
  min-width: 100px;
}
.guarantee-bar {
  display: flex;
  gap: 20px;
  color: #666;
  font-size: 12px;
  background: #f9f9f9;
  padding: 12px 16px;
  border-radius: 6px;
}
.guarantee-bar span {
  display: flex;
  align-items: center;
  gap: 4px;
}
.description-section {
  background: #fff;
  border-radius: 10px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}
.desc-content {
  padding: 16px 0;
  line-height: 1.8;
  color: #555;
  font-size: 14px;
  white-space: pre-wrap;
}
.specs-table {
  width: 100%;
  border-collapse: collapse;
}
.specs-table tr:nth-child(even) {
  background: #f9f9f9;
}
.spec-key {
  width: 180px;
  padding: 10px 16px;
  font-size: 13px;
  color: #666;
  border: 1px solid #eee;
  background: #f5f5f5;
}
.spec-val {
  padding: 10px 16px;
  font-size: 13px;
  color: #333;
  border: 1px solid #eee;
}
.reviews-content {
  padding: 8px 0;
}
.review-item {
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
}
.review-item:last-child { border-bottom: none; }
.review-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 8px;
}
.review-time {
  font-size: 12px;
  color: #999;
}
.review-content {
  margin: 0;
  font-size: 14px;
  color: #555;
  line-height: 1.6;
}
.group-buy-section {
  margin-top: 20px;
  background: #f0fff4;
  border: 1px solid #b2dfdb;
  border-radius: 8px;
  padding: 16px;
}
.group-buy-header {
  font-size: 15px;
  font-weight: 600;
  color: #27ae60;
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 12px;
}
.group-buy-list {
  margin-bottom: 12px;
}
.group-buy-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 8px 0;
  border-bottom: 1px solid #c8e6c9;
}
.group-buy-item:last-child {
  border-bottom: none;
}
.group-price {
  color: #c0392b;
  font-weight: 700;
  font-size: 16px;
  min-width: 70px;
}
.group-progress {
  color: #555;
  font-size: 13px;
}
.btn-group {
  width: 100%;
  height: 46px;
  font-size: 15px;
}
.share-code-box {
  text-align: center;
  padding: 16px 0;
}
.share-code {
  font-size: 32px;
  font-weight: 800;
  color: #c0392b;
  letter-spacing: 6px;
  margin: 16px 0;
  font-family: monospace;
}
.share-hint {
  font-size: 13px;
  color: #666;
  margin-bottom: 16px;
}
</style>
