<template>
  <div class="space-y-6">
    <div v-if="loading" class="flex items-center justify-center h-64">
      <div class="text-subtext-light dark:text-subtext-dark">加载中...</div>
    </div>

    <div v-else-if="error || !barrierGate" class="flex items-center justify-center h-64">
      <div class="text-red-500">{{ error || '道闸不存在' }}</div>
    </div>

    <template v-else>
      <!-- 页面标题 -->
      <div class="flex items-center justify-between">
        <div class="flex items-center space-x-4">
          <button
            @click="$router.push('/barrier-gates')"
            class="text-subtext-light dark:text-subtext-dark hover:text-text-light dark:hover:text-text-dark transition-colors"
          >
            ← 返回
          </button>
          <h1 class="text-2xl font-bold text-text-light dark:text-text-dark">
            道闸详情
          </h1>
        </div>
        <div class="space-x-2">
          <button class="px-4 py-2 bg-primary text-white rounded-lg hover:bg-primary/90 transition-colors">
            编辑
          </button>
          <button class="px-4 py-2 bg-red-500 text-white rounded-lg hover:bg-red-600 transition-colors">
            删除
          </button>
        </div>
      </div>

      <!-- 基本信息 -->
      <div class="bg-surface-light dark:bg-surface-dark rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 p-6">
        <h2 class="text-lg font-semibold text-text-light dark:text-text-dark mb-4">基本信息</h2>
        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="text-sm text-subtext-light dark:text-subtext-dark">道闸编号</label>
            <p class="text-text-light dark:text-text-dark font-medium">{{ barrierGate.gateNo }}</p>
          </div>
          <div>
            <label class="text-sm text-subtext-light dark:text-subtext-dark">设备编号</label>
            <p class="text-text-light dark:text-text-dark font-medium">{{ barrierGate.deviceNo }}</p>
          </div>
          <div>
            <label class="text-sm text-subtext-light dark:text-subtext-dark">门岗位置</label>
            <p class="text-text-light dark:text-text-dark font-medium">{{ barrierGate.doorLocation }}</p>
          </div>
          <div>
            <label class="text-sm text-subtext-light dark:text-subtext-dark">设备位置</label>
            <p class="text-text-light dark:text-text-dark font-medium">
              {{ getDevicePositionText(barrierGate.devicePosition) }}
            </p>
          </div>
          <div>
            <label class="text-sm text-subtext-light dark:text-subtext-dark">屏幕位置</label>
            <p class="text-text-light dark:text-text-dark font-medium">
              {{ getScreenPositionText(barrierGate.screenPosition) }}
            </p>
          </div>
          <div>
            <label class="text-sm text-subtext-light dark:text-subtext-dark">灯箱方向</label>
            <p class="text-text-light dark:text-text-dark font-medium">
              {{ getLightboxDirectionText(barrierGate.lightboxDirection) }}
            </p>
          </div>
        </div>
      </div>

      <!-- 所属社区信息 -->
      <div v-if="community" class="bg-surface-light dark:bg-surface-dark rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 p-6">
        <h2 class="text-lg font-semibold text-text-light dark:text-text-dark mb-4">所属社区</h2>
        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="text-sm text-subtext-light dark:text-subtext-dark">社区编号</label>
            <p class="text-text-light dark:text-text-dark font-medium">{{ community.communityNo }}</p>
          </div>
          <div>
            <label class="text-sm text-subtext-light dark:text-subtext-dark">社区名称</label>
            <p class="text-text-light dark:text-text-dark font-medium">{{ community.buildingName }}</p>
          </div>
          <div>
            <label class="text-sm text-subtext-light dark:text-subtext-dark">城市</label>
            <p class="text-text-light dark:text-text-dark font-medium">{{ community.city }}</p>
          </div>
          <div>
            <label class="text-sm text-subtext-light dark:text-subtext-dark">地址</label>
            <p class="text-text-light dark:text-text-dark font-medium">{{ community.buildingAddress }}</p>
          </div>
        </div>
        <div class="mt-4">
          <button
            @click="$router.push(`/communities/${community.id}`)"
            class="text-primary hover:text-primary/80 transition-colors"
          >
            查看社区详情 →
          </button>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import barrierGateService, { BarrierGate } from '@/src/services/barrierGateService';
import communityService, { Community } from '@/src/services/communityService';

const route = useRoute();
const barrierGate = ref<BarrierGate | null>(null);
const community = ref<Community | null>(null);
const loading = ref(true);
const error = ref('');

const loadBarrierGateDetail = async (barrierId: number) => {
  try {
    loading.value = true;
    
    const barrierData = await barrierGateService.getById(barrierId);
    barrierGate.value = barrierData;

    if (barrierData.communityId) {
      const communityData = await communityService.getById(barrierData.communityId);
      community.value = communityData;
    }
  } catch (err) {
    error.value = err instanceof Error ? err.message : '加载失败';
  } finally {
    loading.value = false;
  }
};

const getDevicePositionText = (position?: number) => {
  switch (position) {
    case 1: return '入口';
    case 2: return '出口';
    default: return '未知';
  }
};

const getScreenPositionText = (position?: number) => {
  switch (position) {
    case 1: return '左侧';
    case 2: return '右侧';
    default: return '未知';
  }
};

const getLightboxDirectionText = (direction?: number) => {
  switch (direction) {
    case 1: return '正面';
    case 2: return '反面';
    default: return '未知';
  }
};

onMounted(() => {
  const id = route.params.id as string;
  if (id) {
    loadBarrierGateDetail(parseInt(id));
  }
});
</script>
