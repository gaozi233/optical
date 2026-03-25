package net.lpcamors.optical.network;

import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;

import net.lpcamors.optical.CreateOptical;
import net.lpcamors.optical.blocks.hologram_source.HologramSourceBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

public class ConfigureHologramSourcePacket extends BlockEntityConfigurationPacket<HologramSourceBlockEntity> {

    private int mode, angle, angleVelocity;

    public ConfigureHologramSourcePacket(FriendlyByteBuf buffer) {
        super(buffer);
    }

    public ConfigureHologramSourcePacket(BlockPos pos, int mode, int angle, int angleVelocity) {
        super(pos);
        this.mode = mode;
        this.angle = angle;
        this.angleVelocity = angleVelocity;
    }

    @Override
    protected void writeSettings(FriendlyByteBuf buffer) {
        buffer.writeInt(mode);
        buffer.writeInt(angle);
        buffer.writeInt(angleVelocity);
    }

    @Override
    protected void readSettings(FriendlyByteBuf buffer) {
        this.mode = buffer.readInt();
        this.angle = buffer.readInt();
        this.angleVelocity = buffer.readInt();
    }

    @Override
    protected void applySettings(HologramSourceBlockEntity be) {
        try {
            HologramSourceBlockEntity controller = be.getController();
            controller.getProfile().displayMode = HologramSourceBlockEntity.Mode.values()[this.mode
                    % HologramSourceBlockEntity.Mode.values().length];
            controller.getProfile().fixedAngle = this.angle;
            controller.getProfile().angleVelocity = this.angleVelocity;
            be.sendData();
        } catch (Exception ex) {
            CreateOptical.LOGGER.error("Unable to send data to server in " + be.toString());
        }

    }
}
