package corp.redacted.game.serveur;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.nio.file.Files;
import java.net.URI;

import java.io.IOException;
import java.io.OutputStream;
import java.io.FileInputStream;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;

import java.util.Enumeration;
import java.lang.NullPointerException;

public class GestionHttp implements HttpHandler{
  String racine = "webapp";
  public static HttpServer hserver;
  public final static int PORT = 8888;

  /**
    * Démarre le serveur HTTP sur le port PORT
    * @return true si le serveur s'est démarré sans erreur, false sinon
  */
  public static boolean start(){
    String ip;
    try{  // On crée le serveur HTTP sur le port PORT
      ip = GestionHttp.getIP(); // On récupère l'IP
      System.out.println(ip);
      if(ip == null){
        return false;
      }
      hserver = HttpServer.create(new InetSocketAddress(ip, PORT), 0);
      hserver.createContext("/", new  GestionHttp());
      hserver.start();
      return true;
    }
    catch(Exception e){
      e.printStackTrace();
      return false;
    }
  }

  /**
    * Éteint le serveur HTTP sur le port PORT
  */
  public static void stop(){
    hserver.stop(0);
  }

  @Override
  public void handle(HttpExchange ex) throws IOException{
    OutputStream out;
    FileInputStream in;
    final byte[] tmp = new byte[65534];
    URI id = ex.getRequestURI();
    File file = new File("webapp" + id.getPath()).getCanonicalFile();
    int nbChar = 0;

    ex.sendResponseHeaders(200, 0);
    out = ex.getResponseBody();

    in = new FileInputStream(file);
    while ((nbChar = in.read(tmp)) >= 0){
      out.write(tmp, 0, nbChar);
    }
    in.close();
    out.close();
  }

  /**
    * Retourne notre adress IPV4 sur le réseau local
    * @return Notre adresse IPV4 sur le réseau local (ou null en cas d'erreur)
  */
  private static String getIP(){
    String ip = null;
    try{
      Enumeration<NetworkInterface> net;
      String[] splitAddr;
      Enumeration<InetAddress> listAddr;
      InetAddress addr;

      // On itère sur les interfaces pour trouver la bonne
      net = NetworkInterface.getNetworkInterfaces();
      while(net.hasMoreElements()){

        // On itère sur les addresses de l'interface pour trouver une IPV4
        listAddr = net.nextElement().getInetAddresses();
        while(listAddr.hasMoreElements()){
          addr = listAddr.nextElement();
          splitAddr = addr.getHostAddress().split("\\.");
          if(splitAddr.length != 0){
            if(splitAddr[0].equals("192")){ // La bonne IPV4 commence par 192
              ip = addr.getHostAddress();
            }
          }
        }
      }
    }
    catch(Exception e){
      e.printStackTrace();
    }
    return ip;
  }
}
