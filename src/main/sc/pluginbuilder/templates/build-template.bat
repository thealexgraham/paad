@ECHO off
set name=^synth_name^
"^builder_dir^/msbuild" "^builder_dir^/SCBuilder/fmod-osc.sln" /p:Configuration=SCBuild;Flavor=%name%
ECHO Plugin Built
COPY "^builder_dir^\SCBuilder\SCBuild\SC-%name%.dll" "C:\Program Files (x86)\FMOD SoundSystem\FMOD Studio 1.04.04\plugins"
COPY "^builder_dir^\SCBuilder\SCBuild\SC-%name%.dll" "^builder_dir^\plugins"
ECHO %name% Plugin Copied to Supercollider Folder