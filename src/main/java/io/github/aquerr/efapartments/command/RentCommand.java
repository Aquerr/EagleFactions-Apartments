package io.github.aquerr.efapartments.command;

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

public class RentCommand extends AbstractCommand
{
    public RentCommand(EagleFactionsApartments plugin)
    {
        super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource source, CommandContext args) throws CommandException
    {
        if (!(source instanceof Player))
            throw new CommandException(EagleFactionsApartments.PLUGIN_ERROR_PREFIX.concat(Text.of("Only in-game players can use this command!")));

        final Player player = (Player)source;
        final Region region = args.requireOne(Text.of("region"));
        final Duration duration = args.requireOne(Text.of("duration"));

        final Instant rentEndDate = Instant.now().plus(duration.getSeconds(), ChronoUnit.SECONDS);

        //Try get money from the player.
        final Optional<UniqueAccount> optionalUniqueAccount = super.getPlugin().getEconomyService().getOrCreateAccount(player.getUniqueId());
        if (!optionalUniqueAccount.isPresent())
            throw new CommandException(EagleFactionsApartments.PLUGIN_ERROR_PREFIX.concat(Text.of("Could not get player's account! Player name = " + player.getName())));

        final UniqueAccount uniqueAccount = optionalUniqueAccount.get();
        if (uniqueAccount.getBalance(super.getPlugin().getEconomyService().getDefaultCurrency()).floatValue() >= region.getPricePerDay() * duration.toDays())
        {
            uniqueAccount.withdraw(super.getPlugin().getEconomyService().getDefaultCurrency(), BigDecimal.valueOf(region.getPricePerDay() * duration.toDays()), Cause.builder().append(player).append(super.getPlugin()).build(Sponge.getCauseStackManager().getCurrentContext()));
            region.setRentBy(player.getUniqueId());
            region.setRentExpiryDateTime(rentEndDate);
            super.getPlugin().getRegionManager().save(region);
            player.sendMessage(Text.of(EagleFactionsApartments.PLUGIN_PREFIX, "You rent the " + region.getName() + " region!"));
        }
        else
        {
            throw new CommandException(EagleFactionsApartments.PLUGIN_ERROR_PREFIX.concat(Text.of(TextColors.RED, "You don't have enough money to rent this region.")));
        }
        return CommandResult.success();
    }
}
