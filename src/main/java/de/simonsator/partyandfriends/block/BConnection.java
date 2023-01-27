package de.simonsator.partyandfriends.block;

import de.simonsator.partyandfriends.communication.sql.MySQLData;
import de.simonsator.partyandfriends.communication.sql.pool.PoolData;
import de.simonsator.partyandfriends.communication.sql.pool.PoolSQLCommunication;
import de.simonsator.partyandfriends.utilities.disable.Disabler;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class BConnection extends PoolSQLCommunication {
	private final String TABLE_PREFIX;

	public BConnection(MySQLData pMySQLData, PoolData pPoolData) throws SQLException {
		super(pMySQLData, pPoolData);
		this.TABLE_PREFIX = pMySQLData.TABLE_PREFIX;
		importTable();
		Disabler.getInstance().registerDeactivated(this);
	}

	private void importTable() {
		Connection con = getConnection();
		PreparedStatement prepStmt = null;
		try {
			prepStmt = con.prepareStatement("CREATE TABLE IF NOT EXISTS `" + TABLE_PREFIX
					+ "blocked` (`blocker_id` INT(8) NOT NULL, FOREIGN KEY (`blocker_id`) REFERENCES `" + TABLE_PREFIX +
					"players`(`player_id`), `blocked_id` INT(8) NOT NULL, FOREIGN KEY (`blocked_id`) REFERENCES `" + TABLE_PREFIX +
					"players`(`player_id`));");
			prepStmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(con, prepStmt);
		}

	}

	public boolean isBlocked(int pBlocker, int pBlocked) {
		Connection con = getConnection();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			rs = (stmt = con.createStatement()).executeQuery("Select blocker_id FROM " + TABLE_PREFIX
					+ "blocked WHERE blocker_id = '" + pBlocker + "' AND blocked_id='" + pBlocked + "' LIMIT 1");
			if (rs.next())
				return true;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(con, rs, stmt);
		}
		return false;
	}

	public void addBlock(int pBlocker, int pBlocked) {
		Connection con = getConnection();
		PreparedStatement prepStmt = null;
		try {
			prepStmt = con.prepareStatement(
					"insert into " + TABLE_PREFIX + "blocked values (?, ?)");
			prepStmt.setInt(1, pBlocker);
			prepStmt.setInt(2, pBlocked);
			prepStmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(con, prepStmt);
		}
	}

	public void removeBlock(int pBlocker, int pBlocked) {
		Connection con = getConnection();
		PreparedStatement prepStmt = null;
		try {
			prepStmt = con.prepareStatement(
					"DELETE FROM " + TABLE_PREFIX + "blocked WHERE blocker_id = '"
							+ pBlocker + "' AND blocked_id='" + pBlocked + "' Limit 1");
			prepStmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(con, prepStmt);
		}
	}

	public List<Integer> getBlockedPlayers(int pBlocker) {
		Connection con = getConnection();
		Statement stmt = null;
		ResultSet rs = null;
		List<Integer> list = new LinkedList<>();
		try {
			rs = (stmt = con.createStatement()).executeQuery("Select blocked_id FROM " + TABLE_PREFIX
					+ "blocked WHERE blocker_id = '" + pBlocker + "'");
			while (rs.next()) list.add(rs.getInt(1));
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(con, rs, stmt);
		}
		return list;
	}
}
