package com.telink.ble.mesh.ui.eh;

import java.io.Serializable;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class SwitchState implements Serializable {
    @Id
    public long id;

    public int lightAddress;

    public int state;

}