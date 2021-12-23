package su.mandora.tarasande.screen.accountmanager.environment

import su.mandora.tarasande.base.screen.accountmanager.environment.EnvironmentPreset

class EnvironmentPresetMojang : EnvironmentPreset(
	"Mojang",
	"https://authserver.mojang.com",
	"https://api.mojang.com",
	"https://sessionserver.mojang.com",
	"https://api.minecraftservices.com"
)

class EnvironmentPresetTheAltening : EnvironmentPreset(
	"The Altening",
	"http://authserver.thealtening.com",
	"http://api.thealtening.com",
	"http://sessionserver.thealtening.com",
	"https://api.minecraftservices.com"
)