package hr.math.kolokvij;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {

    int notificationID = 1;

    private InputStream OpenHttpConnection(String urlString)
            throws IOException
    {
        InputStream in = null;
        int response = -1;

        URL url = new URL(urlString);
        URLConnection conn = url.openConnection();

        if (!(conn instanceof HttpURLConnection))
            throw new IOException("Not an HTTP connection");
        try{
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");
            httpConn.connect();
            response = httpConn.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK) {
                in = httpConn.getInputStream();
            }
        }
        catch (Exception ex)
        {
            Log.d("Networking", ex.getLocalizedMessage());
            throw new IOException("Error connecting");
        }
        return in;
    }

    private Bitmap DownloadImage(String URL)
    {
        Bitmap bitmap = null;
        InputStream in = null;
        try {
            in = OpenHttpConnection(URL);
            bitmap = BitmapFactory.decodeStream(in);
            in.close();
        } catch (IOException e1) {
            Log.d("NetworkingActivity", e1.getLocalizedMessage());
        }
        return bitmap;
    }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        protected Bitmap doInBackground(String... urls) {
            return DownloadImage(urls[0]);
        }

        protected void onPostExecute(Bitmap result) {
            ImageView img = (ImageView) findViewById(R.id.img);
            img.setImageBitmap(result);
            displayNotification(result);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DBAdapter db = new DBAdapter(this);


        //---add a contact---
        db.open();
        long id = db.insertSlike("Guernica", "Pablo Picasso");
        id = db.insertSlike("Starry Night over the Rhone", "Vincent Van Gogh");
        id = db.insertSlike("Zena sa suncobranom", "Claude Monet");
        id = db.insertSlike("Olympia", "Edouard Manet");
        id = db.insertSlike("Stari gitarist", "Pablo Picasso");

        id = db.insertPeriod("Renesansa", "Leonardo da Vinci");
        id = db.insertPeriod("Impresionizam", "Cammille Pissarro");
        id = db.insertPeriod("Relizam", "Gustave Courbet");
        id = db.insertPeriod("Romantizam", "Theodore Gericault");
        id = db.insertPeriod("Eksprezionizam", "Edvard Munch");
        db.close();
    }

    public void ispisBaze(View v)
    {
        DBAdapter db = new DBAdapter(this);

        String sadrzajBaze =" ";

        db.open();
        String slike = "SLIKE:\n";
        Cursor c = db.getAllSlike();
        if (c.moveToFirst())
        {
            do {
                slike = slike + c.getString(0) + " " + c.getString(1) + " " + c.getString(2) + "\n";
            } while (c.moveToNext());
        }
        db.close();

        db.open();
        String period = "PERIOD:\n";
        Cursor c1 = db.getAllPeriod();
        if (c1.moveToFirst())
        {
            do {
                period = period + c1.getString(0) + " " + c1.getString(1) + " " + c1.getString(2) + "\n";
            } while (c1.moveToNext());
        }
        db.close();

        sadrzajBaze = slike+period;

        TextView tv = (TextView)findViewById(R.id.sadrzajB);
        tv.setText(sadrzajBaze);
    }

    public void ispisPicasso(View v)
    {
        DBAdapter db = new DBAdapter(this);

        db.open();
        String picasso = " ";
        Cursor c = db.getSlika("Pablo Picasso");
        if (c.moveToFirst())
        {
            do {
                picasso= picasso + c.getString(0) + " " + c.getString(1) + " " + c.getString(2) + "\n";
            } while (c.moveToNext());
        }
        db.close();

        TextView tv = (TextView)findViewById(R.id.sadrzajPicasso);
        tv.setText(picasso);
    }

    public void izbrisiSliku(View v)
    {
        EditText et = (EditText) findViewById(R.id.izbrisiS);
        int broj = Integer.parseInt(et.getText().toString());

        DBAdapter db = new DBAdapter(this);
        db.open();
        db.deleteSLike(broj);
        db.close();

    }

    public void izbrisiPeriod(View v)
    {
        EditText et = (EditText) findViewById(R.id.izbrisiP);
        int broj = Integer.parseInt(et.getText().toString());

        DBAdapter db = new DBAdapter(this);
        db.open();
        db.deletePeriod(broj);
        db.close();

    }
    public void downloadSlike(View v)
    {
        EditText et = (EditText) findViewById(R.id.urlSlike);
        new DownloadImageTask().execute(et.getText().toString());
    }

    protected void displayNotification(Bitmap result)
    {
        //---PendingIntent to launch activity if the user selects
        // this notification---
        Intent i = new Intent(this, NotificationView.class);

        i.putExtra("notificationID", notificationID);


        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, i, 0);

        long[] vibrate = new long[] { 100, 250, 100, 500};

//Notification Channel - novo od Android O

        String NOTIFICATION_CHANNEL_ID = "my_channel_01";
        CharSequence channelName = "hr.math.karga.MYNOTIF";
        int importance = NotificationManager.IMPORTANCE_LOW;
        NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, importance);
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.RED);
        notificationChannel.enableVibration(true);
        notificationChannel.setVibrationPattern(vibrate);

//za sve verzije
        NotificationManager nm = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);

// za Notification Chanel

        nm.createNotificationChannel(notificationChannel);




//ovako je i u starim verzijama, jedino dodano .setChannelId (za stare verzije to brisemo)
        Intent intent = new Intent(this, MainActivity.class);

// Create pending intent and wrap our intent
        PendingIntent pendingIntent1 = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        Notification notif = new Notification.Builder(this)
                .setContentTitle("Download je zavrsio")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setWhen(System.currentTimeMillis())
                .setShowWhen(true)
                .setContentIntent(pendingIntent1)
                .setVibrate(vibrate)
                .setChannelId(NOTIFICATION_CHANNEL_ID)
                .setStyle(new Notification.BigPictureStyle().bigPicture(result))
                .build();
        //najnovije, od API level 26.1.0., .setWhen ide po defautlu ovdje na currentTimeMillis

/*        final NotificationCompat.Builder notif = new NotificationCompat.Builder(this,NOTIFICATION_CHANNEL_ID)

                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setVibrate(vibrate)
                .setSound(null)
                .setChannelId(NOTIFICATION_CHANNEL_ID)
                .setContentTitle("Meeting with customer at 3pm...")
                .setContentText("this is the second row")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setTicker("Reminder: meeting starts in 5 minutes")
                .setContentIntent(pendingIntent)
                .setAutoCancel(false); */

// za sve verzije

        nm.notify(notificationID, notif);
    }
}
