// TRACKED HASH: cfca572d6c2e46ca49cb8ffbdda7f12c0d1c4d30
package xyz.bluspring.kilt.forgeinjects.world.level.block;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.IdMapper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.extensions.common.IClientBlockExtensions;
import net.minecraftforge.common.extensions.IForgeBlock;
import net.minecraftforge.common.extensions.IForgeBlockState;
import net.minecraftforge.registries.GameData;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import xyz.bluspring.kilt.injections.client.renderer.RenderPropertiesInjection;

@Mixin(Block.class)
public abstract class BlockInject implements IForgeBlock, RenderPropertiesInjection<IClientBlockExtensions> {
    @Shadow @Final @Mutable
    public static IdMapper<BlockState> BLOCK_STATE_REGISTRY;
    @Unique
    private Object renderProperties;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void kilt$useGameDataStateIdMap(CallbackInfo ci) {
        BLOCK_STATE_REGISTRY = GameData.getBlockStateIDMap();
    }

    @Inject(at = @At("TAIL"), method = "<init>")
    public void kilt$initClient(BlockBehaviour.Properties properties, CallbackInfo ci) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            this.initializeClient((extensionProperties) -> {
                renderProperties = extensionProperties;
            });
        }
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;skipRendering(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;)Z", shift = At.Shift.BEFORE), method = "shouldRenderFace", locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private static void kilt$handleRenderFace(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Direction direction, BlockPos blockPos2, CallbackInfoReturnable<Boolean> cir, BlockState blockState2) {
        if (blockState.skipRendering(blockState2, direction))
            cir.setReturnValue(false);
        else if (((IForgeBlockState) blockState).supportsExternalFaceHiding() && ((IForgeBlockState) blockState2).hidesNeighborFace(blockGetter, blockPos, blockState, direction))
            cir.setReturnValue(false);
    }

    @WrapOperation(at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/Level;isClientSide:Z"), method = "popResource(Lnet/minecraft/world/level/Level;Ljava/util/function/Supplier;Lnet/minecraft/world/item/ItemStack;)V")
    private static boolean kilt$checkRestoringBlockSnapshots(Level instance, Operation<Boolean> original) {
        // TODO: actually implement this
        return original.call(instance); //&& !((IForgeLevel) instance).restoringBlockSnapshots;
    }

    @Override
    public Object getRenderPropertiesInternal() {
        return renderProperties;
    }
}