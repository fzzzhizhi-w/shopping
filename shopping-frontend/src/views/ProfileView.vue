<template>
  <div class="profile-page page-container">
    <h2 class="page-heading">个人中心</h2>

    <div class="profile-layout">
      <!-- Left: Info Card -->
      <div class="profile-sidebar">
        <el-card class="avatar-card">
          <div class="avatar-section">
            <div class="avatar-circle">
              <span class="avatar-letter">
                {{ (userStore.userInfo?.username || 'U').charAt(0).toUpperCase() }}
              </span>
            </div>
            <div class="user-meta">
              <div class="username">{{ userStore.userInfo?.username || '-' }}</div>
              <div class="user-since">注册会员</div>
            </div>
          </div>
          <div class="stats-row">
            <div class="stat-item" @click="router.push('/orders')">
              <div class="stat-num">{{ orderCount }}</div>
              <div class="stat-label">我的订单</div>
            </div>
            <div class="stat-item" @click="router.push('/cart')">
              <div class="stat-num">{{ cartStore.itemCount }}</div>
              <div class="stat-label">购物车</div>
            </div>
            <div class="stat-item" @click="router.push('/ai-chat')">
              <div class="stat-num"><el-icon><ChatDotRound /></el-icon></div>
              <div class="stat-label">AI助手</div>
            </div>
          </div>
        </el-card>

        <el-card class="menu-card">
          <ul class="side-menu">
            <li
              v-for="item in menuItems"
              :key="item.key"
              class="menu-item"
              :class="{ active: activeTab === item.key }"
              @click="activeTab = item.key"
            >
              <el-icon><component :is="item.icon" /></el-icon>
              {{ item.label }}
            </li>
          </ul>
        </el-card>
      </div>

      <!-- Right: Content -->
      <div class="profile-content">
        <!-- Basic Info -->
        <el-card v-if="activeTab === 'info'" class="content-card">
          <template #header>
            <span class="card-title">基本信息</span>
          </template>
          <div v-loading="infoLoading">
            <el-form :model="infoForm" :rules="infoRules" ref="infoFormRef" label-width="100px">
              <el-form-item label="用户名">
                <el-input :value="userStore.userInfo?.username" disabled />
              </el-form-item>
              <el-form-item label="邮箱" prop="email">
                <el-input v-model="infoForm.email" placeholder="请输入邮箱" clearable />
              </el-form-item>
              <el-form-item label="手机号" prop="phone">
                <el-input v-model="infoForm.phone" placeholder="请输入手机号" clearable maxlength="11" />
              </el-form-item>
              <el-form-item label="注册时间">
                <el-input :value="formatTime(userStore.userInfo?.createTime)" disabled />
              </el-form-item>
              <el-form-item>
                <el-button type="danger" @click="handleUpdateInfo" :loading="infoLoading">
                  保存修改
                </el-button>
              </el-form-item>
            </el-form>
          </div>
        </el-card>

        <!-- Change Password -->
        <el-card v-if="activeTab === 'password'" class="content-card">
          <template #header>
            <span class="card-title">修改密码</span>
          </template>
          <el-form :model="pwdForm" :rules="pwdRules" ref="pwdFormRef" label-width="100px" style="max-width: 460px">
            <el-form-item label="原密码" prop="oldPassword">
              <el-input
                v-model="pwdForm.oldPassword"
                type="password"
                placeholder="请输入原密码"
                show-password
              />
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input
                v-model="pwdForm.newPassword"
                type="password"
                placeholder="请输入新密码（至少6位）"
                show-password
              />
            </el-form-item>
            <el-form-item label="确认新密码" prop="confirmPassword">
              <el-input
                v-model="pwdForm.confirmPassword"
                type="password"
                placeholder="请再次输入新密码"
                show-password
              />
            </el-form-item>
            <el-form-item>
              <el-button type="danger" @click="handleChangePassword" :loading="pwdLoading">
                修改密码
              </el-button>
              <el-button @click="pwdFormRef?.resetFields()">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <!-- Orders Quick View -->
        <el-card v-if="activeTab === 'orders'" class="content-card">
          <template #header>
            <div class="card-header-row">
              <span class="card-title">我的订单</span>
              <el-button link type="danger" @click="router.push('/orders')">查看全部 →</el-button>
            </div>
          </template>
          <div v-loading="ordersLoading" class="orders-preview">
            <el-empty v-if="!ordersLoading && recentOrders.length === 0" description="暂无订单" />
            <div v-for="order in recentOrders" :key="order.id" class="order-preview-item"
              @click="router.push(`/orders/${order.id}`)">
              <div class="order-preview-left">
                <div class="order-no">{{ order.orderNo || order.id }}</div>
                <div class="order-time">{{ formatTime(order.createTime) }}</div>
              </div>
              <div class="order-preview-right">
                <el-tag :type="getStatusType(order.status)" size="small">
                  {{ getStatusLabel(order.status) }}
                </el-tag>
                <div class="order-amount">¥{{ formatPrice(order.totalAmount) }}</div>
              </div>
            </div>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { useCartStore } from '@/stores/cart'
import { getUserInfo, updateUserInfo, changePassword } from '@/api/auth'
import { getOrders } from '@/api/order'

const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()

const activeTab = ref('info')
const infoFormRef = ref(null)
const pwdFormRef = ref(null)
const infoLoading = ref(false)
const pwdLoading = ref(false)
const ordersLoading = ref(false)
const recentOrders = ref([])
const orderCount = ref(0)

const menuItems = [
  { key: 'info', label: '基本信息', icon: 'User' },
  { key: 'password', label: '修改密码', icon: 'Lock' },
  { key: 'orders', label: '我的订单', icon: 'List' }
]

const infoForm = reactive({
  email: userStore.userInfo?.email || '',
  phone: userStore.userInfo?.phone || ''
})

const infoRules = {
  email: [{ type: 'email', message: '邮箱格式不正确', trigger: 'blur' }],
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }]
}

const pwdForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const pwdRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== pwdForm.newPassword) callback(new Error('两次输入密码不一致'))
        else callback()
      },
      trigger: 'blur'
    }
  ]
}

const formatTime = (t) => {
  if (!t) return '-'
  return new Date(t).toLocaleString('zh-CN')
}
const formatPrice = (v) => (v == null ? '0.00' : Number(v).toFixed(2))
const statusMap = {
  PENDING: { label: '待付款', type: 'warning' },
  PAID: { label: '待发货', type: 'primary' },
  SHIPPED: { label: '待收货', type: 'primary' },
  COMPLETED: { label: '已完成', type: 'success' },
  CANCELLED: { label: '已取消', type: 'info' }
}
const getStatusLabel = (s) => statusMap[s]?.label || s || '未知'
const getStatusType = (s) => statusMap[s]?.type || 'info'

const handleUpdateInfo = async () => {
  await infoFormRef.value.validate()
  infoLoading.value = true
  try {
    await updateUserInfo(infoForm)
    const res = await getUserInfo()
    userStore.setUserInfo(res.data)
    infoForm.email = res.data?.email || ''
    infoForm.phone = res.data?.phone || ''
    ElMessage.success('信息更新成功')
  } catch {
    // ignore
  } finally {
    infoLoading.value = false
  }
}

const handleChangePassword = async () => {
  await pwdFormRef.value.validate()
  pwdLoading.value = true
  try {
    await changePassword({ oldPassword: pwdForm.oldPassword, newPassword: pwdForm.newPassword })
    ElMessage.success('密码修改成功，请重新登录')
    userStore.logout()
    router.push('/login')
  } catch {
    // ignore
  } finally {
    pwdLoading.value = false
  }
}

onMounted(async () => {
  try {
    const res = await getUserInfo()
    userStore.setUserInfo(res.data)
    infoForm.email = res.data?.email || ''
    infoForm.phone = res.data?.phone || ''
  } catch {
    // ignore
  }

  ordersLoading.value = true
  try {
    const res = await getOrders({ page: 1, size: 5 })
    const data = res.data || {}
    recentOrders.value = data.records || data.list || data.content || data || []
    orderCount.value = data.total || recentOrders.value.length
  } catch {
    // ignore
  } finally {
    ordersLoading.value = false
  }
})
</script>

<style scoped>
.profile-page {
  padding-top: 24px;
  padding-bottom: 40px;
}
.page-heading {
  font-size: 22px;
  font-weight: 700;
  color: #333;
  margin-bottom: 20px;
}
.profile-layout {
  display: flex;
  gap: 20px;
  align-items: flex-start;
}
.profile-sidebar {
  width: 220px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.avatar-card {
  text-align: center;
  border-radius: 10px;
}
.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px 0 16px;
  border-bottom: 1px solid #f0f0f0;
}
.avatar-circle {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: linear-gradient(135deg, #c0392b, #e74c3c);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 12px;
  box-shadow: 0 4px 12px rgba(192, 57, 43, 0.3);
}
.avatar-letter {
  font-size: 36px;
  font-weight: 800;
  color: #fff;
}
.username {
  font-size: 16px;
  font-weight: 700;
  color: #333;
}
.user-since {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}
.stats-row {
  display: flex;
  justify-content: space-around;
  padding: 16px 0 4px;
}
.stat-item {
  text-align: center;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 6px;
  transition: background 0.15s;
}
.stat-item:hover {
  background: #f5f5f5;
}
.stat-num {
  font-size: 20px;
  font-weight: 700;
  color: #c0392b;
  display: flex;
  justify-content: center;
  align-items: center;
}
.stat-label {
  font-size: 12px;
  color: #999;
  margin-top: 2px;
}
.menu-card {
  border-radius: 10px;
}
.side-menu {
  list-style: none;
  margin: 0;
  padding: 0;
}
.menu-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 16px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  color: #555;
  transition: all 0.15s;
}
.menu-item:hover {
  background: #fff0f0;
  color: #c0392b;
}
.menu-item.active {
  background: #c0392b;
  color: #fff;
  font-weight: 600;
}
.profile-content {
  flex: 1;
  min-width: 0;
}
.content-card {
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}
.card-title {
  font-size: 16px;
  font-weight: 700;
  color: #333;
}
.card-header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.orders-preview {
  display: flex;
  flex-direction: column;
  gap: 12px;
  min-height: 100px;
}
.order-preview-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-radius: 8px;
  background: #fafafa;
  border: 1px solid #f0f0f0;
  cursor: pointer;
  transition: all 0.15s;
}
.order-preview-item:hover {
  border-color: #c0392b;
  background: #fff8f8;
}
.order-no {
  font-size: 13px;
  color: #555;
  font-family: monospace;
}
.order-time {
  font-size: 12px;
  color: #999;
  margin-top: 2px;
}
.order-preview-right {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
}
.order-amount {
  font-size: 16px;
  font-weight: 700;
  color: #c0392b;
}
</style>
