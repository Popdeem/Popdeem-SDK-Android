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

import com.popdeem.sdk.core.model.PDReward;

/**
 * Util class with convenience methods for Abra Insights Layer.
 * <p>
 * Created by mikenolan on 21/09/2016.
 */

public class PDAbraUtils {

    public static String keyForRewardType(String type) {
        if (type == null || type.isEmpty() || type.equalsIgnoreCase(PDReward.PD_REWARD_TYPE_COUPON)) {
            return PDAbraConfig.ABRA_PROPERTYVALUE_REWARD_TYPE_COUPON;
        } else if (type.equalsIgnoreCase(PDReward.PD_REWARD_TYPE_SWEEPSTAKE)) {
            return PDAbraConfig.ABRA_PROPERTYVALUE_REWARD_TYPE_SWEEPSTAKE;
        } else {
            return PDAbraConfig.ABRA_PROPERTYVALUE_REWARD_TYPE_COUPON;
        }
    }

    public static String keyForRewardAction(String action) {
        if (action == null || action.isEmpty() || action.equalsIgnoreCase(PDReward.PD_REWARD_ACTION_CHECKIN)) {
            return PDAbraConfig.ABRA_PROPERTYVALUE_REWARD_ACTION_CHECKIN;
        } else if (action.equalsIgnoreCase(PDReward.PD_REWARD_ACTION_PHOTO)) {
            return PDAbraConfig.ABRA_PROPERTYVALUE_REWARD_ACTION_PHOTO;
        } else {
            return PDAbraConfig.ABRA_PROPERTYVALUE_REWARD_ACTION_CHECKIN;
        }
    }

}
