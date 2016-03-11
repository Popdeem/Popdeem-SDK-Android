/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Popdeem
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.popdeem.sdk.core.utils;

/**
 * Created by mikenolan on 15/02/16.
 */
public class PDNumberUtils {

    /**
     * Convert string to double
     *
     * @param string {@link String} value to convert
     * @return Converted double value or 0.0 if conversion fails
     */
    public static double toDouble(String string) {
        return toDouble(string, 0.0);
    }

    /**
     * Convert string to double
     *
     * @param string       {@link String} value to convert
     * @param defaultValue default value if conversion fails
     * @return Converted double value or defaultValue if conversion fails
     */
    public static double toDouble(String string, double defaultValue) {
        if (string == null) {
            return defaultValue;
        }

        try {
            return Double.valueOf(string);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Convert string to float
     *
     * @param string {@link String} value to convert
     * @return Converted float value or 0.0f if conversion fails
     */
    public static float toFloat(String string) {
        return toFloat(string, 0.0f);
    }

    /**
     * Convert string to float
     *
     * @param string       {@link String} value to convert
     * @param defaultValue default value if conversion fails
     * @return Converted float value or defaultValue if conversion fails
     */
    public static float toFloat(String string, float defaultValue) {
        if (string == null) {
            return defaultValue;
        }

        try {
            return Float.valueOf(string);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Convert string to long
     *
     * @param string {@link String} value to convert
     * @return Converted long value or 0 if conversion fails
     */
    public static long toLong(String string) {
        return toLong(string, 0);
    }

    /**
     * Convert string to long
     *
     * @param string       {@link String} value to convert
     * @param defaultValue default value if conversion fails
     * @return Converted long value or defaultValue if conversion fails
     */
    public static long toLong(String string, long defaultValue) {
        if (string == null) {
            return defaultValue;
        }

        try {
            return Long.valueOf(string);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }


    /**
     * Convert string to int
     *
     * @param string {@link String} value to convert
     * @return Converted int value or 0 if conversion fails
     */
    public static int toInt(String string) {
        return toInt(string, 0);
    }

    /**
     * Convert string to int
     *
     * @param string       {@link String} value to convert
     * @param defaultValue default value if conversion fails
     * @return Converted int value or defaultValue if conversion fails
     */
    public static int toInt(String string, int defaultValue) {
        if (string == null) {
            return defaultValue;
        }

        try {
            return Integer.valueOf(string);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

}
