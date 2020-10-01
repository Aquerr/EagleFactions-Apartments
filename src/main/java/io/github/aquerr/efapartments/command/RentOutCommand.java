package io.github.aquerr.efapartments.command;

import io.github.aquerr.eaglefactions.api.entities.Faction;
import io.github.aquerr.efapartments.EagleFactionsApartments;
import io.github.aquerr.efapartments.model.Region;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

public class RentOutCommand extends AbstractCommand
{
    public RentOutCommand(EagleFactionsApartments plugin)
    {
        super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource source, CommandContext args) throws CommandException
    {
        if (!(source instanceof Player))
            throw new CommandException(EagleFactionsApartments.PLUGIN_ERROR_PREFIX.concat(Text.of("Only in-game players can use this command!")));

        final Player sourcePlayer = (Player)source;

        final Faction sourcePlayerFaction = super.getPlugin().getEagleFactions().getFactionLogic().getFactionByPlayerUUID(sourcePlayer.getUniqueId())
                .orElseThrow(() -> new CommandException(EagleFactionsApartments.PLUGIN_ERROR_PREFIX.concat(Text.of("You must be in faction in order to use this command!"))));

        if (!sourcePlayer.getUniqueId().equals(sourcePlayerFaction.getLeader()))
            throw new CommandException(EagleFactionsApartments.PLUGIN_ERROR_PREFIX.concat(Text.of("You must be the faction leader to do this!")));

        final Player targetPlayer = args.requireOne(Text.of("player"));
        final Region region = args.requireOne(Text.of("region"));
        final Duration duration = args.requireOne(Text.of("duration"));

        final Instant rentEndDate = Instant.now().plus(duration.getSeconds(), ChronoUnit.SECONDS);

        //Try get money from the player.
        final Optional<UniqueAccount> optionalUniqueAccount = super.getPlugin().getEconomyService().getOrCreateAccount(targetPlayer.getUniqueId());
        if (!optionalUniqueAccount.isPresent())
            throw new CommandException(EagleFactionsApartments.PLUGIN_ERROR_PREFIX.concat(Text.of("Could not get player's account! Player name = " + targetPlayer.getName())));

        final UniqueAccount uniqueAccount = optionalUniqueAccount.get();
        if (uniqueAccount.getBalance(super.getPlugin().getEconomyService().getDefaultCurrency()).floatValue() >= region.getPricePerDay() * duration.toDays())
        {
            uniqueAccount.withdraw(super.getPlugin().getEconomyService().getDefaultCurrency(), BigDecimal.valueOf(region.getPricePerDay() * duration.toDays()), Cause.builder().append(targetPlayer).append(super.getPlugin()).build(Sponge.getCauseStackManager().getCurrentContext()));
            region.setRentBy(targetPlayer.getUniqueId());
            region.setRentExpiryDateTime(rentEndDate);
            super.getPlugin().getRegionManager().save(region);
            sourcePlayer.sendMessage(Text.of(EagleFactionsApartments.PLUGIN_PREFIX, "You rent " + targetPlayer.getName() + " region " + region.getName() + "!"));
            targetPlayer.sendMessage(Text.of(EagleFactionsApartments.PLUGIN_PREFIX, sourcePlayer.getName() + " rent you region " + region.getName()));
        }
        else
        {
            throw new CommandException(EagleFactionsApartments.PLUGIN_ERROR_PREFIX.concat(Text.of(TextColors.RED, "You don't have enough money to rent this region.")));
        }
        return CommandResult.success();
    }
}
