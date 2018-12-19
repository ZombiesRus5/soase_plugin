[[buffOverTimeActionType|buffOverTimeActionType]] : [[Condition]]
   * DoDamage
     * [[damageRate|GenericLevel]] : [[GenericLevel]]
     * [[damageAffectType|damageAffectType]] : [[Enumeration]]
     * [[damageType|damageType]] : [[Enumeration]]
     * isDamageShared : [[Boolean]]
   * DoDamageAndGiveResourceCostPerc
     * [[resourceGainPerc|GenericLevel]] : [[GenericLevel]]
     * [[damageRate|GenericLevel]] : [[GenericLevel]]
   * DoDamageToPlanet
     * [[damageRate|GenericLevel]] : [[GenericLevel]]
     * [[populationKillRate|GenericLevel]] : [[GenericLevel]]
   * DoDamageToTarget
     * [[damageRate|GenericLevel]] : [[GenericLevel]]
     * [[damageAffectType|damageAffectType]] : [[Enumeration]]
     * [[damageType|damageType]] : [[Enumeration]]
     * isDamageShared : [[Boolean]]
   * DoModuleConstruction
     * [[buildRate|GenericLevel]] : [[GenericLevel]]
     * [[moduleCostFraction|GenericLevel]] : [[GenericLevel]]
   * DoPercOfMaxDamageToPlanet
     * [[damageRate|GenericLevel]] : [[GenericLevel]]
     * [[populationKillRate|GenericLevel]] : [[GenericLevel]]
   * DrainAntiMatter
     * [[drainAntiMatterRate|GenericLevel]] : [[GenericLevel]]
   * DrainAntiMatterAndDoDamage
     * [[drainAntiMatterRate|GenericLevel]] : [[GenericLevel]]
     * [[damageRate|GenericLevel]] : [[GenericLevel]]
     * isDamageShared : [[Boolean]]
   * EarnCredits
     * [[earnRate|GenericLevel]] : [[GenericLevel]]
   * EarnResources
     * [[earnRate|GenericLevel]] : [[GenericLevel]]
   * Magnetize
     * [[targetFilter| targetFilter]]
     * [[range|GenericLevel]] : [[GenericLevel]]
     * [[maxNumTargets|GenericLevel]] : [[GenericLevel]]
     * [[damagePerImpact|GenericLevel]] : [[GenericLevel]]
     * pullForceMagnitude : [[Decimal]]
   * RestoreAllegiance
     * [[allegianceRestoreRate|GenericLevel]] : [[GenericLevel]]
   * RestoreAntiMatter
     * [[antiMatterRestoreRate|GenericLevel]] : [[GenericLevel]]
   * RestoreHull
     * [[hullRestoreRate|GenericLevel]] : [[GenericLevel]]
   * RestorePlanetHealth
     * [[planetHealthRestoreRate|GenericLevel]] : [[GenericLevel]]
   * RestoreShields
     * [[shieldRestoreRate|GenericLevel]] : [[GenericLevel]]
   * StealPlanetProduction
     * [[stolenPlanetProductionPerc|GenericLevel]] : [[GenericLevel]]
   * StealPopulationFromAdjacentPlanets
     * [[populationPercStealRate|GenericLevel]] : [[GenericLevel]]
     * [[damageRate|GenericLevel]] : [[GenericLevel]]
