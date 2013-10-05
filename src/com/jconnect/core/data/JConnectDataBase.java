package com.jconnect.core.data;

import java.net.InetSocketAddress;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jconnect.core.model.PeerGroupModel;
import com.jconnect.core.model.PeerModel;
import com.jconnect.core.model.RouteModel;
import com.jconnect.core.peergroup.AbstractPeerGroup;
import com.jconnect.util.uuid.PeerGroupID;
import com.jconnect.util.uuid.PeerID;

/**
 * Extends {@link DbliteConnection}
 * Contains all methods interacting with SQLite DataBase
 *
 */
public class JConnectDataBase extends DbliteConnection {

	private static final String ROUTE_TABLE = "Route";
	private static final String ROUTE_CONTACTUUID_FIELD = "contactUUID";
	private static final String ROUTE_INET_ADDRESS_FIELD = "inetAddress";
	private static final String ROUTE_PORT_FIELD = "port";
	private static final String ROUTE_TRANSPORT_TYPE_FIELD = "transportType";

	private static final String PEERGROUP_TABLE = "Peergroup";
	private static final String PEERGROUP_PEERGROUPUUID_FIELD = "peergroupUUID";

	// private static final String PEERS_TABLE = "Peers";
	private static final String PEERS_PEERUUID_FIELD = "peerUUID";
	// private static final String PEERS_STARTDATE_FIELD = "startDate";

	private static final String PEERANDGROUP_TABLE = "Peerandgroup";

	private static final String CREATE_ROUTE_TABLE = "create table "
			+ ROUTE_TABLE + " " + "(" + ROUTE_CONTACTUUID_FIELD + " text, "
			+ ROUTE_INET_ADDRESS_FIELD + " text, " + ROUTE_PORT_FIELD
			+ " numeric, " + ROUTE_TRANSPORT_TYPE_FIELD + " text, "
			+ "PRIMARY KEY (" + ROUTE_CONTACTUUID_FIELD + ","
			+ ROUTE_INET_ADDRESS_FIELD + "," + ROUTE_PORT_FIELD + ","
			+ ROUTE_TRANSPORT_TYPE_FIELD + ")" + ");";

	private static final String CREATE_PEERGROUP_TABLE = "create table "
			+ PEERGROUP_TABLE + " " + "(" + PEERGROUP_PEERGROUPUUID_FIELD
			+ " text, " + "PRIMARY KEY (" + PEERGROUP_PEERGROUPUUID_FIELD + ")"
			+ ");";

	// private static final String CREATE_PEERS_TABLE = "create table "
	// + PEERS_TABLE + " " + "(" + PEERS_PEERUUID_FIELD + " text, "
	// + PEERS_STARTDATE_FIELD + " long, " + "PRIMARY KEY ("
	// + PEERS_PEERUUID_FIELD + ")" + ");";

	private static final String CREATE_PEERANDGORUP_TABLE = "create table "
			+ PEERANDGROUP_TABLE + " " + "(" + PEERS_PEERUUID_FIELD + " text, "
			+ PEERGROUP_PEERGROUPUUID_FIELD + " text, " + "PRIMARY KEY ("
			+ PEERS_PEERUUID_FIELD + ", " + PEERGROUP_PEERGROUPUUID_FIELD + ")"
			+ ");";

	private Logger log = Logger.getLogger(AbstractPeerGroup.class.getName());

	private static String DBLITENAME = "dblite.db";

	private static final int VERSION = 1;

	JConnectDataBase(String path) {
		super(path + DBLITENAME, VERSION);
	}

	@Override
	protected void onCreate() throws SQLException {
		update(CREATE_ROUTE_TABLE);
		update(CREATE_PEERGROUP_TABLE);
		update(CREATE_PEERANDGORUP_TABLE);

	}

	@Override
	protected void onUpdate() throws SQLException {

	}

	@Override
	protected void onDelete() throws SQLException {

	}

	public ArrayList<RouteModel> getRoutes(PeerID contactUUID) {

		ArrayList<RouteModel> res = new ArrayList<RouteModel>();
		try {
			ResultSet rs = query("select r." + ROUTE_INET_ADDRESS_FIELD + ",r."
					+ ROUTE_PORT_FIELD + ", r." + ROUTE_TRANSPORT_TYPE_FIELD
					+ " from " + ROUTE_TABLE + " r where r."
					+ ROUTE_CONTACTUUID_FIELD + "=\"" + contactUUID.toString()
					+ "\"");

			while (rs.next()) {

				InetSocketAddress s = new InetSocketAddress(
						rs.getString(ROUTE_INET_ADDRESS_FIELD),
						rs.getInt(ROUTE_PORT_FIELD));
				res.add(new RouteModel(contactUUID, s, RouteModel.TransportType
						.valueOf(rs.getString(ROUTE_TRANSPORT_TYPE_FIELD))));

			}

		} catch (SQLException ex) {
			log.log(Level.WARNING, "getRoutes : " + ex.getMessage());

		}
		return res;
	}

	public void saveRoute(RouteModel r) {
		try {
			update("insert into " + ROUTE_TABLE + "(" + ROUTE_CONTACTUUID_FIELD
					+ ", " + ROUTE_INET_ADDRESS_FIELD + "," + ROUTE_PORT_FIELD
					+ ", " + ROUTE_TRANSPORT_TYPE_FIELD + ") values (\""
					+ r.getContactUUID().toString() + "\", \""
					+ r.getSocketAddress().getAddress().toString() + "\","
					+ r.getSocketAddress().getPort() + ", \""
					+ r.getTransportType().toString() + "\")");

		} catch (SQLException ex) {
			log.log(Level.FINER, "saveRoute : " + ex.getMessage());
		}
	}

	// public void savePeer(Peer p) {
	// try {
	// update("insert into " + PEERS_TABLE + "(" + PEERS_PEERUUID_FIELD
	// + ", " + PEERS_STARTDATE_FIELD + ") values (\""
	// + p.getPeerID().toString() + "\", " + p.getStartDate()
	// + ")");
	//
	// } catch (SQLException ex) {
	// log.log(Level.FINER, "savePeer : " + ex.getMessage());
	// }
	// }

	public ArrayList<RouteModel> getPeer(PeerID contactUUID) {

		ArrayList<RouteModel> res = new ArrayList<RouteModel>();
		try {
			ResultSet rs = query("select r." + ROUTE_INET_ADDRESS_FIELD + ",r."
					+ ROUTE_PORT_FIELD + ", r." + ROUTE_TRANSPORT_TYPE_FIELD
					+ " from " + ROUTE_TABLE + " r where r."
					+ ROUTE_CONTACTUUID_FIELD + "=\"" + contactUUID.toString()
					+ "\"");

			while (rs.next()) {

				InetSocketAddress s = new InetSocketAddress(
						rs.getString(ROUTE_INET_ADDRESS_FIELD),
						rs.getInt(ROUTE_PORT_FIELD));
				res.add(new RouteModel(contactUUID, s, RouteModel.TransportType
						.valueOf(rs.getString(ROUTE_TRANSPORT_TYPE_FIELD))));

			}

		} catch (SQLException ex) {
			log.log(Level.WARNING, "getRoutes : " + ex.getMessage());

		}
		return res;
	}

	public void savePeerGroup(PeerGroupModel p) {
		try {
			update("insert into " + PEERGROUP_TABLE + "("
					+ PEERGROUP_PEERGROUPUUID_FIELD + ") values (\""
					+ p.getGroupID().toString() + "\" )");

		} catch (SQLException ex) {
			log.log(Level.FINER, "savePeerGroup : " + ex.getMessage());
		}
	}

	public List<PeerGroupModel> getPeerGroups() {
		List<PeerGroupModel> res = new ArrayList<PeerGroupModel>();
		try {
			ResultSet rs = query("select r." + PEERGROUP_PEERGROUPUUID_FIELD
					+ " from " + PEERGROUP_TABLE + " r");

			while (rs.next()) {

				PeerGroupModel p = new PeerGroupModel(new PeerGroupID(rs
						.getString(PEERGROUP_PEERGROUPUUID_FIELD)));

				res.add(p);

			}

		} catch (SQLException ex) {
			log.log(Level.WARNING, "getPeerGroups : " + ex.getMessage());

		}
		return res;
	}

	public void addGroupMember(PeerGroupID group, PeerModel p) {
		try {
			update("insert into " + PEERANDGROUP_TABLE + "("
					+ PEERS_PEERUUID_FIELD + ", "
					+ PEERGROUP_PEERGROUPUUID_FIELD + ") values (\""
					+ p.getPeerID().toString() + "\", \"" + group.toString()
					+ "\" )");
		} catch (SQLException ex) {
			log.log(Level.FINER, "addGroupMember : " + ex.getMessage());
		}
	}

	public List<PeerModel> getGroupMembers(PeerGroupID groupID) {
		List<PeerModel> res = new ArrayList<PeerModel>();
		try {
			ResultSet rs = query("select r." + PEERS_PEERUUID_FIELD + " from "
					+ PEERANDGROUP_TABLE + " m where "
					+ PEERGROUP_PEERGROUPUUID_FIELD + "=\""
					+ groupID.toString() + "\"");

			while (rs.next()) {

				PeerModel p = new PeerModel(new PeerID(rs
						.getString(PEERS_PEERUUID_FIELD)));
				res.add(p);

			}

		} catch (SQLException ex) {
			log.log(Level.WARNING, "getGroupMembers : " + ex.getMessage());

		}
		return res;
	}

}
