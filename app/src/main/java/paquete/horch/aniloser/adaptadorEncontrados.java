package paquete.horch.aniloser;


import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class adaptadorEncontrados extends RecyclerView.Adapter<adaptadorEncontrados.ViewHolder> implements View.OnClickListener{

    Context context;
    ArrayList<anuncio> espe;

    private View.OnClickListener listener;

    Handler comunicador=new Handler();



    public adaptadorEncontrados( View view,ArrayList<anuncio> anuncio){

        this.context = null;
        this.espe = anuncio;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.listado_animal__encontrado,parent,false);
        v.setOnClickListener(this);
        return new ViewHolder(v);
    }



    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final int i = position;

        holder.especie.setText(espe.get(i).especie);
        holder.raza.setText(espe.get(i).raza);

        holder.saludNivel.setProgress(espe.get(i).salud);



        new Thread(){

            @Override
            public void run() {
                super.run();

                final Bitmap mapaBit =getBitmapFromURL(espe.get(i).url);
                comunicador.post(new Runnable() {
                    @Override
                    public void run() {
                        holder.img.setImageBitmap(mapaBit);
                    }
                });

            }
        }.start();

    }
    @Override
    public int getItemCount() {
        return espe.size();
    }

    public void setOnItemClickListener(View.OnClickListener listener){
        this.listener=listener;
    }

    @Override
    public void onClick(View v) {
        if (listener!=null){
            listener.onClick(v);
        }
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView especie;
        ImageView img;
        TextView raza;
        CardView tarjeta;
        ProgressBar saludNivel;

        public ViewHolder(View itemView) {
            super(itemView);

            img = (ImageView) itemView.findViewById(R.id.imgPerdidaRaza);
            especie = (TextView) itemView.findViewById(R.id.txtPerdidaEspecie);
            raza = (TextView) itemView.findViewById(R.id.txtPerdidaRaza);
            tarjeta=(CardView) itemView.findViewById(R.id.card);
            saludNivel=(ProgressBar) itemView.findViewById(R.id.saludNivel);

        }

    }


    private static OnItemClickListener onItemClickListener;


    public static interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }



    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }
}