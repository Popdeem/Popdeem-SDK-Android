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

package com.popdeem.sdk.core.comparator;

import android.support.annotation.IntDef;

import com.popdeem.sdk.core.model.PDReward;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Comparator;

/**
 * Created by mikenolan on 11/03/16.
 */
public class PDRewardComparator implements Comparator<PDReward> {

    public static final int DISTANCE_COMPARATOR = 0;
    public static final int CREATED_AT_COMPARATOR = 1;
    public static final int CLAIMED_AT_COMPARATOR = 2;

    @IntDef({DISTANCE_COMPARATOR, CREATED_AT_COMPARATOR, CLAIMED_AT_COMPARATOR})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ComparatorType {
    }

    @PDRewardComparator.ComparatorType
    private final int mType;


    public PDRewardComparator(@PDRewardComparator.ComparatorType int type) {
        this.mType = type;
    }

    @Override
    public int compare(PDReward lhs, PDReward rhs) {
        if (mType == DISTANCE_COMPARATOR) {
            return compareDistance(lhs, rhs);
        } else if (mType == CLAIMED_AT_COMPARATOR) {
            return compareClaimedAt(lhs, rhs);
        } else {
            return compareCreatedAt(lhs, rhs);
        }
    }


    /**
     * Compare {@link PDReward} by distance
     *
     * @param lhs Left reward
     * @param rhs Right reward
     * @return int
     */
    private int compareDistance(PDReward lhs, PDReward rhs) {
        // Check if Location Verification is disabled
        if (lhs.getDisableLocationVerification().equalsIgnoreCase(PDReward.PD_TRUE)) {
            return -1;
        } else if (rhs.getDisableLocationVerification().equalsIgnoreCase(PDReward.PD_TRUE)) {
            return 1;
        }

        // Check if reward has a distance
        if (lhs.getDistanceFromUser() <= 0) {
            return 1;
        } else if (rhs.getDistanceFromUser() <= 0) {
            return -1;
        }

        // Then compare the distances
        return (int) (lhs.getDistanceFromUser() - rhs.getDistanceFromUser());
    }


    /**
     * Compare {@link PDReward} by created_at
     *
     * @param lhs Left reward
     * @param rhs Right reward
     * @return int
     */
    private int compareCreatedAt(PDReward lhs, PDReward rhs) {
        // Check if time is not 0
        if (rhs.getCreatedAt() <= 0) {
            return 1;
        } else if (lhs.getCreatedAt() <= 0) {
            return -1;
        }
        return (int) (rhs.getCreatedAt() - lhs.getCreatedAt());
    }


    /**
     * Compare {@link PDReward} by claimed_at
     *
     * @param lhs Left reward
     * @param rhs Right reward
     * @return int
     */
    private int compareClaimedAt(PDReward lhs, PDReward rhs) {
        // Check if time is not 0
        if (rhs.getClaimedAt() <= 0) {
            return 1;
        } else if (lhs.getClaimedAt() <= 0) {
            return -1;
        }
        return (int) (rhs.getClaimedAt() - lhs.getClaimedAt());
    }
}
