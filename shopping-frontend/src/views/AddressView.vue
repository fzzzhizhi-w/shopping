<template>
  <div class="address-page page-container">
    <div class="page-header">
      <h2 class="page-heading">收货地址</h2>
      <el-button type="primary" @click="openDialog()">
        <el-icon><Plus /></el-icon> 新增地址
      </el-button>
    </div>

    <div v-loading="loading" class="address-list">
      <el-empty v-if="!loading && addresses.length === 0" description="暂无收货地址" />
      <div v-for="addr in addresses" :key="addr.id" class="address-card" :class="{ 'is-default': addr.isDefault === 1 }">
        <div class="addr-info">
          <div class="addr-top">
            <span class="addr-name">{{ addr.name }}</span>
            <span class="addr-phone">{{ addr.phone }}</span>
            <el-tag v-if="addr.isDefault === 1" type="danger" size="small">默认</el-tag>
          </div>
          <div class="addr-detail">
            {{ [addr.province, addr.city, addr.district, addr.detail].filter(Boolean).join(' ') }}
          </div>
        </div>
        <div class="addr-actions">
          <el-button size="small" @click="openDialog(addr)">编辑</el-button>
          <el-button size="small" type="primary" v-if="addr.isDefault !== 1" @click="handleSetDefault(addr.id)">设为默认</el-button>
          <el-button size="small" type="danger" @click="handleDelete(addr.id)">删除</el-button>
        </div>
      </div>
    </div>

    <!-- Dialog -->
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑地址' : '新增地址'" width="520px" @closed="resetForm">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="收货人姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="收货人手机号" />
        </el-form-item>
        <el-form-item label="省份">
          <el-input v-model="form.province" placeholder="省份（可选）" />
        </el-form-item>
        <el-form-item label="城市">
          <el-input v-model="form.city" placeholder="城市（可选）" />
        </el-form-item>
        <el-form-item label="区县">
          <el-input v-model="form.district" placeholder="区县（可选）" />
        </el-form-item>
        <el-form-item label="详细地址" prop="detail">
          <el-input v-model="form.detail" placeholder="详细地址" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="设为默认">
          <el-switch v-model="form.isDefault" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getAddresses, addAddress, updateAddress, deleteAddress, setDefaultAddress } from '@/api/user'

const loading = ref(false)
const saving = ref(false)
const addresses = ref([])
const dialogVisible = ref(false)
const formRef = ref(null)

const form = ref({ id: null, name: '', phone: '', province: '', city: '', district: '', detail: '', isDefault: 0 })

const rules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  detail: [{ required: true, message: '请输入详细地址', trigger: 'blur' }]
}

const loadAddresses = async () => {
  loading.value = true
  try {
    const res = await getAddresses()
    addresses.value = res.data || []
  } catch {
    // handled
  } finally {
    loading.value = false
  }
}

const openDialog = (addr = null) => {
  if (addr) {
    form.value = { id: addr.id, name: addr.name, phone: addr.phone, province: addr.province || '',
      city: addr.city || '', district: addr.district || '', detail: addr.detail, isDefault: addr.isDefault }
  } else {
    resetForm()
  }
  dialogVisible.value = true
}

const resetForm = () => {
  form.value = { id: null, name: '', phone: '', province: '', city: '', district: '', detail: '', isDefault: 0 }
  formRef.value?.clearValidate()
}

const handleSave = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  saving.value = true
  try {
    const payload = { ...form.value }
    if (form.value.id) {
      await updateAddress(form.value.id, payload)
      ElMessage.success('地址更新成功')
    } else {
      await addAddress(payload)
      ElMessage.success('地址添加成功')
    }
    dialogVisible.value = false
    loadAddresses()
  } catch {
    // handled
  } finally {
    saving.value = false
  }
}

const handleSetDefault = async (id) => {
  try {
    await setDefaultAddress(id)
    ElMessage.success('默认地址已更新')
    loadAddresses()
  } catch {
    // handled
  }
}

const handleDelete = async (id) => {
  await ElMessageBox.confirm('确定删除该地址吗？', '提示', {
    confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
  }).catch(() => { throw new Error('cancel') })
  try {
    await deleteAddress(id)
    ElMessage.success('删除成功')
    loadAddresses()
  } catch (e) {
    if (e.message !== 'cancel') { /* handled */ }
  }
}

onMounted(loadAddresses)
</script>

<style scoped>
.address-page {
  padding: 24px 0;
}
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.page-heading {
  font-size: 22px;
  font-weight: 700;
  color: #333;
  margin: 0;
}
.address-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.address-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border: 1px solid #eee;
  border-radius: 8px;
  background: #fff;
}
.address-card.is-default {
  border-color: #c0392b;
  background: #fff9f9;
}
.addr-top {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 6px;
}
.addr-name {
  font-weight: 600;
  font-size: 15px;
}
.addr-phone {
  color: #666;
  font-size: 14px;
}
.addr-detail {
  color: #555;
  font-size: 14px;
}
.addr-actions {
  display: flex;
  gap: 8px;
  flex-shrink: 0;
}
</style>
