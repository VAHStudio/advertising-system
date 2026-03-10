<template>
  <div class="space-y-6">
    <div v-if="loading" class="flex items-center justify-center h-64">
      <div class="text-subtext-light dark:text-subtext-dark">加载中...</div>
    </div>

    <div v-else-if="error || !frame" class="flex items-center justify-center h-64">
      <div class="text-red-500">{{ error || '框架不存在' }}</div>
    </div>

    <template v-else>
      <!-- 页面标题 -->
      <div class="flex items-center justify-between">
        <div class="flex items-center space-x-4">
          <button
            @click="$router.push('/frames')"
            class="text-subtext-light dark:text-subtext-dark hover:text-text-light dark:hover:text-text-dark transition-colors"
          >
            ← 返回
          </button>
          <h1 class="text-2xl font-bold text-text-light dark:text-text-dark">
            框架详情
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
            <label class="text-sm text-subtext-light dark:text-subtext-dark">框架编号</label>
            <p class="text-text-light dark:text-text-dark font-medium">{{ frame.frameNo }}</p>
          </div>
          <div>
            <label class="text-sm text-subtext-light dark:text-subtext-dark">楼栋</label>
            <p class="text-text-light dark:text-text-dark font-medium">{{ frame.building }}</p>
          </div>
          <div>
            <label class="text-sm text-subtext-light dark:text-subtext-dark">单元</label>
            <p class="text-text-light dark:text-text-dark font-medium">{{ frame.unit }}</p>
          </div>
          <div>
            <label class="text-sm text-subtext-light dark:text-subtext-dark">电梯</label>
            <p class="text-text-light dark:text-text-dark font-medium">{{ frame.elevator }}</p>
          </div>
          <div>
            <label class="text-sm text-subtext-light dark:text-subtext-dark">内外位置</label>
            <p class="text-text-light dark:text-text-dark font-medium">
              {{ getInnerPositionText(frame.innerPosition) }}
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
import frameService, { Frame } from '@/src/services/frameService';
import communityService, { Community } from '@/src/services/communityService';

const route = useRoute();
const frame = ref<Frame | null>(null);
const community = ref<Community | null>(null);
const loading = ref(true);
const error = ref('');

const loadFrameDetail = async (frameId: number) => {
  try {
    loading.value = true;
    
    const frameData = await frameService.getById(frameId);
    frame.value = frameData;

    if (frameData.communityId) {
      const communityData = await communityService.getById(frameData.communityId);
      community.value = communityData;
    }
  } catch (err) {
    error.value = err instanceof Error ? err.message : '加载失败';
  } finally {
    loading.value = false;
  }
};

const getInnerPositionText = (position?: number) => {
  switch (position) {
    case 1: return '内';
    case 2: return '外';
    default: return '未知';
  }
};

onMounted(() => {
  const id = route.params.id as string;
  if (id) {
    loadFrameDetail(parseInt(id));
  }
});
</script>
