
package paquete.horch.aniloser;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
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
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback, GoogleMap.OnMapLongClickListener,CalendarView.OnDateChangeListener {

    //-----------ftp

    public static boolean modoBD=true;

    public static final int CAPTURE_IMAGE_FULLSIZE_ACTIVITY_REQUEST_CODE = 1777;


    static int width = 0; // ancho absoluto en pixels
    static int height = 0; // alto absoluto en pixels


    Context contexto=this;


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
    final Handler comunicadorConUI = new Handler();


    //----------- vvariable de la imagen----
    private static final int SELECT_FILE = 1;
    Bitmap image = null;
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

    String fecha = "";
    SeekBar barradeSalud;

    RadioButton rdbTelef;
    RadioButton rdbCorreo;
    RadioButton rdbRedes;

    EditText txtContacto;
    EditText txtDescripcion;

    //-------------------------------------------------------LISTADO----------------------------------------------

    RecyclerView listadoPerdidos;
    static ArrayList<Button> listaBotonesSalud = new ArrayList<Button>();

    ArrayList<animal> listaAnimalInicioAnuncio = new ArrayList<animal>();


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


        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        width = metrics.widthPixels; // ancho absoluto en pixels
        height = metrics.heightPixels; // alto absoluto en pixels

        //-----------------------------------------Otros--------------------------------------------------
        spiSize = (Spinner) findViewById(R.id.spiSize);
        spiSize.setAdapter(new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, listados.size));
        calendario = (CalendarView) findViewById(R.id.calendarioDiasMaximos);
        calendario.setOnDateChangeListener(this);
        barradeSalud = (SeekBar) findViewById(R.id.saludDelAnimal);

        rdbTelef = (RadioButton) findViewById(R.id.rdbTelefono);
        rdbCorreo = (RadioButton) findViewById(R.id.rdbCorreo);
        rdbRedes = (RadioButton) findViewById(R.id.rdbRedes);

        txtContacto = (EditText) findViewById(R.id.txtContacto);

        txtDescripcion = (EditText) findViewById(R.id.txtDescripcion);


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //-------------------------Añadir-------------------------

        btnAddSeguimientoEspecie = (Button) findViewById(R.id.btnAddSeguimientoEspecie);
        btnAddSeguimientoRaza = (Button) findViewById(R.id.btnAddSeguimientoRaza);
        btnAddSeguimientoFoto = (Button) findViewById(R.id.btnAddSeguimientoFoto);
        btnAddSeguimientoOtros = (Button) findViewById(R.id.btnAddSeguimientoOtros);

        btnAddImgAceptar = (Button) findViewById(R.id.btnAddImgAceptar);
        btnAddImgCancelar = (Button) findViewById(R.id.btnAddImgCancelar);
        btnAddSeguimientoLugar = (Button) findViewById(R.id.btnSeguimientoLugar);


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


        //-----------------------------------------------------------------------------------------
        listadoPerdidos = (RecyclerView) findViewById(R.id.listadoAnimalesPerdidos);
        ejecutorInicialPerdido();
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

    public BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    ((LinearLayout) findViewById(R.id.RegistroAnimal)).setVisibility(View.GONE);
                    listadoPerdidos.setVisibility(View.VISIBLE);
                    return true;
                case R.id.navigation_dashboard:
                    ((LinearLayout) findViewById(R.id.RegistroAnimal)).setVisibility(View.GONE);
                    listadoPerdidos.setVisibility(View.GONE);
                    return true;
                case R.id.navigation_notifications:
                    ((LinearLayout) findViewById(R.id.RegistroAnimal)).setVisibility(View.VISIBLE);
                    listadoPerdidos.setVisibility(View.GONE);
                    return true;
            }
            return false;
        }
    };

    // Para obtener la imagen sea de galería o de cámara
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap image = null;

        //Concidiones desabilitadas
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
                } catch (FileNotFoundException e) {Log.e("Desde_Gareia","Error de Galeria de archivo no found");}
            }
        }

        if (image!=null) {
            Bitmap mapaDeBitJPG=null;
            btnAddImgCancelar.setEnabled(true);
            btnAddImgAceptar.setEnabled(true);
            Save guardado=new Save();
            guardado.SaveImage(contexto,image,"guardadoTemporalBitMapRegistro");

            File imgFile = new File(contexto.getFilesDir(),"guardadoTemporalBitMapRegistro.jpg");
            if(imgFile.exists()) {
                Log.e(TAG, "Existe la foto");
               mapaDeBitJPG=BitmapFactory.decodeFile(imgFile.getAbsolutePath());
               }
            ((ImageView) findViewById(R.id.ImgAnimalAdd)).setImageBitmap(mapaDeBitJPG);

        }

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
        String nombreImagen="fotoReguistrolistaFTP";
        ((ImageView) findViewById(R.id.ImgAnimalAdd)).buildDrawingCache();
        Bitmap imagen=((ImageView) findViewById(R.id.ImgAnimalAdd)).getDrawingCache();

        ((ScrollView) findViewById(R.id.scrollAddImagen)).setVisibility(View.GONE);
        ((ScrollView) findViewById(R.id.scrollAddUbicacion)).setVisibility(View.VISIBLE);
        btnAddSeguimientoFoto.setEnabled(true);
        Save guardado=new Save();
        guardado.SaveImage(this,imagen,nombreImagen);



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

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();

        final Bitmap mapaBit=((ImageView) findViewById(R.id.ImgAnimalAdd)).getDrawingCache();

        String especie=btnAddSeguimientoEspecie.getText().toString();
        String raza=btnAddSeguimientoRaza.getText().toString();
        final String descripcion=txtDescripcion.getText().toString();
        final String ubicacion=ubicacionSeleccionada.longitude+"-"+ubicacionSeleccionada.latitude;
        final String viaContacto=viaContact;
        final String contacto=txtContacto.getText().toString();
        final String fechaActual=dateFormat.format(date);
        final String fechaMaxima=fecha;
        final String grandor=spiSize.getSelectedItem().toString();
        final String salud=barradeSalud.getProgress()+"";
        String urlImagen="";




        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿Es la siguiente informacion correcta? \n " +
                "Especie : "+btnAddSeguimientoEspecie.getText()+"\n " +
                "Raza : "+btnAddSeguimientoRaza.getText()+"\n " +
                "Ubicacion : "+"\n longitud: "+ubicacionSeleccionada.longitude+"\n Latitud: "+ubicacionSeleccionada.latitude+"\n " +
                "------------------Tu información---------------- \n" +
                "Via de contacto : "+viaContact+"\n " +
                "Contacto : "+txtContacto.getText().toString()+"\n " +
                "Fecha maxima : "+fecha+"\n " +
                "Tamaño : "+spiSize.getSelectedItem().toString()+"\n "+
                "Nivel de Salud : "+barradeSalud.getProgress()+"\n ")
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

                                if (modoBD){

                                    executor("insert","insert into animales(size,fechaMaxima,fechaActual,coordenadas,urlImagen,viaContacto,textoContacto,descripcion,salud,idRazaFK,idUsuarioFK) " +
                                            "values " +
                                            "('"+grandor+"','"+fechaMaxima+"','"+fechaActual+"','"+ubicacionSeleccionada.longitude+"-"+ubicacionSeleccionada.latitude +"'," +
                                                    "'"+BitMapToString(mapaBit)+"','"+viaContacto+"','"+contacto+"','"+descripcion+"',"+salud+",22,1)"
                                            ,"");
                                }
                                else{

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {

                                        String nombreArchivoParaSubir="fotoReguistrolistaFTP.jpg";
                                        MyFTPClientFunctions ftpclient = new MyFTPClientFunctions();



                                        Boolean status = ftpclient.ftpConnect("files.000webhost.com",
                                                "pablohorchftpapp", "poliesterFTPDeLenovo", 21);
                                        if (status == true && new File(contexto.getFilesDir(),nombreArchivoParaSubir).exists()) {
                                            Log.e("FTP------", "Connection Success");




                                            Boolean staatus=ftpclient.ftpUpload("fotoReguistrolistaFTP.jpg","prueba.jpg","/imagenesAnimales",contexto);
                                            if (staatus)
                                                Log.e(TAG, "Ueeeee");
                                            else
                                                Log.e(TAG, "ErrorSubida");

                                        } else {
                                            Log.e("FTP-------", "Connection failed-No existe el archivo");
                                        }
                                        ftpclient.ftpDisconnect();
                                    }
                                }).start();}
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    //---------------Reinicio-----------------------------

    public void clicBtnSeguimiento(View v){
        int btn=v.getId();
        if (btn==(btnAddSeguimientoEspecie.getId())){
            try {
                reinicioEspecie();
            }catch (Exception e){
                setTitle("error "+e.getMessage());
            }

        }

        if (btn==(btnAddSeguimientoRaza.getId())){
            reinicioRaza();
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
        listadoRaza.setVisibility(View.VISIBLE);
        btnAddSeguimientoEspecie.setEnabled(false);
        btnAddSeguimientoEspecie.setText("Especies");
        ArrayList<especie> especies = new ArrayList<especie>();



        for (int x = 0; x < listados.especies.length; x++) {
            especies.add(new especie(listados.especies[x], listados.especieURL[x]));
        }

        listadoRaza.setLayoutManager(new GridLayoutManager(this, 2));
        adaptador adap = clickDeListado(new adaptador(especies), listadoRaza, especies);
        listadoRaza.setAdapter(adap);

        reinicioRaza();

    }
    public void reinicioRaza(){
        listadoRaza.setVisibility(View.VISIBLE);
        btnAddSeguimientoRaza.setEnabled(false);
        btnAddSeguimientoRaza.setText("Raza");
        String especie=btnAddSeguimientoEspecie.getText().toString();

        final ArrayList<especie> perros = new ArrayList<especie>();

        if(btnAddSeguimientoEspecie.isEnabled()) {
            for (int x=0; x < listados.razaPerros.length; x++) {
                perros.add(new especie(listados.razaPerros[x], listados.razaPerroURL[x]));
            }
            final adaptador adapP=new adaptador(perros);
            listadoRaza.setAdapter(adapP);
            adapP.setOnItemClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickDeListado(adapP, listadoRaza, perros);

                }
            });
        }
        reinicioImagen();
    }
    public void reinicioImagen(){
        if (btnAddSeguimientoRaza.isEnabled()){
            ((ScrollView) findViewById(R.id.scrollAddImagen)).setVisibility(View.VISIBLE);}
        else{ ((ScrollView) findViewById(R.id.scrollAddImagen)).setVisibility(View.GONE);}

        btnAddSeguimientoFoto.setEnabled(false);
        image=null;
        ((ImageView) findViewById(R.id.ImgAnimalAdd)).setImageBitmap(null);

        btnAddImgCancelar.setEnabled(false);
        btnAddImgAceptar.setEnabled(false);
        reinicioUbi();
    }
    public void reinicioUbi(){
        btnAddSeguimientoLugar.setEnabled(false);

        if (btnAddSeguimientoFoto.isEnabled()){
            ((ScrollView) findViewById(R.id.scrollAddUbicacion)).setVisibility(View.VISIBLE);}
        else{ ((ScrollView) findViewById(R.id.scrollAddUbicacion)).setVisibility(View.GONE);}

        ((Button) findViewById(R.id.btnAddAsignarUbicacion)).setEnabled(false);
        reinicioOtros();

    }
    public void reinicioOtros(){
        if (btnAddSeguimientoLugar.isEnabled()){
            ((ScrollView) findViewById(R.id.scrollAddOtros)).setVisibility(View.VISIBLE);}
        else{ ((ScrollView) findViewById(R.id.scrollAddOtros)).setVisibility(View.GONE);}


        btnAddSeguimientoOtros.setEnabled(false);
        rdbRedes.setChecked(false);
        rdbCorreo.setChecked(false);
        rdbTelef.setChecked(false);

        txtContacto.setText("");

        txtDescripcion.setText("");

        barradeSalud.setProgress(0);

        spiSize.setAdapter( new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, listados.size));
    }

    @Override
    public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
        fecha=dayOfMonth+"/"+month+"/"+year;
    }


    //----------------------------Listado inicial de aniamles que se han encontrado-----------------------------------
    public void ejecutorInicialPerdido(){

        executor("select","select * from animales",null);



        for (int x=0;x<listaBotonesSalud.size();x++){
            listaBotonesSalud.get(x).setWidth(950);
        }
    }

    @SuppressLint("StaticFieldLeak")
    public void executor(String lugar,String modo, String datos){
        new AsyncTask<String, String, Vector<String>>() {
            @Override

            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Vector<String> doInBackground(String... strings) {

                Vector<String> a=new Vector<String>();
                Connection con=ConexionBD();

                if (strings[0].equals("select")){
                    try {
                        Statement st=con.createStatement();
                        ResultSet rs=st.executeQuery(strings[1]);
                        Log.e("While de mierda","NO ENTRA CREO JAJAJA");

                        while (rs.next()){
                            Log.e("While de mierda","Entra con toda seguridad");

                            try {

                                listaAnimalInicioAnuncio.add(new animal(rs.getInt(1),rs.getString(2),rs.getString(3),rs.getString(4),
                                        rs.getString(5),rs.getString(6),rs.getString(7),
                                        rs.getString(8),rs.getString(9),rs.getInt(10),rs.getInt(11),rs.getInt(12)));


                                comunicadorConUI.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        setTitle(listaAnimalInicioAnuncio.size()+"");
                                        listadoPerdidos.setLayoutManager(new GridLayoutManager(contexto, 1));
                                        adaptadorEncontrados adap = new adaptadorEncontrados(new View(contexto),listaAnimalInicioAnuncio);
                                        listadoPerdidos.setAdapter(adap);

                                        adap.setOnItemClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Log.e("Estoy clicando cabron","Hijo puta-"+listadoPerdidos.getChildAdapterPosition(v));

                                                Intent intent = new Intent(contexto, animalPerdido.class);
                                                startActivity(intent);


                                            }
                                        });
                                    }
                                });
                            } catch (SQLException e) {

                            }

                        }

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                }
                if (strings[0].equals("insert")){
                    try {
                        Statement st=con.createStatement();
                        st.executeUpdate(strings[1]);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

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


class animal{

    int id;
    String size;
    String fechaMaxima;
    String fechaActual;
    String coordenadas;
    String urlImagen;
    String viaContacto;
    String textoContacto;
    String descripcion;
    int salud;
    int idRazaFK;
    int idUsuarioFK;

    public animal(int id,String size,String fechaMaxima,String fechaActual,String coordenadas,String urlImagen,String viaContacto,String textoContacto,String descripcion,int salud,int idRazaFK ,int idUsuarioFK ){
        this.id=id;
        this.size=size;
        this.fechaMaxima=fechaMaxima;
        this.fechaActual=fechaActual;
        this.coordenadas=coordenadas;
        this.urlImagen=urlImagen;
        this.viaContacto=viaContacto;
        this.textoContacto=textoContacto;
        this.descripcion=descripcion;
        this.salud=salud;
        this.idRazaFK=idRazaFK;
        this.idUsuarioFK=idUsuarioFK;


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

class anuncio{

    String especie;
    String raza;
    String lugar;
    String url;
    int salud;


    public anuncio(String especie,String raza,String lugar,int salud,String url){
        this.especie=especie;
        this.raza=raza;
        this.lugar=lugar;
        this.url=url;
        this.salud=salud;
    }
}
