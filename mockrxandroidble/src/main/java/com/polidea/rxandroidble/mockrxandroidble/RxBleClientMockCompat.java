package com.polidea.rxandroidble.mockrxandroidble;

import android.support.annotation.Nullable;

import com.polidea.rxandroidble.RxBleScanResult;
import com.polidea.rxandroidble.internal.util.RxBleScannerCompat;

import java.util.UUID;

import rx.Observable;

public class RxBleClientMockCompat extends RxBleClientMock {

    private final RxBleScannerCompat scannerCompat;

    private RxBleClientMockCompat(Builder builder) {
        super(builder);
        this.scannerCompat = builder.scanner;
    }

    public class Builder extends RxBleClientMock.Builder{

        private RxBleScannerCompat scanner;

        public Builder setScanner(RxBleScannerCompat scanner){
            this.scanner = scanner;
            return this;
        }
    }

}
