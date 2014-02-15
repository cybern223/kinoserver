package newsparserconnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class LocalhostDBConnection {
	public static void main(String args[]) throws IOException {
		Parser p = new Parser();
		Connection connection;
		CharSequence t = "'";
		CharSequence d = "";
		try {
		     // Название драйвера
			String driverName = "com.mysql.jdbc.Driver"; 

			Class.forName(driverName);

			// Create a connection to the database
			String serverName = "localhost";
			String mydatabase = "kinoost_local";
			String url = "jdbc:mysql://" + serverName + "/" + mydatabase;
			String username = "root";
			String password = "123";

			connection = DriverManager.getConnection(url, username, password);
			System.out.println("is connect to DB" + connection);
			Statement stmt = connection.createStatement();
            int j=1;
            int i=1;
			HashMap<String,Movie> tmp= p.parse();
			for (Map.Entry entry : tmp.entrySet()) {
			    String key = (String) entry.getKey();
			    Movie movie = (Movie) entry.getValue();
			    List <Soundtrack> sound = movie.getSounds();
			    key = key.replace(t,d);
			    String img = movie.getImgName();
			    if (img != null)
			    {
			    int l = img.indexOf('_');
			    if(l == -1)
			    	l = img.indexOf('.');
			    img = img.substring(0, l);
			    } else {
			    	img = "0";
			    }
			    int l =key.lastIndexOf(',');
				String year = key.substring(l+1);
				key = key.substring(0, l-1);
				byte a[] = key.getBytes("UTF-8");
				String movieName = new String(a,"UTF-8");
			String query = "Insert into kinoost_local.film values('"+ j + "','" + movieName + "','" + year + "','" + img + "');";
			stmt.executeUpdate(query);
			if (sound == null)
			{
			j++;	
			continue;
			}
			for(Soundtrack s : sound)
			{
				String temp = s.getSong();
				temp = temp.replace(t, d);
				String sql = "Insert into kinoost_local.music values('" + i + "','" + temp + "','" + i + "','" + "0');";
                temp = s.getAuthor();
				temp = temp.replace(t, d);
				String sql1 = "Insert into kinoost_local.perforemer values('" + i + "','" + temp + "');";
				sql = sql + "\n" + sql1;
				//stmt.executeUpdate(sql1);
				stmt.executeUpdate(sql);
				i++;
			}
			j++;
			System.out.println(j-1 + " film ok");
            }
			System.out.println("Parsing finished successfully");
			System.out.println(j-1 + " films were added");
			System.out.println(i-1 + " soundtracks were added");
			connection.close();
		} // end try
		catch (ClassNotFoundException e) {
			e.printStackTrace();
			// Could not find the database driver
		} catch (SQLException e) {
			e.printStackTrace();
			// Could not connect to the database
		}
		catch (NullPointerException e) {
		System.out.print("nullpointer");
		}
	}

}
