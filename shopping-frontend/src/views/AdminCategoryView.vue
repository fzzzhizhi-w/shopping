<template>
  <div class="admin-category-page page-container">
    <div class="page-header">
      <h2 class="page-heading">分类管理</h2>
      <el-button type="primary" @click="openDialog()">
        <el-icon><Plus /></el-icon> 新增分类
      </el-button>
    </div>

    <el-table :data="categories" v-loading="loading" border stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="分类名称" min-width="160" />
      <el-table-column prop="icon" label="图标" width="120" />
      <el-table-column prop="sort" label="排序" width="80" />
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openDialog(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- Add/Edit Dialog -->
    <el-dialog
      v-model="dialogVisible"
      :title="form.id ? '编辑分类' : '新增分类'"
      width="420px"
      @closed="resetForm"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="图标">
          <el-input v-model="form.icon" placeholder="图标名称或URL（可选）" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" style="width: 100%" />
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
import {
  adminListCategories,
  adminCreateCategory,
  adminUpdateCategory,
  adminDeleteCategory
} from '@/api/admin'

const loading = ref(false)
const saving = ref(false)
const categories = ref([])
const dialogVisible = ref(false)
const formRef = ref(null)

const form = ref({ id: null, name: '', icon: '', sort: 0 })

const rules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }]
}

const loadCategories = async () => {
  loading.value = true
  try {
    const res = await adminListCategories()
    categories.value = res.data || []
  } catch {
    // handled by interceptor
  } finally {
    loading.value = false
  }
}

const openDialog = (row = null) => {
  if (row) {
    form.value = { id: row.id, name: row.name, icon: row.icon || '', sort: row.sort || 0 }
  } else {
    resetForm()
  }
  dialogVisible.value = true
}

const resetForm = () => {
  form.value = { id: null, name: '', icon: '', sort: 0 }
  formRef.value?.clearValidate()
}

const handleSave = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  saving.value = true
  try {
    const payload = { name: form.value.name, icon: form.value.icon, sort: form.value.sort }
    if (form.value.id) {
      await adminUpdateCategory(form.value.id, payload)
      ElMessage.success('分类更新成功')
    } else {
      await adminCreateCategory(payload)
      ElMessage.success('分类创建成功')
    }
    dialogVisible.value = false
    loadCategories()
  } catch {
    // handled by interceptor
  } finally {
    saving.value = false
  }
}

const handleDelete = async (id) => {
  await ElMessageBox.confirm('确定要删除该分类吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).catch(() => { throw new Error('cancel') })

  try {
    await adminDeleteCategory(id)
    ElMessage.success('删除成功')
    loadCategories()
  } catch (e) {
    if (e.message !== 'cancel') { /* handled */ }
  }
}

onMounted(() => {
  loadCategories()
})
</script>

<style scoped>
.admin-category-page {
  padding: 24px 0;
}
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.page-heading {
  margin: 0;
  font-size: 22px;
  font-weight: 700;
  color: #333;
}
</style>
