@ECHO off
set name=FM3OP
msbuild SCBuilder/fmod-osc.sln /p:Configuration=SCBuild;Flavor=%name%
ECHO Plugin Built
COPY "SCBuilder\SCBuild\SC-%name%.dll" "C:\Program Files (x86)\FMOD SoundSystem\FMOD Studio 1.04.04\plugins"
COPY "SCBuilder\SCBuild\SC-%name%.dll" "plugins"
ECHO %name% Plugin Copied to Supercollider Folder
PAUSE