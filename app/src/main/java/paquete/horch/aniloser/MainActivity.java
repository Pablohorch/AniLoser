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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;

public class MainActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {

    Spinner spiEspecies;
    Spinner spiRaza;
    Spinner spiSize;
    Spinner spiEdad;

    Button btnAddFotoGaleria;
    ImageView imgAdd;
    SeekBar rango;


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

        spiEspecies.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.preference_category, listados.especies));

        String[] arrayEdad=new String[25];
        for (int x=0;x<arrayEdad.length;x++){
            arrayEdad[x]=x+"";
        }

        spiEdad.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.preference_category, arrayEdad));

        spiRaza.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.preference_category, listados.razaPerros));

        spiSize.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.preference_category, listados.size));




    }

    public void inicializador(){
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //Añadir

        spiEspecies=(Spinner) findViewById(R.id.spiEspecie);
        spiEdad=(Spinner) findViewById(R.id.spiEdad);
        spiRaza=(Spinner) findViewById(R.id.spiRaza);
        spiSize=(Spinner) findViewById(R.id.spiSize);

        imgAdd=(ImageView) findViewById(R.id.imgAnimalAdd);
        rango=(SeekBar) findViewById(R.id.rango);
        rango.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setTitle(progress+" ");

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

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
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            ScrollView add=(ScrollView) findViewById(R.id.scrollAdd);
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    add.setVisibility(View.GONE);

                    return true;
                case R.id.navigation_dashboard:
                 add.setVisibility(View.GONE);
                   return true;
                case R.id.navigation_notifications:
                    add.setVisibility(View.VISIBLE);

                    return true;
            }
            return false;
        }
    };

    public void clicfoto(View v){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿Cómo quiere obtener la imagen?")
                .setPositiveButton("Cámara", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        code = DESDE_CAMARA;
                        startActivityForResult(intent, code);
                    }
                })
                .setNegativeButton("Galería", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                        code = DESDE_GALERIA;
                        startActivityForResult(intent, code);
                    }
                });
        Dialog dialog = builder.create();
        dialog.show();


    }

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
        imgAdd.setImageBitmap(image);

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
