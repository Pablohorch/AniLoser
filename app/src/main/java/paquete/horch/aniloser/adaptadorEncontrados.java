package paquete.horch.aniloser;


import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.data.ObjectExclusionFilterable;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Vector;


public class adaptadorEncontrados extends RecyclerView.Adapter<adaptadorEncontrados.ViewHolder> implements View.OnClickListener{

    Context context;
    ArrayList<animal> espe;

    private View.OnClickListener listener;

    Handler comunicador=new Handler();



    public adaptadorEncontrados( View view,ArrayList<animal> anuncio){

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
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final int i = position;

        new Thread(){

            @Override
            public void run() {
                super.run();
                Bitmap mapaBitOpcional=null;
                if (MainActivity.modoBD)
                    mapaBitOpcional =StringToBitMap(espe.get(i).urlImagen);
                else
                    mapaBitOpcional=getBitmapFromURL(espe.get(i).urlImagen);

                final Bitmap mapaBit=mapaBitOpcional;

                comunicador.post(new Runnable() {
                    @Override
                    public void run() {
                        Vector<TextView> elementosGraficos=new Vector<TextView>();
                        elementosGraficos.add(holder.especie);
                        elementosGraficos.add(holder.raza);

                        String[] x=executor("",""+espe.get(position).idRazaFK,"",elementosGraficos);
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

    @SuppressLint("StaticFieldLeak")
    public String[] executor(String lugar, String modo, String datos, final Vector<TextView> elementos){
        final String[] especieYRaza = {"",""};
        new AsyncTask<String, String, Vector<String>>() {
            @Override

            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Vector<String> doInBackground(String... strings) {

                Vector<String> a=new Vector<String>();
                Connection con=ConexionBD();

                    try {
                        Statement st=con.createStatement();
                        ResultSet rs=st.executeQuery("select * from razas where idRaza="+strings[1]);
                        rs.first();
                        String idEspecie= rs.getString(4);

                        final String raza=rs.getString(2);

                        rs=st.executeQuery("select nombreEspecie from especies where idEspecie="+idEspecie);
                        rs.first();
                        final String especie=rs.getString(1);

                        comunicador.post(new Runnable() {
                            @Override
                            public void run() {

                                 elementos.get(0).setText(especie);
                                 elementos.get(1).setText(raza);
                            }
                        });


                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                return a;
            }

            @Override
            protected void onProgressUpdate(String... values) {
                super.onProgressUpdate(values);
            }

            @Override
            protected void onPostExecute(Vector<String> strings) {

            }

            @Override
            protected void onCancelled() {
                super.onCancelled();
            }

        }.execute(lugar,modo,datos);

        Log.e("Resultado base de datos"," resultado: "+especieYRaza[0]+"-"+especieYRaza[1]);

        return especieYRaza;
    }

    public Connection ConexionBD(){
        Connection con=null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con= DriverManager.getConnection("jdbc:mysql://sql2.freesqldatabase.com:3306/sql2239597", "sql2239597", "dJ6%nG7!");
        } catch(Exception e) {
            e.printStackTrace();

        }
        return con;
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