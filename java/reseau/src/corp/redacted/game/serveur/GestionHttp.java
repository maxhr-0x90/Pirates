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

import java.net.InetSocketAddress;

public class GestionHttp implements HttpHandler{
  String racine = "webapp";
  public static HttpServer hserver;
  public final static int PORT = 8080;

  /**
    * Démarre le serveur HTTP sur le port PORT
  */
  public static void start(){
    try{  // On crée le serveur HTTP sur le port PORT
      hserver = HttpServer.create(new InetSocketAddress("161.3.34.43", PORT), 0);
      hserver.createContext("/", new  GestionHttp());
      hserver.start();
    }
    catch(Exception e){
      e.printStackTrace();
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
}
