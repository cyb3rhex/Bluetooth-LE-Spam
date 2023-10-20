package de.simonkelmann.bluetoothlespam.Services

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertisingSet
import android.bluetooth.le.AdvertisingSetCallback
import android.bluetooth.le.AdvertisingSetParameters
import android.bluetooth.le.BluetoothLeAdvertiser
import android.content.pm.PackageManager
import android.os.Build
import android.os.ParcelUuid
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import de.simonkelmann.bluetoothlespam.AppContext.AppContext
import de.simonkelmann.bluetoothlespam.PermissionCheck.PermissionCheck
import java.util.UUID


class BleService {

    private val _logTag = "BleService"
    private var _currentAdvertisingSet:AdvertisingSet? = null
    private var _macAddress = ""

    // apple = 004C => 76
    var manufacturerId = 76
    var manufacturerSpecificData:ByteArray = byteArrayOf(0x1e,
        0xff.toByte(), 0x4c, 0x00, 0x07, 0x19, 0x07, 0x02, 0x20, 0x75, 0xaa.toByte(), 0x30, 0x01, 0x00, 0x00, 0x45, 0x12, 0x12, 0x12, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00)

    @RequiresApi(Build.VERSION_CODES.O)
    fun advertise(){
        Log.d(_logTag, "Calling Function")

        val advertiser: BluetoothLeAdvertiser = BluetoothAdapter.getDefaultAdapter().bluetoothLeAdvertiser

        val parameters = AdvertisingSetParameters.Builder()
            .setLegacyMode(false) // True by default, but set here as a reminder.
            .setConnectable(true)
            .setInterval(AdvertisingSetParameters.INTERVAL_HIGH)
            .setTxPowerLevel(AdvertisingSetParameters.TX_POWER_HIGH)
            .build()

        val data = AdvertiseData.Builder()
            .setIncludeDeviceName(true)
            .addManufacturerData(manufacturerId, manufacturerSpecificData)
            .build()

        val callback: AdvertisingSetCallback = object : AdvertisingSetCallback() {
            override fun onAdvertisingSetStarted(
                advertisingSet: AdvertisingSet,
                txPower: Int,
                status: Int
            ) {
                Log.i(
                    _logTag, "onAdvertisingSetStarted(): txPower:" + txPower + " , status: " + status
                )

                _currentAdvertisingSet = advertisingSet
            }

            override fun onAdvertisingDataSet(advertisingSet: AdvertisingSet, status: Int) {
                Log.i(_logTag, "onAdvertisingDataSet() :status:$status")
            }

            override fun onScanResponseDataSet(advertisingSet: AdvertisingSet, status: Int) {
                Log.i(_logTag, "onScanResponseDataSet(): status:$status")
            }

            override fun onAdvertisingSetStopped(advertisingSet: AdvertisingSet) {
                Log.i(_logTag, "onAdvertisingSetStopped():")
            }
        }

        if(PermissionCheck.checkPermission(Manifest.permission.BLUETOOTH_ADVERTISE, AppContext.getActivity())){
            advertiser.startAdvertisingSet(parameters, data, null, null, null, callback);
        }

        // After onAdvertisingSetStarted callback is called, you can modify the
        // advertising data and scan response data:
        // After onAdvertisingSetStarted callback is called, you can modify the
        // advertising data and scan response data:
        if(_currentAdvertisingSet != null){
            _currentAdvertisingSet!!.setAdvertisingData(
                AdvertiseData.Builder().setIncludeDeviceName(true).setIncludeTxPowerLevel(true).build()
            )
        } else {
            Log.d(_logTag, "CurrentAdvertisingSet not yet initialized 1");
        }


        // Wait for onAdvertisingDataSet callback...
        // Wait for onAdvertisingDataSet callback...
        if(_currentAdvertisingSet != null){
            _currentAdvertisingSet!!.setScanResponseData(
                AdvertiseData.Builder().addServiceUuid(ParcelUuid(UUID.randomUUID())).build()
            )
        } else {
            Log.d(_logTag, "CurrentAdvertisingSet not yet initialized 2");
        }

        // Wait for onScanResponseDataSet callback...

        // When done with the advertising:
        // Wait for onScanResponseDataSet callback...

        // When done with the advertising:
        //Log.d(_logTag, "Stopping Advertiser");
        //advertiser.stopAdvertisingSet(callback)

    }
}