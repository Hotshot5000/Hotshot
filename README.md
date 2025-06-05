Hotshot SDK

WORK IN PROGRESS!
YOU CAN TAKE A LOOK AND USE THE CODE BUT RIGHT NOW IT'S NOT BUILDABLE WITH ONLY WHAT IS IN THIS REPO!
COMING SOON!

If you have questions, want to help, want to say hi, tell me how bad the code is, write an issue and I will get back to you.

This is a mobile/desktop game where you pilot a spaceship from 1st or 3rd person view and fight against AI or other players online.
Game code is mostly Java but most of the backend is C++.

The base framework is libGDX to enable portability on iOS/Android and Windows but it has been extended with the following libs:

Ogre3D - Rendering, 
Bullet - Physics,
MobiVM - iOS build, 
Kryonet - Networking,
MiniAudio - Audio,
Artemis - Entity Component System,
Retrofit, OkHttp - Http and REST messaging

How to build for Windows

Make sure you have java 11 for x64 and jdk 8 for x64.

Make sure that you are using gradle with JDK 1.8 for 64 bits. Otherwise you get:
ByteBuffer and the Dreaded NoSuchMethodError - Gunnar Morling

Intellij Run config:

com.headwayent.blackholedarksun.desktop.DesktopLauncher
Working dir: C:\Sebi\projects\BlackholeDarksunOnline6\desktop
Jdk select java 1.8 64 bit

Arguments for JMX on visualvm add to DesktopLauncher VM arguments:
-Dcom.sun.management.jmxremote
-Dcom.sun.management.jmxremote.port=9010
-Dcom.sun.management.jmxremote.rmi.port=9010
-Dcom.sun.management.jmxremote.local.only=false
-Dcom.sun.management.jmxremote.authenticate=false
-Dcom.sun.management.jmxremote.ssl=false

