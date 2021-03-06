package de.simonsator.partyandfriends.block.subcommands;

import de.simonsator.partyandfriends.api.friends.abstractcommands.FriendSubCommand;
import de.simonsator.partyandfriends.api.pafplayers.OnlinePAFPlayer;
import de.simonsator.partyandfriends.api.pafplayers.PAFPlayer;
import de.simonsator.partyandfriends.block.BMain;
import de.simonsator.partyandfriends.utilities.ConfigurationCreator;

import java.util.List;

/**
 * @author simonbrungs
 * @version 1.0.0 31.01.17
 */
public class BlockList extends FriendSubCommand {
	private final BMain PLUGIN;
	private final ConfigurationCreator MESSAGES;

	public BlockList(BMain pPlugin, ConfigurationCreator pConfig) {
		super(pConfig.getStringList("Commands.BlockList.Name"), pConfig.getInt("Commands.BlockList.Priority"), pConfig.getString("Messages.List.CommandUsage"), pConfig.getString("Commands.BlockList.Permission"));
		PLUGIN = pPlugin;
		MESSAGES = pConfig;
	}

	@Override
	public void onCommand(OnlinePAFPlayer pPlayers, String[] args) {
		List<PAFPlayer> players = PLUGIN.getBlockedPlayers(pPlayers);
		if (players.isEmpty()) {
			return;
		}
		pPlayers.sendMessage(PREFIX + MESSAGES.getString("Messages.List.List") + getBlockedCombined(players));
	}

	private String getBlockedCombined(List<PAFPlayer> pFriends) {
		StringBuilder friendsCombined = new StringBuilder();
		for (int i = 0; i < pFriends.size(); i++) {
			if (i > 0)
				friendsCombined.append(MESSAGES.getString("Messages.List.PlayerSplit"));
			friendsCombined.append(MESSAGES.getString("Messages.List.Color"));
			friendsCombined.append(pFriends.get(i).getDisplayName());
		}
		return friendsCombined.toString();
	}

}
