package net.minecraftforge.fml.event.lifecycle

import net.minecraftforge.fml.ModLoadingStage
import xyz.bluspring.kilt.loader.mod.ForgeMod

class FMLCommonSetupEvent(mod: ForgeMod?, stage: ModLoadingStage?) : ParallelDispatchEvent(mod, stage) {
    constructor() : this(null, null)

    private fun littleFunkyWorkaround() {
        throw IllegalStateException("You should not be able to access this!")
    }
}