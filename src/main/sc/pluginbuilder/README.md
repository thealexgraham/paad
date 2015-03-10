sc-fmod-pluginbuilder
=====================
by Alex Graham

Automatically builds FMOD Studio plugins that communicate with SuperCollider Synths over OSC. 

Currently Windows only, although the SuperCollider code and the C++ code it generates will work for either system.

Preperation
-------------
1. Copy C:\Windows\Microsoft.Net\Framework64\v4.0.30319\MSBuild.exe into the pluginbuilder directory (or use the one included).
2. Open SCBuilder/fmod-osc.sln in Visual Studio 2010 (could possibly work in other versions). You can close after that
3. Change the FMOD Plugin directory in templates/build-template.bat to your FMOD plugin directory.
4. Move the PluginBuilder.sc class to your SuperCollider extensions folder.

Check out the buildertest.scd for an example of usage. 
**Make sure to run plugin-builder.bat in the pluginbuilder directory after running the generateCode() command!**

If you have any questions, please feel free to email me at alex@alexgraham.net.
