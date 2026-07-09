<template>
  <div class="product-detail">
    <el-card>
      <template #header>
        <div class="detail-header">
          <el-button @click="$router.back()" text>
            <el-icon><ArrowLeft /></el-icon>返回
          </el-button>
          <span class="title">商品详情</span>
        </div>
      </template>
      <el-descriptions :column="2" border v-if="product">
        <el-descriptions-item label="商品编码">{{ product.sku }}</el-descriptions-item>
        <el-descriptions-item label="商品名称">{{ product.name }}</el-descriptions-item>
        <el-descriptions-item label="单位">{{ product.unit }}</el-descriptions-item>
        <el-descriptions-item label="当前库存">
          <el-tag :type="product.stockQuantity > product.safeStock * 2 ? 'success' : product.stockQuantity > product.safeStock ? 'warning' : 'danger'">
            {{ product.stockQuantity }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="安全库存">{{ product.safeStock }}</el-descriptions-item>
        <el-descriptions-item label="备注">{{ product.remark || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ product.createdAt || '-' }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ product.updatedAt || '-' }}</el-descriptions-item>
      </el-descriptions>
      <el-empty v-else description="未找到商品信息" />
    </el-card>
  </div>
</template>

<script setup lang="ts">

defineOptions({ name: 'ProductDetail' })
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getProductDetail } from '@/api/product'
import type { Product } from '@/api/types/product'

const route = useRoute()
const product = ref<Product | null>(null)

onMounted(async () => {
  try {
    const id = Number(route.params.id)
    const res = await getProductDetail(id)
    product.value = res.data
  } catch (e) {
    product.value = null
  }
})
</script>

<style scoped lang="scss">
.product-detail {
  .detail-header {
    display: flex;
    align-items: center;
    gap: 16px;

    .title {
      font-size: 16px;
      font-weight: 600;
    }
  }
}
</style>
