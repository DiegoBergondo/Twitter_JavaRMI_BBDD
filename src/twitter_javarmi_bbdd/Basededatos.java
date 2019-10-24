package twitter_javarmi_bbdd;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;

import twitter_javarmi_common.Gui;
import twitter_javarmi_common.ServicioDatosInterface;


public class Basededatos {
	
	private static long timestamp;
	private static long timestamp2;
	Date date = new Date();

	//Main, levanta el servicio y muestra el menú por pantalla
	public static void main(String[] args) throws Exception {
		
		try{
			
			Registry RBBDD;
			ServicioDatosImpl bbdd = new ServicioDatosImpl();
			RBBDD = LocateRegistry.createRegistry(8888);
			ServicioDatosInterface remoteBBDD = (ServicioDatosInterface)UnicastRemoteObject.exportObject(bbdd, 8888);

					
			RBBDD.rebind("BBDD", remoteBBDD);
	
			bbdd.recuperar();
		
			int opt = 0;
			timestamp=time();

			do {
				opt = Gui.menu("---Base de Datos---", 
						new String[]{ "Información de la Base de Datos", 
							  	"Listar Usuarios Registrados",
							  	"Listar Trinos",
							  	"Salir" });
			
				switch (opt) {
				case 0: info(); break;
				case 1: bbdd.listarRegistrados(); break;
				case 2: bbdd.listarTrinos(); break;
				}
			}
			while (opt != 3);
		
			bbdd.archivar();
			RBBDD.unbind("BBDD");
			UnicastRemoteObject.unexportObject(bbdd, true);
		
			System.out.println("Base de datos Terminada");
		}
		catch (Exception excr) {
			System.out.println("No fue posible lanzar la base de datos");
		}
	}
		
	private static long time(){
		Date date = new Date();
		return date.getTime();
	}
	
	//Muestra por pantalla el tiempo que lleva la base de datos online en segundos, el nombre del objeto remoto y el puerto.
	private static void info(){
		Date date = new Date();
		timestamp2=date.getTime();
		long tiempofinal = timestamp2 - timestamp;
		System.out.println("Objeto remoto BBDD en puerto 8888 y lleva online "+tiempofinal/1000+" segundos");
		System.out.println();
	}
}
