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

package com.popdeem.sdk.core.api.abra;

import android.support.v4.util.Pair;

import java.util.ArrayList;

/**
 * Created by mikenolan on 22/09/2016.
 */

public class PDAbraProperties {

    private ArrayList<Pair<String, String>> mProperties;

    private PDAbraProperties(ArrayList<Pair<String, String>> properties) {
        this.mProperties = properties;
    }

    ArrayList<Pair<String, String>> getProperties() {
        return mProperties;
    }


    /**
     * Builder class for Abra Insights properties
     */
    public static class Builder {

        private ArrayList<Pair<String, String>> mProperties;

        public Builder() {
            this.mProperties = new ArrayList<>();
        }

        public Builder add(String propertyName, String propertyValue) {
            this.mProperties.add(Pair.create(propertyName, propertyValue));
            return this;
        }

        public PDAbraProperties create() {
            return new PDAbraProperties(this.mProperties);
        }
    }

}
