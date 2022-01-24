package com.wj.gradle.plugin

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

open class MainActivity1 : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

       // val bitmap = BitmapFactory.decodeByteArray()
        val code = ByteCode()
        code.sumMethod(1, 5)
        // code.stringMethod()
        val imageView = findViewById<ImageView>(R.id.iv_glide)


        //1.占位图option.placeholder()：在图片加载过程中，临时占位，加载成功之后替换成要显示的图片
        // error()：加载失败之后的异常占位图

        //2.指定图片大小：默认的会自动根据图片大小来决定图片大小，当然可以通override()来设置图片的尺寸
        //可通过设置Target.SIZE_ORIGINAL来设置原图

        //3.默认开启了内存缓存，可通过.skipMemoryCache(true)关闭
        //通过diskCacheStrategy()设置磁盘缓存策略

        //9.图片转换，支持圆角化、圆形化、模糊化等 .circleCrop()
        //transforms()：支持自定义转换，比较好的图片转换库 glide-transformations https://github.com/wasabeef/glide-transformations


        val option = RequestOptions()
        //注意：设置完对应参数，需要调用apply()
        option.placeholder(R.drawable.ic_launcher_foreground)
            .error(R.drawable.ic_launcher_background)
            .skipMemoryCache(true)
            //可以指定图片的大小
            .override(200)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .circleCrop() //可直接对图形进行变化  圆角化、黑白化、模糊化


        //4.默认是根据传入的图片格式，自行选择对应的图片格式进行显示.例如传入的是gif,则会自行加载gif
        //但是如果想要显示静态图,可以设置Glide.with().asBitmap()调用该方法要在load之前

        //5.info(imageview)：可直接在图片获取之后，显示到imageview控件上
        //但是也可以通过继承CustomTarget，自定义Target，拦截图片获取完之后，自行处理获取的图片资源，此时info()传入的是CustomTarget实例

        //6. 只从缓存中取图片
        // Glide.with().load().preload()进行预加载,从图片缓存中取出
        //需代替info()，若需要从网络中加载图片，还需要在调用info()

        //7.直接从缓存文件中读取
        // Glide.with().load().submit()：直接从缓存文件中读取，该过程会阻塞，直到获取到图片才会有值返回，
        // 所以该过程要放到子线程中执行，从返回的FutureTarget中获取文件。此时要将submit()替换info()

        //8.Glide.with().load().listener()：设置各种图片一些过程监听


//        Glide.with(this)
//            .load("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fp2.itc.cn%2Fimages01%2F20210825%2F78c16cf6c33044cebb5426ec3135a949.jpeg&refer=http%3A%2F%2Fp2.itc.cn&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1645001321&t=bbb54a3d2b85d0f55c6f7cc15e97cf9f")
//            .apply(option)
//            .into(imageView)


        //10.更改Glide的配置：继承AppGlideModule，并且通过@GlideModule注解自定义类


    }


}