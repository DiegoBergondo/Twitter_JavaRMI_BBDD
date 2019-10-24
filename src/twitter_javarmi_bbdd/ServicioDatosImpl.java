package twitter_javarmi_bbdd;

import java.io.*;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import twitter_javarmi_common.ServicioDatosInterface;
import twitter_javarmi_common.Trino;

public class ServicioDatosImpl implements ServicioDatosInterface {

	private Map<String, String> nick_pass = new HashMap<String, String>();
	private Map<String, String> nick_nombre = new HashMap<String, String>();
	private Map<String, String> nick_mail = new HashMap<String, String>();	
	private Map<String, List<String>> contactos = new HashMap<String, List<String>>();
	private Map<String, List<Trino>> buffer = new HashMap<String, List<Trino>>();
	
	@Override
	//Método para añadir usuario a la BBDD
	public void añadirUsuario(String nick, String password, String nombre, String mail) throws RemoteException{
		
			nick_pass.put(nick, password);
			nick_nombre.put(nick, nombre);
			nick_mail.put(nick, mail);
	}
	
	@Override
	//Método para añadir amigo a ambas listas.
	public void añadirAmigo(String nickAmigo, String nick) throws RemoteException{

		List<String> misContactos = contactos.get(nick);		
		List<String> susContactos = contactos.get(nickAmigo);
		//Si la lista no existe, se crea.
		if (misContactos == null) {
			misContactos = new LinkedList<String>();
			contactos.put(nick, misContactos);
		}

		if (susContactos == null) {
			susContactos = new LinkedList<String>();
			contactos.put(nickAmigo, susContactos);
		}
		
		misContactos.add(nickAmigo);
		susContactos.add(nick);		
	}
	
	@Override
	//Método para eliminar amigo de ambas listas.
	public void eliminarAmigo(String nickAmigo, String nick) throws RemoteException{

		List<String> misContactos = contactos.get(nick);		
		List<String> susContactos = contactos.get(nickAmigo);
		
		misContactos.remove(nickAmigo);
		susContactos.remove(nick);		
	}
	
	@Override
	//Método para añadir un trino al buffer.
	public void enviarTrino(String trino, String nickDe, String nickA) throws RemoteException{
		List<Trino> trinos = buffer.get(nickA);
		//Si la lista no existe se crea.
		if (trinos == null) {
			trinos = new LinkedList<Trino>();
			buffer.put(nickA, trinos);
		}
		
		trinos.add(new Trino(trino, nickDe));
	}

	@Override
	//Método para listar los trinos del usuario solicitado que estén en el buffer.
	public List<Trino> recibir(String nick) throws RemoteException{
		
		if(!buffer.isEmpty()){
			if(buffer.containsKey(nick)){
				return buffer.get(nick);
			}
			else return null;
		}
		else return null;
	}
	
	@Override
	//Vacía el buffer de trinos de un usuario.
	public void limpiarBuffer(String nick) throws RemoteException {
		
		buffer.get(nick).clear();
	}
	
	@Override
	//Método que indica si un usuario está registrado o no.
	public boolean usuarioRegistrado(String nick) throws RemoteException{
		return nick_pass.containsKey(nick);
	}
	
	@Override
	//Método que indica si un usuario está ya en la lista de amigos de otro o no.
	public boolean usuarioEnMiLista(String nickAmigo, String nickNombre) throws RemoteException{
		List<String> misContactos = contactos.get(nickNombre);		
		if (misContactos == null)
			return false;
		return misContactos.contains(nickAmigo);
	}
	
	@Override
	//Método que devuelve la lista de amigos de un usuario.
	public List<String> amigos(String nick) throws RemoteException{
		return contactos.get(nick);
	}

	
	@Override
	//Método que devuelve el password de un usuario.
	public String getPass(String nick) throws RemoteException{
		return nick_pass.get(nick);
	}
	
	@Override
	//Método que devuelve el nombre de un usuario.
	public String getNombre(String nick) throws RemoteException{
		return nick_nombre.get(nick);
	}
	
	@Override
	//Método que devuelve el email de un usuario.
	public String getMail(String nick) throws RemoteException{
		return nick_mail.get(nick);
	}
	
	@SuppressWarnings("rawtypes")
	//Método que lista los usuarios que hay registrados en la base de datos..
	public void listarRegistrados(){
		Map<String, String> registrados = nick_pass;
		Set set = registrados.entrySet();
		Iterator iterator = set.iterator();
		if (!iterator.hasNext())//Primero se comprueba si hay alguno registrado.
			System.out.println("Todavía no hay usuarios registrados");
		else{//Si hay alguno online se saca la lista.
		while (iterator.hasNext()){
			Map.Entry mentry = (Map.Entry)iterator.next();
			System.out.println("@ " + mentry.getKey());
		}
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	//Método que lista todos los trinos que hay en el buffer (pendientes de entregar) de la base de datos.
	public void listarTrinos() throws RemoteException{
		Map<String, List<Trino>> listaTrinos = buffer;
		Set set = listaTrinos.entrySet();
		Iterator iterator = set.iterator();
		while (iterator.hasNext()){
			Map.Entry mentry = (Map.Entry)iterator.next();
			List<Trino> trinos = (List<Trino>) mentry.getValue();
			if(!trinos.isEmpty()){
				System.out.println("Trino a: " + mentry.getKey());
				for (Trino trino : trinos) {
					System.out.println("De " + trino.ObtenerNickPropietario());
					System.out.println("# " + trino.ObtenerTrino());
					}
				}
			}
		}	
	
	//Método que guarda los datos en ficheros una vez se termina la el servicio de datos.
	public void archivar() throws IOException{
		FileOutputStream fos = new FileOutputStream("nicks");
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(nick_pass);
		oos.close();
		fos.close();
		fos = new FileOutputStream("nombres");
		oos = new ObjectOutputStream(fos);
		oos.writeObject(nick_nombre);
		oos.close();
		fos.close();
		fos = new FileOutputStream("mails");
		oos = new ObjectOutputStream(fos);
		oos.writeObject(nick_mail);
		oos.close();
		fos.close();
		fos = new FileOutputStream("list1");
		oos = new ObjectOutputStream(fos);
		oos.writeObject(contactos);
		oos.close();
		fos.close();
		fos = new FileOutputStream("list2");
		oos = new ObjectOutputStream(fos);
		oos.writeObject(buffer);
		oos.close();
		fos.close();
		
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	//Método que recupera los datos desde los ficheros cuando se inicia el servicio de datos.
	public void recuperar() throws IOException, ClassNotFoundException{
		try
		{
			File nicks = new File("nicks");
			File nombres = new File("nombres");
			File mails = new File("mails");
			File list1 = new File("list1");
			File list2 = new File("list2");
			//Primero verifica que los ficheros existan, ya que en la primera ejecución no existirán y se crearán tras el cierre de la misma.
			if(nicks.isFile() && nombres.isFile() && mails.isFile() && list1.isFile() && list2.isFile())
			{
				FileInputStream fis = new FileInputStream("nicks");
				ObjectInputStream ois = new ObjectInputStream(fis);
				nick_pass = (HashMap) ois.readObject();
				ois.close();
				fis.close();
				fis = new FileInputStream("nombres");
				ois = new ObjectInputStream(fis);
				nick_nombre = (HashMap) ois.readObject();
				ois.close();
				fis.close();
				fis = new FileInputStream("mails");
				ois = new ObjectInputStream(fis);
				nick_mail = (HashMap) ois.readObject();
				ois.close();
				fis.close();
				fis = new FileInputStream("list1");
				ois = new ObjectInputStream(fis);
				contactos = (HashMap) ois.readObject();
				ois.close();
				fis.close();
				fis = new FileInputStream("list2");
				ois = new ObjectInputStream(fis);
				buffer = (HashMap) ois.readObject();
				ois.close();
				fis.close();
			}
			} catch (IOException e) {
			System.out.println("No fue posible recuperar los datos de la base de datos");
			}
		}
}
