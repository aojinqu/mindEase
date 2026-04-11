# MindEase

MindEase is an Android app for university students that combines a mood diary, lightweight emotional support, trend analysis, and an anonymous community experience. The product is designed around the core flow of `record -> analyze -> suggest -> share`, with a strong focus on low-pressure interaction, privacy, and local-first usability.

This repository currently implements an MVP in Android Studio with Java and XML. Based on the PRD and technical design documents, the goal is not medical diagnosis. MindEase is positioned as a daily emotional companion tool that helps users build awareness of their mood patterns and access simple, supportive guidance.

## Product Goals

- Make daily mood check-ins fast and easy
- Help users understand mood trends through charts and calendar views
- Provide rule-based analysis with a path to future AI integration
- Offer a safe anonymous space for expression
- Keep core diary features available in offline or unstable-network scenarios

## Current Project Status

According to [docs/PRD.md](/e:/hku/smartphone/MindEase/docs/PRD.md), [docs/TECHNICAL_DESIGN.md](/e:/hku/smartphone/MindEase/docs/TECHNICAL_DESIGN.md), and [docs/BOARD.md](/e:/hku/smartphone/MindEase/docs/BOARD.md), the current codebase already covers the main MVP flow:

- App flow: `Splash -> Onboarding -> Auth -> Main`
- Main navigation: Home, Analysis, Calendar, Community, Profile
- Mood record flow: create, edit, delete, and recent record retrieval
- Analysis flow: rule-based analysis, trend charts, and AI-fallback support
- Calendar flow: monthly overview and per-day detail lookup
- Suggestion flow: lightweight self-help suggestions based on recent records
- Anonymous community flow: create post, browse feed, open details, basic moderation, anonymous identity mapping
- Local persistence: Room entities, DAOs, database entry point, and mood record persistence path
- Unit tests for core use cases, repository behavior, sentiment rules, and AI fallback logic

The following parts are still pending, partial, or placeholder-only:

- Real remote AI integration
- Firestore or other cloud-backed community storage
- Comments, reports, and more advanced moderation features
- UI automation tests and fuller database integration coverage

## Core Features

### Daily Mood Check-In

- Choose mood type and intensity
- Write diary text
- Attach mood-related tags
- Edit, delete, and review recent records

### Mood Analysis and Suggestions

- Rule-based sentiment and trend analysis
- Weekly and monthly visual summaries
- Short, non-medical support suggestions
- Extension points for future AI-generated analysis

### Mood Calendar

- Calendar-based view of daily mood states
- Tap a date to inspect that day's records

### Anonymous Community

- Publish anonymous emotional posts
- Browse a lightweight community feed
- Open post details
- Apply basic content moderation and anonymous naming

## Tech Stack

- Language: Java 11
- UI: Android XML
- Architecture: MVVM + Repository + UseCase + Service
- Local storage: Room
- Charts: MPAndroidChart
- Build system: Gradle Kotlin DSL
- Minimum Android version: `minSdk 24`
- Target Android version: `targetSdk 36`

The implementation follows the same technical principles defined in the design documents:

- Local-first: core record and analysis features should remain usable without a backend
- Privacy-first: diary data is private by default, and community identity is anonymized
- Extensible: rule-based behavior lands first, while cloud and AI capabilities can be added later

## Project Structure

```text
MindEase/
|-- app/
|   `-- src/
|       |-- main/java/com/mindease/
|       |   |-- app/        # app entry point and dependency container
|       |   |-- common/     # shared UI state, events, session handling
|       |   |-- data/       # Room, entities, DAOs, repository implementations
|       |   |-- domain/     # models, repository contracts, use cases, services
|       |   `-- feature/    # screens and view models
|       |-- main/res/       # XML layouts, themes, menus, resources
|       `-- test/           # unit tests
|-- docs/                   # PRD, technical design, board, task notes
|-- gradle/
`-- README.md
```

## Main Screens

- `SplashActivity`
- `OnboardingActivity`
- `AuthActivity`
- `MainActivity`
- `MoodEditorActivity`
- `PostEditorActivity`
- `PostDetailActivity`

Main fragments:

- `HomeFragment`
- `AnalysisFragment`
- `CalendarFragment`
- `CommunityFragment`
- `ProfileFragment`

## Getting Started

### Requirements

- Android Studio
- JDK 11
- Android SDK installed locally

### Run In Android Studio

1. Open the repository root in Android Studio.
2. Wait for Gradle sync to finish.
3. Make sure `local.properties` points to a valid Android SDK.
4. Run the `app` module on an emulator or device.

### Command Line Build

```powershell
.\gradlew.bat assembleDebug
```

## Testing

The repository already includes unit tests for key business flows and analysis logic.

Run tests from the command line:

```powershell
.\gradlew.bat test
```

## Documentation

- Product requirements: [docs/PRD.md](/e:/hku/smartphone/MindEase/docs/PRD.md)
- Technical design: [docs/TECHNICAL_DESIGN.md](/e:/hku/smartphone/MindEase/docs/TECHNICAL_DESIGN.md)
- Development board: [docs/BOARD.md](/e:/hku/smartphone/MindEase/docs/BOARD.md)
- Course task notes: [docs/TASK.md](/e:/hku/smartphone/MindEase/docs/TASK.md)

## Planned Next Steps

- Integrate a real AI analysis and suggestion service
- Add Firestore or another backend for community sync
- Expand moderation and community interaction features
- Improve Room integration testing and UI automation coverage
- Continue polishing UI states, empty states, and edge-case handling

## Scope Note

MindEase is an MVP-oriented student project focused on product completeness, architecture clarity, and future extensibility. It is not a medical product and does not replace professional psychological support or diagnosis.
