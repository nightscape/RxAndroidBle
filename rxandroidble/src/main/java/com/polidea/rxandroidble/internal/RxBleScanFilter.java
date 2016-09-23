package com.polidea.rxandroidble.internal;

import android.bluetooth.BluetoothAdapter;
import android.os.ParcelUuid;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import no.nordicsemi.android.support.v18.scanner.ScanFilter;

/**
 * Created by griffin on 9/18/16.
 */
public class RxBleScanFilter {
    private ScanFilter scanFilterCompat;

    private RxBleScanFilter(@NonNull ScanFilter scanFilter) {
        this.scanFilterCompat = scanFilter;
    }

    public ScanFilter getScanFilterCompat() {
        return scanFilterCompat;
    }

    public static List<ScanFilter> getScanFilters(List<RxBleScanFilter> scanFilters){
        List<ScanFilter> scanFilterRet = new ArrayList<>();
        for(RxBleScanFilter filter: scanFilters){
            scanFilterRet.add(filter.getScanFilterCompat());
        }
        return scanFilterRet;
    }

    public static final class Builder {
        private final ScanFilter.Builder builder;

        public Builder() {
            this.builder = new ScanFilter.Builder();
        }

        /**
         * Set filter on device name.
         */
        public Builder setDeviceName(String deviceName) {
            builder.setDeviceName(deviceName);
            return this;
        }

        /**
         * Set filter on device address.
         *
         * @param deviceAddress The device Bluetooth address for the filter. It needs to be in the
         *                      format of "01:02:03:AB:CD:EF". The device address can be validated using
         *                      {@link BluetoothAdapter#checkBluetoothAddress}.
         * @throws IllegalArgumentException If the {@code deviceAddress} is invalid.
         */
        public Builder setDeviceAddress(String deviceAddress) {
            builder.setDeviceAddress(deviceAddress);
            return this;
        }

        /**
         * Set filter on service uuid.
         */
        public Builder setServiceUuid(ParcelUuid serviceUuid) {
            builder.setServiceUuid(serviceUuid);
            return this;
        }

        /**
         * Set filter on partial service uuid. The {@code uuidMask} is the bit mask for the
         * {@code serviceUuid}. Set any bit in the mask to 1 to indicate a match is needed for the
         * bit in {@code serviceUuid}, and 0 to ignore that bit.
         *
         * @throws IllegalArgumentException If {@code serviceUuid} is {@code null} but
         *                                  {@code uuidMask} is not {@code null}.
         */
        public Builder setServiceUuid(ParcelUuid serviceUuid, ParcelUuid uuidMask) {
            builder.setServiceUuid(serviceUuid, uuidMask);
            return this;
        }

        /**
         * Set filtering on service data.
         *
         * @throws IllegalArgumentException If {@code serviceDataUuid} is null.
         */
        public Builder setServiceData(ParcelUuid serviceDataUuid, byte[] serviceData) {
            builder.setServiceData(serviceDataUuid, serviceData);
            return this;
        }

        /**
         * Set partial filter on service data. For any bit in the mask, set it to 1 if it needs to
         * match the one in service data, otherwise set it to 0 to ignore that bit.
         * <p>
         * The {@code serviceDataMask} must have the same length of the {@code serviceData}.
         *
         * @throws IllegalArgumentException If {@code serviceDataUuid} is null or
         *                                  {@code serviceDataMask} is {@code null} while {@code serviceData} is not or
         *                                  {@code serviceDataMask} and {@code serviceData} has different length.
         */
        public Builder setServiceData(ParcelUuid serviceDataUuid,
                                      byte[] serviceData, byte[] serviceDataMask) {
            builder.setServiceData(serviceDataUuid, serviceData, serviceDataMask);
            return this;
        }

        /**
         * Set filter on on manufacturerData. A negative manufacturerId is considered as invalid id.
         * <p>
         * Note the first two bytes of the {@code manufacturerData} is the manufacturerId.
         *
         * @throws IllegalArgumentException If the {@code manufacturerId} is invalid.
         */
        public Builder setManufacturerData(int manufacturerId, byte[] manufacturerData) {
            builder.setManufacturerData(manufacturerId, manufacturerData);
            return this;
        }

        /**
         * Set filter on partial manufacture data. For any bit in the mask, set it the 1 if it needs
         * to match the one in manufacturer data, otherwise set it to 0.
         * <p>
         * The {@code manufacturerDataMask} must have the same length of {@code manufacturerData}.
         *
         * @throws IllegalArgumentException If the {@code manufacturerId} is invalid, or
         *                                  {@code manufacturerData} is null while {@code manufacturerDataMask} is not,
         *                                  or {@code manufacturerData} and {@code manufacturerDataMask} have different
         *                                  length.
         */
        public Builder setManufacturerData(int manufacturerId, byte[] manufacturerData,
                                           byte[] manufacturerDataMask) {
            builder.setManufacturerData(manufacturerId, manufacturerData, manufacturerDataMask);
            return this;
        }

        /**
         * Build {@link ScanFilter}.
         *
         * @throws IllegalArgumentException If the filter cannot be built.
         */
        public RxBleScanFilter build() {
            return new RxBleScanFilter(builder.build());
        }


    }
}
