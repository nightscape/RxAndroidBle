package com.polidea.rxandroidble.internal.operations;

import com.polidea.rxandroidble.exceptions.BleScanException;
import com.polidea.rxandroidble.internal.RxBleInternalScanResult;
import com.polidea.rxandroidble.internal.RxBleLog;
import com.polidea.rxandroidble.internal.RxBleRadioOperation;
import com.polidea.rxandroidble.internal.RxBleScanSettings;
import com.polidea.rxandroidble.internal.RxBleScanFilter;
import com.polidea.rxandroidble.internal.util.RxBleScannerCompat;

import java.util.List;

import no.nordicsemi.android.support.v18.scanner.ScanCallback;
import no.nordicsemi.android.support.v18.scanner.ScanResult;

public class RxBleRadioOperationScanCompat extends RxBleRadioOperation<RxBleInternalScanResult> {

    private RxBleScannerCompat rxBluetoothScanner;
    private RxBleScanSettings rxScanSettings;
    private List<RxBleScanFilter> rxScanFilters;

    public RxBleRadioOperationScanCompat(RxBleScannerCompat rxBluetoothScanner, List<RxBleScanFilter> rxScanFilters, RxBleScanSettings rxScanSettings) {
        this.rxBluetoothScanner = rxBluetoothScanner;
        this.rxScanSettings = rxScanSettings;
        this.rxScanFilters = rxScanFilters;
    }

    private final ScanCallback scanCallbackCompat = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            onNext(new RxBleInternalScanResult(result.getDevice(), result.getRssi(), result.getScanRecord().getBytes()));
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            for (ScanResult result :
                    results) {
                onNext(new RxBleInternalScanResult(result.getDevice(), result.getRssi(), result.getScanRecord().getBytes()));
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            onError(new BleScanException(BleScanException.BLUETOOTH_CANNOT_START));

        }
    };

    @Override
    protected void protectedRun() throws Throwable {
        try {
            rxBluetoothScanner.startScanCompat(RxBleScanFilter.getScanFilters(getScanFilters()), getScanSettings().getScanSettingsCompat(), scanCallbackCompat);
        } catch (Throwable throwable) {
            RxBleLog.e(throwable, "Error while calling BluetoothAdapter.startLeScan()");
            onError(new BleScanException(BleScanException.BLUETOOTH_CANNOT_START));
        } finally {
            releaseRadio();
        }
    }



    public synchronized void stop() {
        rxBluetoothScanner.stopScanCompat(scanCallbackCompat);
    }

    public List<RxBleScanFilter> getScanFilters() {
        return rxScanFilters;
    }

    public RxBleScanSettings getScanSettings() {
        return rxScanSettings;
    }
}
