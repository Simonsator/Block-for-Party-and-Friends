package de.simonsator.partyandfriends.block;

import de.simonsator.partyandfriends.communication.sql.MySQLData;
import de.simonsator.partyandfriends.communication.sql.SQLCommunication;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

/**
 * @author simonbrungs
 * @version 1.0.0 09.01.17
 */
public class BConnection extends SQLCommunication {
	private final String TABLE_PREFIX;

	public BConnection(MySQLData pMySQLData) {
		super(pMySQLData);
		this.TABLE_PREFIX = pMySQLData.TABLE_PREFIX;
		importTable();
	}

	private void importTable() {
		Connection con = getConnection();
		PreparedStatement prepStmt = null;
		try {
			prepStmt = con.prepareStatement("CREATE TABLE IF NOT EXISTS " + DATABASE + ".`" + TABLE_PREFIX
					+ "blocked` (`blocker_id` INT(8) NOT NULL, `blocked_id` INT(8) NOT NULL);");
			prepStmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(prepStmt);
		}

	}

	public boolean isBlocked(int pBlocker, int pBlocked) {
		Connection con = getConnection();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			rs = (stmt = con.createStatement()).executeQuery("Select blocker_id FROM `" + DATABASE + "`." + TABLE_PREFIX
					+ "blocked WHERE blocker_id = '" + pBlocker + "' AND blocked_id='" + pBlocked + "' LIMIT 1");
			if (rs.next())
				return true;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(rs, stmt);
		}
		return false;
	}

	public void addBlock(int pBlocker, int pBlocked) {
		Connection con = getConnection();
		PreparedStatement prepStmt = null;
		try {
			prepStmt = con.prepareStatement(
					"insert into `" + DATABASE + "`." + TABLE_PREFIX + "blocked values (?, ?)");
			prepStmt.setInt(1, pBlocker);
			prepStmt.setInt(2, pBlocked);
			prepStmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(prepStmt);
		}
	}

	public void removeBlock(int pBlocker, int pBlocked) {
		Connection con = getConnection();
		PreparedStatement prepStmt = null;
		try {
			prepStmt = con.prepareStatement(
					"DELETE FROM `" + DATABASE + "`." + TABLE_PREFIX + "blocked WHERE blocker_id = '"
							+ pBlocker + "' AND blocked_id='" + pBlocked + "' Limit 1");
			prepStmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(prepStmt);
		}
	}

	public List<Integer> getBlockedPlayers(int pBlocker) {
		Connection con = getConnection();
		Statement stmt = null;
		ResultSet rs = null;
		List<Integer> list = new LinkedList<>();
		try {
			rs = (stmt = con.createStatement()).executeQuery("Select blocked_id FROM `" + DATABASE + "`." + TABLE_PREFIX
					+ "blocked WHERE blocker_id = '" + pBlocker + "'");
			while (rs.next()) list.add(rs.getInt(1));
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(rs, stmt);
		}
		return list;
	}
}
