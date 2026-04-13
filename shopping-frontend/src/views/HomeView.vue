<template>
  <div class="home-page">
    <!-- Banner Carousel -->
    <section class="banner-section">
      <el-carousel :interval="4000" height="400px" arrow="always">
        <el-carousel-item v-for="banner in banners" :key="banner.id">
          <!-- API banner with image -->
          <div v-if="banner.image" class="banner-slide-img" @click="banner.link && router.push(banner.link)" :style="{ cursor: banner.link ? 'pointer' : 'default' }">
            <img :src="banner.image" :alt="banner.title || ''" class="banner-bg-img" />
            <div class="banner-img-overlay" v-if="banner.title">
              <h2 class="banner-title">{{ banner.title }}</h2>
            </div>
          </div>
          <!-- Static gradient banner -->
          <div v-else class="banner-slide" :style="{ background: banner.gradient }">
            <div class="banner-content">
              <h2 class="banner-title">{{ banner.title }}</h2>
              <p class="banner-sub">{{ banner.sub }}</p>
              <el-button type="warning" size="large" round @click="router.push(banner.link)">
                {{ banner.btn }}
              </el-button>
            </div>
            <div class="banner-deco">{{ banner.emoji }}</div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </section>

    <!-- Home Ads (if any) -->
    <div v-if="homeAds.length > 0" class="home-ads page-container">
      <div
        v-for="ad in homeAds"
        :key="ad.id"
        class="ad-item"
        @click="ad.link && router.push(ad.link)"
      >
        <img :src="ad.image" :alt="ad.title || ''" class="ad-img" />
      </div>
    </div>

    <div class="page-container">
      <!-- Categories -->
      <section class="categories-section">
        <div class="section-header">
          <span class="section-title">商品分类</span>
        </div>
        <div class="category-grid" v-loading="catsLoading">
          <div
            class="category-item"
            v-for="cat in categories"
            :key="cat.id"
            @click="router.push(`/products?categoryId=${cat.id}`)"
          >
            <div class="cat-icon">
              <el-icon :size="28"><component :is="getCatIcon(cat.name)" /></el-icon>
            </div>
            <span class="cat-name">{{ cat.name }}</span>
          </div>
          <div class="category-item" @click="router.push('/products')">
            <div class="cat-icon more-icon">
              <el-icon :size="28"><MoreFilled /></el-icon>
            </div>
            <span class="cat-name">全部分类</span>
          </div>
        </div>
      </section>

      <!-- Flash Sale / Promo Bar -->
      <section class="promo-bar">
        <div class="promo-item" style="background: linear-gradient(135deg,#c0392b,#e74c3c)">
          <el-icon :size="24"><Lightning /></el-icon>
          <div>
            <div class="promo-name">限时秒杀</div>
            <div class="promo-desc">每日9点开始</div>
          </div>
        </div>
        <div class="promo-item" style="background: linear-gradient(135deg,#f39c12,#e67e22)">
          <el-icon :size="24"><Present /></el-icon>
          <div>
            <div class="promo-name">品牌特卖</div>
            <div class="promo-desc">低至3折起</div>
          </div>
        </div>
        <div class="promo-item" style="background: linear-gradient(135deg,#27ae60,#2ecc71)">
          <el-icon :size="24"><Van /></el-icon>
          <div>
            <div class="promo-name">免费配送</div>
            <div class="promo-desc">满99元包邮</div>
          </div>
        </div>
        <div class="promo-item" style="background: linear-gradient(135deg,#8e44ad,#9b59b6)">
          <el-icon :size="24"><Medal /></el-icon>
          <div>
            <div class="promo-name">品质保障</div>
            <div class="promo-desc">正品保证7天退换</div>
          </div>
        </div>
      </section>

      <!-- Hot Products -->
      <section class="products-section">
        <div class="section-header">
          <span class="section-title">🔥 热销商品</span>
          <router-link to="/products" class="view-more">查看更多 →</router-link>
        </div>
        <div class="product-grid" v-loading="hotLoading">
          <ProductCard
            v-for="product in hotProducts"
            :key="product.id"
            :product="product"
          />
          <div v-if="!hotLoading && hotProducts.length === 0" class="empty-hint">
            暂无热销商品
          </div>
        </div>
      </section>

      <!-- New Products -->
      <section class="products-section">
        <div class="section-header">
          <span class="section-title">✨ 新品推荐</span>
          <router-link to="/products" class="view-more">查看更多 →</router-link>
        </div>
        <div class="product-grid" v-loading="newLoading">
          <ProductCard
            v-for="product in newProducts"
            :key="product.id"
            :product="product"
          />
          <div v-if="!newLoading && newProducts.length === 0" class="empty-hint">
            暂无新品
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import ProductCard from '@/components/ProductCard.vue'
import { getHotProducts, getNewProducts, getCategories } from '@/api/product'
import { getBanners, getAds } from '@/api/banner'

const router = useRouter()

const hotProducts = ref([])
const newProducts = ref([])
const categories = ref([])
const hotLoading = ref(false)
const newLoading = ref(false)
const catsLoading = ref(false)

const STATIC_BANNERS = [
  {
    id: 1,
    gradient: 'linear-gradient(135deg, #c0392b 0%, #e74c3c 50%, #c0392b 100%)',
    title: '年终盛典 · 全场钜惠',
    sub: '万款商品低至3折，限时抢购！',
    btn: '立即抢购',
    link: '/products',
    emoji: '🛍️'
  },
  {
    id: 2,
    gradient: 'linear-gradient(135deg, #1a252f 0%, #2c3e50 50%, #34495e 100%)',
    title: '新品首发 · 科技潮流',
    sub: '最新电子产品，引领时代潮流！',
    btn: '查看新品',
    link: '/products',
    emoji: '📱'
  },
  {
    id: 3,
    gradient: 'linear-gradient(135deg, #1e3c72 0%, #2a5298 50%, #1e3c72 100%)',
    title: '品牌特卖 · 正品保障',
    sub: '顶级品牌直供，品质无忧购物！',
    btn: '品牌专区',
    link: '/products',
    emoji: '👑'
  }
]

const banners = ref(STATIC_BANNERS)
const homeAds = ref([])

const catIconMap = {
  '手机': 'Iphone',
  '电脑': 'Monitor',
  '服装': 'Goods',
  '食品': 'Food',
  '家电': 'Cpu',
  '运动': 'Trophy',
  '图书': 'Reading',
  '美妆': 'MagicStick',
  '母婴': 'Avatar',
  '家居': 'House',
  '数码': 'Camera',
  '汽车': 'Bicycle'
}

const getCatIcon = (name) => {
  for (const key of Object.keys(catIconMap)) {
    if (name && name.includes(key)) return catIconMap[key]
  }
  return 'Goods'
}

onMounted(async () => {
  catsLoading.value = true
  hotLoading.value = true
  newLoading.value = true

  // Load banners from API (fallback to static if empty/error)
  try {
    const res = await getBanners()
    const apiBanners = res.data || []
    if (apiBanners.length > 0) {
      banners.value = apiBanners.map((b) => ({
        id: b.id,
        image: b.image,
        title: b.title,
        link: b.link || '/products',
        gradient: 'linear-gradient(135deg,#c0392b,#e74c3c)'
      }))
    }
  } catch {
    // keep static banners
  }

  // Load home ads
  try {
    const res = await getAds('home_top')
    homeAds.value = res.data || []
  } catch {
    // ignore
  }

  try {
    const res = await getCategories()
    categories.value = (res.data || []).slice(0, 11)
  } catch {
    // ignore
  } finally {
    catsLoading.value = false
  }

  try {
    const res = await getHotProducts()
    hotProducts.value = (res.data || []).slice(0, 8)
  } catch {
    // ignore
  } finally {
    hotLoading.value = false
  }

  try {
    const res = await getNewProducts()
    newProducts.value = (res.data || []).slice(0, 8)
  } catch {
    // ignore
  } finally {
    newLoading.value = false
  }
})
</script>

<style scoped>
.home-page {
  min-height: 100vh;
}
.banner-section {
  margin-bottom: 0;
}
.banner-slide {
  height: 400px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 15%;
  position: relative;
  overflow: hidden;
}
.banner-slide-img {
  height: 400px;
  width: 100%;
  position: relative;
  overflow: hidden;
}
.banner-bg-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.banner-img-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(transparent, rgba(0,0,0,0.5));
  padding: 20px 40px;
}
.banner-img-overlay .banner-title {
  color: #fff;
  font-size: 28px;
  font-weight: 700;
  margin: 0;
}
.banner-content {
  z-index: 1;
  color: #fff;
}
.banner-title {
  font-size: 38px;
  font-weight: 800;
  margin-bottom: 12px;
  text-shadow: 2px 2px 8px rgba(0,0,0,0.3);
}
.banner-sub {
  font-size: 18px;
  margin-bottom: 28px;
  opacity: 0.9;
}
.banner-deco {
  font-size: 140px;
  opacity: 0.25;
  user-select: none;
  line-height: 1;
}
.home-ads {
  display: flex;
  gap: 12px;
  padding: 16px 0 0;
}
.ad-item {
  flex: 1;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
}
.ad-img {
  width: 100%;
  height: 100px;
  object-fit: cover;
  display: block;
  transition: transform 0.3s;
}
.ad-item:hover .ad-img {
  transform: scale(1.03);
}
.categories-section {
  background: #fff;
  border-radius: 10px;
  padding: 24px;
  margin: 24px 0;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.view-more {
  color: #c0392b;
  font-size: 13px;
  text-decoration: none;
}
.view-more:hover {
  text-decoration: underline;
}
.category-grid {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 12px;
  min-height: 80px;
}
.category-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 14px 8px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}
.category-item:hover {
  background: #fff5f5;
  transform: translateY(-2px);
}
.cat-icon {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: linear-gradient(135deg, #fff0f0, #ffd6d6);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #c0392b;
}
.more-icon {
  background: linear-gradient(135deg, #f0f0ff, #d6d6ff);
  color: #6c5ce7;
}
.cat-name {
  font-size: 12px;
  color: #555;
  text-align: center;
}
.promo-bar {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
  margin-bottom: 24px;
}
.promo-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 20px;
  border-radius: 10px;
  color: #fff;
  cursor: pointer;
  transition: transform 0.2s;
}
.promo-item:hover {
  transform: translateY(-2px);
}
.promo-name {
  font-size: 15px;
  font-weight: 700;
}
.promo-desc {
  font-size: 12px;
  opacity: 0.85;
  margin-top: 2px;
}
.products-section {
  background: #fff;
  border-radius: 10px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}
.product-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  min-height: 100px;
}
.empty-hint {
  grid-column: 1 / -1;
  text-align: center;
  color: #999;
  padding: 40px;
  font-size: 14px;
}
</style>
