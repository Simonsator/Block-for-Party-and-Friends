package de.simonsator.partyandfriends.block.subcommands;

import de.simonsator.partyandfriends.api.friends.abstractcommands.FriendSubCommand;
import de.simonsator.partyandfriends.api.pafplayers.OnlinePAFPlayer;
import de.simonsator.partyandfriends.api.pafplayers.PAFPlayer;
import de.simonsator.partyandfriends.api.pafplayers.PAFPlayerManager;
import de.simonsator.partyandfriends.block.BMain;
import de.simonsator.partyandfriends.friends.commands.Friends;
import de.simonsator.partyandfriends.utilities.PatterCollection;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.config.Configuration;

import java.util.regex.Matcher;

/**
 * @author simonbrungs
 * @version 1.0.0 09.01.17
 */
public class UnBlock extends FriendSubCommand {
	private final BMain PLUGIN;
	private final Matcher NOT_BLOCKED;
	private final Matcher UNBLOCKED;

	public UnBlock(String[] pCommands, int pPriority, String pHelp, BMain pPlugin, Configuration pConfig) {
		super(pCommands, pPriority, pHelp);
		PLUGIN = pPlugin;
		NOT_BLOCKED = PatterCollection.PLAYER_PATTERN.matcher(pConfig.getString("Messages.UnBlock.NotBlocked"));
		UNBLOCKED = PatterCollection.PLAYER_PATTERN.matcher(pConfig.getString("Messages.UnBlock.UnBlocked"));
	}

	@Override
	public void onCommand(OnlinePAFPlayer pPlayer, String[] args) {
		if (!isPlayerGiven(pPlayer, args))
			return;
		PAFPlayer toUnBlock = PAFPlayerManager.getInstance().getPlayer(args[1]);
		if (!toUnBlock.doesExist()) {
			sendError(pPlayer, new TextComponent(Friends.getInstance().getPrefix() + NOT_BLOCKED.replaceFirst(args[1])));
			return;
		}
		if (!PLUGIN.isBlocked(pPlayer, toUnBlock)) {
			sendError(pPlayer, new TextComponent(Friends.getInstance().getPrefix() + NOT_BLOCKED.replaceFirst(args[1])));
			return;
		}
		PLUGIN.removeBlock(pPlayer, toUnBlock);
		pPlayer.sendMessage(Friends.getInstance().getPrefix() + UNBLOCKED.replaceFirst(toUnBlock.getDisplayName()));
	}
}
