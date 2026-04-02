<template>
  <div class="layout">
    <AppHeader />
    <main class="main-content">
      <router-view />
    </main>
    <footer class="footer">
      <div class="page-container">
        <p>© 2024 潮流商城 · 品质生活，从这里开始</p>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import AppHeader from '@/components/AppHeader.vue'
import { useCartStore } from '@/stores/cart'
import { useUserStore } from '@/stores/user'
import { getCart } from '@/api/cart'

const cartStore = useCartStore()
const userStore = useUserStore()

onMounted(async () => {
  if (userStore.isLoggedIn) {
    try {
      const res = await getCart()
      cartStore.setCart(res.data || [])
    } catch {
      // ignore
    }
  }
})
</script>

<style scoped>
.layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}
.main-content {
  flex: 1;
  padding-top: 96px;
  background: #f5f5f5;
}
.footer {
  background: #333;
  color: #aaa;
  text-align: center;
  padding: 20px 0;
  font-size: 13px;
  margin-top: 40px;
}
</style>
