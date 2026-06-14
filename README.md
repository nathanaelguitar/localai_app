# Canopy KMP

Premium Kotlin Multiplatform / Compose Multiplatform mobile app scaffold for Android and iOS.

This is intentionally not a ChatGPT clone. It uses its own product identity, oak/canopy visual system, workspace organization, saved workflows, onboarding, persona selection, local settings, and a demo chat repository.

## What works now

- Onboarding flow
- Workspace switcher
- Conversation list
- New chat
- Open chat
- Send message with a fake local assistant response
- Loading/typing state
- Pin/unpin thread
- Rename thread from latest message
- Clear thread
- Saved workflows
- Load workflow into composer
- Persona switcher
- Settings screen
- Dark/light theme toggle
- Provider/endpoint/API label local settings
- iOS SwiftUI host for the shared Compose UI
- Android activity entry point

## Backend hook

Replace this file when you are ready:

`composeApp/src/commonMain/kotlin/com/nathanael/canopy/data/ChatRepository.kt`

Keep the `ChatRepository` interface and replace `DemoChatRepository` with your API client.

## Run Android

1. Open the root folder in Android Studio or IntelliJ IDEA.
2. Let Gradle sync.
3. Run the `composeApp` Android target.

## Run iOS from Xcode

1. Make sure Xcode is installed.
2. Make sure Gradle is available. If `./gradlew` complains, run:

```bash
brew install gradle
cd canopy-kmp
gradle wrapper --gradle-version 8.14.3
```

3. Open:

```bash
open iosApp/iosApp.xcodeproj
```

4. Select an iPhone simulator.
5. Press Run.
6. If signing complains, set your Apple development team in Xcode under the `iosApp` target.

The Xcode project has a build phase that calls:

```bash
./gradlew :composeApp:embedAndSignAppleFrameworkForXcode
```

That builds the Kotlin/Compose framework and links it into the SwiftUI shell.

## Product direction

The current name is Canopy. The premium oak-tree background is drawn in Compose using Canvas, so there are no external image assets to manage. The UX is intentionally project/workflow-oriented to reduce App Store review risk from looking like a generic AI wrapper.
