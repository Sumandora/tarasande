# Package-Protocol-Hack (Tarasande)
### tarasande package to provide a ViaVersion implementation

## Goal and structure 
This package contains the whole Protocol Translation part of tarasande, basically the ProtocolHack of tarasande is divided into four big parts:

| Part                       | Description                                                                                                                                                             | Source                                           |
|----------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------|
| ViaLoadingBase             | Loads ViaVersion, ViaBackwards and ViaRewind.<br>Specifies the basic platform and the implementation of the EventLoops.<br>Defines all Versions and their Protocol Ids. | https://github.com/FlorianMichael/ViaLoadingBase |
| ViaBeta                    | Adds support for all Minecraft versions from c0.0.15a-1 to 1.7.10 and classic protocol extensions                                                                       | https://github.com/FlorianMichael/ViaBeta        |
| ViaCursed                  | Adds support for AprilFool snapshots (3DShareware, Infinite, CombatTest8C) and the Bedrock edition                                                                      | https://github.com/FlorianMichael/ViaCursed      |
| ClampClient Protocol Fixes | General fixes in Minecraft code that are too difficult at Protocol level                                                                                                | https://github.com/FlorianMichael/ClampSource    |
