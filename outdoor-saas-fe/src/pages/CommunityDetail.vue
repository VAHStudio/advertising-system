<template>
  <div class="space-y-6">
    <div v-if="loading" class="flex items-center justify-center h-64">
      <div class="text-subtext-light dark:text-subtext-dark">加载中...</div>
    </div>

    <div v-else-if="error || !community" class="flex items-center justify-center h-64">
      <div class="text-red-500">{{ error || '社区不存在' }}</div>
    </div>

    <template v-else>
      <!-- 页面标题和操作按钮 -->
      <div class="flex items-center justify-between">
        <div class="flex items-center space-x-4">
          <button
            @click="$router.push('/communities')"
            class="text-subtext-light dark:text-subtext-dark hover:text-text-light dark:hover:text-text-dark transition-colors"
          >
            ← 返回
          </button>
          <h1 class="text-2xl font-bold text-text-light dark:text-text-dark">
            社区详情
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

      <!-- 基本信息卡片 -->
      <div class="bg-surface-light dark:bg-surface-dark rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 p-6">
        <h2 class="text-lg font-semibold text-text-light dark:text-text-dark mb-4">基本信息</h2>
        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="text-sm text-subtext-light dark:text-subtext-dark">社区编号</label>
            <p class="text-text-light dark:text-text-dark font-medium">{{ community.communityNo }}</p>
          </div>
          <div>
            <label class="text-sm text-subtext-light dark:text-subtext-dark">楼宇名称</label>
            <p class="text-text-light dark:text-text-dark font-medium">{{ community.buildingName }}</p>
          </div>
          <div>
            <label class="text-sm text-subtext-light dark:text-subtext-dark">地址</label>
            <p class="text-text-light dark:text-text-dark font-medium">{{ community.buildingAddress }}</p>
          </div>
          <div>
            <label class="text-sm text-subtext-light dark:text-subtext-dark">城市</label>
            <p class="text-text-light dark:text-text-dark font-medium">{{ community.city }}</p>
          </div>
          <div>
            <label class="text-sm text-subtext-light dark:text-subtext-dark">坐标</label>
            <p class="text-text-light dark:text-text-dark font-medium">
              {{ community.coordLat }}, {{ community.coordLng }}
            </p>
          </div>
          <div>
            <label class="text-sm text-subtext-light dark:text-subtext-dark">媒体数量</label>
            <p class="text-text-light dark:text-text-dark font-medium">
              道闸: {{ barrierGates.length }} | 框架: {{ frames.length }}
            </p>
          </div>
        </div>
      </div>

      <!-- 道闸列表 -->
      <div class="bg-surface-light dark:bg-surface-dark rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 overflow-hidden">
        <div class="px-6 py-4 border-b border-gray-200 dark:border-gray-700">
          <h2 class="text-lg font-semibold text-text-light dark:text-text-dark">
            道闸列表 ({{ barrierGates.length }})
          </h2>
        </div>
        <table class="w-full">
          <thead class="bg-gray-50 dark:bg-gray-800">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-semibold text-subtext-light dark:text-subtext-dark uppercase">编号</th>
              <th class="px-6 py-3 text-left text-xs font-semibold text-subtext-light dark:text-subtext-dark uppercase">设备号</th>
              <th class="px-6 py-3 text-left text-xs font-semibold text-subtext-light dark:text-subtext-dark uppercase">位置</th>
              <th class="px-6 py-3 text-left text-xs font-semibold text-subtext-light dark:text-subtext-dark uppercase">状态</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-200 dark:divide-gray-700">
            <tr v-for="gate in barrierGates" :key="gate.id" class="hover:bg-gray-50 dark:hover:bg-gray-800/50">
              <td class="px-6 py-4 text-sm text-text-light dark:text-text-dark">{{ gate.gateNo }}</td>
              <td class="px-6 py-4 text-sm text-subtext-light dark:text-subtext-dark">{{ gate.deviceNo }}</td>
              <td class="px-6 py-4 text-sm text-subtext-light dark:text-subtext-dark">{{ gate.doorLocation }}</td>
              <td class="px-6 py-4 text-sm">
                <span :class="['px-2 py-1 rounded text-xs', gate.releaseStatus === 1 
                  ? 'bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-200' 
                  : 'bg-gray-100 text-gray-800 dark:bg-gray-700 dark:text-gray-300']">
                  {{ gate.releaseStatus === 1 ? '可用' : '占用' }}
                </span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- 框架列表 -->
      <div class="bg-surface-light dark:bg-surface-dark rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 overflow-hidden">
        <div class="px-6 py-4 border-b border-gray-200 dark:border-gray-700">
          <h2 class="text-lg font-semibold text-text-light dark:text-text-dark">
            框架列表 ({{ frames.length }})
          </h2>
        </div>
        <table class="w-full">
          <thead class="bg-gray-50 dark:bg-gray-800">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-semibold text-subtext-light dark:text-subtext-dark uppercase">编号</th>
              <th class="px-6 py-3 text-left text-xs font-semibold text-subtext-light dark:text-subtext-dark uppercase">楼栋</th>
              <th class="px-6 py-3 text-left text-xs font-semibold text-subtext-light dark:text-subtext-dark uppercase">单元</th>
              <th class="px-6 py-3 text-left text-xs font-semibold text-subtext-light dark:text-subtext-dark uppercase">电梯</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-200 dark:divide-gray-700">
            <tr v-for="frame in frames" :key="frame.id" class="hover:bg-gray-50 dark:hover:bg-gray-800/50">
              <td class="px-6 py-4 text-sm text-text-light dark:text-text-dark">{{ frame.frameNo }}</td>
              <td class="px-6 py-4 text-sm text-subtext-light dark:text-subtext-dark">{{ frame.building }}</td>
              <td class="px-6 py-4 text-sm text-subtext-light dark:text-subtext-dark">{{ frame.unit }}</td>
              <td class="px-6 py-4 text-sm text-subtext-light dark:text-subtext-dark">{{ frame.elevator }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import communityService, { Community } from '@/src/services/communityService';
import barrierGateService, { BarrierGate } from '@/src/services/barrierGateService';
import frameService, { Frame } from '@/src/services/frameService';

const route = useRoute();
const community = ref<Community | null>(null);
const barrierGates = ref<BarrierGate[]>([]);
const frames = ref<Frame[]>([]);
const loading = ref(true);
const error = ref('');

const loadCommunityDetail = async (communityId: number) => {
  try {
    loading.value = true;
    
    // 并行加载社区详情、道闸列表、框架列表
    const [communityData, barriersData, framesData] = await Promise.all([
      communityService.getById(communityId),
      barrierGateService.getByCommunity(communityId),
      frameService.getByCommunity(communityId),
    ]);

    community.value = communityData;
    barrierGates.value = barriersData;
    frames.value = framesData;
  } catch (err) {
    error.value = err instanceof Error ? err.message : '加载失败';
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  const id = route.params.id as string;
  if (id) {
    loadCommunityDetail(parseInt(id));
  }
});
</script>
