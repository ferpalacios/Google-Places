package com.example.places;

import java.util.List;
import android.os.Bundle;

import android.annotation.SuppressLint;
import android.app.Activity;

import android.app.ProgressDialog;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

@SuppressLint({ "HandlerLeak", "NewApi" })
public class CheckInActivity extends Activity implements LocationListener {

    private String IdLugares[];
    private String IdLugaresAnterior[];
    private TextView salida;
    private ListView listV;
    private LocationManager manejadorLocalizacion;
    private String proveedor;
    private Location localizacion;
    private Criteria criterioProv;
    private boolean HayLugares;
    ProgressDialog buscandoBarra;
    private Place DatosListView[];
	@Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        //Establecemos el Layout ListView como contenido de la ventana 
        setContentView(R.layout.lista);
        //Creamos el objeto manejador de localizaciones
    	manejadorLocalizacion = (LocationManager) getSystemService(LOCATION_SERVICE);
    	//Creamos el objeto que sirve para establecer el criterio de eleccion de proveedores de posicion
	    criterioProv = new Criteria();
	    //Buscamos el de mayor precision
    	criterioProv.setAccuracy(Criteria.ACCURACY_FINE);
    	ActualizarTodo();
    }
    public void ActualizarPos(){
    	//Buscamos el mejor proveedor que se ajusta al criterio
		proveedor = manejadorLocalizacion.getBestProvider(criterioProv, true);
		//Buscamos la ultima localizacion disponible para ese proveedor
		localizacion = manejadorLocalizacion.getLastKnownLocation(proveedor);
    }
    public void ActualizarLugares(){
    	//Debemos crear una hebra que realizara las operaciones de conexion a internet
    	//La conexion nunca debe realizarse en la hebra principal
	    new Thread(new Runnable(){
	    	public void run(){
	    		List<Place>datos = BuscarSitiosCercanos(localizacion.getLatitude(), localizacion.getLongitude(),500);
	    		//Creamos el mensaje con los datos para mandarselo a la hebra principal
	      		Message msg = new Message();
				msg.obj=datos;
				//Mandamos el mensaje
				manejador.sendMessage(msg);
				if (!HayLugares){
					//Como ya hemos mandado los datos, se puede eliminar el dialogo de progreso
					buscandoBarra.dismiss();
				}
	      	}
	    }).start();
    }
    void ActualizarTodo(){
	    ActualizarPos();
    	HayLugares=false;
    	//Mientras la hebra secundaria realiza la conexion a internet y busca los datos
    	//la principal mostrara un dialogo de progreso con un mensaje para el usuario
    	buscandoBarra = ProgressDialog.show(this,"Buscando lugares","Por favor, ten paciencia",true, false);
		ActualizarLugares();
    }
    //Creamos un objeto manejador, necesario para recibir los datos de la hebra secundaria
    Handler manejador = new Handler(){
    	@Override
        public void handleMessage(Message msg){
    		if (msg.obj!=null){
    			//Almacenamos los datos del mensaje
    			List<Place> datos = (List<Place>) msg.obj;
    			//Creamos el vector del objeto de tipo place y lo rellenamos con los datos de cada lugar
    			DatosListView = new Place[datos.size()];
    			for (int i=0; i<DatosListView.length; ++i)
    				DatosListView[i] = new Place(datos.get(i));
    			if (!HayLugares){
    				//Almacenamos los ID de los lugares
    				IdLugares = IdLugaresAnterior = new String[datos.size()];
    				for (int i=0; i<datos.size(); ++i){
    					IdLugares[i] = datos.get(i).getId();
    					IdLugaresAnterior[i] = datos.get(i).getId();
    				}
					HayLugares = true;
    				MostrarLugares();
    			}
    			else{
    				IdLugaresAnterior = IdLugares;
    				IdLugares = new String[datos.size()];
    				for (int i=0; i<datos.size(); ++i)
    					IdLugares[i] = datos.get(i).getId();
    			}
    		}
    	}
    };
    public void MostrarLugares(){
    	//Establecemos la lista a mostrar
    	listV = (ListView) findViewById(R.id.lista);
    	//Creamos un nuevo adaptador para los elementos de la lista
    	listV.setAdapter(new AdaptadorLista(this, R.layout.fila, DatosListView){
    		/*Implementamos el metodo abstracto que coloca cada dato del lugar en su
    		correspondiente seccion para mostrarlo como una fila en el ListView*/
    		@Override
			public void MuestraLugar(Place lugar, View view) {
		        if (lugar != null) {
		            TextView titulo = (TextView) view.findViewById(R.id.titulo); 
		            TextView descripcion = (TextView) view.findViewById(R.id.descripcion); 
		            ImageView icono = (ImageView) view.findViewById(R.id.icono); 
		            if (titulo != null) 
		            	titulo.setText(lugar.getNombre()); 
		            if (descripcion != null)
		            	descripcion.setText(lugar.getDireccion()); 
		            if (icono != null)
		            	icono.setImageBitmap(lugar.getIcono());
		        }
		        else{
		        	//Si no se encuentran lugares, se muestra un TextView que lo indica
			    	setContentView(R.layout.textview);
				    salida = (TextView) findViewById(R.id.salida);
			    	salida.setText("No se encuentran lugares cercanos\n");
			    }
			}
		});
    }
    public List<Place> BuscarSitiosCercanos(double latitud, double longitud, double radio){
    	//Creamos el nuevo objeto de tipo ServicioPlaces usando la API Key que hemos recibido de Google Code
    	ServicioPlaces servicio = new ServicioPlaces("AIzaSyBrPgwfYY6Zwmygxga7HmiVcwgEruhlMNw");
        /*Puede optarse por añadir el ultimo parametro si se quiere restringir la busqueda a un determinado
        tipo de lugar*/
        List<Place> salida = servicio.BuscarLugares(latitud,longitud,radio,"");
        return salida;
    }
    //Cuando el usuario comienza a interactuar, comprobamos si hay actualizaciones de posicion
    @Override    protected void onResume() {
        super.onResume();
        manejadorLocalizacion.requestLocationUpdates(proveedor, 10000, 1, this);
    }
    //Realizamos una actualizacion completa si tras abandonar la aplicacion el usuario, vuelve a iniciarla 
    @Override	 protected void onRestart(){
    	super.onRestart();
        ActualizarTodo();
    }
    //Anulamos las actualizaciones de posicion mientras no se ejecute la aplicacion
    @Override    protected void onPause() {
        super.onPause();
        manejadorLocalizacion.removeUpdates(this);
    }
    /*Si hay cambio de posicion, actualizamos los lugares si la distancia a la posicion anterior 
    es de 20 metros y mostramos los lugares si son diferentes a los actuales*/
	@Override
	public void onLocationChanged(Location pos) {
		// TODO Auto-generated method stub
		if (localizacion.distanceTo(pos)>=20){
			ActualizarPos();
			ActualizarLugares();
			if (IdLugares != IdLugaresAnterior){
				MostrarLugares();
			}
		}
	}
	/*Tanto si el proveedor es deshabilitado como habilitado de nuevo realizamos una actualizacion total
	para buscar el mejor proveedor otra vez*/
	@Override
	public void onProviderDisabled(String prov) {
		// TODO Auto-generated method stub
			ActualizarTodo();
	}
	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		ActualizarTodo();
	}
	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}
}

