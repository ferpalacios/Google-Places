package com.example.places;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/*Es necesario crear esta clase para adaptar los parametros de cada objeto Place
a cada campo de fila.xml*/ 
public abstract class AdaptadorLista extends BaseAdapter{

	    private Place lugares[]; 
	    private int R_layout_IdVista; 
	    private Context contexto;

	    public AdaptadorLista(Context contexto, int R_layout_IdVista, Place lugares[]) {
	        super();
	        this.contexto = contexto;
	        this.lugares = lugares; 
	        this.R_layout_IdVista = R_layout_IdVista; 
	    }

	    @Override
	    public View getView(int position, View view, ViewGroup parent) {
	    	//Instanciamos la vista mediante LayoutInflater
	        if (view == null) {
	        	LayoutInflater vi = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
	            view = vi.inflate(R_layout_IdVista, null); 
	        }
	        MuestraLugar (lugares[position], view);
	        return view; 
	    }

	    @Override
	    public int getCount() {
	        return lugares.length;
	    }

	    @Override
	    public Object getItem(int posicion) {
	        return lugares[posicion];
	    }

	    @Override
	    public long getItemId(int posicion) {
	        return posicion;
	    }
	    //Este metodo adapta cada parametro de lugar a cada campo de la vista view
	    public abstract void MuestraLugar (Place lugar, View view);

}
