<template>
  <div class="login-page">
    <div class="login-card">
      <div class="login-logo">
        <el-icon :size="40" color="#c0392b"><ShoppingBag /></el-icon>
        <h1 class="logo-text">潮流商城</h1>
        <p class="logo-sub">品质生活，从这里开始</p>
      </div>

      <el-form
        :model="form"
        :rules="rules"
        ref="formRef"
        class="login-form"
        @keyup.enter="handleLogin"
      >
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            placeholder="请输入用户名"
            size="large"
            :prefix-icon="User"
            clearable
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            size="large"
            :prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="danger"
            size="large"
            class="login-btn"
            @click="handleLogin"
            :loading="loading"
          >
            登 录
          </el-button>
        </el-form-item>
      </el-form>

      <div class="login-footer">
        <span class="hint">还没有账号？</span>
        <router-link to="/register" class="link">立即注册</router-link>
      </div>
    </div>

    <div class="login-bg-deco">
      <div class="deco-circle c1"></div>
      <div class="deco-circle c2"></div>
      <div class="deco-circle c3"></div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { login } from '@/api/auth'
import { getUserInfo } from '@/api/auth'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)

const form = ref({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }, { min: 6, message: '密码长度至少6位', trigger: 'blur' }]
}

const handleLogin = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    const res = await login(form.value)
    const token = res.data?.token || res.token
    userStore.setToken(token)
    try {
      const infoRes = await getUserInfo()
      userStore.setUserInfo(infoRes.data)
    } catch {
      // ignore info error
    }
    ElMessage.success('登录成功！欢迎回来')
    const redirect = route.query.redirect || '/'
    router.push(redirect)
  } catch {
    // error shown by interceptor
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #c0392b 0%, #e74c3c 40%, #c0392b 100%);
  position: relative;
  overflow: hidden;
}
.login-card {
  background: #fff;
  border-radius: 16px;
  padding: 48px 44px;
  width: 420px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.25);
  position: relative;
  z-index: 1;
}
.login-logo {
  text-align: center;
  margin-bottom: 36px;
}
.logo-text {
  font-size: 28px;
  font-weight: 800;
  color: #c0392b;
  margin: 10px 0 6px;
}
.logo-sub {
  font-size: 13px;
  color: #999;
}
.login-form {
  margin-bottom: 8px;
}
.login-btn {
  width: 100%;
  height: 46px;
  font-size: 16px;
  font-weight: 600;
  background: #c0392b;
  border-color: #c0392b;
  letter-spacing: 6px;
}
.login-footer {
  text-align: center;
  font-size: 14px;
  color: #666;
  margin-top: 16px;
}
.hint {
  margin-right: 4px;
}
.link {
  color: #c0392b;
  font-weight: 600;
}
.link:hover {
  text-decoration: underline;
}
.login-bg-deco {
  position: absolute;
  inset: 0;
  pointer-events: none;
}
.deco-circle {
  position: absolute;
  border-radius: 50%;
  opacity: 0.15;
  background: #fff;
}
.c1 { width: 400px; height: 400px; top: -100px; right: -80px; }
.c2 { width: 250px; height: 250px; bottom: -60px; left: -40px; }
.c3 { width: 150px; height: 150px; top: 60%; left: 15%; }
</style>
