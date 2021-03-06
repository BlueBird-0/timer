package com.example.todolist

import android.Manifest
import android.app.Activity
import android.app.ListActivity
import android.app.PendingIntent.getActivity
import android.bluetooth.*
import android.content.*
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
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
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothDevice
import android.graphics.Color
import android.os.Build
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityCompat.startActivityForResult
import android.support.v4.content.ContextCompat
import android.widget.Button
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.time.Year
import kotlin.concurrent.schedule


private const val SCAN_PERIOD : Long = 10000
class MainActivity : AppCompatActivity() {

    var socket : BluetoothSocket ?= null
    val bluetoothAdapter : BluetoothAdapter ?= BluetoothAdapter.getDefaultAdapter()
    private val MY_UUID_SECURE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    var mBtSocket : BluetoothSocket? = null
    var mOutput : OutputStream? = null
    var mInput : InputStream?= null


    var today_study_time = 0   //하룻동안 공부 시간
    var studing_state = false   //false:공부안함  true: 공부함
    var distanceTimer : TimerTask ?= null

    var bluetoothState = 0   //0 = 연결 안됨, 1 = 연결 중, 2 = 연결

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar.setTitle("")
        toolbar.hideOverflowMenu()
        setSupportActionBar(toolbar)

        val permissionListener :PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                //Toast.makeText(applicationContext, "권한 허가", Toast.LENGTH_SHORT).show()
            }

            override fun onPermissionDenied(deniedPermissions: java.util.ArrayList<String>?) {
                Toast.makeText(applicationContext, "권한이 필요합니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
        TedPermission.with(this)
            .setPermissionListener(permissionListener)
            .setDeniedMessage("거리 측정을 하기 위해서 권한이 필요합니다.")
            .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
            .check()

        val mReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                distanceCheck()
                //val sendString = intent?.getStringExtra("sendString")
                //Log.d("test001", sendString)
                //val toast = Toast.makeText(applicationContext, "리시버 실행", Toast.LENGTH_LONG).show()

                Thread(Runnable {
                        val action = intent?.action
                        //Log.d("test001", "리시버 실행 (action):"+intent?.action)
                        if(BluetoothDevice.ACTION_FOUND.equals(action)) {
                            val device :BluetoothDevice? = intent?.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                            //Log.d("test001", "검색 : "+device?.name)
                            if(device?.name.equals("raspberrypi")) {
                                bluetoothAdapter?.cancelDiscovery()
                                val rssi = intent?.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE)
                                Log.d("test001", "장치이름 : " + device?.name + " RSSI : " + rssi)

                                runOnUiThread(Runnable {
                                    distance.setText("DISTANCE : "+rssi)
                                })
                                if(rssi>-60){
                                    studing_state = true
                                }else{
                                    studing_state = false
                                }

                                //블루투스 끊겼는지확인?
                                /*if(mBtSocket == null)
                                {
                                    bluetooth_led.setColorFilter(Color.argb(255, 151, 151, 151))
                                }*/

                                //거리 데이터 보내기
                                mOutput?.write(("rssi\n"+rssi+"\n").toByteArray())
                            }
                        }
                }).start()
            }
        }
        Log.d("test001", "블루투스 상태 : "+ BluetoothAdapter.getDefaultAdapter().state)


        //MainActivity.unregisterReceiver(broadcastReceiver);
        registerReceiver(mReceiver, IntentFilter(BluetoothDevice.ACTION_FOUND))

        //올려 뒀을때 타이머!
        var state_checker:Int = 0
        val count = object : CountDownTimer(10000000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if (studing_state == true) {
                    today_study_time++
                    timer.setText("" + today_study_time / 60 + ":" + today_study_time % 60)
                    state_checker = 1
                }

                if(studing_state==false){
                    if(state_checker==1){
                        //timer.setText(""+millisUntilFinished/60000+":"+millisUntilFinished%60000/1000)
                        timer.setText("" + today_study_time / 60 + ":" + today_study_time % 60)


                        fun recordTime () : String{
                            var recording = ""+Calendar.YEAR+":"+Calendar.MONTH+":"+Calendar.DAY_OF_MONTH+"_"+today_study_time
                            return recording
                        }
                        var  studytime = Intent()
                        studytime.putExtra("공부시간",recordTime())
                        setResult(0,studytime)

                        state_checker=2
                    }
                }

                state_checker=0

            }

            override fun onFinish() {
                timer.setText("done!")
            }
        }.start()


        bluetooth_led.setOnClickListener(View.OnClickListener {
            if(bluetoothState == 1 || bluetoothState == 2) {
                bluetoothState = 0
                runOnUiThread(Runnable {  bluetooth_led.setColorFilter(Color.argb(255, 151, 151, 151))  })
                bluetoothAdapter?.disable()
                Toast.makeText(applicationContext, "BLUE close", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(applicationContext, "BLUE search...", Toast.LENGTH_SHORT).show()
                Thread(Runnable {
                    bluetoothState = 1       //연결중 상태
                    Log.d("test001", bluetoothState.toString() + "ㅇㅇㅇㅇㅇㅇ")

                    while (bluetoothState == 1) {
                        runOnUiThread(Runnable { bluetooth_led.setColorFilter(Color.argb(255, 242, 203, 97)) })
                        Thread.sleep(300)
                        runOnUiThread(Runnable { bluetooth_led.setColorFilter(Color.argb(255, 151, 151, 151)) })
                        Thread.sleep(300)
                    }
                }).start()
                //연결 돼있으면 연결 해제
                //블루투스 연결
                bluetoothConnection()
            }
        })


        //시간마다 distanceCheck 실행!!
        Thread(Runnable {
            while(true) {
                if(bluetoothAdapter?.isDiscovering == false) {

                    distanceCheck()
                }
                Thread.sleep(300)
            }
        }).start()

        var Alarm_btn = findViewById<Button>(R.id.Alram)
        Alarm_btn.setOnClickListener(View.OnClickListener {
            val intent = Intent(this,Alarm::class.java)
            startActivityForResult(intent, 0)
        })

        btn_calendar.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, TimeRecord::class.java)
            startActivity(intent)
        })

        btn_option.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)})




    }

    fun distanceCheck(){
        if (bluetoothAdapter?.isDiscovering == true) {
            bluetoothAdapter?.cancelDiscovery()
        }
        bluetoothAdapter?.startDiscovery()
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

    fun bluetoothConnection() {
        //블루투스 연결
//        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if(bluetoothAdapter != null) {
            Log.d("test001", "Device does not support BlueTooth")
        }
        if(!bluetoothAdapter!!.isEnabled){
            val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(intent, RESULT_OK) // what is requestCode?
        }

        //기기 연결
        Log.d("test001", "기기 연결 해볼까?")
        var pairedDevices : Set<BluetoothDevice> = bluetoothAdapter!!.bondedDevices
        var arrayAdapter = ArrayList<String>()
        if(pairedDevices.size > 0) {
            Log.d("test001", "if문에 걸렸어")
            for(device in pairedDevices) {
                arrayAdapter.add(device.name+ "\n"+device.address)
            }
        }
        for(adapter in arrayAdapter)
            Log.d("test001", adapter)

        try{
            //bluetoothAdapter!!.startDiscovery()
            var heroDevice = bluetoothAdapter!!.getRemoteDevice("B8:27:EB:1B:27:9E")
            mBtSocket = heroDevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"))
            mBtSocket =
                heroDevice.javaClass.getMethod("createRfcommSocket", *arrayOf<Class<*>>(Int::class.javaPrimitiveType!!))
                    .invoke(heroDevice, 1) as BluetoothSocket


        } catch(e : Exception) {
            bluetoothState = 0
            e.printStackTrace()
            Log.d("test001", "아 여기도 에러가 떳어요 : "+e.printStackTrace())
        }
        Thread(Runnable {

            try {
                // 소켓을 연결한다.
                Log.d("test001", "연결 중")
                mBtSocket?.connect()
                if(mBtSocket != null)
                {
                    runOnUiThread(Runnable {
                        bluetooth_led.setColorFilter(Color.argb(255, 0, 45, 219))
                    })
                }

                Log.d("test001", "연결 완료")
                bluetoothState = 2
                runOnUiThread(Runnable {
                    Toast.makeText(applicationContext, "BLUE connect!", Toast.LENGTH_SHORT).show()
                })
                // 입출력을 위한 스트림 오브젝트를 얻는다

                mInput = mBtSocket?.getInputStream()
                mOutput = mBtSocket?.getOutputStream()

                while (true) {
                    //mOutput?.writer(charset("CanyouSpeakKOR?"))
                    // 입력 데이터를 그대로 출력한다
                    //mOutput?.write(mInput!!.read())


                    val sendIntent = Intent("com.example.todolist.SEND_BROAD_CAST")

                    // Register the BroadcastReceiver



                    mOutput?.write(("r\n").toByteArray())
                    Log.d("test001", "데이터 보냄")
                    //Log.d("test001", "데이터 받음"+ mInput?.read())
                    var ca = Calendar.getInstance()
                    ca.add(Calendar.MINUTE, 2)
                    var date = SimpleDateFormat("MMddHHmmyyyy").format(ca.time)
                    mOutput?.write(("T_set\n"+date+"\n").toByteArray())
                    Log.d("test001", "데이터 보냄, 현재시간 : "+ date )


                    //Thread.sleep(3000)
                    //mOutput?.write(("print\n").toByteArray())
                    //Log.d("test001", "데이터 보냄")
                    //Thread.sleep(3000)
                    //mOutput?.write(("q").toByteArray())
                    //Log.d("test001", "데이터 보냄")
                    //Thread.sleep(3000)

                    break
                }

            } catch (e: Exception) {
                Log.d("test001", "에러 또떠 ")
                bluetoothState = 0
                e.printStackTrace()

            }
        }).start()

    }

    private val REQUEST_ACCESS_FINE_LOCATION = 1000
    fun permissionCehck(){

    }


    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        return false
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            0-> {
                var result = data?.getLongExtra("결과", 0)
                Log.d("test003",""+result+" 차이 ")

                if (result != null) {
                    val count = object : CountDownTimer(result, 5000) {
                        override fun onTick(millisUntilFinished: Long) {
                        }

                        override fun onFinish() {
                            mOutput?.write(("alarm\n").toByteArray())
                            Log.d("test003", "데이터 보냄")

                        }
                    }.start()
                }
            }


        }


    }
}
