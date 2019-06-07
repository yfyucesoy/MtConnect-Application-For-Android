package com.yusuffurkanyucesoy.girisekrani;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import java.net.HttpURLConnection;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;



import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;








public class Mazak1Activity extends AppCompatActivity {



    Button btnXml;

    LineGraphSeries<DataPoint> seriesz;
    LineGraphSeries<DataPoint> seriesy;
    LineGraphSeries<DataPoint> seriesx;
    TextView txtCreation, txtSender, txtInstanceId, txtVersion; //Header texts
    TextView txtUuid, txtName; //Device texts
    TextView txtXload, txtYload, txtZload, txtXabs, txtYabs, txtZabs, txtTemp; //Linear Details
    GraphView graphMt1;
    //http degiskenleri

    NodeList nodeDeviceStream;
    NodeList nodeStream;
    NodeList nestedNode;
    NodeList nodeHeader;
    ProgressDialog pDialog;
    Switch aSwitch;
    double xdata = 0;
    double ydata = 0;
    double zdata = 0;
    private static final String TAG = "tokenim";
    String tmpZAbs = "0";
    String tmpYAbs = "0";
    String tmpXAbs="0";
    String tmpSicaklik="0";


    String URL = "http://mtconnect.mazakcorp.com:5609/current";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mazak1);

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
        aSwitch=findViewById(R.id.switch1);

        txtXabs.setBackgroundColor(Color.BLUE);
        txtYabs.setBackgroundColor(Color.RED);
        txtZabs.setBackgroundColor(Color.YELLOW);
        btnXml=findViewById(R.id.btnXml);
        btnXml.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent xmlekran= new Intent(Mazak1Activity.this,XML_Activity.class);
                startActivity(xmlekran);
            }
        });




        //grafik basma

        graphMt1.setBackgroundColor(Color.rgb(128,53,174));
        seriesz = new LineGraphSeries<DataPoint>();
        seriesy = new LineGraphSeries<DataPoint>();
        seriesx=new LineGraphSeries<DataPoint>();



        seriesy.setColor(Color.RED);
        seriesz.setColor(Color.YELLOW);
        seriesx.setColor(Color.BLUE);

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



                        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if (isChecked) {

                                    double douTemp=Double.parseDouble(tmpSicaklik);

                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M && douTemp>22)
                                    {

                                        int smsPermission= checkSelfPermission(Manifest.permission.SEND_SMS);
                                        if (smsPermission!= PackageManager.PERMISSION_GRANTED)
                                        {
// sms permission is not granted then invoke the user to allow this permission
                                            requestPermissions(new String[]{Manifest.permission.SEND_SMS},0);
                                        }
                                        else if (smsPermission== PackageManager.PERMISSION_GRANTED)
                                        {
// if this permission is granted then call sms sending method
                                            smsSendingFun();
                                        }
                                    }
                                    else
                                    {

                                        smsSendingFun();
                                    }
                                }
                                else
                                {
                                    // The toggle is disabled
                                }
                            }
                        });




                        double zData = Double.parseDouble(tmpZAbs);
                        double yData = Double.parseDouble(tmpYAbs);
                        double xData = -1*(Double.parseDouble(tmpXAbs));
                        seriesz.appendData(new DataPoint(zdata, zData), true, 300);
                        seriesy.appendData(new DataPoint(ydata, yData), true, 300);
                        seriesx.appendData(new DataPoint(xdata, xData), true, 300);
                        zdata += 1;
                        ydata += 1;
                        xdata += 1;

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

    // DownloadXML AsyncTask
    private class DownloadXML extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressbar
            pDialog = new ProgressDialog(Mazak1Activity.this);
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
                nodeStream = (NodeList) doc.getElementsByTagName("ComponentStream");
                nodeDeviceStream = (NodeList) doc.getElementsByTagName("DeviceStream");
                nodeHeader = (NodeList) doc.getElementsByTagName("Header");
                nestedNode = (NodeList) doc.getElementsByTagName("Description");

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
            for (int temp = 0; temp < nodeHeader.getLength(); temp++) {
                Element hElement = (Element) nodeHeader.item(temp);

                txtCreation.setText( hElement.getAttribute("creationTime"));
                txtSender.setText( hElement.getAttribute("sender"));
                txtInstanceId.setText( hElement.getAttribute("instanceId"));
                txtVersion.setText( hElement.getAttribute("version"));

            }

            //Device details
            for (int temp = 0; temp < nodeDeviceStream.getLength(); temp++) {
                Element hElement = (Element) nodeDeviceStream.item(temp);

                txtUuid.setText( hElement.getAttribute("uuid"));
                txtName.setText( hElement.getAttribute("name"));
            }

            for (int temp = 0; temp < nodeStream.getLength(); temp++) {
                Node nNode = nodeStream.item(temp);
                Node sNode = nestedNode.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element dataElement = (Element) nodeStream.item(temp);

                    //Get the name you want//
                    String[] nameArray = new String[100];
                    nameArray[temp] = dataElement.getAttribute("name");

                    if (nameArray[temp].equalsIgnoreCase("Z")) {
                        Element sampleElement = (Element) nodeStream.item(temp);
                        NodeList dataList = sampleElement.getElementsByTagName("Samples");

                        for (int j = 0; j < dataList.getLength(); j++) {
                            Element temperatureElement = (Element) dataList.item(j);

                            txtZabs.setText( temperatureElement.getElementsByTagName("Position").item(0).getTextContent());
                            tmpZAbs = temperatureElement.getElementsByTagName("Position").item(0).getTextContent();
                            txtZload.setText( temperatureElement.getElementsByTagName("Load").item(0).getTextContent());


                        }
                    }
                    //Temperature
                    if (nameArray[temp].equalsIgnoreCase("C")) {
                        Element sampleElement = (Element) nodeStream.item(temp);
                        NodeList dataList = sampleElement.getElementsByTagName("Samples");

                        for (int j = 0; j < dataList.getLength(); j++) {
                            Element temperatureElement = (Element) dataList.item(j);
                            //Print between tags such as temperature

                            txtTemp.setText(temperatureElement.getElementsByTagName("Temperature").item(0).getTextContent());
                            tmpSicaklik=temperatureElement.getElementsByTagName("Temperature").item(0).getTextContent();
                        }
                    }
                    if (nameArray[temp].equalsIgnoreCase("X")) {
                        Element sampleElement = (Element) nodeStream.item(temp);
                        NodeList dataList = sampleElement.getElementsByTagName("Samples");

                        for (int j = 0; j < dataList.getLength(); j++) {
                            Element temperatureElement = (Element) dataList.item(j);

                            NodeList temperatureList = temperatureElement.getElementsByTagName("Position");
                            txtXabs.setText( temperatureElement.getElementsByTagName("Position").item(0).getTextContent());
                            tmpXAbs=temperatureElement.getElementsByTagName("Position").item(0).getTextContent();
                            txtXload.setText( temperatureElement.getElementsByTagName("Load").item(0).getTextContent());


                        }
                    } else if (nameArray[temp].equalsIgnoreCase("Y"))

                    {
                        Element sampleElement = (Element) nodeStream.item(temp);
                        NodeList dataList = sampleElement.getElementsByTagName("Samples");

                        for (int j = 0; j < dataList.getLength(); j++) {
                            Element temperatureElement = (Element) dataList.item(j);


                            NodeList temperatureList = temperatureElement.getElementsByTagName("Position");
                            txtYabs.setText( temperatureElement.getElementsByTagName("Position").item(0).getTextContent());
                            tmpYAbs = temperatureElement.getElementsByTagName("Position").item(0).getTextContent();
                            txtYload.setText(temperatureElement.getElementsByTagName("Load").item(0).getTextContent());


                        }
                    }


                }
                // Close progressbar
                pDialog.dismiss();

            }
        }
    }

    public void smsSendingFun()
    {
        SmsManager smsManager = SmsManager.getDefault();

        smsManager.sendTextMessage("05425498290", null, "Cihaz Olağan Sıcaklık Değerini Aştı !!!", null, null);

    }

}















