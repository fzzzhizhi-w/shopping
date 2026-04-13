import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/',
    component: () => import('@/views/LayoutView.vue'),
    children: [
      { path: '', name: 'Home', component: () => import('@/views/HomeView.vue') },
      { path: 'products', name: 'ProductList', component: () => import('@/views/ProductListView.vue') },
      { path: 'products/:id', name: 'ProductDetail', component: () => import('@/views/ProductDetailView.vue') },
      { path: 'cart', name: 'Cart', component: () => import('@/views/CartView.vue'), meta: { requiresAuth: true } },
      { path: 'orders', name: 'Orders', component: () => import('@/views/OrdersView.vue'), meta: { requiresAuth: true } },
      { path: 'orders/:id', name: 'OrderDetail', component: () => import('@/views/OrderDetailView.vue'), meta: { requiresAuth: true } },
      { path: 'ai-chat', name: 'AiChat', component: () => import('@/views/AiChatView.vue'), meta: { requiresAuth: true } },
      { path: 'profile', name: 'Profile', component: () => import('@/views/ProfileView.vue'), meta: { requiresAuth: true } },
      { path: 'admin/products', name: 'AdminProducts', component: () => import('@/views/AdminProductView.vue'), meta: { requiresAdmin: true } },
      { path: 'admin/orders', name: 'AdminOrders', component: () => import('@/views/AdminOrderView.vue'), meta: { requiresAdmin: true } },
      { path: 'admin/categories', name: 'AdminCategories', component: () => import('@/views/AdminCategoryView.vue'), meta: { requiresAdmin: true } },
      { path: 'admin/banners', name: 'AdminBanners', component: () => import('@/views/AdminBannerView.vue'), meta: { requiresAdmin: true } },
      { path: 'admin/ads', name: 'AdminAds', component: () => import('@/views/AdminAdView.vue'), meta: { requiresAdmin: true } },
      { path: 'admin/reviews', name: 'AdminReviews', component: () => import('@/views/AdminReviewView.vue'), meta: { requiresAdmin: true } },
      { path: 'favorites', name: 'Favorites', component: () => import('@/views/FavoritesView.vue'), meta: { requiresAuth: true } },
      { path: 'history', name: 'History', component: () => import('@/views/HistoryView.vue'), meta: { requiresAuth: true } },
      { path: 'addresses', name: 'Addresses', component: () => import('@/views/AddressView.vue'), meta: { requiresAuth: true } },
      { path: 'group-buys', name: 'GroupBuys', component: () => import('@/views/GroupBuyView.vue'), meta: { requiresAuth: true } },
      { path: 'merchant/products', name: 'MerchantProducts', component: () => import('@/views/MerchantProductView.vue'), meta: { requiresMerchant: true } },
      { path: 'merchant/orders', name: 'MerchantOrders', component: () => import('@/views/MerchantOrderView.vue'), meta: { requiresMerchant: true } }
    ]
  },
  { path: '/login', name: 'Login', component: () => import('@/views/LoginView.vue') },
  { path: '/register', name: 'Register', component: () => import('@/views/RegisterView.vue') }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  }
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  if (to.meta.requiresAdmin) {
    if (!userStore.isLoggedIn) {
      next({ path: '/login', query: { redirect: to.fullPath } })
    } else if (userStore.userInfo?.role !== 'admin') {
      next({ path: '/' })
    } else {
      next()
    }
  } else if (to.meta.requiresMerchant) {
    if (!userStore.isLoggedIn) {
      next({ path: '/login', query: { redirect: to.fullPath } })
    } else if (userStore.userInfo?.role !== 'merchant' && userStore.userInfo?.role !== 'admin') {
      next({ path: '/' })
    } else {
      next()
    }
  } else if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    next({ path: '/login', query: { redirect: to.fullPath } })
  } else {
    next()
  }
})

export default router
