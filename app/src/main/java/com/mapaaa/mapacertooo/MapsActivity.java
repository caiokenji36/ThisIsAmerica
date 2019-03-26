package com.mapaaa.mapacertooo;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng loc = null;
    private LatLng google = null;
    private LocationManager locationManager;
    private static final int REQUEST_GPS = 1;
    private Button startButton;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private TextView pergunta, pontos, bemvindo;
    private Button alterarNome;
    private Button botaoOk;
    private Button fotoButton;
    private EditText editResposta;
    public boolean teste, testeNome;
    private Marker marker;
    public String nome;
    private ImageView imageFoto;
    ArrayList<QuestionModel> questionModelArrayList;
    int currentPosition = 0;

    public MapsActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);









        startButton = (Button) findViewById(R.id.startButton);
        pergunta = (TextView) findViewById(R.id.perguntaID);
        alterarNome = (Button) findViewById(R.id.alterarNomeBotao);
        botaoOk = (Button) findViewById(R.id.botaoOk);
        fotoButton = (Button) findViewById(R.id.fotoButton);
        editResposta = (EditText) findViewById(R.id.editResposta);
        pontos = (TextView) findViewById(R.id.point);
        bemvindo = (TextView) findViewById(R.id.lbNome);

        teste=false;
        testeNome=false;

        alterarNome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //inserir dialogo do conteúdo
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(MapsActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_name, null);
                final EditText mNome = (EditText) mView.findViewById(R.id.name);
                final Button mButton = (Button) mView.findViewById(R.id.btnInserir);

                mBuilder.setView(mView);
                final AlertDialog dialCriat = mBuilder.create();

                mButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        nome = mNome.getText().toString();
                        bemvindo.setText(getString(R.string.Bem_vindo) + nome + " !");
                        dialCriat.hide();
                        testeNome = true;
                    }
                });

                dialCriat.show();
            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (testeNome == true) {
                    teste = true;
                    setUpQUestion();
                    setData();
                    startButton.setEnabled(false);
                }else {
                    erro();
                }
            }
        });

        botaoOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(teste == true) {
                    checkAnswer();

                }else{
                    erro();
                }
            }
        });



        fotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }

        });

    }

    //Aqui eu estou refazendo a operação do botão para que assim quando for perguntar pela segunda vez espere o usuáriio a responder
    public void testError(){
        botaoOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(teste == true) {
                    checkAnswer();

                }else{
                    erro();
                }
            }
        });
    }



    @Override
    protected void onStart() {
        super.onStart();
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            //verifica se deve exibir uma explicação sobre a necessidade da permissão
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(this, getString(R.string.message_gps),
                        Toast.LENGTH_SHORT).show();
            }
            //pede permissão
            ActivityCompat.requestPermissions(this, new String
                    []{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_GPS);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
                    0, locationListener);
        }
    }
    private LocationListener locationListener =
            new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[]
            permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_GPS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
                                0, locationListener);
                    }
                }
                break;
        }
    }





    private void erro() {
        Toast.makeText(this, R.string.erro, Toast.LENGTH_SHORT).show();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new
                Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) !=
                null) {
            startActivityForResult(takePictureIntent,
                    REQUEST_IMAGE_CAPTURE);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                Bitmap img = (Bitmap) bundle.get("data");

                Bitmap.createScaledBitmap(img, 120, 120, false);
                ImageView foto = (ImageView) findViewById(R.id.imageFoto);

                foto.setImageBitmap(img);

            }
        }
    }

    public void setUpQUestion(){

        questionModelArrayList = new ArrayList<>();

        questionModelArrayList.add(new QuestionModel(getString(R.string.capital_brasil), getString(R.string.Brasilia)));
        questionModelArrayList.add(new QuestionModel(getString(R.string.capital_argentina), getString(R.string.Buenos_aires)));
        questionModelArrayList.add(new QuestionModel(getString(R.string.capital_chile), getString(R.string.Santiago)));
        questionModelArrayList.add(new QuestionModel(getString(R.string.capital_paraguai), getString(R.string.assuncao)));
        questionModelArrayList.add(new QuestionModel("Capital do Uruguai", getString(R.string.Montevidéu)));
        questionModelArrayList.add(new QuestionModel("Capital da Bolívia", getString(R.string.La_paz)));
        questionModelArrayList.add(new QuestionModel("Capital do Peru", getString(R.string.Lima)));
        questionModelArrayList.add(new QuestionModel("Capital do Equador", getString(R.string.Quito)));
        questionModelArrayList.add(new QuestionModel("Capital do Suriname", getString(R.string.Paramaribo)));
        questionModelArrayList.add(new QuestionModel("Capital da Guiana", getString(R.string.Georgetown)));
        questionModelArrayList.add(new QuestionModel("Capital da Venezuela", "Caracas"));
        questionModelArrayList.add(new QuestionModel("Capital da Colômbia", "Bogotá"));
        questionModelArrayList.add(new QuestionModel("Capital do Panamá", "Panamá"));
        questionModelArrayList.add(new QuestionModel("Capital de El Salvador", "San Salvador"));
        questionModelArrayList.add(new QuestionModel("Capital de Honduras", "Tegucigalpa"));
        questionModelArrayList.add(new QuestionModel("Capital da Costa Rica", "San José"));
        questionModelArrayList.add(new QuestionModel("Capital da Guatemala", "Guatemala"));
        questionModelArrayList.add(new QuestionModel("Capital de Cuba", "Havana"));
        questionModelArrayList.add(new QuestionModel("Capital da República Dominicana", "Santo Domingo"));
        questionModelArrayList.add(new QuestionModel("Capital da Jamaica ", "Kingston"));
        questionModelArrayList.add(new QuestionModel("Capital do Haiti", "Porto Príncipe"));
        questionModelArrayList.add(new QuestionModel("Capital de Barbados", "Bridgetown"));
        questionModelArrayList.add(new QuestionModel("Capital de Granada", "Saint George's"));
        questionModelArrayList.add(new QuestionModel("Capital do Bahamas", "Nassau"));
        questionModelArrayList.add(new QuestionModel("Capital do Dominica", "Roseau"));
        questionModelArrayList.add(new QuestionModel("Capital de Santa Lúcia", "Castries"));
        questionModelArrayList.add(new QuestionModel("Capital de Belize", "Belmopan"));
        questionModelArrayList.add(new QuestionModel("Capital do México", "Cidade do México"));
        questionModelArrayList.add(new QuestionModel("Capital do Estados Unidos", "Washington"));
        questionModelArrayList.add(new QuestionModel("Capital do Canadá", "Ottawa"));

        Collections.shuffle(questionModelArrayList,new Random(System.nanoTime())); //nanotime e uma semente para sempre mudar a questao
    }

    public int contador=0;

    public void setData(){
        if(questionModelArrayList.size()>currentPosition) {
            pergunta.setText(questionModelArrayList.get(currentPosition).getQuestionString());
        }else{

            Toast.makeText(this,"Voce acabou o jogo",Toast.LENGTH_SHORT).show();
            contador=0;
            pontos.setText(String.valueOf(contador));
            testeNome = false;
            teste = false;
            startButton.setEnabled(true);

        }

    }
    boolean resposta = false;

    //teste com a opção de duas vezes já pré definida
    public void checkAnswer(){
        String answerString = editResposta.getText().toString().trim();

        if (answerString.equalsIgnoreCase(questionModelArrayList.get(currentPosition).getAnswer())) {

            //função de quando a resposta é conciderado correto
            respostaCerta();

        } else {

            if(resposta == false){

                //dando a opção de tentar mais uma vez
                Toast.makeText(this, "Tente novamente", Toast.LENGTH_SHORT).show();

                //função que chama o botão novamente
                testError();
                editResposta.setText("");

                //declarando que é para ir para declaração de resposta errada
                resposta = true;

            }else {

                //função de resposta errada
                respostaErrada();
                resposta = false;
            }

        }

    }
    public void respostaCerta(){

        //declarações de contúdo
        String answerString = editResposta.getText().toString().trim();
        Toast.makeText(this, "Resposta certa", Toast.LENGTH_SHORT).show();
        currentPosition++;
        contador = contador + 2;
        pontos.setText(String.valueOf(contador));
        setData();
        editResposta.setText("");

        float zoomLevel = 5.0f;
        switch (answerString.toLowerCase().trim()) {
            case "brasilia":
                google = new LatLng(-15.7750836, -48.0772978);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(google, zoomLevel));
                break;

            case "buenos aires":
                google = new LatLng(-34.6157437, -58.5733862);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(google, zoomLevel));
                break;

            case "santiago":
                google = new LatLng(-33.4724727, -70.9100318);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(google, zoomLevel));
                break;

            case "assunção":
                google = new LatLng(-25.2968361, -57.6681303);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(google, zoomLevel));
                break;

            case "montevidéu":
                google = new LatLng(-34.821018, -56.3765264);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(google, zoomLevel));
                break;

            case "la paz":
                google = new LatLng(-16.5206657, -68.2641674);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(google, zoomLevel));
                break;

            case "lima":
                google = new LatLng(-11.7899993, -78.9482695);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(google, zoomLevel));
                break;

            case "quito":
                google = new LatLng(-0.1865921, -78.7107495);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(google, zoomLevel));
                break;

            case "paramaribo":
                google = new LatLng(5.8483205, -55.2478883);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(google, zoomLevel));
                break;

            case "georgetown":
                google = new LatLng(6.787627, -58.1865532);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(google, zoomLevel));
                break;

            case "caracas":
                google = new LatLng(10.4683612, -67.0304561);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(google, zoomLevel));
                break;

            case "bogota":
                google = new LatLng(4.6482422, -74.3880181);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(google, zoomLevel));
                break;

            case "panama":
                google = new LatLng(8.8623226, -79.7010959);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(google, zoomLevel));
                break;

            case "san salvador":
                google = new LatLng(13.6914757, -89.2497138);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(google, zoomLevel));
                break;

            case "tegucigalpa":
                google = new LatLng(14.0839053, -87.2750141);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(google, zoomLevel));
                break;

            case "san jose":
                google = new LatLng(9.935607, -84.1833856);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(google, zoomLevel));
                break;

            case "guatemala":
                google = new LatLng(14.6262096, -90.5626019);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(google, zoomLevel));
                break;

            case "havana":
                google = new LatLng(23.0506249, -82.4730913);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(google, zoomLevel));
                break;

            case "santo domingo":
                google = new LatLng(18.4800103, -70.0170529);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(google, zoomLevel));
                break;

            case "kingston":
                google = new LatLng(18.0179237, -76.8706976);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(google, zoomLevel));
                break;

            case "porto principe":
                google = new LatLng(18.5790242, -72.3545014);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(google, zoomLevel));
                break;

            case "bridgetown":
                google = new LatLng(13.1013093, -59.6315575);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(google, zoomLevel));
                break;

            case "saint george's":
                google = new LatLng(12.0539679, -61.7588454);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(google, zoomLevel));
                break;

            case "nassau":
                google = new LatLng(25.0324949, -77.5471821);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(google, zoomLevel));
                break;

            case "roseau":
                google = new LatLng(15.3060796, -61.3948394);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(google, zoomLevel));
                break;

            case "castries":
                google = new LatLng(14.0104826, -60.9951591);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(google, zoomLevel));
                break;

            case "belmopan":
                google = new LatLng(17.2548368, -88.8001093);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(google, zoomLevel));
                break;

            case "washington":
                google = new LatLng(38.8935128, -77.1546631);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(google, zoomLevel));
                break;

            case "ottawa":
                google = new LatLng(45.248786, -76.3607206);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(google, zoomLevel));
                break;
        }
        if(currentPosition == 30){
            Toast.makeText(this,"Fim de jogo" , Toast.LENGTH_SHORT).show();
            startButton.setEnabled(true);
            bemvindo.setText("");}
    }
    public void respostaErrada(){
        Toast.makeText(this, "Resposta errada", Toast.LENGTH_SHORT).show();
        currentPosition++;
        setData();
        editResposta.setText("");
    }










    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);



        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                mMap.clear();
                marker.remove();
                contador= contador+2;
                pontos.setText(String.valueOf(contador));
                return true;
            }
        });





    }


    public void addMarker( LatLng latLng,String title, String snniping){
        MarkerOptions options = new MarkerOptions();
        options.position(latLng).title(title).snippet(snniping).draggable(true);
        mMap.addMarker(options);
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_card_giftcard));

    }

}