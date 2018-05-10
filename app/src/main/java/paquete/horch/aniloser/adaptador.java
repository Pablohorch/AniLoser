package paquete.horch.aniloser;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class adaptador extends RecyclerView.Adapter<adaptador.ViewHolder>{

    Context context;
    ArrayList<especie> espe;
    Handler comunicador=new Handler();

    public adaptador( ArrayList<especie> especie){
        this.context = null;
        this.espe = especie;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.tarjetaslistado,parent,false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final int i = position;
        holder.cliente.setText(espe.get(i).nombre);

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


    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView cliente;
        ImageView img;


        public ViewHolder(View itemView) {
            super(itemView);

            img = (ImageView) itemView.findViewById(R.id.imgAddRaza);
            cliente = (TextView) itemView.findViewById(R.id.txtAddRaza);




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