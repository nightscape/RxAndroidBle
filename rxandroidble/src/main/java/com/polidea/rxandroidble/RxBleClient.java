package com.polidea.rxandroidble;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.polidea.rxandroidble.internal.RxBleDeviceProvider;
import com.polidea.rxandroidble.internal.RxBleLog;
import com.polidea.rxandroidble.internal.radio.RxBleRadioImpl;
import com.polidea.rxandroidble.internal.util.BleConnectionCompat;
import com.polidea.rxandroidble.internal.util.CheckerLocationPermission;
import com.polidea.rxandroidble.internal.util.CheckerLocationProvider;
import com.polidea.rxandroidble.internal.util.LocationServicesStatus;
import com.polidea.rxandroidble.internal.util.ProviderApplicationTargetSdk;
import com.polidea.rxandroidble.internal.util.ProviderDeviceSdk;
import com.polidea.rxandroidble.internal.util.RxBleAdapterWrapper;
import com.polidea.rxandroidble.internal.util.RxBleScannerCompat;
import com.polidea.rxandroidble.internal.util.UUIDUtil;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat;
import rx.Observable;
import rx.Scheduler;
import rx.schedulers.Schedulers;

public abstract class RxBleClient {

    /**
     * Returns instance of RxBleClient using application context. It is required by the client to maintain single instance of RxBleClient.
     *
     * @param context Any Android context
     * @return BLE client instance.
     */
    public static RxBleClient create(@NonNull Context context) {
        return create(context,false);
    }

    public static RxBleClient create(@NonNull Context context, boolean useCompat) {
        final Context applicationContext = context.getApplicationContext();
        final RxBleScannerCompat rxBleScannerCompat = new RxBleScannerCompat(BluetoothLeScannerCompat.getScanner());
        final RxBleRadioImpl rxBleRadio = new RxBleRadioImpl();
        final RxBleAdapterStateObservable adapterStateObservable = new RxBleAdapterStateObservable(applicationContext);
        final BleConnectionCompat bleConnectionCompat = new BleConnectionCompat(context);
        final RxBleAdapterWrapper rxBleAdapter = new RxBleAdapterWrapper(BluetoothAdapter.getDefaultAdapter());

        final ExecutorService executor = Executors.newSingleThreadExecutor();
        final Scheduler gattCallbacksProcessingScheduler = Schedulers.from(executor);
        final LocationManager locationManager = (LocationManager) applicationContext.getSystemService(Context.LOCATION_SERVICE);
        final CheckerLocationPermission checkerLocationPermission = new CheckerLocationPermission(applicationContext);
        final CheckerLocationProvider checkerLocationProvider = new CheckerLocationProvider(locationManager);
        final ProviderApplicationTargetSdk providerApplicationTargetSdk = new ProviderApplicationTargetSdk(applicationContext);
        final ProviderDeviceSdk providerDeviceSdk = new ProviderDeviceSdk();
        final LocationServicesStatus locationServicesStatus = new LocationServicesStatus(
            checkerLocationProvider,
            checkerLocationPermission,
            providerDeviceSdk,
            providerApplicationTargetSdk
        );
        final RxBleDeviceProvider deviceProvider = new RxBleDeviceProvider(
            rxBleAdapter,
            rxBleRadio,
            bleConnectionCompat,
            adapterStateObservable,
            gattCallbacksProcessingScheduler
        );
        if (useCompat) {
            return new RxBleClientImplCompat(
                    rxBleAdapter,
                    rxBleScannerCompat,
                    rxBleRadio,
                    adapterStateObservable,
                    new UUIDUtil(),
                    locationServicesStatus,
                    deviceProvider
            );

        } else {
            return new RxBleClientImpl(
                    rxBleAdapter,
                    rxBleRadio,
                    adapterStateObservable,
                    new UUIDUtil(),
                    locationServicesStatus,
                    deviceProvider);
        }
    }

    /**
     * A convenience method.
     * Sets the log level that will be printed out in the console. Default is LogLevel.NONE which logs nothing.
     *
     * @param logLevel the minimum log level to log
     */
    public static void setLogLevel(@RxBleLog.LogLevel int logLevel) {
        RxBleLog.setLogLevel(logLevel);
    }

    /**
     * Obtain instance of RxBleDevice for provided MAC address. This may be the same instance that was provided during scan operation but
     * this in not guaranteed.
     *
     * @param macAddress Bluetooth LE device MAC address.
     * @return Handle for Bluetooth LE operations.
     */
    public abstract RxBleDevice getBleDevice(@NonNull String macAddress);

    public abstract Set<RxBleDevice> getBondedDevices();

    /**
     * Returns an infinite observable emitting BLE scan results.
     * Scan is automatically started and stopped based on the Observable lifecycle.
     * Scan is started on subscribe and stopped on unsubscribe. You can safely subscribe multiple observers to this observable.
     * <p>
     * The library automatically handles Bluetooth adapter state changes but you are supposed to prompt
     * the user to enable it if it's disabled.
     *
     * @param filterServiceUUIDs Filtering settings. Scan results are only filtered by exported services.
     * @throws com.polidea.rxandroidble.exceptions.BleScanException emits in case of error starting the scan
     */
    public abstract Observable<RxBleScanResult> scanBleDevices(@Nullable UUID... filterServiceUUIDs);


}
