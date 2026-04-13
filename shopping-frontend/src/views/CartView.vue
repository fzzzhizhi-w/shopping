<template>
  <div class="cart-page page-container">
    <h2 class="page-heading">我的购物车</h2>

    <div class="cart-layout" v-if="cartStore.items.length > 0">
      <!-- Cart Table -->
      <div class="cart-table-wrap">
        <el-table
          :data="cartStore.items"
          style="width: 100%"
          @selection-change="handleSelectionChange"
          row-key="id"
          ref="tableRef"
        >
          <el-table-column type="selection" width="50" />
          <el-table-column label="商品信息" min-width="280">
            <template #default="{ row }">
              <div class="product-cell">
                <img
                  :src="row.product?.mainImage || row.image || 'https://via.placeholder.com/80x80'"
                  class="product-thumb"
                  @click="goToProduct(row)"
                  @error="onImgError"
                />
                <span class="product-name" @click="goToProduct(row)">
                  {{ row.product?.name || row.productName || row.name }}
                </span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="单价" width="120">
            <template #default="{ row }">
              <span class="item-price">¥{{ formatPrice(row.price) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="数量" width="160">
            <template #default="{ row }">
              <el-input-number
                v-model="row.quantity"
                :min="1"
                :max="row.product?.stock || 999"
                size="small"
                @change="(val) => updateQuantity(row, val)"
              />
            </template>
          </el-table-column>
          <el-table-column label="小计" width="120">
            <template #default="{ row }">
              <span class="subtotal">¥{{ formatPrice(row.price * row.quantity) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80">
            <template #default="{ row }">
              <el-button type="danger" link @click="handleRemove(row.id)">
                <el-icon><Delete /></el-icon>
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="cart-footer">
          <el-checkbox v-model="allSelected" @change="toggleAll">全选</el-checkbox>
          <el-button type="danger" link @click="handleClearSelected" :disabled="selectedItems.length === 0">
            删除选中
          </el-button>
        </div>
      </div>

      <!-- Summary -->
      <div class="summary-card">
        <el-card class="summary">
          <template #header>
            <span class="summary-title">订单摘要</span>
          </template>
          <div class="summary-row">
            <span>已选商品</span>
            <span>{{ selectedItems.length }} 件</span>
          </div>
          <div class="summary-row">
            <span>商品总价</span>
            <span class="total-price">¥{{ formatPrice(selectedTotal) }}</span>
          </div>
          <div class="summary-row discount">
            <span>优惠</span>
            <span>-¥0.00</span>
          </div>
          <div class="summary-divider"></div>
          <div class="summary-row total-row">
            <span>合计</span>
            <span class="final-price">¥{{ formatPrice(selectedTotal) }}</span>
          </div>
          <el-button
            type="danger"
            size="large"
            class="checkout-btn"
            :disabled="selectedItems.length === 0"
            @click="handleCheckout"
          >
            去结算 ({{ selectedItems.length }})
          </el-button>
        </el-card>

        <el-card class="guarantee-card">
          <div class="guarantee-item"><el-icon><CircleCheck /></el-icon> 正品保证</div>
          <div class="guarantee-item"><el-icon><Van /></el-icon> 免费配送 (满99元)</div>
          <div class="guarantee-item"><el-icon><RefreshLeft /></el-icon> 7天无理由退换</div>
        </el-card>
      </div>
    </div>

    <!-- Empty Cart -->
    <el-empty v-else description="购物车空空如也" :image-size="160">
      <el-button type="danger" @click="router.push('/products')">去逛逛</el-button>
    </el-empty>

    <!-- Checkout Dialog -->
    <el-dialog v-model="checkoutVisible" title="填写收货信息" width="500px" :close-on-click-modal="false">
      <div v-if="savedAddresses.length > 0" class="saved-addresses">
        <p class="addr-hint">选择已保存的地址：</p>
        <div
          v-for="addr in savedAddresses"
          :key="addr.id"
          class="addr-item"
          :class="{ selected: selectedAddressId === addr.id }"
          @click="handleSelectAddress(addr)"
        >
          <span class="addr-name">{{ addr.name }}</span>
          <span class="addr-phone">{{ addr.phone }}</span>
          <span class="addr-detail">{{ [addr.province, addr.city, addr.district, addr.detail].filter(Boolean).join(' ') }}</span>
          <el-tag v-if="addr.isDefault" size="small" type="success" class="addr-default">默认</el-tag>
        </div>
        <el-divider>或手动填写</el-divider>
      </div>
      <el-form :model="orderForm" :rules="orderRules" ref="orderFormRef" label-width="80px">
        <el-form-item label="收货人" prop="receiverName">
          <el-input v-model="orderForm.receiverName" placeholder="请输入收货人姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="receiverPhone">
          <el-input v-model="orderForm.receiverPhone" placeholder="请输入手机号" maxlength="11" />
        </el-form-item>
        <el-form-item label="收货地址" prop="receiverAddress">
          <el-input
            v-model="orderForm.receiverAddress"
            type="textarea"
            :rows="3"
            placeholder="请输入详细地址"
          />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="orderForm.remark" placeholder="选填，备注给商家" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="checkoutVisible = false">取消</el-button>
        <el-button type="danger" @click="submitOrder" :loading="orderLoading">
          提交订单 (¥{{ formatPrice(selectedTotal) }})
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useCartStore } from '@/stores/cart'
import { getCart, removeFromCart, updateCart, clearCart } from '@/api/cart'
import { createOrder } from '@/api/order'
import { getAddresses } from '@/api/user'

const router = useRouter()
const cartStore = useCartStore()
const tableRef = ref(null)
const orderFormRef = ref(null)

const selectedItems = ref([])
const allSelected = ref(false)
const checkoutVisible = ref(false)
const orderLoading = ref(false)
const savedAddresses = ref([])
const selectedAddressId = ref(null)

const orderForm = ref({
  receiverName: '',
  receiverPhone: '',
  receiverAddress: '',
  remark: ''
})

const orderRules = {
  receiverName: [{ required: true, message: '请输入收货人姓名', trigger: 'blur' }],
  receiverPhone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  receiverAddress: [{ required: true, message: '请输入收货地址', trigger: 'blur' }]
}

const selectedTotal = computed(() =>
  selectedItems.value.reduce((sum, item) => sum + item.price * item.quantity, 0)
)

const formatPrice = (price) => {
  if (price == null) return '0.00'
  return Number(price).toFixed(2)
}

const onImgError = (e) => {
  e.target.src = 'https://via.placeholder.com/80x80?text=图'
}

const goToProduct = (row) => {
  const id = row.productId || row.product?.id
  if (id) router.push(`/products/${id}`)
}

const handleSelectionChange = (items) => {
  selectedItems.value = items
  allSelected.value = items.length === cartStore.items.length && cartStore.items.length > 0
}

const toggleAll = (val) => {
  if (val) {
    tableRef.value?.toggleAllSelection()
  } else {
    tableRef.value?.clearSelection()
  }
}

const updateQuantity = async (row, val) => {
  try {
    await updateCart(row.id, { quantity: val })
    const res = await getCart()
    cartStore.setCart(res.data || [])
  } catch {
    // ignore
  }
}

const handleRemove = async (id) => {
  await ElMessageBox.confirm('确定要删除该商品吗？', '提示', { type: 'warning' })
  try {
    await removeFromCart(id)
    const res = await getCart()
    cartStore.setCart(res.data || [])
    ElMessage.success('已删除')
  } catch {
    // ignore
  }
}

const handleClearSelected = async () => {
  await ElMessageBox.confirm(`确定删除选中的 ${selectedItems.value.length} 件商品吗？`, '提示', { type: 'warning' })
  try {
    for (const item of selectedItems.value) {
      await removeFromCart(item.id)
    }
    const res = await getCart()
    cartStore.setCart(res.data || [])
    ElMessage.success('已删除选中商品')
  } catch {
    // ignore
  }
}

const handleCheckout = async () => {
  if (selectedItems.value.length === 0) {
    ElMessage.warning('请先选择商品')
    return
  }
  checkoutVisible.value = true
  try {
    const res = await getAddresses()
    savedAddresses.value = res.data || []
  } catch {
    savedAddresses.value = []
  }
}

const handleSelectAddress = (addr) => {
  selectedAddressId.value = addr.id
  orderForm.value.receiverName = addr.name || ''
  orderForm.value.receiverPhone = addr.phone || ''
  const parts = [addr.province, addr.city, addr.district, addr.detail].filter(Boolean)
  orderForm.value.receiverAddress = parts.join(' ')
}

const submitOrder = async () => {
  await orderFormRef.value.validate()
  orderLoading.value = true
  try {
    await createOrder({
      address: orderForm.value.receiverAddress,
      receiverName: orderForm.value.receiverName,
      receiverPhone: orderForm.value.receiverPhone,
      remark: orderForm.value.remark,
      cartIds: selectedItems.value.map(item => item.id)
    })
    ElMessage.success('订单提交成功！')
    checkoutVisible.value = false
    const res = await getCart()
    cartStore.setCart(res.data || [])
    router.push('/orders')
  } catch {
    // ignore
  } finally {
    orderLoading.value = false
  }
}

onMounted(async () => {
  try {
    const res = await getCart()
    cartStore.setCart(res.data || [])
  } catch {
    // ignore
  }
})
</script>

<style scoped>
.cart-page {
  padding-top: 24px;
  padding-bottom: 40px;
}
.page-heading {
  font-size: 22px;
  font-weight: 700;
  color: #333;
  margin-bottom: 20px;
}
.cart-layout {
  display: flex;
  gap: 20px;
  align-items: flex-start;
}
.cart-table-wrap {
  flex: 1;
  min-width: 0;
  background: #fff;
  border-radius: 10px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}
.product-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}
.product-thumb {
  width: 70px;
  height: 70px;
  object-fit: cover;
  border-radius: 6px;
  cursor: pointer;
  border: 1px solid #eee;
}
.product-name {
  font-size: 13px;
  color: #333;
  cursor: pointer;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.product-name:hover {
  color: #c0392b;
}
.item-price {
  color: #666;
  font-size: 14px;
}
.subtotal {
  color: #c0392b;
  font-weight: 600;
  font-size: 15px;
}
.cart-footer {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px 0 0;
  border-top: 1px solid #f0f0f0;
  margin-top: 8px;
}
.summary-card {
  width: 280px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.summary-title {
  font-size: 16px;
  font-weight: 700;
}
.summary-row {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  color: #555;
  padding: 8px 0;
}
.total-price {
  color: #c0392b;
  font-weight: 600;
}
.discount {
  color: #27ae60;
}
.summary-divider {
  height: 1px;
  background: #f0f0f0;
  margin: 4px 0;
}
.total-row {
  font-size: 16px;
  font-weight: 700;
}
.final-price {
  color: #c0392b;
  font-size: 22px;
  font-weight: 800;
}
.checkout-btn {
  width: 100%;
  height: 46px;
  font-size: 16px;
  background: #c0392b;
  border-color: #c0392b;
  margin-top: 12px;
}
.guarantee-card {
  font-size: 13px;
  color: #666;
}
.guarantee-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 0;
}
.saved-addresses {
  margin-bottom: 12px;
}
.addr-hint {
  font-size: 13px;
  color: #666;
  margin-bottom: 8px;
}
.addr-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 12px;
  border: 1px solid #eee;
  border-radius: 6px;
  cursor: pointer;
  margin-bottom: 6px;
  font-size: 13px;
  transition: border-color 0.2s;
}
.addr-item:hover {
  border-color: #c0392b;
}
.addr-item.selected {
  border-color: #c0392b;
  background: #fff8f8;
}
.addr-name {
  font-weight: 600;
  flex-shrink: 0;
}
.addr-phone {
  color: #666;
  flex-shrink: 0;
}
.addr-detail {
  color: #555;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.addr-default {
  flex-shrink: 0;
}
</style>
