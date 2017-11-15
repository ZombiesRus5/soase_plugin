Alpha Version History:

Eclipse Plugin:

3/18/2011 v.0.8.3 

1.Defect: Fixes issue opening non-eclipse managed files in the Sins Editor via the File->File Open... menu actions. 
2.Defect: Fixes issue ordering issue of reference files for some validations introduced with version 0.8. For example vanilla brush files are overiding Entrenchment reference brush files. The ordering was introduced to better handle total conversion projects. 


Eclipse Plugin: 3/11/2001 v.0.8.2 

1.Enhancement: Adds support for custom Galaxy files (.galaxy) 
1.Enhancement: Adds support for Explosiondata files 
2.Enhancement: Adds two pass validation for Galaxy and GalaxyScenarioDef files for the following validations. 
1.Galaxy template references 
2.Galaxy orbit body type references 
3.Galaxy design references 


3/10/2011 v.0.8.1 

1.Defect: Fixes issue with file deletions causing fullBuild to error in some scenarios. 


3/10/2011 v.0.8.0 

1.Enhancement: Updates Wiki to reflect new changes 
2.Enhancement: Adds support for resource event listening which is used to determine when a fullBuild or incrementalBuild is required. This should alleviate a lot of the Project/Clean... manual work when adding/removing and modifying files. 
3.Enhancement: Adds aiUseTime->OnlyWhenDebrisWithinRange? (Beta 1.2) 
4.Enhancement: Adds aiUseTargetCondition->AntimatterExceedsAmount? (Beta 1.2) 
5.Defect: Adds missing FightersPerSquadron? for buffEntityModifierType 
6.Defect: Adds ChaosBolt? to weaponClassForWeaponPassive 
7.Enhancement: Adds defaults for upgrade times 
8.Enhancement: Adds new GameEventSound?:QuestEnded? (Beta 1.2) 
9.Enhancement: Adds new PlayerAITable->BuildMines? (Beta 1.2) 
10.Enhancement: Adds new PlanetData?->minPropagatedCultureRate (Beta 1.2) 
11.Enhancement: Adds new PlanetData?->culturePropagationPerc (Beta 1.2) 
12.Enhancement: Adds new PlanetData?->cultureDecayRate (Beta 1.2) 
13.Enhancement: Adds new pirateRaidDef->raidCost:0-4?(Beta 1.2) 
14.Enhancement: Adds new playerDiplomacyAIDef->tradeBonusRatePerTrip (Beta 1.2) 
15.Enhancement: Adds new playerDiplomacyAIDef->tradeBonusCap (Beta 1.2) 
16.Enhancement: Adds new playerDiplomacyAIDef->tradeBonusDecayRate (Beta 1.2) 


v.0.7.7
Enhancement: Add support for vanilla only settings
Enhancement: Add support for project property overrides (right click on project in explorer)
Defect: NumCriticalHitEffectSoundIDs incorrectly referenced Particle for SoundID

11/16/2010 v.0.7.6
Enhancement: Add support for multiple mods
Enhancement: Add version support for conditions
Enhancement: Add version tags to additional constants
Enhancement: Add research subject upgrade time validation support


11/01/2010 v.0.7.5 
Enhancement: Add support for constraints on decimals 
Enhancement: Add travelSpeed validation when weaponType=Beam 
Defect: Add missing FightersPerSquadron? buffEntityModifierType 
Defect: Add missing minCount/maxCount in rebellionInfo/requiredShip 

10/12/2010 v.0.7.4 
Defect: WithinSolarSystem? should be WithinCurrentSolarSystem? 
Enhancement: useCostType is Passive but autocast is TRUE 
Enhancement: Validate weaponEffect weaponType equals Weapon's weaponType 
Defect: ResearchSubject Tier validation not working properly for Artifacts 
Defect: ApplyForceFromSpawner? not validating correctly on force line 
Enhancement: Add support for tier level research cost validation (defaulted to off via preferences) 
Defect: HEX Color validation currently showing errors on RGB (6 character hex codes), works for RGBA 

8/24/2010 v.0.7.3 
1.Enhancement: Adds tier validation support for ResearchSubject entity files 
2.Defect: Fixes issue with requiredShip rule not correctly validating minCount and maxCount. 

8/24/2010 v.0.7.2 
1.Defect: Fixes issue with content assistance not correctly completing unfinished portion of keyword. 
2.Defect: Fixes issue with content assistance not correctly completing unfinished values 
3.Defect: Fixes issue with content assistance not recognizing _ (underscores) in ID values 
4.Defect: Fixes issue with Project/Clean... not completing. 

8/23/2010 v.0.7.1 
1.Fixes issue with v.0.7 build not including icons 

8/23/2010 v.0.7 
1.Enity Editor Enhancements 
1.Adds Syntax Coloring Support 
2.Adds Hover Help Support 
3.Adds Content Assistant 
Enumerations/Constants 
Sound References 
Entity References 
Explosions 
Particle Effects 
4.Adds popup menu with F3 shortcut to open referenced entity files 
5.Adds new icons 
2.Adds new preference options 
1.Version support 
2.Editor Tab Width 
3.Show Hover Help Flag 
4.Syntax Coloring 
3.Entity Builder 
1.Adds additional progress information 
4.Entity Parser/Validator 
1.Addds support for Vanilla/Entrenchment/Diplomacy validations 
2.Fixes some issues with validation 
3.Enhanced validation for many conditional type fields 
1.finishConditionType 
2.buffInstantActionType 
3.etc 
7/23/2010 v.0.6 
1.Added new grammar validation rules 
1.Any - Just validates there is a value 
2.SoundFile?? - Validates the referenced file and extension exists 
3.Color - Validates the color is a RGBA value 
4.Position - Validates the position matches #, #, ... 
2.Added brushes.xml - validates all .brushes files 
1.common.xml changes 
1.Added condition canAlwaysFire for fireConstraintType 
2.Added missing force element_rule 
2.Added constants.xml - validates Gameplay.constants 
3.Added help support to grammar definitions 
4.Added missing FALSE condition for hasWeaponLevels in frigate.xml 
5.Added galaxy_scenario_def.xml - validates .galaxyScenarioDef files 
4.Added missing validation rules to pip_cloud.xml 
5.Added missing validation rules to quest.xml 
6.Added sound_data.xml - validates .sounddata files 
1.Added string_info.xml - validates .str files 
8.Build/Nature 
1.Added support for files other than .entity 
1.Added new preference for sins installation directory 
2.Allows for binary file references to be located such as .ogg 
9.Entity Definition View 
Added support for files other than .entity 1.Entity Parser 
1.Fixes issue when tabs are used instead of spaces between keyword and value 
2.Adds validation support for Any, SoundFile??, Color and Position 
3.Fixes issue where an error was not reported at the end of the file 
4.Adds references to field definition handler - used for wiki generation 
2.WikiDefinitionBuilder?? 
1.Adds new functionality to generate dynamic wiki pages based on definition xmls 
7/14/2010 v.0.5 
1.Added custom perspective for Sins of a Solar Empire. 
2.Added custom icon for Sins of a Solar Empire files. 
3.Added text editor extensions to automatically map str, entity, constants and galaxyScenarioDef to open in the Eclipse Text Editor. 
4.Added support for placing mod files in sub-directories in the Eclipse project. 
For example path?/mod/GameInfo?, path?/mod/String, etc. 

6/21/2010 v.0.4 
1.Added support for projects that exist outside of the workspace. Allows a project to be created directly in the mod directories. 
2.Added Entity Definition View 
3.Added new Sose preferences for configuring reference case validation, decimal and integer validations. 

http://dl.dropbox.com/u/5790092/SinsTools/soseplugin_v.0.3.zip (6/10)
Missing allegianceDecreasePerRoundTrip

http://dl.dropbox.com/u/5790092/SinsTools/soseplugin_v.0.2.zip (6/10)
Minor enhancements only

http://dl.dropbox.com/u/5790092/soseplugin_v.0.1.zip
Eclipse plugin tool adding visual Entity level editor support

Ant Tools:

http://dl.dropbox.com/u/5790092/SinsTools/sins_build_tools_v.0.5.jar.7z (6/10)
Missing allegianceDecreasePerRoundTrip

http://dl.dropbox.com/u/5790092/SinsTools/sins_build_tools_v.0.4.jar.7z 
Integrated with new EntityParser
Definition files now stored in jar file

http://dl.dropbox.com/u/5790092/sins_build_tools_v0.3.7z (4/29)
Minor improvements to error checking and line number reporting.
Implemented entity_definitions.xsd for definition language.
Modified all *.xml to use entity_definitions.xsd

http://dl.dropbox.com/u/5790092/sins_build_tools_v0.2.7z (4/22)
Adds support for ignoring case when validating Sounds, Explosions, and Brush entries.
Adds missing diplomacy buffs for credits, relationshipIncrease, planetHealthRestoreRate and allegianceRestoreRate.
Adds diplomacy missions.
Adds pirate armorType (common.xml).
Adds missing diplomacy buffInstantActionTypes, buffOverTimeActionTypes, buffEntityModifierTypes, modifierTypes (common.xml).
Adds missing diplomacy constraint in (common.xml).
Adds missing diplomacy entries in (player.xml).
Adds PactBonus to ResearchField validation in (research_subject.xml).
Adds missing pact bonuses to allianceType (research_subject.xml).
Adds AbilityDLevel to propertyType validation (star_base_upgrade.xml).

alpha release dated 04/22/2010 (v0.1)
http://dl.dropbox.com/u/5790092/sins_build_tools_v0.1.7z

Ant Tasks Supported:
sose.tools.BrushGenerator.java
sose.tools.EntityValidator.java
sose.tools.ManifestGenerator.java

System requirements: 

Ant
Java 1.6
Diplomacy Full Reference Files (Optional)
Entrenchment Full Reference Files
Vanilla Full Reference Files
(For full reference files: http://forums.sinsofasolarempire.com/378266)

