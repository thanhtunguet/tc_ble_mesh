## V4.1.0.0_Patch_0005

### Dependency Updates

* telink_b85m_ble_single_connection_sdk_v3.4.1
* mesh ble library commit log: SHA-1: ac49216f1036804ddeffcddd82bb1c301e70f8c4

### Bug Fixes

* N/A

### Features

* N/A

### Performance Improvements

* reduce LPN power consumption. before modification, the concurrent of lpn is 23.5ua with 5 seconds lpn poll interval. after modification, the concurrent of lpn decrease to 16.4ua.

### BREAKING CHANGES

* N/A

### Notes

* N/A

### CodeSize

* Flash and RAM (default target):
  - 8258_mesh_LPN:____Flash 111 KB, RAM (23 KB + 3K stack),

### Dependency Updates

* N/A

### Bug Fixes

* 降低LPN功耗。修改之前，lpn在5秒poll间隔下电流为23.5ua。修改后，电流降低到16.4ua。

### Features

* N/A

### Performance Improvements

* N/A

### BREAKING CHANGES

* N/A

### Notes

* N/A

## V4.1.0.0_Patch_0004

### Dependency Updates

* telink_b85m_ble_single_connection_sdk_v3.4.1
* mesh ble library commit log: SHA-1: ac49216f1036804ddeffcddd82bb1c301e70f8c4

### Bug Fixes

* Add timeout for tx extend adv to avoid rf tx flag being cleared abnormally by other IRQs.

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

  - 8258_mesh:_________Flash 118 KB, RAM (28 KB + 3K stack),
  - 8258_mesh_LPN:____Flash 111 KB, RAM (23 KB + 3K stack),
  - 8258_mesh_gw:_____Flash 128 KB, RAM (36 KB + 3K stack),
  - 8258_mesh_switch:__Flash 106 KB, RAM (25 KB + 3K stack),


### Dependency Updates

* telink_b85m_ble_single_connection_sdk_v3.4.1
* mesh library commit log: SHA-1: ac49216f1036804ddeffcddd82bb1c301e70f8c4

### Bug Fixes

* 发扩展广播包时添加超时时间，以防rf发送中断标记被异常清除。

### Features

* N/A

### Performance Improvements

* N/A

### BREAKING CHANGES

* N/A

### Notes

* 为避免编译错误以及功能丢失，升级SDK时，请确认更新全部SDK文件。


## V4.1.0.0_Patch_0003

### Dependency Updates

* telink_b85m_ble_single_connection_sdk_v3.4.1
* mesh ble library commit log: SHA-1: 5c84f6ef2bd29a69cae45b94b8e1347e2e503e8b

### Bug Fixes

* delete the duplicate __clzsi2() in mesh lib which already exists in soft-fp.

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

  - 8258_mesh:_________Flash 118 KB, RAM (28 KB + 3K stack),
  - 8258_mesh_LPN:____Flash 111 KB, RAM (23 KB + 3K stack),
  - 8258_mesh_gw:_____Flash 128 KB, RAM (36 KB + 3K stack),
  - 8258_mesh_switch:__Flash 106 KB, RAM (25 KB + 3K stack),


### Dependency Updates

* telink_b85m_ble_single_connection_sdk_v3.4.1
* mesh library commit log: SHA-1: 5c84f6ef2bd29a69cae45b94b8e1347e2e503e8b

### Bug Fixes

* 删除mesh库中已在浮点库定义的__clzsi2()函数。

### Features

* N/A

### Performance Improvements

* N/A

### BREAKING CHANGES

* N/A

### Notes

* 为避免编译错误以及功能丢失，升级SDK时，请确认更新全部SDK文件。

## V4.1.0.0_Patch_0002

### Dependency Updates

* telink_b85m_ble_single_connection_sdk_v3.4.1
* mesh ble library commit log: SHA-1: 5c84f6ef2bd29a69cae45b94b8e1347e2e503e8b

### Bug Fixes

* fix receiver miss message while multi nodes sending group segment message.

### Features

* add extend adv filter callback function mesh_blc_aux_adv_filter().

### Performance Improvements

* N/A

### BREAKING CHANGES

* N/A

### Notes

* to avoid compilation errors or loss of functionality, please update all files when upgrading the SDK.

### CodeSize

* Flash and RAM (default target):

  - 8258_mesh:_________Flash 118 KB, RAM (28 KB + 3K stack),
  - 8258_mesh_LPN:____Flash 111 KB, RAM (23 KB + 3K stack),
  - 8258_mesh_gw:_____Flash 128 KB, RAM (36 KB + 3K stack),
  - 8258_mesh_switch:__Flash 106 KB, RAM (25 KB + 3K stack),


### Dependency Updates

* telink_b85m_ble_single_connection_sdk_v3.4.1
* mesh library commit log: SHA-1: 5c84f6ef2bd29a69cae45b94b8e1347e2e503e8b

### Bug Fixes

* 修复多个节点同时发送非单播的分包消息时，影响接收端收包的问题.

### Features

* 添加扩展广播包过滤回调函数mesh_blc_aux_adv_filter().

### Performance Improvements

* N/A

### BREAKING CHANGES

* N/A

### Notes

* 为避免编译错误以及功能丢失，升级SDK时，请确认更新全部SDK文件。


## V4.1.0.0_Patch_0001

### Dependency Updates

* telink_b85m_ble_single_connection_sdk_v3.4.1
* mesh library commit log: SHA-1: a8babefbf77bbe6760423492b64f1e8ecbdb23f9

### Bug Fixes

* N/A

### Features

* open source relay protection list code.

### Performance Improvements

* N/A

### BREAKING CHANGES

* N/A

### Notes

* to avoid compilation errors or loss of functionality, please update all files when upgrading the SDK.

### CodeSize

* Flash and RAM (default target):

  - 8258_mesh:_________Flash 118 KB, RAM (28 KB + 3K stack),
  - 8258_mesh_LPN:____Flash 111 KB, RAM (23 KB + 3K stack),
  - 8258_mesh_gw:_____Flash 128 KB, RAM (36 KB + 3K stack),
  - 8258_mesh_switch:__Flash 106 KB, RAM (25 KB + 3K stack),


### Dependency Updates

* telink_b85m_ble_single_connection_sdk_v3.4.1
* mesh library commit log: SHA-1: a8babefbf77bbe6760423492b64f1e8ecbdb23f9

### Bug Fixes

* 开源消息重放保护表.

### Features

* N/A

### Performance Improvements

* N/A

### BREAKING CHANGES

* N/A

### Notes

* 为避免编译错误以及功能丢失，升级SDK时，请确认更新全部SDK文件。

