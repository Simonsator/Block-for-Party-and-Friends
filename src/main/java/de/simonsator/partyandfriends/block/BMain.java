package de.simonsator.partyandfriends.block;

import de.simonsator.partyandfriends.api.events.command.FriendshipCommandEvent;
import de.simonsator.partyandfriends.api.events.command.party.InviteEvent;
import de.simonsator.partyandfriends.api.pafplayers.OnlinePAFPlayer;
import de.simonsator.partyandfriends.api.pafplayers.PAFPlayer;
import de.simonsator.partyandfriends.api.pafplayers.PAFPlayerManager;
import de.simonsator.partyandfriends.block.subcommands.Block;
import de.simonsator.partyandfriends.block.subcommands.BlockList;
import de.simonsator.partyandfriends.block.subcommands.UnBlock;
import de.simonsator.partyandfriends.communication.sql.MySQLData;
import de.simonsator.partyandfriends.friends.commands.Friends;
import de.simonsator.partyandfriends.friends.subcommands.Add;
import de.simonsator.partyandfriends.main.Main;
import de.simonsator.partyandfriends.pafplayers.manager.PAFPlayerManagerMySQL;
import de.simonsator.partyandfriends.pafplayers.mysql.OnlinePAFPlayerMySQL;
import de.simonsator.partyandfriends.pafplayers.mysql.PAFPlayerMySQL;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static de.simonsator.partyandfriends.utilities.PatterCollection.PLAYER_PATTERN;

/**
 * @author simonbrungs
 * @version 1.0.0 09.01.17
 */
public class BMain extends Plugin implements Listener {
	private BConnection connection;

	@Override
	public void onEnable() {
		try {
			Configuration configuration = (new BConfigurationCreator(new File(getDataFolder(), "config.yml"))).getCreatedConfiguration();
			connection = new BConnection(new MySQLData(Main.getInstance().getConfig().getString("MySQL.Host"),
					Main.getInstance().getConfig().getString("MySQL.Username"), Main.getInstance().getConfig().getString("MySQL.Password"),
					Main.getInstance().getConfig().getInt("MySQL.Port"), Main.getInstance().getConfig().getString("MySQL.Database"),
					Main.getInstance().getConfig().getString("MySQL.TablePrefix")));
			ProxyServer.getInstance().getPluginManager().registerListener(this, this);
			Friends.getInstance().addCommand(new Block(configuration.getStringList("Commands.Block.Name").toArray(new String[1]), configuration.getInt("Commands.Block.Priority"), configuration.getString("Messages.Block.CommandUsage"), this, configuration));
			Friends.getInstance().addCommand(new UnBlock(configuration.getStringList("Commands.UnBlock.Name").toArray(new String[1]), configuration.getInt("Commands.UnBlock.Priority"), configuration.getString("Messages.UnBlock.CommandUsage"), this, configuration));
			if (configuration.getBoolean("Commands.BlockList.Use"))
				Friends.getInstance().addCommand(new BlockList(this, configuration));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@EventHandler
	public void onInvite(InviteEvent pEvent) {
		if (isBlocked(pEvent.getExecutor(), pEvent.getInteractPlayer()) || isBlocked(pEvent.getInteractPlayer(), pEvent.getExecutor())) {
			pEvent.getExecutor().sendMessage(new TextComponent(Main.getInstance().getPartyPrefix()
					+ Main.getInstance().getMessagesYml().getString("Party.Command.Invite.CanNotInviteThisPlayer")));
			pEvent.setCancelled(true);
		}
	}

	@EventHandler
	public void onAdd(FriendshipCommandEvent pEvent) {
		if (pEvent.getCaller().getClass().equals(Add.class))
			if (isBlocked(pEvent.getExecutor(), pEvent.getInteractPlayer()) || isBlocked(pEvent.getInteractPlayer(), pEvent.getExecutor())) {
				pEvent.getCaller().sendError(pEvent.getExecutor(), Friends.getInstance().getPrefix() + PLAYER_PATTERN.matcher("Friends.Command.Add.CanNotSendThisPlayer").replaceFirst(pEvent.getInteractPlayer().getName()));
				pEvent.setCancelled(true);
			}
	}


	public boolean isBlocked(PAFPlayer pBlocker, PAFPlayer pBlocked) {
		return connection.isBlocked(((PAFPlayerMySQL) pBlocker).getPlayerID(),
				((PAFPlayerMySQL) pBlocked).getPlayerID());
	}

	public void addBlock(OnlinePAFPlayer pBlocker, PAFPlayer pBlocked) {
		connection.addBlock(((OnlinePAFPlayerMySQL) pBlocker).getPlayerID(),
				((PAFPlayerMySQL) pBlocked).getPlayerID());
	}

	public void removeBlock(OnlinePAFPlayer pBlocker, PAFPlayer pBlocked) {
		connection.removeBlock(((OnlinePAFPlayerMySQL) pBlocker).getPlayerID(),
				((PAFPlayerMySQL) pBlocked).getPlayerID());
	}

	public List<PAFPlayer> getBlockedPlayers(OnlinePAFPlayer pPlayer) {
		List<Integer> idList = connection.getBlockedPlayers(((PAFPlayerMySQL) pPlayer).getPlayerID());
		List<PAFPlayer> pafPlayers = new ArrayList<>(idList.size());
		for (int id : idList)
			pafPlayers.add(((PAFPlayerManagerMySQL) PAFPlayerManager.getInstance()).getPlayer(id));
		return pafPlayers;
	}
}
