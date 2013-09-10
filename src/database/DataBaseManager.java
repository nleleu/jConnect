package database;

import java.sql.SQLException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DataBaseManager extends DbliteConnection{

	private static DataBaseManager instance;

	public static Lock exclusiveAccess = new ReentrantLock();


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



		

	}


	@Override
	protected void onUpdate() throws SQLException {
		

	}

	@Override
	protected void onDelete() throws SQLException {
		

	}










}
