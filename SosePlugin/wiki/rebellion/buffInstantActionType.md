[[buffInstantActionType|buffInstantActionType]]: [[Condition]]
   * AddPopulationToPlanet
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * [[populationAdded|GenericLevel]]: [[GenericLevel]]
   * ApplyArbitraryTargettedBuffToSelf
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * buffType: [[Entity]]
     * [[range|GenericLevel]]: [[GenericLevel]]
     * [[effectInfo| effectInfo]]
   * ApplyBuffToFirstSpawnerNoFilterNoRange
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * buffType: [[Entity]]
   * ApplyBuffToIncomingHyperspacers
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * buffType: [[Entity]]
     * [[targetFilter| targetFilter]]
     * [[effectInfo| effectInfo]]
   * ApplyBuffToLastSpawnerNoFilterNoRange
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * buffType: [[Entity]]
   * ApplyBuffToLastSpawnerWithTravelNoFilterNoRange
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * buffType: [[Entity]]
     * travelSpeed: [[Decimal]]
     * [[hasWeaponEffects|hasWeaponEffects]]: [[Condition]]
   * ApplyBuffToLocalOrbitBody
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * buffType: [[Entity]]
     * [[targetFilter| targetFilter]]
     * [[effectInfo| effectInfo]]
   * ApplyBuffToLocalOrbitBodyWithTravel
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * buffType: [[Entity]]
     * [[targetFilter| targetFilter]]
     * travelSpeed: [[Decimal]]
     * [[hasWeaponEffects|hasWeaponEffects]]: [[Condition]]
   * ApplyBuffToSelf
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * buffType: [[Entity]]
     * [[effectInfo| effectInfo]]
   * ApplyBuffToSelfWithFilter
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * buffType: [[Entity]]
     * [[targetFilter| targetFilter]]
   * ApplyBuffToTarget
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * buffType: [[Entity]]
     * [[targetFilter| targetFilter]]
     * [[range|GenericLevel]]: [[GenericLevel]]
     * [[effectInfo| effectInfo]]
   * ApplyBuffToTargetNoFilterNoRange
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * buffType: [[Entity]]
   * ApplyBuffToTargetNoRange
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * buffType: [[Entity]]
     * [[targetFilter| targetFilter]]
     * [[effectInfo| effectInfo]]
   * ApplyBuffToTargetOnWeaponFired
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * buffType: [[Entity]]
     * [[targetFilter| targetFilter]]
   * ApplyBuffToTargetWithEntryVehicles
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * buffType: [[Entity]]
     * [[targetFilter| targetFilter]]
     * [[range|GenericLevel]]: [[GenericLevel]]
     * entryVehicleType: [[Entity]]
     * [[numEntryVehicles|GenericLevel]]: [[GenericLevel]]
     * travelTime: [[Decimal]]
     * [[entryVehicleLaunchInfo|effectInfo]]: [[effectInfo]]
   * ApplyBuffToTargetWithTravel
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * buffType: [[Entity]]
     * [[targetFilter| targetFilter]]
     * [[range|GenericLevel]]: [[GenericLevel]]
     * travelSpeed: [[Decimal]]
     * [[hasWeaponEffects|hasWeaponEffects]]: [[Condition]]
   * ApplyBuffToTargetWithTravelNoFilterNoRange
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * buffType: [[Entity]]
     * travelSpeed: [[Decimal]]
     * [[hasWeaponEffects|hasWeaponEffects]]: [[Condition]]
   * ApplyBuffToTargetsAtAdjacentOrbitBodies
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * buffType: [[Entity]]
     * [[targetFilter| targetFilter]]
     * [[maxTargetCount|GenericLevel]]: [[GenericLevel]]
     * [[effectInfo| effectInfo]]
   * ApplyBuffToTargetsAtOrbitBody
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * buffType: [[Entity]]
     * [[targetFilter| targetFilter]]
     * [[maxTargetCount|GenericLevel]]: [[GenericLevel]]
     * [[effectInfo| effectInfo]]
   * ApplyBuffToTargetsInColumn
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * buffType: [[Entity]]
     * [[targetFilter| targetFilter]]
     * [[columnRadius|GenericLevel]]: [[GenericLevel]]
     * [[maxTargetCount|GenericLevel]]: [[GenericLevel]]
     * [[effectInfo| effectInfo]]
   * ApplyBuffToTargetsInDirectionalCone
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * buffType: [[Entity]]
     * [[targetFilter| targetFilter]]
     * [[weaponBank|weaponBank]]: [[Enumeration]]
       * FRONT
       * BACK
       * LEFT
       * RIGHT
     * [[range|GenericLevel]]: [[GenericLevel]]
     * [[coneAngle|GenericLevel]]: [[GenericLevel]]
     * [[maxTargetCount|GenericLevel]]: [[GenericLevel]]
     * [[effectInfo| effectInfo]]
   * ApplyBuffToTargetsInRadius
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * buffType: [[Entity]]
     * [[targetFilter| targetFilter]]
     * [[range|GenericLevel]]: [[GenericLevel]]
     * [[maxTargetCount|GenericLevel]]: [[GenericLevel]]
     * [[effectInfo| effectInfo]]
   * ApplyBuffToTargetsInRadiusOfTargetWithTravel
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * buffType: [[Entity]]
     * [[targetFilter| targetFilter]]
     * [[range|GenericLevel]]: [[GenericLevel]]
     * [[maxTargetCount|GenericLevel]]: [[GenericLevel]]
     * travelSpeed: [[Decimal]]
     * effectStaggerDelay: [[Decimal]]
     * [[hasWeaponEffects|hasWeaponEffects]]: [[Condition]]
   * ApplyBuffToTargetsInRadiusWithChainTravel
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * buffType: [[Entity]]
     * [[targetFilter| targetFilter]]
     * [[range|GenericLevel]]: [[GenericLevel]]
     * [[maxTargetCount|GenericLevel]]: [[GenericLevel]]
     * chainDelay: [[Decimal]]
     * [[hasWeaponEffects|hasWeaponEffects]]: [[Condition]]
   * ApplyBuffToTargetsInRadiusWithTravel
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * buffType: [[Entity]]
     * [[targetFilter| targetFilter]]
     * [[range|GenericLevel]]: [[GenericLevel]]
     * [[maxTargetCount|GenericLevel]]: [[GenericLevel]]
     * [[effectInfo| effectInfo]]
     * travelSpeed: [[Decimal]]
     * effectStaggerDelay: [[Decimal]]
     * [[hasWeaponEffects|hasWeaponEffects]]: [[Condition]]
   * ApplyBuffToTargetsLinked
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * buffType: [[Entity]]
     * [[targetFilter| targetFilter]]
     * [[effectInfo| effectInfo]]
   * ApplyForceFromSpawner
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * force: [[Decimal]]
   * ApplyImpulseFromSpawner
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * impulse: [[Decimal]]
   * ApplyImpulseInSpawnerDirection
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * impulse: [[Decimal]]
   * ApplyOrRemoveBuffToSelf
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * buffType: [[Entity]]
     * [[effectInfo| effectInfo]]
   * ApplyRelationshipModifierToOwnerOfPlanetInCurrentGravityWell
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * [[relationshipIncrease|GenericLevel]]: [[GenericLevel]]
   * ApplyTargettedBuffToSelf
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * buffType: [[Entity]]
     * [[targetFilter| targetFilter]]
     * [[range|GenericLevel]]: [[GenericLevel]]
     * [[effectInfo| effectInfo]]
   * ApplyTargettedBuffToSelfNoFilterNoRange
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * buffType: [[Entity]]
     * [[effectInfo| effectInfo]]
   * ApplyTargettedBuffToSelfNoRange
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * buffType: [[Entity]]
     * [[targetFilter| targetFilter]]
     * [[effectInfo| effectInfo]]
   * AttractDebris
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * [[range|GenericLevel]]: [[GenericLevel]]
     * travelSpeed: [[Decimal]]
   * ChangePlayerIndexToFirstSpawner
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * isPermanent: [[Boolean]]
     * failIfNotEnoughShipSlots: [[Boolean]]
     * experiencePercentageToAward: [[Decimal]]
   * ChangePlayerIndexToNeutral
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
   * ClearRecordedDamage
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
   * ColonizePlanet
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * [[targetFilter| targetFilter]]
     * [[range|GenericLevel]]: [[GenericLevel]]
     * delayUntilColonization: [[Decimal]]
     * entryVehicleType: [[Entity]]
     * [[numEntryVehicles|GenericLevel]]: [[GenericLevel]]
     * travelTime: [[Decimal]]
     * [[entryVehicleLaunchInfo|effectInfo]]: [[effectInfo]]
     * afterColonizeBuffType: [[Entity]]
   * ConvertDamageToAntiMatter
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * [[antiMatterFromDamagePerc|GenericLevel]]: [[GenericLevel]]
   * ConvertFrigateToResources
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * [[percentageOfCost|GenericLevel]]: [[GenericLevel]]
   * ConvertNearbyDebrisToHull
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * [[range|GenericLevel]]: [[GenericLevel]]
     * [[hullPerDebris|GenericLevel]]: [[GenericLevel]]
     * [[debrisCleanupSounds| debrisCleanupSounds]]
       * soundCount: [[Iteration]]
         * sound: [[Sound]]
   * ConvertSquadMembersToMines
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * spaceMineType: [[Entity]]
     * [[expiryTime|GenericLevel]]: [[GenericLevel]]
   * CreateCannonShell
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * cannonShellType: [[Entity]]
     * [[effectInfo| effectInfo]]
   * CreateClonedFrigate
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * [[numFrigates|GenericLevel]]: [[GenericLevel]]
     * impulse: [[Decimal]]
     * [[expiryTime|GenericLevel]]: [[GenericLevel]]
     * spawnFrigateSoundID: [[Sound]]
   * CreateFrigate
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * frigateType: [[Entity]]
     * [[numFrigates|GenericLevel]]: [[GenericLevel]]
     * matchOwnerDamageState: [[Boolean]]
     * impulse: [[Decimal]]
     * [[expiryTime|GenericLevel]]: [[GenericLevel]]
     * spawnFrigateSoundID: [[Sound]]
     * postSpawnBuff: [[Entity]]
   * CreateFrigateAtArbitraryTarget
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * frigateType: [[Entity]]
     * [[numFrigates|GenericLevel]]: [[GenericLevel]]
     * [[expiryTime|GenericLevel]]: [[GenericLevel]]
     * spawnFrigateSoundID: [[Sound]]
     * [[range|GenericLevel]]: [[GenericLevel]]
   * CreateFrigateAtTarget
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * frigateType: [[Entity]]
     * [[numFrigates|GenericLevel]]: [[GenericLevel]]
     * impulse: [[Decimal]]
     * [[expiryTime|GenericLevel]]: [[GenericLevel]]
     * spawnFrigateSoundID: [[Sound]]
     * [[targetFilter| targetFilter]]
     * [[range|GenericLevel]]: [[GenericLevel]]
   * CreateIllusionFighters
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * [[numFighters|GenericLevel]]: [[GenericLevel]]
     * [[expiryTime|GenericLevel]]: [[GenericLevel]]
     * [[effectInfo| effectInfo]]
   * CreatePlanetModule
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * planetModuleType: [[Entity]]
     * spawnPlanetModuleSoundID: [[Sound]]
   * CreateSpaceMine
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * spaceMineType: [[Entity]]
     * [[numMines|GenericLevel]]: [[GenericLevel]]
     * impulse: [[Decimal]]
     * angleVariance: [[Decimal]]
     * [[expiryTime|GenericLevel]]: [[GenericLevel]]
     * spawnMineSoundID: [[Sound]]
   * CreateSquad
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * squadType: [[Entity]]
     * [[numSquads|GenericLevel]]: [[GenericLevel]]
     * [[expiryTime|GenericLevel]]: [[GenericLevel]]
     * spawnSquadSoundID: [[Sound]]
   * CreateStarBase
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * starBaseType: [[Entity]]
     * [[placementRadius|GenericLevel]]: [[GenericLevel]]
   * DoAllegianceChangeToPlanet
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * [[allegianceChange|GenericLevel]]: [[GenericLevel]]
   * DoDamage
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * [[damage|GenericLevel]]: [[GenericLevel]]
     * [[damageAffectType|damageAffectType]]: [[Enumeration]]
     * [[damageType|damageType]]: [[Enumeration]]
     * isDamageShared: [[Boolean]]
   * DoDamagePerEntityInRadius
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * [[damagePerEntity|GenericLevel]]: [[GenericLevel]]
     * [[damageAffectType|damageAffectType]]: [[Enumeration]]
     * [[damageType|damageType]]: [[Enumeration]]
     * isDamageShared: [[Boolean]]
     * [[targetFilter| targetFilter]]
     * [[range|GenericLevel]]: [[GenericLevel]]
     * [[maxTargetCount|GenericLevel]]: [[GenericLevel]]
   * DoDamagePerLastSpawnerPopulation
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * [[damagePerPopulationPoint|GenericLevel]]: [[GenericLevel]]
     * [[damageAffectType|damageAffectType]]: [[Enumeration]]
     * [[damageType|damageType]]: [[Enumeration]]
     * isDamageShared: [[Boolean]]
   * DoDamagePercOfCurrentHull
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * [[hullDamagePerc|GenericLevel]]: [[GenericLevel]]
     * [[damageAffectType|damageAffectType]]: [[Enumeration]]
     * [[damageType|damageType]]: [[Enumeration]]
     * isDamageShared: [[Boolean]]
   * DoDamageToPlanet
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * [[damage|GenericLevel]]: [[GenericLevel]]
     * [[populationKilled|GenericLevel]]: [[GenericLevel]]
   * DoInterrupt
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
   * DoInterruptUltimate
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
   * Explore
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
   * ForceAttackersToRepickAttackTarget
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
   * GainAntimatterEqualToTarget
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
   * GainHullEqualToTarget
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
   * GainShieldEqualToTarget
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
   * GiveCreditsToPlayer
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * [[credits|GenericLevel]]: [[GenericLevel]]
     * [[effectInfo| effectInfo]]
   * GiveExperience
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * experienceToGive: [[Decimal]]
   * IncreaseOwnerAbilityLevel
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * abilityIndex: [[Decimal]]
     * [[abilityLevel|GenericLevel]]: [[GenericLevel]]
   * InitializeMovementTowardLastSpawner
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * minLinearSpeed: [[Decimal]]
     * maxLinearSpeed: [[Decimal]]
   * InitializeRandomMotion
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * minRandomAngularSpeed: [[Decimal]]
     * maxRandomAngularSpeed: [[Decimal]]
     * minRandomLinearSpeed: [[Decimal]]
     * maxRandomLinearSpeed: [[Decimal]]
   * MakeDead
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
   * MatchTargetVelocity
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
   * PlayAttachedEffect
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * [[effectInfo| effectInfo]]
   * PlayDetachedEffectsInRadius
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * [[effectInfo| effectInfo]]
     * [[range|GenericLevel]]: [[GenericLevel]]
     * numEffects: [[Integer]]
     * delayBetweenEffects: [[Decimal]]
   * PlayPersistantAttachedEffect
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * [[effectInfo| effectInfo]]
   * PlayPersistantBeamEffect
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * [[hasWeaponEffects|hasWeaponEffects]]: [[Condition]]
   * PropagateWeaponDamageReceivedToTargetsInRadius
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * [[targetFilter| targetFilter]]
     * [[range|GenericLevel]]: [[GenericLevel]]
     * [[maxTargetCount|GenericLevel]]: [[GenericLevel]]
   * RecordDamage
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
   * RemoveAntiMatter
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * [[antiMatter|GenericLevel]]: [[GenericLevel]]
   * RemoveAntiMatterPerc
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * [[antiMatterPerc|GenericLevel]]: [[GenericLevel]]
   * RemoveBuffOfType
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * buffTypeToRemove: [[Entity]]
   * ResetPhysicsState
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
   * RestoreAntiMatter
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * [[antiMatter|GenericLevel]]: [[GenericLevel]]
   * RestoreHullPoints
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * [[hull|GenericLevel]]: [[GenericLevel]]
   * RestoreHullPointsPerc
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * [[hullPerc|GenericLevel]]: [[GenericLevel]]
   * RestoreShieldPoints
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * [[shields|GenericLevel]]: [[GenericLevel]]
   * RestoreShieldPointsPerc
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * [[shieldPerc|GenericLevel]]: [[GenericLevel]]
   * ResurrectCapitalShip
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
   * RetaliateBounty
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * [[bountyAmount|GenericLevel]]: [[GenericLevel]]
   * RetaliateDamage
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * [[damageRetaliationPerc|GenericLevel]]: [[GenericLevel]]
   * RuinPlanet
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * [[targetFilter| targetFilter]]
     * [[range|GenericLevel]]: [[GenericLevel]]
     * afterRuinBuffType: [[Entity]]
   * SetTauntTargetToLastSpawner
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
   * SpawnResourceExtractors
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * [[numExtractors|GenericLevel]]: [[GenericLevel]]
   * SpawnShipsAtPlanet
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * spawnShipsLevelCount: [[Iteration]]
       * [[spawnShips| spawnShips]]
     * spawnShipsArrivalDelayTime: [[Decimal]]
     * [[spawnShipsHyperspaceSpawnType|spawnShipsHyperspaceSpawnType]]: [[Enumeration]]
   * StealAntiMatterForFirstSpawner
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * [[antiMatter|GenericLevel]]: [[GenericLevel]]
   * StealResources
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * [[resourceToSteal|resourceToSteal]]: [[Enumeration]]
     * [[resourceAmount|GenericLevel]]: [[GenericLevel]]
   * TeleportTowardsArbitraryTarget
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
   * TeleportTowardsMoveTarget
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * teleportDistance: [[Decimal]]
   * TeleportTowardsTarget
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * teleportStopOffset: [[Decimal]]
   * TiltUpVectorInTargetDirection
     * [[instantActionTriggerType|instantActionTriggerType]]: [[Condition]]
     * angleOffset: [[Decimal]]
