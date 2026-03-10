import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '@/src/stores/auth';

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/src/pages/Login.vue'),
      meta: { public: true },
    },
    {
      path: '/',
      component: () => import('@/src/components/Layout.vue'),
      redirect: '/plans',
      children: [
        {
          path: 'plans',
          name: 'Plans',
          component: () => import('@/src/pages/Plans.vue'),
        },
        {
          path: 'plans/:id',
          name: 'PlanDetail',
          component: () => import('@/src/pages/PlanDetail.vue'),
        },
        {
          path: 'communities',
          name: 'Communities',
          component: () => import('@/src/pages/Communities.vue'),
        },
        {
          path: 'communities/:id',
          name: 'CommunityDetail',
          component: () => import('@/src/pages/CommunityDetail.vue'),
        },
        {
          path: 'barrier-gates',
          name: 'BarrierGates',
          component: () => import('@/src/pages/BarrierGates.vue'),
        },
        {
          path: 'barrier-gates/:id',
          name: 'BarrierGateDetail',
          component: () => import('@/src/pages/BarrierGateDetail.vue'),
        },
        {
          path: 'frames',
          name: 'Frames',
          component: () => import('@/src/pages/Frames.vue'),
        },
        {
          path: 'frames/:id',
          name: 'FrameDetail',
          component: () => import('@/src/pages/FrameDetail.vue'),
        },
        {
          path: 'plan-communities',
          name: 'PlanCommunities',
          component: () => import('@/src/pages/PlanCommunities.vue'),
        },
        {
          path: 'plan-barriers',
          name: 'PlanBarriers',
          component: () => import('@/src/pages/PlanBarriers.vue'),
        },
        {
          path: 'plan-frames',
          name: 'PlanFrames',
          component: () => import('@/src/pages/PlanFrames.vue'),
        },
        {
          path: 'ai-assistant',
          name: 'AIAssistant',
          component: () => import('@/src/pages/AIAssistantPage.vue'),
        },
      ],
    },
    {
      path: '/:pathMatch(.*)*',
      redirect: '/plans',
    },
  ],
});

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore();
  
  if (!to.meta.public && !authStore.isAuthenticated) {
    next('/login');
  } else {
    next();
  }
});

export default router;
