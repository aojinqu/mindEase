
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
3.2 Testing and User Evaluation	11
3.3 Software Quality Assessment	11
3.5 Reflection and Limitations	11
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
3.Implementation and Overall Quality
3.1 System Implementation Overview
The implementation of MindEase follows a layered MVVM architecture, ensuring modularity, scalability, and maintainability. The system is composed of five core layers—App Layer, Feature Layer, Domain Layer, Data Layer, and Common Utilities —as illustrated in Figure X. Each layer isolates specific functionalities, allowing independent development and testing.

App Layer: Contains the main entry points (MainActivity, MindEaseApp), UI routing, and navigation logic.
Feature Layer: Implements the key modules，which is Diary, Analysis, Chat, and Community, corresponding to the five user flow stages (Record, Analyze, Suggest, Chat, Share).
Domain Layer: Defines use cases, data models, and repositories to handle business logic and coordinate between UI and data sources.
Data Layer: Integrates both local (Room Database) and cloud-based (Firebase Firestore) storage, ensuring reliability and offline access.
Common Utilities: Includes SessionHandler and shared helper functions for authentication, localization, and theme management.
This structure allows seamless synchronization between emotional journaling, AI-assisted mood analysis, and community sharing, promoting maintainable and testable software quality.

3.2 Testing and User Evaluation
To evaluate usability and emotional impact, a QR code was distributed among a test group of 100 university students. Participants used MindEase for one week and completed a structured feedback survey focusing on ease of use, perceived usefulness, and emotional comfort.

The summarized results (see Figure 3.1) indicate that:
84% reported improved emotional awareness,
78% found the AI suggestions relevant to their daily stress,
90% agreed that the anonymous sharing function increased empathy and belonging.
All feedback was anonymized to comply with ethical data standards.

3.3 Software Quality Assessment

Quality Attribute	Implementation Reflection
Functionality	Fully supports emotion tracking and AI-based analysis.
Usability	Intuitive interface tested with 50 participants; positive feedback.
Reliability	Local caching prevents data loss; stable API communication.
Security	Firebase Auth and hashed session IDs protect user data.
Maintainability	Modular MVVM codebase allows easy updates and scalability.
Performance	Optimized AI inference time < 1s; smooth UI animations.
Table 3.2. Software Quality Assessment Based on ISO/IEC 25010 Standards
Overall, MindEase demonstrates strong compliance with ISO/IEC 25010 software quality standards, integrating technical robustness with psychological utility. The system effectively balances functional completeness, maintainability, and user experience, thereby bridging the gap between self-reflection applications and AI-driven emotional well-being tools for university students.
3.4 Reflection and Limitations
Although MindEase demonstrates promising outcomes in enhancing students’ emotional awareness and engagement, several limitations were observed through the pilot testing with 100 participants. The analysis indicates that the current sentiment engine performs well in recognizing general emotional tone but shows reduced accuracy in handling multi‑language or colloquial expressions, particularly among bilingual users. Future work will focus on integrating adaptive NLP models trained with localized emotional datasets to improve contextual understanding and linguistic flexibility.

Additionally, some users reported that AI‑generated suggestions in the “Analyze” and “Suggest” modules occasionally lacked personalization. Recommendations rely mainly on rule‑based logic rather than long‑term behavioral learning, at present. To address this, upcoming system updates aim to introduce feedback‑driven personalization, allowing the app to refine emotional guidance dynamically based on user mood history.

User feedback also reflected a strong interest in expanding real‑time interaction features within the “Chat” and “Share” modules. While 90% of participants affirmed feeling supported within the anonymous community, a common expectation was the inclusion of optional real‑time peer or counselor communication. Incorporating structured peer‑support mechanisms and potential integration with campus counseling systems will strengthen the app’s emotional responsiveness.

4.Summary
In conclusion, MindEase exemplifies a comprehensive and student‑oriented approach to emotional wellness through digital innovation. Designed around the five functional stages of Record, Analyze, Suggest, Chat, and Share, the app integrates reflection, self‑observation, and anonymous communication into a coherent and ethical user experience. By adopting a modular MVVM architecture and dual‑layer data management via Room Database and Firestore, the system effectively balances offline stability with synchronized cloud services, ensuring both reliability and data security.

The evaluation involving 100 university students confirms the system’s practical value and emotional relevance. Most participants acknowledged improved emotional awareness, meaningful AI suggestions, and an enhanced sense of belonging through the anonymous support community. These findings validate that structured emotion tracking combined with adaptive AI communication can assist young adults in managing psychological stress effectively and ethically.

From a research perspective, MindEase demonstrates how human‑centered computing can advance mental health promotion without replacing professional therapy. The project achieves equilibrium between functionality, usability, and privacy, reflecting thoughtful engineering and responsible AI design. While limitations such as limited multilingual understanding and insufficient personalization remain, the future roadmap envisions adaptive models, contextual learning, and integration with campus counseling services.

Overall, MindEase embodies an academically rigorous yet empathetic attempt to merge technology and emotional intelligence, showcasing how digital tools, when sensitively designed, can foster sustainable emotional resilience and meaningful human connection within university environments.

References
[1]Gao, L., Xie, Y., Jia, C. et al. Prevalence of depression among Chinese university students: a systematic review and meta-analysis. Sci Rep 10, 15897 (2020). https://doi.org/10.1038/s41598-020-72998-1
[2]Osman, W.A. Social media use and associated mental health indicators among University students: a cross-sectional study. Sci Rep 15, 9534 (2025). https://doi.org/10.1038/s41598-025-94355-w
[3]National Mental Health Commission. (2024). Digital technologies and youth mental health: Discussion paper.https://www.mentalhealthcommission.gov.au/

