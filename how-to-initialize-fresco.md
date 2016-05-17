Fresco 的初始化
===

使用 Fresco 之前必须进行初始化，最简单的初始化方式是直接调用 `Fresco.initialize(context)`：

```java
/** Initializes Fresco with the default config. */
public static void initialize(Context context) {
  ImagePipelineFactory.initialize(context);
  initializeDrawee(context);
}
```

也可以自定义初始化方式：

```java
/** Initializes Fresco with the specified config. */
public static void initialize(Context context, ImagePipelineConfig imagePipelineConfig) {
  ImagePipelineFactory.initialize(imagePipelineConfig);
  initializeDrawee(context);
}
```

`ImagePipelineConfig` 中存储了初始化用到的自定义参数。

`initialize` 方法其实就做了两件事，一个是初始化 `ImagePipelineFactory`（负责获取数据），另一个是初始化 `drawee`（负责显示数据），我们首先来看 `ImagePipelineFactory` 的初始化过程。

ImagePipelineFactory
---

对应着 Fresco 的初始化方法，`ImagePipelineFactory` 也提供了两种初始化方式：

```java
/** Initializes {@link ImagePipelineFactory} with default config. */
public static void initialize(Context context) {
  initialize(ImagePipelineConfig.newBuilder(context).build());
}
/** Initializes {@link ImagePipelineFactory} with the specified config. */
public static void initialize(ImagePipelineConfig imagePipelineConfig) {
  sInstance = new ImagePipelineFactory(imagePipelineConfig);
}
```

`initialize(Context context)` 通过 `ImagePipelineConfig.newBuilder(context).build()` 创建了一个默认的 `ImagePipelineConfig`。

`ImagePipelineFactory` 是 `ImagePipeline` 的 **Factory**，我们来看一下如何生产 `ImagePipeline`：

```java
public ImagePipeline getImagePipeline() {
  if (mImagePipeline == null) {
    mImagePipeline =
      new ImagePipeline(
        getProducerSequenceFactory(),
        mConfig.getRequestListeners(),
        mConfig.getIsPrefetchEnabledSupplier(),
        getBitmapMemoryCache(),
        getEncodedMemoryCache(),
        getMainBufferedDiskCache(),
        getSmallImageBufferedDiskCache(),
        mConfig.getCacheKeyFactory(),
        mThreadHandoffProducerQueue);
    }
    return mImagePipeline;
}
```
原来，一个 `ImagePipelineFactory` 只会生产一个 `ImagePipeline`，这是因为创建 `ImagePipeline` 会比较“昂贵”，所以一个工厂只能生产一个。:)

后面我们会具体分析[如何创建 image pipeline](how-to-initialize-image-pipeline.md)，先来看一下 Fresco 的另一个初始化动作 - `initializeDrawee`。

Drawee
---

Fresco 通过 `initializeDrawee` 初始化了一个 `SimpleDraweeView`：

```java
private static PipelineDraweeControllerBuilderSupplier sDraweeControllerBuilderSupplier;
private static void initializeDrawee(Context context) {
  sDraweeControllerBuilderSupplier = new PipelineDraweeControllerBuilderSupplier(context);
  SimpleDraweeView.initialize(sDraweeControllerBuilderSupplier);
}
```

`PipelineDraweeControllerBuilderSupplier` 会提供一个 `PipelineDraweeControllerBuilder`，`PipelineDraweeControllerBuilder` 会创建一个 `DraweeController`。

Drawee 使用 `sDraweeControllerBuilderSupplier` 进行初始化，并且在内部保存了实例。

```java
private static Supplier<? extends SimpleDraweeControllerBuilder> sDraweeControllerBuilderSupplier
/** Initializes {@link SimpleDraweeView} with supplier of Drawee controller builders. */
public static void initialize(Supplier<? extends SimpleDraweeControllerBuilder> draweeControllerBuilderSupplier) {
  sDraweeControllerBuilderSupplier = draweeControllerBuilderSupplier;
}
```

---

Fresco 通过初始化创建了 `ImagePipelineFactory` 和 `SimpleDraweeView`。

`ImagePipelineFactory` 负责创建 `ImagePipeline`，`ImagePipeline` 负责获取数据，`SimpleDraweeView` 负责显示 `ImagePipeline` 获取的数据。
“前后方”都已经创建完毕，我们继续分析。

