/********************************************************************************************************
 * @file Scheduler.java
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
package com.telink.ble.mesh.model;


import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;


/**
 * scheduler
 * Created by kee on 2018/9/17.
 * Mesh_Model_Specification v1.0.pdf#5.2.3.4
 */
public final class Scheduler implements Serializable, Parcelable {

    public long id;

    /**
     * #ref builder desc
     */
    public static final int YEAR_ANY = 0x64;

    public static final int DAY_ANY = 0x00;

    public static final int HOUR_ANY = 0x18;

    public static final int HOUR_RANDOM = 0x19;

    public static final int MINUTE_ANY = 0x3C;
    public static final int MINUTE_CYCLE_15 = 0x3D;
    public static final int MINUTE_CYCLE_20 = 0x3E;
    public static final int MINUTE_RANDOM = 0x3F;

    public static final int SECOND_ANY = 0x3C;
    public static final int SECOND_CYCLE_15 = 0x3D;
    public static final int SECOND_CYCLE_20 = 0x3E;
    public static final int SECOND_RANDOM = 0x3F;

    public static final int ACTION_OFF = 0x0;
    public static final int ACTION_ON = 0x1;
    public static final int ACTION_SCENE = 0x2;
    public static final int ACTION_NO = 0xF;

    /**
     * 4 bits
     */
    private byte index;

    /**
     * 76 bits
     */
    public SchedulerRegister register;

    public Scheduler() {
    }

//    private Scheduler(byte index, SchedulerRegister register) {
//        this.index = index;
////        this.register = new ToOne<SchedulerRegister>(this, SchedulerRegister_.__)
//        this.register.setTarget(register);
//    }

    /*public long getRegisterParam0() {
        return index |
                register.year << 4 |
                register.month << 11 |
                register.day << 23 |
                register.hour << 28 |
                register.minute << 33 |
                register.second << 39 |
                register.week << 45 |
                register.action << 52 |
                register.transTime << 56;
    }

    public int getRegisterParam1() {
        return register.sceneId;
    }*/

    public SchedulerRegister getRegister() {
        return register;
    }

    protected Scheduler(Parcel in) {
        index = in.readByte();
        register = in.readParcelable(SchedulerRegister.class.getClassLoader());
    }

    public static final Creator<Scheduler> CREATOR = new Creator<Scheduler>() {
        @Override
        public Scheduler createFromParcel(Parcel in) {
            return new Scheduler(in);
        }

        @Override
        public Scheduler[] newArray(int size) {
            return new Scheduler[size];
        }
    };

    public byte getIndex() {
        return index;
    }


    public static Scheduler fromBytes(byte[] data) {
        if (data == null || data.length != 10) return null;
        byte index = (byte) (data[0] & 0x0F);
        SchedulerRegister reg = new SchedulerRegister();
        reg.year = (data[0] >> 4 & 0b1111) | ((data[1] & 0b111) << 4);
        reg.month = (data[1] >> 3 & 0b11111) | ((data[2] & 0b1111111) << 5);
        reg.day = (data[2] >> 7 & 0b1) | ((data[3] & 0b1111) << 1); // d3 4
        reg.hour = (data[3] >> 4 & 0b1111) | ((data[4] & 0b1) << 4); // d4 1
        reg.minute = ((data[4] >> 1) & 0b111111); // d4 7
        reg.second = ((data[4] >> 7 & 0b1) | ((data[5] & 0b11111)) << 1); // d5 5
        reg.week = ((data[5] >> 5 & 0b111) | ((data[6] & 0b1111)) << 3); // d6 4
        reg.action = (data[6] >> 4 & 0b1111); // d6 8
        reg.transTime = data[7] & 0xFF; // d7 8
        reg.sceneId = (data[8] & 0xFF) | ((data[9] << 8) & 0xFF); // d8 d9
        Scheduler scheduler = new Scheduler();
        scheduler.index = index;
        scheduler.register = reg;
        return scheduler;
//        return new Scheduler(index, reg);
    }

    public byte[] toBytes() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(10).order(ByteOrder.LITTLE_ENDIAN);
        SchedulerRegister register = this.register;
        byteBuffer.putLong(index |
                register.year << 4 |
                register.month << 11 |
                register.day << 23 |
                register.hour << 28 |
                register.minute << 33 |
                register.second << 39 |
                register.week << 45 |
                register.action << 52 |
                register.transTime << 56)
                .putShort((short) register.sceneId);
        return byteBuffer.array();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(index);
        dest.writeParcelable(register, flags);
    }

    public static final class Builder {


        /**
         * 4 bits
         * Index of the Schedule Register entry to set
         * 0x0-0xF
         */
        private byte index = 1;

        /**
         * 7 bits
         * 0x00–0x63: 2 least significant digits of the year
         * 0x64: Any year
         * All other values: Prohibited
         */
        private byte year = 0x00;

        /**
         * 12 bits
         * bit 0-11 means Scheduled in
         * January/February/March/April/May/June/July/August/September/October/November/December
         */
        private short month = 0b000000000000;

        /**
         * 5 bits
         * 0x00 Any day
         * 0x01–0x1F Day of the month, when number overflow, event occurs in the last day
         */
        private byte day = 0x00;

        /**
         * 5 bits
         * 0x00–0x17: Hour of the day (00 to 23 hours)
         * 0x18: Any hour of the day
         * 0x19: Once a day (at a random hour)
         * All other values: Prohibited
         */
        private byte hour = 0x00;

        /**
         * 6 bits
         * 0x00–0x3B Minute of the hour (00 to 59)
         * 0x3C Any minute of the hour
         * 0x3D Every 15 minutes (minute modulo 15 is 0) (0, 15, 30, 45)
         * 0x3E Every 20 minutes (minute modulo 20 is 0) (0, 20, 40)
         * 0x3F Once an hour (at a random minute)
         */
        private byte minute = 0x00;

        /**
         * 6 bits
         * 0x00–0x3B Second of the minute (00 to 59)
         * 0x3C Any second of the minute
         * 0x3D Every 15 seconds (minute modulo 15 is 0) (0, 15, 30, 45)
         * 0x3E Every 20 seconds (minute modulo 20 is 0) (0, 20, 40)
         * 0x3F Once an minute (at a random second)
         */
        private byte second = 0x00;

        /**
         * 7 bits
         * bit 0-6 means
         * Scheduled on Mondays/Tuesdays/Wednesdays/Thursdays/Fridays/Saturdays/Sundays
         */
        private byte week = 0b000000;

        /**
         * 4 bits
         * 0x0 Turn Off
         * 0x1 Turn On
         * 0x2 Scene Recall
         * 0xF No action
         * All other values
         * Reserved for Future Use
         */
        private byte action;

        /**
         * 8 bits
         * #reference model spec 3.1.3.1
         * bit 0-5 The number of Steps
         * bit 6-7 Transition Step Resolution
         */
        private byte transTime;


        /**
         * 16 bit
         * scene id
         */
        private short sceneId;

        public Builder setIndex(byte index) {
            this.index = index;
            return this;
        }

        public Builder setYear(byte year) {
            this.year = year;
            return this;
        }

        public Builder setMonth(short month) {
            this.month = month;
            return this;
        }

        public Builder setDay(byte day) {
            this.day = day;
            return this;
        }

        public Builder setHour(byte hour) {
            this.hour = hour;
            return this;
        }

        public Builder setMinute(byte minute) {
            this.minute = minute;
            return this;
        }

        public Builder setSecond(byte second) {
            this.second = second;
            return this;
        }

        public Builder setWeek(byte week) {
            this.week = week;
            return this;
        }

        public Builder setAction(byte action) {
            this.action = action;
            return this;
        }

        public Builder setTransTime(byte transTime) {
            this.transTime = transTime;
            return this;
        }

        public Builder setSceneId(short sceneId) {
            this.sceneId = sceneId;
            return this;
        }



        public Scheduler build() {
            SchedulerRegister register = new SchedulerRegister(
                    this.year,
                    this.month,
                    this.day,
                    this.hour,
                    this.minute,
                    this.second,
                    this.week,
                    this.action,
                    this.transTime,
                    this.sceneId
            );
            Scheduler scheduler = new Scheduler();
            scheduler.index = index;
            scheduler.register = register;
            return scheduler;
//            return new Scheduler(this.index, register);
        }
    }

}
