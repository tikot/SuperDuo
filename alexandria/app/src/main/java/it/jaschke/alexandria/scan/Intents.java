/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.jaschke.alexandria.scan;

/**
 * This class provides the constants to use when sending an Intent to Barcode Scanner.
 * These strings are effectively API and cannot be changed.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class Intents {

    public static final class Scan {

        /**
         * If a barcode is found, Barcodes returns {@link android.app.Activity#RESULT_OK} to
         * {@link android.app.Activity#onActivityResult(int, int, android.content.Intent)}
         * of the app which requested the scan via
         * {@link android.app.Activity#startActivityForResult(android.content.Intent, int)}
         * The barcodes contents can be retrieved with
         * {@link android.content.Intent#getStringExtra(String)}.
         * If the user presses Back, the result code will be {@link android.app.Activity#RESULT_CANCELED}.
         */
        public static final String RESULT = "SCAN_RESULT";

        /**
         * Call {@link android.content.Intent#getStringExtra(String)} with {@link #RESULT_FORMAT}
         * to determine which barcode format was found.
         * See {@link com.google.zxing.BarcodeFormat} for possible values.
         */
        public static final String RESULT_FORMAT = "SCAN_RESULT_FORMAT";

        /**
         * Call {@link android.content.Intent#getStringExtra(String)} with {@link #RESULT_UPC_EAN_EXTENSION}
         * to return the content of any UPC extension barcode that was also found. Only applicable
         * to {@link com.google.zxing.BarcodeFormat#UPC_A} and {@link com.google.zxing.BarcodeFormat#EAN_13}
         * formats.
         */
        public static final String RESULT_UPC_EAN_EXTENSION = "SCAN_RESULT_UPC_EAN_EXTENSION";

        /**
         * Call {@link android.content.Intent#getByteArrayExtra(String)} with {@link #RESULT_BYTES}
         * to get a {@code byte[]} of raw bytes in the barcode, if available.
         */
        public static final String RESULT_BYTES = "SCAN_RESULT_BYTES";

        /**
         * Key for the value of {@link com.google.zxing.ResultMetadataType#ORIENTATION}, if available.
         * Call {@link android.content.Intent#getIntArrayExtra(String)} with {@link #RESULT_ORIENTATION}.
         */
        public static final String RESULT_ORIENTATION = "SCAN_RESULT_ORIENTATION";

        /**
         * Key for the value of {@link com.google.zxing.ResultMetadataType#ERROR_CORRECTION_LEVEL}, if available.
         * Call {@link android.content.Intent#getStringExtra(String)} with {@link #RESULT_ERROR_CORRECTION_LEVEL}.
         */
        public static final String RESULT_ERROR_CORRECTION_LEVEL = "SCAN_RESULT_ERROR_CORRECTION_LEVEL";

        /**
         * Prefix for keys that map to the values of {@link com.google.zxing.ResultMetadataType#BYTE_SEGMENTS},
         * if available. The actual values will be set under a series of keys formed by adding 0, 1, 2, ...
         * to this prefix. So the first byte segment is under key "SCAN_RESULT_BYTE_SEGMENTS_0" for example.
         * Call {@link android.content.Intent#getByteArrayExtra(String)} with these keys.
         */
        public static final String RESULT_BYTE_SEGMENTS_PREFIX = "SCAN_RESULT_BYTE_SEGMENTS_";

    }
}
