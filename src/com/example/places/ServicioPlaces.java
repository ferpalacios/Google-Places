package com.example.places;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/*Esta clase crea la url para la busqueda de lugares y extrae la informacion
de dicha url. Los contenidos de dicha url se pueden obtener en XML o en JSON, en este
caso se ha optado por usar el formato JSON*/
public class ServicioPlaces{
	//La clave API que recibimos tras registrar la aplicacion en Google Code
	//En la seccion "APIs & auth -> APIs" tambien debemos activar Places API
    private String API_Key;
    public ServicioPlaces(String API_key) {
        this.API_Key = API_key;
    }

    public void setApiKey(String API_Key) {
        this.API_Key = API_Key;
    }

    public List<Place> BuscarLugares(double latitud, double longitud, double radio, String RestriccionLugar) 
    {
        String urlString = CrearUrl(latitud, longitud, radio, RestriccionLugar);
		try {
            String json = getContenidosUrl(urlString);
            JSONObject object = new JSONObject(json);
            JSONArray array = object.getJSONArray("results");
            //Creamos un array con todos los lugares
            ArrayList<Place> arrayList = new ArrayList<Place>();
            for (int i = 0; i < array.length(); i++) {
                try {
                    Place place = Place.jsonToPlace((JSONObject) array.get(i));
                    arrayList.add(place);
                } catch (Exception e) {
                }
            }
            return arrayList;
        } catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        return null;
    }
    /*Para buscar los lugares seguimos el ejemplo siguiente
	//https://maps.googleapis.com/maps/api/place/search/json?location=latitud,longitud&radius=radio&types=tipoLugar&sensor=false&key=API_key*/
    private String CrearUrl(double latitud, double longitud, double radio, String tipoLugar) {
         String urlString = new String("https://maps.googleapis.com/maps/api/place/search/json?");

        if (tipoLugar.equals("")) {
                urlString+=("&location=");
                urlString+=(Double.toString(latitud));
                urlString+=(",");
                urlString+=(Double.toString(longitud));
                urlString+=("&radius=");
                urlString+=Double.toString(radio);
                urlString+=("&sensor=false&key=" + API_Key);
        } else {
                urlString+=("&location=");
                urlString+=(Double.toString(latitud));
                urlString+=(",");
                urlString+=(Double.toString(longitud));
                urlString+=("&radius=");
                urlString+=Double.toString(radio);
                urlString+=("&types="+tipoLugar);
                urlString+=("&sensor=false&key=" + API_Key);
        }
        return urlString.toString();
    }
    //Almacenamos en un String todos los contenidos de la Url
    private String getContenidosUrl(String direccionURL) 
    {
        String contenido = new String("");

        try {
            URL url = new URL(direccionURL);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String linea;
            while ((linea = in.readLine()) != null) 
            {
                contenido+=(linea + "\n");
            }

            in.close();
        }

        catch (Exception e)
        {

            e.printStackTrace();

        }

        return contenido;
    }
    
}
