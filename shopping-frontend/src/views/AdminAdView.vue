<template>
  <div class="admin-ad-page page-container">
    <div class="page-header">
      <h2 class="page-heading">广告位管理</h2>
      <el-button type="primary" @click="openDialog()"><el-icon><Plus /></el-icon> 新增广告</el-button>
    </div>

    <el-table :data="ads" v-loading="loading" border stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column label="图片" width="120">
        <template #default="{ row }">
          <el-image :src="row.image" style="width:100px;height:50px;object-fit:cover" fit="cover" />
        </template>
      </el-table-column>
      <el-table-column prop="title" label="标题" min-width="120" />
      <el-table-column prop="position" label="位置" width="120" />
      <el-table-column prop="link" label="链接" min-width="140" show-overflow-tooltip />
      <el-table-column prop="sort" label="排序" width="70" />
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '停用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openDialog(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑广告' : '新增广告'" width="480px" @closed="resetForm">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="form.title" placeholder="广告标题（可选）" />
        </el-form-item>
        <el-form-item label="图片URL" prop="image">
          <el-input v-model="form.image" placeholder="请输入图片URL" />
        </el-form-item>
        <el-form-item label="跳转链接">
          <el-input v-model="form.link" placeholder="点击跳转链接" />
        </el-form-item>
        <el-form-item label="展示位置">
          <el-select v-model="form.position" placeholder="选择展示位置" style="width:100%">
            <el-option label="首页顶部" value="home_top" />
            <el-option label="首页侧边" value="home_side" />
            <el-option label="商品列表" value="product_list" />
          </el-select>
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" style="width:100%" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">停用</el-radio>
          </el-radio-group>
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
import { adminListAds, adminCreateAd, adminUpdateAd, adminDeleteAd } from '@/api/admin'

const loading = ref(false)
const saving = ref(false)
const ads = ref([])
const dialogVisible = ref(false)
const formRef = ref(null)
const form = ref({ id: null, title: '', image: '', link: '', position: 'home_top', sort: 0, status: 1 })
const rules = { image: [{ required: true, message: '请输入图片URL', trigger: 'blur' }] }

const load = async () => {
  loading.value = true
  try {
    const res = await adminListAds()
    ads.value = res.data || []
  } finally { loading.value = false }
}

const openDialog = (row = null) => {
  form.value = row ? { ...row } : { id: null, title: '', image: '', link: '', position: 'home_top', sort: 0, status: 1 }
  dialogVisible.value = true
}

const resetForm = () => { formRef.value?.clearValidate() }

const handleSave = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  saving.value = true
  try {
    const { id, ...payload } = form.value
    if (id) { await adminUpdateAd(id, payload); ElMessage.success('更新成功') }
    else { await adminCreateAd(payload); ElMessage.success('创建成功') }
    dialogVisible.value = false
    load()
  } finally { saving.value = false }
}

const handleDelete = async (id) => {
  await ElMessageBox.confirm('确定删除？', '提示', { type: 'warning' }).catch(() => { throw new Error('cancel') })
  try { await adminDeleteAd(id); ElMessage.success('删除成功'); load() }
  catch (e) { if (e.message !== 'cancel') { /* handled */ } }
}

onMounted(load)
</script>

<style scoped>
.admin-ad-page { padding: 24px 0; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.page-heading { margin: 0; font-size: 22px; font-weight: 700; color: #333; }
</style>
