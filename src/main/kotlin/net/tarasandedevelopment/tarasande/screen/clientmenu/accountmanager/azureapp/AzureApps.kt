package net.tarasandedevelopment.tarasande.screen.clientmenu.accountmanager.azureapp

import net.tarasandedevelopment.tarasande.base.screen.clientmenu.accountmanager.azureapp.AzureAppPreset
import java.util.UUID

class AzureAppPresetInGameSwitcher : AzureAppPreset(
    "Ingame Account Switcher",
    UUID.fromString("54fd49e4-2103-4044-9603-2b028c814ec3"),
    "XboxLive.signin offline_access"
)

class AzureAppPresetBashAuth : AzureAppPreset(
    "Bash/Clamp Auth",
    UUID.fromString("841d89e4-7da3-438c-b2c6-8a269e142f69"),
    "XboxLive.signin offline_access",
    clientSecret = "-1t5cM1-neTZw1aZ01.8uxz..xBC94tHE6"
)

class AzureAppPresetPolyMC : AzureAppPreset(
    "PolyMC",
    UUID.fromString("549033b2-1532-4d4e-ae77-1bbaa46f9d74"),
    "XboxLive.signin offline_access"
)

class AzureAppPresetMultiMC : AzureAppPreset(
    "PolyMC",
    UUID.fromString("499546d9-bbfe-4b9b-a086-eb3d75afb78f"),
    "XboxLive.signin offline_access"
)

class AzureAppPresetTechnicLauncher : AzureAppPreset(
    "Technic Launcher",
    UUID.fromString("8dfabc1d-38a9-42d8-bc08-677dbc60fe65"),
    "XboxLive.signin offline_access"
)

class AzureAppPresetLabyMod : AzureAppPreset(
    "LabyMod",
    UUID.fromString("27843883-6e3b-42cb-9e51-4f55a700601e"),
    "XboxLive.signin offline_access"
)

class AzureAppPresetOldTechnicLauncher : AzureAppPreset(
    "Old Technic L.",
    UUID.fromString("5f8b309f-ad5f-49bf-877a-8b94afd75b9f"),
    "XboxLive.signin offline_access"
)

class AzureAppPresetGDLauncher : AzureAppPreset(
    "GD Launcher",
    UUID.fromString("b9336bf8-c6bb-4344-aabe-63d0bfa8db2e"),
    "offline_access xboxlive.signin xboxlive.offline_access"
)
