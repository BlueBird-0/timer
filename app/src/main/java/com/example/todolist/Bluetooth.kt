package com.example.todolist

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.support.v4.app.ActivityCompat.startActivity
import android.support.v4.app.ActivityCompat.startActivityForResult
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

public class Bluetooth (val mBluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()){
    var bluetoothDevice : BluetoothDevice  ?= null
    var deviceName : String ?= null
    val UUID = java.util.UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")
    var bluetoothSocket : BluetoothSocket ?= null
    var outputStream : OutputStream ?= null
    var inputStream : InputStream ?= null

    var readBufferPosition : Int = 0
    var readBuffer = ByteArray(1024)
    var workerThread : Unit ?= null

    fun bluetoothAvailable() : Boolean {
        if(mBluetoothAdapter == null){
            return false
        }
        return true
    }

    fun bluetoothOn() : Intent? {
        //블루투스 장치가 켜져 있지 않은지 체크
        var enableIntent : Intent ?= null
        if(!mBluetoothAdapter.isEnabled) {
            enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        }
        return enableIntent
    }

    fun bluetoothDiscoverable() : Intent? {
        var discoverableIntent : Intent ?= null
        //검색 가능한 상태인지 확인
        if(mBluetoothAdapter.scanMode != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            //검색 가능한 상태로 바꾸기 위한 액티비티 띄우기
            discoverableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
        }
        return discoverableIntent
    }


    fun connectDevice(deviceName : String) {
        //페어링된 디바이스들을 모두 검색
        var pairedDevices : Set<BluetoothDevice> = mBluetoothAdapter.bondedDevices
        for(tempDevice in pairedDevices) {
            //사용자가 선택한 이름과 같은 디바이스로 설정하고 반복문 종료
            if(deviceName.equals(tempDevice.name)) {
                bluetoothDevice = tempDevice
                break
            }
        }
        //Rfcomm채널을 통해 블루투스 디바이스와 통신하는 소켓 생성
        try{
            bluetoothSocket = bluetoothDevice?.createRfcommSocketToServiceRecord(UUID)
            bluetoothSocket?.connect()
            //데이터 송,수신 스트림을 얻어옵니다.
            outputStream = bluetoothSocket?.outputStream
            inputStream = bluetoothSocket?.inputStream
            //데이터 수신 함수 호출
            receiveData()
        }catch ( e:IOException ) {
            e.printStackTrace()
            Log.d("test001","catch IOException - fun connectDevice")
        }
    }

    fun sendData(text : String){
        //문자열에 개행문자("\n"를 추가합니다)
        text.plus("\n")
        try{
            outputStream?.write(text.toByteArray())
        }catch (e:Exception) {
            e.printStackTrace()
            Log.d("test001","catch IOException - fun sendData")
        }
    }

    fun receiveData() {
        val handler : Handler = Handler()
        //데이털를 수신하기 위한 버퍼를 생성
        readBufferPosition = 0;
        readBuffer = ByteArray(1024)

        //데이터를 수신하기 위한 쓰레드 생성
        workerThread = Thread({
            while(Thread.currentThread().isInterrupted) {
                try{
                    //데이터를 수신했는지 확인합니다.
                    var byteAvailable : Int = inputStream!!.available()
                    //데이터가 수신 된 경우
                    if(byteAvailable > 0) {
                        //입력 스트림에서 바이트 단위로 읽어 옵니다.
                        var bytes = ByteArray(byteAvailable)
                        inputStream?.read(bytes)
                        //입력 스트림 바이트를 한 바이트씩 읽어 옵니다.
                        for(i in 0..byteAvailable) {
                            var tempByte : Byte = bytes[i]
                            //개행문자를 기준으로 받음(한줄)
                            if(tempByte.equals('\n')) {
                                //readBuffer배열을 encodedBytes로 복사
                                var encodedBytes = ByteArray(readBufferPosition)
                                System.arraycopy(readBuffer, 0, encodedBytes, 0 ,encodedBytes.size)
                                //인코딩 된 바이트 배열을 문자열로 변환
                                var text = String(encodedBytes, charset("US-ASCII"))
                                readBufferPosition = 0;
                                handler.post(Runnable {
                                    //텍스트 출력
                                    Log.d("test001", "handler.post text출력 테스트용 : " + text)
                                })
                            }
                            else {
                                readBuffer[readBufferPosition++] = tempByte
                            }
                        }
                    }
                }catch (e : IOException){
                    e.printStackTrace()
                    Log.d("test001", "catch IOException - fun receiveData")
                }
            }
        }).start()

    }
}