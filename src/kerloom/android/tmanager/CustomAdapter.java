package kerloom.android.tmanager;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CustomAdapter extends BaseAdapter{

	private ArrayList data; //ArrayList donde se pasa info para el layout propio
	Context c;
	
	CustomAdapter(ArrayList arrayList, Context context){
		data = arrayList;
		c = context;
	}
	
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return data.get(position);
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View v = convertView;
		if (v == null){
			LayoutInflater vi = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.lista_partidos, null);
		}
		
		TextView tvEquipo1 = (TextView) v.findViewById(R.id.tvEquipo1); //Inicializar
		TextView tvEquipo2 = (TextView) v.findViewById(R.id.tvEquipo2);
		TextView tvPuntos1 = (TextView) v.findViewById(R.id.tvPuntos1);
		TextView tvPuntos2 = (TextView) v.findViewById(R.id.tvPuntos2);
		
		PartidoDetalles partido = (PartidoDetalles) data.get(position); //Tomar el objeto del array
		
		tvEquipo1.setText(partido.Equipo1); //Poner valores correspondientes en la vista
		tvEquipo2.setText(partido.Equipo2);
		tvPuntos1.setText(partido.puntos1);
		tvPuntos2.setText(partido.puntos2);
		
		
		return v; //Regresar vista propia
	}

}
