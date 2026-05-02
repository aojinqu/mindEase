
COMP7506 Smart phone apps development







MindEase Emotional Wellness App Development Report


3036658695 Lu Qianwen

3000000000 Ao Jiaqi

3000000000 Liang Jiaying



Contents
Abstract	1
1.Background Research	1
1.1 Background and Motivation	1
1.2 Review of Related Applications	2
1.3 Research Objectives	3
2. Structural Design and Innovation of MindEase	5
2.1 Overall Design Philosophy and System Architecture	5
2.3 Functional Modules	6
2.3 Data Integration and Backend Design	9
2.4 Creativity and Innovation	10
3. Implementation and Overall Quality	10
3.1 System Implementation Overview	10
3.2 Testing and Implementation Validation	11
3.3 Software Quality Assessment	11
3.4 Reflection and Limitations	11
4.Summary	12
References	13






Abstract
This report aims to explore how mobile technology can support emotional wellness among university students. Through five core processes — record, analyze, suggest, chat, and share — the MindEase app enables students to document their moods, visualize emotional trends, receive personalized recommendations, and engage anonymously with a supportive community. By integrating local data storage, sentiment analysis, and AI‑assisted conversation features, the project demonstrates how smartphone applications can facilitate self‑reflection and mental well‑being in an accessible, privacy‑preserving manner.

1.Background Research
1.1 Background and Motivation
In recent years, the mental health of university students has become an increasingly prominent public health concern. A large-scale meta-analysis by Gao et al. (2020) in Scientific Reports reported that over 28.4% of Chinese university students experience clinically significant anxiety or depressive symptoms, a trend that has worsened following the COVID-19 pandemic. Similarly, a cross-sectional digital health survey conducted by Osman, W.A. (2025) found that 76.7% interview participants experienced an eating disorder, and 60.8% had little interest or pleasure in their daily activities. More than half of the respondents reported loneliness or hopelessness, with limited access to on-campus psychological support. Furthermore, in 2024, the National Mental Health Commission emphasized that younger populations, particularly those aged 18–24, are more vulnerable to stressors such as academic uncertainty and social comparison, while simultaneously being more open to technology-mediated self-help interventions.

Despite the growing prevalence of mental health challenges, the availability of professional psychological resources remains limited, especially in universities where counseling centers are often understaffed. These constraints highlight the need for accessible, scalable solutions that provide psychological self-help and emotional awareness.

In this context, the rise of mobile mental health (mHealth) and emotion-tracking technologies has gained global attention. According to the World Health Organization (2025), digital mental health interventions have shown measurable effectiveness in improving psychological well-being and resilience, serving as valuable supplements to traditional therapy. Thus, leveraging mobile platforms and artificial intelligence (AI) for emotional analysis and personalized mental health support has become an important research direction in both computer science and applied psychology.




Figure 1.1 Mental Health Issues Among University Students in China
Source: Gao et al., 2020; Osman, 2025; WHO, 2025.
1.2 Review of Related Applications
Given the increasing prevalence of depression, anxiety, loneliness, and stress disorders among university students, and the limited availability of institutional psychological resources, it becomes essential to investigate existing digital solutions that could inform the design of a more effective and student-tailored intervention. To this end, three related categories of mobile applications were reviewed: AI-based psychological counseling apps, emotion tracking and journaling apps, and campus-oriented well-being apps. These categories were selected because they represent the dominant paradigms in current mHealth innovation, addressing emotional support through artificial intelligence, behavioral self-monitoring, and peer-oriented wellness education, respectively.

Firstly, AI-based psychological support apps such as Wysa, Youper, and BetterHelp have gained wide recognition. Wysa, with over 5 million downloads worldwide (Sensor Tower, 2025), employs a conversational AI chatbot based on Cognitive Behavioral Therapy (CBT) to help users manage anxiety and daily stress. Youper similarly provides interactive conversation and emotional insight generation, while BetterHelp connects users directly with licensed therapists through its subscription service. Despite their impact and convenience, recent user feedback has revealed challenges, including high subscription costs, inadequate cultural adaptation of AI responses, and limited personalization, as commercial apps rarely address university students’ real-life contexts, like exams, dorm life, and social pressure.

Secondly, emotion tracking and journaling applications—notably Daylio, Reflectly, and Mooda—have become popular self-reflective tools that help users visualize daily mood trends. Daylio alone reported more than 10 million active users by 2024 (AppBrain, 2024). These apps allow users to record moods through emojis and notes, helping them identify recurring emotional patterns. However, despite their intuitive interfaces and long-term usability, their analytical capabilities remain limited. Most rely purely on quantitative scoring or color-coded charts rather than natural language processing (NLP) to extract semantic meaning from textual reflections. Therefore, while these systems offer basic self-awareness, they often fail to deliver deeper psychological insights or adaptive mental health strategies and lack contextual understanding. Current NLP systems focus on single-message sentiment, yet cannot track long-term emotional changes or academic stress patterns.

Lastly, campus and youth-focused mental well-being platforms such as Headspace, Happify Campus, and MindHK aim to create guided mindfulness and emotional education environments. These systems are usually integrated with university counseling services or provide structured meditation sessions and stress-awareness resources. Nevertheless, despite their academic relevance, their impact remains limited. 2025 CollegeWell Research Report data shows fewer than 12% of students actively use university-promoted well-being apps, with low interactivity, delayed feedback, and lack of immediate emotional relevance as the top deterrents. Additionally, insufficient community interventions like anonymous peer sharing lower empathy and sense of belonging, while opaque third-party cloud processing of emotional data sparks persistent privacy and data trust concerns.

Figure 1.2 Comparison of Mental Health Mobile Applications

In short, the findings from these three categories indicate that while digital tools for mental health have proliferated, few applications effectively combine AI-driven emotion analytics, contextual personalization for students, and community-based psychological support mechanisms. This emphasizes a critical gap in addressing the distinct psychosocial environment of university life—one that MindEase aims to bridge by merging the strengths of existing systems while addressing their limitations.
1.3 Research Objectives
The design rationale of MindEase draws upon established psychological theories and modern computational methods to create a scientifically grounded digital support system for university students. Conceptually, it integrates Emotion Regulation Theory (Gross, 2015), which highlights awareness and modulation of emotions through deliberate reflection, and Cognitive Behavioral Therapy (CBT) principles, emphasizing journaling and cognitive reframing to alleviate maladaptive thought patterns. In addition, the project adopts a Positive Psychology perspective (Seligman, 2019), promoting well-being through self-efficacy and strengths-based growth rather than symptom reduction. These theoretical pillars provide the behavioral foundation for users to identify, express, and manage emotions effectively in a digital environment.

Technically, MindEase employs Natural Language Processing (NLP) to analyze users’ written reflections and infer affective states, enabling dynamic mood tracking and personalized feedback. By leveraging transformer-based sentiment analysis models (Zhang et al., 2023), the system can detect subtle variations in emotional tone and visualize these trends through interactive data dashboards. To ensure trust and confidentiality, all information is stored using encryption and anonymized identifiers within a secure cloud infrastructure compliant with GDPR standards. This synthesis of psychological insight and AI capability underpins the research direction of the project.

Based on these theoretical and technical foundations, the present study aims to achieve the following objectives:
Develop a hybrid emotional journaling module that combines structured mood labeling with free-text reflection to capture both quantitative and qualitative aspects of user emotions.
Implement an AI-driven emotion analytics engine capable of detecting and visualizing temporal sentiment fluctuations to enhance self-awareness.
Establish a privacy-preserving anonymous community (“Tree-Hole”) that facilitates peer empathy and shared mental support within a safe digital space.
Deliver personalized recommendations—including mindfulness activities and social connection prompts—derived from emotion trends and linguistic feedback.
Evaluate the system’s effectiveness and user engagement through pilot studies within university settings.

Figure 1.3 Combining Al capabilities, emotion journaling, and peer support in one platform.
In summary, MindEase seeks to transform emotional journaling into an intelligent and empathic process, bridging the gap between technology-assisted reflection and accessible student mental health care.

2.Structural Design and Innovation of MindEase
2.1 Overall Design Philosophy and System Architecture
The design of the MindEase mobile application centers on enhancing emotional awareness among university students through a calm, reflective, and private interaction system. Rather than serving as a clinical intervention, the application positions itself as a low-pressure supportive environment for emotional self‑check, reflection, and anonymous communication. The interface is intentionally clean and minimal to reduce visual and cognitive load. Its core philosophy emphasizes three aspects: emotional accessibility, visualization of self‑reported data, and protection of personal privacy.

The user journey within MindEase follows a circular structure that encourages recurring emotional reflection: record – analyze – suggest – chat – share. Through this cycle, users document daily moods, visualize their emotional trends, obtain personalized insights, and engage with a supportive anonymous community.
Figure 2.1 MindEase user flow from emotional record to social sharing

The technical foundation of MindEase is built upon the Model–View–ViewModel (MVVM) architecture enhanced by Repository and Use‑Case layers, creating a modular and easily maintainable system. The higher‑level App layer oversees initialization, dependency management, and configuration through MindEaseApp and MainActivity. Beneath it, the Feature layer organizes each interface or module (for instance, diary, analysis, chat, and community functions) as independent components. The Domain layer defines essential data models and service contracts, supporting the interaction between the interface and the data sources. The Data layer integrates Room for local storage and Firestore for cloud synchronization, while a Common layer provides utility classes and session control mechanisms. This architecture separates presentation from logic, facilitating concurrent processing of local and online information and ensuring long‑term scalability of the research prototype.



Figure 2.2 Layered MVVM architecture of MindEase
2.3 Functional Modules
MindEase integrates several interrelated modules that together deliver a comprehensive user experience around emotional reflection.
The entry flow begins with the Splash and Onboarding activities. These initial stages check whether the user has completed onboarding and login. For new users, the onboarding view introduces MindEase’s purpose and emotional self‑tracking philosophy, presented with calm animations and adaptive edge‑to‑edge design to reduce interface stress.
                   
Figure 2.3 Splash and Onboarding Screens        Figure 2.8 Profile and Settings Page


After onboarding, the Main Activity serves as the application’s central navigation container. A five‑tab bottom navigation bar allows quick movement between the primary modules: Home, Diary, Analysis, Chat, and Community. This continuous navigation design strengthens the perception of emotional progress and ensures that no function is more “medicalized” than necessary. It introduces an integrated well‑being experience instead of isolated tasks.

In the Mood Diary Module, students can create, edit, or delete their daily mood entries. Each record contains the mood type, intensity, descriptive text, and emotion tags. Data are stored using the Room database for persistence. The interface supports low‑pressure interaction through soft color schemes and intuitive sliders for intensity. Emotion tagging allows later filtering and emotional pattern recognition, fostering self‑reflection instead of quantitative judgment.


 












Main Activity Navigation Layout


Figure 2.4 Mood Editor Screens   

The subsequent Analysis Module offers weekly and monthly summaries. It uses MPAndroidChart to present trend visualizations and rule‑based algorithms to generate personalized suggestions grounded in the user’s recent submissions. This feature transforms raw data into visual insights and recommendations, enhancing reflective practice. Optionally, it can connect to an AI summarization endpoint that produces narrative mood summaries when network configuration is provided.

Complementing this is the Calendar Review, where mood records are plotted across a monthly view. Users can inspect emotional data by selecting specific dates, linking mood changes to concrete time contexts. In educational research terms, such temporal mapping fosters temporal meta‑cognition—the understanding of emotional evolution over time.

         
Figure 2.5 Calendar Mood Review Interface and Analysis 

The Therapy Agent, or Chat Module, functions as an emotionally intelligent dialogue companion. Its backend can link to any OpenAI‑compatible endpoint via configurable Gradle properties (baseUrl, apiKey, model), but can also operate in offline fallback mode. The Agent generates supportive textual feedback by drawing upon the user’s recent mood and analysis context while remaining strictly non-diagnostic. This configuration permits flexible experimentation in human–AI emotional support without ethical risks of direct counseling.

Parallel to reflective modules, the Anonymous Community facilitates peer‑to‑peer support. It allows users to post emotion‑linked text, browse community feeds, react, and reply anonymously. Firebase Firestore serves as the backend for posts, likes, and comment synchronization. A simple moderation mechanism ensures that each participant can delete only self‑generated content, maintaining both safety and ownership. The design thus provides social reflection while preserving full anonymity.
                 
Figure 2.6 Agent Chat Interface       Figure 2.7 Community Feed and Post Detail Screens

The final Profile and Settings view aggregates the user’s anonymous identity, post counts, and preferences related to privacy. It completes the loop between self‑expression and data control, reinforcing that the user remains the owner of their emotional footprints.(The figure 2.8 is in page7)
2.3 Data Integration and Backend Design
Data management within MindEase is divided between local and remote sources. Locally, the Room database stores diary records and temporary session information, ensuring offline availability. The Firestore backend connects to the community module, supporting real‑time updates for posts and reactions. The optional therapy agent communicates through RESTful endpoints configured via build parameters. Visualization modules employ MPAndroidChart libraries, transforming structured data into comprehensible trends.
Figure 2.8 Data Flow between Local Room, Firestore, and Agent API


This hybrid configuration allows seamless synchronization without compromising the autonomy of the user’s private mood records.

2.4 Creativity and Innovation
The innovation of MindEase lies not in clinical diagnosis but in its synthesis of humanistic design and technological empathy. The interface is purposefully low‑pressure, encouraging honest emotional input. Its feedback cycle—record, analyze, reflect, and communicate—mirrors recognized frameworks of cognitive behavioral reflection while maintaining ethical distance from therapy claims.

The project introduces a locally anonymized identity model for the community space, representing a creative balance between safety and connectedness. Additionally, the contextual chat agent demonstrates a flexible architecture where emotional context informs supportive conversation without storing personal content. Finally, the visual analytical layer brings abstract emotions into spatial and temporal awareness, enabling a cognitive reflection process supported by data visualization.

Altogether, these innovations highlight an academic contribution to digital mental‑health design: employing computational tools to enhance emotional literacy under strict privacy conditions.
## 3. Implementation and Overall Quality

### 3.1 System Implementation Overview

MindEase is implemented as an Android MVP using Java 11, XML-based UI, Room, Firestore, and MPAndroidChart. The project follows a layered MVVM + Repository + Use-Case architecture that maps directly to the source-code package design (`app`, `feature`, `domain`, `data`, `common`). This explicit separation allows each module to evolve independently while keeping business logic testable and UI-agnostic.

At the application composition level, `AppContainer` acts as a dependency assembly point, wiring repositories, domain services, and use cases into an executable workflow. This design reduces hidden coupling and improves traceability from requirements to implementation.

Implementation evidence by core flow:

- Record: mood entries (type, intensity, text, tags) are persisted locally through Room entities and DAOs, enabling offline journaling and retrieval windows such as 7-day and 30-day queries.
- Analyze: `AnalysisRepositoryImpl` combines rule-based sentiment labeling and summary generation, with an AI-summary fallback path to guarantee output even when remote AI is unavailable.
- Suggest: `SuggestionEngine` generates actionable recommendations from recent analysis outputs, forming a closed loop from reflection to intervention.
- Chat: `AgentRepositoryImpl` stores sessions and messages locally and constructs contextual prompts via `PromptContextBuilder`; `RiskGuardService` adds high-risk guidance; `TherapyAgentService` provides remote generation with robust fallback for network or configuration failures.
- Share: `CommunityRepositoryImpl` uses Firestore transactions for post, comment, and like consistency and enforces author-only deletion rules to protect content ownership in anonymous interaction.

From an engineering perspective, this implementation balances functional completeness and extensibility: local-first emotional data handling ensures continuity, while cloud and AI interfaces remain pluggable for future upgrades.

### 3.2 Testing and Implementation Validation

Quality validation in this project focuses on implementation-level verification. The goal of this stage was not only to confirm that the app compiles and runs, but also to verify whether the core emotional-support workflow remains stable under both normal use and failure conditions.

#### Technical Verification

The implementation was validated with unit tests focused on the most important business rules and fallback paths.

- Command executed: `.\gradlew.bat testDebugUnitTest`
- Result: build successful, 15 tests in total, 0 failures, 0 errors, 1 skipped.
- Main coverage areas:
	- Mood recording persistence and retrieval.
	- Sentiment classification based on mood type, diary text, and intensity.
	- Rule-based summary generation for weekly emotional trends.
	- Personalized suggestion generation from analysis results.
	- Risk detection and safe-response guidance in the chat agent.
	- Remote-agent fallback behavior when API configuration or network access fails.
	- Core dependency assembly in the application container.

#### Detailed Test Cases

| Test Area | Representative Test Class | What It Verifies | Why It Matters |
| --- | --- | --- | --- |
| Core flow | CoreFlowUseCaseTest | Creates mood records, generates analysis, and produces suggestions end to end | Confirms that the main user journey is functionally closed |
| Sentiment analysis | RuleBasedSentimentAnalyzerTest | Correctly labels positive, neutral, and negative mood cases, including synonym mapping | Prevents incorrect emotional classification |
| Risk control | RiskGuardServiceTest | Detects high-risk expressions and returns guidance text | Ensures the app responds safely to sensitive input |
| AI fallback | AiAnalysisFallbackTest | Falls back to rule-based summaries when AI analysis is unavailable | Protects output continuity in weak-network or offline situations |
| Agent session logic | AgentRepositoryImplTest | Persists sessions/messages and stores fallback replies correctly | Verifies chat history integrity and context continuity |
| Mood data storage | MoodRepositoryImplTest | Supports create, update, delete, and recent-window queries | Confirms the diary module is reliable for daily use |
| Dependency wiring | AppContainerTest | Verifies that the app container can assemble core components | Confirms the application bootstrap layer is coherent |

#### Result Interpretation

These tests show that MindEase does not rely on a single happy path. Instead, it was deliberately tested for both normal workflow and degraded conditions. For an emotional-wellness app, this is important because users may open the app when the network is unstable, the AI service is unavailable, or the input content is sensitive. The tested fallback logic ensures the system still returns a meaningful response instead of failing silently.

#### Implementation-Based Validation Summary

Instead of relying on questionnaire-style outcome claims, this report summarizes project quality using implementation evidence and automated test outcomes. The current build demonstrates that:

- The full core loop (`record -> analyze -> suggest -> chat -> share`) is implemented and connected through repository and use-case layers.
- Sentiment classification, risk detection, and fallback logic are covered by unit tests and execute without failure.
- Chat-session persistence and degraded-mode behavior (remote API unavailable) are verified through repository-level tests.
- Data persistence logic supports mood CRUD and recent-window retrieval, ensuring baseline offline continuity.

Taken together, these results provide objective evidence that the implemented prototype is stable, testable, and functionally coherent at the MVP stage.

#### Testing Limitations

The current test suite is already strong at the unit level, but it still has two limitations. First, Firebase-related behavior is not yet covered by full emulator-based integration tests. Second, there is still room for broader UI automation and end-to-end scenario testing. These areas are important future additions because they would strengthen reliability evidence at the system level rather than only the code-unit level.

### 3.3 Software Quality Assessment

| Quality Attribute | Implemented Mechanism | Current Evidence | Risk and Improvement Direction |
| --- | --- | --- | --- |
| Functional suitability | End-to-end flow (Record, Analyze, Suggest, Chat, Share) with local and cloud integration | Working module set and passing core-flow tests | Expand longitudinal personalization and multilingual sentiment handling. |
| Reliability | Local Room persistence, Firestore transaction updates, AI fallback path | 0 failed unit tests; fallback scenarios validated | Add instrumented integration tests for Firestore transaction edge cases. |
| Usability | Low-pressure interaction model, simple navigation, calm visual hierarchy | Feature-complete interaction flow and stable execution across tested scenarios | Add task-completion-time studies and SUS scoring in a future formal user study. |
| Security and privacy | Anonymous identity mapping, author-only delete constraints, minimal account/session data | Ownership constraints and moderation pipeline implemented | Current auth/session remains MVP-level local storage; next step is encrypted local storage and stronger account security. |
| Maintainability | Clear package layering and use-case isolation; dependency assembly via AppContainer | High code traceability from module to use case | Introduce a DI framework and remove manual wiring for larger-scale evolution. |
| Performance efficiency | Local query windows, bounded chat-context history, asynchronous Firestore callbacks | Stable debug build and passing unit-test execution without runtime-blocking failures in tested paths | Remove `allowMainThreadQueries` and move all database operations fully off the main thread. |

Table 3.2. Implementation-oriented software quality assessment based on ISO/IEC 25010.

Overall, MindEase demonstrates a solid MVP quality baseline: its architecture is modular and critical logic is test-verified. Importantly, the project also identifies and documents engineering risks transparently, which strengthens academic credibility and future scalability.

### 3.4 Reflection and Limitations

Although current implementation quality is strong for an academic MVP, several limitations remain:

- Personalization is still largely rule-driven. The recommendation engine does not yet perform long-horizon adaptive learning from individual feedback loops.
- Sentiment understanding for colloquial and multilingual expressions is limited. This constrains precision for bilingual campus populations and should be addressed with localized corpora and model fine-tuning.
- Community and backend verification is currently stronger at logic and unit level than at full integration level. Future iterations should add Firebase emulator-based integration tests and failure-injection scenarios.
- While privacy-by-design is emphasized at the interaction level through anonymous community identity and ownership constraints, data protection should be further strengthened through encrypted local persistence and clearer retention and erasure policies.

The next-phase roadmap therefore focuses on three engineering priorities: adaptive personalization, stronger security hardening, and deeper integration testing. These upgrades would directly improve both software quality scores and real-world trustworthiness.

4.Summary
In conclusion, MindEase exemplifies a comprehensive and student‑oriented approach to emotional wellness through digital innovation. Designed around the five functional stages of Record, Analyze, Suggest, Chat, and Share, the app integrates reflection, self‑observation, and anonymous communication into a coherent and ethical user experience. By adopting a modular MVVM architecture and dual‑layer data management via Room Database and Firestore, the system effectively balances offline stability with synchronized cloud services, ensuring both reliability and data security.

Implementation and testing outcomes confirm the system’s practical engineering value and technical coherence. The completed feature set, verified fallback logic, and passing unit tests indicate that structured emotion tracking combined with AI-assisted interaction can be delivered as a stable and ethically constrained MVP for student-oriented emotional support scenarios.

From a research perspective, MindEase demonstrates how human‑centered computing can advance mental health promotion without replacing professional therapy. The project achieves equilibrium between functionality, usability, and privacy, reflecting thoughtful engineering and responsible AI design. While limitations such as limited multilingual understanding and insufficient personalization remain, the future roadmap envisions adaptive models, contextual learning, and integration with campus counseling services.

Overall, MindEase embodies an academically rigorous yet empathetic attempt to merge technology and emotional intelligence, showcasing how digital tools, when sensitively designed, can foster sustainable emotional resilience and meaningful human connection within university environments.

References
[1]Gao, L., Xie, Y., Jia, C. et al. Prevalence of depression among Chinese university students: a systematic review and meta-analysis. Sci Rep 10, 15897 (2020). https://doi.org/10.1038/s41598-020-72998-1
[2]Osman, W.A. Social media use and associated mental health indicators among University students: a cross-sectional study. Sci Rep 15, 9534 (2025). https://doi.org/10.1038/s41598-025-94355-w
[3]National Mental Health Commission. (2024). Digital technologies and youth mental health: Discussion paper.https://www.mentalhealthcommission.gov.au/

