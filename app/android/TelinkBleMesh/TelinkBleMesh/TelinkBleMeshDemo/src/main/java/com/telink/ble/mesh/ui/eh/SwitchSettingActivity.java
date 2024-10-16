/********************************************************************************************************
 * @file DeviceScanningActivity.java
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

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.ArrayMap;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.telink.ble.mesh.TelinkMeshApplication;
import com.telink.ble.mesh.demo.R;
import com.telink.ble.mesh.foundation.Event;
import com.telink.ble.mesh.foundation.EventListener;
import com.telink.ble.mesh.model.MeshInfo;
import com.telink.ble.mesh.model.NodeInfo;
import com.telink.ble.mesh.ui.BaseActivity;
import com.telink.ble.mesh.util.Arrays;
import com.telink.ble.mesh.util.MeshLogger;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * set enocean device (switch)
 */
public final class SwitchSettingActivity extends BaseActivity implements EventListener<String> {

    /**
     * set key 0 and key 1
     */
    public static final int REQUEST_CODE_SET_KEY_0_1 = 1;

    /**
     * set key 0 and key 1
     */
    public static final int REQUEST_CODE_SET_KEY_2_3 = 2;

    /**
     * EnOcean pair response
     */
    public static final byte EH_PAIR_ST_SUCCESS = 0;

    public static final byte EH_PAIR_ST_MISSING_PKT = 1;

    public static final byte EH_PAIR_ST_AUTH_FAILED = 2;

    public static final byte EH_PAIR_ST_UNICAST_ADDR_OCCUPIED = 3;

    public static final byte EH_PAIR_ST_INSUFFICIENT_RES = 4;

    public static final byte EH_PAIR_NOT_ENOUGH_INFO = 5;

    public static final byte EH_PAIR_PUB_INVALID_ADDRESS = 6;

    public static final byte EH_PAIR_PUB_INSUFFICIENT_RES = 7;

    private NodeInfo switchLight;

    private MeshInfo mesh;

    private TextView tv_action_key_0_1, tv_action_key_2_3;

    private List<NodeInfo> lights;

    private TelinkMeshApplication mApplication;

    private Handler mHandler = new Handler();

    private SelectNodeInSwitchAdapter adapter;

    private SwitchActionAdapter actionAdapter;
    private CheckBox cb_all;
    private Switch sw_merge_act;
    private View ll_all;

    /**
     * 0(default) : 12 | 34
     * 1: 12 | 3 | 4
     * 2: 1 | 2 | 34
     * 3: 1 | 2 | 3 | 4
     */
    private int switchActionMode;

    private RadioButton rb_mode0, rb_mode1, rb_mode2, rb_mode3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.activity_switch_setting);
        setTitle("EnOcean Setting");
        enableBackNav(true);

        this.mApplication = TelinkMeshApplication.getInstance();
        mesh = this.mApplication.getMeshInfo();
//        this.mApplication.addEventListener(NotificationEvent.GET_DEVICE_STATE, this);
//        this.mApplication.addEventListener(NotificationEvent.ONLINE_STATUS, this);
//        this.mApplication.addEventListener(DeviceEvent.STATUS_CHANGED, this);
        initData();
        initView();
    }

    private void initData() {
        int address = getIntent().getIntExtra("meshAddress", 0);
        switchLight = mesh.getDeviceByMeshAddress(address);
        lights = new ArrayList<>();
        for (NodeInfo light : mesh.nodes) {
            if (SwitchUtils.isSwitch(light)) {
                continue;
            }
            light.selected = switchLight.isLightPubSet(light);
            lights.add(light);
        }
    }

    private void initView() {
        TextView tv_switch_info = findViewById(R.id.tv_switch_info);
        tv_switch_info.setText(getFormatSwitchInfo());
        RecyclerView rv_devices = findViewById(R.id.rv_devices);
        rv_devices.setLayoutManager(new LinearLayoutManager(this));

        adapter = new SelectNodeInSwitchAdapter(this, lights);
//        adapter.setOnItemClickListener(position -> adapter.changeSelection(position));// this::setSwitch
        rv_devices.setAdapter(adapter);

        findViewById(R.id.view_key_0_1).setOnClickListener(
                v -> startActivityForResult(new Intent(SwitchSettingActivity.this, SwitchActionSettingActivity.class).putExtra(SwitchActionSettingActivity.EXTRA_ACTION, switchLight.switchActions.get(0)), REQUEST_CODE_SET_KEY_0_1));
        findViewById(R.id.view_key_2_3).setOnClickListener(
                v -> startActivityForResult(new Intent(SwitchSettingActivity.this, SwitchActionSettingActivity.class).putExtra(SwitchActionSettingActivity.EXTRA_ACTION, switchLight.switchActions.get(1)), REQUEST_CODE_SET_KEY_2_3));

        tv_action_key_0_1 = findViewById(R.id.tv_action_key_0_1);
        tv_action_key_2_3 = findViewById(R.id.tv_action_key_2_3);


        findViewById(R.id.btn_kick).setOnClickListener(v ->
                showConfirmDialog("kick out EnOcean switch?", (dialog, which) -> kickOut()));


        Toolbar toolbar = findViewById(R.id.title_bar);
        toolbar.inflateMenu(R.menu.switch_setting);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.item_save) {
                save();
            }
            return false;
        });

        cb_all = findViewById(R.id.cb_all);
        ll_all = findViewById(R.id.ll_all);
        ll_all.setOnClickListener(v -> {
            boolean allSelected = adapter.allSelected();
            adapter.selectAll(!allSelected);
            cb_all.setChecked(!allSelected);
//            adapter.selectAll(!adapter.allSelected());
        });
        adapter.selectAll(true);
        cb_all.setChecked(true);

        if (SwitchUtils.isSwitchAndFromNfc(switchLight)) {
            findViewById(R.id.view_nfc_setting).setVisibility(View.VISIBLE);
            findViewById(R.id.view_nfc_setting).setOnClickListener(v -> startActivity(new Intent(this, SwitchNfcSettingActivity.class).putExtra("meshAddress", switchLight.meshAddress)));
        } else {
            findViewById(R.id.view_nfc_setting).setVisibility(View.GONE);
        }

        sw_merge_act = findViewById(R.id.sw_merge_act);
        sw_merge_act.setChecked(switchLight.switchActions.size() == 2);
        sw_merge_act.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                switchLight.switchActions.clear();
                switchLight.switchActions.addAll(SwitchUtils.getDefaultActions());
            } else {
                switchLight.switchActions.clear();
                switchLight.switchActions.addAll(SwitchUtils.getSingleActions());
            }
            updateUI();
            mesh.saveOrUpdate();
        });

        actionAdapter = new SwitchActionAdapter(this);
        RecyclerView rv_actions = findViewById(R.id.rv_actions);
        rv_actions.setLayoutManager(new LinearLayoutManager(this));
        rv_actions.setAdapter(actionAdapter);
        updateUI();

        findViewById(R.id.ll_mode0).setOnClickListener(v -> showModeWarningDialog(0));
        findViewById(R.id.ll_mode1).setOnClickListener(v -> showModeWarningDialog(1));
        findViewById(R.id.ll_mode2).setOnClickListener(v -> showModeWarningDialog(2));
        findViewById(R.id.ll_mode3).setOnClickListener(v -> showModeWarningDialog(3));

        rb_mode0 = findViewById(R.id.rb_mode0);
        rb_mode1 = findViewById(R.id.rb_mode1);
        rb_mode2 = findViewById(R.id.rb_mode2);
        rb_mode3 = findViewById(R.id.rb_mode3);
        initActionMode();
    }

    private void initActionMode() {

        if (switchLight.switchActions.size() == 2) {
            switchActionMode = 0;
        } else if (switchLight.switchActions.size() == 3) {
            SwitchAction action = switchLight.switchActions.get(1);
            if (action.action == SwitchUtils.SWITCH_ACTION_LIGHTNESS) {
                switchActionMode = 1;
            } else {
                switchActionMode = 2;
            }
        } else if (switchLight.switchActions.size() == 4) {
            switchActionMode = 3;
        }

        rb_mode0.setChecked(switchActionMode == 0);
        rb_mode1.setChecked(switchActionMode == 1);
        rb_mode2.setChecked(switchActionMode == 2);
        rb_mode3.setChecked(switchActionMode == 3);
    }

    private void showModeWarningDialog(int mode) {
        if (switchActionMode == mode) {
            return;
        }
        showConfirmDialog("Warning! Change the key layout will make the data lost", (dialog, which) -> changeActionMode(mode));
    }

    private void changeActionMode(int mode) {
        this.switchActionMode = mode;
        rb_mode0.setChecked(mode == 0);
        rb_mode1.setChecked(mode == 1);
        rb_mode2.setChecked(mode == 2);
        rb_mode3.setChecked(mode == 3);

        switchLight.switchActions.clear();
        switch (mode) {
            case 0:
                switchLight.switchActions.addAll(SwitchUtils.getDefaultActions());
                break;
            case 1:
                switchLight.switchActions.addAll(SwitchUtils.getActions1());
                break;
            case 2:
                switchLight.switchActions.addAll(SwitchUtils.getActions2());
                break;
            case 3:
                switchLight.switchActions.addAll(SwitchUtils.getSingleActions());
                break;
        }
        updateUI();
        mesh.saveOrUpdate();
    }


    public void updateAllSelectState() {
        cb_all.setChecked(adapter.allSelected());
    }


    private void kickOut() {
        removeAll();
        TelinkMeshApplication.getInstance().getMeshInfo().removeNode(switchLight);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK || data == null) return;
        SwitchAction action = (SwitchAction) data.getSerializableExtra(SwitchActionSettingActivity.EXTRA_ACTION);
        if (action == null) {
            return;
        }
        saveAction(action);
        updateUI();
    }

    private void saveAction(SwitchAction action) {
        MeshLogger.d("save action : " + action.keyIndex + " -- " + action.keyCount);
        SwitchAction switchAction;
        for (int i = 0; i < switchLight.switchActions.size(); i++) {
            switchAction = switchLight.switchActions.get(i);
            if (switchAction.keyIndex == action.keyIndex) {
                switchLight.switchActions.set(i, action);
                break;
            }
        }
        mesh.saveOrUpdate();
    }

    private void updateUI() {
        actionAdapter.resetData(switchLight.switchActions);
    }

    /**********************************************************************************************
     * setting commands
     **********************************************************************************************/

    private HashSet<Integer> receivedRsp = new HashSet<>();
    int onlineCount = 0;


    //    private Light settingLight;
    // add pair and pub
    private static final int SETTING_OP_ADD_PAIR_PUB = 1;

    // add publish only
    private static final int SETTING_OP_ADD_PUB = 2;

    // delete publish only
//    private static final int SETTING_OP_DEL_PUB = 3;

    // delete pair
    private static final int SETTING_OP_DEL_PAIR = 4;
    private int settingOp = 0;


    private void save() {
        if (lights.size() == 0) {
            return;
        }
        final boolean allSelected = adapter.allSelected();
        receivedRsp.clear();
        onlineCount = mesh.getOnlineCountInAll();
        if (onlineCount == 0) {
            toastMsg("no device online");
            return;
        }
        mHandler.removeCallbacks(SET_SWITCH_TIMEOUT_TASK);
        mHandler.postDelayed(SET_SWITCH_TIMEOUT_TASK, 5 * 1000);
        showWaitingDialog("setting switch...");
        if (allSelected) {
            addAll();
        } else {
            settingOp = SETTING_OP_DEL_PAIR;
            removeAll();
        }
    }


    private Runnable SET_SWITCH_TIMEOUT_TASK = () -> {
        MeshLogger.d("set switch timeout");
        onSaveComplete(false);
    };

    private void onSaveComplete(boolean success) {
        MeshLogger.d("onSaveComplete : " + success);
        runOnUiThread(() -> {
            dismissWaitingDialog();
            if (success) {
                toastMsg("save successful");
            } else {
                toastMsg("save timeout");
            }
        });

        settingOp = 0;
    }

    private void addAll() {
        boolean allPaired = true;
        for (NodeInfo light : lights) {
            if (!switchLight.isLightPaired(light)) {
                allPaired = false;
                break;
            }
        }
        MeshLogger.d("all lights paired ? " + allPaired);
        if (allPaired) {
            setAllPub();
        } else {
            setAllPairAndPub();
        }
    }


    private void setAllPub() {
        MeshLogger.d("set all publish");
        settingOp = SETTING_OP_ADD_PUB;
        int address = 0xFFFF;


        ArrayMap<SwitchAction, Boolean> preparedActions = getPreparedActions();
        int segN = preparedActions.size() - 1;
        int index;
        byte[] paramsAll = new byte[preparedActions.size() * 10];
        byte pubOpcode = (byte) 0xCD;
        for (index = 0; index < preparedActions.size(); index++) {
            int finalI = index;
            mHandler.postDelayed(() -> {
                byte seg = getSeg(segN, finalI);
                byte[] params = EH_PairPub(seg, preparedActions.keyAt(finalI), preparedActions.valueAt(finalI));
                System.arraycopy(params, 0, paramsAll, finalI * 10, params.length);
//                TelinkLightService.Instance().sendCommandNoResponse(pubOpcode, address, params);
            }, index * 320);
        }

        mHandler.postDelayed(() -> {
            byte opcode = (byte) 0xC7;
            int crc = crc16(paramsAll);
            byte[] params = EH_PairConfirm((short) crc);
//            TelinkLightService.Instance().sendCommandNoResponse(opcode, address, params);
        }, index * 320);
    }


    private void setAllPairAndPub() {
        settingOp = SETTING_OP_ADD_PAIR_PUB;
        MeshLogger.d("add all");
        int address = 0xFFFF;
        ArrayMap<SwitchAction, Boolean> preparedActions = getPreparedActions();

        byte[] paramsAll = new byte[(3 + preparedActions.size()) * 10];
        int segN = (3 + preparedActions.size()) - 1;
        int index = 0;
        // set mac
        {
            byte seg = getSeg(segN, 0);
            byte opcode = (byte) 0xCD;
            byte[] params = EH_PairParMac(seg);
            System.arraycopy(params, 0, paramsAll, 0, params.length);
//            TelinkLightService.Instance().sendCommandNoResponse(opcode, address, params);
        }

        index += 1;
        {
            mHandler.postDelayed(() -> {
                byte seg = getSeg(segN, 1);
                byte opcode = (byte) 0xCD;
                byte[] params = EH_PairParSet_L(seg);
                System.arraycopy(params, 0, paramsAll, 10, params.length);
//                TelinkLightService.Instance().sendCommandNoResponse(opcode, address, params);
            }, index * 320);
        }


        index += 1;
        {
            mHandler.postDelayed(() -> {
                byte seg = getSeg(segN, 2);
                byte opcode = (byte) 0xCD;
                byte[] params = EH_PairParSet_H(seg);
                System.arraycopy(params, 0, paramsAll, 20, params.length);
//                TelinkLightService.Instance().sendCommandNoResponse(opcode, address, params);
            }, index * 320);
        }


        byte pubOpcode = (byte) 0xCD;
        for (; index < preparedActions.size() + 3; index++) {
            int finalI = index;
            mHandler.postDelayed(() -> {
                byte seg = getSeg(segN, finalI);
                byte[] params = EH_PairPub(seg, preparedActions.keyAt(finalI), preparedActions.valueAt(finalI));
                System.arraycopy(params, 0, paramsAll, finalI * 10, params.length);
//                TelinkLightService.Instance().sendCommandNoResponse(pubOpcode, address, params);
            }, index * 320);
        }

        /*index += 1;
        {
            mHandler.postDelayed(() -> {
                byte seg = getSeg(segN, 3);
                SwitchAction switchAction = switchLight.switchActions.get(0);
                byte opcode = (byte) 0xCD;
                byte[] params = EH_PairPub(seg, switchAction);
                System.arraycopy(params, 0, paramsAll, 30, params.length);
                TelinkLightService.Instance().sendCommandNoResponse(opcode, address, params);
            }, index * 320);
        }


        index += 1;
        {
            mHandler.postDelayed(() -> {
                byte seg = getSeg(segN, 4);
                SwitchAction switchAction = switchLight.switchActions.get(1);
                byte opcode = (byte) 0xCD;
                byte[] params = EH_PairPub(seg, switchAction);
                System.arraycopy(params, 0, paramsAll, 40, params.length);
                TelinkLightService.Instance().sendCommandNoResponse(opcode, address, params);
            }, index * 320);
        }*/

        {
            mHandler.postDelayed(() -> {
                byte opcode = (byte) 0xC7;
                int crc = crc16(paramsAll);
                byte[] params = EH_PairConfirm((short) crc);
//                TelinkLightService.Instance().sendCommandNoResponse(opcode, address, params);
            }, index * 320);
        }
    }

    private boolean isOpposite(SwitchAction actionA, SwitchAction actionB) {
        if (actionA.action != actionB.action) return false;
        int action = actionA.action;
        if (action == SwitchUtils.SWITCH_ACTION_ON_OFF) {
            return actionA.value != actionB.value; // on and off
        } else {
            return actionA.value + actionB.value == 0; // +20 and -20
        }
    }

    /**
     * get the action list that need to be configured
     */
    private ArrayMap<SwitchAction, Boolean> getPreparedActions() {
        List<SwitchAction> actions = switchLight.switchActions;
        ArrayMap<SwitchAction, Boolean> preparedActions = new ArrayMap<>();
        int actionCnt = switchLight.switchActions.size();
        if (actionCnt == 4) {
            if (!isOpposite(actions.get(0), actions.get(1))) {
                // use generic
                preparedActions.put(actions.get(0), true);
                preparedActions.put(actions.get(1), true);
            } else {
                // use special
                preparedActions.put(actions.get(0), false);
            }
            if (!isOpposite(actions.get(2), actions.get(3))) {
                // use generic
                preparedActions.put(actions.get(2), true);
                preparedActions.put(actions.get(3), true);
            } else {
                // use special
                preparedActions.put(actions.get(2), false);
            }
        } else {
            preparedActions.put(actions.get(0), false);
            preparedActions.put(actions.get(1), false);
        }
        MeshLogger.d("PreparedActions : " + preparedActions.size());
        return preparedActions;
    }

    private void removeAll() {
        MeshLogger.d("remove all");
        int address = 0xFFFF;
        // set mac
        {
            byte opcode = (byte) 0xC7;
            byte[] params = EH_PairDelete();
//            TelinkLightService.Instance().sendCommandNoResponse(opcode, address, params);
        }
    }

    public byte getSeg(int segN, int segO) {
        return (byte) (segO | (segN << 4));
    }

    public int crc16(byte[] packet) {
        MeshLogger.d("calc crc : " + Arrays.bytesToHexString(packet, ""));
        int length = packet.length;
        short[] poly = new short[]{0, (short) 0xA001};
        int crc = 0xFFFF;
        int ds;

        for (int j = 0; j < length; j++) {
            ds = packet[j];
            for (int i = 0; i < 8; i++) {
                crc = (crc >> 1) ^ poly[(crc ^ ds) & 1] & 0xFFFF;
                ds = ds >> 1;
            }
        }

        return crc;
    }


    private byte[] EH_PairParMac(byte seg) {
        byte subOpcode = 0x00;
        byte[] macBytes = Arrays.hexToBytes(switchLight.macAddress.replace(":", ""));
        byte[] cmdParams = ByteBuffer.allocate(10).order(ByteOrder.LITTLE_ENDIAN)
                .put(seg)// segO + segN
                .put(subOpcode)
                .putShort((short) switchLight.meshAddress)
                .put(Arrays.reverse(macBytes)).array();
        MeshLogger.d("params - EH_PairParMac3208 : " + Arrays.bytesToHexString(cmdParams, ""));
        return cmdParams;
    }

    /**
     * key low
     * CMD-EH_PairParSet3208_L	=00 02 1B 17 00 04 00 52 15 00 00 00 00 00 00 ff ff cd 11 02 01 01 5F EA 8F 55 67 44 74 D3
     *
     * @return
     */
    private byte[] EH_PairParSet_L(byte seg) {
        byte subOpcode = 0x01;
        byte[] cmdParams = ByteBuffer.allocate(10)
                .put(seg)
                .put(subOpcode)
                .put(switchLight.deviceKey, 0, 8).array();
        MeshLogger.d("params - EH_PairParSet3208_L : " + Arrays.bytesToHexString(cmdParams, ""));
        return cmdParams;
    }


    /**
     * key high
     * CMD-EH_PairParSet3208_H	=00 02 1B 17 00 04 00 52 15 00 00 00 00 00 00 ff ff cd 11 02 02 01 BB F2 1C 29 63 3E 13 9A
     *
     * @return
     */
    private byte[] EH_PairParSet_H(byte seg) {
        byte subOpcode = 0x02;
        byte[] cmdParams = ByteBuffer.allocate(10)
                .put(seg)
                .put(subOpcode)
                .put(switchLight.deviceKey, 8, 8).array();
        MeshLogger.d("params - EH_PairParSet3208_H : " + Arrays.bytesToHexString(cmdParams, ""));
        return cmdParams;
    }


    /*
     * confirm
     * CMD-EH_PairConfirm3208	=
     * 00 02 1B 17 00 04 00 52 15 00 00 00 00 00 00 ff ff c7 11 02 10 10 01 00 7A 00 00 00 00 00
     *                                                             retransmitCount
     *                                                                sub_op
     *                                                                   pair_id
     *                                                                      unicast address
     *                                                                            rsv
     *                                                                                     auth
     * @return
     */
    private byte[] EH_PairConfirm(short crc) {
        byte retransmitCount = 0x10;
        byte subOpcode = 0x10;
//        byte pairId = 0x01;
//        byte[] rsv = new byte[]{0x00, 0x00, 0x00};
//        byte[] auth = new byte[]{0x00, 0x00}; //  crc16
        byte[] cmdParams = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN)
                .put(retransmitCount)
                .put(subOpcode)
                .putShort(crc).array();
        MeshLogger.d("params - EH_PairConfirm3208 : " + Arrays.bytesToHexString(cmdParams, ""));
        return cmdParams;
    }

    /**
     * pair delete
     *
     * @return
     */
    private byte[] EH_PairDelete() {
        byte retransmitCount = 0x10;
        byte subOpcode = 0x11;
        short address = (short) switchLight.meshAddress;
        byte[] cmdParams = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN)
                .put(retransmitCount)
                .put(subOpcode)
                .putShort(address)
                .array();
        MeshLogger.d("params - EH_PairDelete : " + Arrays.bytesToHexString(cmdParams, ""));
        return cmdParams;
    }

    /**
     * publish command
     */
    private byte[] EH_PairPub(byte seg, SwitchAction action, boolean isGeneric) {
        if (isGeneric) {
            return SwitchUtils.genGenericCmd(switchLight.meshAddress, seg, action);
        }
        return SwitchUtils.genSpecialCmd(switchLight.meshAddress, seg, action);
    }


    private byte[] genSceneRecallPubParams(byte seg, SwitchAction action) {
        byte subOp = SwitchUtils.getPublishSubOp(action);
        byte b0 = (byte) ((subOp & 0x0F) | ((action.keyIndex & 0x0F) << 4));
        short address = (short) switchLight.meshAddress;

        byte[] cmdParams = ByteBuffer.allocate(10).order(ByteOrder.LITTLE_ENDIAN)
                .put(seg)
                .put(b0)
                .putShort(address)
                .putShort((short) action.publishAddress)
                .put(SwitchUtils.OP_LGT_CMD_LOAD_SCENE)
                .array();
        MeshLogger.d("params - gen scene recall : " + Arrays.bytesToHexString(cmdParams, ""));
        return cmdParams;
    }

    private String getFormatSwitchInfo() {
        return String.format("MeshAddress : 0x%04X\nMacAddress : %s", switchLight.meshAddress, switchLight.macAddress);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.mApplication.removeEventListener(this);
        this.mHandler.removeCallbacksAndMessages(null);
    }

    /**
     * 事件处理方法
     *
     * @param event
     */
    @Override
    public void performed(Event<String> event) {
        /*if (NotificationEvent.GET_DEVICE_STATE.equals(event.getType())) {
            byte[] params = ((NotificationEvent) event).getArgs().params;
            int src = ((NotificationEvent) event).getArgs().src;
            onPairConfirm(src, params);
        } else if (NotificationEvent.ONLINE_STATUS.equals(event.getType()) || DeviceEvent.STATUS_CHANGED.equals(event.getType())) {
            runOnUiThread(() -> adapter.notifyDataSetChanged());
        }*/
    }

    // pair confirm rsp: 100001
    // pair delete rsp:  1100
    private void onPairConfirm(int address, byte[] params) {
        MeshLogger.d("onPairConfirm ---- ");
        if (params.length < 2) {
            MeshLogger.d("params len error");
            return;
        }
        byte subOp = params[0];
        byte st = params[1];
        if (st != EH_PAIR_ST_SUCCESS) {
            MeshLogger.d("setting rsp error : " + st);
            return;
        }
        onPairResponseSuccess(subOp, address);
        runOnUiThread(() -> {
            dismissWaitingDialog();
            adapter.notifyDataSetChanged();
        });
        mesh.saveOrUpdate();
    }

    private void onPairResponseSuccess(int subOp, int address) {
        MeshLogger.d(String.format("onPairResponseSuccess : settingOp=0x%02X  subOp=0x%02X", settingOp, subOp));
        if (settingOp == 0) return;
        switch (settingOp) {
            case SETTING_OP_ADD_PAIR_PUB:
            case SETTING_OP_ADD_PUB:
                if (subOp == SwitchUtils.SUB_OP_PAIR_CONFIRM) {
                    switchLight.updateLightState(address, SwitchUtils.SWITCH_STATE_PUBLISH_SET_COMPLETE);
                }
                break;
            /*case SETTING_OP_DEL_PUB:
                if (subOp == SwitchUtils.SUB_OP_PAIR_CONFIRM) {
                    switchLight.updateLightState(address, SwitchUtils.SWITCH_STATE_PAIR_COMPLETE);
                }
                break;*/
            case SETTING_OP_DEL_PAIR:
                if (subOp == SwitchUtils.SUB_OP_PAIR_DELETE) {
                    switchLight.updateLightState(address, SwitchUtils.SWITCH_STATE_UNPAIRED);
                }
                break;
        }
        receivedRsp.add(address);
        if (receivedRsp.size() >= onlineCount) {
            mHandler.removeCallbacks(SET_SWITCH_TIMEOUT_TASK);
            onSaveComplete(true);
        }
    }
}
