package com.polidea.rxandroidble;

import android.bluetooth.BluetoothDevice;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.polidea.rxandroidble.internal.util.RxBleScannerCompat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat;
import no.nordicsemi.android.support.v18.scanner.ScanCallback;
import no.nordicsemi.android.support.v18.scanner.ScanFilter;
import no.nordicsemi.android.support.v18.scanner.ScanResult;
import no.nordicsemi.android.support.v18.scanner.ScanSettings;

public class MockRxBleScannerCompat extends RxBleScannerCompat {


    private List<ScanResult> scanResults = new ArrayList<ScanResult>();
    private Set<BluetoothDevice> bondedDeviceSet = new HashSet<BluetoothDevice>();

    public MockRxBleScannerCompat() {
        super(null);
    }

    public MockRxBleScannerCompat(@Nullable BluetoothLeScannerCompat scanner) {
        super(scanner);
    }

    @Override
    public void startScanCompat(List<ScanFilter> filters, @NonNull ScanSettings settings, @NonNull ScanCallback callback) {
        for (ScanResult result : scanResults) {
            if (checkScanFilters(filters, result))
                callback.onScanResult(0, result);
        }
    }

    private boolean checkScanFilters(List<ScanFilter> filters, ScanResult scanResult) {
        if (filters != null && !filters.isEmpty()) {
            for (ScanFilter filter : filters) {
                if (filter.matches(scanResult)) {
                    return true;
                }
            }
        } else {
            return true;
        }
        return false;
    }

    @Override
    public void stopScanCompat(ScanCallback scanCallback) {

    }

    public void addScanResult(ScanResult scanResult) {
        scanResults.add(scanResult);
    }

    public void addBondedDevice(BluetoothDevice bluetoothDevice) {
        bondedDeviceSet.add(bluetoothDevice);
    }

}
