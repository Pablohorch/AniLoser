package paquete.horch.aniloser;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Range;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {


    public static final int CAPTURE_IMAGE_FULLSIZE_ACTIVITY_REQUEST_CODE = 1777;

    Button btnAddSeguimientoEspecie;
    Button btnAddSeguimientoRaza;
    Button btnAddSeguimientoFoto;
    Button btnAddSeguimientoOtros;
    Button btnAddImgAceptar;
    Button btnAddImgCancelar;




    //--------------------Variable del animal
    int salud=0;



    //----------- vvariable de la imagen----
    private static final int SELECT_FILE = 1;
    static int code = 0;
    private static final int DESDE_CAMARA = 1;
    private static final int DESDE_GALERIA = 2;
    Intent intent;
    //------------------------------------------


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        inicializador();


        String[] arrayEdad=new String[25];
        for (int x=0;x<arrayEdad.length;x++){
            arrayEdad[x]=x+"";
        }




    }

    public void inicializador(){
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //Añadir

        btnAddSeguimientoEspecie=(Button) findViewById(R.id.btnAddSeguimientoEspecie);
        btnAddSeguimientoRaza=(Button) findViewById(R.id.btnAddSeguimientoRaza);
        btnAddSeguimientoFoto=(Button) findViewById(R.id.btnAddSeguimientoFoto);
        btnAddSeguimientoOtros=(Button) findViewById(R.id.btnAddSeguimientoOtros);


//--------------------------------------------------------------------------------------------
        final RecyclerView listadoRaza=(RecyclerView) findViewById(R.id.listadoAddRazas);




        final ArrayList<especie> especies=new ArrayList<especie>();

        for (int x=0;x<listados.especies.length;x++){
            especies.add(new especie(listados.especies[x],listados.especieURL[x]));
            }


        listadoRaza.setLayoutManager(new GridLayoutManager(this,2));
        adaptador adap=clickDeListado(new adaptador(especies),listadoRaza,especies);
        listadoRaza.setAdapter(adap);
}

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Funcion de la barra de navegacion inferior
    public BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener  = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
           switch (item.getItemId()) {
                case R.id.navigation_home:
                     return true;
                case R.id.navigation_dashboard:
                   return true;
                case R.id.navigation_notifications:
                   return true;
            }
            return false;
        }
    };

    // Para obtener la imagen sea de galería o de cámara
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap image = null;
        if (requestCode == DESDE_CAMARA) {
            if(requestCode == DESDE_CAMARA && resultCode == RESULT_OK && data != null){
                image = (Bitmap) data.getParcelableExtra("data");
            }
        }
        if (requestCode == DESDE_GALERIA) {
            if(requestCode == DESDE_GALERIA && resultCode == RESULT_OK && data != null){
                Uri rutaImagen = data.getData();
                try {
                    image = BitmapFactory.decodeStream(new BufferedInputStream(getContentResolver().openInputStream(rutaImagen)));
                } catch (FileNotFoundException e) { }
            }

        }
        ((ImageView) findViewById(R.id.ImgAnimalAdd)).setImageBitmap(image);
    }

    public adaptador clickDeListado(adaptador AV, final RecyclerView listaAdd, Object x){
        final Object y=x;
     AV.setOnItemClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             ArrayList<especie> a=(ArrayList) y;
             String nombre=a.get(listaAdd.getChildAdapterPosition(v)).nombre;

             Boolean especieCompleta=btnAddSeguimientoEspecie.isEnabled();
             Boolean razaCompleta=btnAddSeguimientoRaza.isEnabled();


             if (especieCompleta==false){
                    btnAddSeguimientoEspecie.setText(nombre);
                    btnAddSeguimientoEspecie.setEnabled(true);

                 final ArrayList<especie> perros=new ArrayList<especie>();

                 for (int x=0;x<listados.razaPerros.length;x++){
                     perros.add(new especie(listados.razaPerros[x],listados.razaPerroURL[x]));
                 }
                 final adaptador adapP=new adaptador(perros);
                 listaAdd.setAdapter(adapP);
                 adapP.setOnItemClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         clickDeListado(adapP,listaAdd,perros);

                     }
                 });

             }else if(!razaCompleta){
                    listaAdd.setVisibility(View.GONE);
                    btnAddSeguimientoRaza.setText(nombre);
                    btnAddSeguimientoRaza.setEnabled(true);
             }
         }
     });
     return AV;
    }
    public void clickFotos(View v){
        Button a=(Button) v;
        if (a.getText().equals("Camara")){
            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            code = DESDE_CAMARA;
            startActivityForResult(intent, code);
        }else{
            intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            code = DESDE_GALERIA;
            startActivityForResult(intent, code);
        }
    }
}


class animal{

        String especie;
        String raza;
        String edad;
        String descripcion;
        String salud;
        String bitmap;
        String siz;

        public animal(String Aespecie,String Araza,String Asalud,String Aedad,String Adescripcion,String Asize,String Abitmap){
            especie=Aespecie;
            raza=Araza;
            edad=Aedad;
            descripcion=Adescripcion;
            salud=Asalud;
            bitmap=Abitmap;
            siz=Asize;
        }
  }

class especie{

    String nombre;
    String url;


    public especie(String Aespecie,String Araza){
        nombre=Aespecie;
        url=Araza;
     }
}


