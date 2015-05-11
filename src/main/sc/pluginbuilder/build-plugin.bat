@ECHO off
set name=Live-pingdemolive
"C:\Users\Alex\Dropbox\Thesis\thesis-code\workspace\agthesis-java\src\main\sc\pluginbuilder/msbuild" "C:\Users\Alex\Dropbox\Thesis\thesis-code\workspace\agthesis-java\src\main\sc\pluginbuilder/SCBuilder/fmod-osc.sln" /p:Configuration=SCBuild;Flavor=%name%
ECHO Plugin Built
COPY "C:\Users\Alex\Dropbox\Thesis\thesis-code\workspace\agthesis-java\src\main\sc\pluginbuilder\SCBuilder\SCBuild\SC-%name%.dll" "C:\Program Files (x86)\FMOD SoundSystem\FMOD Studio 1.04.04\plugins"
COPY "C:\Users\Alex\Dropbox\Thesis\thesis-code\workspace\agthesis-java\src\main\sc\pluginbuilder\SCBuilder\SCBuild\SC-%name%.dll" "C:\Users\Alex\Dropbox\Thesis\thesis-code\workspace\agthesis-java\src\main\sc\pluginbuilder\plugins"
ECHO %name% Plugin Copied to Supercollider Folder