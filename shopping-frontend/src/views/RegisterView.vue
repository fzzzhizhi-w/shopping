<template>
  <div class="register-page">
    <div class="register-card">
      <div class="register-logo">
        <el-icon :size="36" color="#c0392b"><ShoppingBag /></el-icon>
        <h1 class="logo-text">潮流商城</h1>
        <p class="logo-sub">注册账号，开启品质购物</p>
      </div>

      <el-form
        :model="form"
        :rules="rules"
        ref="formRef"
        class="register-form"
        label-position="top"
      >
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="form.username"
            placeholder="请输入用户名（4-20位字母数字）"
            size="large"
            :prefix-icon="User"
            clearable
          />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码（至少6位）"
            size="large"
            :prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="form.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            size="large"
            :prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input
            v-model="form.email"
            placeholder="请输入邮箱（选填）"
            size="large"
            :prefix-icon="Message"
            clearable
          />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input
            v-model="form.phone"
            placeholder="请输入手机号（选填）"
            size="large"
            :prefix-icon="Phone"
            clearable
            maxlength="11"
          />
        </el-form-item>
        <el-form-item label="账号类型">
          <el-radio-group v-model="form.role" size="large">
            <el-radio value="user">普通用户</el-radio>
            <el-radio value="merchant">商家</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item>
          <el-button
            type="danger"
            size="large"
            class="register-btn"
            @click="handleRegister"
            :loading="loading"
          >
            立即注册
          </el-button>
        </el-form-item>
      </el-form>

      <div class="register-footer">
        <span class="hint">已有账号？</span>
        <router-link to="/login" class="link">立即登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Message, Phone } from '@element-plus/icons-vue'
import { register } from '@/api/auth'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const form = ref({
  username: '',
  password: '',
  confirmPassword: '',
  email: '',
  phone: '',
  role: 'user'
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== form.value.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 4, max: 20, message: '用户名长度在4-20位之间', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]+$/, message: '只能包含字母、数字和下划线', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    const { confirmPassword, ...data } = form.value
    await register(data)
    ElMessage.success('注册成功！请登录')
    router.push('/login')
  } catch {
    // error shown by interceptor
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #c0392b 0%, #e74c3c 40%, #c0392b 100%);
  padding: 40px 16px;
}
.register-card {
  background: #fff;
  border-radius: 16px;
  padding: 40px 44px;
  width: 480px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.25);
}
.register-logo {
  text-align: center;
  margin-bottom: 28px;
}
.logo-text {
  font-size: 26px;
  font-weight: 800;
  color: #c0392b;
  margin: 8px 0 4px;
}
.logo-sub {
  font-size: 13px;
  color: #999;
}
.register-btn {
  width: 100%;
  height: 46px;
  font-size: 16px;
  font-weight: 600;
  background: #c0392b;
  border-color: #c0392b;
}
.register-footer {
  text-align: center;
  font-size: 14px;
  color: #666;
  margin-top: 12px;
}
.link {
  color: #c0392b;
  font-weight: 600;
}
.link:hover {
  text-decoration: underline;
}
</style>
