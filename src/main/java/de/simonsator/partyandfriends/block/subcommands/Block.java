package de.simonsator.partyandfriends.block.subcommands;

import de.simonsator.partyandfriends.api.friends.abstractcommands.FriendSubCommand;
import de.simonsator.partyandfriends.api.pafplayers.OnlinePAFPlayer;
import de.simonsator.partyandfriends.api.pafplayers.PAFPlayer;
import de.simonsator.partyandfriends.api.pafplayers.PAFPlayerManager;
import de.simonsator.partyandfriends.block.BMain;
import de.simonsator.partyandfriends.friends.commands.Friends;
import de.simonsator.partyandfriends.friends.subcommands.Deny;
import de.simonsator.partyandfriends.utilities.ConfigurationCreator;
import de.simonsator.partyandfriends.utilities.PatterCollection;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.List;
import java.util.regex.Matcher;

/**
 * @author simonbrungs
 * @version 1.0.0 09.01.17
 */
public class Block extends FriendSubCommand {
	private final Matcher FRIENDS;
	private final Matcher ALREADY_BLOCKED;
	private final Matcher BLOCKED;
	private final String GIVEN_PLAYER_EQUALS_EXECUTOR;
	private final BMain PLUGIN;
	private final Deny DENY_COMMAND = (Deny) Friends.getInstance().getSubCommand(Deny.class);

	public Block(List<String> pCommands, int pPriority, String pPermission, String pHelp, BMain pPlugin, ConfigurationCreator pConfig) {
		super(pCommands, pPriority, pHelp, pPermission);
		PLUGIN = pPlugin;
		FRIENDS = PatterCollection.PLAYER_PATTERN.matcher(pConfig.getString("Messages.Block.Friends"));
		BLOCKED = PatterCollection.PLAYER_PATTERN.matcher(pConfig.getString("Messages.Block.Blocked"));
		ALREADY_BLOCKED = PatterCollection.PLAYER_PATTERN.matcher(pConfig.getString("Messages.Block.AlreadyBlocked"));
		GIVEN_PLAYER_EQUALS_EXECUTOR = pConfig.getString("Messages.Block.GivenPlayerEqualsExecutor");
	}

	@Override
	public void onCommand(OnlinePAFPlayer pPlayer, String[] args) {
		if (!isPlayerGiven(pPlayer, args))
			return;
		if (pPlayer.getName().equalsIgnoreCase(args[1])) {
			sendError(pPlayer, new TextComponent(TextComponent.fromLegacyText(PREFIX + GIVEN_PLAYER_EQUALS_EXECUTOR)));
			return;
		}
		PAFPlayer toBlock = PAFPlayerManager.getInstance().getPlayer(args[1]);
		if (!toBlock.doesExist()) {
			sendError(pPlayer, "Friends.General.DoesNotExist");
			return;
		}
		if (pPlayer.isAFriendOf(toBlock)) {
			sendError(pPlayer, new TextComponent(TextComponent.fromLegacyText(PREFIX + FRIENDS.replaceFirst(toBlock.getName()))));
			return;
		}
		if (PLUGIN.isBlocked(pPlayer, toBlock)) {
			sendError(pPlayer, new TextComponent(TextComponent.fromLegacyText(PREFIX + ALREADY_BLOCKED.replaceFirst(toBlock.getDisplayName()))));
			return;
		}
		if (pPlayer.hasRequestFrom(toBlock)) {
			args[0] = DENY_COMMAND.getCommandName();
			DENY_COMMAND.onCommand(pPlayer, args);
		}
		toBlock.denyRequest(pPlayer);
		PLUGIN.addBlock(pPlayer, toBlock);
		pPlayer.sendMessage(PREFIX + BLOCKED.replaceFirst(toBlock.getDisplayName()));
	}
}
