package de.simonsator.partyandfriends.block.subcommands;

import de.simonsator.partyandfriends.api.friends.abstractcommands.FriendSubCommand;
import de.simonsator.partyandfriends.api.pafplayers.OnlinePAFPlayer;
import de.simonsator.partyandfriends.api.pafplayers.PAFPlayer;
import de.simonsator.partyandfriends.api.pafplayers.PAFPlayerManager;
import de.simonsator.partyandfriends.block.BMain;
import de.simonsator.partyandfriends.friends.commands.Friends;
import de.simonsator.partyandfriends.friends.subcommands.Deny;
import de.simonsator.partyandfriends.utilities.PatterCollection;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.config.Configuration;

import java.util.regex.Matcher;

/**
 * @author simonbrungs
 * @version 1.0.0 09.01.17
 */
public class Block extends FriendSubCommand {
	private final Matcher FRIENDS;
	private final Matcher ALREADY_BLOCKED;
	private final Matcher BLOCKED;
	private final BMain PLUGIN;
	private final Deny DENY_COMMAND = (Deny) Friends.getInstance().getSubCommand(Deny.class);

	public Block(String[] pCommands, int pPriority, String pHelp, BMain pPlugin, Configuration pConfig) {
		super(pCommands, pPriority, pHelp);
		PLUGIN = pPlugin;
		FRIENDS = PatterCollection.PLAYER_PATTERN.matcher(pConfig.getString("Messages.Block.Friends"));
		BLOCKED = PatterCollection.PLAYER_PATTERN.matcher(pConfig.getString("Messages.Block.Blocked"));
		ALREADY_BLOCKED = PatterCollection.PLAYER_PATTERN.matcher(pConfig.getString("Messages.Block.AlreadyBlocked"));

	}

	@Override
	public void onCommand(OnlinePAFPlayer pPlayer, String[] args) {
		PAFPlayer toBlock = PAFPlayerManager.getInstance().getPlayer(args[1]);
		if (toBlock == null) {
			sendError(pPlayer, "Friends.General.DoesNotExist");
			return;
		}
		if (pPlayer.isAFriendOf(toBlock)) {
			sendError(pPlayer, new TextComponent(Friends.getInstance().getPrefix() + FRIENDS.replaceFirst(toBlock.getName())));
			return;
		}
		if (PLUGIN.isBlocked(pPlayer, toBlock)) {
			sendError(pPlayer, new TextComponent(Friends.getInstance().getPrefix() + ALREADY_BLOCKED.replaceFirst(toBlock.getDisplayName())));
			return;
		}
		if (pPlayer.hasRequestFrom(toBlock)) {
			args[0] = DENY_COMMAND.getCommandName();
			DENY_COMMAND.onCommand(pPlayer, args);
		}
		PLUGIN.addBlock(pPlayer, toBlock);
		pPlayer.sendMessage(Friends.getInstance().getPrefix() + BLOCKED.replaceFirst(pPlayer.getDisplayName()));
	}
}
