package com.example.todolist

import android.app.ListActivity
import android.bluetooth.*
import android.content.*
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
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
import java.nio.charset.Charset
import android.bluetooth.BluetoothSocket







private const val SCAN_PERIOD : Long = 10000

class MainActivity : AppCompatActivity() {
    var socket : BluetoothSocket ?= null
    var mReceiver : BroadcastReceiver? = null
    var bluetoothAdapter : BluetoothAdapter ?= null
    private val MY_UUID_SECURE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    var mBtSocket : BluetoothSocket? = null


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

        //val intent = Intent(this,CalendarActivity::class.java)
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
        //socket?.connect()
    }

    fun findBLEDevice() {
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
        Log.d("test001", "기기 연결 해볼까?");
        var pairedDevices : Set<BluetoothDevice> = bluetoothAdapter!!.bondedDevices
        var arrayAdapter = ArrayList<String>()
        if(pairedDevices.size > 0) {
            Log.d("test001", "if문에 걸렸어");
            for(device in pairedDevices) {
                arrayAdapter.add(device.name+ "\n"+device.address)
            }
        }
        for(adapter in arrayAdapter)
            Log.d("test001", adapter);

        try{
            bluetoothAdapter!!.startDiscovery()
            var heroDevice = bluetoothAdapter!!.getRemoteDevice("B8:27:EB:5F:37:48")
            mBtSocket = heroDevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"))
            mBtSocket =
                heroDevice.javaClass.getMethod("createRfcommSocket", *arrayOf<Class<*>>(Int::class.javaPrimitiveType!!))
                    .invoke(heroDevice, 1) as BluetoothSocket
        } catch(e : Exception) {
            e.printStackTrace();
            Log.d("test001", "아 여기도 에러가 떳어요 : "+e.printStackTrace())
        }
        Thread(Runnable {
            try {

                // 소켓을 연결한다.
                Log.d("test001", "연결 중");
                mBtSocket?.connect()
                Log.d("test001", "연결 완료");
                //Thread.sleep(2000);
                // 입출력을 위한 스트림 오브젝트를 얻는다

                var mInput = mBtSocket?.getInputStream()

                var mOutput = mBtSocket?.getOutputStream()

                while (true) {
                    //mOutput?.writer(charset("CanyouSpeakKOR?"))
                    // 입력 데이터를 그대로 출력한다
                    //mOutput?.write(mInput!!.read())
                    mOutput?.write(("time\n").toByteArray())
                    Log.d("test001", "데이터 보냄");
                    Thread.sleep(3000)
                    mOutput?.write(("print\n").toByteArray())
                    Log.d("test001", "데이터 보냄");
                    Thread.sleep(3000)
                    mOutput?.write(("q").toByteArray())
                    Log.d("test001", "데이터 보냄");
                    Thread.sleep(3000)
                    break;
                }

            } catch (e: Exception) {
                Log.d("test001", "에러 또떠 ")
                e.printStackTrace()

            }
        }).start()










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
