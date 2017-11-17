 * [[Buff| Buff]]
   * [[onReapplyDuplicateType|onReapplyDuplicateType]]: [[Enumeration]]
   * [[buffStackingLimitType|buffStackingLimitType]]: [[Enumeration]]
   * stackingLimit: [[Integer]]
   * allowFirstSpawnerToStack: [[Boolean]]
   * [[buffExclusivityForAIType|buffExclusivityForAIType]]: [[Enumeration]]
   * isInterruptable: [[Boolean]]
   * isChannelling: [[Boolean]]
   * numInstantActions: [[Iteration]]
     * [[instantAction| instantAction]]
       * [[buffInstantActionType|buffInstantActionType]]: [[Condition]]
   * numPeriodicActions: [[Iteration]]
     * [[periodicAction| periodicAction]]
       * [[actionCountType|actionCountType]]: [[Condition]]
       * [[actionIntervalTime|GenericLevel]]: [[GenericLevel]]
       * [[buffInstantActionType|buffInstantActionType]]: [[Condition]]
   * numOverTimeActions: [[Iteration]]
     * [[overTimeAction| overTimeAction]]
       * [[buffOverTimeActionType|buffOverTimeActionType]]: [[Condition]]
   * numEntityModifiers: [[Iteration]]
     * [[entityModifier| entityModifier]]
       * [[buffEntityModifierType|buffEntityModifierType]]: [[Enumeration]]
       * [[value|GenericLevel]]: [[GenericLevel]]
   * numEntityBoolModifiers: [[Iteration]]
     * [[entityBoolModifier|entityBoolModifier]]: [[Enumeration]]
       * ActivelyBeingConstructed
       * Boarded
       * CanBeCaptured
       * CancelCannotBeDamaged
       * CancelPhaseOut
       * CannotBeColonized
       * CannotBeDamaged
       * CannotBeScuttled
       * DetectIncomingHyperspacingEntities
       * DisableAbilities
       * DisableAbilitiesUltimate
       * DisableAngularEngines
       * DisableConstruction
       * DisableFighterLaunch
       * DisableLinearEngines
       * DisableModuleFunctionality
       * DisablePhaseJump
       * DisableRegeneration
       * DisableWeapons
       * DisableWeaponsUltimate
       * ForceIsDamagedOverlayInHyperspace
       * HasDestabilizedHyperspaceEffect
       * HasShieldMeshEffect
       * HasTauntTarget
       * ImmuneToNonUltimateDisable
       * InstantBuildFighters
       * IsFlagship
       * IsIllusionShip
       * IsInNonFriendlyGravityWell
       * IsInvisible
       * IsPhaseGateEndPoint
       * IsResourceFocusActive
       * JumpBlockerImmune
       * NextAbilityAmplified
       * PhaseDodge
       * PhaseOut
       * ProxySensor
       * WeaponsDealNoDamage
   * numFinishConditions: [[Iteration]]
     * [[finishCondition| finishCondition]]
       * [[finishConditionType|finishConditionType]]: [[Condition]]

