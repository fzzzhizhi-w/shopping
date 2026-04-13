<template>
  <div class="admin-banner-page page-container">
    <div class="page-header">
      <h2 class="page-heading">轮播图管理</h2>
      <el-button type="primary" @click="openDialog()"><el-icon><Plus /></el-icon> 新增轮播图</el-button>
    </div>

    <el-table :data="banners" v-loading="loading" border stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column label="图片" width="120">
        <template #default="{ row }">
          <el-image :src="row.image" style="width:100px;height:50px;object-fit:cover" fit="cover" />
        </template>
      </el-table-column>
      <el-table-column prop="title" label="标题" min-width="140" />
      <el-table-column prop="link" label="链接" min-width="160" show-overflow-tooltip />
      <el-table-column prop="sort" label="排序" width="80" />
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

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑轮播图' : '新增轮播图'" width="480px" @closed="resetForm">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="form.title" placeholder="标题（可选）" />
        </el-form-item>
        <el-form-item label="图片URL" prop="image">
          <el-input v-model="form.image" placeholder="请输入图片URL" />
        </el-form-item>
        <el-form-item label="跳转链接">
          <el-input v-model="form.link" placeholder="点击跳转链接（可选）" />
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
import { adminListBanners, adminCreateBanner, adminUpdateBanner, adminDeleteBanner } from '@/api/admin'

const loading = ref(false)
const saving = ref(false)
const banners = ref([])
const dialogVisible = ref(false)
const formRef = ref(null)
const form = ref({ id: null, title: '', image: '', link: '', sort: 0, status: 1 })
const rules = { image: [{ required: true, message: '请输入图片URL', trigger: 'blur' }] }

const load = async () => {
  loading.value = true
  try {
    const res = await adminListBanners()
    banners.value = res.data || []
  } finally { loading.value = false }
}

const openDialog = (row = null) => {
  form.value = row ? { ...row } : { id: null, title: '', image: '', link: '', sort: 0, status: 1 }
  dialogVisible.value = true
}

const resetForm = () => { formRef.value?.clearValidate() }

const handleSave = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  saving.value = true
  try {
    const { id, ...payload } = form.value
    if (id) { await adminUpdateBanner(id, payload); ElMessage.success('更新成功') }
    else { await adminCreateBanner(payload); ElMessage.success('创建成功') }
    dialogVisible.value = false
    load()
  } finally { saving.value = false }
}

const handleDelete = async (id) => {
  await ElMessageBox.confirm('确定删除？', '提示', { type: 'warning' }).catch(() => { throw new Error('cancel') })
  try { await adminDeleteBanner(id); ElMessage.success('删除成功'); load() }
  catch (e) { if (e.message !== 'cancel') { /* handled */ } }
}

onMounted(load)
</script>

<style scoped>
.admin-banner-page { padding: 24px 0; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.page-heading { margin: 0; font-size: 22px; font-weight: 700; color: #333; }
</style>
