package database;

import java.sql.SQLException;

import com.jconnect.core.Route;

public class DataBaseManager extends DbliteConnection{

	private static DataBaseManager instance;

	private static final String PEER_REGISTER_TABLE = "PeerRegister";

	private static final String PEER_REGISTER_CONTACTUUID_FIELD = "ContactUUID";
	private static final String PEER_REGISTER_SOCKET_ADDRESS_FIELD = "SocketAdress";
	private static final String PEER_REGISTER_IS_LOCAL_ADDRESS_FIELD = "isLocal";
	

	private static String DBLITEPATH = "dblite.db";
	

	private static final int VERSION = 1;

	public static DataBaseManager getInstance() 
	{
		if(instance==null){
			instance = new DataBaseManager();
		}

		return instance;

	}

	private DataBaseManager()
	{
		//TODO : Preference path
		super(DBLITEPATH, VERSION);

	}

	@Override
	protected void onCreate() throws SQLException {
		update("create table "+PEER_REGISTER_TABLE+" "+
				"("+PEER_REGISTER_CONTACTUUID_FIELD+" text, " +
				PEER_REGISTER_SOCKET_ADDRESS_FIELD+" text, " +
				PEER_REGISTER_IS_LOCAL_ADDRESS_FIELD + " numeric, "+
				"PRIMARY KEY ("+PEER_REGISTER_CONTACTUUID_FIELD+","+PEER_REGISTER_SOCKET_ADDRESS_FIELD+")" +
				");");


		

	}


	@Override
	protected void onUpdate() throws SQLException {
		

	}

	@Override
	protected void onDelete() throws SQLException {
		

	}
	
	
	
	public void saveRoute(Route r)
	{
		
		
//		try
//		{
//			
//		}
//		catch(SQLException ex)
//		{
//			// if the error message is "out of memory", 
//			// it probably means no database file is found
//			System.err.println(e.getFilepath());
//			System.err.println(ex.getMessage());
//		}
	}










}
