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

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;

public class KickCommand extends AbstractCommand
{
    public KickCommand(EagleFactionsApartments plugin)
    {
        super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource source, CommandContext args) throws CommandException
    {
        if (!(source instanceof Player))
            throw new CommandException(EagleFactionsApartments.PLUGIN_ERROR_PREFIX.concat(Text.of("Only in-game players can use this command!")));

        final Player sourcePlayer = (Player)source;
        final Player targetPlayer = args.requireOne(Text.of("player"));

        final Faction sourcePlayerFaction = super.getPlugin().getEagleFactions().getFactionLogic().getFactionByPlayerUUID(sourcePlayer.getUniqueId())
                .orElseThrow(() -> new CommandException(EagleFactionsApartments.PLUGIN_ERROR_PREFIX.concat(Text.of("You must be in faction in order to use this command!"))));

        if (!sourcePlayer.getUniqueId().equals(sourcePlayerFaction.getLeader()))
            throw new CommandException(EagleFactionsApartments.PLUGIN_ERROR_PREFIX.concat(Text.of("You must be the faction leader to do this!")));

        final Region region = super.getPlugin().getRegionManager().findAllForFaction(sourcePlayerFaction.getName()).stream().filter(x->targetPlayer.getUniqueId().equals(x.getRentBy())).findFirst()
                .orElseThrow(() -> new CommandException(EagleFactionsApartments.PLUGIN_ERROR_PREFIX.concat(Text.of("This player is not renting any region."))));

        returnMoneyForUnusedDay(targetPlayer, sourcePlayer, region.getRentExpirationDateTime(), region.getPricePerDay());

        region.setRentBy(null);
        region.setRentExpiryDateTime(null);

        super.getPlugin().getRegionManager().save(region);

        sourcePlayer.sendMessage(EagleFactionsApartments.PLUGIN_PREFIX.concat(Text.of("Successfully kicked player " + targetPlayer.getName() + " from region " + region.getName())));
        return CommandResult.success();
    }

    private void returnMoneyForUnusedDay(final Player kickedPlayer, final Player leaderPlayer, final Instant rentExpiryDate, final float pricePerDay) throws CommandException
    {
        final UniqueAccount kickedPlayerAccount = super.getPlugin().getEconomyService().getOrCreateAccount(kickedPlayer.getUniqueId())
                .orElseThrow(() -> new CommandException(EagleFactionsApartments.PLUGIN_ERROR_PREFIX.concat(Text.of("Could not get player's account! Player name = " + kickedPlayer.getName()))));

        final UniqueAccount leaderPlayerAccount = super.getPlugin().getEconomyService().getOrCreateAccount(leaderPlayer.getUniqueId())
            .orElseThrow(() -> new CommandException(EagleFactionsApartments.PLUGIN_ERROR_PREFIX.concat(Text.of("Could not get player's account! Player name = " + kickedPlayer.getName()))));

        final Instant instant = Instant.now();
        final Duration duration = Duration.between(instant, rentExpiryDate);

        final BigDecimal withdrawMoney = leaderPlayerAccount.withdraw(super.getPlugin().getEconomyService().getDefaultCurrency(), BigDecimal.valueOf(pricePerDay * duration.toDays()), Cause.builder().append(leaderPlayer).append(super.getPlugin()).build(Sponge.getCauseStackManager().getCurrentContext())).getAmount();
        kickedPlayerAccount.deposit(super.getPlugin().getEconomyService().getDefaultCurrency(), withdrawMoney, Cause.builder().append(111).append(super.getPlugin()).build(Sponge.getCauseStackManager().getCurrentContext())).getAmount();
    }
}
