package mx.tecnm.tepic.ladm_u4_garciamoreno_alumno

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import mx.tecnm.tepic.ladm_u4_garciamoreno_alumno.databinding.ActivityMainBinding
import java.io.InputStream
import java.io.OutputStream

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var bluetoothAdapter: BluetoothAdapter

    var dispositivoBt=ArrayList<BluetoothDevice>()

    val UUID=java.util.UUID.fromString("62ff97af-13e9-47db-a3ee-9bc1c4d3757b")
    val NOMBRE = "ASISTENCIA"

    var REQUEST_ENABLE_BLUETOOTH=1
    val siPermiso=1

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bluetoothAdapter=BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH)
        }

        binding.buscar.setOnClickListener {
            var arr = dispositivosConectados()
            binding.lista.adapter=ArrayAdapter(this,android.R.layout.simple_list_item_1,arr)
        }
        binding.lista.setOnItemClickListener { adapterView, view, i, l ->
            if(!binding.nocontrol.text.toString().equals("")) {
                var cliente = Cliente(dispositivoBt[i], this)
                cliente.start()
            }
        }

    }


    @SuppressLint("MissingPermission")
    fun dispositivosConectados():ArrayList<String>{
        var dispositivos = ArrayList<String>()
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices

        for(pD in pairedDevices!!){
            dispositivos.add(pD.name)
            dispositivoBt.add(pD)
        }



        return dispositivos

    }
    @SuppressLint("MissingPermission")
    inner class Cliente(var dispositivo:BluetoothDevice,activity: MainActivity):Thread(){
        //lateinit var dispositivo: BluetoothDevice
        lateinit var socket: BluetoothSocket
        var activity=activity
        init {
            //this.dispositivo=dispositivo
            try{
                socket=dispositivo.createRfcommSocketToServiceRecord(UUID)
            }catch(e:Exception){

            }
        }

        override fun run() {
            super.run()
            Log.i("client", "Connecting")
            try{

                socket.connect()
                runOnUiThread {
                    Toast.makeText(activity,"Conectado como cliente", Toast.LENGTH_LONG).show()
                }
            }catch(e:Exception){

            }
            val outputStream: OutputStream = socket.outputStream
            val inputStream: InputStream = socket.inputStream
            try{
                outputStream.write(binding.nocontrol.text.toString().toByteArray())
                Log.i("client", "Sent")
            }catch (e:Exception){
                Log.e("client", "Cannot send", e)
            }finally {
                outputStream.close()
                inputStream.close()
                this.socket.close()
            }
        }


    }
    inner class Transmision(var socket: BluetoothSocket):Thread(){
        private val inputStream: InputStream = socket.inputStream
        private val outputStream: OutputStream = socket.outputStream
        private val buffer:ByteArray = ByteArray(1024)

        override fun run() {
            super.run()
            var numBytes: Int
            try{
                outputStream.write(binding.nocontrol.text.toString().toByteArray())

            }catch (e:Exception){

            }

        }
    }

}