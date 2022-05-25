# Tarasande
### This is a private project. The absence of a LICENSE file is intended.

## Goals
- Provide easy usable interfaces
- Be powerful and allow the user to change everything he wants to
- Improve gameplay compared to vanilla
- Bypass various anticheats
- Be able to use the modification without being caught

## Features
### High Priority
- Combat modules, which look legit
- Movement modules
- Somewhat good-looking render modules
### Middle Priority
- Combat modules, which look obvious
- Anarchy modules
### Low Priority
- Minor enhancements to minecraft
### Special Priority
- Compatibility with various other modifications

## Roadmap
1. Base
2. Account Management (with encrypted files)
3. ClickGUI
4. Basic combat modules
5. Basic movement modules
6. Basic render modules
7. More advanced modules
8. Anarchy modules
9. Multi-version support

## TODO-List
- Vehicle Speed
- Elytra Flight
- Baritone Sprint Jump
- Use Mixins to remove the hit and particle handlers from trajectory fake arrows
- BowAimbot
- More Anti Bots
- Inventory management
- KillAura: Simulate Damage in case cooldown isn't gone but full dmg isn't needed to kill somebody
- PvP-Bot for Practice Game Modes
- Fix PlayerUtil#simulateAttack
- Clear buffers on world change in Blink/Latency
- Stop using Thread.stop
- Make the friend alias feature accessable to the user (frontend is missing)
- MidClick (alias prompt with screen and callback?)
- KillAura: Calculate attack cycle from enemies
- Configure ESP / "ESP Studio" - Fully customizable 2D esp setting like spirthack
  - Items are classes with getSpace, draw(orientation, axis start, axis end)
  - Right click for values in classes
- SafeWalk: Onground only sneak
- Rename all accessor methods to have a "tarasande_" prefix

## Notes
- Matrix Bots never sneak (might be applicable to other bots)
- AutoTool: Axes can set shields on cooldown. Maybe implement that if possible axes are being used to set shields on cooldown before actually attacking with sword
