<template>
  <header class="app-header">
    <div class="header-inner page-container">
      <!-- Logo -->
      <router-link to="/" class="logo">
        <el-icon :size="28"><ShoppingBag /></el-icon>
        <span class="logo-text">潮流商城</span>
      </router-link>

      <!-- Search -->
      <div class="search-bar">
        <el-input
          v-model="keyword"
          placeholder="搜索商品、品牌"
          class="search-input"
          @keyup.enter="handleSearch"
          clearable
        >
          <template #append>
            <el-button @click="handleSearch" :icon="Search" class="search-btn" />
          </template>
        </el-input>
      </div>

      <!-- Right actions -->
      <div class="header-actions">
        <!-- AI Chat -->
        <router-link to="/ai-chat" class="action-item" title="AI助手">
          <el-icon :size="22"><ChatDotRound /></el-icon>
          <span class="action-label">AI助手</span>
        </router-link>

        <!-- Cart -->
        <router-link to="/cart" class="action-item" title="购物车">
          <el-badge :value="cartStore.itemCount || 0" :max="99" :hidden="!cartStore.itemCount">
            <el-icon :size="22"><ShoppingCart /></el-icon>
          </el-badge>
          <span class="action-label">购物车</span>
        </router-link>

        <!-- User -->
        <div class="action-item" v-if="userStore.isLoggedIn">
          <el-dropdown @command="handleCommand" trigger="click">
            <span class="user-trigger">
              <el-icon :size="22"><UserFilled /></el-icon>
              <span class="action-label">{{ userStore.userInfo?.username || '我的' }}</span>
              <el-icon class="arrow"><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon> 个人中心
                </el-dropdown-item>
                <el-dropdown-item command="orders">
                  <el-icon><List /></el-icon> 我的订单
                </el-dropdown-item>
                <el-dropdown-item command="favorites">
                  <el-icon><Star /></el-icon> 我的收藏
                </el-dropdown-item>
                <el-dropdown-item command="history">
                  <el-icon><Clock /></el-icon> 浏览历史
                </el-dropdown-item>
                <el-dropdown-item command="addresses">
                  <el-icon><Location /></el-icon> 收货地址
                </el-dropdown-item>
                <el-dropdown-item command="ai-chat">
                  <el-icon><ChatDotRound /></el-icon> AI助手
                </el-dropdown-item>
                <template v-if="userStore.userInfo?.role === 'admin'">
                  <el-dropdown-item divided command="admin-products">
                    <el-icon><Setting /></el-icon> 商品管理
                  </el-dropdown-item>
                  <el-dropdown-item command="admin-orders">
                    <el-icon><Document /></el-icon> 订单管理
                  </el-dropdown-item>
                  <el-dropdown-item command="admin-categories">
                    <el-icon><Grid /></el-icon> 分类管理
                  </el-dropdown-item>
                  <el-dropdown-item command="admin-banners">
                    <el-icon><PictureFilled /></el-icon> 轮播图管理
                  </el-dropdown-item>
                  <el-dropdown-item command="admin-ads">
                    <el-icon><Tickets /></el-icon> 广告位管理
                  </el-dropdown-item>
                  <el-dropdown-item command="admin-reviews">
                    <el-icon><ChatSquare /></el-icon> 评价管理
                  </el-dropdown-item>
                </template>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon> 退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
        <div class="action-item auth-links" v-else>
          <router-link to="/login">登录</router-link>
          <span class="divider">|</span>
          <router-link to="/register">注册</router-link>
        </div>
      </div>
    </div>

    <!-- Sub navigation -->
    <div class="sub-nav">
      <div class="page-container sub-nav-inner">
        <router-link to="/" class="sub-nav-item">首页</router-link>
        <router-link
          v-for="cat in categories"
          :key="cat.id"
          :to="`/products?categoryId=${cat.id}`"
          class="sub-nav-item"
        >{{ cat.name }}</router-link>
        <router-link to="/products" class="sub-nav-item">全部商品</router-link>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Setting, Document, Grid, Star, Clock, Location, PictureFilled, Tickets, ChatSquare } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { useCartStore } from '@/stores/cart'
import { getCategories } from '@/api/product'

const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()

const keyword = ref('')
const categories = ref([])

const handleSearch = () => {
  if (keyword.value.trim()) {
    router.push({ path: '/products', query: { keyword: keyword.value.trim() } })
  }
}

const handleCommand = (command) => {
  if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      userStore.logout()
      cartStore.clearCart()
      ElMessage.success('已退出登录')
      router.push('/')
    }).catch(() => {})
  } else if (command === 'profile') {
    router.push('/profile')
  } else if (command === 'orders') {
    router.push('/orders')
  } else if (command === 'favorites') {
    router.push('/favorites')
  } else if (command === 'history') {
    router.push('/history')
  } else if (command === 'addresses') {
    router.push('/addresses')
  } else if (command === 'ai-chat') {
    router.push('/ai-chat')
  } else if (command === 'admin-products') {
    router.push('/admin/products')
  } else if (command === 'admin-orders') {
    router.push('/admin/orders')
  } else if (command === 'admin-categories') {
    router.push('/admin/categories')
  } else if (command === 'admin-banners') {
    router.push('/admin/banners')
  } else if (command === 'admin-ads') {
    router.push('/admin/ads')
  } else if (command === 'admin-reviews') {
    router.push('/admin/reviews')
  }
}

onMounted(async () => {
  try {
    const res = await getCategories()
    categories.value = (res.data || []).slice(0, 8)
  } catch {
    // ignore
  }
})
</script>

<style scoped>
.app-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  background: #c0392b;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}
.header-inner {
  display: flex;
  align-items: center;
  height: 60px;
  gap: 20px;
}
.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #fff;
  font-size: 20px;
  font-weight: 700;
  white-space: nowrap;
  text-decoration: none;
}
.logo-text {
  letter-spacing: 1px;
}
.search-bar {
  flex: 1;
  max-width: 600px;
}
.search-input :deep(.el-input__wrapper) {
  border-radius: 4px 0 0 4px;
  height: 38px;
}
.search-input :deep(.el-input-group__append) {
  background: #f39c12;
  border-color: #f39c12;
  color: #fff;
  border-radius: 0 4px 4px 0;
  padding: 0 16px;
}
.search-input :deep(.el-input-group__append:hover) {
  background: #e67e22;
}
.search-input :deep(.el-input-group__append .el-icon) {
  color: #fff;
  font-size: 18px;
}
.header-actions {
  display: flex;
  align-items: center;
  gap: 20px;
  color: #fff;
}
.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: #fff;
  cursor: pointer;
  text-decoration: none;
  font-size: 12px;
  gap: 2px;
}
.action-item:hover {
  color: #f39c12;
}
.action-label {
  font-size: 11px;
  white-space: nowrap;
}
.user-trigger {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: #fff;
  cursor: pointer;
  gap: 2px;
  outline: none;
}
.user-trigger:hover {
  color: #f39c12;
}
.arrow {
  font-size: 10px !important;
}
.auth-links {
  font-size: 13px;
  flex-direction: row;
  gap: 4px;
}
.auth-links a {
  color: #fff;
}
.auth-links a:hover {
  color: #f39c12;
  text-decoration: underline;
}
.divider {
  opacity: 0.5;
}
.sub-nav {
  background: #a93226;
}
.sub-nav-inner {
  display: flex;
  align-items: center;
  gap: 0;
  height: 36px;
}
.sub-nav-item {
  color: rgba(255, 255, 255, 0.85);
  font-size: 13px;
  padding: 0 14px;
  height: 36px;
  line-height: 36px;
  text-decoration: none;
  display: block;
  transition: background 0.2s;
}
.sub-nav-item:hover,
.sub-nav-item.router-link-active {
  background: rgba(255, 255, 255, 0.15);
  color: #fff;
}
</style>
