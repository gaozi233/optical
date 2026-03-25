package net.lpcamors.optical.network;

import com.simibubi.create.foundation.networking.BlockEntityDataPacket;

import net.lpcamors.optical.blocks.optical_source.GenericOpticalSourceBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public class ConfigureOpticalSourcePacket extends BlockEntityDataPacket<GenericOpticalSourceBlockEntity> {

    private CompoundTag tag;

    public ConfigureOpticalSourcePacket(GenericOpticalSourceBlockEntity be) {
        super(be.getBlockPos());
        tag = new CompoundTag();
        be.writeSafe(tag);

    }

    public ConfigureOpticalSourcePacket(FriendlyByteBuf buffer) {
        super(buffer);
        tag = buffer.readNbt();
    }

    @Override
    protected void writeData(FriendlyByteBuf buffer) {
        buffer.writeNbt(this.tag);
    }

    @Override
    protected void handlePacket(GenericOpticalSourceBlockEntity blockEntity) {
        blockEntity.load(tag);
    }

}
