# MindEase 技术设计文档

## 1. 文档概述

### 1.1 项目目标

MindEase 是一款面向大学生的 AI 辅助情绪日记与情绪支持 Android 应用。项目目标是在 Android Studio + Java 技术栈下，完成一个低门槛、低压力、可持续使用的情绪记录与支持系统，覆盖“记录 - 分析 - 建议 - 社区”主流程。

### 1.2 技术目标

- 使用 Android Studio + Java 作为主要开发环境
- 采用 XML + Material Design 构建界面
- 支持本地优先的数据存储与离线使用
- 采用可扩展架构，支持后续接入 Firebase 或第三方 AI API
- 在 MVP 阶段优先保证核心流程闭环、稳定性和可演示性

### 1.3 设计原则

- 低耦合：界面、业务逻辑、数据层解耦
- 易扩展：先支持规则分析，再平滑升级为 AI 接口分析
- 本地优先：核心情绪记录、查询、统计在离线情况下仍可使用
- 隐私优先：情绪记录默认私密，社区身份匿名化
- MVP 友好：优先实现最小可运行产品，避免过度设计

---

## 2. 系统架构

### 2.1 总体架构

建议采用分层架构，整体可理解为：

- 表现层（Presentation Layer）
- 领域/业务层（Domain Layer）
- 数据层（Data Layer）
- 基础设施层（Infrastructure Layer）

### 2.2 架构说明

#### 表现层

负责页面展示、用户交互、导航控制与状态更新。

- Activity：承载主导航容器与少量独立页面
- Fragment：承载首页、记录、分析、日历、社区、个人中心等主要页面
- ViewModel：管理页面状态、调用 UseCase、向 UI 输出可观察数据
- Adapter / Dialog / CustomView：处理列表、弹窗和复用组件

#### 领域/业务层

负责核心业务规则与用例编排。

- UseCase：封装“创建情绪记录”“获取趋势分析”“生成建议”“匿名发帖”等动作
- Service：处理规则分析、建议生成、触发因子提取、匿名身份映射等逻辑
- Domain Model：抽象业务实体，如 MoodRecord、Suggestion、CommunityPost

#### 数据层

负责本地数据库、远程接口、缓存与仓库逻辑。

- Repository：向业务层提供统一数据访问接口
- Local Data Source：基于 Room/SQLite 管理本地数据
- Remote Data Source：对接 Firebase Auth、Firestore、Retrofit API
- Mapper：Entity、DTO、Domain Model 之间转换

#### 基础设施层

负责数据库、网络、安全、日志、配置等底层能力。

- Room Database / SQLiteOpenHelper
- Retrofit / OkHttp
- SharedPreferences / EncryptedSharedPreferences
- Firebase SDK
- 图表库 MPAndroidChart

### 2.3 推荐架构模式

推荐采用 `MVVM + Repository + UseCase`。

原因：

- 比 MVP 更适合中等复杂度页面状态管理
- 与 Room、LiveData、Firebase 集成自然
- 便于后续加入 AI 接口、缓存策略和更多分析逻辑

### 2.4 核心技术选型

- 语言：Java
- IDE：Android Studio
- UI：XML + Material Components
- 架构：MVVM + Repository + UseCase
- 本地数据库：Room
- 本地配置：SharedPreferences / EncryptedSharedPreferences
- 登录认证：Firebase Authentication
- 云端社区数据：Firestore
- 图表：MPAndroidChart
- 网络：Retrofit + OkHttp
- 图片：Glide

### 2.5 架构图（逻辑）

```text
UI(Activity/Fragment)
        |
        v
ViewModel
        |
        v
UseCase / Service
        |
        v
Repository
   |           |
   v           v
Local DB    Remote API/Firebase
```

---

## 3. App 模块划分

### 3.1 模块列表

建议按功能划分以下模块：

1. 启动与引导模块
2. 用户账户模块
3. 情绪记录模块
4. 情绪分析模块
5. 情绪日历模块
6. 个性化建议模块
7. 匿名社区模块
8. 个人中心与设置模块
9. 公共基础模块

### 3.2 各模块职责

#### 1）启动与引导模块

- Splash 启动页
- 首次安装引导页
- 登录状态检查
- 进入主页面路由分发

#### 2）用户账户模块

- 邮箱注册
- 邮箱登录
- 退出登录
- 个人资料读取与更新
- 匿名社区昵称映射

#### 3）情绪记录模块

- 情绪类型选择
- 情绪强度选择
- 标签选择
- 日记文本输入
- 记录的新增、编辑、删除、查询

#### 4）情绪分析模块

- 近 7 天 / 30 天趋势统计
- 正向/中性/负向分类统计
- 高频标签与压力源分析
- 周总结与月总结生成
- 规则分析与 AI 分析双通道

#### 5）情绪日历模块

- 月视图展示情绪打卡状态
- 某日记录详情查看
- 连续打卡统计

#### 6）个性化建议模块

- 基于最近 3-7 天记录生成建议
- 建议类型分类，如减压、休息、社交、呼吸练习
- 每日建议卡片展示
- AI 建议失败时降级为规则建议

#### 7）匿名社区模块

- 匿名发帖
- 社区列表浏览
- 标签筛选
- 点赞/支持互动
- 帖子详情查看
- 基础敏感词审核

#### 8）个人中心与设置模块

- 昵称/头像设置
- 隐私设置
- 提醒开关
- 数据同步开关
- 关于与免责声明

#### 9）公共基础模块

- 网络状态检测
- 时间与日期工具
- 常量管理
- 错误处理
- 通用组件

---

## 4. 页面结构

### 4.1 页面总览

建议页面结构如下：

1. SplashActivity
2. OnboardingActivity
3. AuthActivity
4. MainActivity
5. MoodEditorActivity（或 BottomSheetDialogFragment）
6. PostEditorActivity
7. PostDetailActivity
8. SettingsActivity

MainActivity 内部通过 Bottom Navigation 承载以下主页面 Fragment：

- HomeFragment
- AnalysisFragment
- CalendarFragment
- CommunityFragment
- ProfileFragment

### 4.2 页面职责说明

#### SplashActivity

- App 启动展示
- 检查是否首次使用
- 检查登录状态
- 跳转到引导页、登录页或主页

#### OnboardingActivity

- 说明产品价值
- 展示隐私与非医疗化声明
- 引导完成首次进入

#### AuthActivity

- 登录页与注册页切换
- 表单校验
- Firebase Authentication 交互

#### MainActivity

- 应用主容器
- 承载底部导航
- 管理 Fragment 切换

#### HomeFragment

- 今日问候
- 快速打卡入口
- 最近情绪摘要
- 今日建议卡片
- 进入分析、日历、社区的快捷入口

#### MoodEditorActivity

- 选择情绪
- 设置强度
- 选择标签
- 输入日记文本
- 保存或编辑已有记录

#### AnalysisFragment

- 周趋势图
- 月度统计图
- 标签频率统计
- 触发因子分析
- AI / 规则总结

#### CalendarFragment

- 月历视图
- 每日情绪颜色标记
- 点击日期查看详情

#### CommunityFragment

- 匿名帖子流
- 标签筛选
- 点赞/抱抱/支持一下
- 进入发帖页

#### PostDetailActivity

- 帖子完整内容
- 点赞数、评论数
- 评论区（可在 MVP 中预留）

#### ProfileFragment

- 用户资料
- 隐私选项
- 提醒与偏好设置
- 关于页入口

#### SettingsActivity

- 账户设置
- 数据与隐私设置
- 免责声明

---

## 5. 导航流程

### 5.1 首次使用导航流程

```text
SplashActivity
  -> OnboardingActivity
  -> AuthActivity
  -> MainActivity(HomeFragment)
  -> MoodEditorActivity
  -> 返回首页显示建议卡片
```

### 5.2 日常使用导航流程

```text
SplashActivity
  -> MainActivity(HomeFragment)
  -> MoodEditorActivity
  -> 保存记录
  -> HomeFragment / AnalysisFragment / CalendarFragment
```

### 5.3 社区使用导航流程

```text
MainActivity(CommunityFragment)
  -> PostDetailActivity
  -> 返回社区列表

MainActivity(CommunityFragment)
  -> PostEditorActivity
  -> 发布成功
  -> 返回社区列表并刷新
```

### 5.4 底部导航建议

建议底部导航包含 5 个一级入口：

- 首页
- 分析
- 日历
- 社区
- 我的

### 5.5 导航设计原则

- 核心情绪记录入口保持高可见性
- 主流程不超过 3-4 步
- 独立编辑页采用 Activity 或 BottomSheet，提高专注感
- 社区与个人情绪记录分离，避免信息混淆

---

## 6. 数据库设计

### 6.1 数据存储策略

推荐采用“本地 + 云端”混合方案：

- 本地 Room：情绪记录、建议缓存、分析缓存、标签、设置
- Firebase Auth：登录认证
- Firestore：社区帖子、评论、远程用户资料

如果课程实现需简化，也可采用：

- 本地 Room 完成全部情绪记录与统计
- 社区数据先用本地假数据或本地表模拟

### 6.2 本地数据库表设计

#### 1）users

用于缓存当前用户基础信息。

| 字段名 | 类型 | 说明 |
|---|---|---|
| user_id | TEXT PK | 用户唯一 ID |
| email | TEXT | 邮箱 |
| nickname | TEXT | 昵称 |
| avatar_url | TEXT | 头像地址 |
| anonymous_name | TEXT | 匿名社区显示名 |
| created_at | INTEGER | 创建时间戳 |
| updated_at | INTEGER | 更新时间戳 |

#### 2）mood_records

| 字段名 | 类型 | 说明 |
|---|---|---|
| record_id | TEXT PK | 记录 ID |
| user_id | TEXT | 所属用户 |
| mood_type | TEXT | 情绪类型 |
| mood_intensity | INTEGER | 强度 1-5 |
| sentiment_score | REAL | 情绪分值 |
| sentiment_label | TEXT | 正向/中性/负向 |
| diary_text | TEXT | 日记内容 |
| created_at | INTEGER | 创建时间戳 |
| updated_at | INTEGER | 更新时间戳 |
| sync_status | INTEGER | 同步状态 |

#### 3）mood_tags

标签主表。

| 字段名 | 类型 | 说明 |
|---|---|---|
| tag_id | TEXT PK | 标签 ID |
| tag_name | TEXT | 标签名称 |
| tag_category | TEXT | 标签分类 |

#### 4）mood_record_tag_cross_ref

记录与标签多对多关联表。

| 字段名 | 类型 | 说明 |
|---|---|---|
| record_id | TEXT | 记录 ID |
| tag_id | TEXT | 标签 ID |

联合主键：`record_id + tag_id`

#### 5）suggestions

| 字段名 | 类型 | 说明 |
|---|---|---|
| suggestion_id | TEXT PK | 建议 ID |
| user_id | TEXT | 用户 ID |
| source_type | TEXT | rule / ai |
| suggestion_type | TEXT | 减压/休息/运动等 |
| suggestion_text | TEXT | 建议内容 |
| related_record_id | TEXT | 关联记录，可为空 |
| generated_at | INTEGER | 生成时间 |

#### 6）analysis_snapshots

用于缓存分析结果，减少重复计算。

| 字段名 | 类型 | 说明 |
|---|---|---|
| snapshot_id | TEXT PK | 快照 ID |
| user_id | TEXT | 用户 ID |
| period_type | TEXT | week / month |
| summary_text | TEXT | 总结文本 |
| positive_count | INTEGER | 正向次数 |
| neutral_count | INTEGER | 中性次数 |
| negative_count | INTEGER | 负向次数 |
| generated_at | INTEGER | 生成时间 |

#### 7）app_settings

| 字段名 | 类型 | 说明 |
|---|---|---|
| setting_key | TEXT PK | 配置键 |
| setting_value | TEXT | 配置值 |

### 6.3 云端数据结构设计

#### 1）community_posts

| 字段名 | 类型 | 说明 |
|---|---|---|
| post_id | TEXT PK | 帖子 ID |
| anonymous_user_id | TEXT | 匿名身份 ID |
| anonymous_name | TEXT | 匿名显示名 |
| content | TEXT | 帖子正文 |
| emotion_tag | TEXT | 主要情绪标签 |
| support_count | INTEGER | 支持数 |
| like_count | INTEGER | 点赞数 |
| comment_count | INTEGER | 评论数 |
| status | TEXT | normal / hidden / flagged |
| created_at | TIMESTAMP | 创建时间 |

#### 2）post_comments

| 字段名 | 类型 | 说明 |
|---|---|---|
| comment_id | TEXT PK | 评论 ID |
| post_id | TEXT | 所属帖子 |
| anonymous_user_id | TEXT | 匿名用户 ID |
| content | TEXT | 评论内容 |
| created_at | TIMESTAMP | 创建时间 |

### 6.4 实体关系

```text
User 1 ---- N MoodRecord
MoodRecord N ---- N MoodTag
User 1 ---- N Suggestion
User 1 ---- N AnalysisSnapshot
AnonymousUser 1 ---- N CommunityPost
CommunityPost 1 ---- N PostComment
```

---

## 7. 关键类设计

### 7.1 核心实体类

- `User`
- `MoodRecord`
- `MoodTag`
- `Suggestion`
- `AnalysisSnapshot`
- `CommunityPost`
- `PostComment`

### 7.2 Room Entity 类

- `UserEntity`
- `MoodRecordEntity`
- `MoodTagEntity`
- `MoodRecordTagCrossRef`
- `SuggestionEntity`
- `AnalysisSnapshotEntity`

### 7.3 DAO 接口

- `UserDao`
- `MoodRecordDao`
- `MoodTagDao`
- `SuggestionDao`
- `AnalysisSnapshotDao`

### 7.4 Repository 接口

- `AuthRepository`
- `MoodRepository`
- `AnalysisRepository`
- `SuggestionRepository`
- `CommunityRepository`
- `UserRepository`

### 7.5 Repository 实现类

- `AuthRepositoryImpl`
- `MoodRepositoryImpl`
- `AnalysisRepositoryImpl`
- `SuggestionRepositoryImpl`
- `CommunityRepositoryImpl`
- `UserRepositoryImpl`

### 7.6 UseCase 类

- `LoginUseCase`
- `RegisterUseCase`
- `GetCurrentUserUseCase`
- `CreateMoodRecordUseCase`
- `UpdateMoodRecordUseCase`
- `DeleteMoodRecordUseCase`
- `GetRecentMoodRecordsUseCase`
- `GenerateMoodAnalysisUseCase`
- `GenerateSuggestionUseCase`
- `GetCalendarMoodUseCase`
- `CreateCommunityPostUseCase`
- `GetCommunityFeedUseCase`
- `SupportPostUseCase`

### 7.7 Service 类

#### `RuleBasedSentimentAnalyzer`

- 基于关键词词典与强度规则生成 `sentiment_score`
- 在没有远程 AI 能力时作为默认实现

#### `AiAnalysisService`

- 负责调用第三方 API 生成更自然的情绪总结
- 支持失败回退到规则分析

#### `SuggestionEngine`

- 根据最近 3-7 天情绪记录生成简短建议
- 控制输出风格为非医疗化、可执行

#### `TriggerFactorService`

- 根据标签出现频率和负向记录关联程度提取触发因子

#### `ContentModerationService`

- 对社区发帖做敏感词和攻击性词汇检查

#### `AnonymousIdentityService`

- 生成匿名用户名
- 将真实身份与社区身份隔离

### 7.8 ViewModel 类

- `SplashViewModel`
- `AuthViewModel`
- `HomeViewModel`
- `MoodEditorViewModel`
- `AnalysisViewModel`
- `CalendarViewModel`
- `CommunityViewModel`
- `PostDetailViewModel`
- `ProfileViewModel`
- `SettingsViewModel`

### 7.9 UI 支撑类

- `MoodRecordAdapter`
- `CommunityPostAdapter`
- `TagChipAdapter`
- `CalendarDayDecorator`
- `ChartDataMapper`
- `UiState`
- `Event`

---

## 8. MVP 范围定义

### 8.1 MVP 必做范围

本项目第一阶段建议只交付以下内容：

1. 邮箱注册与登录
2. 首页与底部导航框架
3. 情绪记录的新增、编辑、删除、查看
4. 标签选择与文字记录
5. 基于规则的基础情绪分析
6. 周度和月度图表展示
7. 情绪日历展示
8. 个性化建议生成
9. 匿名社区发帖与浏览

### 8.2 MVP 中建议弱化或延期的部分

- 评论/回复
- AI 安慰重写
- 共鸣匹配
- 情绪骤降通知
- 写给未来的信
- 复杂内容审核
- 多设备实时同步

### 8.3 MVP 验收口径

- 用户能在 3 步内完成一次情绪记录
- 记录内容可持久保存并可编辑删除
- 分析页可展示至少 7 天和 30 天结果
- 日历页能按日期查看情绪详情
- 建议页能根据最近记录输出一条有效建议
- 社区页可匿名发布与浏览帖子
- AI 接口不可用时，规则分析仍可工作

---

## 9. 推荐包结构

建议按分层 + 功能结合的方式组织，避免全部按 Activity/Fragment/Utils 平铺。

```text
com.mindease
├─ app
│  ├─ MindEaseApp.java
│  ├─ di
│  └─ navigation
├─ common
│  ├─ base
│  ├─ constants
│  ├─ utils
│  ├─ result
│  └─ widget
├─ data
│  ├─ local
│  │  ├─ db
│  │  ├─ dao
│  │  ├─ entity
│  │  └─ preference
│  ├─ remote
│  │  ├─ api
│  │  ├─ dto
│  │  ├─ firebase
│  │  └─ service
│  ├─ mapper
│  └─ repository
├─ domain
│  ├─ model
│  ├─ repository
│  ├─ usecase
│  └─ service
├─ feature
│  ├─ splash
│  ├─ onboarding
│  ├─ auth
│  ├─ home
│  ├─ mood
│  ├─ analysis
│  ├─ calendar
│  ├─ suggestion
│  ├─ community
│  ├─ profile
│  └─ settings
└─ workers
```

### 9.1 feature 层建议结构

以 `mood` 模块为例：

```text
feature/mood
├─ MoodEditorActivity.java
├─ MoodEditorViewModel.java
├─ adapter
├─ model
└─ dialog
```

以 `community` 模块为例：

```text
feature/community
├─ CommunityFragment.java
├─ PostDetailActivity.java
├─ PostEditorActivity.java
├─ CommunityViewModel.java
├─ PostDetailViewModel.java
└─ adapter
```

### 9.2 包结构设计原则

- `feature` 管页面与交互
- `domain` 管业务规则
- `data` 管数据来源
- `common` 放公共能力
- 后续如项目扩大，可将 `feature` 再拆成独立 Gradle module

---

## 10. 关键流程设计

### 10.1 情绪记录流程

```text
用户进入记录页
-> 选择情绪类型
-> 设置强度
-> 选择标签并输入文本
-> 点击保存
-> CreateMoodRecordUseCase
-> RuleBasedSentimentAnalyzer 分析
-> MoodRepository 保存到 Room
-> SuggestionEngine 生成建议
-> 首页/分析页刷新
```

### 10.2 情绪分析流程

```text
AnalysisFragment 请求近 7/30 天数据
-> GetRecentMoodRecordsUseCase
-> AnalysisRepository 聚合统计
-> TriggerFactorService 提取高频标签
-> AiAnalysisService 或规则总结生成文案
-> ChartDataMapper 转换为图表数据
-> UI 展示折线图、统计图和总结
```

### 10.3 社区发帖流程

```text
用户进入发帖页
-> 输入内容与情绪标签
-> ContentModerationService 检查
-> AnonymousIdentityService 生成匿名身份
-> CreateCommunityPostUseCase
-> CommunityRepository 上传 Firestore
-> 社区列表刷新
```

---

## 11. 非功能设计

### 11.1 性能

- 首页、分析页优先展示缓存数据
- 大量统计计算放入后台线程执行
- 图表渲染只处理最近 7 天和 30 天必要数据

### 11.2 安全与隐私

- 登录态 token 安全保存
- 情绪日记默认仅用户本人可见
- 社区模块不暴露真实邮箱、昵称等信息
- 在设置页和首次引导页明确声明“非医疗诊断工具”

### 11.3 可靠性

- 网络失败不影响本地情绪记录
- AI 接口失败时自动切回规则分析
- 社区数据获取失败时展示空态和重试按钮

### 11.4 可维护性

- Repository 统一数据访问
- UseCase 统一业务入口
- ViewModel 不直接依赖数据库实现细节

---

## 12. 迭代建议

### 12.1 第一阶段

- 完成本地记录、分析、建议与基础社区

### 12.2 第二阶段

- 接入 AI API
- 增加 AI 安慰重写
- 增加共鸣匹配与微型干预卡片

### 12.3 第三阶段

- 增加提醒通知
- 增加评论/举报
- 增加心理资源入口与紧急支持信息

---

## 13. 总结

MindEase 的技术设计应围绕“本地优先、结构清晰、规则可落地、AI 可扩展”展开。对课程项目而言，最合理的实现路径是先用 Java + XML + Room + Firebase 构建稳定的 MVP，再通过规则分析保证核心体验，最后根据时间接入更强的 AI 能力。该方案既符合 PRD 的产品定位，也能在 Android Studio 环境下以较低风险完成开发与演示。
