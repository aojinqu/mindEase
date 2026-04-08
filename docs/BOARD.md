# MindEase 开发看板（BOARD）

## 1. 当前状态

- 项目阶段：`v0.4-mvp-frontend-closed`（MVP 前端主流程已闭环）
- 开发状态：`进行中`
- 更新时间：`2026-04-08`

## 2. 本次完成（根据 PRD + 技术设计）

### 2.1 工程与架构骨架

- 已将命名空间统一为 `com.mindease`
- 已补齐依赖骨架：`Fragment`、`Lifecycle`、`RecyclerView`、`Room`
- 已建立分层目录结构：
  - `app`
  - `common`
  - `data`
  - `domain`
  - `feature`

### 2.2 应用入口与导航骨架

- 已新增 `MindEaseApp`
- 已配置 `SplashActivity` 为启动页
- 已新增页面骨架：
  - `SplashActivity`
  - `OnboardingActivity`
  - `AuthActivity`
  - `MainActivity`
  - `SettingsActivity`
- 已在 `MainActivity` 搭建 Bottom Navigation + 5 个主页面 Fragment：
  - `HomeFragment`
  - `AnalysisFragment`
  - `CalendarFragment`
  - `CommunityFragment`
  - `ProfileFragment`

### 2.3 数据层骨架（Room）

- 已新增 Entity：
  - `MoodRecordEntity`
  - `MoodTagEntity`
  - `MoodRecordTagCrossRef`
  - `SuggestionEntity`
  - `AnalysisSnapshotEntity`
- 已新增 DAO：
  - `MoodRecordDao`
  - `MoodTagDao`
  - `SuggestionDao`
  - `AnalysisSnapshotDao`
- 已新增数据库入口：`MindEaseDatabase`

### 2.4 领域层与仓储骨架

- 已新增 Domain Model：`MoodRecord`、`Suggestion`
- 已新增 Repository 接口：
  - `AuthRepository`
  - `UserRepository`
  - `MoodRepository`
  - `AnalysisRepository`
  - `SuggestionRepository`
  - `CommunityRepository`
- 已新增 Repository 实现并打通核心内存数据流（Room 持久化待接入）
- 已新增 UseCase 骨架：
  - `CreateMoodRecordUseCase`
  - `GetRecentMoodRecordsUseCase`
  - `GenerateMoodAnalysisUseCase`
  - `GenerateSuggestionUseCase`
- 已新增 Service 骨架：
  - `RuleBasedSentimentAnalyzer`
  - `SuggestionEngine`

### 2.5 资源与UI骨架

- 已新增布局：
  - `activity_splash.xml`
  - `activity_onboarding.xml`
  - `activity_auth.xml`
  - `activity_main.xml`
  - `activity_settings.xml`
  - `fragment_home.xml`
  - `fragment_analysis.xml`
  - `fragment_calendar.xml`
  - `fragment_community.xml`
  - `fragment_profile.xml`
- 已新增底部导航菜单：`res/menu/bottom_nav_menu.xml`
- 已补充基础文案：`res/values/strings.xml`

## 3. 看板状态更新

### 3.1 P0（核心骨架）

- 创建 Android 项目骨架：`已完成`
- 搭建基础 package 结构：`已完成`
- MainActivity + Bottom Navigation：`已完成`
- Room 基础结构：`已完成`
- 情绪记录主流程：`已完成（领域层核心逻辑）`

### 3.2 P1（MVP核心能力）

- 登录注册：`已完成（本地会话态 + 表单校验 + 流程跳转）`
- 分析页图表：`已完成（LineChart + BarChart）`
- 建议页与首页摘要：`已完成（已接入实时分析/建议）`
- 日历页：`已完成（按日期点击查看详情）`

### 3.3 P2（增强能力）

- 匿名社区发帖与浏览：`已完成（含独立发帖页 + 详情页）`
- Firebase 接入：`未开始`
- UI细化与空状态：`进行中`

## 4. MVP覆盖检查（更新后）

- 注册与登录：`已完成（前端闭环）`
- 创建/编辑/删除情绪记录：`已完成（页面 + 交互 + 数据流）`
- 标签与文本输入：`已完成（MoodEditor 页面）`
- 基础情绪分析：`已完成（规则分析）`
- 周/月趋势图：`已完成（真实图表组件）`
- 情绪日历：`已完成（可点击日期查看当日详情）`
- 个性化建议：`已完成（规则建议）`
- 匿名发帖与浏览：`已完成（含发帖页、详情页、筛选与跳转）`
- 页面真实数据流：`已完成（MoodEditor -> Home/Analysis/Calendar/Community）`
- 模拟器可运行：`已通过 assembleDebug 构建验证`

## 5. 风险与备注

- 已完成核心领域逻辑 + MVP 前端主流程闭环 + 页面真实数据绑定
- Firebase 与 AI 远程能力未接入
- 需要下一阶段尽快打通 `MoodRecord` 的真实本地增删改查链路

## 6. 下一步（建议迭代顺序）

1. 将 `MoodRepository` 从内存实现切换为 Room 持久化
2. 接入 Auth + 用户隔离（按 user_id 管理记录）
3. 接入 Firebase/Firestore 并替换社区本地内存实现
4. 接入 AI 分析通道与失败降级策略

## 7. 开发日志

### 2026-03-25

- 读取 PRD、技术设计、原看板
- 生成并落地 Android 分层代码骨架
- 搭建启动页、主导航与五大主 Fragment
- 建立 Room Entity/DAO/Database 骨架
- 更新 BOARD 状态为 `v0.2-skeleton`
- 完成 MVP 核心 UI/UX 页面（Home / MoodEditor / Analysis / Calendar / Community / Profile）
- 更新 BOARD 状态为 `v0.3-mvp-ui`
- 实现应用级 `AppContainer`，统一仓储与用例实例
- 完成 `MoodEditor` 真正写入记录并触发分析与建议
- 完成 Home/Analysis/Calendar/Community 页面真实数据读取与刷新
- 验证通过：`gradlew test`、`gradlew assembleDebug`

### 2026-04-08

- 完成前端导航闭环：`Splash -> Onboarding/Auth/Main`
- 完成登录/注册页面交互与本地会话态接入
- 完成情绪记录页面新增/编辑/删除闭环（含最近记录选择）
- Analysis 页面接入真实图表（折线 + 柱状）
- Calendar 页面改为可点击日期并展示当日详情
- 新增 `PostEditorActivity`、`PostDetailActivity` 并接入 Community 跳转
- Profile/Settings 页面完成可操作项（昵称、匿名名、隐私/提醒开关、登出）
- 补齐基础 ViewModel 分层并接入关键页面
- 构建验证通过：`gradlew test`、`gradlew assembleDebug`

## 8. 测试模块（新增）

### 8.1 目标

- 将“核心能力 + 自动化测试”作为同一迭代交付物
- 保证主流程改动后可快速回归

### 8.2 当前状态

- 核心单元测试：`进行中`（本轮已补齐核心用例）
- UI 自动化测试：`未开始`

### 8.3 测试任务看板

| 任务 | 状态 | 备注 |
|---|---|---|
| MoodRepository CRUD 测试 | 已完成 | 覆盖 create/update/delete/getRecent |
| Sentiment Analyzer 测试 | 已完成 | 覆盖 positive/negative/空文本 |
| 核心链路用例测试 | 已完成 | 覆盖 create -> analysis -> suggestion |
| UI 流程测试 | 进行中 | 关键页面已落地，后续接入 Espresso |

### 8.4 下一步

1. 补充 Room DAO 仿真测试
2. 新增 ViewModel 层测试
3. 在 CI 中接入 `gradlew test`
  
  
## 9. 文档对照缺口（2026-04-08）

> 对照 `PRD.md`、`TECHNICAL_DESIGN.md` 与当前代码状态补充。以下条目属于“文档目标已定义，但实现未完成/未闭环”。

### 9.1 前端状态（已更新）

- 登录/注册流程：`已完成`
- Splash/Onboarding/Auth/Main 导航闭环：`已完成`
- 情绪记录编辑/删除交互：`已完成`
- 分析页真实图表：`已完成`
- 日历按日期查看详情：`已完成`
- 社区详情页与独立发帖页：`已完成`
- Profile/Settings 可操作项：`已完成`
- 关键页面 ViewModel 接入：`已完成（基础形态）`

### 9.2 后端/数据层未完成

已在 `2026-04-08` 本轮完成（本地后端闭环）：

- 账号仓储占位已替换：`AuthRepositoryImpl` 已接入 `SessionManager` 登录态读取。
- 用户隔离已生效：`UserRepositoryImpl` 基于登录邮箱生成稳定 `user_id`（不再固定 `guest`）。
- Room 主链路已打通：`RoomMoodRepository` 已接入 `MoodRecordDao/MoodTagDao/MoodRecordTagDao`，支持按 `user_id` 的记录增删改查与标签关联持久化。
- 社区内容审核与匿名身份映射已实现：新增 `ContentModerationService`、`AnonymousIdentityService`，帖子展示已包含匿名名且发布内容会做审核清洗。
- AI 分析通道与降级链路已形成：新增 `AiAnalysisService`，分析流程先尝试 AI 总结，失败自动回退规则分析文案。
- 建议数据已按用户隔离：`SuggestionRepositoryImpl` 由全局单例建议改为按 `user_id` 存取。

仍待外部平台/后续迭代（非本地单机后端可完全闭环项）：

- 社区云端数据未接入（Firestore/远程 API 仍未接入）。
- 评论/回复、举报、复杂审核等社区增强功能未实现。
- 多设备同步与远程数据一致性策略未实现。

### 9.3 测试与质量缺口

- UI 自动化测试未开始（目前以领域/仓储单测为主）。
- Room DAO 与数据库集成测试不足（仅核心逻辑覆盖，未覆盖真实本地数据库读写链路）。
- 登录、导航守卫、异常链路（网络失败/鉴权失败）测试缺口较大。
