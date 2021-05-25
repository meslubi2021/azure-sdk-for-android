// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.core.util;

/**
 * A generic interface for sending service requests using the provided service version.
 */
public interface ServiceVersion {
    /**
     * Gets the string representation of the {@link ServiceVersion}
     *
     * @return the string representation of the {@link ServiceVersion}
     */
    String getVersion();
}
