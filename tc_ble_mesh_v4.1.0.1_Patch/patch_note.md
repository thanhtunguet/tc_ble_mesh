## V4.1.0.1_Patch_0001

### Dependency Updates

* telink_b85m_ble_single_connection_sdk_v3.4.1
* mesh ble library commit log: SHA-1: a73b6395993989b369fe62d2be0d5c0be8496c05

### Bug Fixes

* fix the issue of cpu_long_sleep_wakeup() when using external 32k rc.

### Features

* N/A

### Performance Improvements

* N/A

### BREAKING CHANGES

* N/A

### Notes

* to avoid compilation errors or loss of functionality, please update all files when upgrading the SDK.

### CodeSize

* Flash and RAM (default target):
  - 8258_mesh:_________Flash 125.4 KB, RAM (27.7 KB + 3K stack),
  - 8258_mesh_LPN:____Flash 118.9 KB, RAM (21.7 KB + 3K stack),
  - 8258_mesh_gw:_____Flash 125.0 KB, RAM (30.5 KB + 3K stack),
  - 8258_mesh_switch:__Flash 114.1 KB, RAM (24.9 KB + 3K stack),
  
  - 8278_mesh:_________Flash 123.4 KB, RAM (27.5 KB + 3K stack),
  - 8278_mesh_LPN:____Flash 117.0 KB, RAM (23.3 KB + 3K stack),
  - 8278_mesh_gw:_____Flash 123.0 KB, RAM (31.0 KB + 3K stack),
  - 8278_mesh_switch:__Flash 110.7 KB, RAM (25.0 KB + 3K stack),


### Dependency Updates

* telink_b85m_ble_single_connection_sdk_v3.4.1
* mesh library commit log: SHA-1: a73b6395993989b369fe62d2be0d5c0be8496c05

### Bug Fixes

* 修复使用外部32k rc晶振时cpu_long_sleep_wakeup()休眠异常问题。

### Features

* N/A

### Performance Improvements

* N/A

### BREAKING CHANGES

* N/A

### Notes

* 为避免编译错误以及功能丢失，升级SDK时，请确认更新全部SDK文件。