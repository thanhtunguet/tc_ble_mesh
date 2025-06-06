/********************************************************************************************************
 * @file GattOtaParameters.java
 *
 * @brief for TLSR chips
 *
 * @author telink
 * @date Sep. 30, 2017
 *
 * @par Copyright (c) 2017, Telink Semiconductor (Shanghai) Co., Ltd. ("TELINK")
 *
 *          Licensed under the Apache License, Version 2.0 (the "License");
 *          you may not use this file except in compliance with the License.
 *          You may obtain a copy of the License at
 *
 *              http://www.apache.org/licenses/LICENSE-2.0
 *
 *          Unless required by applicable law or agreed to in writing, software
 *          distributed under the License is distributed on an "AS IS" BASIS,
 *          WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *          See the License for the specific language governing permissions and
 *          limitations under the License.
 *******************************************************************************************************/
package com.telink.ble.mesh.foundation.parameter;

import com.telink.ble.mesh.entity.ConnectionFilter;

/**
 * This class represents the parameters for GATT OTA (Over-The-Air) functionality.
 * It extends the Parameters class.
 */
public class GattOtaParameters extends Parameters {

    /**
     * Constructs a new GattOtaParameters object with the specified connection filter and firmware.
     *
     * @param filter   The connection filter to be used.
     * @param firmware The firmware to be used for OTA.
     */
    public GattOtaParameters(ConnectionFilter filter, byte[] firmware) {
        this.set(COMMON_PROXY_FILTER_INIT_NEEDED, true);
        this.set(ACTION_CONNECTION_FILTER, filter);
        this.setFirmware(firmware);
    }

    /**
     * Sets the firmware for OTA.
     *
     * @param firmware The firmware to be used for OTA.
     */
    public void setFirmware(byte[] firmware) {
        this.set(ACTION_OTA_FIRMWARE, firmware);
    }
}