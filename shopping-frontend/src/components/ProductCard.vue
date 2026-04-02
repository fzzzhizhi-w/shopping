<template>
  <el-card class="product-card" shadow="hover" @click="goToDetail">
    <div class="product-image">
      <img
        :src="product.mainImage || `https://via.placeholder.com/300x300?text=${encodeURIComponent(product.name || '商品')}`"
        :alt="product.name"
        @error="onImgError"
      />
      <div class="product-badge" v-if="product.isHot">热销</div>
      <div class="product-badge new-badge" v-else-if="product.isNew">新品</div>
    </div>
    <div class="product-info">
      <div class="product-name">{{ product.name }}</div>
      <div class="product-desc">{{ product.description }}</div>
      <div class="product-price">
        <span class="price-symbol">¥</span>
        <span class="price-value">{{ formatPrice(product.price) }}</span>
      </div>
      <div class="product-meta">
        <span class="sales">{{ product.sales || 0 }}人已购</span>
        <span class="stock" v-if="product.stock !== undefined">库存: {{ product.stock }}</span>
      </div>
    </div>
  </el-card>
</template>

<script setup>
import { useRouter } from 'vue-router'

const props = defineProps({
  product: {
    type: Object,
    required: true
  }
})

const router = useRouter()

const goToDetail = () => {
  router.push(`/products/${props.product.id}`)
}

const formatPrice = (price) => {
  if (price == null) return '0.00'
  return Number(price).toFixed(2)
}

const onImgError = (e) => {
  e.target.src = `https://via.placeholder.com/300x300?text=暂无图片`
}
</script>

<style scoped>
.product-card {
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  border-radius: 8px;
  overflow: hidden;
  height: 100%;
  display: flex;
  flex-direction: column;
}
.product-card:hover {
  transform: translateY(-4px);
}
.product-image {
  position: relative;
  width: 100%;
  padding-bottom: 100%;
  overflow: hidden;
  background: #f8f8f8;
}
.product-image img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}
.product-card:hover .product-image img {
  transform: scale(1.05);
}
.product-badge {
  position: absolute;
  top: 8px;
  left: 8px;
  background: #c0392b;
  color: #fff;
  font-size: 11px;
  padding: 2px 7px;
  border-radius: 3px;
  font-weight: 600;
}
.new-badge {
  background: #f39c12;
}
.product-info {
  padding: 10px;
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.product-name {
  font-size: 13px;
  font-weight: 600;
  color: #333;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  min-height: 36px;
}
.product-desc {
  font-size: 11px;
  color: #999;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.product-price {
  color: #c0392b;
  font-weight: 700;
  margin-top: 4px;
}
.price-symbol {
  font-size: 13px;
}
.price-value {
  font-size: 20px;
}
.product-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 11px;
  color: #999;
  margin-top: 2px;
}
.sales {
  color: #f39c12;
  font-weight: 500;
}
</style>
