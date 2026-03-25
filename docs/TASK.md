# Theme: Developing a smart phone application In Android Studio


## 20. AI 生成 UI 的使用建议

如果你打算用 AI 生成 UI，可以把它作为“设计辅助流程”写进项目方法，而不是写成核心功能。也就是说：

- AI 用于帮助团队快速产出页面草图、界面风格和组件布局
- 最终界面仍由开发团队在 Android Studio 中落地实现

这样写会更专业，也更符合课程项目逻辑。

### 20.1 在 PRD 中如何体现

你可以在“设计与实现说明”或“开发方法”中加入下面这段：

**AI 辅助界面设计说明**  
本项目将在 UI/UX 设计阶段使用 AI 设计工具辅助生成页面草图、界面风格探索和组件布局建议，以提高原型设计效率。生成结果将由开发团队进行筛选、修改和本地实现，确保最终界面符合产品定位、用户体验需求及 Android 平台设计规范。

如果你想写得更正式一点，也可以补一句：

- AI 生成内容仅作为设计参考，不直接替代最终产品设计决策。

---

## 20.2 推荐的 UI 生成 Prompt 写法

如果你要让 AI 帮你生成界面图，提示词最好包含以下信息：

- App 类型
- 目标用户
- 页面名称
- 核心功能
- 视觉风格
- 色彩倾向
- 平台限制
- 组件要求

### 通用 Prompt 模板

```text
Design a mobile app UI for an Android app called MindEase, an AI-assisted mood diary and emotional support app for university students.

Target users: university students under stress from study, deadlines, and social life.

Please design a clean, calm, friendly, and modern interface.

Style requirements:
- Soft and emotionally safe visual tone
- Minimal and uncluttered layout
- Warm pastel colors such as light blue, mint green, beige, and soft peach
- Rounded cards and accessible typography
- Suitable for Android mobile screens

Main page purpose:
- Quick mood check-in
- Show today’s emotional summary
- Display one short self-help suggestion
- Provide access to mood calendar and anonymous community

Please generate:
- Home screen UI
- Mood check-in screen
- Mood analysis screen with charts
- Anonymous community feed screen

Keep the design practical for Android Studio implementation using Java and XML layouts.
```

---

## 20.3 针对不同页面的 Prompt 示例

### 首页 Prompt

```text
Design the home screen of an Android app called MindEase, a mood diary and emotional support app for university students.

The home screen should include:
- greeting section
- quick mood check-in button
- today’s mood summary card
- one AI suggestion card
- shortcut buttons to mood calendar, trend analysis, and anonymous community

Visual style:
- calm, warm, emotionally safe
- pastel tones
- modern Android app layout
- easy to implement with XML layouts
```

### 情绪记录页 Prompt

```text
Design a mood check-in screen for an Android app called MindEase.

The screen should include:
- emoji-based mood selector
- mood intensity slider
- tag selection chips
- text input for diary notes
- save button

Design goals:
- simple, fast, low-pressure interaction
- suitable for university students
- clean and soft interface
- feasible to build in Android Studio with Java
```

### 分析页 Prompt

```text
Design an emotion analysis screen for an Android app called MindEase.

The screen should include:
- weekly mood line chart
- monthly mood summary card
- emotional trigger tag statistics
- AI-generated short summary section

Style:
- data visualization should be simple and readable
- calm and supportive tone
- Android-friendly component structure
```

### 树洞社区页 Prompt

```text
Design an anonymous community feed screen for an Android app called MindEase.

The screen should include:
- anonymous post cards
- emotion tags on each post
- support/like button
- filter bar by emotion category
- floating action button for creating a new post

Style:
- emotionally safe
- supportive and friendly
- not noisy or overly social-media-like
- practical for Android implementation
```

---

## 20.4 你在生成 UI 时要特别加上的限制

为了让 AI 产出的界面更容易落地，建议在 prompt 里加这些限制：

- 使用常见 Android 组件布局
- 避免过度复杂的玻璃拟态和超多渐变
- 不要设计太多悬浮层和复杂动画
- 保持页面层级清晰
- 每个页面最多 1 到 2 个视觉重点
- 适合 Java + XML 实现

你可以在 prompt 最后加一句：

```text
Do not create overly futuristic or unrealistic UI. Keep it practical for implementation in Android Studio using Java and XML.
```

---

## 20.5 更适合写进项目文档的表述

如果你后面要在报告里提到“我们用了 AI 帮助生成 UI”，可以直接写：

**AI-assisted UI/UX design**  
AI tools were used during the early design phase to explore layout options, color styles, and page structures for the app. The generated designs were then refined manually and adapted into Android-compatible layouts for implementation in Android Studio.

对应中文版本：

**AI 辅助 UI/UX 设计**  
本项目在前期界面设计阶段使用 AI 工具探索页面布局、色彩风格与模块结构，以提升原型设计效率。生成结果经过团队人工筛选与修改后，再转换为适合 Android Studio 实现的界面方案。

## Task:
1) Do a background research. List at least 3 similar applications in the market, 
summarize their features and point out their shortcomings / possible 
improvements. 
2) Design a smart phone application based on your findings in 1). For example, if 
your background research is about smart phone games, you should design a new 
smart phone game. 
3) Develop your designed application using one of the following IDEs: i) Android 
Studio for Android applications; ii) Xcode for iOS applications; or iii) cross
platform tools like React Native, Flutter, Unity and Unreal for cross-platform 
applications. You may also use PHP, Python or others for the server program 
(when necessary). Hybrid application and mini program are also acceptable.

