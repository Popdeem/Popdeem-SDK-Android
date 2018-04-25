package com.popdeem.sdk.core.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

/**
 * Created by Dave Hannan on 15/12/2016.
 * Project: Popdeem-SDK-Android
 * A small class to help with permissions in the case of client apps conflicting with the SDK
 */

public class PDPermissionHelper {

    private static String TAG = PDPermissionHelper.class.getSimpleName();

    /**
     * Method to check manifest to see if permission exists - assuming manifests merge on build
     * */
    public static boolean hasPermissionInManifest(Context context, String permissionName) {
        final String packageName = context.getPackageName();
        try {
            final PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
            final String[] declaredPermissions = packageInfo.requestedPermissions;
            if (declaredPermissions != null && declaredPermissions.length > 0) {
                for (String p : declaredPermissions) {
                    if (p.equals(permissionName)) {
                        return true;
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "hasPermissionInManifest: ", e);
        }
        return false;
    }
}
