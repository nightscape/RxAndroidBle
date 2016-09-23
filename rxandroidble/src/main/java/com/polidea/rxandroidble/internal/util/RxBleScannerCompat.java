package com.polidea.rxandroidble.internal.util;

import android.support.annotation.Nullable;

import java.util.List;

import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat;
import no.nordicsemi.android.support.v18.scanner.ScanCallback;
import no.nordicsemi.android.support.v18.scanner.ScanFilter;
import no.nordicsemi.android.support.v18.scanner.ScanSettings;

/**
 * Created by griffin on 9/18/16.
 */
public class RxBleScannerCompat {
    private final BluetoothLeScannerCompat scannerCompat;

    public RxBleScannerCompat(@Nullable BluetoothLeScannerCompat scanner) {
        this.scannerCompat = scanner;
    }

    public BluetoothLeScannerCompat getScannerCompat() {
        return scannerCompat;
    }

    public void startScanCompat(List<ScanFilter> scanFilterList, ScanSettings scanSettings, ScanCallback scanCallback){

        getScannerCompat().startScan(scanFilterList,scanSettings,scanCallback);
    }

    public void stopScanCompat(ScanCallback scanCallback){
        getScannerCompat().stopScan(scanCallback);
    }
}
