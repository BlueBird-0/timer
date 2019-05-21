package com.example.todolist

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
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
import android.content.IntentFilter
import android.content.BroadcastReceiver



class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar.setTitle("")
        setSupportActionBar(toolbar)

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
        bluetoothPareing()
    }

    fun bluetoothConnection() {
        //블루투스 연결
        var bluetoothAdapter : BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if(bluetoothAdapter == null) {
            Log.d("test001", "Device does not support BlueTooth")
        }
        if(!bluetoothAdapter.isEnabled()){
            val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(intent, RESULT_OK) // what is requestCode?
        }

        //기기 연결
        var pairedDevices : Set<BluetoothDevice> = bluetoothAdapter.bondedDevices
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
