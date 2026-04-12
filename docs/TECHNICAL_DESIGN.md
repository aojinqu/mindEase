# MindEase 技术设计文档

## 1. 文档概述

### 1.1 项目目标

MindEase 是一款面向大学生的 AI 辅助情绪日记与情绪支持 Android 应用。项目目标是在 Android Studio + Java 技术栈下，完成一个低门槛、低压力、可持续使用的情绪记录与支持系统，覆盖“记录 - 分析 - 建议 - 社区”主流程。

### 1.2 技术目标

- 使用 Android Studio + Java 作为主要开发环境
- 采用 XML + Material Design 构建界面
- 支持本地优先的数据存储与离线使用
- 采用可扩展架构，支持后续接入 Firebase 或第三方 AI API
- 支持新增心理疗愈 Agent，通过外部中转站接入大模型 API
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
- Service：处理规则分析、建议生成、触发因子提取、匿名身份映射、Agent Prompt 构建等逻辑
- Domain Model：抽象业务实体，如 MoodRecord、Suggestion、CommunityPost

#### 数据层

负责本地数据库、远程接口、缓存与仓库逻辑。

- Repository：向业务层提供统一数据访问接口
- Local Data Source：基于 Room/SQLite 管理本地数据
- Remote Data Source：对接 Firebase Auth、Firestore、Retrofit API、外部中转站大模型 API
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
7. 心理疗愈 Agent 模块
8. 匿名社区模块
9. 个人中心与设置模块
10. 公共基础模块

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

#### 7）心理疗愈 Agent 模块

- Agent 对话页与消息流管理
- 会话历史读取与展示
- 聚合最近情绪记录、趋势、标签、建议等上下文
- 基于 Prompt 模板构建外部模型请求
- 调用外部中转站大模型 API
- 对高风险表达执行安全提醒与资源引导
- 远程失败时回退到本地规则安抚回复

#### 8）匿名社区模块

- 匿名发帖
- 社区列表浏览
- 标签筛选
- 点赞/支持互动
- 帖子详情查看
- 评论/回复
- 删除自己发布的帖子与评论
- 基础敏感词审核

#### 9）个人中心与设置模块

- 昵称/头像设置
- 隐私设置
- 提醒开关
- 数据同步开关
- 关于与免责声明

#### 10）公共基础模块

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
6. AgentChatActivity
7. PostEditorActivity
8. PostDetailActivity
9. SettingsActivity

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

#### AgentChatActivity

- 展示心理疗愈 Agent 对话消息流
- 接收用户输入与快捷提问
- 显示“已结合近期记录”的上下文状态
- 支持多轮对话、加载中状态、失败重试
- 展示温和免责声明与求助资源入口

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
- 评论列表、回复输入与删除入口
- 仅对作者显示帖子删除按钮

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
- 本地 Room：Agent 会话缓存、Prompt 上下文摘要缓存
- Firebase Auth：登录认证
- Firestore：社区帖子、评论、远程用户资料
- 外部中转站 API：心理疗愈 Agent 对话生成

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

#### 8）agent_sessions

用于保存 Agent 会话元信息。

| 字段名 | 类型 | 说明 |
|---|---|---|
| session_id | TEXT PK | 会话 ID |
| user_id | TEXT | 用户 ID |
| title | TEXT | 会话标题 |
| latest_context_summary | TEXT | 最近一次上下文摘要 |
| model_name | TEXT | 调用模型名 |
| created_at | INTEGER | 创建时间戳 |
| updated_at | INTEGER | 更新时间戳 |

#### 9）agent_messages

用于保存 Agent 对话消息。

| 字段名 | 类型 | 说明 |
|---|---|---|
| message_id | TEXT PK | 消息 ID |
| session_id | TEXT | 所属会话 ID |
| role | TEXT | user / assistant / system |
| message_text | TEXT | 消息正文 |
| prompt_context_json | TEXT | 本轮注入的上下文摘要 |
| status | TEXT | pending / success / failed / fallback |
| created_at | INTEGER | 创建时间戳 |

### 6.3 云端数据结构设计

#### 1）community_posts

| 字段名 | 类型 | 说明 |
|---|---|---|
| post_id | TEXT PK | 帖子 ID |
| anonymous_user_id | TEXT | 匿名身份 ID |
| author_user_id | TEXT | 实际作者用户 ID（仅权限判断使用，不对外展示） |
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
| parent_comment_id | TEXT | 父评论 ID，顶层评论可为空 |
| author_user_id | TEXT | 实际作者用户 ID（仅权限判断使用） |
| content | TEXT | 评论内容 |
| like_count | INTEGER | 评论点赞数 |
| created_at | TIMESTAMP | 创建时间 |

### 6.4 实体关系

```text
User 1 ---- N MoodRecord
MoodRecord N ---- N MoodTag
User 1 ---- N Suggestion
User 1 ---- N AnalysisSnapshot
User 1 ---- N AgentSession
AgentSession 1 ---- N AgentMessage
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
- `AgentSession`
- `AgentMessage`
- `CommunityPost`
- `PostComment`

### 7.2 Room Entity 类

- `UserEntity`
- `MoodRecordEntity`
- `MoodTagEntity`
- `MoodRecordTagCrossRef`
- `SuggestionEntity`
- `AnalysisSnapshotEntity`
- `AgentSessionEntity`
- `AgentMessageEntity`

### 7.3 DAO 接口

- `UserDao`
- `MoodRecordDao`
- `MoodTagDao`
- `SuggestionDao`
- `AnalysisSnapshotDao`
- `AgentSessionDao`
- `AgentMessageDao`

### 7.4 Repository 接口

- `AuthRepository`
- `MoodRepository`
- `AnalysisRepository`
- `SuggestionRepository`
- `AgentRepository`
- `CommunityRepository`
- `UserRepository`

### 7.5 Repository 实现类

- `AuthRepositoryImpl`
- `MoodRepositoryImpl`
- `AnalysisRepositoryImpl`
- `SuggestionRepositoryImpl`
- `AgentRepositoryImpl`
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
- `StartAgentSessionUseCase`
- `SendAgentMessageUseCase`
- `GetAgentSessionHistoryUseCase`
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

#### `PromptContextBuilder`

- 从最近记录、趋势统计、标签、建议、会话历史中提取本轮 Prompt 上下文
- 控制注入字段数量，遵循最小必要原则

#### `TherapyAgentService`

- 负责构建 system prompt / user prompt
- 负责调用外部中转站大模型 API
- 统一处理超时、失败重试与 fallback 回复

#### `RiskGuardService`

- 检测高风险表达
- 在必要时追加求助资源与安全提示

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
- `AgentChatViewModel`
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
9. 心理疗愈 Agent 基础对话能力
10. 匿名社区发帖与浏览

### 8.2 MVP 中建议弱化或延期的部分

- AI 安慰重写
- 共鸣匹配
- 情绪骤降通知
- 写给未来的信
- 复杂内容审核
- 多设备实时同步
- Agent 的流式输出、长期记忆与多模型路由

### 8.3 MVP 验收口径

- 用户能在 3 步内完成一次情绪记录
- 记录内容可持久保存并可编辑删除
- 分析页可展示至少 7 天和 30 天结果
- 日历页能按日期查看情绪详情
- 建议页能根据最近记录输出一条有效建议
- Agent 对话页能基于用户最新数据生成至少一轮疗愈回复
- 外部中转站 API 不可用时可自动回退到本地安抚文案
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
│  ├─ agent
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

以 `agent` 模块为例：

```text
feature/agent
├─ AgentChatActivity.java
├─ AgentChatViewModel.java
├─ adapter
├─ model
└─ widget
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

用户进入帖子详情页
-> 加载帖子、评论与点赞状态
-> 可发表评论或回复评论
-> 仅作者可删除自己的帖子
-> 仅评论作者且无子回复时可删除评论
-> 删除成功后刷新详情或返回社区列表
```

### 10.4 心理疗愈 Agent 对话流程

```text
用户进入 AgentChatActivity
-> 输入消息
-> SendAgentMessageUseCase
-> AgentRepository 读取最近情绪记录/趋势/建议/会话历史
-> PromptContextBuilder 生成上下文摘要
-> RiskGuardService 扫描高风险表达
-> TherapyAgentService 组装 Prompt
-> Remote Agent API 通过外部中转站调用大模型
-> 成功：保存 assistant 回复并刷新消息流
-> 失败：返回 fallback 安抚文案并标记为 fallback
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
- Agent 请求只上传最小必要上下文，不直接暴露完整原始历史
- 外部中转站 API key 仅保存在安全配置层，不写死在客户端源码中

### 11.3 可靠性

- 网络失败不影响本地情绪记录
- AI 接口失败时自动切回规则分析
- 社区数据获取失败时展示空态和重试按钮
- Agent API 超时或失败时，消息流应回退到本地安抚文案并可重试

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
- 增加心理疗愈 Agent，并对接外部中转站大模型 API
- 增加共鸣匹配与微型干预卡片

### 12.3 第三阶段

- 增加提醒通知
- 增加举报、复杂审核与社区治理能力
- 增加心理资源入口与紧急支持信息

---

## 13. 总结

MindEase 的技术设计应围绕“本地优先、结构清晰、规则可落地、AI 可扩展”展开。对课程项目而言，最合理的实现路径是先用 Java + XML + Room + Firebase 构建稳定的 MVP，再通过规则分析保证核心体验，最后根据时间接入更强的 AI 能力。该方案既符合 PRD 的产品定位，也能在 Android Studio 环境下以较低风险完成开发与演示。

## 14. 测试模块设计（新增）

### 14.1 测试分层

- Domain 单元测试：验证规则分析、建议生成、用例编排
- Repository 单元测试：验证核心 CRUD 与时间窗口查询逻辑
- UI/集成测试（后续）：验证页面主流程联动

### 14.2 覆盖重点

- `MoodRepository`：创建/更新/删除/最近 N 天查询
- `RuleBasedSentimentAnalyzer`：positive/neutral/negative 判定
- `GenerateMoodAnalysisUseCase`：统计正确性与摘要文本
- `GenerateSuggestionUseCase`：负向聚集时触发减压建议
- `PromptContextBuilder`：上下文拼装正确性与字段裁剪
- `SendAgentMessageUseCase`：外部 API 成功/失败/fallback 分支

### 14.3 执行策略

- 日常开发执行 `./gradlew test`
- 提交前至少跑通核心单测
- 出现回归时补充对应失败用例后再修复
