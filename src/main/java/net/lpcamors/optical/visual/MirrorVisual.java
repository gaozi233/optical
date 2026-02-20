package net.lpcamors.optical.visual;

import java.util.function.Consumer;

import org.joml.Quaternionf;

import com.simibubi.create.content.kinetics.base.ShaftVisual;

import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import net.lpcamors.optical.COPartialModels;
import net.lpcamors.optical.blocks.encased_mirror.EncasedMirrorBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class MirrorVisual extends ShaftVisual<EncasedMirrorBlockEntity> implements SimpleDynamicVisual {

    protected final TransformedInstance mirror;
    public MirrorVisual(VisualizationContext context, EncasedMirrorBlockEntity blockEntity, float partialTick) {
        super(context, blockEntity, partialTick);

        mirror = instancerProvider()
                .instancer(InstanceTypes.TRANSFORMED,
                        Models.partial(COPartialModels.MIRROR))
                .createInstance();
        rotateMirror(partialTick);
    }
    public void rotateMirror(float pt){
        var facing = blockState.getValue(BlockStateProperties.FACING);
        float angle = blockEntity.getIndependentAngle(pt) * (float)Math.PI / 180F;
        if(facing.getAxis().isHorizontal()) angle -= (float) (Math.PI / 2F);

        mirror.setIdentityTransform()
                .translate(getVisualPosition())
                .center()
                .rotate(angle, Direction.get(Direction.AxisDirection.POSITIVE, facing.getAxis()))
                .rotate(new Quaternionf().rotateTo(0, 1, 0, facing.getStepX(), facing.getStepY(), facing.getStepZ()))
                .uncenter()
                .setChanged();
    }

    @Override
    public void beginFrame(DynamicVisual.Context ctx) {
        rotateMirror(ctx.partialTick());
    }

    @Override
    public void collectCrumblingInstances(Consumer<dev.engine_room.flywheel.api.instance.Instance>   consumer) {
        super.collectCrumblingInstances(consumer);
        consumer.accept(this.mirror);
    }

    @Override
    public void updateLight(float v) {
        super.updateLight(v);
        relight(mirror);
    }

    @Override
    protected void _delete() {
        super._delete();
        this.mirror.delete();
    }
}
