package com.example.todolist

import android.bluetooth.*
import android.content.*
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.CountDownTimer
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import android.support.v4.app.FragmentActivity
import android.provider.Browser.sendString
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.Toast
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    var socket : BluetoothSocket ?= null
    var mReceiver : BroadcastReceiver? = null
    var bluetoothAdapter : BluetoothAdapter ?= null
    private val MY_UUID_SECURE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    var bluetooth : Bluetooth = Bluetooth()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar.setTitle("")
        setSupportActionBar(toolbar)

        var intentfilter: IntentFilter = IntentFilter()
        intentfilter.addAction("com.example.todolist.SEND_BROAD_CAST")
        Log.d("test001", "tttttt")
        var mReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val sendString = intent?.getStringExtra("sendString")
                Log.d("test001", sendString)
                val toast = Toast.makeText(getApplicationContext(), "qnldknqwwdqnlk", Toast.LENGTH_LONG).show()
            }
        }
        Log.d("test001", "블루투스 상태 : "+ BluetoothAdapter.getDefaultAdapter().state);

        registerReceiver(mReceiver, intentfilter)


        val sendIntent = Intent("com.example.todolist.SEND_BROAD_CAST")
        sendIntent.putExtra ("isBoolean", true)
        sendIntent.putExtra("sendInteger", 123)
        sendIntent.putExtra("sendString", "Intent String")
        sendBroadcast(sendIntent)


        val count = object : CountDownTimer(1000000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timer.setText(""+millisUntilFinished/60000+":"+millisUntilFinished%60000/1000)
            }

            override fun onFinish() {
                timer.setText("done!");
            }
        }.start()

        val intent = Intent(this,CalendarActivity::class.java)
        //startActivity(intent)




        //블루투스 연결
        bluetoothConnection()


        Blue.setOnClickListener(View.OnClickListener {
            connect()
        })
        server.setOnClickListener(View.OnClickListener {
            Log.d("test001", "call - createServer");
            createServer()
        })
    }


    fun createServer(){
        Thread({
            var bluetoothServerSocket : BluetoothServerSocket

            var secure : Boolean = true
            var mSocketType = if (secure) "Secure" else "Insecure"
            if(secure)
                bluetoothServerSocket = bluetoothAdapter!!.listenUsingInsecureRfcommWithServiceRecord("name", MY_UUID_SECURE)
            else
                bluetoothServerSocket = bluetoothAdapter!!.listenUsingRfcommWithServiceRecord("name", MY_UUID_SECURE)

            Log.d("test001", "accpt ready")
            socket = bluetoothServerSocket.accept()
            Log.d("test001", "Good")

            try {
                Log.d("test001", "try Connect")
                socket?.connect()
            }catch (e:Exception){
                Log.d("test001", e.printStackTrace().toString())
            }
            Log.d("test001", "connectSuccess")


            socket?.close()
            Log.d("test001", "successed")
        }).start()
    }

    override fun unregisterReceiver(receiver: BroadcastReceiver?) {
        super.unregisterReceiver(mReceiver)

    }

    fun connect() {
        socket?.connect()
    }

    fun bluetoothConnection() {
        //블루투스 연결
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if(bluetoothAdapter == null) {
            Log.d("test001", "Device does not support BlueTooth")
        }
        if(!bluetoothAdapter!!.isEnabled){
            val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(intent, RESULT_OK) // what is requestCode?
        }

        //기기 연결
        var pairedDevices : Set<BluetoothDevice> = bluetoothAdapter!!.bondedDevices
        var arrayAdapter = ArrayList<String>()
        if(pairedDevices.size > 0) {
            for(device in pairedDevices) {
                arrayAdapter.add(device.name+ "\n"+device.address)
            }
        }

        // Create a BroadcastReceiver for ACTION_FOUND
        val mReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action
                // When discovery finds a device
                if (BluetoothDevice.ACTION_FOUND == action) {
                    // Get the BluetoothDevice object from the Intent
                    val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    // Add the name and address to an array adapter to show in a ListView
                    arrayAdapter.add(device.name + "\n" + device.address)
                }
            }
        }
        // Register the BroadcastReceiver
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(mReceiver, filter) // Don't forget to unregister during onDestroy
    }

    fun bluetoothPareing() {
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
