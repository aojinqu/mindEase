# MindEase 开发看板（BOARD）

## 1. 当前状态

- 项目阶段：`v0.2-skeleton`（骨架已落地）
- 开发状态：`进行中`
- 更新时间：`2026-03-25`

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
- 已新增 Repository 实现占位类（后续补真实数据流）
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
- 情绪记录主流程：`未开始`

### 3.2 P1（MVP核心能力）

- 登录注册：`未开始`
- 分析页图表：`未开始`
- 建议页与首页摘要：`未开始`
- 日历页：`未开始`

### 3.3 P2（增强能力）

- 匿名社区发帖与浏览：`未开始`
- Firebase 接入：`未开始`
- UI细化与空状态：`未开始`

## 4. MVP覆盖检查（更新后）

- 注册与登录：`未完成`
- 创建/编辑/删除情绪记录：`未完成`
- 标签与文本输入：`未完成`
- 基础情绪分析：`未完成`
- 周/月趋势图：`未完成`
- 情绪日历：`未完成`
- 个性化建议：`未完成`
- 匿名发帖与浏览：`未完成`
- 模拟器可运行：`待本轮编译验证`

## 5. 风险与备注

- 当前为“可编译骨架优先”，业务逻辑仍为占位实现
- Firebase、图表和AI能力未接入
- 需要下一阶段尽快打通 `MoodRecord` 的真实本地增删改查链路

## 6. 下一步（建议迭代顺序）

1. 完成 `MoodEditor` 页面与 `MoodRecord` 本地 CRUD
2. 在 Home/Analysis/Calendar 三页接入真实数据读取
3. 接入规则分析与建议生成的真实调用链
4. 最后接入 Auth + Community（Firebase 或本地模拟）

## 7. 开发日志

### 2026-03-25

- 读取 PRD、技术设计、原看板
- 生成并落地 Android 分层代码骨架
- 搭建启动页、主导航与五大主 Fragment
- 建立 Room Entity/DAO/Database 骨架
- 更新 BOARD 状态为 `v0.2-skeleton`
