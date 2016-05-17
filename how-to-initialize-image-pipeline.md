ImagePipeline 的初始化
===

`ImagePipelineFactory` 是一个单例工厂，而且只生产一个 `ImagePipeline` 实例。

```java
private static ImagePipelineFactory sInstance = null;

/** Initializes {@link ImagePipelineFactory} with the specified config. */
public static void initialize(ImagePipelineConfig imagePipelineConfig) {
  sInstance = new ImagePipelineFactory(imagePipelineConfig);
}

/** Gets the instance of {@link ImagePipelineFactory}. */
public static ImagePipelineFactory getInstance() {
  return Preconditions.checkNotNull(sInstance, "ImagePipelineFactory was not initialized!");
}
```

构造 `ImagePipeline` 的参数由 `ImagePipelineConfig` 提供，所以我们首先来分析 `ImagePipelineConfig`。

`ImagePipelineConfig` 的构造方法是 `private` 类型，因此创建一个 `ImagePipelineConfig` 必须通过它的一个内部类 - `Builder`，而 `Builder` 的构造方法也 `private` 类型：

```java
private Builder(Context context) {
  // Doesn't use a setter as always required.
  mContext = Preconditions.checkNotNull(context);
}
```

所以只能通过 `ImagePipelineConfig` 的 static 方法 - `newBuilder` 来创建一个 builder：

```java
public static Builder newBuilder(Context context) {
  return new Builder(context);
}
```

`AnimatedImageFactory` 定义了一个解码动图（GIF、WebP）的接口。

```java
/**
 * Decoder for animated images.
 */
public interface AnimatedImageFactory {

  /**
   * Decodes a GIF into a CloseableImage.
   * @param encodedImage encoded image (native byte array holding the encoded bytes and meta data)
   * @param options the options for the decode
   * @param bitmapConfig the Bitmap.Config used to generate the output bitmaps
   * @return a {@link CloseableImage} for the GIF image
   */
  public CloseableImage decodeGif(
      final EncodedImage encodedImage,
      final ImageDecodeOptions options,
      final Bitmap.Config bitmapConfig);

  /**
   * Decode a WebP into a CloseableImage.
   * @param encodedImage encoded image (native byte array holding the encoded bytes and meta data)
   * @param options the options for the decode
   * @param bitmapConfig the Bitmap.Config used to generate the output bitmaps
   * @return a {@link CloseableImage} for the WebP image
   */
  public CloseableImage decodeWebP(
      final EncodedImage encodedImage,
      final ImageDecodeOptions options,
      final Bitmap.Config bitmapConfig);

}
```

`decodeWebP` 和 `decodeGif` 可以把一个 `EncodedImage` 转换成一个 `CloseableImage`。

`Bitmap.Config` 是一个枚举类型，用于定义一个像素的位数。

```java
public enum Config {
  ALPHA_8,
  RGB_565,
  ARGB_4444,
  ARGB_8888
}
```

`Builder` 还定义了两个 `MemoryCacheParams` 的 `Supplier`：

```java
private Supplier<MemoryCacheParams> mBitmapMemoryCacheParamsSupplier;
private Supplier<MemoryCacheParams> mEncodedMemoryCacheParamsSupplier;
```

具体的不同点后面会分析到，先看 `MemoryCacheParams` 的定义：

```java
/**
 * Configuration for a memory cache.
 */
public class MemoryCacheParams {
  // The maximum size of the cache, in bytes.
  public final int maxCacheSize;

  // The maximum number of items that can live in the cache.
  public final int maxCacheEntries;

  // The eviction queue is an area of memory that stores items ready for eviction but have not yet been deleted. This is the maximum size of that queue in bytes.
  public final int maxEvictionQueueSize;

  // The maximum number of entries in the eviction queue.
  public final int maxEvictionQueueEntries;

  // The maximum size of a single cache entry.
  public final int maxCacheEntrySize;
}
```

`CacheKeyFactory` 为三种图像（Bitmap / post-processed bitmap / encoded image）创建 *cache keys*：
  
```java
/**
 * Factory methods for creating cache keys for the pipeline.
 */
public interface CacheKeyFactory {

  /**
   * @return {@link CacheKey} for doing bitmap cache lookups in the pipeline.
   */
  CacheKey getBitmapCacheKey(ImageRequest request, Object callerContext);

  /**
   * @return {@link CacheKey} for doing post-processed bitmap cache lookups in the pipeline.
   */
  CacheKey getPostprocessedBitmapCacheKey(ImageRequest request, Object callerContext);

  /**
   * @return {@link CacheKey} for doing encoded image lookups in the pipeline.
   */
  CacheKey getEncodedCacheKey(ImageRequest request);
}
```
