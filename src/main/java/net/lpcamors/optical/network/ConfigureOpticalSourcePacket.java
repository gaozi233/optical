package net.lpcamors.optical.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.simibubi.create.foundation.networking.BlockEntityDataPacket;

import net.createmod.catnip.nbt.NBTHelper;
import net.lpcamors.optical.blocks.optical_source.BeamHelper.BeamProperties;
import net.lpcamors.optical.blocks.optical_source.GenericOpticalSourceBlockEntity;
import net.lpcamors.optical.blocks.optical_source.GenericOpticalSourceBlockEntity.BeamSection;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;

public class ConfigureOpticalSourcePacket extends BlockEntityDataPacket<GenericOpticalSourceBlockEntity> {

    private List<BeamSection> sections;
    private Map<BlockPos, BeamProperties> activators;

    public ConfigureOpticalSourcePacket(GenericOpticalSourceBlockEntity be) {
        super(be.getBlockPos());
        this.sections = List.copyOf(be.sections);
        this.activators = Map.copyOf(be.activators);
    }

    public ConfigureOpticalSourcePacket(FriendlyByteBuf buffer) {
        super(buffer);

        ArrayList<BeamSection> list = new ArrayList<>();
        Map<BlockPos, BeamProperties> map = new HashMap<>();
        CompoundTag compound = buffer.readNbt();

        if (compound.contains("Sections")) {
            ListTag sections = compound.getList("Sections", CompoundTag.TAG_COMPOUND);
            sections.forEach(tag -> {
                BeamSection.read((CompoundTag) tag).ifPresent(list::add);
            });
            this.sections = List.copyOf(list);
        }
        if (compound.contains("Activators")) {
            ListTag activators = compound.getList("Activators", CompoundTag.TAG_COMPOUND);
            activators.forEach(tag -> {
                if (tag instanceof CompoundTag compoundTag) {
                    if (!compoundTag.contains("Pos"))
                        return;
                    BlockPos pos = new BlockPos(
                            NBTHelper.readVec3i(compoundTag.getList("Pos", CompoundTag.TAG_COMPOUND)));
                    Optional<BeamProperties> prop = BeamProperties.read(compoundTag);
                    if (prop.isPresent()) {
                        map.put(pos, prop.get());
                    }
                }
            });

            this.activators = Map.copyOf(map);
        }
    }

    @Override
    protected void writeData(FriendlyByteBuf buffer) {

        CompoundTag compound = new CompoundTag();

        ListTag sections = new ListTag();
        this.sections.forEach(sec -> {
            CompoundTag tag = new CompoundTag();
            BeamSection.write(tag, sec);
            sections.add(tag);
        });

        ListTag activators = new ListTag();
        this.activators.forEach((pos, prop) -> {
            CompoundTag tag = new CompoundTag();
            tag.put("Pos", NBTHelper.writeVec3i(pos));
            prop.write(tag);
            activators.add(tag);

        });

        compound.put("Sections", sections);
        compound.put("Activators", activators);

        buffer.writeNbt(compound);

    }

    @Override
    protected void handlePacket(GenericOpticalSourceBlockEntity blockEntity) {
        blockEntity.sections.clear();
        blockEntity.activators.clear();
        blockEntity.sections.addAll(this.sections);
        blockEntity.activators.putAll(this.activators);
        blockEntity.updateSections();
    }

}
