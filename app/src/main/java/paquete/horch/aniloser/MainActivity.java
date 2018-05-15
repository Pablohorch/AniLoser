package paquete.horch.aniloser;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
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
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback, GoogleMap.OnMapLongClickListener,CalendarView.OnDateChangeListener{


    public static final int CAPTURE_IMAGE_FULLSIZE_ACTIVITY_REQUEST_CODE = 1777;

    Button btnAddSeguimientoEspecie;
    Button btnAddSeguimientoRaza;
    Button btnAddSeguimientoFoto;
    Button btnAddSeguimientoOtros;
    Button btnAddSeguimientoLugar;
    Button btnAddImgAceptar;
    Button btnAddImgCancelar;


    //--------------------Variable del animal
    int salud = 0;

    //-------------listado
    RecyclerView listadoRaza;

    //----------- vvariable de la imagen----
    private static final int SELECT_FILE = 1;
    static int code = 0;
    private static final int DESDE_CAMARA = 1;
    private static final int DESDE_GALERIA = 2;
    Intent intent;
    //------------------- Mapas -----------------------
    GoogleMap mapasAdd;
    private Marker animalMarket;
    MarkerOptions mapAddDondeEstaElAnimalOption;
    LatLng ubicacionSeleccionada;

    //--------------------Otros------------------------
    Spinner spiSize;
    CalendarView calendario;

    String fecha ="";
    SeekBar barradeSalud;

    RadioButton rdbTelef;
    RadioButton rdbCorreo;
    RadioButton rdbRedes;

    EditText txtContacto;
    EditText txtDescripcion;


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


        String[] arrayEdad = new String[25];
        for (int x = 0; x < arrayEdad.length; x++) {
            arrayEdad[x] = x + "";
        }

    }

    public void inicializador() {
        //-----------------------------------------Otros--------------------------------------------------
        spiSize=(Spinner) findViewById(R.id.spiSize);
        spiSize.setAdapter( new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, listados.size));
        calendario=(CalendarView) findViewById(R.id.calendarioDiasMaximos);
        calendario.setOnDateChangeListener(this);
        barradeSalud=(SeekBar) findViewById(R.id.saludDelAnimal);

        rdbTelef=(RadioButton) findViewById(R.id.rdbTelefono);
        rdbCorreo=(RadioButton) findViewById(R.id.rdbCorreo);
        rdbRedes=(RadioButton) findViewById(R.id.rdbRedes);

        txtContacto=(EditText) findViewById(R.id.txtContacto);

        txtDescripcion=(EditText) findViewById(R.id.txtDescripcion);


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //-------------------------Añadir-------------------------

        btnAddSeguimientoEspecie = (Button) findViewById(R.id.btnAddSeguimientoEspecie);
        btnAddSeguimientoRaza = (Button) findViewById(R.id.btnAddSeguimientoRaza);
        btnAddSeguimientoFoto = (Button) findViewById(R.id.btnAddSeguimientoFoto);
        btnAddSeguimientoOtros = (Button) findViewById(R.id.btnAddSeguimientoOtros);

        btnAddImgAceptar = (Button) findViewById(R.id.btnAddImgAceptar);
        btnAddImgCancelar = (Button) findViewById(R.id.btnAddImgCancelar);
        btnAddSeguimientoLugar=(Button) findViewById(R.id.btnSeguimientoLugar);


//--------------------------------------------------------------------------------------------
        listadoRaza = (RecyclerView) findViewById(R.id.listadoAddRazas);


        ArrayList<especie> especies = new ArrayList<especie>();

        for (int x = 0; x < listados.especies.length; x++) {
            especies.add(new especie(listados.especies[x], listados.especieURL[x]));
        }


        listadoRaza.setLayoutManager(new GridLayoutManager(this, 2));
        adaptador adap = clickDeListado(new adaptador(especies), listadoRaza, especies);
        listadoRaza.setAdapter(adap);

//--------------------------------------- Mapas ------------------------------------------------
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapaDondeSePerdio);

        mapFragment.getMapAsync(this);
        mapAddDondeEstaElAnimalOption = new MarkerOptions().position(new LatLng(0, 0)).title("Que te gusta marcar");


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

    public BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener  = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override    public boolean onNavigationItemSelected(@NonNull MenuItem item) {   switch (item.getItemId()) {
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
            if (requestCode == DESDE_CAMARA && resultCode == RESULT_OK && data != null) {
                image = (Bitmap) data.getParcelableExtra("data");
            }
        }
        if (requestCode == DESDE_GALERIA) {
            if (requestCode == DESDE_GALERIA && resultCode == RESULT_OK && data != null) {
                Uri rutaImagen = data.getData();
                try {
                    image = BitmapFactory.decodeStream(new BufferedInputStream(getContentResolver().openInputStream(rutaImagen)));
                } catch (FileNotFoundException e) {
                }
            }

        }
        ((ImageView) findViewById(R.id.ImgAnimalAdd)).setImageBitmap(image);
        btnAddImgCancelar.setEnabled(true);
        btnAddImgAceptar.setEnabled(true);

    }

    public adaptador clickDeListado(adaptador AV, final RecyclerView listaAdd, Object x) {
        final Object y = x;
        AV.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<especie> a = (ArrayList) y;
                String nombre = a.get(listaAdd.getChildAdapterPosition(v)).nombre;

                Boolean especieCompleta = btnAddSeguimientoEspecie.isEnabled();
                Boolean razaCompleta = btnAddSeguimientoRaza.isEnabled();


                if (especieCompleta == false) {
                    btnAddSeguimientoEspecie.setText(nombre);
                    btnAddSeguimientoEspecie.setEnabled(true);

                    final ArrayList<especie> perros = new ArrayList<especie>();

                    for (int x = 0; x < listados.razaPerros.length; x++) {
                        perros.add(new especie(listados.razaPerros[x], listados.razaPerroURL[x]));
                    }
                    final adaptador adapP = new adaptador(perros);
                    listaAdd.setAdapter(adapP);
                    adapP.setOnItemClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            clickDeListado(adapP, listaAdd, perros);

                        }
                    });

                } else if (!razaCompleta) {
                    listaAdd.setVisibility(View.GONE);
                    btnAddSeguimientoRaza.setText(nombre);
                    btnAddSeguimientoRaza.setEnabled(true);
                    ((ScrollView) findViewById(R.id.scrollAddImagen)).setVisibility(View.VISIBLE);
                }
            }
        });
        return AV;
    }

    public void clickFotos(View v) {
        Button a = (Button) v;
        if (a.getText().equals("Camara")) {
            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            code = DESDE_CAMARA;
            startActivityForResult(intent, code);
        } else {
            intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            code = DESDE_GALERIA;
            startActivityForResult(intent, code);
        }
    }

    public void clickCancelarFoto(View v) {
        ((ImageView) findViewById(R.id.ImgAnimalAdd)).setImageBitmap(null);
    }

    public void clickAceptarFoto(View v) {
        ((ScrollView) findViewById(R.id.scrollAddImagen)).setVisibility(View.GONE);
        ((ScrollView) findViewById(R.id.scrollAddUbicacion)).setVisibility(View.VISIBLE);
        btnAddSeguimientoFoto.setEnabled(true);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapasAdd = googleMap;
        animalMarket = mapasAdd.addMarker(mapAddDondeEstaElAnimalOption);
        mapasAdd.setOnMapLongClickListener(this);

    }

    @Override
    public void onMapLongClick(LatLng latLng) {

       mapAddDondeEstaElAnimalOption.position(latLng);
       mapasAdd.moveCamera(CameraUpdateFactory.newLatLng(latLng));
       animalMarket.remove();
       animalMarket= mapasAdd.addMarker(mapAddDondeEstaElAnimalOption);
       ubicacionSeleccionada=latLng;
        ((Button) findViewById(R.id.btnAddAsignarUbicacion)).setEnabled(true);
 }

    public void clikcAsignaraUBI(View v){
     AlertDialog.Builder builder = new AlertDialog.Builder(this);
     builder.setMessage("¿Esta seguro que es la ubicacion del animal?")
             .setTitle("Advertencia")
             .setCancelable(false)
             .setNegativeButton("Cancelar",
                     new DialogInterface.OnClickListener() {
                         public void onClick(DialogInterface dialog, int id) {
                             dialog.cancel();
                         }
                     })
             .setPositiveButton("Continuar",
                     new DialogInterface.OnClickListener() {
                         public void onClick(DialogInterface dialog, int id) {
                             ((ScrollView) findViewById(R.id.scrollAddUbicacion)).setVisibility(View.GONE);
                             ((ScrollView) findViewById(R.id.scrollAddOtros)).setVisibility(View.VISIBLE);
                             btnAddSeguimientoLugar.setEnabled(true);

                         }
                     });
     AlertDialog alert = builder.create();
     alert.show();
 }


 //---------AceptarAnimal-----------------------

    public void clicAceptarAnimal(View v){
        String viaContact="";
        if (rdbTelef.isChecked())
            viaContact="Telefono";
        if (rdbCorreo.isChecked())
            viaContact="Correo Electronico";
        if (rdbRedes.isChecked())
            viaContact="Red Social";


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿Es la siguiente informacion correcta? \n " +
                "Especie : "+btnAddSeguimientoEspecie.getText()+"\n " +
                "Raza : "+btnAddSeguimientoRaza.getText()+"\n " +
                "Ubicacion : "+btnAddSeguimientoEspecie.getText()+"\n " +
                "------------------Tu información---------------- \n" +
                "Via de contacto : "+viaContact+"\n " +
                "Contacto : "+txtContacto.getText().toString()+"\n " +
                "Fecha maxima : "+fecha+"\n " +
                "Tamaño : "+spiSize.getSelectedItem().toString()+"\n ")
                .setTitle("Cuidado")
                .setCancelable(false)
                .setNegativeButton("Cancelar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                .setPositiveButton("Continuar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

 //---------------Reinicio-----------------------------

    public void clicBtnSeguimiento(View v){
       int btn=v.getId();
            setTitle(btn+"");
        if (btn==(btnAddSeguimientoEspecie.getId())){
            try {
                reinicioEspecie();
            }catch (Exception e){
                setTitle("error "+e.getMessage());
            }

        }

        if (btn==(btnAddSeguimientoFoto.getId())){
            reinicioImagen();
        }
        if (btn==(btnAddSeguimientoLugar.getId())){
            reinicioUbi();
        }
        if (btn==(btnAddSeguimientoOtros.getId())){
            reinicioOtros();
        }


    }

    public void reinicioEspecie(){
        btnAddSeguimientoEspecie.setText("Especies");
        ArrayList<especie> especies = new ArrayList<especie>();

        for (int x = 0; x < listados.especies.length; x++) {
            especies.add(new especie(listados.especies[x], listados.especieURL[x]));
        }

        listadoRaza.setLayoutManager(new GridLayoutManager(this, 2));
        adaptador adap = clickDeListado(new adaptador(especies), listadoRaza, especies);
        listadoRaza.setAdapter(adap);

        reinicioImagen();

    }
    public void reinicioRaza(){

        String especie=btnAddSeguimientoEspecie.getText().toString();

        final ArrayList<especie> perros = new ArrayList<especie>();

        for (int x = 0; x < listados.razaPerros.length; x++) {
            perros.add(new especie(listados.razaPerros[x], listados.razaPerroURL[x]));
        }
        final adaptador adapP = new adaptador(perros);
        listadoRaza.setAdapter(adapP);
        adapP.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickDeListado(adapP, listadoRaza, perros);

            }});

        reinicioImagen();
    }
    public void reinicioImagen(){
        ((ImageView) findViewById(R.id.ImgAnimalAdd)).setImageBitmap(null);
        btnAddImgCancelar.setEnabled(false);
        btnAddImgAceptar.setEnabled(false);
       // reinicioUbi();
    }
    public void reinicioUbi(){
        ((Button) findViewById(R.id.btnAddAsignarUbicacion)).setEnabled(false);
              reinicioOtros();

    }
    public void reinicioOtros(){
        rdbRedes.setChecked(false);
        rdbCorreo.setChecked(false);
        rdbTelef.setChecked(false);

        txtContacto.setText("");

        txtDescripcion.setText("");

        barradeSalud.setProgress(0);

        //calendario.setDate(System.currentTimeMillis(),false,true);

        spiSize.setAdapter( new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, listados.size));
    }

    @Override
    public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
        fecha=dayOfMonth+"/"+month+"/"+year;
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


/*
   //Funcion de la barra de navegacion inferior
    public BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener  = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override    public boolean onNavigationItemSelected(@NonNull MenuItem item) {   switch (item.getItemId()) {
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

 */