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

import android.net.Uri;

/**
 * Created by mikenolan on 06/05/16.
 */
public class PDReferralUtils {

    public static int getRequestIdFromUrl(Uri targetUri) {
        if (targetUri == null || targetUri.toString().isEmpty()) {
            return -1;
        }

        String[] parts = targetUri.toString().split("/");
        for (int i = 0, count = parts.length; i < count; i++) {
            String part = parts[i];
            if (part.equalsIgnoreCase("requests") && (i + 1 < count)) {
                String splitFromParams = parts[i + 1].split("\\?", 2)[0];
                return PDNumberUtils.toInt(splitFromParams, -1);
            }
        }
        return -1;
    }

}
