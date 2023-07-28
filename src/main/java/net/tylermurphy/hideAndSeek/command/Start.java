package net.tylermurphy.hideAndSeek.command;

import net.tylermurphy.hideAndSeek.Main;
import net.tylermurphy.hideAndSeek.command.util.ICommand;
import net.tylermurphy.hideAndSeek.game.util.Status;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static net.tylermurphy.hideAndSeek.configuration.Config.errorPrefix;
import static net.tylermurphy.hideAndSeek.configuration.Config.minPlayers;
import static net.tylermurphy.hideAndSeek.configuration.Localization.message;

public class Start implements ICommand {

	public void execute(Player sender, String[] args) {
		if (Main.getInstance().getGame().checkCurrentMap()) {
			sender.sendMessage(errorPrefix + message("GAME_SETUP"));
			return;
		}
		if (Main.getInstance().getGame().getStatus() != Status.STANDBY) {
			sender.sendMessage(errorPrefix + message("GAME_INPROGRESS"));
			return;
		}
		if (!Main.getInstance().getBoard().contains(sender)) {
			sender.sendMessage(errorPrefix + message("GAME_NOT_INGAME"));
			return;
		}
		if (Main.getInstance().getBoard().size() < minPlayers) {
			sender.sendMessage(errorPrefix + message("START_MIN_PLAYERS").addAmount(minPlayers));
			return;
		}
	
		if (args.length < 1) {
			Main.getInstance().getGame().start();
			return;
		};

        List<Player> initialSeekers = new ArrayList<>(args.length);
        for (int i = 0; i < args.length; i++) {
            Player seeker = Bukkit.getPlayer(args[i]);
		    if (seeker == null || !Main.getInstance().getBoard().contains(seeker) || initialSeekers.contains(seeker)) {
		    	sender.sendMessage(errorPrefix + message("START_INVALID_NAME").addPlayer(args[i]));
		    	return;
            }
        }
		Main.getInstance().getGame().start(initialSeekers);
	}
	
	public String getLabel() {
		return "start";
	}
	
	public String getUsage() {
		return "<*seekers...>";
	}

	public String getDescription() {
		return "Starts the game either with a random set of seekers or a chosen list";
	}

	public List<String> autoComplete(@NotNull String parameter, @NotNull String typed) {
		return Main.getInstance().getBoard().getPlayers().stream().map(Player::getName).collect(Collectors.toList());
	}

}
