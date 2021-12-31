#krtc-token-api-java


**说明**

此项目为 krtc_token_api_v1 版本的 java 实现。



**源码**

直接将KuaiShouTokenAPI，Base64URL 下载到本地使用即可



**使用**
```bash
 public static void main(String []args){
        long sdkappid = 23424434;//您的appid 可在控制台项目详情中查询
        String key = "key";//您的appsign 可在控制台项目详情中查询
        KuaiShouTokenAPI api = new KuaiShouTokenAPI(sdkappid,key);
        String userId = "43ffdgd4";//用户id
        long expire = 86400L;//代表有效期一天
        String token = api.genToken(userId,expire);
        System.out.println("生成的token为："+token);
    }
```
