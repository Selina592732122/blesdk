# ======================== 基本设置 ========================
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose

# ======================== 保留重要属性 ========================
-keepattributes Signature,InnerClasses,Exceptions,Annotation,*Annotation*,EnclosingMethod
-keepattributes SourceFile,LineNumberTable

# ======================== 保留四大组件 ========================
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}

# ======================== 保留 View 相关 ========================
-keep public class * extends android.view.View
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# ======================== 保留你的包 ========================
-keep class com.shenghao.** { *; }
-keep interface com.shenghao.** { *; }

# ======================== 保留数据模型 ========================
-keep class com.shenghao.model.** {
    <fields>;
    <methods>;
    public <init>();
}
-keep class com.shenghao.bean.** { *; }
-keep class com.shenghao.entity.** { *; }

# ======================== Gson 相关 ========================
-keep class com.google.gson.** { *; }
-dontwarn com.google.gson.**
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken { *; }

# ======================== 第一次的缺失规则（华为相关） ========================
-dontwarn android.telephony.HwTelephonyManager
-dontwarn com.huawei.android.os.BuildEx$VERSION
-dontwarn com.huawei.hms.support.hianalytics.HiAnalyticsUtils
-dontwarn com.huawei.hms.utils.HMSBIInitializer
-dontwarn com.huawei.libcore.io.ExternalStorageFile
-dontwarn com.huawei.libcore.io.ExternalStorageFileInputStream
-dontwarn com.huawei.libcore.io.ExternalStorageFileOutputStream
-dontwarn com.huawei.libcore.io.ExternalStorageRandomAccessFile

-dontwarn java.awt.Color
-dontwarn java.awt.Font
-dontwarn java.awt.Point
-dontwarn java.awt.Rectangle

-dontwarn javax.money.CurrencyUnit
-dontwarn javax.money.Monetary
-dontwarn org.javamoney.moneta.Money

-dontwarn org.bouncycastle.crypto.BlockCipher
-dontwarn org.bouncycastle.crypto.engines.AESEngine
-dontwarn org.bouncycastle.crypto.prng.SP800SecureRandom
-dontwarn org.bouncycastle.crypto.prng.SP800SecureRandomBuilder

-dontwarn org.joda.time.DateTime
-dontwarn org.joda.time.DateTimeZone
-dontwarn org.joda.time.Duration
-dontwarn org.joda.time.Instant
-dontwarn org.joda.time.LocalDate
-dontwarn org.joda.time.LocalDateTime
-dontwarn org.joda.time.LocalTime
-dontwarn org.joda.time.Period
-dontwarn org.joda.time.ReadablePartial
-dontwarn org.joda.time.format.DateTimeFormat
-dontwarn org.joda.time.format.DateTimeFormatter

-dontwarn springfox.documentation.spring.web.json.Json

# ======================== 第二次的缺失规则（网络安全相关） ========================
# Google Guava
-dontwarn com.google.common.collect.ArrayListMultimap
-dontwarn com.google.common.collect.Multimap
-keep class com.google.common.collect.** { *; }

# Bouncy Castle JSSE
-dontwarn org.bouncycastle.jsse.BCSSLParameters
-dontwarn org.bouncycastle.jsse.BCSSLSocket
-dontwarn org.bouncycastle.jsse.provider.BouncyCastleJsseProvider
-keep class org.bouncycastle.jsse.** { *; }

# Conscrypt
-dontwarn org.conscrypt.Conscrypt$Version
-dontwarn org.conscrypt.Conscrypt
-dontwarn org.conscrypt.ConscryptHostnameVerifier
-keep class org.conscrypt.** { *; }

# OpenJSSE
-dontwarn org.openjsse.javax.net.ssl.SSLParameters
-dontwarn org.openjsse.javax.net.ssl.SSLSocket
-dontwarn org.openjsse.net.ssl.OpenJSSE
-keep class org.openjsse.** { *; }

# ======================== 通用忽略警告 ========================
-dontwarn android.support.**
-dontwarn androidx.**
-dontwarn com.android.**
-dontwarn dalvik.**
-dontwarn org.apache.http.**
-dontwarn javax.annotation.**
-dontwarn sun.misc.**
-dontwarn kotlin.**
-dontwarn org.jetbrains.**

# ======================== 保留枚举 ========================
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ======================== 保留 Parcelable ========================
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# ======================== 保留 Serializable ========================
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# ======================== 保留 R 文件 ========================
-keepclassmembers class **.R$* {
    public static <fields>;
}
# 再保留 FastJSON
-keep class com.alibaba.fastjson.** { *; }
-dontwarn javax.servlet.ServletOutputStream
-dontwarn javax.servlet.http.HttpServletRequest
-dontwarn javax.servlet.http.HttpServletResponse
-dontwarn javax.ws.rs.Consumes
-dontwarn javax.ws.rs.Produces
-dontwarn javax.ws.rs.RuntimeType
-dontwarn javax.ws.rs.WebApplicationException
-dontwarn javax.ws.rs.core.Configurable
-dontwarn javax.ws.rs.core.Configuration
-dontwarn javax.ws.rs.core.Context
-dontwarn javax.ws.rs.core.Feature
-dontwarn javax.ws.rs.core.FeatureContext
-dontwarn javax.ws.rs.core.MediaType
-dontwarn javax.ws.rs.core.MultivaluedMap
-dontwarn javax.ws.rs.core.Response
-dontwarn javax.ws.rs.core.StreamingOutput
-dontwarn javax.ws.rs.ext.ContextResolver
-dontwarn javax.ws.rs.ext.MessageBodyReader
-dontwarn javax.ws.rs.ext.MessageBodyWriter
-dontwarn javax.ws.rs.ext.Provider
-dontwarn javax.ws.rs.ext.Providers
-dontwarn org.glassfish.jersey.CommonProperties
-dontwarn org.glassfish.jersey.internal.spi.AutoDiscoverable
-dontwarn org.glassfish.jersey.internal.util.PropertiesHelper
-dontwarn org.springframework.core.MethodParameter
-dontwarn org.springframework.core.ResolvableType
-dontwarn org.springframework.core.annotation.Order
-dontwarn org.springframework.data.redis.serializer.RedisSerializer
-dontwarn org.springframework.data.redis.serializer.SerializationException
-dontwarn org.springframework.http.HttpHeaders
-dontwarn org.springframework.http.HttpInputMessage
-dontwarn org.springframework.http.HttpOutputMessage
-dontwarn org.springframework.http.MediaType
-dontwarn org.springframework.http.converter.AbstractHttpMessageConverter
-dontwarn org.springframework.http.converter.GenericHttpMessageConverter
-dontwarn org.springframework.http.converter.HttpMessageNotReadableException
-dontwarn org.springframework.http.converter.HttpMessageNotWritableException
-dontwarn org.springframework.http.server.ServerHttpRequest
-dontwarn org.springframework.http.server.ServerHttpResponse
-dontwarn org.springframework.http.server.ServletServerHttpRequest
-dontwarn org.springframework.messaging.Message
-dontwarn org.springframework.messaging.MessageHeaders
-dontwarn org.springframework.messaging.converter.AbstractMessageConverter
-dontwarn org.springframework.util.Assert
-dontwarn org.springframework.util.CollectionUtils
-dontwarn org.springframework.util.MimeType
-dontwarn org.springframework.util.ObjectUtils
-dontwarn org.springframework.util.StringUtils
-dontwarn org.springframework.validation.BindingResult
-dontwarn org.springframework.web.bind.annotation.ControllerAdvice
-dontwarn org.springframework.web.bind.annotation.ResponseBody
-dontwarn org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice
-dontwarn org.springframework.web.servlet.view.AbstractView
-dontwarn org.springframework.web.socket.sockjs.frame.AbstractSockJsMessageCodec
# ======================== 最终保险 ========================
# 如果还有问题，启用这一行（但会使混淆效果减弱）
# -ignorewarnings

# ======================== 高德地图完整混淆配置 ========================

########################################
#           高德地图 SDK               #
########################################

# 核心包
-keep class com.amap.api.** { *; }
-keep class com.autonavi.** { *; }
-keep class com.loc.** { *; }
-keep class com.aps.** { *; }
-keep class com.unicom.** { *; }

# 地图核心
-keep class com.autonavi.base.** { *; }
-keep class com.autonavi.amap.mapcore.** { *; }
-keep class com.autonavi.ae.gmap.** { *; }

# 地图视图
-keep class com.amap.api.maps.AMap { *; }
-keep class com.amap.api.maps.MapView { *; }
-keep class com.amap.api.maps.TextureMapView { *; }
-keep class com.amap.api.maps.SupportMapFragment { *; }
#-keep class com.amap.api.maps.SupportTextureMapFragment { *; }

# 定位
-keep class com.amap.api.location.AMapLocation { *; }
-keep class com.amap.api.location.AMapLocationClient { *; }
-keep class com.amap.api.location.AMapLocationClientOption { *; }
-keep class com.amap.api.location.AMapLocationListener { *; }

# 搜索
-keep class com.amap.api.services.core.** { *; }
-keep class com.amap.api.services.poisearch.** { *; }
-keep class com.amap.api.services.geocoder.** { *; }

# 导航
-keep class com.amap.api.navi.** { *; }

########################################
#           JNI/Native 方法            #
########################################

# 保留所有 native 方法
-keepclasseswithmembernames class * {
    native <methods>;
}

# 保留 System.loadLibrary 调用
-keepclasseswithmembers class * {
    public static void loadLibrary(java.lang.String);
}

# 保留动态加载的类
-keep class * {
    static <clinit>();
}

########################################
#           OpenGL/渲染相关             #
########################################

# GLSurfaceView 和 Renderer
-keep class * implements android.opengl.GLSurfaceView$Renderer {
    *;
}

-keepclassmembers class * {
    public void onSurfaceCreated(...);
    public void onSurfaceChanged(...);
    public void onDrawFrame(...);
}

# 保留 OpenGL 相关的回调
-keep class * {
    void onSurfaceCreated(javax.microedition.khronos.opengles.GL10,
                          javax.microedition.khronos.egl.EGLConfig);
}

########################################
#           忽略警告                   #
########################################

# 忽略所有高德相关的警告
-dontwarn com.amap.api.**
-dontwarn com.autonavi.**
-dontwarn com.loc.**
-dontwarn com.aps.**
-dontwarn com.unicom.**

# 忽略 OpenGL 相关警告
-dontwarn javax.microedition.khronos.**
-dontwarn android.opengl.**