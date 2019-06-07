package com.yusuffurkanyucesoy.girisekrani;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import java.net.URL;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class Mazak3Activity extends AppCompatActivity {
    LineGraphSeries<DataPoint> seriesz;
    LineGraphSeries<DataPoint> seriesy;
    LineGraphSeries<DataPoint> seriesx;
    GraphView graphMt1;
    //http degiskenleri
    TextView txtCreation,txtSender,txtInstanceId,txtVersion; //Header texts
    TextView txtUuid,txtName; //Device texts
    TextView txtXload,txtYload,txtZload,txtXabs,txtYabs,txtZabs,txtTemp; //Linear Details
    NodeList nodeStream;
    NodeList nodeDeviceStream;
    NodeList nestedNode;
    NodeList nodeHeader;
    ProgressDialog pDialog;
    double xdata=0;
    double ydata=0;
    double zdata=0;
    private static final String TAG = "tokenim";
    String tmpZAbs="0";
    String tmpYAbs="0";

    String URL = "http://mtconnect.mazakcorp.com:5610/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mazak3);
        graphMt1 = findViewById(R.id.graphMt1);
        txtSender = findViewById(R.id.txtSender);
        txtZabs = findViewById(R.id.txtZabs);
        txtYabs = findViewById(R.id.txtYabs);
        txtCreation = findViewById(R.id.txtCreationTime);
        txtUuid = findViewById(R.id.txtUuId);
        txtTemp = findViewById(R.id.txtStemp);
        txtInstanceId = findViewById(R.id.txtInstanceID);
        txtVersion = findViewById(R.id.txtVerison);
        txtName = findViewById(R.id.txtName);
        txtXload = findViewById(R.id.txtXload);
        txtYload = findViewById(R.id.txtYload);
        txtXabs = findViewById(R.id.txtXabs);
        txtZload = findViewById(R.id.txtZload);
        final Random rndx = new Random();
        final Random rndy = new Random();
        final Random rndz = new Random();


        //grafik basma

        graphMt1.setBackgroundColor(Color.rgb(128,53,174));
        seriesz = new LineGraphSeries<DataPoint>();
        seriesy = new LineGraphSeries<DataPoint>();
        seriesx=new LineGraphSeries<DataPoint>();

        seriesy.setColor(Color.rgb(0,0,0));
        seriesz.setColor(Color.rgb(255,255,255));
        seriesx.setColor(Color.rgb(125,125,125));



        //handler
        final Handler handler = new Handler();
        Timer timer;
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        new DownloadXML().execute(URL);
                        zdata += 1;
                        ydata += 1;
                        xdata += 1;
                        double zData =rndz.nextInt(20)+450.09;
                        double yData =rndy.nextInt(20)+280.03;
                        double xData =rndx.nextInt(20)+350.05;
                        txtXabs.setText(String.valueOf(xData));
                        txtYabs.setText(String.valueOf(yData));
                        txtZabs.setText(String.valueOf(zData));
                        seriesz.appendData(new DataPoint(zdata, zData), true, 300);
                        seriesy.appendData(new DataPoint(ydata, yData), true, 300);
                        seriesx.appendData(new DataPoint(xdata, xData), true, 300);

                        graphMt1.addSeries(seriesz);
                        graphMt1.addSeries(seriesy);
                        graphMt1.addSeries(seriesx);
                    }
                });
            }
        };
        timer = new Timer();
        timer.schedule(timerTask, 1000, 5000);
    }
    private class DownloadXML extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressbar
            pDialog = new ProgressDialog(Mazak3Activity.this);
            // Set progressbar title
            pDialog.setTitle("Fetching Data...");
            // Set progressbar message
            pDialog.setMessage("Loading...");
            pDialog.setIndeterminate(false);
            // Show progressbar
            pDialog.show();
        }


        @Override
        protected Void doInBackground(String... Url) {
            try {
                URL url = new URL(Url[0]);
                DocumentBuilderFactory dbf = DocumentBuilderFactory
                        .newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                // Download the XML file
                Document doc = db.parse(new InputSource(url.openStream()));
                doc.getDocumentElement().normalize();
                // Locate the Tag Name

                nodeDeviceStream = (NodeList) doc.getElementsByTagName("Device");
                nodeHeader = (NodeList) doc.getElementsByTagName("Header");



            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;

        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(Void args) {

            //Header details
            for (int temp = 0; temp < nodeHeader.getLength(); temp++)
            {
                Element hElement = (Element) nodeHeader.item(temp);

                txtCreation.setText(hElement.getAttribute("creationTime"));
                txtSender.setText(hElement.getAttribute("sender"));
                txtInstanceId.setText(hElement.getAttribute("instanceId"));
                txtVersion.setText(hElement.getAttribute("version"));

            }

            //Randomly
            Random rndxload= new Random();
            Random rndyload= new Random();
            Random rndzload= new Random();
            Random rndstemp=new Random();
            txtXload.setText(String.valueOf(rndxload.nextInt(2)+1));
            txtYload.setText(String.valueOf(rndyload.nextInt(2)+1));
            txtZload.setText(String.valueOf(rndzload.nextInt(5)+37));
            txtTemp.setText(String.valueOf(rndstemp.nextInt(5)+25));


            //Device details
            for (int temp = 0; temp < nodeDeviceStream.getLength(); temp++)
            {
                Element hElement = (Element) nodeDeviceStream.item(temp);
                txtUuid.setText(hElement.getAttribute("uuid"));
                txtName.setText(hElement.getAttribute("name"));
            }




            // Close progressbar
            pDialog.dismiss();

        }
    }
}
