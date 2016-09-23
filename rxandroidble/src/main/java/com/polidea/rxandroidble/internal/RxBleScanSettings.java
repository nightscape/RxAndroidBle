package com.polidea.rxandroidble.internal;

import android.support.annotation.NonNull;

import no.nordicsemi.android.support.v18.scanner.ScanSettings;

/**
 * Created by griffin on 9/18/16.
 */

public class RxBleScanSettings {

    private final ScanSettings scanSettingsCompat;

    private RxBleScanSettings(@NonNull ScanSettings scanSettingsCompat){
        this.scanSettingsCompat = scanSettingsCompat;
    }

    public int getScanMode() {
        return scanSettingsCompat.getScanMode();
    }

    public int getCallbackType() {
        return scanSettingsCompat.getCallbackType();
    }

    public int getMatchMode() {
        return scanSettingsCompat.getMatchMode();
    }

    public int getNumOfMatches() {
        return scanSettingsCompat.getNumOfMatches();
    }

    public boolean getUseHardwareFilteringIfSupported() {
        return scanSettingsCompat.getUseHardwareFilteringIfSupported();
    }

    public boolean getUseHardwareBatchingIfSupported() {
        return scanSettingsCompat.getUseHardwareBatchingIfSupported();
    }

    public boolean getUseHardwareCallbackTypesIfSupported() {
        return scanSettingsCompat.getUseHardwareCallbackTypesIfSupported();
    }

    public long getMatchLostDeviceTimeout() {
        return scanSettingsCompat.getMatchLostDeviceTimeout();
    }

    public long getMatchLostTaskInterval() {
        return scanSettingsCompat.getMatchLostTaskInterval();
    }

    /**
     * Returns report delay timestamp based on the device clock.
     */
    public long getReportDelayMillis() {
        return scanSettingsCompat.getReportDelayMillis();
    }

    public ScanSettings getScanSettingsCompat(){
        return scanSettingsCompat;
    }

    public static final class Builder {
        private final ScanSettings.Builder builder;

        public Builder() {
            builder = new ScanSettings.Builder();
        }

        /**
         * Set scan mode for Bluetooth LE scan.
         *
         * @param scanMode The scan mode can be one of {@link ScanSettings#SCAN_MODE_LOW_POWER},
         *                 {@link ScanSettings#SCAN_MODE_BALANCED} or
         *                 {@link ScanSettings#SCAN_MODE_LOW_LATENCY}.
         * @throws IllegalArgumentException If the {@code scanMode} is invalid.
         */
        public Builder setScanMode(int scanMode) {
            builder.setScanMode(scanMode);
            return this;
        }

        /**
         * Set callback type for Bluetooth LE scan.
         *
         * @param callbackType The callback type flags for the scan.
         * @throws IllegalArgumentException If the {@code callbackType} is invalid.
         */
        public Builder setCallbackType(int callbackType) {
            builder.setCallbackType(callbackType);
            return this;
        }

        /**
         * Set report delay timestamp for Bluetooth LE scan.
         *
         * @param reportDelayMillis Delay of report in milliseconds. Set to 0 to be notified of
         *                          results immediately. Values &gt; 0 causes the scan results to be queued up and
         *                          delivered after the requested delay or when the internal buffers fill up.
         * @throws IllegalArgumentException If {@code reportDelayMillis} &lt; 0.
         */
        public Builder setReportDelay(long reportDelayMillis) {
            builder.setReportDelay(reportDelayMillis);
            return this;
        }

        /**
         * Set the number of matches for Bluetooth LE scan filters hardware match
         *
         * @param numOfMatches The num of matches can be one of
         *                     {@link ScanSettings#MATCH_NUM_ONE_ADVERTISEMENT} or
         *                     {@link ScanSettings#MATCH_NUM_FEW_ADVERTISEMENT} or
         *                     {@link ScanSettings#MATCH_NUM_MAX_ADVERTISEMENT}
         * @throws IllegalArgumentException If the {@code matchMode} is invalid.
         */
        public Builder setNumOfMatches(int numOfMatches) {
            builder.setNumOfMatches(numOfMatches);
            return this;
        }

        /**
         * Set match mode for Bluetooth LE scan filters hardware match
         *
         * @param matchMode The match mode can be one of
         *                  {@link ScanSettings#MATCH_MODE_AGGRESSIVE} or
         *                  {@link ScanSettings#MATCH_MODE_STICKY}
         * @throws IllegalArgumentException If the {@code matchMode} is invalid.
         */
        public Builder setMatchMode(int matchMode) {
            builder.setMatchMode(matchMode);
            return this;
        }

        /**
         * Several phones may have some issues when it comes to offloaded filtering. Even if it should be supported,
         * it may not work as expected. It has been observed for example, that setting 2 filters with different devices addresses on Nexus 6 with Lollipop
         * gives no callbacks if one or both devices advertise. See https://code.google.com/p/android/issues/detail?id=181561.
         *
         * @param use true to enable (default) hardware offload filtering. If false a compat software filtering will be used (uses much more resources).
         */
        public Builder setUseHardwareFilteringIfSupported(boolean use) {
            builder.setUseHardwareFilteringIfSupported(use);
            return this;
        }

        /**
         * Some devices, for example Samsung S6 and S6 Edge with Lollipop, return always the same RSSI value for all devices if offloaded batching is used.
         * Batching may also be emulated using a compat mechanism - a periodically called timer. Timer approach requires more resources but reports devices
         * in constant delays and works on devices that does not support offloaded batching. In comparison, when setReportDelay(..) is called with
         * parameter 1000 the standard, hardware triggered callback will be called every 1500ms +-200ms.
         *
         * @param use true to enable (default) hardware offloaded batching if they are supported. False to always use compat mechanism.
         */
        public Builder setUseHardwareBatchingIfSupported(boolean use) {
            builder.setUseHardwareBatchingIfSupported(use);
            return this;
        }


        public Builder setUseHardwareCallbackTypesIfSupported(boolean use) {
            builder.setUseHardwareCallbackTypesIfSupported(use);
            return this;
        }

        public Builder setMatchOptions(final long deviceTimeoutMillis, final long taskIntervalMillis) {
            builder.setMatchOptions(deviceTimeoutMillis, taskIntervalMillis);
            return this;
        }

        /**
         * Pre-Lollipop scanning requires a wakelock and the CPU cannot go to sleep. To conserve power we can optionally
         * scan for a certain duration (scan interval) and then rest for a time before starting scanning again. Won't
         * affect Lollipop or later devices
         *
         * @param scanInterval interval in ms to scan at a time
         * @param restInterval interval to sleep for without scanning before scanning again for scanInterval
         * @return
         */
        public Builder setPowerSave(final long scanInterval, final long restInterval) {
            builder.setPowerSave(scanInterval, restInterval);
            return this;
        }

        /**
         * Build {@link ScanSettings}.
         */
        public RxBleScanSettings build() {
            return new RxBleScanSettings(builder.build());
        }
    }

}
