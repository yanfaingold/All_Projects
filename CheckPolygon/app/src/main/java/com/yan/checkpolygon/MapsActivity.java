package com.yan.checkpolygon;

import android.app.ProgressDialog;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mGoogleMap;
    private Polygon drawenPolygon;
    private Polyline distanceLine;
    private boolean loading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        try {
            importfile("test_polygon.kml");
        } catch (IOException e) {
            e.printStackTrace();
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mGoogleMap = googleMap;

        //kml layer works but cant click inside thats why better draw polygon myself

//        KmlLayer layer = null;
//        try {
//            layer = new KmlLayer(mGoogleMap, R.raw.allowed_area, getApplicationContext());
//        } catch (XmlPullParserException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try {
//            layer.addLayerToMap();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (XmlPullParserException e) {
//            e.printStackTrace();
//        }

        try {
            drawPoligonFromKmlFile(getStringFromFile(Environment.getExternalStorageDirectory() + "/test_polygon.kml"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        mGoogleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {


                mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    Marker marker;

                    public void onMapClick(final LatLng clickedPosition) {
                        if (!loading) {//ignore clicks while loading
                            loading = true;
                            //create and lunch load dialog
                            final ProgressDialog loadingProgressDialog;
                            loadingProgressDialog = ProgressDialog.show(MapsActivity.this, "Loading", "Please Wait", true);
                            if (marker != null) {
                                marker.remove();
                            }
                            if (distanceLine != null) {
                                distanceLine.remove();
                            }
                            final int tempFill = drawenPolygon.getFillColor();
                            final int polyInlineColor = Color.GRAY;
                            drawenPolygon.setFillColor(polyInlineColor);
                            GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {
                                @Override
                                public void onSnapshotReady(Bitmap snapshot) {
                                    Point clickedPositionPixels = mGoogleMap.getProjection().toScreenLocation(clickedPosition);
                                    marker = mGoogleMap.addMarker(new MarkerOptions().position(clickedPosition));
                                    //check inside or not
                                    if (snapshot.getPixel(clickedPositionPixels.x, clickedPositionPixels.y) == polyInlineColor) {
                                        //inside case when taking a snapshot and checking if color i set temp for polygon area is equal to colr of clicked point
                                        marker.setTitle("Inside");
                                    } else {
                                        //check which of the outline points have the closest distance
                                        float minDistancePoint = Float.MAX_VALUE;
                                        LatLng closestLatLngPoint = clickedPosition;

                                        for (int i = 0; i < drawenPolygon.getPoints().size() - 1; i++) {//for each point in polygon
                                            List<LatLng> linePoints = getAllPointsWithDistance(20, drawenPolygon.getPoints().get(i), drawenPolygon.getPoints().get(i + 1));//maximum distance
                                            for (LatLng linePoint : linePoints) {//check each point distance
                                                float distanceInMeters = getDistanceBetween(clickedPosition, linePoint);
                                                if (minDistancePoint > distanceInMeters) {
                                                    minDistancePoint = distanceInMeters;
                                                    closestLatLngPoint = linePoint;
                                                }
                                            }
                                        }


                                        //draw the line from clicked point to closest
                                        // Instantiates a new Polyline object and adds points to define a rectangle
                                        PolylineOptions rectOptions = new PolylineOptions().add(clickedPosition).add(closestLatLngPoint).color(Color.BLUE);
                                        // Get back the mutable Polyline
                                        distanceLine = mGoogleMap.addPolyline(rectOptions);
                                        marker.setTitle("Outside, closest is: " + (getDistanceBetween(clickedPosition, closestLatLngPoint) / 1000) + " Km");
                                    }
                                    //show marker info
                                    marker.showInfoWindow();
                                    //remove loading
                                    loadingProgressDialog.dismiss();
                                    loading = false;
                                    //return prev filled color
                                    drawenPolygon.setFillColor(tempFill);
                                }
                            };
                            mGoogleMap.snapshot(callback);
                        }
                    }
                });
                //init camera tel aviv
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(32.109333, 34.855499), 12.0f));
            }
        });
    }

    //add more points to each line to get more accurate closest length to click outside polygon
    private List<LatLng> getAllPointsWithDistance(float distance, LatLng point1, LatLng point2) {
        List<LatLng> filledPoints = new ArrayList<LatLng>();
        filledPoints.add(point1);
        filledPoints.add(point2);
        while (getDistanceBetween(filledPoints.get(0), filledPoints.get(1)) > distance) {
            for (int i = 0; i < filledPoints.size() - 1; i = i + 2) {//add center points
                filledPoints.add(i + 1, new LatLng((filledPoints.get(i).latitude + filledPoints.get(i + 1).latitude) / 2, (filledPoints.get(i).longitude + filledPoints.get(i + 1).longitude) / 2));
            }
        }
        return filledPoints;
    }
    //get distance between 2 points
    private float getDistanceBetween(LatLng point1, LatLng point2) {
        float[] results = new float[1];
        Location.distanceBetween(point1.latitude, point1.longitude, point2.latitude, point2.longitude, results);
        return results[0];
    }
    //since kml layer disabling ability to click on the layer added i instead read file coordinates and draw it myself with google maps api
    private void drawPoligonFromKmlFile(String kmlFile) {
        //get only coordinates
        String pattern = "<coordinates>(.|\\r\\n|\\r|\\n)*<\\/coordinates>";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(kmlFile);
        String inverseKmlCordString;
        if (m.find()) {
            inverseKmlCordString = m.group(0).replaceAll("<coordinates>|\\r\\n|\\r|\\n|\\t|<\\/coordinates>|0 ", "");
            String[] inverseKmlCordParts = inverseKmlCordString.split(",");
            PolygonOptions rectOptions = new PolygonOptions();
            for (int i = inverseKmlCordParts.length - 1; i >= 0; i = i - 2) {
                rectOptions.add(new LatLng(Double.parseDouble(inverseKmlCordParts[i]), Double.parseDouble(inverseKmlCordParts[i - 1])));
            }
            rectOptions.strokeColor(Color.rgb(1, 1, 1)).fillColor(Color.argb(90, 0, 0, 0));
            drawenPolygon = mGoogleMap.addPolygon(rectOptions);
        } else {
            System.out.println("NO MATCH");
        }
    }

    private String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    private String getStringFromFile(String filePath) throws Exception {
        File fl = new File(filePath);
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        //Make sure you close all streams.
        fin.close();
        return ret;
    }
    private void importfile(String name) throws IOException {
        //create empty file with right name to write to
        File new_file = new File(Environment.getExternalStorageDirectory() + File.separator + name);
        try {
            new_file.createNewFile();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        //copy data from file in proj
        AssetManager am = getAssets();
        InputStream inputstream = am.open(name);

        try {
            if (new_file.length() == 0)
                copyfiles(inputstream, new_file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void copyfiles(InputStream in, File dst) throws IOException {
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }
}
