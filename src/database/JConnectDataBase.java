package database;

import java.net.InetSocketAddress;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jconnect.model.PeerGroup;
import com.jconnect.model.Route;
import com.jconnect.peergroup.AbstractPeerGroup;
import com.jconnect.peergroup.peer.Peer;

public class JConnectDataBase extends DbliteConnection{

	private static JConnectDataBase instance;

	private static final String ROUTE_TABLE = "Route";
	private static final String ROUTE_CONTACTUUID_FIELD = "contactUUID";
	private static final String ROUTE_INET_ADDRESS_FIELD = "inetAddress";
	private static final String ROUTE_PORT_FIELD = "port";
	private static final String ROUTE_TRANSPORT_TYPE_FIELD = "transportType";
	
	private static final String PEERS_TABLE = "Peers";
	private static final String PEERS_PEERUUID_FIELD = "peerUUID";
	private static final String PEERS_STARTDATE_FIELD = "startDate";



	private Logger log = Logger.getLogger(AbstractPeerGroup.class.getName());

	private static String DBLITEPATH = "dblite.db";


	private static final int VERSION = 1;

	public static JConnectDataBase getInstance() 
	{
		if(instance==null){
			instance = new JConnectDataBase();
		}

		return instance;

	}

	private JConnectDataBase()
	{
		//TODO : Preference path
		super(DBLITEPATH, VERSION);

	}

	@Override
	protected void onCreate() throws SQLException {
		update("create table "+ROUTE_TABLE+" "+
				"("+ROUTE_CONTACTUUID_FIELD+" text, " +
				ROUTE_INET_ADDRESS_FIELD+" text, " +
				ROUTE_PORT_FIELD+" numeric, " +
				ROUTE_TRANSPORT_TYPE_FIELD + " text, "+
				"PRIMARY KEY ("+ROUTE_CONTACTUUID_FIELD+","+ROUTE_INET_ADDRESS_FIELD+","+ROUTE_PORT_FIELD+","+ROUTE_TRANSPORT_TYPE_FIELD+")" +
				");");




	}


	@Override
	protected void onUpdate() throws SQLException {


	}

	@Override
	protected void onDelete() throws SQLException {


	}


	public ArrayList<Route> getRoutes(UUID contactUUID)
	{

		ArrayList<Route> res = new ArrayList<Route>();
		try
		{
			ResultSet rs =  query("select r."+ROUTE_INET_ADDRESS_FIELD+",r."+ROUTE_PORT_FIELD+", r."+ROUTE_TRANSPORT_TYPE_FIELD+
					" from "+ROUTE_TABLE+" r where r."+ROUTE_CONTACTUUID_FIELD+"=\""+contactUUID.toString()+"\"");

			while(rs.next())
			{

		
				InetSocketAddress s = new InetSocketAddress(rs.getString(ROUTE_INET_ADDRESS_FIELD), rs.getInt(ROUTE_PORT_FIELD));
				res.add(new Route(contactUUID,s,Route.TransportType.valueOf(rs.getString(ROUTE_TRANSPORT_TYPE_FIELD))));

			}

		}
		catch(SQLException ex)
		{
			log.log(Level.WARNING,"getRoutes : "+ ex.getMessage());
	
		}
		return res;
	}


	public void saveRoute(Route r)
	{
		try
		{
			update("insert into "+ROUTE_TABLE+ "("+ROUTE_CONTACTUUID_FIELD+", "+ROUTE_INET_ADDRESS_FIELD+","+ROUTE_PORT_FIELD+", "+ROUTE_TRANSPORT_TYPE_FIELD+") values (\""+r.getContactUUID().toString() + "\", \""+r.getSocketAddress().getAddress().toString()+"\","+r.getSocketAddress().getPort()+", \""+r.getTransportType().toString()+"\")");

		}
		catch(SQLException ex)
		{
			log.log(Level.FINER,"saveRoute : "+ ex.getMessage());
		}
	}
	
	public void savePeer(Peer p)
	{
		try
		{
			//Enregistre-t-on vraiment startDate ? Je pense pas que y'ai besoin de persitance pour ça.
			update("insert into "+PEERS_TABLE+ "("+PEERS_PEERUUID_FIELD+", "+PEERS_STARTDATE_FIELD+") values (\""+p.getPeerID().toString() + "\", "+p.getStartDate()+")");
			
		}
		catch(SQLException ex)
		{
			log.log(Level.FINER,"savePeer : "+ ex.getMessage());
		}
	}
	
	
	public void savePeerGroup(PeerGroup p)
	{
		//TODO
	}










}
