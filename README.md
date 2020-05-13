# The Procedural Audio Asset Designer (PAAD)
##### by Alex Graham

### Check out the [demo video!](https://www.youtube.com/watch?v=TJlD6vVK9yk)

This software was developed for my Master's Thesis at NYU's Music Technology program. The document can be found [here](https://github.com/thealexgraham/paad/files/4624090/graham_paad_thesis.pdf).

## Overview
PAAD is a tool that allows you to design procedural audio assets and easily export them for use in game engines.

Create procedural music and sound effects using the power of the SuperCollider synthesis engine and PAAD's easy to use patch-based interface. With one click, export a fully contained asset that can be easily implemented into a game engine (currently as an FMOD plugin).

![PAAD](https://user-images.githubusercontent.com/3069010/81853415-60989980-952a-11ea-97bc-a848671516e8.png "PAAD Interface")

### Create
Create procedural audio assets in a patch-based interface for Supercollider.

PAAD has support for creating modules for sound generation, effect processing, buffer loading/playback, pattern generation/playback, parameter control and routines.

### Expand
Modules in PAAD are comprised of SuperCollider definitions. Anything that can be created in SuperCollider can be used in PAAD.

Any PAAD definitions that are placed in PAAD’s startup folder will be available for use within the PAAD interface.
PAAD module definitions can be edited from within the interface and the changes are heard in real time.

![Real-time module editing](https://user-images.githubusercontent.com/3069010/81853414-60000300-952a-11ea-9401-77a742f5c9b3.png)

### Export
PAAD assets are self-contained and can be run completely independently from the interface using the SuperCollider server and controlled with OSC messages. This means PAAD assets can easily be implemented into game engines.

PAAD currently is able to create FMOD plugins- which can either be self-contained or control the PAAD interface. Just select which parameters from the asset you’d like to control from FMOD and click export.


![Export parameters](http://alexgraham.net/paad/exportselect.png) ![Export](https://user-images.githubusercontent.com/3069010/81853413-60000300-952a-11ea-8810-291080e1afbe.png)
![FMOD Patch](https://user-images.githubusercontent.com/3069010/81853412-60000300-952a-11ea-8dba-ca202432e241.png)

PAAD was developed for my Master's thesis at NYU and will be available in the near future. Please contact me for any questions!

### Links:

#### [Demo Video](https://www.youtube.com/watch?v=TJlD6vVK9yk)

#### [Research Document](https://github.com/thealexgraham/paad/files/4624090/graham_paad_thesis.pdf).


