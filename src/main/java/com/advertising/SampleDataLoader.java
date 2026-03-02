package com.advertising;

import com.advertising.entity.*;
import com.advertising.mapper.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDate;

@Component
public class SampleDataLoader implements CommandLineRunner {

    @Autowired
    private CommunityMapper communityMapper;

    @Autowired
    private BarrierGateMapper barrierGateMapper;

    @Autowired
    private FrameMapper frameMapper;

    @Autowired
    private PlanMapper planMapper;

    @Autowired
    private PlanCommunityMapper planCommunityMapper;

    @Autowired
    private PlanBarrierMapper planBarrierMapper;

    @Autowired
    private PlanFrameMapper planFrameMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        try {
            // 1. 先创建所有必要的表
            createAITables();
            
            // 2. 检查示例数据是否已存在
            Plan existingPlan = planMapper.selectByPlanNo("PLAN20250601001");
            if (existingPlan != null) {
                System.out.println("Sample data already exists, skipping...");
                return;
            }

            System.out.println("Creating sample data...");

            // 1. Create communities
            Community comm1 = createCommunity("COMM001", "万科翡翠公园", "江苏省", "南京市", "江宁区", "天元西路88号");
            Community comm2 = createCommunity("COMM002", "中海国际社区", "江苏省", "南京市", "建邺区", "江东中路188号");
            Community comm3 = createCommunity("COMM003", "保利罗兰春天", "江苏省", "南京市", "栖霞区", "仙林大道128号");

            // 2. Create barrier gates
            BarrierGate bg1 = createBarrierGate(comm1.getId(), "GATE001", "DEV001", "南门");
            BarrierGate bg2 = createBarrierGate(comm1.getId(), "GATE002", "DEV002", "北门");
            BarrierGate bg3 = createBarrierGate(comm1.getId(), "GATE003", "DEV003", "东门");
            BarrierGate bg4 = createBarrierGate(comm2.getId(), "GATE004", "DEV004", "主入口");
            BarrierGate bg5 = createBarrierGate(comm2.getId(), "GATE005", "DEV005", "侧门");

            // 3. Create frames
            Frame f1 = createFrame(comm1.getId(), "FRAME001", "1栋", "1单元", "1号梯");
            Frame f2 = createFrame(comm1.getId(), "FRAME002", "1栋", "2单元", "1号梯");
            Frame f3 = createFrame(comm1.getId(), "FRAME003", "2栋", "1单元", "1号梯");
            Frame f4 = createFrame(comm3.getId(), "FRAME004", "5栋", "1单元", "1号梯");
            Frame f5 = createFrame(comm3.getId(), "FRAME005", "5栋", "2单元", "1号梯");

            // 4. Create sample plan 1: Coca-Cola (Barrier)
            Plan plan1 = createPlan("PLAN20250601001", "可口可乐夏季促销道闸投放方案", "可口可乐",
                "选择南京市核心社区主入口道闸广告位，覆盖高端住宅小区，触达家庭消费群体");

            // Create plan communities for plan 1
            PlanCommunity pc1 = createPlanCommunity(plan1.getId(), comm1.getId(), 3, 0);
            PlanCommunity pc2 = createPlanCommunity(plan1.getId(), comm2.getId(), 2, 0);

            // Create plan barriers
            createPlanBarrier(plan1.getId(), bg1.getId(), pc1.getId());
            createPlanBarrier(plan1.getId(), bg2.getId(), pc1.getId());
            createPlanBarrier(plan1.getId(), bg3.getId(), pc1.getId());
            createPlanBarrier(plan1.getId(), bg4.getId(), pc2.getId());
            createPlanBarrier(plan1.getId(), bg5.getId(), pc2.getId());

            // 5. Create sample plan 2: Huawei (Frame)
            Plan plan2 = createPlan("PLAN20250601002", "华为Mate系列新品发布框架投放方案", "华为",
                "选择南京市高端社区电梯框架广告位，精准触达商务人群");

            PlanCommunity pc3 = createPlanCommunity(plan2.getId(), comm1.getId(), 0, 3);
            PlanCommunity pc4 = createPlanCommunity(plan2.getId(), comm3.getId(), 0, 2);

            createPlanFrame(plan2.getId(), f1.getId(), pc3.getId());
            createPlanFrame(plan2.getId(), f2.getId(), pc3.getId());
            createPlanFrame(plan2.getId(), f3.getId(), pc3.getId());
            createPlanFrame(plan2.getId(), f4.getId(), pc4.getId());
            createPlanFrame(plan2.getId(), f5.getId(), pc4.getId());

            // 6. Create sample plan 3: Xiaomi (Mixed)
            Plan plan3 = createPlan("PLAN20250601003", "小米智能家居全场景投放方案", "小米",
                "道闸+框架组合投放，实现社区入口和电梯内双重曝光");

            PlanCommunity pc5 = createPlanCommunity(plan3.getId(), comm1.getId(), 2, 2);
            PlanCommunity pc6 = createPlanCommunity(plan3.getId(), comm2.getId(), 2, 1);

            createPlanBarrier(plan3.getId(), bg1.getId(), pc5.getId());
            createPlanBarrier(plan3.getId(), bg2.getId(), pc5.getId());
            createPlanBarrier(plan3.getId(), bg4.getId(), pc6.getId());
            createPlanBarrier(plan3.getId(), bg5.getId(), pc6.getId());

            createPlanFrame(plan3.getId(), f1.getId(), pc5.getId());
            createPlanFrame(plan3.getId(), f2.getId(), pc5.getId());
            createPlanFrame(plan3.getId(), f4.getId(), pc6.getId());

            System.out.println("Sample data created successfully!");
            System.out.println("Created 3 sample plans with barrier and frame selections.");

        } catch (Exception e) {
            System.err.println("Sample data creation failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createAITables() {
        try {
            System.out.println("Checking and creating AI tables...");
            
            // 创建 ai_agent 表
            jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS ai_agent (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    agent_id VARCHAR(50) NOT NULL UNIQUE,
                    name VARCHAR(100) NOT NULL,
                    role VARCHAR(50),
                    description TEXT,
                    system_prompt TEXT,
                    welcome_message TEXT,
                    tools JSON,
                    knowledge_base JSON,
                    status VARCHAR(20) DEFAULT 'active',
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    INDEX idx_agent_id (agent_id),
                    INDEX idx_status (status)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
            """);
            
            // 创建 ai_conversations 表
            jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS ai_conversations (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    session_id VARCHAR(100) NOT NULL,
                    agent_id VARCHAR(50) NOT NULL,
                    user_id VARCHAR(50),
                    title VARCHAR(200),
                    status VARCHAR(20) DEFAULT 'active',
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    INDEX idx_session_id (session_id),
                    INDEX idx_agent_id (agent_id),
                    INDEX idx_user_id (user_id),
                    INDEX idx_status (status)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
            """);
            
            // 创建 ai_messages 表
            jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS ai_messages (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    conversation_id INT NOT NULL,
                    role VARCHAR(20) NOT NULL,
                    content TEXT NOT NULL,
                    metadata JSON,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    INDEX idx_conversation_id (conversation_id),
                    INDEX idx_role (role),
                    FOREIGN KEY (conversation_id) REFERENCES ai_conversations(id) ON DELETE CASCADE
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
            """);
            
            // 插入默认Agent数据
            insertDefaultAgents();
            
            System.out.println("AI tables created successfully!");
        } catch (Exception e) {
            System.err.println("Error creating AI tables: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void insertDefaultAgents() {
        try {
            // 检查是否已有数据
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM ai_agent", Integer.class);
            if (count != null && count > 0) {
                System.out.println("AI agents already exist, skipping...");
                return;
            }
            
            // 插入销售AI助理
            jdbcTemplate.update("""
                INSERT INTO ai_agent (agent_id, name, role, description, system_prompt, welcome_message) 
                VALUES (?, ?, ?, ?, ?, ?)
            """, "agent_sales", "销售AI助理", "sales", 
                "我是永达传媒的销售AI助理，专精于广告销售领域。我可以帮您管理客户、跟进商机、生成报价、分析业绩数据等。",
                "你是永达传媒的销售AI助理，专精于广告销售领域。",
                "您好！我是销售AI助理。我可以帮您：\n• 管理客户信息和联系记录\n• 跟进商机和推进销售流程\n• 快速生成报价和合同\n• 分析销售业绩和客户价值\n• 提供市场洞察和竞品分析\n\n请问有什么可以帮您的吗？");
            
            // 插入媒介AI助理
            jdbcTemplate.update("""
                INSERT INTO ai_agent (agent_id, name, role, description, system_prompt, welcome_message) 
                VALUES (?, ?, ?, ?, ?, ?)
            """, "agent_media", "媒介AI助理", "media",
                "我是永达传媒的媒介AI助理，专注于媒体资源管理。我可以帮您查询库存、优化资源配置、分析点位效果等。",
                "你是永达传媒的媒介AI助理，专精于媒体资源管理。",
                "您好！我是媒介AI助理。我可以帮您：\n• 查询各区域广告位库存情况\n• 优化资源配置和点位分配\n• 分析点位历史投放效果\n• 制定上下刊计划\n• 监控点位状态\n\n请问有什么可以帮您的吗？");
            
            // 插入工程AI助理
            jdbcTemplate.update("""
                INSERT INTO ai_agent (agent_id, name, role, description, system_prompt, welcome_message) 
                VALUES (?, ?, ?, ?, ?, ?)
            """, "agent_engineering", "工程AI助理", "engineering",
                "我是永达传媒的工程AI助理，专注于工程执行领域。我可以帮您安排上刊任务、管理工程进度、处理异常问题等。",
                "你是永达传媒的工程AI助理，专精于工程执行领域。",
                "您好！我是工程AI助理。我可以帮您：\n• 安排上刊任务和人员调度\n• 管理工程进度和质量\n• 处理监播异常问题\n• 管理工程档案\n• 协调资源保障\n\n请问有什么可以帮您的吗？");
            
            System.out.println("Default AI agents inserted successfully!");
        } catch (Exception e) {
            System.err.println("Error inserting default agents: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Community createCommunity(String no, String name, String province, String city, String district, String address) {
        Community community = new Community();
        community.setCommunityNo(no);
        community.setBuildingName(name);
        community.setBuildingAddress(address);
        community.setCity(city);
        communityMapper.insert(community);
        return communityMapper.selectByCommunityNo(no);
    }

    private BarrierGate createBarrierGate(Integer communityId, String gateNo, String deviceNo, String location) {
        BarrierGate gate = new BarrierGate();
        gate.setCommunityId(communityId);
        gate.setGateNo(gateNo);
        gate.setDeviceNo(deviceNo);
        gate.setDoorLocation(location);
        barrierGateMapper.insert(gate);
        return barrierGateMapper.selectByGateNo(gateNo);
    }

    private Frame createFrame(Integer communityId, String frameNo, String building, String unit, String elevator) {
        Frame frame = new Frame();
        frame.setCommunityId(communityId);
        frame.setFrameNo(frameNo);
        frame.setBuilding(building);
        frame.setUnit(unit);
        frame.setElevator(elevator);
        frameMapper.insert(frame);
        return frameMapper.selectByFrameNo(frameNo);
    }

    private Plan createPlan(String planNo, String planName, String customer, String mediaReq) {
        Plan plan = new Plan();
        plan.setPlanNo(planNo);
        plan.setPlanName(planName);
        plan.setCustomer(customer);
        plan.setMediaRequirements(mediaReq);
        plan.setReleaseDateBegin(LocalDate.of(2025, 6, 1));
        plan.setReleaseDateEnd(LocalDate.of(2025, 8, 31));
        plan.setReleaseStatus(1);
        plan.setSalesType(1);
        planMapper.insert(plan);
        return planMapper.selectByPlanNo(planNo);
    }

    private PlanCommunity createPlanCommunity(Integer planId, Integer communityId, int barrierQty, int frameQty) {
        PlanCommunity pc = new PlanCommunity();
        pc.setPlanId(planId);
        pc.setCommunityId(communityId);
        pc.setReleaseDateBegin(LocalDate.of(2025, 6, 1));
        pc.setReleaseDateEnd(LocalDate.of(2025, 8, 31));
        pc.setBarrierRequiredQty(barrierQty);
        pc.setFrameRequiredQty(frameQty);
        planCommunityMapper.insert(pc);
        // Return the inserted record
        return planCommunityMapper.selectAll().stream()
            .filter(p -> p.getPlanId().equals(planId) && p.getCommunityId().equals(communityId))
            .findFirst()
            .orElse(null);
    }

    private void createPlanBarrier(Integer planId, Integer barrierGateId, Integer planCommunityId) {
        PlanBarrier pb = new PlanBarrier();
        pb.setPlanId(planId);
        pb.setBarrierGateId(barrierGateId);
        pb.setPlanCommunityId(planCommunityId);
        pb.setReleaseDateBegin(LocalDate.of(2025, 6, 1));
        pb.setReleaseDateEnd(LocalDate.of(2025, 8, 31));
        pb.setReleaseStatus(1);
        planBarrierMapper.insert(pb);
    }

    private void createPlanFrame(Integer planId, Integer frameId, Integer planCommunityId) {
        PlanFrame pf = new PlanFrame();
        pf.setPlanId(planId);
        pf.setFrameId(frameId);
        pf.setPlanCommunityId(planCommunityId);
        pf.setReleaseDateBegin(LocalDate.of(2025, 6, 1));
        pf.setReleaseDateEnd(LocalDate.of(2025, 8, 31));
        pf.setReleaseStatus(1);
        planFrameMapper.insert(pf);
    }
}