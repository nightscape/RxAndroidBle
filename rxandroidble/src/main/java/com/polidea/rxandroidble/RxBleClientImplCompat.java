package com.polidea.rxandroidble;

import android.os.ParcelUuid;
import android.support.annotation.Nullable;

import com.polidea.rxandroidble.exceptions.BleScanException;
import com.polidea.rxandroidble.internal.RxBleDeviceProvider;
import com.polidea.rxandroidble.internal.RxBleRadio;
import com.polidea.rxandroidble.internal.RxBleScanFilter;
import com.polidea.rxandroidble.internal.RxBleScanSettings;
import com.polidea.rxandroidble.internal.operations.RxBleRadioOperationScanCompat;
import com.polidea.rxandroidble.internal.util.LocationServicesStatus;
import com.polidea.rxandroidble.internal.util.RxBleAdapterWrapper;
import com.polidea.rxandroidble.internal.util.RxBleScannerCompat;
import com.polidea.rxandroidble.internal.util.UUIDUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class RxBleClientImplCompat extends RxBleClientImpl {


    private final RxBleScannerCompat rxBleScannerCompat;

    private final Map<List<RxBleScanFilter>, Observable<RxBleScanResult>> queuedScanOperationsCompat = new HashMap<>();

    RxBleClientImplCompat(RxBleAdapterWrapper rxBleAdapterWrapper, RxBleScannerCompat rxBleScannerCompat, RxBleRadio rxBleRadio, Observable<RxBleAdapterStateObservable.BleAdapterState> adapterStateObservable, UUIDUtil uuidUtil, LocationServicesStatus locationServicesStatus, RxBleDeviceProvider rxBleDeviceProvider) {
        super(rxBleAdapterWrapper, rxBleRadio, adapterStateObservable, uuidUtil, locationServicesStatus, rxBleDeviceProvider);

        this.rxBleScannerCompat = rxBleScannerCompat;
    }

    @Override
    public Observable<RxBleScanResult> scanBleDevices(@Nullable UUID... filterServiceUUIDs) {
        List<RxBleScanFilter> scanFilters = new ArrayList<>();
        if (filterServiceUUIDs != null) {
            for (UUID uuid : filterServiceUUIDs) {
                RxBleScanFilter scanFilter = new RxBleScanFilter.Builder().setServiceUuid(ParcelUuid.fromString(uuid.toString())).build();
                scanFilters.add(scanFilter);
            }
        }
        return scanBleDevicesCompat(scanFilters,new RxBleScanSettings.Builder().build());
    }


    public Observable<RxBleScanResult> scanBleDevicesCompat(@Nullable List<RxBleScanFilter> scanFilters, RxBleScanSettings scanSettings) {

        if (!rxBleAdapterWrapper.hasBluetoothAdapter()) {
            return Observable.error(new BleScanException(BleScanException.BLUETOOTH_NOT_AVAILABLE));
        } else if (!rxBleAdapterWrapper.isBluetoothEnabled()) {
            return Observable.error(new BleScanException(BleScanException.BLUETOOTH_DISABLED));
        } else if (!locationServicesStatus.isLocationPermissionOk()) {
            return Observable.error(new BleScanException(BleScanException.LOCATION_PERMISSION_MISSING));
        } else if (!locationServicesStatus.isLocationProviderOk()) {
            return Observable.error(new BleScanException(BleScanException.LOCATION_SERVICES_DISABLED));
        } else {
            return initializeScanCompat(scanFilters, scanSettings);
        }
    }

    private Observable<RxBleScanResult> initializeScanCompat(List<RxBleScanFilter> scanFilterList, RxBleScanSettings scanSettings) {
        synchronized (queuedScanOperationsCompat) {
            Observable<RxBleScanResult> queuedScan = queuedScanOperationsCompat.get(scanFilterList);
            if (queuedScan == null) {
                queuedScan = createScanOperationCompat(scanFilterList, scanSettings);
                queuedScanOperationsCompat.put(scanFilterList, queuedScan);
            }
            return queuedScan;
        }
    }

    private Observable<RxBleScanResult> createScanOperationCompat(List<RxBleScanFilter> scanFilterList, RxBleScanSettings scanSettings) {
        final RxBleRadioOperationScanCompat scanOperation = new RxBleRadioOperationScanCompat(rxBleScannerCompat, scanFilterList, scanSettings);
        return rxBleRadio.queue(scanOperation)
                .doOnUnsubscribe(() -> {

                    synchronized (queuedScanOperationsCompat) {
                        scanOperation.stop();
                        queuedScanOperationsCompat.remove(scanFilterList);
                    }
                })
                .mergeWith(bluetoothAdapterOffExceptionObservable())
                .map(this::convertToPublicScanResult)
                .share();
    }



}
