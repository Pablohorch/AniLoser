package paquete.horch.aniloser;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class adaptador extends RecyclerView.Adapter<adaptador.ViewHolder> implements View.OnClickListener{

    Context context;
    ArrayList<especie> espe;

    private View.OnClickListener listener;

    Handler comunicador=new Handler();

    public adaptador( ArrayList<especie> especie){
        this.context = null;
        this.espe = especie;

    }
///----------------------------ARRIba explicar lo de la especies el objecto --------------------------------
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.tarjetaslistado,parent,false);
        v.setOnClickListener(this);
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
    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView cliente;
        ImageView img;


        public ViewHolder(View itemView) {
            super(itemView);

            img = (ImageView) itemView.findViewById(R.id.imgAddRaza);
            cliente = (TextView) itemView.findViewById(R.id.txtAddRaza);
        }
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
    //-------------------------------------bitmap string

    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte=Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }



//------------------------------------------------------

    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);

        byte [] b=baos.toByteArray();
        String temp=Base64.encodeToString(b, Base64.DEFAULT);

        return temp;
    }
}