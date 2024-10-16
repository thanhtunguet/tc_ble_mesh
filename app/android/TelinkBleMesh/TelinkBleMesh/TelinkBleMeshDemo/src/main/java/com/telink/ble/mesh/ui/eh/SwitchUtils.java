/********************************************************************************************************
 * @file MeshCommandUtil.java
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
package com.telink.ble.mesh.ui.eh;

import com.telink.ble.mesh.core.MeshUtils;
import com.telink.ble.mesh.model.NodeInfo;
import com.telink.ble.mesh.util.Arrays;
import com.telink.ble.mesh.util.MeshLogger;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kee on 2018/4/28.
 */

public class SwitchUtils {

    public static final int PRODUCT_UUID_SWITCH = 0xE215;

    public static final int SWITCH_ACTION_ON_OFF = 0x00;

    public static final int SWITCH_ACTION_LIGHTNESS = 0x01;

    public static final int SWITCH_ACTION_CT = 0x02;

    public static final int SWITCH_ACTION_SCENE_RECALL = 0x03;


    public static final int SWITCH_STATE_UNPAIRED = 0x00;

    public static final int SWITCH_STATE_PAIR_COMPLETE = 0x01;

    public static final int SWITCH_STATE_PUBLISH_SET_COMPLETE = 0x02;

    /**
     * sub opcodes
     */
    public static final byte SUB_OP_PAIR_MAC = 0x00;

    public static final byte SUB_OP_PAIR_KEY_LOW = 0x01;

    public static final byte SUB_OP_PAIR_KEY_HIGH = 0x02;

    public static final byte SUB_OP_PUB_GENERIC = 0x03;

    public static final byte SUB_OP_PUB_ON_OFF = 0x04;

    public static final byte SUB_OP_PUB_DELTA_LIGHTNESS = 0x05;

    public static final byte SUB_OP_PUB_DELTA_CT = 0x06;

    public static final byte SUB_OP_PUB_SCENE_RECALL = 0x07;


    public static final byte SUB_OP_PAIR_CONFIRM = 0x10;

    public static final byte SUB_OP_PAIR_DELETE = 0x11;

    public static final byte OP_LGT_CMD_LOAD_SCENE = 0x2f;


    public static boolean isSwitch(NodeInfo light) {
        if (light.macAddress == null) return false;
        int pid = MeshUtils.bytes2Integer(Arrays.hexToBytes(light.macAddress.replace(":", "")), 0, 2, ByteOrder.BIG_ENDIAN);
        return pid == PRODUCT_UUID_SWITCH;
    }

    /**
     * light#temperature is not used
     *
     * @param light
     * @return
     */
    public static boolean isSwitchAndFromNfc(NodeInfo light) {
        return isSwitch(light) && light.isFromNfc;
    }

    public static NodeInfo convert(SwitchDevice device, int meshAddress) {
        NodeInfo light = new NodeInfo();
        String mac = device.sourceAddress;
        mac = mac.replace(":", "");
        int productUUID = Integer.valueOf(mac.substring(0, 4), 16);
        light.name = "Enocean Switch";
        light.macAddress = device.sourceAddress;
        light.meshAddress = meshAddress;
        light.deviceUUID = ByteBuffer.allocate(16).put(Arrays.hexToBytes(mac)).array();
        light.deviceKey = device.securityKey;
        light.bound = true;
        light.compositionData = null;
        light.elementCnt = 1;
        return light;
    }


    /**
     * default : 2 actions
     * action 0 : on off
     * action 1 : lightness delta(+-20)
     */
    public static List<SwitchAction> getDefaultActions() {
        List<SwitchAction> defaultActions = new ArrayList<>();
        SwitchAction action0 = new SwitchAction();
        action0.action = SwitchUtils.SWITCH_ACTION_ON_OFF;
        action0.value = 1;
        action0.keyIndex = 0;
        action0.keyCount = 2;
        action0.publishAddress = 0xFFFF;
        defaultActions.add(action0);

        SwitchAction action1 = new SwitchAction();
        action1.action = SwitchUtils.SWITCH_ACTION_LIGHTNESS;
        action1.value = 20;
        action1.keyIndex = 2;
        action1.keyCount = 2;
        action1.publishAddress = 0xFFFF;
        defaultActions.add(action1);
        return defaultActions;
    }

    /**
     * 3 actions
     * 12|3|4
     *
     * @return
     */
    public static List<SwitchAction> getActions1() {
        List<SwitchAction> defaultActions = new ArrayList<>();
        {
            SwitchAction action0 = new SwitchAction();
            action0.action = SwitchUtils.SWITCH_ACTION_ON_OFF;
            action0.value = 1;
            action0.keyIndex = 0;
            action0.keyCount = 2;
            action0.publishAddress = 0xFFFF;
            defaultActions.add(action0);
        }

        {
            SwitchAction action2 = new SwitchAction();
            action2.action = SwitchUtils.SWITCH_ACTION_LIGHTNESS;
            action2.value = 20;
            action2.keyIndex = 2;
            action2.keyCount = 1;
            action2.publishAddress = 0xFFFF;
            defaultActions.add(action2);
        }

        {
            SwitchAction action3 = new SwitchAction();
            action3.action = SwitchUtils.SWITCH_ACTION_LIGHTNESS;
            action3.value = -20;
            action3.keyIndex = 3;
            action3.keyCount = 1;
            action3.publishAddress = 0xFFFF;
            defaultActions.add(action3);
        }
        return defaultActions;
    }


    /**
     * 3 actions
     * 1|2|34
     *
     * @return
     */
    public static List<SwitchAction> getActions2() {
        List<SwitchAction> defaultActions = new ArrayList<>();
        {
            SwitchAction action0 = new SwitchAction();
            action0.action = SwitchUtils.SWITCH_ACTION_ON_OFF;
            action0.value = 1;
            action0.keyIndex = 0;
            action0.keyCount = 1;
            action0.publishAddress = 0xFFFF;
            defaultActions.add(action0);
        }

        {
            SwitchAction action1 = new SwitchAction();
            action1.action = SwitchUtils.SWITCH_ACTION_ON_OFF;
            action1.value = 0;
            action1.keyIndex = 1;
            action1.keyCount = 1;
            action1.publishAddress = 0xFFFF;
            defaultActions.add(action1);
        }

        {
            SwitchAction action2 = new SwitchAction();
            action2.action = SwitchUtils.SWITCH_ACTION_LIGHTNESS;
            action2.value = 20;
            action2.keyIndex = 2;
            action2.keyCount = 2;
            action2.publishAddress = 0xFFFF;
            defaultActions.add(action2);
        }

        return defaultActions;
    }

    /**
     * 4 actions
     *
     * @return
     */
    public static List<SwitchAction> getSingleActions() {
        List<SwitchAction> defaultActions = new ArrayList<>();
        {
            SwitchAction action0 = new SwitchAction();
            action0.action = SwitchUtils.SWITCH_ACTION_ON_OFF;
            action0.value = 1;
            action0.keyIndex = 0;
            action0.keyCount = 1;
            action0.publishAddress = 0xFFFF;
            defaultActions.add(action0);
        }

        {
            SwitchAction action1 = new SwitchAction();
            action1.action = SwitchUtils.SWITCH_ACTION_ON_OFF;
            action1.value = 0;
            action1.keyIndex = 1;
            action1.keyCount = 1;
            action1.publishAddress = 0xFFFF;
            defaultActions.add(action1);
        }


        {
            SwitchAction action2 = new SwitchAction();
            action2.action = SwitchUtils.SWITCH_ACTION_LIGHTNESS;
            action2.value = 20;
            action2.keyIndex = 2;
            action2.keyCount = 1;
            action2.publishAddress = 0xFFFF;
            defaultActions.add(action2);
        }

        {
            SwitchAction action3 = new SwitchAction();
            action3.action = SwitchUtils.SWITCH_ACTION_LIGHTNESS;
            action3.value = -20;
            action3.keyIndex = 3;
            action3.keyCount = 1;
            action3.publishAddress = 0xFFFF;
            defaultActions.add(action3);
        }
        return defaultActions;
    }

    public static final int PAIR_CMD_MAC = 0x01;

    public static final int PAIR_CMD_KEY_L = 0x02;

    public static final int PAIR_CMD_KEY_H = 0x03;


    public static byte getPublishSubOp(SwitchAction action) {
        switch (action.action) {
            case SWITCH_ACTION_ON_OFF:
                return SUB_OP_PUB_ON_OFF;
            case SWITCH_ACTION_LIGHTNESS:
                return SUB_OP_PUB_DELTA_LIGHTNESS;
            case SWITCH_ACTION_CT:
                return SUB_OP_PUB_DELTA_CT;
            case SWITCH_ACTION_SCENE_RECALL:
                return OP_LGT_CMD_LOAD_SCENE;
        }
        return 0;
    }

    /**
     * 3 bytes
     *
     * @param action
     * @return
     */
    public static byte[] getGenericPubParams(SwitchAction action) {
        switch (action.action) {
            case SWITCH_ACTION_ON_OFF:
                return new byte[]{(byte) action.value};
            case SWITCH_ACTION_LIGHTNESS:
//                return new byte[]{SUB_OP_PUB_DELTA_LIGHTNESS, (byte) action.value};
                return new byte[]{0x00, (byte) action.value, 0x00};
            case SWITCH_ACTION_CT:
                return new byte[]{0x00, (byte) action.value, 0x00};
            case SWITCH_ACTION_SCENE_RECALL:
                return new byte[]{(byte) action.value};
        }
        return new byte[]{0x00};
    }

    public static byte getGenericOp(SwitchAction action) {
        switch (action.action) {
            case SWITCH_ACTION_ON_OFF:
                return 0x10;

            case SWITCH_ACTION_LIGHTNESS:
                return 0x0C;

            case SWITCH_ACTION_CT:
                return 0x0D;

            case SWITCH_ACTION_SCENE_RECALL:
                return OP_LGT_CMD_LOAD_SCENE;
        }
        return 0;
    }


    public static byte[] genSpecialCmd(int address, byte seg, SwitchAction action) {
        byte subOp = SwitchUtils.getPublishSubOp(action);
        // 2 bit
        int keyPairEn = 0;
        short pubAddress0 = 0;
        short pubAddress1 = 0;
        if (action.keyIndex == 0) {
            keyPairEn = 1;
            pubAddress0 = (short) action.publishAddress;
        } else if (action.keyIndex == 2) {
            keyPairEn = 2;
            pubAddress1 = (short) action.publishAddress;
        }


//        int keyOffset = action.keyIndex;
        int keyOffset = 0;
        byte keyPar = (byte) (keyPairEn | (keyOffset << 4));

        byte value = (byte) action.value;

        byte[] cmdParams = ByteBuffer.allocate(10).order(ByteOrder.LITTLE_ENDIAN)
                .put(seg)
                .put(subOp)
                .putShort((short) address)
                .put(keyPar)
                .putShort(pubAddress0)
                .putShort(pubAddress1)
                .put(value)
                .array();
        MeshLogger.d("params - EH_PairPub : " + Arrays.bytesToHexString(cmdParams, ""));
        return cmdParams;
    }

    public static byte[] genGenericCmd(int address, byte seg, SwitchAction action) {
        byte subOp = SwitchUtils.SUB_OP_PUB_GENERIC;
        byte b0 = (byte) ((subOp & 0x0F) | ((action.keyIndex & 0x0F) << 4));
        byte[] params = SwitchUtils.getGenericPubParams(action);
        byte op = SwitchUtils.getGenericOp(action);
        byte[] cmdParams = ByteBuffer.allocate(10).order(ByteOrder.LITTLE_ENDIAN)
                .put(seg)
                .put(b0)
                .putShort((short) address)
                .putShort((short) action.publishAddress)
                .put(op)
                .put(params)
                .array();
        MeshLogger.d("params - gen generic cmd : " + Arrays.bytesToHexString(cmdParams, ""));
        return cmdParams;
    }
}
