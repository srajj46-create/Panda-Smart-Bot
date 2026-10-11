# Panda Smart BOT (prototype)

## What this prototype does
- Lets you enable an Android Accessibility Service.
- Provides Start/Stop controls.
- Attempts to click a visible `CLAIM` UI node when Android exposes that text to accessibility.
- Alternates taps on the left/right halves of the screen while enabled.

## Important limitations
This is a **prototype, not a finished smart bot**. It does not yet visually detect bamboo branches or determine the safest side. It cannot guarantee that levels will be completed. Many Unity/game-rendered buttons and game HUD elements are not exposed as accessibility text, so Claim detection may not work in this game. Ads may not expose their close button to accessibility; never auto-click ad install buttons. Keep the game supervised and stop the bot if it behaves incorrectly.

## Build APK using GitHub
1. Create a GitHub repository and upload all files in this project.
2. Make sure the workflow is at `.github/workflows/android.yml`.
3. Open the repository's **Actions** tab.
4. Select **Build Panda Smart BOT APK** and tap **Run workflow** (or push to `main`).
5. Open the completed run, go to **Artifacts**, and download `panda-smart-bot-debug-apk`.
6. Extract the ZIP and install `app-debug.apk`.

## Use
1. Install and open Panda Smart BOT.
2. Tap **Enable Accessibility Service** and enable Panda Smart BOT in Android Accessibility settings.
3. Return to the app and tap **Start BOT**.
4. Open Panda Climbing Game yourself and supervise it.
5. Tap **Stop BOT** to stop automation.

## Next development step
For real smart branch avoidance, add a screen-capture pipeline and vision logic that identifies the panda, bamboo, and branch positions in gameplay frames. That requires collecting screenshots from several gameplay states and tuning the detector against them.
