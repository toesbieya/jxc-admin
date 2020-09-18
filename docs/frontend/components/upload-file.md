# Upload File

集成七牛云直传，需要配合后端使用。支持图片、word、pdf、ppt、rar、excel、txt、zip类型的文件预览（使用kkFileView）。

<img :src="$withBase('/upload-file.png')">

### 引入：

`@/component/UploadFile`

### 使用： 
```html
<upload-file/>
```

### UploadFile Attributes：

| 参数     | 说明                   | 类型              | 默认     |
| :------: | :--------------------: | :---------------: | :------: |
| disabled | 等同于`el-upload`      | `boolean`         | -        |
| multiple | 等同于`el-upload`      | `boolean`         | `true`   | 
| fileList | 等同于`el-upload`      | `array`           | `[]`     | 
| limit    | 等同于`el-upload`      | `number`          | `10`     | 
| maxSize  | 单次上传文件的最大大小 | `number / string` | `'10MB'` | 

### UploadFile Events：

| 事件名称 | 说明                         | 回调参数          |
| :------: | :--------------------------: | :---------------: |
| remove   | 移出不是本次上传的文件时触发 | `{url}`           |
| success  | 上传成功后触发               | `(file,response)` |
