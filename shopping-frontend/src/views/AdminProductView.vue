<template>
  <div class="admin-product-page page-container">
    <div class="page-header">
      <h2 class="page-heading">商品管理</h2>
      <el-button type="primary" @click="openDialog()">
        <el-icon><Plus /></el-icon> 新增商品
      </el-button>
    </div>

    <!-- Search Bar -->
    <div class="search-bar">
      <el-input
        v-model="keyword"
        placeholder="搜索商品名称"
        clearable
        style="width: 240px"
        @keyup.enter="loadProducts"
        @clear="loadProducts"
      />
      <el-select v-model="statusFilter" placeholder="状态" clearable style="width: 120px" @change="loadProducts">
        <el-option label="上架" :value="1" />
        <el-option label="下架" :value="0" />
      </el-select>
      <el-button type="primary" @click="loadProducts">搜索</el-button>
    </div>

    <!-- Product Table -->
    <el-table :data="products" v-loading="loading" border stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column label="图片" width="80">
        <template #default="{ row }">
          <el-image
            :src="row.mainImage || 'https://via.placeholder.com/50x50'"
            style="width: 50px; height: 50px; object-fit: cover"
            fit="cover"
          />
        </template>
      </el-table-column>
      <el-table-column prop="name" label="商品名称" min-width="160" show-overflow-tooltip />
      <el-table-column prop="price" label="价格" width="100">
        <template #default="{ row }">¥{{ row.price }}</template>
      </el-table-column>
      <el-table-column prop="stock" label="库存" width="80" />
      <el-table-column prop="sales" label="销量" width="80" />
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">
            {{ row.status === 1 ? '上架' : '下架' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openDialog(row)">编辑</el-button>
          <el-button
            size="small"
            :type="row.status === 1 ? 'warning' : 'success'"
            @click="toggleStatus(row)"
          >
            {{ row.status === 1 ? '下架' : '上架' }}
          </el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- Pagination -->
    <el-pagination
      v-model:current-page="pageNum"
      v-model:page-size="pageSize"
      :total="total"
      :page-sizes="[10, 20, 50]"
      layout="total, sizes, prev, pager, next"
      class="pagination"
      @size-change="loadProducts"
      @current-change="loadProducts"
    />

    <!-- Add/Edit Dialog -->
    <el-dialog
      v-model="dialogVisible"
      :title="form.id ? '编辑商品' : '新增商品'"
      width="600px"
      @closed="resetForm"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="90px">
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="商品描述">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入商品描述" />
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="form.price" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="库存" prop="stock">
          <el-input-number v-model="form.stock" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="form.categoryId" placeholder="请选择分类" style="width: 100%">
            <el-option
              v-for="cat in categories"
              :key="cat.id"
              :label="cat.name"
              :value="cat.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="主图URL">
          <el-input v-model="form.mainImage" placeholder="请输入主图URL" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">上架</el-radio>
            <el-radio :label="0">下架</el-radio>
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
import {
  adminListProducts,
  adminCreateProduct,
  adminUpdateProduct,
  adminDeleteProduct,
  adminUpdateProductStatus,
  adminListCategories
} from '@/api/admin'

const loading = ref(false)
const saving = ref(false)
const products = ref([])
const categories = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const keyword = ref('')
const statusFilter = ref(null)
const dialogVisible = ref(false)
const formRef = ref(null)

const form = ref({
  id: null,
  name: '',
  description: '',
  price: 0,
  stock: 0,
  categoryId: null,
  mainImage: '',
  status: 1
})

const rules = {
  name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  stock: [{ required: true, message: '请输入库存', trigger: 'blur' }]
}

const loadProducts = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      keyword: keyword.value || undefined,
      status: statusFilter.value !== null && statusFilter.value !== undefined ? statusFilter.value : undefined
    }
    const res = await adminListProducts(params)
    products.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch {
    // handled by interceptor
  } finally {
    loading.value = false
  }
}

const loadCategories = async () => {
  try {
    const res = await adminListCategories()
    categories.value = res.data || []
  } catch {
    // ignore
  }
}

const openDialog = (row = null) => {
  if (row) {
    form.value = {
      id: row.id,
      name: row.name,
      description: row.description || '',
      price: row.price,
      stock: row.stock,
      categoryId: row.categoryId,
      mainImage: row.mainImage || '',
      status: row.status
    }
  } else {
    resetForm()
  }
  dialogVisible.value = true
}

const resetForm = () => {
  form.value = { id: null, name: '', description: '', price: 0, stock: 0, categoryId: null, mainImage: '', status: 1 }
  formRef.value?.clearValidate()
}

const handleSave = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  saving.value = true
  try {
    const payload = { ...form.value }
    delete payload.id
    if (form.value.id) {
      await adminUpdateProduct(form.value.id, payload)
      ElMessage.success('商品更新成功')
    } else {
      await adminCreateProduct(payload)
      ElMessage.success('商品创建成功')
    }
    dialogVisible.value = false
    loadProducts()
  } catch {
    // handled by interceptor
  } finally {
    saving.value = false
  }
}

const toggleStatus = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  try {
    await adminUpdateProductStatus(row.id, newStatus)
    ElMessage.success(newStatus === 1 ? '已上架' : '已下架')
    loadProducts()
  } catch {
    // handled by interceptor
  }
}

const handleDelete = async (id) => {
  await ElMessageBox.confirm('确定要删除该商品吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).catch(() => { throw new Error('cancel') })

  try {
    await adminDeleteProduct(id)
    ElMessage.success('删除成功')
    loadProducts()
  } catch (e) {
    if (e.message !== 'cancel') { /* handled */ }
  }
}

onMounted(() => {
  loadProducts()
  loadCategories()
})
</script>

<style scoped>
.admin-product-page {
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
.search-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}
.pagination {
  margin-top: 20px;
  justify-content: flex-end;
}
</style>
