package io.github.aquerr.efapartments.command;

import io.github.aquerr.efapartments.EagleFactionsApartments;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.entity.MainPlayerInventory;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import java.util.Arrays;

public class WandCommand extends AbstractCommand
{
    public WandCommand(EagleFactionsApartments plugin)
    {
        super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource source, CommandContext args) throws CommandException
    {
        if (!(source instanceof Player))
            throw new CommandException(Text.of(EagleFactionsApartments.PLUGIN_ERROR_PREFIX, "Only in-game players can use this command!"));

        final Player player = (Player) source;
        final World world = player.getWorld();

        final MainPlayerInventory mainPlayerInventory = player.getInventory().query(QueryOperationTypes.INVENTORY_TYPE.of(MainPlayerInventory.class));
        mainPlayerInventory.offer(getEFAWand());

        world.spawnParticles(ParticleEffect.builder().quantity(10).type(ParticleTypes.FIREWORKS_SPARK).build(), player.getPosition(), 1);
        world.playSound(SoundTypes.ENTITY_EXPERIENCE_ORB_PICKUP, player.getPosition(), 10, 5);

        return CommandResult.success();
    }

    private ItemStack getEFAWand()
    {
        return ItemStack.builder()
                .itemType(ItemTypes.GOLDEN_AXE)
                .quantity(1)
                .add(Keys.DISPLAY_NAME, Text.of(TextColors.AQUA, "EF-A Wand"))
                .add(Keys.ITEM_LORE, Arrays.asList(Text.of(TextColors.GOLD, "Left click", TextColors.RESET, " to select first corner"),
                        Text.of(TextColors.GOLD, "Right click", TextColors.RESET, " to select second corner")))
                .build();
    }
}
