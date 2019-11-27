package com.example.swimmingpoolmonitor;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.swimmingpoolmonitor.ui.main.SectionsPagerAdapter;
import com.google.android.material.tabs.TabLayout;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;


public class MainActivity extends AppCompatActivity {

    public boolean sendNumber = false;
    public static Semaphore lock = new Semaphore(1);
    public static CyclicBarrier barrier = new CyclicBarrier(2);
    public String phoneNumber = "+1";
    public String id = "1234";

    public String CHANNEL_ID = "notifChan";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            lock.acquire();
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
        FileInputStream fis;
        try {
            File file = new File(getFilesDir(), "phoneNumber.txt");
            fis = new FileInputStream(file);
        } catch(FileNotFoundException e) {
            sendNumber = true;
            System.out.println("file doesn't exist.");
        }
        System.out.println(sendNumber);

        System.out.println(lock.toString());
        if (sendNumber == true) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Welcome!");
            builder.setMessage("Please enter your Phone number in order to receive alerts:");
            // Set up the input
            final EditText input = new EditText(this);
            // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            // Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    phoneNumber = phoneNumber + input.getText().toString();
                    try {
                        barrier.await();
                    } catch (BrokenBarrierException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });

            builder.show();
        }

        lock.release();
        System.out.println(phoneNumber);
        System.out.println(lock.toString());
        createNotificationChannel();
        setContentView(R.layout.activity_main);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    barrier.await();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (sendNumber == true) {
                    try {
                        lock.acquire();
                        System.out.println("Sending Number");
                        System.out.println(phoneNumber);
                        String content = id + ", " + phoneNumber;
                        final ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                        final DataOutputStream dataOut = new DataOutputStream(byteOut);
                        final byte[] buf = content.getBytes();


                        DatagramSocket socket = new DatagramSocket(4000);

                        InetAddress address = InetAddress.getByName("3.88.34.147");

                        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4000);
                        socket.send(packet);
                        System.out.println("sent packet");
                        socket.close();


                        PrintWriter writer = new PrintWriter(new File(getFilesDir(),"phoneNumber.txt"));

                        writer.println(content);
                        writer.close();

                        File file = new File(getFilesDir(), "phoneNumber.txt");
                        FileInputStream fis = new FileInputStream(file);
                        System.out.println(file.toString());

                    } catch (SocketException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    lock.release();
                }
            }
        }).start();
/*      NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("ALERT")
                .setContentText("Potential Drowning Detected.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(0, builder.build());
*/
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

     public void showPopUp() {
        System.out.println(phoneNumber);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Welcome!");
        builder.setMessage("Please enter your Phone number in order to receive alerts:");
        // Set up the input
         final EditText input = new EditText(this);
         // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
         input.setInputType(InputType.TYPE_CLASS_TEXT);
         builder.setView(input);

         // Set up the buttons
         builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int which) {
                 phoneNumber = "+1" + input.getText().toString();
             }
         });
         builder.show();

         System.out.println(phoneNumber);
         lock.release();
    }
}