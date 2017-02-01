package de.simonsator.partyandfriends.block;

import de.simonsator.partyandfriends.utilities.ConfigurationCreator;

import java.io.File;
import java.io.IOException;

/**
 * @author simonbrungs
 * @version 1.0.0 09.01.17
 */
public class BConfigurationCreator extends ConfigurationCreator {
	protected BConfigurationCreator(File pFile) throws IOException {
		super(pFile);
		readFile();
		defaultValues();
		saveFile();
		process(configuration);
	}

	private void defaultValues() {
		set("Commands.Block.Name", "block", "blockplayer");
		set("Commands.Block.Priority", 1000);
		set("Commands.UnBlock.Name", "unblock", "unblockplayer");
		set("Commands.UnBlock.Priority", 1001);
		set("Commands.BlockList.Name", "blocklist", "listblock", "listblocked", "blockedlist");
		set("Commands.BlockList.Priority", 1002);
		set("Commands.BlockList.Use", true);
		set("Messages.Block.CommandUsage", "&8/&5friend block [name of the player]&r &8- &7Blocks a player");
		set("Messages.Block.Blocked", " &7You blocked successfully the player &e[PLAYER]");
		set("Messages.Block.GivenPlayerEqualsExecutor", " &7You cannot block yourself.");
		set("Messages.Block.Friends", " &7Before you can block this player you need first to remove this friend by using the command &5/friend remove [PLAYER]");
		set("Messages.Block.AlreadyBlocked", " &7You already blocked the player &e[PLAYER]");
		set("Messages.UnBlock.CommandUsage", "&8/&5friend unblock [name of the player]&r &8- &7Unblocks a player");
		set("Messages.UnBlock.NotBlocked", " &7You never blocked the player &e[PLAYER]");
		set("Messages.UnBlock.UnBlocked", " &7You unblocked the player &e[PLAYER]");
		set("Messages.Add", " &7You cannot add somebody as a friend if he is blocked. Unblock him by using the command /friend unblock [PLAYER]");
		set("Messages.Invite", " &7You cannot invite somebody into a party if he is blocked. Unblock him by using the command /friend unblock [PLAYER]");
		set("Messages.List.CommandUsage", "&8/&5friend blocklist &r&8- &7Lists all blocked players");
		set("Messages.List.Color", "&c");
		set("Messages.List.PlayerSplit", "&7, ");
		set("Messages.List.List", " &7You blocked these players: ");
	}

	@Override
	public void reloadConfiguration() throws IOException {
		configuration = (new BConfigurationCreator(FILE)).getCreatedConfiguration();
	}
}
