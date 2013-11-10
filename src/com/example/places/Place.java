package com.example.places;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


public class Place {
    private String id;
    private Bitmap icono;
    private String nombre;
    private String direccion; //Direccion en la que se encuentra el lugar

    public Place (String id, Bitmap icono, String nombre, String direccion){
    	this.id = id;
    	this.icono = icono;
    	this.nombre = nombre;
    	this.direccion = direccion;
    }
    public Place (Place lugar){
    	this.id = lugar.id;
    	this.icono = lugar.icono;
    	this.nombre = lugar.nombre;
    	this.direccion = lugar.direccion;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Bitmap getIcono() {
        return icono;
    }

    public void setIcono(Bitmap icono) {
        this.icono = icono;
    }
    
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    static Place jsonToPlace(JSONObject json) {
		try {
			String id = json.getString("id");
			Bitmap icono = ImagenDeUrl(json.getString("icon"));
			String nombre = json.getString("name");
			String direccion = json.getString("vicinity");
            Place lugar = new Place(id, icono, nombre, direccion);
            /*Con el metodo "getString" de json obtenemos los valores asociados a 
            los valores asignados a cada campo deseado del objeto json*/
            lugar.setIcono(ImagenDeUrl(json.getString("icon")));
            lugar.setNombre(json.getString("name"));
            lugar.setDireccion(json.getString("vicinity"));
            lugar.setId(json.getString("id"));
            return lugar;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return null;
    }
    /*Esta funcion nos permite obtener una imagen de tipo Bitmap a
    partir de una URL que se pasa como parametro*/
    public static Bitmap ImagenDeUrl(String url) {
    	URL imageUrl = null;
		HttpURLConnection conHttp = null; 
		try {
			//Abrimos la conexion Http de la URL
			imageUrl = new URL(url);
			conHttp = (HttpURLConnection) imageUrl.openConnection();
			conHttp.connect();
			//Decodificamos el contenido de la URL
			Bitmap imagen = BitmapFactory.decodeStream(conHttp.getInputStream());
			return imagen;
		} catch (IOException e) { 
		e.printStackTrace();
		}
		return null;
	}
}