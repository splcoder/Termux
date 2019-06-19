package com.example.termux;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/*
 * Utilizar PostgreSQL en Android con JDBC
 * www.programacion.com.py - Recursos y documentación para desarrolladores - By Rodrigo Paszniuk
 * PD: Para agregar una libreria .jar cualquiera al proyecto solamente se debe agregar a la carpeta libs del mismo.
 * Para actualizar cambios hacer click derecho al proyecto y luego  click izquierdo a Refresh.
 *
 * Gradle -> dependencies: poner el nombre del jdbc (jar)
 * 		implementation files('libs/postgresql-42.2.5')
 *
 *
 *
 * 	VER:	https://wiki.termux.com/wiki/Main_Page
 * 			https://pgolub.wordpress.com/2017/11/24/postgresql-on-android/
 * 			https://wiki.termux.com/wiki/Postgresql
 * 			https://gist.github.com/Kartones/dd3ff5ec5ea238d4c546
 * 			https://www.a2hosting.es/kb/developer-corner/postgresql/connect-to-postgresql-from-the-command-line
 * 			https://www.makeuseof.com/tag/use-linux-command-line-android-termux/
----------------------------------------------------------------------------------------------------
				After Termux install:
					$ pkg install postgresql
				And after Postgresql install (to start it first create your folder you want to store the data in and then init te folder):
					$ mkdir -p $PREFIX/var/lib/postgresql
					$ initdb $PREFIX/var/lib/postgresql
				Starting the database
					$ pg_ctl -D $PREFIX/var/lib/postgresql start
				Similarly stop the database using
					$ pg_ctl stop
				Create a user to allow you to connect form an App:
					$ createuser --superuser --pwprompt yourUserName	<<< NO <<< FALTA Replication y Bypass RLS
					postgres=# create user sergio with login superuser createdb createrole replication bypassrls
				Show users:
					$ psql posgres
					$ postgres=# \du				>>> Gives: Superuser, Create role, Create DB, Replication, Bypass RLS
				Create your database:
					$ createdb mydb
				Open your database
					$ psql mydb

				-----------------------------------------------------------
				User-PC:~$ psql postgres
				psql (9.5.7)
				Type "help" for help.

				postgres=# help
				You are using psql, the command-line interface to PostgreSQL.
				Type:  \copyright for distribution terms
					   \h for help with SQL commands
					   \? for help with psql commands
					   \g or terminate with semicolon to execute query
					   \q to quit
				postgres=# \? createdb
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
create database fifa;
\c fifa;
create table jugadores(id serial PRIMARY KEY, name VARCHAR(50) NOT NULL);
insert into jugadores (name) VALUES ('Sergio');
select * from jugadores;


create user sergio;
ALTER USER sergio WITH PASSWORD 'entrar';
ALTER USER sergio WITH ENCRYPTED PASSWORD 'entrar';
grant all privileges on database fifa to sergio;
revoke all privileges on database fifa from sergio;
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
	Show all tables (of a db)
		\dt
	Programmatically (or from the psql interface too, of course):
		SELECT * FROM pg_catalog.pg_tables;
----------------------------------------------------------------------------------------------------
	List of databases
		postgres=> \l
	Postgres login commands
		psql -d mydb -U myuser
 */
public class MainActivity extends AppCompatActivity {

	TextView txtData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		txtData = findViewById( R.id.txtData );

		Log.e( "Postgre", "Start" );

		//Desde la version 3 de android, no se permite abrir una conexión de red desde el thread principal.
		//Por lo tanto se debe crear uno nuevo.
		sqlThread.start();
	}

	/*Thread sqlThread = new Thread() {
		public void run(){
			try {
				Class.forName("org.postgresql.Driver");
				// "jdbc:postgresql://IP:PUERTO/DB", "USER", "PASSWORD");
				Connection conn = DriverManager.getConnection(
						"jdbc:postgresql://192.168.0.4:5432/fifa", "test", "test");
				//En el stsql se puede agregar cualquier consulta SQL deseada.
				String stsql = "Select version()";
				Statement st = conn.createStatement();
				ResultSet rs = st.executeQuery(stsql);
				rs.next();
				//---------------------------------------------------
				Log.e( "Postgre", "Con resultado, fuera de runOnUiThread" );
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						//txtData.setText("#" + rs.getString(1));
						Log.e( "Postgre", "Con resultado" );
					}
				});
				//---------------------------------------------------
				//System.out.println( rs.getString(1) );
				conn.close();
			} catch (SQLException se) {
				//System.out.println("oops! No se puede conectar. Error: " + se.toString());
				Log.e( "Postgre", "No se puede conectar" );
			} catch (ClassNotFoundException e) {
				//System.out.println("oops! No se encuentra la clase. Error: " + e.getMessage());
				Log.e( "Postgre", "No se encuentra la clase" );
			}
		}
	};*/

	Thread sqlThread = new Thread( new Runnable(){
		public void run(){
			try {
				Log.e( "Postgre", "In the Thread -> Start" );
				Class.forName("org.postgresql.Driver");
				// "jdbc:postgresql://IP:PUERTO/DB", "USER", "PASSWORD");
				//Connection conn = DriverManager.getConnection("jdbc:postgresql://192.168.0.4:5432/fifa", "test", "test");
				//Connection conn = DriverManager.getConnection("jdbc:postgresql://192.168.0.4:5432/fifa", "postgres", "postgres");
				//Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/fifa", "postgres", "postgres");
				//Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/fifa", "sergio", "entrar");
				Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/fifa", "sergio", "pepe2");	// TODO problema LA CONTRASEÑA SE LA SALTA POR EL FORRO !!!
				//Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/fifa", "u0_a86", "");
				// USUARIO:		u0_a86, u0_a87
				// PASSWORD:	???
				//En el stsql se puede agregar cualquier consulta SQL deseada.
				//String stsql = "Select version()";
				String stsql = "Select * from jugadores";
				Statement st = conn.createStatement();
				final ResultSet rs = st.executeQuery(stsql);
				rs.next();
				//---------------------------------------------------
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Log.e( "Postgre", "Con resultado" );
						try {
							//txtData.setText("#" + rs.getString(1).toString() );
							txtData.setText("#" + rs.getString(1).toString() + ": " + rs.getString(2).toString() );
						} catch (SQLException e) {
							Log.e( "Postgre", "In runOnUiThread -> BAD setText..." );
							e.printStackTrace();
						}
					}
				});
				//---------------------------------------------------
				conn.close();
			} catch (SQLException se) {
				Log.e( "Postgre", "No se puede conectar" );
			} catch (ClassNotFoundException e) {
				Log.e( "Postgre", "No se encuentra la clase" );
			} catch( Exception e ){
				Log.e( "Postgre", "Exception -> " + e.toString() );
			}
		}
	});
}
